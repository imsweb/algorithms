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

    // RUCA 2020
    private String _commutingArea2020;

    // URIC 2000
    private String _indicatorCode2000;

    // URIC 2010
    private String _indicatorCode2010;

    // URIC 2020
    private String _indicatorCode2020;

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

    // Social Vulnerability Index (2018)
    private String _sviOverallStateBased2018;

    // Social Vulnerability Index (2022)
    private String _sviOverallStateBased2022;

    // Congressional district (118)
    private String _congressionalDistrict118;

    // Congressional district (119)
    private String _congressionalDistrict119;

    // Persistent Poverty
    private String _persistentPoverty;

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

    public String getIndicatorCode2020() {
        return _indicatorCode2020;
    }

    public void setIndicatorCode2020(String indicatorCode2020) {
        _indicatorCode2020 = indicatorCode2020;
    }

    public String getCommutingArea2020() {
        return _commutingArea2020;
    }

    public void setCommutingArea2020(String commutingArea2020) {
        _commutingArea2020 = commutingArea2020;
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

    public String getSviOverallStateBased2018() {
        return _sviOverallStateBased2018;
    }

    public void setSviOverallStateBased2018(String sviOverallStateBased2018) {
        _sviOverallStateBased2018 = sviOverallStateBased2018;
    }

    public String getSviOverallStateBased2022() {
        return _sviOverallStateBased2022;
    }

    public void setSviOverallStateBased2022(String sviOverallStateBased2022) {
        _sviOverallStateBased2022 = sviOverallStateBased2022;
    }

    public String getCongressionalDistrict118() {
        return _congressionalDistrict118;
    }

    public void setCongressionalDistrict118(String congressionalDistrict118) {
        _congressionalDistrict118 = congressionalDistrict118;
    }

    public String getCongressionalDistrict119() {
        return _congressionalDistrict119;
    }

    public void setCongressionalDistrict119(String congressionalDistrict119) {
        _congressionalDistrict119 = congressionalDistrict119;
    }

    public String getPersistentPoverty() {
        return _persistentPoverty;
    }

    public void setPersistentPoverty(String persistentPoverty) {
        _persistentPoverty = persistentPoverty;
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
