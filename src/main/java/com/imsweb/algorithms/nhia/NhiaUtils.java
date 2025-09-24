/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.imsweb.algorithms.internal.Utils;

/**
 * This class is used to calculate the NHIA variable. More information can be found here:
 * <a href="http://www.naaccr.org/Research/DataAnalysisTools.aspx">http://www.naaccr.org/Research/DataAnalysisTools.aspx</a>
 * <br/><br/>
 * This Java implementation is based ONLY on the SAS implementation of the algorithm; the PDF documentation was not accurate when
 * this algorithm was implemented and therefore was not taken into account.
 */
public final class NhiaUtils {

    public static final String ALG_NAME = "NAACCR Hispanic Identification Algorithm (NHIA)";
    public static final String ALG_VERSION = "released in August 2024";

    public static final String NHIA_NON_HISPANIC = "0";
    public static final String NHIA_MEXICAN = "1";
    public static final String NHIA_PUERTO_RICAN = "2";
    public static final String NHIA_CUBAN = "3";
    public static final String NHIA_SOUTH_CENTRAL_AMER = "4";
    public static final String NHIA_OTHER_SPANISH = "5";
    public static final String NHIA_SPANISH_NOS = "6";
    public static final String NHIA_SURNAME_ONLY = "7";
    public static final String NHIA_DOMINICAN = "8";

    public static final String NHIA_OPTION_ALL_CASES = "0";
    public static final String NHIA_OPTION_SEVEN_AND_NINE = "1";
    public static final String NHIA_OPTION_SEVEN_ONLY = "2";

    private static final String _SPAN_HISP_ORIG_NON_HISPANIC = "0";
    private static final String _SPAN_HISP_ORIG_MEXICAN = "1";
    private static final String _SPAN_HISP_ORIG_PUERTO_RICAN = "2";
    private static final String _SPAN_HISP_ORIG_CUBAN = "3";
    private static final String _SPAN_HISP_ORIG_SOUTH_CENTRAL_AMER = "4";
    private static final String _SPAN_HISP_ORIG_OTHER_SPANISH = "5";
    private static final String _SPAN_HISP_ORIG_SPANISH_NOS = "6";
    private static final String _SPAN_HISP_ORIG_SURNAME_ONLY = "7";
    private static final String _SPAN_HISP_ORIG_DOMINICAN = "8";
    private static final String _SPAN_HISP_ORIG_UNKNOWN = "9";

    private static final String _GENDER_MALE = "1";
    private static final String _GENDER_FEMALE = "2";

    // spanish/Hispanic origins for direct identification
    private static final List<String> _DIRECT_IDENTIFICATION_ORIGINS = Arrays.asList("1", "2", "3", "4", "5", "6", "8");

    // spanish/Hispanic origins for indirect identification
    private static final List<String> _INDIRECT_IDENTIFICATION_ORIGINS = Arrays.asList("0", "6", "7", "9");

    // Birthplace countries corresponding to NHIA of MEXICAN (under the High Probability of Hispanic Ethnicity in documentation)
    private static final List<String> _BPC_MEXICAN = Collections.singletonList("MEX");

    // birthplace countries corresponding to NHIA of PERTO-RICAN (under the High Probability of Hispanic Ethnicity in documentation)
    private static final List<String> _BPC_PUERTO_RICAN = Collections.singletonList("PRI");

    // birthplace countries corresponding to NHIA of CUBAN (under the High Probability of Hispanic Ethnicity in documentation)
    private static final List<String> _BPC_CUBAN = Collections.singletonList("CUB");

    // birthplace countries corresponding to NHIA of SOUTH-CENTRAL-AMERICAN (under the High Probability of Hispanic Ethnicity in documentation)
    private static final List<String> _BPC_SOUTH_CENTRAL_AMER = Arrays.asList("ZZC", "GTM", "HND", "SLV", "NIC", "CRI", "PAN", "ZZS", "COL", "VEN", "ECU", "PER", "BOL", "CHL", "ARG", "PRY", "URY");

    // birthplace countries corresponding to NHIA of OTHER-SPANISH (under the High Probability of Hispanic Ethnicity in documentation)
    private static final List<String> _BPC_OTHER_SPANISH = Arrays.asList("ESP", "AND");

    // birthplace countries corresponding to NHIA of DOMINICAN-REPUBLIC (under the High Probability of Hispanic Ethnicity in documentation)
    private static final List<String> _BPC_DOMINICAN_REP = Collections.singletonList("DOM");

    // race being excluded from Indirect Identification
    private static final List<String> _RACE_EXCLUDED = Arrays.asList("03", "06", "07");

    // special Asian and Pacific Islander
    private static final List<String> _RACE_PACIFIC = Arrays.asList("96", "97");

    // cached lookups
    private static Set<String> _LOW_HISP_ETHN_COUNTIES;
    private static Set<String> _HEAVILY_HISPANIC_NAMES;
    private static Set<String> _RARELY_HISPANIC_NAMES;

    // internal lock to control concurrency to the data
    private static final ReentrantReadWriteLock _LOCK = new ReentrantReadWriteLock();

    private NhiaUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates the NHIA value for the provided patient DTO and option.
     * <br/><br/>
     * The provided patient dto should have a list of record input dto which has the following parameters:
     * <ul>
     * <li>spanishHispanicOrigin</li>
     * <li>birthplaceCountry</li>
     * <li>race1</li>
     * <li>ihs</li>
     * <li>addressAtDxState</li>
     * <li>countyAtDxAnalysis</li>
     * <li>sex</li>
     * <li>nameLast</li>
     * <li>nameMaiden</li>
     * <li>nameBirthSurname</li>
     * </ul>
     * <br/><br/>
     * The optiosn are also defined as constants in this class:
     * <ul>
     * <li>O: always apply the surname portion of the algorithm (corresponds to the 'All Records' option in the SAS algorithm</li>
     * <li>1: run the surname portion only if Spanish/Hispanic Origin is 7 or 9 (corresponds to the 'OPTION1' option in the SAS algorithm)</li>
     * <li>2: run the surname portion only if Spanish/Hispanic Origin is 7 and convert cases with a Spanish/Hispanic Origin of 9 to 0
     * (corresponds to the 'OPTION2' option in the SAS algorithm)</li>
     * </ul>
     * If you are not sure which option to provide, use NHIA_OPTION_SEVEN_AND_NINE since this is the default that the SAS algorithm uses.
     * @param input a patient-level input DTO; the list of tumor-level input DTO it contains will be used to determine the state/county to use.
     * @param option option indicating when to apply the Indirect Identification based on names for spanish/hispanic original values of 0, 7 and 9
     * @return the computed NHIA value
     */
    public static NhiaResultsDto computeNhia(NhiaInputPatientDto input, String option) {

        String state = null;
        String county = null;
        if (input != null && input.getTumors() != null && !input.getTumors().isEmpty()) {
            state = input.getTumors().getFirst().getStateAtDx();
            county = input.getTumors().getFirst().getCountyAtDxAnalysis();

            //The option (to run the surname portion) is applied for a patient if hispanic percentage is < 5 % for all of the counties of DX.
            //Lets first assume all counties are less than 5% hispanic.
            boolean lowHispanicCounty = true;
            //Then lets go through all records and see if there are counties with hispanic percentage greater than 5%, if we get one consider the countyDx of the patient as high hispanic
            for (NhiaInputTumorDto tumor : input.getTumors())
                if (!isLowHispanicEthnicityCounty(tumor.getCountyAtDxAnalysis(), tumor.getStateAtDx())) {
                    lowHispanicCounty = false;
                    //Lets use that county which is more than 5%
                    state = tumor.getStateAtDx();
                    county = tumor.getCountyAtDxAnalysis();
                    break;
                }
            //if lowHispanicCounty is still true, that means all counties of Dx are low hispanic, So we should consider the patient is in low hispanic county.
            //lets use unknown county which is always considered as low hispanic
            if (lowHispanicCounty)
                county = "999";
        }

        return computeNhia(input, state, county, option);
    }

    /**
     * Calculates the NHIA value for the provided Record DTO and option.
     * <br/><br/>
     * The provided record dto has the following parameters:
     * <ul>
     * <li>spanishHispanicOrigin</li>
     * <li>birthplaceCountry</li>
     * <li>race1</li>
     * <li>ihs</li>
     * <li>addressAtDxState</li>
     * <li>countyAtDxAnalysis</li>
     * <li>sex</li>
     * <li>nameLast</li>
     * <li>nameMaiden</li>
     * <li>nameBirthSurname</li>
     * </ul>
     * <br/><br/>
     * The options are also defined as constants in this class:
     * <ul>
     * <li>O: always apply the surname portion of the algorithm (corresponds to the 'All Records' option in the SAS algorithm</li>
     * <li>1: run the surname portion only if Spanish/Hispanic Origin is 7 or 9 (corresponds to the 'OPTION1' option in the SAS algorithm)</li>
     * <li>2: run the surname portion only if Spanish/Hispanic Origin is 7 and convert cases with a Spanish/Hispanic Origin of 9 to 0
     * (corresponds to the 'OPTION2' option in the SAS algorithm)</li>
     * </ul>
     * If you are not sure which option to provide, use NHIA_OPTION_SEVEN_AND_NINE since this is the default that the SAS algorithm uses.
     * @param input a patient-level input DTO; the list of tumor-level input DTO it contains won't be used
     * @param state the state at DX value to use
     * @param county the county at DX analysis value to use
     * @param option option indicating when to apply the Indirect Identification based on names for spanish/hispanic original values of 0, 7 and 9
     * @return the computed NHIA value
     */
    public static NhiaResultsDto computeNhia(NhiaInputPatientDto input, String state, String county, String option) {
        NhiaResultsDto nhia = new NhiaResultsDto(NHIA_NON_HISPANIC);

        // avoid the NPE
        if (input == null)
            return nhia;

        // option is required
        if (option == null)
            throw new IllegalStateException("Option value required!");

        // invalid options
        if (!option.equals(NHIA_OPTION_ALL_CASES) && !option.equals(NHIA_OPTION_SEVEN_AND_NINE) && !option.equals(NHIA_OPTION_SEVEN_ONLY))
            throw new IllegalStateException("Invalid option! Valid options are '0','1' and '2'");

        // get spanish/hispanic origin and race
        String spanishOrigin = input.getSpanishHispanicOrigin();
        String race1 = input.getRace1();

        if (_DIRECT_IDENTIFICATION_ORIGINS.contains(spanishOrigin))
            nhia.setNhia(applyDirectIdentification(spanishOrigin));
        if (_INDIRECT_IDENTIFICATION_ORIGINS.contains(spanishOrigin) || _RACE_PACIFIC.contains(race1)) {
            // try to use birthplace
            String birthplaceCountry = input.getBirthplaceCountry();
            if (_BPC_MEXICAN.contains(birthplaceCountry))
                nhia.setNhia(NHIA_MEXICAN);
            else if (_BPC_PUERTO_RICAN.contains(birthplaceCountry))
                nhia.setNhia(NHIA_PUERTO_RICAN);
            else if (_BPC_CUBAN.contains(birthplaceCountry))
                nhia.setNhia(NHIA_CUBAN);
            else if (_BPC_SOUTH_CENTRAL_AMER.contains(birthplaceCountry))
                nhia.setNhia(NHIA_SOUTH_CENTRAL_AMER);
            else if (_BPC_OTHER_SPANISH.contains(birthplaceCountry))
                nhia.setNhia(NHIA_OTHER_SPANISH);
            else if (_BPC_DOMINICAN_REP.contains(birthplaceCountry))
                nhia.setNhia(NHIA_DOMINICAN);
                //else try step 3-5 for cases 0, 7, 9 not 6
            else if (!_SPAN_HISP_ORIG_SPANISH_NOS.equals(spanishOrigin) && !_RACE_PACIFIC.contains(race1)) {
                // try to use indirect identification using surnames
                boolean runSurname = true;
                if (_SPAN_HISP_ORIG_NON_HISPANIC.equals(spanishOrigin)) {
                    if (isLowHispanicEthnicityCounty(county, state))
                        runSurname = NHIA_OPTION_ALL_CASES.equals(option);
                    nhia.setNhia(applyIndirectIdentification(input, runSurname));
                }
                else if (_SPAN_HISP_ORIG_UNKNOWN.equals(spanishOrigin)) {
                    if (isLowHispanicEthnicityCounty(county, state))
                        runSurname = NHIA_OPTION_ALL_CASES.equals(option) || NHIA_OPTION_SEVEN_AND_NINE.equals(option);
                    nhia.setNhia(applyIndirectIdentification(input, runSurname));
                }
                else if (_SPAN_HISP_ORIG_SURNAME_ONLY.equals(spanishOrigin))
                    nhia.setNhia(applyIndirectIdentification(input, true));
            }
        }
        // if didn't work, default to non-hispanic
        if (nhia.getNhia() == null)
            nhia.setNhia(NHIA_NON_HISPANIC);

        return nhia;
    }

    private static String applyDirectIdentification(String spanishOrigin) {
        String result;
        if (_SPAN_HISP_ORIG_MEXICAN.equals(spanishOrigin))
            result = NHIA_MEXICAN;
        else if (_SPAN_HISP_ORIG_PUERTO_RICAN.equals(spanishOrigin))
            result = NHIA_PUERTO_RICAN;
        else if (_SPAN_HISP_ORIG_CUBAN.equals(spanishOrigin))
            result = NHIA_CUBAN;
        else if (_SPAN_HISP_ORIG_SOUTH_CENTRAL_AMER.equals(spanishOrigin))
            result = NHIA_SOUTH_CENTRAL_AMER;
        else if (_SPAN_HISP_ORIG_OTHER_SPANISH.equals(spanishOrigin))
            result = NHIA_OTHER_SPANISH;
        else if (_SPAN_HISP_ORIG_DOMINICAN.equals(spanishOrigin))
            result = NHIA_DOMINICAN;
        else if (_SPAN_HISP_ORIG_SPANISH_NOS.equals(spanishOrigin))
            result = NHIA_SPANISH_NOS;
        else
            result = NHIA_NON_HISPANIC;
        return result;

    }

    private static String applyIndirectIdentification(NhiaInputPatientDto input, boolean applySurnames) {
        String result = null;

        // get the variables
        String race1 = input.getRace1();
        String sex = input.getSex();
        String ihs = input.getIhs();
        String nameLast = input.getNameLast();
        String birthSurname = input.getNameBirthSurname();

        // try to use race (if it is in excluded race, no need to apply surname)
        if (_RACE_EXCLUDED.contains(race1) || "1".equals(ihs))
            result = NHIA_NON_HISPANIC;

        // if didn't work, try to use surname
        if (result == null && applySurnames) {
            if (_GENDER_MALE.equals(sex)) {
                if (isHeavilyHispanic(nameLast))
                    result = NHIA_SURNAME_ONLY;
                else
                    result = NHIA_NON_HISPANIC;
            }
            else if (_GENDER_FEMALE.equals(sex)) {
                if (isHeavilyHispanic(birthSurname))
                    result = NHIA_SURNAME_ONLY;
                else if (isRarelyHispanic(birthSurname))
                    result = NHIA_NON_HISPANIC;
                else {
                    if (isHeavilyHispanic(nameLast))
                        result = NHIA_SURNAME_ONLY;
                    else
                        result = NHIA_NON_HISPANIC;
                }
            }
        }

        // if didn't work, default to non-hispanic
        if (result == null)
            result = NHIA_NON_HISPANIC;

        return result;
    }

    private static boolean isLowHispanicEthnicityCounty(String county, String state) {
        if (county == null || "999".equals(county) || "998".equals(county))
            return true;

        _LOCK.readLock().lock();
        try {
            if (_LOW_HISP_ETHN_COUNTIES == null) {
                _LOCK.readLock().unlock();
                _LOCK.writeLock().lock();
                try {
                    _LOW_HISP_ETHN_COUNTIES = readData("nhia-low-hisp-ethn-counties.csv");
                }
                finally {
                    _LOCK.writeLock().unlock();
                    _LOCK.readLock().lock();
                }
            }
            return _LOW_HISP_ETHN_COUNTIES.contains(state + county);
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    public static boolean isHeavilyHispanic(String name) {
        if (name == null || name.isEmpty())
            return false;

        _LOCK.readLock().lock();
        try {
            if (_HEAVILY_HISPANIC_NAMES == null) {
                _LOCK.readLock().unlock();
                _LOCK.writeLock().lock();
                try {
                    _HEAVILY_HISPANIC_NAMES = readData("nhia-heavily-hisp-names.csv");
                }
                finally {
                    _LOCK.writeLock().unlock();
                    _LOCK.readLock().lock();
                }
            }
            return _HEAVILY_HISPANIC_NAMES.contains(name.toUpperCase());
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    public static boolean isRarelyHispanic(String name) {
        if (name == null || name.isEmpty())
            return false;

        _LOCK.readLock().lock();
        try {
            if (_RARELY_HISPANIC_NAMES == null) {
                _LOCK.readLock().unlock();
                _LOCK.writeLock().lock();
                try {
                    _RARELY_HISPANIC_NAMES = readData("nhia-rarely-hisp-names.csv");
                }
                finally {
                    _LOCK.writeLock().unlock();
                    _LOCK.readLock().lock();
                }
            }
            return _RARELY_HISPANIC_NAMES.contains(name.toUpperCase());
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    private static Set<String> readData(String file) {
        Set<String> result = new HashSet<>();

        Utils.processInternalFileNoHeaders("nhia/" + file, line -> result.add(line.getField(0).toUpperCase()));

        return result;
    }

    /**
     * Returns the list of counties with low Hispanic ethnicity (&lt; 5%).
     * @return map where the keys are the state abbreviation and the values are the list of counties
     */
    public static Map<String, List<String>> getLowHispanicCountiesPerState() {
        Map<String, List<String>> result = new TreeMap<>();

        // force initialization
        isLowHispanicEthnicityCounty("?", "?");

        for (String s : _LOW_HISP_ETHN_COUNTIES)
            result.computeIfAbsent(s.substring(0, 2), k -> new ArrayList<>()).add(s.substring(2));

        result.values().forEach(Collections::sort);

        return result;
    }
}
