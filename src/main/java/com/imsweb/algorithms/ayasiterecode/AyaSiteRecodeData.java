/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ayasiterecode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

public class AyaSiteRecodeData {

    private final List<Object> _siteInclusions;
    private final List<Object> _histologyInclusions;
    private final List<Object> _behaviorInclusions;
    private final String _recode;

    public AyaSiteRecodeData(String site, String hist, String beh, String recode) {
        _siteInclusions = parseValue(site);
        _histologyInclusions = parseValue(hist);
        _behaviorInclusions = parseValue(beh);
        _recode = recode;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "RedundantIfStatement", "java:S1126"})
    public boolean matches(String site, String hist, String beh) {

        boolean behMatches = false;
        for (Object obj : _behaviorInclusions) {
            behMatches = obj instanceof Range ? ((Range)obj).contains(beh) : Objects.equals(obj, beh);
            if (behMatches)
                break;
        }
        if (!behMatches)
            return false;

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
