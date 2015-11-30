/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod10NodeDto extends HistStageDataDto {

    private List<Object> _sites;

    private List<Object> _histologies;

    private List<Object> _eod10LymphNodes;

    private String _result;

    public HistStageDataEod10NodeDto(String[] row) {

        _sites = parse(row[0]);
        _histologies = parse(row[1]);
        _eod10LymphNodes = parse(row[2]);
        _result = row[3];
    }

    public String computeResult(String site, String histology, String eod10LymphNodes) {
        String result = null;

        boolean found = find(_sites, site);
        if (found)
            found = find(_histologies, histology);
        if (found)
            found = find(_eod10LymphNodes, eod10LymphNodes);

        if (found)
            result = _result;

        return result;
    }
}
