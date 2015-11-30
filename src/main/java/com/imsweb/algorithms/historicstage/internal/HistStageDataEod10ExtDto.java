/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod10ExtDto extends HistStageDataDto {

    private List<Object> _years;

    private List<Object> _sites;

    private List<Object> _histologies;

    private List<Object> _eod10Extension;

    private String _result;

    public HistStageDataEod10ExtDto(String[] row) {
        _years = parse(row[0]);
        _sites = parse(row[1]);
        _histologies = parse(row[2]);
        _eod10Extension = parse(row[3]);
        _result = row[4];
    }

    public String computeResult(String year, String site, String histology, String eod10Extension) {
        String result = null;

        boolean found = find(_years, year);
        if (found)
            found = find(_sites, site);
        if (found)
            found = find(_histologies, histology);
        if (found)
            found = find(_eod10Extension, eod10Extension);

        if (found)
            result = _result;

        return result;
    }
}
