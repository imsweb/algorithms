/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.Algorithms;

public final class Utils {

    private static final Pattern _SITE_PATTERN = Pattern.compile("[A-Z](\\d){0,3}");
    private static final Pattern _HIST_PATTERN = Pattern.compile("\\d{4}");
    private static final Pattern _HIST_RANGE_PATTERN = Pattern.compile("\\d{4}-\\d{4}");
    private static final Pattern _BEH_PATTERN = Pattern.compile("\\d");
    private static final Pattern _BEH_RANGE_PATTERN = Pattern.compile("\\d-\\d");

    private Utils() {
        // no instances of this class allowed!
    }

    public static void processInternalFile(String file, Consumer<NamedCsvRecord> consumer) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
            if (is == null)
                throw new IllegalStateException("Unable to find " + file);
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8); CsvReader<NamedCsvRecord> csvReader = CsvReader.builder().ofNamedCsvRecord(reader)) {
                csvReader.stream().forEach(consumer);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to read " + file, e);
        }
    }

    public static void processInternalFileNoHeaders(String file, Consumer<CsvRecord> consumer) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
            if (is == null)
                throw new IllegalStateException("Unable to find " + file);
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8); CsvReader<CsvRecord> csvReader = CsvReader.builder().ofCsvRecord(reader)) {
                csvReader.stream().forEach(consumer);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to read " + file, e);
        }
    }

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
                result.add(Range.of(Integer.valueOf(parts[0].substring(1)), Integer.valueOf(parts[1].substring(1))));
            }
        }
        return result;
    }

    /**
     * Expands the provided histology codes as either individual integer codes, or as ranges.
     * @param toExpand sites to expand
     * @return expanded sites
     */
    public static List<Object> expandHistologiesAsIntegers(String toExpand) {
        if (StringUtils.isBlank(toExpand))
            return null;
        List<Object> result = new ArrayList<>();
        for (String s : StringUtils.split(toExpand, ',')) {
            if (!s.contains("-")) {
                if (!_HIST_PATTERN.matcher(s).matches())
                    throw new IllegalStateException("Invalid histology code: " + s);
                result.add(Integer.valueOf(s));
            }
            else {
                if (!_HIST_RANGE_PATTERN.matcher(s).matches())
                    throw new IllegalStateException("Invalid histology range: " + s);
                String[] parts = StringUtils.split(s, '-');
                result.add(Range.of(Integer.valueOf(parts[0]), Integer.valueOf(parts[1])));
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
        if (StringUtils.isBlank(toExpand))
            return null;
        List<Object> result = new ArrayList<>();
        for (String s : StringUtils.split(toExpand, ',')) {
            if (!s.contains("-")) {
                if (!_BEH_PATTERN.matcher(s).matches())
                    throw new IllegalStateException("Invalid behavior code: " + s);
                result.add(Integer.valueOf(s));
            }
            else {
                if (!_BEH_RANGE_PATTERN.matcher(s).matches())
                    throw new IllegalStateException("Invalid behavior range: " + s);
                String[] parts = StringUtils.split(s, '-');
                result.add(Range.of(Integer.valueOf(parts[0]), Integer.valueOf(parts[1])));
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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

    public static Map<String, Object> extractPatient(AlgorithmInput input) {
        return input.getPatient() == null ? Collections.emptyMap() : input.getPatient();
    }

    public static List<Map<String, Object>> extractTumors(Map<String, Object> patient) {
        return extractTumors(patient, false);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> extractTumors(Map<String, Object> patient, boolean createTumorIfEmpty) {
        List<Map<String, Object>> tumors = (List<Map<String, Object>>)patient.get(Algorithms.FIELD_TUMORS);
        if (tumors == null)
            tumors = new ArrayList<>();
        if (tumors.isEmpty() && createTumorIfEmpty)
            tumors.add(new HashMap<>());
        return tumors;
    }

    public static String extractYear(String fullDate) {
        if (fullDate == null || fullDate.length() < 4)
            return null;

        return fullDate.substring(0, 4);
    }

    public static String extractMonth(String fullDate) {
        if (fullDate == null || fullDate.length() < 6)
            return null;

        return fullDate.substring(4, 6);
    }

    public static String extractDay(String fullDate) {
        if (fullDate == null || fullDate.length() < 8)
            return null;

        return fullDate.substring(6, 8);
    }

    public static String combineDate(String year, String month, String day) {
        String newValue = null;

        if (!StringUtils.isBlank(year)) {
            year = StringUtils.leftPad(year, 4, "0");
            if (!StringUtils.isBlank(month)) {
                month = StringUtils.leftPad(month, 2, "0");
                if (!StringUtils.isBlank(day)) {
                    day = StringUtils.leftPad(day, 2, "0");
                    newValue = year + month + day;
                }
                else
                    newValue = year + month;
            }
            else
                newValue = year;
        }

        return newValue;
    }

}
