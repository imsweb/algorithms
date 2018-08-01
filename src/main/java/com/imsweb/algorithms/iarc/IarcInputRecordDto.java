/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iarc;

public class IarcInputRecordDto {

    private String _dateOfDiagnosisYear;

    private String _dateOfDiagnosisMonth;

    private String _dateOfDiagnosisDay;

    private Integer _sequenceNumber;

    private String _site;

    private String _histology;

    private String _behavior;

    private String _siteGroup;

    private Integer _histGroup;

    private Integer _internationalPrimaryIndicator;

    public IarcInputRecordDto() {
    }

    public IarcInputRecordDto(String dateOfDiagnosisYear, String dateOfDiagnosisMonth, String dateOfDiagnosisDay, Integer sequenceNumber, String site, String histology, String behavior) {
        _dateOfDiagnosisYear = dateOfDiagnosisYear;
        _dateOfDiagnosisMonth = dateOfDiagnosisMonth;
        _dateOfDiagnosisDay = dateOfDiagnosisDay;
        _sequenceNumber = sequenceNumber;
        _site = site;
        _histology = histology;
        _behavior = behavior;
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

    public Integer getSequenceNumber() {
        return _sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        _sequenceNumber = sequenceNumber;
    }

    public String getSite() {
        return _site;
    }

    public void setSite(String site) {
        _site = site;
    }

    public String getHistology() {
        return _histology;
    }

    public void setHistology(String histology) {
        _histology = histology;
    }

    public String getBehavior() {
        return _behavior;
    }

    public void setBehavior(String behavior) {
        _behavior = behavior;
    }

    public String getSiteGroup() {
        return _siteGroup;
    }

    public void setSiteGroup(String siteGroup) {
        _siteGroup = siteGroup;
    }

    public Integer getHistGroup() {
        return _histGroup;
    }

    public void setHistGroup(Integer histGroup) {
        _histGroup = histGroup;
    }

    public Integer getInternationalPrimaryIndicator() {
        return _internationalPrimaryIndicator;
    }

    public void setInternationalPrimaryIndicator(Integer internationalPrimaryIndicator) {
        _internationalPrimaryIndicator = internationalPrimaryIndicator;
    }
}
