/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataCsMetsDto extends HistStageDataDto {

    private List<Object> _schema;

    private List<Object> _mets;

    private String _result;

    public HistStageDataCsMetsDto(String[] row) {

        _schema = parse(row[0]);
        _mets = parse(row[1]);
        _result = row[2];
    }

    public String computeResult(String schema, String mets) {
        String result = null;

        boolean found = find(_schema, schema);
        if (found)
            found = find(_mets, mets);
        if (found)
            result = _result;

        return result;
    }

}
