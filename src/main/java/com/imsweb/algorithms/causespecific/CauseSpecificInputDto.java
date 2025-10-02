/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

public class CauseSpecificInputDto {

    private String _primarySite;

    private String _histologyIcdO3;

    private String _behaviorIcdO3;

    private String _dateOfDiagnosisYear;

    private String _sequenceNumberCentral;

    private String _icdRevisionNumber;

    private String _causeOfDeath;

    private String _dateOfLastContactYear;

    public String getPrimarySite() {
        return _primarySite;
    }

    public void setPrimarySite(String primarySite) {
        _primarySite = primarySite;
    }

    public String getHistologyIcdO3() {
        return _histologyIcdO3;
    }

    public void setHistologyIcdO3(String histologyIcdO3) {
        _histologyIcdO3 = histologyIcdO3;
    }

    public String getBehaviorIcdO3() {
        return _behaviorIcdO3;
    }

    public void setBehaviorIcdO3(String behaviorIcdO3) {
        _behaviorIcdO3 = behaviorIcdO3;
    }

    public String getDateOfDiagnosisYear() {
        return _dateOfDiagnosisYear;
    }

    public void setDateOfDiagnosisYear(String dateOfDiagnosisYear) {
        _dateOfDiagnosisYear = dateOfDiagnosisYear;
    }

    public String getSequenceNumberCentral() {
        return _sequenceNumberCentral;
    }

    public void setSequenceNumberCentral(String sequenceNumberCentral) {
        _sequenceNumberCentral = sequenceNumberCentral;
    }

    public String getIcdRevisionNumber() {
        return _icdRevisionNumber;
    }

    public void setIcdRevisionNumber(String icdRevisionNumber) {
        _icdRevisionNumber = icdRevisionNumber;
    }

    public String getCauseOfDeath() {
        return _causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        _causeOfDeath = causeOfDeath;
    }

    public String getDateOfLastContactYear() {
        return _dateOfLastContactYear;
    }

    public void setDateOfLastContactYear(String dateOfLastContactYear) {
        _dateOfLastContactYear = dateOfLastContactYear;
    }
}
