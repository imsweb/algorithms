/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod13digGeneralStageDto extends HistStageDataDto {

    private List<Object> _sites;

    private List<Object> _extRecode;

    private List<Object> _nodeRecode;

    private String _result;

    public HistStageDataEod13digGeneralStageDto(String[] row) {

        _sites = parse(row[0]);
        _extRecode = parse(row[1]);
        _nodeRecode = parse(row[2]);
        _result = row[3];
    }

    public String computeResult(String site, String extRecode, String nodeRecode) {
        String result = null;

        boolean found = find(_sites, site);
        if (found)
            found = find(_extRecode, extRecode);
        if (found)
            found = find(_nodeRecode, nodeRecode);
        if (found)
            result = _result;

        return result;
    }

}
