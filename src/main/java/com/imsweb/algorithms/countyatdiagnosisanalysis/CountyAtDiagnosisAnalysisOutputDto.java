/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.countyatdiagnosisanalysis;

public class CountyAtDiagnosisAnalysisOutputDto {

    private String _countyAtDxAnalysis;
    private String _countyAtDxAnalysisFlag;

    public String getCountyAtDxAnalysis() {
        return _countyAtDxAnalysis;
    }

    public void setCountyAtDxAnalysis(String countyAtDxAnalysis) {
        _countyAtDxAnalysis = countyAtDxAnalysis;
    }

    public String getCountyAtDxAnalysisFlag() {
        return _countyAtDxAnalysisFlag;
    }

    public void setCountyAtDxAnalysisFlag(String countyAtDxAnalysisFlag) {
        _countyAtDxAnalysisFlag = countyAtDxAnalysisFlag;
    }
}
