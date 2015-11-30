/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.survival;

import java.util.List;

public class SurvivalTimeInputPatientDto {

    private List<SurvivalTimeInputRecordDto> _survivalTimeInputPatientDtoList;

    public List<SurvivalTimeInputRecordDto> getSurvivalTimeInputPatientDtoList() {
        return _survivalTimeInputPatientDtoList;
    }

    public void setSurvivalTimeInputPatientDtoList(List<SurvivalTimeInputRecordDto> survivalTimeInputPatientDtoList) {
        _survivalTimeInputPatientDtoList = survivalTimeInputPatientDtoList;
    }
}
