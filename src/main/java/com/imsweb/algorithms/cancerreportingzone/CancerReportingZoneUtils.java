/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.StateCountyTractInputDto.CensusTract;
import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

public final class CancerReportingZoneUtils {

    public static final String ALG_NAME = "NAACCR Cancer Reporting Zones";
    public static final String ALG_VERSION = "version 1.0 released in August 2021";

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
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned Cancer Reporting Zone will be a 5-digit or 7-digit alphanumeric code or it can be one of the unknown values listed below :
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

        if (input.hasInvalidStateCountyOrCensusTract(CensusTract._2010)) {
            result.setCancerReportingZone(CANCER_REPORTING_ZONE_UNK_A);
        }
        else if (input.hasUnknownStateCountyOrCensusTract(CensusTract._2010)) {
            result.setCancerReportingZone(CANCER_REPORTING_ZONE_UNK_D);
        }
        else if (input.countyIsNotReported())
            result.setCancerReportingZone(CANCER_REPORTING_ZONE_UNK_B);
        else {

            if (!CountryData.getInstance().isTractDataInitialized(input.getAddressAtDxState()))
                CountryData.getInstance().initializeTractData(input.getAddressAtDxState());

            StateData stateData = CountryData.getInstance().getTractData(input.getAddressAtDxState());
            if (stateData != null) {
                CountyData countyData = stateData.getCountyData(input.getCountyAtDxAnalysis());
                if (countyData != null) {
                    CensusData censusData = countyData.getCensusData(input.getCensusTract2010());
                    if (censusData != null)
                        result.setCancerReportingZone(censusData.getCancerReportingZone());
                }
            }
        }

        if (result.getCancerReportingZone() == null)
            result.setCancerReportingZone(CANCER_REPORTING_ZONE_UNK_C);

        return result;
    }
}
