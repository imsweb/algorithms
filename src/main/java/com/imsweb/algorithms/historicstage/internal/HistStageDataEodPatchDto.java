/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEodPatchDto extends HistStageDataDto {

    private List<Object> _reportingSources;

    private List<Object> _sites;

    private List<Object> _histologies;

    private String _result;

    public HistStageDataEodPatchDto(String[] row) {

        _reportingSources = parse(row[0]);
        _sites = parse(row[1]);
        _histologies = parse(row[2]);
        _result = row[3];
    }

    public String computeResult(String reportingSource, String site, String histology) {
        String result = null;

        boolean found = find(_reportingSources, reportingSource);
        if (found)
            found = find(_sites, site);
        if (found)
            found = find(_histologies, histology);
        if (found)
            result = _result;

        return result;
    }

}
