/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

public class NhiaInputRecordDto {

    private String _spanishHispanicOrigin;

    private String _nameLast;

    private String _nameMaiden;

    private String _birthplaceCountry;

    private String _race1;

    private String _sex;

    private String _ihs;

    private String _countyAtDx;

    private String _stateAtDx;

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

    public String getNameMaiden() {
        return _nameMaiden;
    }

    public void setNameMaiden(String nameMaiden) {
        _nameMaiden = nameMaiden;
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

    public String getSex() {
        return _sex;
    }

    public void setSex(String sex) {
        _sex = sex;
    }

    public String getIhs() {
        return _ihs;
    }

    public void setIhs(String ihs) {
        _ihs = ihs;
    }

    public String getCountyAtDx() {
        return _countyAtDx;
    }

    public void setCountyAtDx(String countyAtDx) {
        this._countyAtDx = countyAtDx;
    }

    public String getStateAtDx() {
        return _stateAtDx;
    }

    public void setStateAtDx(String stateAtDx) {
        this._stateAtDx = stateAtDx;
    }
}
