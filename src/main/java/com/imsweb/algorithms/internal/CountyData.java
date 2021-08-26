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
    private String _uiho;

    // urban continuum data
    private String _urbanContinuum1993;
    private String _urbanContinuum2003;
    private String _urbanContinuum2013;

    // census data keyed by census tract code
    private Map<String, CensusData> _censusData = new HashMap<>();

    public String getPRCDA() {
        return _prcda;
    }

    public void setPRCDA(String prcda) {
        _prcda = prcda;
    }

    public String getUIHO() {
        return _uiho;
    }

    public void setUIHO(String uiho) {
        _uiho = uiho;
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
