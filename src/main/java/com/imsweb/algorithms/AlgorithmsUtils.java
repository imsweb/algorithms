/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public final class AlgorithmsUtils {

    private static final Pattern _SITE_PATTERN = Pattern.compile("[A-Z](\\d){0,3}");

    /**
     * Expands the provided string of sites into a list of sites, individual elements or ranges should be comma separated.
     * <p/>
     * Supports ranges (C120-C129) and optional last digits (C12 means C120-C129; C1 means C100-C199).
     * <p/>
     * Method is not case sensitive (c123 will be translated into C123).
     * <p/>
     * Created on Dec 19, 2011 by depryf
     * @param sites string of sites
     * @return the list of expanded sites, null if no site has been provided
     */
    public static List<String> expandSites(String sites) {
        if (sites == null || sites.trim().isEmpty())
            return null;

        List<String> result = new ArrayList<>();

        for (String elem : StringUtils.split(StringUtils.replace(sites, " ", "").toUpperCase(), ',')) {
            if (elem.isEmpty())
                continue;

            // range
            if (elem.contains("-")) {
                String[] parts = StringUtils.split(elem, '-');
                String left = parts[0];
                if (NumberUtils.isDigits(left))
                    left = "C" + elem;
                String right = parts[1];
                if (NumberUtils.isDigits(right))
                    right = "C" + elem;

                if (_SITE_PATTERN.matcher(left).matches() && _SITE_PATTERN.matcher(right).matches()) {
                    String leftPrefix = elem.substring(0, 1);
                    String rightPrefix = elem.substring(0, 1);
                    if (leftPrefix.equals(rightPrefix)) {
                        String leftIndex = left.substring(1);
                        String rightIndex = right.substring(1);

                        int start = Integer.parseInt(StringUtils.rightPad(leftIndex, 3, "0"));
                        int end = Integer.parseInt(StringUtils.rightPad(rightIndex, 3, "9"));
                        for (int i = start; i <= end; i++)
                            result.add(leftPrefix + StringUtils.leftPad(String.valueOf(i), 3, "0"));
                    }
                }
            }
            else {
                if (NumberUtils.isDigits(elem))
                    elem = "C" + elem;
                if (_SITE_PATTERN.matcher(elem).matches()) {
                    String prefix = elem.substring(0, 1);
                    String index = elem.substring(1);

                    int start = Integer.parseInt(StringUtils.rightPad(index, 3, "0"));
                    int end = Integer.parseInt(StringUtils.rightPad(index, 3, "9"));
                    for (int i = start; i <= end; i++)
                        result.add(prefix + StringUtils.leftPad(String.valueOf(i), 3, "0"));
                }
            }
        }

        return result;
    }

    /**
     * Expands the provided primary sites as either individual integer codes, or as ranges.
     * @param toExpand sites to expand
     * @return expanded sites
     */
    public static List<Object> expandSitesAsIntegers(String toExpand) {
        if (StringUtils.isBlank(toExpand))
            return null;
        List<Object> result = new ArrayList<>();
        for (String s : StringUtils.split(toExpand, ',')) {
            if (!s.contains("-"))
                result.add(Integer.valueOf(s.substring(1)));
            else {
                String[] parts = StringUtils.split(s, '-');
                result.add(Range.between(Integer.valueOf(parts[0].substring(1)), Integer.valueOf(parts[1].substring(1))));
            }
        }
        return result;
    }

    /**
     * Expands the provided histologies as either individual integer codes, or as ranges.
     * @param toExpand sites to expand
     * @return expanded sites
     */
    public static List<Object> expandHistologiesAsIntegers(String toExpand) {
        if (StringUtils.isBlank(toExpand))
            return null;
        List<Object> result = new ArrayList<>();
        for (String s : StringUtils.split(toExpand, ',')) {
            if (!s.contains("-"))
                result.add(Integer.valueOf(s));
            else {
                String[] parts = StringUtils.split(s, '-');
                result.add(Range.between(Integer.valueOf(parts[0]), Integer.valueOf(parts[1])));
            }
        }
        return result;
    }

    /**
     * Expands the provided behaviors as either individual integer codes, or as ranges.
     * @param toExpand behaviors to expand
     * @return expanded behaviors
     */
    public static List<Object> expandBehaviorsAsIntegers(String toExpand) {
        return expandHistologiesAsIntegers(toExpand);
    }

    @SuppressWarnings("unchecked")
    public static boolean isContained(List<?> list, Integer value) {
        if (list == null)
            return false;
        for (Object obj : list)
            if ((obj instanceof Range && ((Range)obj).contains(value)) || (obj.equals(value)))
                return true;
        return false;
    }

    public static boolean isHistologyContained(String list, Integer value) {
        return isContained(expandHistologiesAsIntegers(list), value);
    }
}
