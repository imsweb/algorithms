/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.lymphoma;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.internal.Utils;

public final class LymphoidNeoplasmRecodeUtils {

    public static final String ALG_NAME = "SEER Lymphoid Neoplasm Recode";

    public static final String ALG_VERSION_2021 = "2021 Revision";

    public static final String UNKNOWN = "99";

    private static List<LymphoidNeoplasmRecodeData> _DATA_2021;

    private LymphoidNeoplasmRecodeUtils() {
        // no instances of this class allowed!
    }

    /**
     * Returns the coded Lymphoid Neoplasm from the provided input fields.
     * @param version the algorithm version (see constants)
     * @param site primary site
     * @param histology histology ICD-O-3
     * @return coded Lymphoid Neoplasm, possibly the unknown value, but never null
     */
    public static String calculateSiteRecode(String version, String site, String histology) {
        if (!ALG_VERSION_2021.equals(version))
            throw new IllegalStateException("Invalid version: " + version);

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

        Utils.processInternalFile("lymphoma/" + filename, line -> {
            String site = StringUtils.trimToNull(line.getField(1));
            String hist = StringUtils.trimToNull(line.getField(2));
            String recode = StringUtils.trimToNull(line.getField(3));
            if (site != null && hist != null && recode != null && !recode.contains("-"))
                result.add(new LymphoidNeoplasmRecodeData(site, hist, StringUtils.leftPad(recode, 2, "0")));
        });

        return result;
    }
}
