/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

public interface CancerReportingZoneDataProvider {

    /**
     * Returns Cancer Reporting Zone code for provided state of dx, county of dx for analysis, and census tract.
     * <p/>
     * @param state state at DX
     * @param county county at DX for analysis
     * @param censusTract census tract
     * @return the corresponding Cancer Reporting Zone code
     */
    String getCancerReportingZone(String state, String county, String censusTract);
}
