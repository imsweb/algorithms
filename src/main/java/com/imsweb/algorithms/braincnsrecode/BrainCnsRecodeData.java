/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.braincnsrecode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

public class BrainCnsRecodeData {

    private final List<Object> _siteInclusions;
    private final List<Object> _behaviorInclusions;
    private final List<Object> _histologyInclusions;
    private final List<Object> _histologyExclusions;
    private final String _recode;

    public BrainCnsRecodeData(String site, String beh, String histInc, String histExc, String recode) {
        _siteInclusions = parseValue(site);
        _behaviorInclusions = parseValue(beh);
        _histologyInclusions = parseValue(histInc);
        _histologyExclusions = parseValue(histExc);
        _recode = recode;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "RedundantIfStatement", "java:S1126"})
    public boolean matches(String site, String hist, String beh) {

        boolean behMatches = false;
        if (_behaviorInclusions.isEmpty())
            behMatches = true;
        else {
            for (Object obj : _behaviorInclusions) {
                behMatches = obj instanceof Range ? ((Range)obj).contains(beh) : Objects.equals(obj, beh);
                if (behMatches)
                    break;
            }
        }
        if (!behMatches)
            return false;

        boolean siteMatches = false;
        if (_siteInclusions.isEmpty())
            siteMatches = true;
        else {
            for (Object obj : _siteInclusions) {
                siteMatches = obj instanceof Range ? ((Range)obj).contains(site) : Objects.equals(obj, site);
                if (siteMatches)
                    break;
            }
        }
        if (!siteMatches)
            return false;

        boolean histMatches = false;
        if (_histologyInclusions.isEmpty())
            histMatches = true;
        else {
            for (Object obj : _histologyInclusions) {
                histMatches = obj instanceof Range ? ((Range)obj).contains(hist) : Objects.equals(obj, hist);
                if (histMatches)
                    break;
            }
        }
        if (histMatches && !_histologyExclusions.isEmpty()) {
            for (Object obj : _histologyExclusions) {
                if (obj instanceof Range ? ((Range)obj).contains(hist) : Objects.equals(obj, hist)) {
                    histMatches = false;
                    break;
                }
            }
        }
        if (!histMatches)
            return false;

        return true;
    }

    public String getRecode() {
        return _recode;
    }

    private List<Object> parseValue(String value) {
        List<Object> result = new ArrayList<>();
        for (String val : StringUtils.split(value.replace(".", ""), ',')) {
            String[] parts = StringUtils.split(val, '-');
            if (parts.length == 2)
                result.add(Range.between(parts[0], parts[1]));
            else
                result.add(val);
        }
        return result;
    }
}
