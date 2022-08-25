/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

public class CancerReportingZoneOutputDto {

    private String _cancerReportingZone;

    private String _cancerReportingZoneTractCert;

    public String getCancerReportingZone() {
        return _cancerReportingZone;
    }

    public void setCancerReportingZone(String cancerReportingZone) {
        _cancerReportingZone = cancerReportingZone;
    }

    public String getCancerReportingZoneTractCert() {
        return _cancerReportingZoneTractCert;
    }

    public void setCancerReportingZoneTractCert(String cancerReportingZoneTractCert) {
        _cancerReportingZoneTractCert = cancerReportingZoneTractCert;
    }
}
