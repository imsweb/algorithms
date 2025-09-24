/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

public class NhiaInputTumorDto {

    private String _countyAtDxAnalysis;

    private String _stateAtDx;

    public String getCountyAtDxAnalysis() {
        return _countyAtDxAnalysis;
    }

    public void setCountyAtDxAnalysis(String countyAtDx) {
        _countyAtDxAnalysis = countyAtDx;
    }

    public String getStateAtDx() {
        return _stateAtDx;
    }

    public void setStateAtDx(String stateAtDx) {
        this._stateAtDx = stateAtDx;
    }
}
