/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms.braincnsrecode;

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

public class BrainCnsRecodeUtils {

    public static final String ALG_NAME = "SEER Brain/CNS Recode";

    public static final String ALG_VERSION_2020 = "2020 Revision";

    public static final String UNKNOWN_2020 = "99";

    private static List<BrainCnsRecodeData> _DATA_2020;

    public static String computeBrainCsnRecode(String version, String site, String histology, String behavior) {

        String unknownValue;
        if (ALG_VERSION_2020.equals(version))
            unknownValue = UNKNOWN_2020;
        else
            throw new RuntimeException("Invalid version: " + version);

        if (StringUtils.isBlank(site) || StringUtils.isBlank(histology) || StringUtils.isBlank(behavior))
            return unknownValue;

        if (!isDataInitialized())
            initializeData();

        for (BrainCnsRecodeData row : _DATA_2020)
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

        _DATA_2020 = readData("brain-cnsrecode-2020revision.csv");
    }

    @SuppressWarnings("SameParameterValue")
    private static List<BrainCnsRecodeData> readData(String filename) {
        List<BrainCnsRecodeData> result = new ArrayList<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("braincnsrecode/" + filename)) {
            if (is == null)
                throw new RuntimeException("Unable to find " + filename);

            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                for (String[] row : new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder().build()).withSkipLines(1).build().readAll()) {
                    String site = StringUtils.trim(row[1]).replace(" ", "");
                    String beh = StringUtils.trim(row[2]).replace(" ", "");
                    String histInc = StringUtils.trim(row[3]).replace(" ", "");
                    String histExc = StringUtils.trim(row[4]).replace(" ", "");
                    String recode = StringUtils.trimToNull(row[5]);
                    if (recode != null && !recode.contains("-"))
                        result.add(new BrainCnsRecodeData(site, beh, histInc, histExc, StringUtils.leftPad(recode, 2, "0")));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException("Unable to read " + filename, e);
        }
        return result;
    }
}
