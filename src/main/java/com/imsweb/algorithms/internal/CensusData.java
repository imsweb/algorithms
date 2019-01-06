/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.Map;

public class CensusData {

    // RUCA data
    private String _commutingArea2000;
    private String _commutingArea2010;

    // URIC data
    private String _indicatorCode2000;
    private Float _indicatorCodePercentage2000;
    private String _indicatorCode2010;
    private Float _indicatorCodePercentage2010;

    // poverty indicator data
    private Map<String, String> _povertyIndicators;

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

    public Float getIndicatorCodePercentage2000() {
        return _indicatorCodePercentage2000;
    }

    public void setIndicatorCodePercentage2000(Float indicatorCodePercentage2000) {
        _indicatorCodePercentage2000 = indicatorCodePercentage2000;
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

    public Float getIndicatorCodePercentage2010() {
        return _indicatorCodePercentage2010;
    }

    public void setIndicatorCodePercentage2010(Float indicatorCodePercentage2010) {
        _indicatorCodePercentage2010 = indicatorCodePercentage2010;
    }

    public Map<String, String> getPovertyIndicators() {
        return _povertyIndicators;
    }

    public void setPovertyIndicators(Map<String, String> povertyIndicators) {
        _povertyIndicators = povertyIndicators;
    }
}
