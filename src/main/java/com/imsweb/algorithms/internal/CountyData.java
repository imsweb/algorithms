/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * County-related data.
 */
public class CountyData {

    // prcda, uiho data
    private String _prcda;
    private String _prcda2017;
    private String _uiho;
    private String _uihoCity;

    // urban continuum data
    private String _urbanContinuum1993;
    private String _urbanContinuum2003;
    private String _urbanContinuum2013;

    // census data keyed by census tract code
    private Map<String, CensusData> _censusData = new HashMap<>();

    public String getPrcda() {
        return _prcda;
    }

    public String getPrcda2017() {
        return _prcda2017;
    }

    public void setPrcda(String prcda) {
        _prcda = prcda;
    }

    public void setPrcda2017(String prcda2017) {
        _prcda2017 = prcda2017;
    }

    public String getUiho() {
        return _uiho;
    }

    public String getUihoCity() {
        return _uihoCity;
    }

    public void setUiho(String uiho) {
        _uiho = uiho;
    }

    public void setUihoCity(String uihoCity) {
        _uihoCity = uihoCity;
    }

    public String getUrbanContinuum1993() {
        return _urbanContinuum1993;
    }

    public void setUrbanContinuum1993(String urbanContinuum1993) {
        _urbanContinuum1993 = urbanContinuum1993;
    }

    public String getUrbanContinuum2003() {
        return _urbanContinuum2003;
    }

    public void setUrbanContinuum2003(String urbanContinuum2003) {
        _urbanContinuum2003 = urbanContinuum2003;
    }

    public String getUrbanContinuum2013() {
        return _urbanContinuum2013;
    }

    public void setUrbanContinuum2013(String urbanContinuum2013) {
        _urbanContinuum2013 = urbanContinuum2013;
    }

    /**
     * Returns the census data for a given census tract code, null if not found.
     */
    public CensusData getCensusData(String census) {
        return _censusData.get(census);
    }

    /**
     * Returns all the data; package private so it's only used during initialization.
     */
    Map<String, CensusData> getData() {
        return _censusData;
    }
}
