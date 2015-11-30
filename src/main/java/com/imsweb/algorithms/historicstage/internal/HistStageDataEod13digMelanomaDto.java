/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.List;

public class HistStageDataEod13digMelanomaDto extends HistStageDataDto {

    private List<Object> _sites;

    private List<Object> _digit5;

    private List<Object> _digit6;

    private List<Object> _digit9;

    private List<Object> _digit10;

    private List<Object> _digit11;

    private List<Object> _digit12;

    private List<Object> _digit13;

    private String _result;

    public HistStageDataEod13digMelanomaDto(String[] row) {

        _sites = parse(row[0]);
        _digit5 = parse(row[1]);
        _digit6 = parse(row[2]);
        _digit9 = parse(row[3]);
        _digit10 = parse(row[4]);
        _digit11 = parse(row[5]);
        _digit12 = parse(row[6]);
        _digit13 = parse(row[7]);
        _result = row[8];
    }

    public String computeResult(String site, String dig5, String dig6, String dig9, String dig10, String dig11, String dig12, String dig13) {
        String result = null;

        boolean found = find(_sites, site);
        if (found)
            found = find(_digit5, dig5);
        if (found)
            found = find(_digit6, dig6);
        if (found)
            found = find(_digit9, dig9);
        if (found)
            found = find(_digit10, dig10);
        if (found)
            found = find(_digit11, dig11);
        if (found)
            found = find(_digit12, dig12);
        if (found)
            found = find(_digit13, dig13);
        if (found)
            result = _result;

        return result;
    }

}
