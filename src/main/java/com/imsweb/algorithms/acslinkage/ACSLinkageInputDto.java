/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.acslinkage;

public class ACSLinkageInputDto {

    private String _addressAtDxState;

    private String _addressAtDxCounty;

    private String _dateOfDiagnosisYear;

    private String _censusTract2010;

    public ACSLinkageInputDto() {
    }

    public String getAddressAtDxState() {
        return _addressAtDxState;
    }

    public void setAddressAtDxState(String addressAtDxState) {
        _addressAtDxState = addressAtDxState;
    }

    public String getAddressAtDxCounty() {
        return _addressAtDxCounty;
    }

    public void setAddressAtDxCounty(String addressAtDxCounty) {
        _addressAtDxCounty = addressAtDxCounty;
    }

    public String getDateOfDiagnosisYear() {
        return _dateOfDiagnosisYear;
    }

    public void setDateOfDiagnosisYear(String dateOfDiagnosisYear) {
        _dateOfDiagnosisYear = dateOfDiagnosisYear;
    }

    public String getCensusTract2010() {
        return _censusTract2010;
    }

    public void setCensusTract2010(String censusTract2010) {
        _censusTract2010 = censusTract2010;
    }
}
