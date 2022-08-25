/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.HashMap;
import java.util.Map;

public class CensusData {

    // RUCA 2000
    private String _commutingArea2000;

    // RUCA 2010
    private String _commutingArea2010;

    // URIC 2000
    private String _indicatorCode2000;

    // URIC 2010
    private String _indicatorCode2010;

    // poverty indicator data (1995-2004)
    private String _naaccrPovertyIndicator9504;

    // poverty indicator data (2005-2007)
    private String _naaccrPovertyIndicator0507;

    // Cancer Reporting Zone
    private String _cancerReportingZone;

    // Cancer Reporting Zone Tract Certainty
    private String _cancerReportingZoneTractCert;

    // EPHT SubCounty 5K
    private String _epht2010GeoId5k;

    // EPHT SubCounty 20K
    private String _epht2010GeoId20k;

    // EPHT SubCounty 50K
    private String _epht2010GeoId50k;

    // TractEstCongressDist
    private String _tractEstCongressDist;

    // Social Vulnerability Index
    private String _sviOverallStateBased;

    // year data keyed by DX year
    private final Map<String, YearData> _yearData = new HashMap<>();

    public String getIndicatorCode2000() {
        return _indicatorCode2000;
    }

    public void setIndicatorCode2000(String indicatorCode2000) {
        _indicatorCode2000 = indicatorCode2000;
    }

    public String getCommutingArea2000() {
        return _commutingArea2000;
    }

    public void setCommutingArea2000(String commutingArea2000) {
        _commutingArea2000 = commutingArea2000;
    }

    public String getIndicatorCode2010() {
        return _indicatorCode2010;
    }

    public void setIndicatorCode2010(String indicatorCode2010) {
        _indicatorCode2010 = indicatorCode2010;
    }

    public String getCommutingArea2010() {
        return _commutingArea2010;
    }

    public void setCommutingArea2010(String commutingArea2010) {
        _commutingArea2010 = commutingArea2010;
    }

    public String getNaaccrPovertyIndicator9504() {
        return _naaccrPovertyIndicator9504;
    }

    public void setNaaccrPovertyIndicator9504(String naaccrPovertyIndicator9504) {
        _naaccrPovertyIndicator9504 = naaccrPovertyIndicator9504;
    }

    public String getNaaccrPovertyIndicator0507() {
        return _naaccrPovertyIndicator0507;
    }

    public void setNaaccrPovertyIndicator0507(String naaccrPovertyIndicator0507) {
        _naaccrPovertyIndicator0507 = naaccrPovertyIndicator0507;
    }

    public String getEpht2010GeoId20k() {
        return _epht2010GeoId20k;
    }

    public void setEpht2010GeoId20k(String epht2010GeoId20k) {
        _epht2010GeoId20k = epht2010GeoId20k;
    }

    public String getEpht2010GeoId5k() {
        return _epht2010GeoId5k;
    }

    public void setEpht2010GeoId5k(String epht2010GeoId5k) {
        _epht2010GeoId5k = epht2010GeoId5k;
    }

    public String getEpht2010GeoId50k() {
        return _epht2010GeoId50k;
    }

    public void setEpht2010GeoId50k(String epht2010GeoId50k) {
        _epht2010GeoId50k = epht2010GeoId50k;
    }

    public String getTractEstCongressDist() {
        return _tractEstCongressDist;
    }

    public void setTractEstCongressDist(String tractEstCongressDist) {
        _tractEstCongressDist = tractEstCongressDist;
    }

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

    public String getSviOverallStateBased() {
        return _sviOverallStateBased;
    }

    public void setSviOverallStateBased(String sviOverallStateBased) {
        _sviOverallStateBased = sviOverallStateBased;
    }

    /**
     * Returns the census data for a given census tract code, null if not found.
     */
    public YearData getYearData(String year) {
        return _yearData.get(year);
    }

    /**
     * Returns all the data; package private so it's only used during initialization.
     */
    Map<String, YearData> getData() {
        return _yearData;
    }
}
