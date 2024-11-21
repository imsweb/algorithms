/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms.braincnsrecode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

public final class BrainCnsRecodeUtils {

    public static final String ALG_NAME = "SEER Brain/CNS Recode";

    public static final String ALG_VERSION_2020 = "2020 Revision";

    public static final String UNKNOWN_2020 = "99";

    private static List<BrainCnsRecodeData> _DATA_2020;

    private BrainCnsRecodeUtils() {
        // no instances of this class allowed!
    }

    public static String computeBrainCsnRecode(String version, String site, String histology, String behavior) {

        String unknownValue;
        if (ALG_VERSION_2020.equals(version))
            unknownValue = UNKNOWN_2020;
        else
            throw new IllegalStateException("Invalid version: " + version);

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
                throw new IllegalStateException("Unable to find " + filename);

            File csvFile = new File("src/main/resources/braincnsrecode/" + filename);
            try (CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(csvFile.toPath())) {
                reader.stream().forEach(line -> {
                    String site = StringUtils.trim(line.getField(1)).replace(" ", "");
                    String beh = StringUtils.trim(line.getField(2)).replace(" ", "");
                    String histInc = StringUtils.trim(line.getField(3)).replace(" ", "");
                    String histExc = StringUtils.trim(line.getField(4)).replace(" ", "");
                    String recode = StringUtils.trimToNull(line.getField(5));
                    if (recode != null && !recode.contains("-"))
                        result.add(new BrainCnsRecodeData(site, beh, histInc, histExc, StringUtils.leftPad(recode, 2, "0")));
                });
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to read " + filename, e);
        }
        return result;
    }
}
