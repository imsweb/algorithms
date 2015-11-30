/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataCsNodeDto extends HistStageDataDto {

    private List<Object> _schema;

    private List<Object> _lymphNodes;

    private String _result;

    public HistStageDataCsNodeDto(String[] row) {

        _schema = parse(row[0]);
        _lymphNodes = parse(row[1]);
        _result = row[2];
    }

    public String computeResult(String schema, String lymphNode) {
        String result = null;

        boolean found = find(_schema, schema);
        if (found)
            found = find(_lymphNodes, lymphNode);
        if (found)
            result = _result;

        return result;
    }


}
