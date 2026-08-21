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

    // Cancer Reporting Zone 2010
    private String _cancerReportingZone2010;

    // Cancer Reporting Zone Tract Certainty 2010
    private String _cancerReportingZoneTractCert2010;

    // Cancer Reporting Zone 2020
    private String _cancerReportingZone2020;

    // Cancer Reporting Zone Tract Certainty 2020
    private String _cancerReportingZoneTractCert2020;

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

    // Persistent Poverty 2007-2011
    private String _persistentPoverty0711;

    // Persistent Poverty 2017-2021
    private String _persistentPoverty1721;

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

    public String getCancerReportingZone2010() {
        return _cancerReportingZone2010;
    }

    public void setCancerReportingZone2010(String cancerReportingZone2010) {
        _cancerReportingZone2010 = cancerReportingZone2010;
    }

    public String getCancerReportingZoneTractCert2010() {
        return _cancerReportingZoneTractCert2010;
    }

    public void setCancerReportingZoneTractCert2010(String cancerReportingZoneTractCert2010) {
        _cancerReportingZoneTractCert2010 = cancerReportingZoneTractCert2010;
    }

    public String getCancerReportingZone2020() {
        return _cancerReportingZone2020;
    }

    public void setCancerReportingZone2020(String cancerReportingZone2020) {
        _cancerReportingZone2020 = cancerReportingZone2020;
    }

    public String getCancerReportingZoneTractCert2020() {
        return _cancerReportingZoneTractCert2020;
    }

    public void setCancerReportingZoneTractCert2020(String cancerReportingZoneTractCert2020) {
        _cancerReportingZoneTractCert2020 = cancerReportingZoneTractCert2020;
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

    public String getPersistentPoverty0711() {
        return _persistentPoverty0711;
    }

    public void setPersistentPoverty0711(String persistentPoverty0711) {
        _persistentPoverty0711 = persistentPoverty0711;
    }

    public String getPersistentPoverty1721() {
        return _persistentPoverty1721;
    }

    public void setPersistentPoverty1721(String persistentPoverty1721) {
        _persistentPoverty1721 = persistentPoverty1721;
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
