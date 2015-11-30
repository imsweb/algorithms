/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod10StageDto extends HistStageDataDto {

    private List<Object> _extRecode;

    private List<Object> _nodeRecode;

    private String _result;

    public HistStageDataEod10StageDto(String[] row) {

        _extRecode = parse(row[0]);
        _nodeRecode = parse(row[1]);
        _result = row[2];
    }

    public String computeResult(String extRecode, String nodeRecode) {
        String result = null;

        boolean found = find(_extRecode, extRecode);
        if (found)
            found = find(_nodeRecode, nodeRecode);
        if (found)
            result = _result;

        return result;
    }

}
