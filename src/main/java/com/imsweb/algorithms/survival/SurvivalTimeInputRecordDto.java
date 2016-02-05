/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.survival;

import org.apache.commons.lang3.math.NumberUtils;

public class SurvivalTimeInputRecordDto implements Comparable<SurvivalTimeInputRecordDto> {

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

    private int _sortedIndex;

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

    public int getSortedIndex() {
        return _sortedIndex;
    }

    public void setSortedIndex(int sortedIndex) {
        _sortedIndex = sortedIndex;
    }

    @Override
    public int compareTo(SurvivalTimeInputRecordDto other) {
        int year = NumberUtils.isDigits(_dateOfDiagnosisYear) ? Integer.parseInt(_dateOfDiagnosisYear) : 9999;
        int month = NumberUtils.isDigits(_dateOfDiagnosisMonth) ? Integer.parseInt(_dateOfDiagnosisMonth) : 99;
        int day = NumberUtils.isDigits(_dateOfDiagnosisDay) ? Integer.parseInt(_dateOfDiagnosisDay) : 99;
        if (month == 99)
            day = 99;
        int seqNum = NumberUtils.isDigits(_sequenceNumberCentral) ? Integer.parseInt(_sequenceNumberCentral) : -1;
        if (seqNum >= 60 && seqNum <= 97)
            seqNum = seqNum + 100;

        int otherYear = NumberUtils.isDigits(other._dateOfDiagnosisYear) ? Integer.parseInt(other._dateOfDiagnosisYear) : 9999;
        int otherMonth = NumberUtils.isDigits(other._dateOfDiagnosisMonth) ? Integer.parseInt(other._dateOfDiagnosisMonth) : 99;
        int otherDay = NumberUtils.isDigits(other._dateOfDiagnosisDay) ? Integer.parseInt(other._dateOfDiagnosisDay) : 99;
        if (otherMonth == 99)
            otherDay = 99;
        int otherSeqNum = NumberUtils.isDigits(other._sequenceNumberCentral) ? Integer.parseInt(other._sequenceNumberCentral) : -1;
        if (otherSeqNum >= 60 && otherSeqNum <= 97)
            otherSeqNum = otherSeqNum + 100;

        if (year == 9999 || otherYear == 9999)
            return seqNum - otherSeqNum;
        else if (year != otherYear)
            return year - otherYear;
        else {
            if (month == 99 || otherMonth == 99)
                return seqNum - otherSeqNum;
            else if (month != otherMonth)
                return month - otherMonth;
            else {
                if (day == 99 || otherDay == 99 || day == otherDay)
                    return seqNum - otherSeqNum;
                else
                    return day - otherDay;
            }
        }
    }
}
