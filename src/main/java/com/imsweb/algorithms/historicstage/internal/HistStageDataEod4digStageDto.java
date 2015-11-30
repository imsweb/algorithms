/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod4digStageDto extends HistStageDataDto {

    private List<Object> _histologies;

    private List<Object> _sites;

    private List<Object> _eod4digExt;

    private List<Object> _eod4digNode;

    private String _result;

    public HistStageDataEod4digStageDto(String[] row) {

        _histologies = parse(row[0]);
        _sites = parse(row[1]);
        _eod4digExt = parse(row[2]);
        _eod4digNode = parse(row[3]);
        _result = row[4];
    }

    public String computeResult(String histology, String site, String eod4digExt, String eod4digNode) {
        String result = null;

        boolean found = find(_histologies, histology);
        if (found)
            found = find(_sites, site);
        if (found)
            found = find(_eod4digExt, eod4digExt);
        if (found)
            found = find(_eod4digNode, eod4digNode);
        if (found)
            result = _result;

        return result;
    }

}
