/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.StateCountyTractInputDto.CensusTract;
import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;

public final class CancerReportingZoneUtils {

    public static final String ALG_NAME = "NAACCR Cancer Reporting Zones";
    public static final String ALG_VERSION = "August 21, 2026";

    //Unknown values for each code
    public static final String CANCER_REPORTING_ZONE_UNK_A = "A";
    public static final String CANCER_REPORTING_ZONE_UNK_B = "B";
    public static final String CANCER_REPORTING_ZONE_UNK_C = "C";
    public static final String CANCER_REPORTING_ZONE_UNK_D = "D";

    private CancerReportingZoneUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates the Cancer Reporting Zone for the provided input DTO
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * <li>censusTract2020 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned Cancer Reporting Zone will be a 5-digit or 7-digit alphanumeric code, or it can be one of the unknown values listed below :
     * <ul>
     * <li>A = State, county, or tract are invalid</li>
     * <li>B = State and tract are valid, but county was not reported</li>
     * <li>C = State + county + tract combination was not found</li>
     * <li>D = State, county, or tract are blank or unknown/li>
     * </ul>
     * <br/><br/>
     * @param input a <code>StateCountyTractInputDto</code> input object
     * @return the computed Cancer Reporting Zone value
     */
    public static CancerReportingZoneOutputDto computeCancerReportingZone(StateCountyTractInputDto input) {
        CancerReportingZoneOutputDto result = new CancerReportingZoneOutputDto();

        input.applyRecodes();

        if (input.hasInvalidStateCountyOrCensusTract(CensusTract.CENSUS_2020)) {
            // 2010
            result.setCancerReportingZone2010(CANCER_REPORTING_ZONE_UNK_A);
            result.setCancerReportingZoneTractReq2010(CANCER_REPORTING_ZONE_UNK_A);
            // 2020
            result.setCancerReportingZone2020(CANCER_REPORTING_ZONE_UNK_A);
            result.setCancerReportingZoneTractReq2020(CANCER_REPORTING_ZONE_UNK_A);
        }
        else if (input.hasUnknownStateCountyOrCensusTract(CensusTract.CENSUS_2020)) {
            // 2010
            result.setCancerReportingZone2010(CANCER_REPORTING_ZONE_UNK_D);
            result.setCancerReportingZoneTractReq2010(CANCER_REPORTING_ZONE_UNK_D);
            // 2020
            result.setCancerReportingZone2020(CANCER_REPORTING_ZONE_UNK_D);
            result.setCancerReportingZoneTractReq2020(CANCER_REPORTING_ZONE_UNK_D);
        }
        else if (input.countyIsNotReported()) {
            // 2010
            result.setCancerReportingZone2010(CANCER_REPORTING_ZONE_UNK_B);
            result.setCancerReportingZoneTractReq2010(CANCER_REPORTING_ZONE_UNK_B);
            // 2020
            result.setCancerReportingZone2020(CANCER_REPORTING_ZONE_UNK_B);
            result.setCancerReportingZoneTractReq2020(CANCER_REPORTING_ZONE_UNK_B);
        }
        else {
            CensusData censusData = CountryData.getCensusData(input, CensusTract.CENSUS_2020);
            if (censusData != null) {
                // 2010
                result.setCancerReportingZone2010(censusData.getCancerReportingZone2010());
                result.setCancerReportingZoneTractReq2010(censusData.getCancerReportingZoneTractReq2010());
                // 2020
                result.setCancerReportingZone2020(censusData.getCancerReportingZone2020());
                result.setCancerReportingZoneTractReq2020(censusData.getCancerReportingZoneTractReq2020());
            }
        }

        // 2010
        if (result.getCancerReportingZone2010() == null)
            result.setCancerReportingZone2010(CANCER_REPORTING_ZONE_UNK_C);
        if (result.getCancerReportingZoneTractReq2010() == null)
            result.setCancerReportingZoneTractReq2010(CANCER_REPORTING_ZONE_UNK_C);
        // 2020
        if (result.getCancerReportingZone2020() == null)
            result.setCancerReportingZone2020(CANCER_REPORTING_ZONE_UNK_C);
        if (result.getCancerReportingZoneTractReq2020() == null)
            result.setCancerReportingZoneTractReq2020(CANCER_REPORTING_ZONE_UNK_C);

        return result;
    }
}
