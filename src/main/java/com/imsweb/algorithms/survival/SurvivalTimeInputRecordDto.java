/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.survival;

public class SurvivalTimeInputRecordDto {

    private String _patientIdNumber;

    private String _dateOfDiagnosisYear;

    private String _dateOfDiagnosisMonth;

    private String _dateOfDiagnosisDay;

    private String _dateOfLastContactYear;

    private String _dateOfLastContactMonth;

    private String _dateOfLastContactDay;

    private String _birthYear;

    private String _birthMonth;

    private String _birthDay;

    private String _vitalStatus;

    private String _sequenceNumberCentral;

    private String _typeOfReportingSource;
   
    public String getPatientIdNumber() {
        return _patientIdNumber;
    }

    public void setPatientIdNumber(String patientIdNumber) {
        _patientIdNumber = patientIdNumber;
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

    public String getDateOfLastContactYear() {
        return _dateOfLastContactYear;
    }

    public void setDateOfLastContactYear(String dateOfLastContactYear) {
        _dateOfLastContactYear = dateOfLastContactYear;
    }

    public String getDateOfLastContactMonth() {
        return _dateOfLastContactMonth;
    }

    public void setDateOfLastContactMonth(String dateOfLastContactMonth) {
        _dateOfLastContactMonth = dateOfLastContactMonth;
    }

    public String getDateOfLastContactDay() {
        return _dateOfLastContactDay;
    }

    public void setDateOfLastContactDay(String dateOfLastContactDay) {
        _dateOfLastContactDay = dateOfLastContactDay;
    }

    public String getVitalStatus() {
        return _vitalStatus;
    }

    public void setVitalStatus(String vitalStatus) {
        _vitalStatus = vitalStatus;
    }

    public String getSequenceNumberCentral() {
        return _sequenceNumberCentral;
    }

    public void setSequenceNumberCentral(String sequenceNumberCentral) {
        _sequenceNumberCentral = sequenceNumberCentral;
    }

    public String getBirthYear() {
        return _birthYear;
    }

    public void setBirthYear(String birthYear) {
        _birthYear = birthYear;
    }

    public String getBirthMonth() {
        return _birthMonth;
    }

    public void setBirthMonth(String birthMonth) {
        _birthMonth = birthMonth;
    }

    public String getBirthDay() {
        return _birthDay;
    }

    public void setBirthDay(String birthDay) {
        _birthDay = birthDay;
    }

    public String getTypeOfReportingSource() {
        return _typeOfReportingSource;
    }

    public void setTypeOfReportingSource(String typeOfReportingSource) {
        _typeOfReportingSource = typeOfReportingSource;
    }

}
