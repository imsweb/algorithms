/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

public class CensusTractPovertyIndicatorInputDto {

    private String _addressAtDxState;

    private String _countyAtDxAnalysis;

    private String _dateOfDiagnosisYear;

    private String _censusTract2000;

    private String _censusTract2010;

    public String getAddressAtDxState() {
        return _addressAtDxState;
    }

    public void setAddressAtDxState(String addressAtDxState) {
        _addressAtDxState = addressAtDxState;
    }

    public String getCountyAtDxAnalysis() {
        return _countyAtDxAnalysis;
    }

    public void setCountyAtDxAnalysis(String countyAtDxAnalysis) {
        _countyAtDxAnalysis = countyAtDxAnalysis;
    }

    public String getDateOfDiagnosisYear() {
        return _dateOfDiagnosisYear;
    }

    public void setDateOfDiagnosisYear(String dateOfDiagnosisYear) {
        _dateOfDiagnosisYear = dateOfDiagnosisYear;
    }

    public String getCensusTract2000() {
        return _censusTract2000;
    }

    public void setCensusTract2000(String censusTract2000) {
        _censusTract2000 = censusTract2000;
    }

    public String getCensusTract2010() {
        return _censusTract2010;
    }

    public void setCensusTract2010(String censusTract2010) {
        _censusTract2010 = censusTract2010;
    }
}
