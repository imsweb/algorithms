/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

public abstract class HistStageDataDto {

    private static Pattern _RANGE_PATTERN = Pattern.compile("^(\\d+)-(\\d+)$");
    private static Pattern _NUMBER_PATTERN = Pattern.compile("^(\\d+)$");

    protected List<Object> parse(String str) {
        List<Object> result = new ArrayList<>();

        for (String token : StringUtils.split(str, ',')) {
            Matcher matcher = _RANGE_PATTERN.matcher(token);
            if (matcher.matches())
                result.add(Range.between(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2))));
            else {
                matcher = _NUMBER_PATTERN.matcher(token);
                if (matcher.matches())
                    result.add(Integer.valueOf(matcher.group(1)));
                else
                    result.add(token);
            }
        }

        return result;
    }

    protected boolean find(List<Object> list, String value) {
        if (value == null)
            return false;

        Integer intValue = _NUMBER_PATTERN.matcher(value).matches() ? Integer.valueOf(value) : null;
        for (Object obj : list) {
            if (obj instanceof Range) {
                Range range = (Range)obj;
                if (intValue != null && intValue.compareTo((Integer)range.getMinimum()) >= 0 && intValue.compareTo((Integer)range.getMaximum()) <= 0)
                    return true;
            }
            else if (obj instanceof Integer) {
                if (intValue != null && intValue.equals(obj))
                    return true;
            }
            else {
                if (value.equals(obj))
                    return true;
            }
        }

        return false;
    }

}
