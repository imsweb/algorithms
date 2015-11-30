/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod2digExtNodeStageDto extends HistStageDataDto {

    private List<Object> _sites;

    private List<Object> _histologies;

    private List<Object> _extRecode;

    private List<Object> _nodeRecode;

    private String _result;

    public HistStageDataEod2digExtNodeStageDto(String[] row) {

        _sites = parse(row[0]);
        _histologies = parse(row[1]);
        _extRecode = parse(row[2]);
        _nodeRecode = parse(row[3]);
        _result = row[4];
    }

    public String computeResult(String site, String histology, String extRecode, String nodeRecode) {
        String result = null;

        boolean found = find(_sites, site);
        if (found)
            found = find(_histologies, histology);
        if (found)
            found = find(_extRecode, extRecode);
        if (found)
            found = find(_nodeRecode, nodeRecode);
        if (found)
            result = _result;

        return result;
    }

}
