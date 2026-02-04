/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

import java.util.List;

public class NhiaInputPatientDto {

    private String _spanishHispanicOrigin;

    private String _nameLast;

    private String _nameBirthSurname;

    private String _birthplaceCountry;

    private String _race1;

    private String _sexAssignedAtBirth;

    private String _ihs;

    private List<NhiaInputTumorDto> _tumors;

    public String getSpanishHispanicOrigin() {
        return _spanishHispanicOrigin;
    }

    public void setSpanishHispanicOrigin(String spanishHispanicOrigin) {
        _spanishHispanicOrigin = spanishHispanicOrigin;
    }

    public String getNameLast() {
        return _nameLast;
    }

    public void setNameLast(String nameLast) {
        _nameLast = nameLast;
    }

    public String getNameBirthSurname() {
        return _nameBirthSurname;
    }

    public void setNameBirthSurname(String nameBirthSurname) {
        _nameBirthSurname = nameBirthSurname;
    }

    public String getBirthplaceCountry() {
        return _birthplaceCountry;
    }

    public void setBirthplaceCountry(String birthplaceCountry) {
        _birthplaceCountry = birthplaceCountry;
    }

    public String getRace1() {
        return _race1;
    }

    public void setRace1(String race1) {
        _race1 = race1;
    }

    public String getSexAssignedAtBirth() {
        return _sexAssignedAtBirth;
    }

    public void setSexAssignedAtBirth(String sexAssignedAtBirth) {
        _sexAssignedAtBirth = sexAssignedAtBirth;
    }

    public String getIhs() {
        return _ihs;
    }

    public void setIhs(String ihs) {
        _ihs = ihs;
    }

    public List<NhiaInputTumorDto> getTumors() {
        return _tumors;
    }

    public void setTumors(List<NhiaInputTumorDto> tumors) {
        _tumors = tumors;
    }
}
