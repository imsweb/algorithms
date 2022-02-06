/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.lymphoma;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

public class LymphoidNeoplasmRecodeData {

    private final List<Object> _siteInclusions;
    private final List<Object> _histologyInclusions;
    private final String _recode;

    public LymphoidNeoplasmRecodeData(String site, String hist, String recode) {
        _siteInclusions = parseValue(site, true);
        _histologyInclusions = parseValue(hist, false);
        _recode = recode;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "RedundantIfStatement"})
    public boolean matches(String site, String hist) {

        boolean siteMatches = false;
        for (Object obj : _siteInclusions) {
            siteMatches = obj instanceof Range ? ((Range)obj).contains(site) : Objects.equals(obj, site);
            if (siteMatches)
                break;
        }
        if (!siteMatches)
            return false;

        boolean histMatches = false;
        for (Object obj : _histologyInclusions) {
            histMatches = obj instanceof Range ? ((Range)obj).contains(hist) : Objects.equals(obj, hist);
            if (histMatches)
                break;
        }
        if (!histMatches)
            return false;

        return true;
    }

    public String getRecode() {
        return _recode;
    }

    private List<Object> parseValue(String value, boolean isSite) {
        List<Object> result = new ArrayList<>();
        for (String val : StringUtils.split(value, ',')) {
            String[] parts = StringUtils.split(val, '-');
            if (parts.length == 2) {
                String low = isSite && !parts[0].startsWith("C") ? ("C" + parts[0]) : parts[0];
                String high = isSite && !parts[1].startsWith("C") ? ("C" + parts[1]) : parts[1];
                result.add(Range.between(low, high));
            }
            else
                result.add(isSite && !val.startsWith("C") ? ("C" + val) : val);
        }
        return result;
    }
}
