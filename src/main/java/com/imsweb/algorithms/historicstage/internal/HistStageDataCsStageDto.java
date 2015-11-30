/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataCsStageDto extends HistStageDataDto {

    private List<Object> _extRecode;

    private List<Object> _nodeRecode;

    private List<Object> _matsRecode;

    private String _result;

    public HistStageDataCsStageDto(String[] row) {

        _extRecode = parse(row[0]);
        _nodeRecode = parse(row[1]);
        _matsRecode = parse(row[2]);
        _result = row[3];
    }

    public String computeResult(String extRecode, String nodeRecode, String metsRecode) {
        String result = null;

        boolean found = find(_extRecode, extRecode);
        if (found)
            found = find(_nodeRecode, nodeRecode);
        if (found)
            found = find(_matsRecode, metsRecode);
        if (found)
            result = _result;

        return result;
    }

}
