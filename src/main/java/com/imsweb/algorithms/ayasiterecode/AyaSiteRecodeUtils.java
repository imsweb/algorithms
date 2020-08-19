/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ayasiterecode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class AyaSiteRecodeUtils {

    public static final String ALG_NAME = "AYA Site Recode/WHO 2008 Definition";
    public static final String ALG_VERSION = "WHO2008";
    public static final String ALG_INFO = "Adolescents and Young Adults (AYA) Site Recode with WHO 2008 Definition.";

    public static String AYA_SITE_RECODE_UNKNOWN = "99";

    private static List<AyaSiteRecodeData> _DATA;

    /**
     * Returns the coded AYA Site Recode from the provided input fields.
     * @param site primary site
     * @param histology histology ICD-O-3
     * @param behavior beahvior ICD-O-3
     * @return coded AYA Site Recode, possibly the unknown value, but never null
     */
    public static String calculateSiteRecode(String site, String histology, String behavior) {
        if (StringUtils.isBlank(site) || StringUtils.isBlank(histology) || StringUtils.isBlank(behavior))
            return AYA_SITE_RECODE_UNKNOWN;

        if (!isDataInitialized())
            initializeData();

        for (AyaSiteRecodeData data : _DATA)
            if (data.matches(site, histology, behavior))
                return data.getRecode();

        return AYA_SITE_RECODE_UNKNOWN;

    }

    private static boolean isDataInitialized() {
        return _DATA != null;
    }

    private static synchronized void initializeData() {
        if (_DATA != null)
            return;
        _DATA = readData("ayarecodewho2008.txt");
    }

    @SuppressWarnings("SameParameterValue")
    private static List<AyaSiteRecodeData> readData(String filename) {
        List<AyaSiteRecodeData> result = new ArrayList<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ayasiterecode/" + filename)) {
            if (is == null)
                throw new RuntimeException("Unable to find " + filename);
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII)) {
                for (String[] row : new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).withSkipLines(2).build().readAll()) {
                    String beh = StringUtils.trimToNull(row[1]);
                    String site = StringUtils.trimToNull(row[2]);
                    String hist = StringUtils.trimToNull(row[3]);
                    String recode = StringUtils.trimToNull(row[4]);
                    if (beh != null && site != null && hist != null && recode != null)
                        result.add(new AyaSiteRecodeData(site, hist, beh, recode));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException("Unable to read " + filename, e);
        }
        return result;
    }
}
