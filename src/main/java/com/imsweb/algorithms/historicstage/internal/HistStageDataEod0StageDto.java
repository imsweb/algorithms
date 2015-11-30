/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod0StageDto extends HistStageDataDto {

    private List<Object> _sites;

    private List<Object> _histologies;

    private List<Object> _digit1;

    private List<Object> _digit2;

    private String _result;

    public HistStageDataEod0StageDto(String[] row) {

        _sites = parse(row[0]);
        _histologies = parse(row[1]);
        _digit1 = parse(row[2]);
        _digit2 = parse(row[3]);
        _result = row[4];
    }

    public String computeResult(String site, String histology, String dig1, String dig2) {
        String result = null;

        boolean found = find(_sites, site);
        if (found)
            found = find(_histologies, histology);
        if (found)
            found = find(_digit1, dig1);
        if (found)
            found = find(_digit2, dig2);
        if (found)
            result = _result;

        return result;
    }

}
