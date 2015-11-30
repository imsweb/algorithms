/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataSchemaDto extends HistStageDataDto {

    private List<Object> _sites;

    private List<Object> _histologies;

    private String _result;

    public HistStageDataSchemaDto(String[] row) {

        _sites = parse(row[0]);
        _histologies = parse(row[1]);
        _result = row[2];
    }

    public String computeResult(String site, String histology) {
        String result = null;

        boolean found = find(_sites, site);
        if (found)
            found = find(_histologies, histology);
        if (found)
            result = _result;

        return result;
    }

}
