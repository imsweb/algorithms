/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataCsExtDto extends HistStageDataDto {

    private List<Object> _schema;

    private List<Object> _extension;

    private String _result;

    public HistStageDataCsExtDto(String[] row) {

        _schema = parse(row[0]);
        _extension = parse(row[1]);
        _result = row[2];
    }

    public String computeResult(String schema, String extension) {
        String result = null;

        boolean found = find(_schema, schema);
        if (found)
            found = find(_extension, extension);
        if (found)
            result = _result;

        return result;
    }

}
