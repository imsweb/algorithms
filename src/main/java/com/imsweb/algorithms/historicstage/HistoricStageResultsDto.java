/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage;

public class HistoricStageResultsDto {

    private String _result;

    public HistoricStageResultsDto() {
    }

    public HistoricStageResultsDto(String result) {
        _result = result;
    }

    public String getResult() {
        return _result;

    }

    public void setResult(String result) {
        _result = result;
    }
}
