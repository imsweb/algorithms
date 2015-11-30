/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

import java.util.List;

public class NhiaInputPatientDto {
    
    List<NhiaInputRecordDto> _nhiaInputPatientDtoList;

    public List<NhiaInputRecordDto> getNhiaInputPatientDtoList() {
        return _nhiaInputPatientDtoList;
    }

    public void setNhiaInputPatientDtoList(List<NhiaInputRecordDto> nhiaInputPatientDtoList) {
        _nhiaInputPatientDtoList = nhiaInputPatientDtoList;
    }
}
