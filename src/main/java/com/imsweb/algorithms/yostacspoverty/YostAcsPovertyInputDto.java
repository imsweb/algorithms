package com.imsweb.algorithms.yostacspoverty;

public class YostAcsPovertyInputDto {

    private String _addressAtDxState;

    private String _countyAtDxAnalysis;

    private String _censusTract2010;

    private String _dateOfDiagnosis;

    public String getAddressAtDxState() {
        return _addressAtDxState;
    }

    public String getCensusTract2010() {
        return _censusTract2010;
    }

    public String getCountyAtDxAnalysis() {
        return _countyAtDxAnalysis;
    }

    public void setAddressAtDxState(String addressAtDxState) {
        _addressAtDxState = addressAtDxState;
    }

    public void setCensusTract2010(String censusTract2010) {
        _censusTract2010 = censusTract2010;
    }

    public void setCountyAtDxAnalysis(String countyAtDxAnalysis) {
        _countyAtDxAnalysis = countyAtDxAnalysis;
    }

    public String getDateOfDiagnosis() {
        return _dateOfDiagnosis;
    }

    public void setDateOfDiagnosis(String dateOfDiagnosis) {
        _dateOfDiagnosis = dateOfDiagnosis;
    }
}
