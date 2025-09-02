/*
 * Copyright (C) 2025 Information Management Services, Inc.
 */
package com.imsweb.algorithms.breastcategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_BREAST_SUBTYPE;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_ESTROGEN_RECEPTOR_SUM_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_HER2_OVERALL_SUM_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_PROGESTERONE_RECEPTOR_SUM_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_ESTROGEN_RECEPTOR_SUMMARY;
import static com.imsweb.algorithms.Algorithms.FIELD_HER2_OVERALL_SUMMARY;
import static com.imsweb.algorithms.Algorithms.FIELD_PROGESTERONE_RECEPTOR_SUMMARY;
import static com.imsweb.algorithms.Algorithms.FIELD_SSF_1;
import static com.imsweb.algorithms.Algorithms.FIELD_SSF_11;
import static com.imsweb.algorithms.Algorithms.FIELD_SSF_13;
import static com.imsweb.algorithms.Algorithms.FIELD_SSF_14;
import static com.imsweb.algorithms.Algorithms.FIELD_SSF_15;
import static com.imsweb.algorithms.Algorithms.FIELD_SSF_2;
import static com.imsweb.algorithms.Algorithms.FIELD_SSF_9;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMOR_MARKER_1;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMOR_MARKER_2;

public class BreastCategoryAlgorithm extends AbstractAlgorithm {

    public static final String BREAST_SUBTYPE_HR_POS_HER2_POS = "1";
    public static final String BREAST_SUBTYPE_HR_NEG_HER2_POS = "2";
    public static final String BREAST_SUBTYPE_HR_POS_HER2_NEG = "3";
    public static final String BREAST_SUBTYPE_HR_NEG_HER2_NEG = "4";
    public static final String BREAST_SUBTYPE_UNKNOWN = "5";
    public static final String BREAST_SUBTYPE_NOT_CODED = "9";

    public static final String ER_PR_HER2_POSITIVE = "1";
    public static final String ER_PR_HER2_NEGATIVE = "2";
    // original logic used to support value 3 for BORDERLINE cases, but that's not supported anymore...
    public static final String ER_PR_HER2_UNKNOWN = "4";
    public static final String ER_PR_HER2_NOT_CODED = "9";

    public BreastCategoryAlgorithm() {
        super(Algorithms.ALG_BREAST_CANCER_CATEGORY, "SEER Breast Cancer Category", "released in September 2025");

        _url = "https://seer.cancer.gov/seerstat/databases/ssf/breast-subtype.html";

        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_TUMOR_MARKER_1));
        _inputFields.add(Algorithms.getField(FIELD_TUMOR_MARKER_2));
        _inputFields.add(Algorithms.getField(FIELD_SSF_1));
        _inputFields.add(Algorithms.getField(FIELD_SSF_2));
        _inputFields.add(Algorithms.getField(FIELD_SSF_9));
        _inputFields.add(Algorithms.getField(FIELD_SSF_11));
        _inputFields.add(Algorithms.getField(FIELD_SSF_13));
        _inputFields.add(Algorithms.getField(FIELD_SSF_14));
        _inputFields.add(Algorithms.getField(FIELD_SSF_15));
        _inputFields.add(Algorithms.getField(FIELD_ESTROGEN_RECEPTOR_SUMMARY));
        _inputFields.add(Algorithms.getField(FIELD_PROGESTERONE_RECEPTOR_SUMMARY));
        _inputFields.add(Algorithms.getField(FIELD_HER2_OVERALL_SUMMARY));

        _outputFields.add(Algorithms.getField(FIELD_ESTROGEN_RECEPTOR_SUM_RECODE));
        _outputFields.add(Algorithms.getField(FIELD_PROGESTERONE_RECEPTOR_SUM_RECODE));
        _outputFields.add(Algorithms.getField(FIELD_HER2_OVERALL_SUM_RECODE));
        _outputFields.add(Algorithms.getField(FIELD_BREAST_SUBTYPE));

        _unknownValues.put(FIELD_ESTROGEN_RECEPTOR_SUM_RECODE, Arrays.asList(ER_PR_HER2_UNKNOWN, ER_PR_HER2_NOT_CODED));
        _unknownValues.put(FIELD_PROGESTERONE_RECEPTOR_SUM_RECODE, Arrays.asList(ER_PR_HER2_UNKNOWN, ER_PR_HER2_NOT_CODED));
        _unknownValues.put(FIELD_HER2_OVERALL_SUM_RECODE, Arrays.asList(ER_PR_HER2_UNKNOWN, ER_PR_HER2_NOT_CODED));
        _unknownValues.put(FIELD_BREAST_SUBTYPE, Arrays.asList(BREAST_SUBTYPE_UNKNOWN, BREAST_SUBTYPE_NOT_CODED));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            Map<String, Object> outputTumor = new HashMap<>();

            String erRecode = ER_PR_HER2_NOT_CODED;
            String prRecode = ER_PR_HER2_NOT_CODED;
            String her2Recode = ER_PR_HER2_NOT_CODED;
            String breastSubtype = BREAST_SUBTYPE_NOT_CODED;

            int dxYear = Utils.extractYearAsInt((String)inputTumor.get(FIELD_DX_DATE), 0);
            String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
            String hist = (String)inputTumor.get(FIELD_HIST_O3);
            if (isBreastCase(site, hist)) {
                String tumorMarker1 = (String)inputTumor.get(FIELD_TUMOR_MARKER_1);
                String tumorMarker2 = (String)inputTumor.get(FIELD_TUMOR_MARKER_2);
                String ssf1 = (String)inputTumor.get(FIELD_SSF_1);
                String ssf2 = (String)inputTumor.get(FIELD_SSF_2);
                String ssf9 = (String)inputTumor.get(FIELD_SSF_9);
                String ssf11 = (String)inputTumor.get(FIELD_SSF_11);
                String ssf13 = (String)inputTumor.get(FIELD_SSF_13);
                String ssf14 = (String)inputTumor.get(FIELD_SSF_14);
                String ssf15 = (String)inputTumor.get(FIELD_SSF_15);
                String erSummary = (String)inputTumor.get(FIELD_ESTROGEN_RECEPTOR_SUMMARY);
                String prSummary = (String)inputTumor.get(FIELD_PROGESTERONE_RECEPTOR_SUMMARY);
                String her2Summary = (String)inputTumor.get(FIELD_HER2_OVERALL_SUMMARY);

                erRecode = computeErPr(dxYear, tumorMarker1, ssf1, erSummary);
                prRecode = computeErPr(dxYear, tumorMarker2, ssf2, prSummary);
                her2Recode = computeHer2(dxYear, ssf9, ssf11, ssf13, ssf14, ssf15, her2Summary);
                breastSubtype = computeBreastSubtype(dxYear, erRecode, prRecode, her2Recode);
            }

            outputTumor.put(FIELD_ESTROGEN_RECEPTOR_SUM_RECODE, erRecode);
            outputTumor.put(FIELD_PROGESTERONE_RECEPTOR_SUM_RECODE, prRecode);
            outputTumor.put(FIELD_HER2_OVERALL_SUM_RECODE, her2Recode);
            outputTumor.put(FIELD_BREAST_SUBTYPE, breastSubtype);

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }

    static boolean isBreastCase(String site, String hist) {
        if (site == null || site.length() != 4 || hist == null || hist.length() != 4)
            return false;

        // C500-C506, C508-C509: 8000-8700, 8982-8983
        boolean cond1 = site.compareTo("C500") >= 0 && site.compareTo("C509") <= 0 && ((hist.compareTo("8000") >= 0 && hist.compareTo("8700") <= 0) || hist.equals("8982") || hist.equals("8983"));

        // C501-C506, C508-C509: 8720-8790
        boolean cond2 = site.compareTo("C501") >= 0 && site.compareTo("C509") <= 0 && (hist.compareTo("8720") >= 0 && hist.compareTo("8790") <= 0);

        return cond1 || cond2;
    }

    static String computeBreastSubtype(int dxYear, String erRecode, String prRecode, String her2Recode) {
        String result;

        if (dxYear < 2010)
            result = ER_PR_HER2_NOT_CODED;
        else if (ER_PR_HER2_POSITIVE.equals(her2Recode)) {
            if (ER_PR_HER2_POSITIVE.equals(erRecode) || ER_PR_HER2_POSITIVE.equals(prRecode))
                result = BREAST_SUBTYPE_HR_POS_HER2_POS;
            else if (ER_PR_HER2_NEGATIVE.equals(erRecode) && ER_PR_HER2_NEGATIVE.equals(prRecode))
                result = BREAST_SUBTYPE_HR_NEG_HER2_POS;
            else
                result = BREAST_SUBTYPE_UNKNOWN;
        }
        else if (ER_PR_HER2_NEGATIVE.equals(her2Recode)) {
            if (ER_PR_HER2_POSITIVE.equals(erRecode) || ER_PR_HER2_POSITIVE.equals(prRecode))
                result = BREAST_SUBTYPE_HR_POS_HER2_NEG;
            else if (ER_PR_HER2_NEGATIVE.equals(erRecode) && ER_PR_HER2_NEGATIVE.equals(prRecode))
                result = BREAST_SUBTYPE_HR_NEG_HER2_NEG;
            else
                result = BREAST_SUBTYPE_UNKNOWN;

        }
        else
            result = BREAST_SUBTYPE_UNKNOWN;

        return result;
    }

    static String computeErPr(int dxYear, String tumorMarker, String ssf, String ssdi) {
        String result;

        if (dxYear < 1990)
            result = ER_PR_HER2_NOT_CODED;
        else if (dxYear < 2004)
            result = computeErPrHer2ByTumorMarker(tumorMarker);
        else if (dxYear < 2018)
            result = computeErPrHer2BySsf(ssf);
        else
            result = computeErPrHer2BySsdi(ssdi);

        return result;
    }

    static String computeHer2(int dxYear, String ssf9, String ssf11, String ssf13, String ssf14, String ssf15, String ssdi) {
        String result;

        if (dxYear < 2010)
            result = ER_PR_HER2_NOT_CODED;
        else if (dxYear < 2018) {
            if (ssf15 == null || ssf15.isEmpty() || "988".equals(ssf15)) {
                if ("010".equals(ssf11) || "010".equals(ssf13) || "010".equals(ssf14))
                    result = ER_PR_HER2_POSITIVE;
                else if ("020".equals(ssf11) || "020".equals(ssf13) || "020".equals(ssf14))
                    result = ER_PR_HER2_NEGATIVE;
                else
                    result = computeErPrHer2BySsf(ssf9);
            }
            else
                result = computeErPrHer2BySsf(ssf15);
        }
        else
            result = computeErPrHer2BySsdi(ssdi);

        return result;
    }

    private static String computeErPrHer2ByTumorMarker(String tumorMarker) {
        String result;

        if (tumorMarker == null)
            result = ER_PR_HER2_UNKNOWN;
        else {
            switch (tumorMarker) {
                case "1":
                    result = ER_PR_HER2_POSITIVE;
                    break;
                case "2":
                    result = ER_PR_HER2_NEGATIVE;
                    break;
                default:
                    result = ER_PR_HER2_UNKNOWN;
            }
        }

        return result;
    }

    private static String computeErPrHer2BySsf(String ssf) {
        String result;

        if (ssf == null)
            result = ER_PR_HER2_UNKNOWN;
        else {
            switch (ssf) {
                case "010":
                    result = ER_PR_HER2_POSITIVE;
                    break;
                case "020":
                    result = ER_PR_HER2_NEGATIVE;
                    break;
                default:
                    result = ER_PR_HER2_UNKNOWN;
            }
        }

        return result;
    }

    private static String computeErPrHer2BySsdi(String ssdi) {
        String result;

        if (ssdi == null)
            result = ER_PR_HER2_UNKNOWN;
        else {
            switch (ssdi) {
                case "1":
                    result = ER_PR_HER2_POSITIVE;
                    break;
                case "0":
                    result = ER_PR_HER2_NEGATIVE;
                    break;
                default:
                    result = ER_PR_HER2_UNKNOWN;
            }
        }

        return result;
    }
}
