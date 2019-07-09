/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.countyatdiagnosisanalysis;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReaderBuilder;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;

public class CountyAtDxAnalysisUtils {

    public static final String ALG_NAME = "County at Diagnosis Analysis";
    public static final String ALG_VERSION = "1.0";

    // special codes
    public static final String INVALID_COUNTY_CODE = "999";
    public static final String CANADIAN_COUNTY_CODE = "998";

    // flag values
    public static final String REP_REP_GEO_EQUAL = "2";
    public static final String REP_GEO_BLANK_OR_UNK = "2.1";
    public static final String REP_GEO_INVALID_FOR_STATE = "2.2";
    public static final String GEO_CERT_KNOWN_REP_UNK = "3";
    public static final String GEO_CERT_1_OR_6 = "4";
    public static final String REP_CERT_5 = "5";
    public static final String REP_CERT_UNK = "6";
    public static final String GEO_CERT_UNK_REP_UNK = "6.1";
    public static final String REP_CERT_2_3_OR_4 = "7";
    public static final String OTHER_CANADIAN_STATE = "9";
    public static final String OTHER_STATE_OR_DX_YEAR_BLANK = "10";
    public static final String OTHER_REP_AND_GEO_BLANK = "10.1";

    private static final List<String> _CANADIAN_STATE_ABBREVIATIONS = Arrays.asList("AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT");

    public static CountyAtDxAnalysisOutputDto computeCountyAtDiagnosis(CountyAtDxAnalysisInputDto input) {
        CountyAtDxAnalysisOutputDto output = new CountyAtDxAnalysisOutputDto();

        String dateOfDiagnosis = input.getDateOfDiagnosis();
        Integer diagnosisYear;

        if (dateOfDiagnosis == null || isBlankOrNineFilled(dateOfDiagnosis) || dateOfDiagnosis.length() < 4)
            diagnosisYear = null;
        else {
            try {
                diagnosisYear = Integer.valueOf(dateOfDiagnosis.substring(0, 4));
            }
            catch (NumberFormatException e) {
                diagnosisYear = null;
            }
        }

        if (diagnosisYear == null || StringUtils.isBlank(input.getAddrAtDxState())) {
            output.setCountyAtDxAnalysis(INVALID_COUNTY_CODE);
            output.setCountyAtDxAnalysisFlag(OTHER_STATE_OR_DX_YEAR_BLANK);
        }
        else if (_CANADIAN_STATE_ABBREVIATIONS.contains(input.getAddrAtDxState())) {
            output.setCountyAtDxAnalysis(CANADIAN_COUNTY_CODE);
            output.setCountyAtDxAnalysisFlag(OTHER_CANADIAN_STATE);
        }
        else {
            String geocoderCountyAtDx;
            String geocoderCertainty;

            if (diagnosisYear < 2000) {
                geocoderCountyAtDx = input.getCountyAtDxGeocode1990();
                geocoderCertainty = input.getCensusTrCert19708090();
            }
            else if (diagnosisYear < 2010) {
                geocoderCountyAtDx = input.getCountyAtDxGeocode2000();
                geocoderCertainty = input.getCensusTrCertainty2000();
            }
            else {
                geocoderCountyAtDx = input.getCountyAtDxGeocode2010();
                geocoderCertainty = input.getCensusTrCertainty2010();
            }

            if (StringUtils.isBlank(geocoderCountyAtDx) && StringUtils.isBlank(input.getCountyAtDx())) {
                output.setCountyAtDxAnalysis(INVALID_COUNTY_CODE);
                output.setCountyAtDxAnalysisFlag(OTHER_REP_AND_GEO_BLANK);
            }
            else if (isBlankOrNineFilled(geocoderCountyAtDx)) {
                output.setCountyAtDxAnalysis(input.getCountyAtDx());
                output.setCountyAtDxAnalysisFlag(REP_GEO_BLANK_OR_UNK);
            }
            else if (geocoderCountyAtDx.equals(input.getCountyAtDx())) {
                output.setCountyAtDxAnalysis(input.getCountyAtDx());
                output.setCountyAtDxAnalysisFlag(REP_REP_GEO_EQUAL);
            }
            else {
                if (!CountryData.getInstance().isCountyAtDxAnalysisInitialized())
                    CountryData.getInstance().initializeCountyAtDxAnalysisData(loadCountyAtDxAnalysisData());

                if (CountryData.getInstance().getCountyAtDxAnalysisData(input.getAddrAtDxState()) == null ||
                        CountryData.getInstance().getCountyAtDxAnalysisData(input.getAddrAtDxState()).getCountyData(geocoderCountyAtDx) == null) {
                    output.setCountyAtDxAnalysis(input.getCountyAtDx());
                    output.setCountyAtDxAnalysisFlag(REP_GEO_INVALID_FOR_STATE);
                }
                else if (!isBlankOrNineFilled(geocoderCertainty) && isBlankOrNineFilled(input.getCountyAtDx())) {
                    output.setCountyAtDxAnalysis(geocoderCountyAtDx);
                    output.setCountyAtDxAnalysisFlag(GEO_CERT_KNOWN_REP_UNK);
                }
                else if (Arrays.asList("1", "6").contains(geocoderCertainty)) {
                    output.setCountyAtDxAnalysis(geocoderCountyAtDx);
                    output.setCountyAtDxAnalysisFlag(GEO_CERT_1_OR_6);
                }
                else if (Arrays.asList("2", "3", "4").contains(geocoderCertainty)) {
                    output.setCountyAtDxAnalysis(input.getCountyAtDx());
                    output.setCountyAtDxAnalysisFlag(REP_CERT_2_3_OR_4);
                }
                else if ("5".equals(geocoderCertainty)) {
                    output.setCountyAtDxAnalysis(input.getCountyAtDx());
                    output.setCountyAtDxAnalysisFlag(REP_CERT_5);
                }
                else if (isBlankOrNineFilled(input.getCountyAtDx())) {
                    output.setCountyAtDxAnalysis(geocoderCountyAtDx);
                    output.setCountyAtDxAnalysisFlag(GEO_CERT_UNK_REP_UNK);
                }
                else {
                    output.setCountyAtDxAnalysis(input.getCountyAtDx());
                    output.setCountyAtDxAnalysisFlag(REP_CERT_UNK);
                }
            }
        }

        return output;
    }

    static boolean isBlankOrNineFilled(String str) {
        if (StringUtils.isBlank(str))
            return true;

        boolean allNines = true;
        for (Character c : str.toCharArray())
            allNines &= c.equals('9');

        return allNines;
    }

    @SuppressWarnings("ConstantConditions")
    private static Map<String, Map<String, CountyData>> loadCountyAtDxAnalysisData() {
        Map<String, java.util.Map<String, CountyData>> result = new HashMap<>();
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("countyatdxanalysis/state-county-map.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll())
                result.computeIfAbsent(row[0], k -> new HashMap<>()).computeIfAbsent(row[1], k -> new CountyData());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
