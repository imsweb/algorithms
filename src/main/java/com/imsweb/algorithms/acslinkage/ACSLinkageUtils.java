/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.acslinkage;

import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDate;
import java.util.Map;

/**
 * This class can be used to calculate census tract poverty indicator.
 * Created on Oct 17, 2013 by howew
 * @author howew
 */
public final class ACSLinkageUtils {

    public static final String ALG_NAME = "NAACCR ACS Linkage Program";
    public static final String ALG_VERSION = "1.0";
    public static final String ALG_INFO = "NAACCR ACS Linkage Program version 1.0, released in October 2017";

    //Naaccr Items Used for calculation
    public static final String PROP_STATE_DX = "addressAtDxState";
    public static final String PROP_COUNTY_DX = "addressAtDxCounty";
    public static final String PROP_DIAGNOSIS_YEAR = "dateOfDiagnosisYear";
    public static final String PROP_CENSUS_TRACT_2010 = "censusTract2010";

    private static ACSLinkageDataProvider _PROVIDER;

    /**
     * Calculates the ACS Data for the provided record
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
     * <li>dateOfDiagnosisYear (#390)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned data will be a fixed width formatted string containing many values
     * <br/><br/>
     * @param record a map of properties representing a NAACCR line
     * @return the computed poverty indicator value
     */
    public static ACSLinkageOutputDto computeACSData(Map<String, String> record, ACSLinkageDataProvider.Range range) {

        ACSLinkageInputDto input = new ACSLinkageInputDto();
        input.setAddressAtDxState(record.get(PROP_STATE_DX));
        input.setAddressAtDxCounty(record.get(PROP_COUNTY_DX));
        input.setDateOfDiagnosisYear(record.get(PROP_DIAGNOSIS_YEAR));
        input.setCensusTract2010(record.get(PROP_CENSUS_TRACT_2010));

        return computeACSData(input, range, true);
    }

    /**
     * Calculates the ACS Data for the provided record
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
     * <li>dateOfDiagnosisYear (#390)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned data will be a fixed width formatted string containing many values
     * <br/><br/>
     * @param record a map of properties representing a NAACCR line
     * @return the computed poverty indicator value
     */
    public static ACSLinkageOutputDto computeACSData(Map<String, String> record, ACSLinkageDataProvider.Range range, boolean includeRecentYears) {

        ACSLinkageInputDto input = new ACSLinkageInputDto();
        input.setAddressAtDxState(record.get(PROP_STATE_DX));
        input.setAddressAtDxCounty(record.get(PROP_COUNTY_DX));
        input.setDateOfDiagnosisYear(record.get(PROP_DIAGNOSIS_YEAR));
        input.setCensusTract2010(record.get(PROP_CENSUS_TRACT_2010));

        return computeACSData(input, range, includeRecentYears);
    }

    /**
     * Calculates the census tract poverty indicator for the provided census tract poverty indicator input dto
     * If the boolean includeRecentYears is set to true, the algorithm will link cases diagnosed after 2015.
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>_addressAtDxState</li>
     * <li>_addressAtDxCounty</li>
     * <li>_dateOfDiagnosisYear</li>
     * <li>_censusTract2010</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned data will be a fixed width formatted string containing many values
     * <br/><br/>
     * @param input a <code>ACSLinkageInputDto</code> input object
     * @param includeRecentYears if true, the linkage will be performed on cases diagnosed after 2015
     * @return the computed ACS data
     */
    public static ACSLinkageOutputDto computeACSData(ACSLinkageInputDto input, ACSLinkageDataProvider.Range range, boolean includeRecentYears) {
        ACSLinkageOutputDto result = new ACSLinkageOutputDto();

        // if poverty indicator can not be calculated set it to unknown
        result.setACSData(ACSLinkageDataProvider.getUnknownValueForRange(range));

        String dxYear = input.getDateOfDiagnosisYear();
        if (dxYear == null || !NumberUtils.isDigits(dxYear) || input.getAddressAtDxState() == null || input.getAddressAtDxCounty() == null)
            return result;

        String censusTract;
        int year = Integer.parseInt(dxYear);
        if (year >= 2005 && (year <= 2015 || (includeRecentYears && year <= LocalDate.now().getYear()))) {
            censusTract = input.getCensusTract2010();
        }
        else
            return result;

        if (censusTract != null) {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            result.setACSData(_PROVIDER.getACSData(range, input.getAddressAtDxState(), input.getAddressAtDxCounty(), censusTract));
        }

        // getACSData method never returns null, but lets make sure we don't return null value anyways
        if (result.getACSData() == null)
            result.setACSData(ACSLinkageDataProvider.getUnknownValueForRange(range));

        return result;
    }

    /**
     * Use this method to register your own data provider instead of using the internal one that is entirely in memory.
     * <br/><br/>
     * This has to be done before the first call to the compute method, or the internal one will be registered by default.
     * <br/><br/>
     * Once a provider has been set, this method cannot be called (it will throw an exception).
     * @param provider the <code>ACSLinkageDataProvider</code> to set
     */
    public static synchronized void setDataProvider(ACSLinkageDataProvider provider) {
        if (_PROVIDER != null)
            throw new RuntimeException("The data provider has already been set!");
        _PROVIDER = provider;
    }

    private static synchronized void initializeInternalDataProvider() {
        if (_PROVIDER != null)
            return;
        _PROVIDER = new ACSLinkageCsvData();
    }
}
