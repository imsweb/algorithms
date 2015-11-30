/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod13digExtDto extends HistStageDataDto {

    private List<Object> _sites;

    private List<Object> _digit4;

    private List<Object> _digit5;

    private List<Object> _digit6;

    private List<Object> _digit7;

    private List<Object> _digit8;

    private List<Object> _digit13;

    private String _result;

    public HistStageDataEod13digExtDto(String[] row) {

        _sites = parse(row[0]);
        _digit4 = parse(row[1]);
        _digit5 = parse(row[2]);
        _digit6 = parse(row[3]);
        _digit7 = parse(row[4]);
        _digit8 = parse(row[5]);
        _digit13 = parse(row[6]);
        _result = row[7];
    }

    public String computeResult(String site, String dig4, String dig5, String dig6, String dig7, String dig8, String dig13) {
        String result = null;

        boolean found = find(_sites, site);
        if (found)
            found = find(_digit4, dig4);
        if (found)
            found = find(_digit5, dig5);
        if (found)
            found = find(_digit6, dig6);
        if (found)
            found = find(_digit7, dig7);
        if (found)
            found = find(_digit8, dig8);
        if (found)
            found = find(_digit13, dig13);
        if (found)
            result = _result;

        return result;
    }

}
