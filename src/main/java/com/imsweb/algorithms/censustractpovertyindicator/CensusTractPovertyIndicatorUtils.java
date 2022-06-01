/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import java.time.LocalDate;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class can be used to calculate census tract poverty indicator.
 * Created on Oct 17, 2013 by BekeleS
 * @author bekeles
 */
public final class CensusTractPovertyIndicatorUtils {

    public static final String ALG_NAME = "NAACCR Poverty Linkage Program";
    public static final String ALG_VERSION = "version 10.0 released in August 2020";

    //Unknown value for census tract poverty indicator
    public static final String POVERTY_INDICATOR_UNKNOWN = "9";

    public static final String YEAR_CATEGORY_1 = "1";
    public static final String YEAR_CATEGORY_2 = "2";
    public static final String YEAR_CATEGORY_3 = "3";
    public static final String YEAR_CATEGORY_4 = "4";
    public static final String YEAR_CATEGORY_5 = "5";
    public static final String YEAR_CATEGORY_6 = "6";
    public static final String YEAR_CATEGORY_7 = "7";
    public static final String YEAR_CATEGORY_8 = "8";
    public static final String YEAR_CATEGORY_9 = "9";
    public static final String YEAR_CATEGORY_10 = "10";
    public static final String YEAR_CATEGORY_11 = "11";

    private static final CensusTractPovertyIndicatorDataProvider _PROVIDER = new CensusTractPovertyIndicatorDataProvider();

    private CensusTractPovertyIndicatorUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates the census tract poverty indicator for the provided census tract poverty indicator input dto
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>_addressAtDxState</li>
     * <li>_countyAtDxAnalysis</li>
     * <li>_dateOfDiagnosisYear</li>
     * <li>_censusTract2000</li>
     * <li>_censusTract2010</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned poverty indicator will have the following values:
     * 1 = less than 5%
     * 2 = greater or equal to 5%, but less than 10%
     * 3 = greater or equal to 10%, but less than 20%
     * 4 = greater or equal to 20%
     * 9 = Unknown
     * <br/><br/>
     * @param input a <code>CensusTractPovertyIndicatorInputDto</code> input object
     * @return the computed poverty indicator value
     */
    public static CensusTractPovertyIndicatorOutputDto computePovertyIndicator(CensusTractPovertyIndicatorInputDto input) {
        return computePovertyIndicator(input, true);
    }

    /**
     * Calculates the census tract poverty indicator for the provided census tract poverty indicator input dto
     * If the boolean includeRecentYears is set to true, The algorithm uses 2009-2011 data for 2012+ diagnosis years.
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>_addressAtDxState</li>
     * <li>_countyAtDxAnalysis</li>
     * <li>_dateOfDiagnosisYear</li>
     * <li>_censusTract2000</li>
     * <li>_censusTract2010</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned poverty indicator will have the following values:
     * 1 = less than 5%
     * 2 = greater or equal to 5%, but less than 10%
     * 3 = greater or equal to 10%, but less than 20%
     * 4 = greater or equal to 20%
     * 9 = Unknown
     * <br/><br/>
     * @param input a <code>CensusTractPovertyIndicatorInputDto</code> input object
     * @param includeRecentYears if true, the indicator will be calculated for the past 2-3 years using the latest data, otherwise they will be set to 9
     * @return the computed poverty indicator value
     */
    public static CensusTractPovertyIndicatorOutputDto computePovertyIndicator(CensusTractPovertyIndicatorInputDto input, boolean includeRecentYears) {
        CensusTractPovertyIndicatorOutputDto result = new CensusTractPovertyIndicatorOutputDto();

        // if poverty indicator can not be calculated set it to unknown
        result.setCensusTractPovertyIndicator(POVERTY_INDICATOR_UNKNOWN);

        String dxYear = input.getDateOfDiagnosisYear();
        if (!NumberUtils.isDigits(dxYear) || input.getAddressAtDxState() == null || input.getCountyAtDxAnalysis() == null)
            return result;

        String censusTract;
        String yearCategory;
        int year = Integer.parseInt(dxYear);
        if (year >= 1995 && year <= 2004) {
            yearCategory = YEAR_CATEGORY_1;
            censusTract = input.getCensusTract2000();
        }
        else if (year >= 2005 && year <= 2007) {
            yearCategory = YEAR_CATEGORY_2;
            censusTract = input.getCensusTract2000();
        }
        else if (year == 2008) {
            yearCategory = YEAR_CATEGORY_3;
            censusTract = input.getCensusTract2010();
        }
        else if (year == 2009) {
            yearCategory = YEAR_CATEGORY_4;
            censusTract = input.getCensusTract2010();
        }
        else if (year == 2010) {
            yearCategory = YEAR_CATEGORY_5;
            censusTract = input.getCensusTract2010();
        }
        else if (year == 2011) {
            yearCategory = YEAR_CATEGORY_6;
            censusTract = input.getCensusTract2010();
        }
        else if (year == 2012) {
            yearCategory = YEAR_CATEGORY_7;
            censusTract = input.getCensusTract2010();
        }
        else if (year == 2013) {
            yearCategory = YEAR_CATEGORY_8;
            censusTract = input.getCensusTract2010();
        }
        else if (year == 2014) {
            yearCategory = YEAR_CATEGORY_9;
            censusTract = input.getCensusTract2010();
        }
        else if (year == 2015) {
            yearCategory = YEAR_CATEGORY_10;
            censusTract = input.getCensusTract2010();
        }
        else if (year >= 2016 && (year <= 2018 || (includeRecentYears && year <= LocalDate.now().getYear()))) {
            yearCategory = YEAR_CATEGORY_11;
            censusTract = input.getCensusTract2010();
        }
        else
            return result;

        if (censusTract != null)
            result.setCensusTractPovertyIndicator(_PROVIDER.getPovertyIndicator(yearCategory, input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), censusTract));

        // getPovertyIndicator method never returns null, but lets make sure we don't return null value anyways
        if (result.getCensusTractPovertyIndicator() == null)
            result.setCensusTractPovertyIndicator(POVERTY_INDICATOR_UNKNOWN);

        return result;
    }
}
