/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.napiia;

import java.util.List;

public class NapiiaInputPatientDto {
    
    List<NapiiaInputRecordDto> _napiiaInputPatientDtoList;

    public List<NapiiaInputRecordDto> getNapiiaInputPatientDtoList() {
        return _napiiaInputPatientDtoList;
    }

    public void setNapiiaInputPatientDtoList(List<NapiiaInputRecordDto> napiiaInputPatientDtoList) {
        _napiiaInputPatientDtoList = napiiaInputPatientDtoList;
    }
}
