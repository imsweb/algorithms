/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import com.imsweb.algorithms.StateCountyTractInputDto;

public class CensusTractPovertyIndicatorInputDto extends StateCountyTractInputDto {

    private String _dateOfDiagnosisYear;

    public String getDateOfDiagnosisYear() {
        return _dateOfDiagnosisYear;
    }

    public void setDateOfDiagnosisYear(String dateOfDiagnosisYear) {
        _dateOfDiagnosisYear = dateOfDiagnosisYear;
    }
}
