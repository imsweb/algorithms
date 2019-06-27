/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.countyatdiagnosisanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountyAtDiagnosisAnalysisUtils {

    public static final String ALG_NAME = "County at Diagnosis Analysis";
    public static final String ALG_VERSION = "1.0";
    public static final String ALG_INFO = "County at Diagnosis Analysis 1.0, released in TBD";

    public static final String INVALID_COUNTY_CODE = "999";
    public static final String CANADIAN_COUNTY_CODE = "998";
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

    private static final List<String> _VALID_COUNTY_CODES = new ArrayList<>();
    static {
        try {
            File countyCodesFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\countyatdxanalysis\\county-codes.txt");
            BufferedReader reader = new BufferedReader(new FileReader(countyCodesFile));
            reader.lines().forEach(_VALID_COUNTY_CODES::add);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final Map<String, String> _STATE_CODES = new HashMap<>();
    static {
        try {
            File countyCodesFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\countyatdxanalysis\\state-code-mapping.txt");
            BufferedReader reader = new BufferedReader(new FileReader(countyCodesFile));
            reader.lines().map(line -> line.split("\t")).forEach(entry -> _STATE_CODES.put(entry[0], entry[1]));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static CountyAtDiagnosisAnalysisOutputDto computeCountyAtDiagnosis(CountyAtDiagnosisAnalysisInputDto input) {
        CountyAtDiagnosisAnalysisOutputDto output = new CountyAtDiagnosisAnalysisOutputDto();

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

        if (diagnosisYear == null || isBlank(input.getAddrAtDxState())) {
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

            if (isBlank(geocoderCountyAtDx) && isBlank(input.getCountyAtDx())) {
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
            else if (!_VALID_COUNTY_CODES.contains(_STATE_CODES.getOrDefault(input.getAddrAtDxState(), "00") + geocoderCountyAtDx)) {
                output.setCountyAtDxAnalysis(input.getCountyAtDx());
                output.setCountyAtDxAnalysisFlag(REP_GEO_INVALID_FOR_STATE);
            }
            else if (!isBlankOrNineFilled(geocoderCertainty) && isBlankOrNineFilled(input.getCountyAtDx())) {
                output.setCountyAtDxAnalysis(geocoderCountyAtDx);
                output.setCountyAtDxAnalysisFlag(GEO_CERT_KNOWN_REP_UNK);
            }
            else {
                if (Arrays.asList("1", "6").contains(geocoderCertainty)) {
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
                else if (isBlankOrNineFilled(geocoderCertainty)) {
                    if (isBlankOrNineFilled(input.getCountyAtDx())) {
                        output.setCountyAtDxAnalysis(geocoderCountyAtDx);
                        output.setCountyAtDxAnalysisFlag(GEO_CERT_UNK_REP_UNK);
                    }
                    else {
                        output.setCountyAtDxAnalysis(input.getCountyAtDx());
                        output.setCountyAtDxAnalysisFlag(REP_CERT_UNK);
                    }
                }
            }
        }

        return output;
    }

    private static boolean isBlank(String str) {
        if (str == null || str.isEmpty())
            return true;

        boolean allSpaces = true;
        for (Character c : str.toCharArray())
            allSpaces &= c.equals(' ');

        return allSpaces;
    }

    private static boolean isBlankOrNineFilled(String str) {
        if (isBlank(str))
            return true;

        boolean allNines = true;
        for (Character c : str.toCharArray())
            allNines &= c.equals('9');

        return allNines;
    }
}
