/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

public class CancerReportingZoneUtils {

    public static final String ALG_NAME = "Cancer Reporting Zones";
    public static final String ALG_VERSION = "1.0";
    public static final String ALG_INFO = "Cancer Reporting Zones version 1.0 released in August 2021";

    //Unknown values for each code
    public static final String CANCER_REPORTING_ZONE_UNKNOWN = "C";

    // data provider
    private static CancerReportingZoneDataProvider _PROVIDER;

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
     * @param input a <code>CancerReportingZoneInputDto</code> input object
     * @return the computed Cancer Reporting Zone value
     */
    public static CancerReportingZoneOutputDto computeCancerReportingZone(CancerReportingZoneInputDto input) {
        CancerReportingZoneOutputDto result = new CancerReportingZoneOutputDto();
        input.applyRecodes();

        if (!input.isStateValidOrMissingOrUnknown() || !input.isCountyValidOrMissingOrUnknown() || !input.isCensusTract2010ValidOrMissingOrUnknown())
            result.setCancerReportingZone("A");
        else if (input.isStateMissingOrUnknown() || input.isCountyMissingOrUnknown() || input.isCensusTract2010MissingOrUnknown())
            result.setCancerReportingZone("D");
        else if ("000".equals(input.getCountyAtDxAnalysis()))
            result.setCancerReportingZone("B");
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            result.setCancerReportingZone(_PROVIDER.getCancerReportingZone(input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010()));
        }
        if (result.getCancerReportingZone() == null)
            result.setCancerReportingZone(CANCER_REPORTING_ZONE_UNKNOWN);

        return result;
    }

    /**
     * Use this method to register your own data provider instead of using the internal one that is entirely in memory.
     * <br/><br/>
     * This has to be done before the first call to the compute method, or the internal one will be registered by default.
     * <br/><br/>
     * Once a provider has been set, this method cannot be called (it will throw an exception).
     * @param provider the <code>CancerReportingZoneDataProvider</code> to set
     */
    public static synchronized void setDataProvider(CancerReportingZoneDataProvider provider) {
        if (_PROVIDER != null)
            throw new RuntimeException("The data provider has already been set!");
        _PROVIDER = provider;
    }

    private static synchronized void initializeInternalDataProvider() {
        if (_PROVIDER != null)
            return;
        _PROVIDER = new CancerReportingZoneCsvData();
    }
}
