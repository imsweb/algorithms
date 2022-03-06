/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.lymphoma;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class LymphoidNeoplasmRecodeUtils {

    public static final String ALG_NAME = "SEER Lymphoid Neoplasm Recode";

    public static final String ALG_VERSION_2021 = "2021 Revision";

    public static final String UNKNOWN = "99";

    private static List<LymphoidNeoplasmRecodeData> _DATA_2021;

    /**
     * Returns the coded Lymphoid Neoplasm from the provided input fields.
     * @param version the algorithm version (see constants)
     * @param site primary site
     * @param histology histology ICD-O-3
     * @return coded Lymphoid Neoplasm, possibly the unknown value, but never null
     */
    public static String calculateSiteRecode(String version, String site, String histology) {
        if (!ALG_VERSION_2021.equals(version))
            throw new RuntimeException("Invalid version: " + version);

        if (StringUtils.isBlank(site) || StringUtils.isBlank(histology))
            return UNKNOWN;

        String unformattedSite = site.startsWith("C") ? site.substring(1) : site;
        if (!NumberUtils.isDigits(unformattedSite))
            return UNKNOWN;

        if (!isDataInitialized())
            initializeData();

        for (LymphoidNeoplasmRecodeData row : _DATA_2021)
            if (row.matches(site, histology))
                return row.getRecode();

        return UNKNOWN;

    }

    private static boolean isDataInitialized() {
        return _DATA_2021 != null;
    }

    private static synchronized void initializeData() {
        if (_DATA_2021 != null)
            return;

        _DATA_2021 = readData("lymphoma-2021revision.csv");
    }

    @SuppressWarnings("SameParameterValue")
    private static List<LymphoidNeoplasmRecodeData> readData(String filename) {
        List<LymphoidNeoplasmRecodeData> result = new ArrayList<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("lymphoma/" + filename)) {
            if (is == null)
                throw new RuntimeException("Unable to find " + filename);

            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                for (String[] row : new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder().withSeparator(',').build()).withSkipLines(1).build().readAll()) {
                    String site = StringUtils.trimToNull(row[1]);
                    String hist = StringUtils.trimToNull(row[2]);
                    String recode = StringUtils.trimToNull(row[3]);
                    if (site != null && hist != null && recode != null && !recode.contains("-"))
                        result.add(new LymphoidNeoplasmRecodeData(site, hist, StringUtils.leftPad(recode, 2, "0")));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException("Unable to read " + filename, e);
        }
        return result;
    }
}
