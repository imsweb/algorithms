/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import java.time.LocalDate;

import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.StateCountyTractInputDto.CensusTract;
import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.yostacspoverty.YostAcsPovertyInputDto;
import com.imsweb.algorithms.yostacspoverty.YostAcsPovertyUtils;

/**
 * This class can be used to calculate census tract poverty indicator.
 * Created on Oct 17, 2013 by BekeleS
 * @author bekeles
 */
public final class CensusTractPovertyIndicatorUtils {

    public static final String ALG_NAME = "NAACCR Poverty Linkage Program";
    public static final String ALG_VERSION = "version 11.0 released in September 2020";

    //Unknown value for census tract poverty indicator
    public static final String POVERTY_INDICATOR_UNKNOWN = "9";

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
     * <li>_censusTract2020</li>
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
     * <li>_censusTract2020</li>
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

        int year = Integer.parseInt(dxYear);
        if (year >= 1995 && year <= 2007) {
            CensusData censusData = CountryData.getCensusData(input, CensusTract.CENSUS_2000);
            if (censusData != null)
                result.setCensusTractPovertyIndicator(year <= 2004 ? censusData.getNaaccrPovertyIndicator9504() : censusData.getNaaccrPovertyIndicator0507());
        }
        else if (year >= 2008 && (year <= 2021 || (includeRecentYears && year <= LocalDate.now().getYear()))) {
            YostAcsPovertyInputDto yostAcsPovertyInput = new YostAcsPovertyInputDto();
            yostAcsPovertyInput.setAddressAtDxState(input.getAddressAtDxState());
            yostAcsPovertyInput.setCountyAtDxAnalysis(input.getCountyAtDxAnalysis());
            yostAcsPovertyInput.setCensusTract2010(input.getCensusTract2010());
            yostAcsPovertyInput.setCensusTract2020(input.getCensusTract2020());
            yostAcsPovertyInput.setDateOfDiagnosis(String.valueOf(Math.min(year, 2021))); // we have to use 2017 (last year of data available) for all "recent years"...
            result.setCensusTractPovertyIndicator(deriveValueFromPercentage(YostAcsPovertyUtils.computeYostAcsPovertyData(yostAcsPovertyInput).getAcsPctPovAllRaces()));
        }

        // safety net - never return a null value
        if (result.getCensusTractPovertyIndicator() == null)
            result.setCensusTractPovertyIndicator(POVERTY_INDICATOR_UNKNOWN);

        return result;
    }

    static String deriveValueFromPercentage(String percentStr) {
        if (percentStr == null)
            return POVERTY_INDICATOR_UNKNOWN;

        try {
            float percent = NumberUtils.createFloat(percentStr);

            if (percent < 5.0)
                return "1";
            else if (percent < 10.0)
                return "2";
            else if (percent < 20.0)
                return "3";
            else
                return "4";
        }
        catch (NumberFormatException e) {
            return POVERTY_INDICATOR_UNKNOWN;
        }

    }
}
