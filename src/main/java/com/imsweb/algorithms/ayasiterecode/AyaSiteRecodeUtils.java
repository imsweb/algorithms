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
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public final class AyaSiteRecodeUtils {

    public static final String ALG_NAME = "SEER Adolescents and Young Adults (AYA) Site Recode";

    public static final String ALG_VERSION_2008 = "WHO 2008";
    public static final String ALG_VERSION_2020 = "2020 Revision";

    public static final String AYA_SITE_RECODE_UNKNOWN_2008 = "99";
    public static final String AYA_SITE_RECODE_UNKNOWN_2020 = "999";

    private static List<AyaSiteRecodeData> _DATA_2008;
    private static List<AyaSiteRecodeData> _DATA_2020;

    private AyaSiteRecodeUtils() {
        // no instances of this class allowed!
    }

    /**
     * Returns the coded AYA Site Recode from the provided input fields.
     * @param version the algorithm version (see constants)
     * @param site primary site
     * @param histology histology ICD-O-3
     * @param behavior beahvior ICD-O-3
     * @return coded AYA Site Recode, possibly the unknown value, but never null
     */
    public static String calculateSiteRecode(String version, String site, String histology, String behavior) {

        String unknownValue;
        if (ALG_VERSION_2008.equals(version))
            unknownValue = AYA_SITE_RECODE_UNKNOWN_2008;
        else if (ALG_VERSION_2020.equals(version))
            unknownValue = AYA_SITE_RECODE_UNKNOWN_2020;
        else
            throw new IllegalStateException("Invalid version: " + version);

        if (StringUtils.isBlank(site) || StringUtils.isBlank(histology) || StringUtils.isBlank(behavior))
            return unknownValue;

        if (!isDataInitialized())
            initializeData();

        List<AyaSiteRecodeData> data;
        if (ALG_VERSION_2008.equals(version))
            data = _DATA_2008;
        else
            data = _DATA_2020;

        for (AyaSiteRecodeData row : data)
            if (row.matches(site, histology, behavior))
                return row.getRecode();

        return unknownValue;

    }

    private static boolean isDataInitialized() {
        return _DATA_2020 != null;
    }

    private static synchronized void initializeData() {
        if (_DATA_2020 != null)
            return;

        _DATA_2008 = readData("ayarecodewho2008.txt");
        _DATA_2020 = readData("ayarecode-2020revision.csv");
    }

    @SuppressWarnings("SameParameterValue")
    private static List<AyaSiteRecodeData> readData(String filename) {
        List<AyaSiteRecodeData> result = new ArrayList<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ayasiterecode/" + filename)) {
            if (is == null)
                throw new IllegalStateException("Unable to find " + filename);

            boolean txt = filename.endsWith(".txt");

            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder().withSeparator(txt ? ';' : ',').build()).withSkipLines(txt ? 2 : 1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String beh = StringUtils.trimToNull(row[txt ? 1 : 3]);
                    String site = StringUtils.trimToNull(row[2]);
                    String hist = StringUtils.trimToNull(row[txt ? 3 : 1]);
                    String recode = StringUtils.trimToNull(row[4]);
                    if (beh != null && site != null && hist != null && recode != null)
                        result.add(new AyaSiteRecodeData(site, hist, beh, StringUtils.leftPad(recode, txt ? 2 : 3, "0")));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException("Unable to read " + filename, e);
        }
        return result;
    }
}
