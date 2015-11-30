/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary;

public class MPInput {

    private String _primarySite;

    private String _histologIcdO3;

    private String _behaviorIcdO3;

    private String _laterality;

    private String _dateOfDiagnosisYear;

    private String _dateOfDiagnosisMonth;

    private String _dateOfDiagnosisDay;

    public String getPrimarySite() {
        return _primarySite;
    }

    public void setPrimarySite(String primarySite) {
        _primarySite = primarySite;
    }

    public String getHistologIcdO3() {
        return _histologIcdO3;
    }

    public void setHistologIcdO3(String histologIcdO3) {
        _histologIcdO3 = histologIcdO3;
    }

    public String getBehaviorIcdO3() {
        return _behaviorIcdO3;
    }

    public void setBehaviorIcdO3(String behaviorIcdO3) {
        _behaviorIcdO3 = behaviorIcdO3;
    }

    public String getLaterality() {
        return _laterality;
    }

    public void setLaterality(String laterality) {
        _laterality = laterality;
    }

    public String getDateOfDiagnosisYear() {
        return _dateOfDiagnosisYear;
    }

    public void setDateOfDiagnosisYear(String dateOfDiagnosisYear) {
        _dateOfDiagnosisYear = dateOfDiagnosisYear;
    }

    public String getDateOfDiagnosisMonth() {
        return _dateOfDiagnosisMonth;
    }

    public void setDateOfDiagnosisMonth(String dateOfDiagnosisMonth) {
        _dateOfDiagnosisMonth = dateOfDiagnosisMonth;
    }

    public String getDateOfDiagnosisDay() {
        return _dateOfDiagnosisDay;
    }

    public void setDateOfDiagnosisDay(String dateOfDiagnosisDay) {
        _dateOfDiagnosisDay = dateOfDiagnosisDay;
    }

}
