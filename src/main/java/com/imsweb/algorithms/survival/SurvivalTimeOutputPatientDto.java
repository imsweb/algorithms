/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.survival;

import java.util.List;

public class SurvivalTimeOutputPatientDto {

    private String _vitalStatusRecode;

    private List<SurvivalTimeOutputRecordDto> _survivalTimeOutputPatientDtoList;

    public String getVitalStatusRecode() {
        return _vitalStatusRecode;
    }

    public void setVitalStatusRecode(String vitalStatusRecode) {
        _vitalStatusRecode = vitalStatusRecode;
    }

    public List<SurvivalTimeOutputRecordDto> getSurvivalTimeOutputPatientDtoList() {
        return _survivalTimeOutputPatientDtoList;
    }

    public void setSurvivalTimeOutputPatientDtoList(List<SurvivalTimeOutputRecordDto> survivalTimeOutputPatientDtoList) {
        _survivalTimeOutputPatientDtoList = survivalTimeOutputPatientDtoList;
    }
}
