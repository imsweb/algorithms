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

public final class CensusTractPovertyIndicatorUtils {

    public static final String ALG_NAME = "NAACCR Poverty Linkage Program";
    public static final String ALG_VERSION = "released in September 2025";

    //Unknown value for census tract poverty indicator
    public static final String POVERTY_INDICATOR_UNKNOWN = "9";

    private CensusTractPovertyIndicatorUtils() {
        // no instances of this class allowed!
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
     * @return the computed poverty indicator value
     */
    public static CensusTractPovertyIndicatorOutputDto computePovertyIndicator(CensusTractPovertyIndicatorInputDto input) {
        CensusTractPovertyIndicatorOutputDto result = new CensusTractPovertyIndicatorOutputDto();

        // if poverty indicator can not be calculated set it to unknown
        result.setCensusTractPovertyIndicator(POVERTY_INDICATOR_UNKNOWN);

        String dxYear = input.getDateOfDiagnosisYear();
        if (!NumberUtils.isDigits(dxYear) || input.getAddressAtDxState() == null || input.getCountyAtDxAnalysis() == null)
            return result;

        // 1. If DX Year is 1995-2004, use census 2000 and special 95-04 lookup (not year-dependent).
        // 2. If DX Year is 2005 use census 2000 and special 05-07 lookup (not year-dependent).
        // 3. If DX Year 2006-2007 use 2008 data from 2010 census
        // 4. If DX Year is 2008-2015: use census 2010 with provided year and derive value from ACS.
        // 5. if DX Year is 2016-2017: use 2018 data from 2020 census
        // 6. If DX Year is 2018-2021 use census 2020 from provided year and derive value from ACS.
        // 7. If DX Year 2022+, use 2021 data from 2020 census
        // 8. Else return unknown

        int year = Integer.parseInt(dxYear);
        if (year >= 1995 && year <= 2005) {
            CensusData censusData = CountryData.getCensusData(input, CensusTract.CENSUS_2000);
            if (censusData != null)
                result.setCensusTractPovertyIndicator(year <= 2004 ? censusData.getNaaccrPovertyIndicator9504() : censusData.getNaaccrPovertyIndicator0507());
        }
        else if (year >= 2006 && year <= LocalDate.now().getYear()) {
            YostAcsPovertyInputDto yostAcsPovertyInput = new YostAcsPovertyInputDto();
            yostAcsPovertyInput.setAddressAtDxState(input.getAddressAtDxState());
            yostAcsPovertyInput.setCountyAtDxAnalysis(input.getCountyAtDxAnalysis());
            yostAcsPovertyInput.setCensusTract2010(input.getCensusTract2010());
            yostAcsPovertyInput.setCensusTract2020(input.getCensusTract2020());
            yostAcsPovertyInput.setDateOfDiagnosis(dxYear);
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
