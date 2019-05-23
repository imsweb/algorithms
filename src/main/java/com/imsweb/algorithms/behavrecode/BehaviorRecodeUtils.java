/*
 * Copyright (C) 2016 Information Management Services, Inc.
 */
package com.imsweb.algorithms.behavrecode;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class is used to calculate SEER Behavior Recode For Analysis.
 * Created on March 01, 2016
 * @author Sewbesew Bekele
 */
public class BehaviorRecodeUtils {

    //Algorithm info
    public static final String ALG_NAME = "SEER Behavior Recode for Analysis - 1973-2004 SEER Research Data (November 2006 submission) and Later Releases";
    public static final String ALG_VERSION = "2006+";
    public static final String ALG_INFO = "Calculates SEER behavior recode for November 2006 submission and later releases.";

    //Behavior recode for analysis values
    public static final String BENIGN = "0";
    public static final String BORDER_LINE = "1";
    public static final String INSITU = "2";
    public static final String MALIGNANT = "3";
    public static final String ONLY_MALIGNANT_IN_ICDO3 = "4";
    public static final String NO_LONGER_REPORTABLE_IN_ICDO3 = "5";
    public static final String ONLY_MALIGNANT_2010_AND_AFTER = "6";
    public static final String UNKNOWN = "9";

    //Properties used for calculation
    public static final String PROP_PRIMARY_SITE = "primarySite";
    public static final String PROP_HISTOLOGY_3 = "histologyIcdO3";
    public static final String PROP_BEHAVIOR_3 = "behaviorIcdO3";
    public static final String PROP_DATE_OF_DIAGNOSIS_YEAR = "dateOfDiagnosisYear";

    public BehaviorRecodeUtils() {
    }

    @Deprecated
    public static String computeBehaviorRecode(Map<String, String> record) {
        if (record == null || record.isEmpty())
            return UNKNOWN;
        return computeBehaviorRecode(record.get(PROP_PRIMARY_SITE), record.get(PROP_HISTOLOGY_3), record.get(PROP_BEHAVIOR_3), record.get(PROP_DATE_OF_DIAGNOSIS_YEAR));
    }

    public static String computeBehaviorRecode(String site, String hist, String behavior, String dxYear) {
        String behaviorRecode = UNKNOWN;
        int iSite = site != null && site.toUpperCase().startsWith("C") && site.length() == 4 && NumberUtils.isDigits(site.substring(1)) ? Integer.parseInt(site.substring(1)) : -1;
        int iHist = hist != null && hist.length() == 4 && NumberUtils.isDigits(hist) ? Integer.parseInt(hist) : -1;
        int iBehavior = behavior != null && behavior.length() == 1 && NumberUtils.isDigits(behavior) ? Integer.parseInt(behavior) : -1;
        int iDxYear = dxYear != null && dxYear.length() == 4 && NumberUtils.isDigits(dxYear) ? Integer.parseInt(dxYear) : 9999;
        if (iSite == -1 || iHist == -1 || iBehavior == -1 || iDxYear == 9999)
            return behaviorRecode;

        //Step 1, If your data only has ICD-O-2 histology codes for data before 2001, please use the ICD Conversion Program to convert these codes to ICD-O-3.
        //This class assumes the conversion is already done.
        //Step 2, The behavior code for all the urinary bladder cases is set to a value of 3 (malignant).
        if (iSite >= 670 && iSite <= 679)
            iBehavior = 3;
        //Step 3, Any case with an ICD-O-3 behavior of 1 and ICD-O-3 histology of 9421-9422 will have the behavior set to 3.
        if (iBehavior == 1 && (iHist == 9421 || iHist == 9422))
            iBehavior = 3;

        //Step 4, The recode is created based on  primary site, ICD-O-3 histology, and ICD-O-3 behavior:

        //2004+ brain cases (C700-C701, C709-729, C751-C753) with ICD-O-3 behavior of 0 (benign) are coded as 0 (benign) in the new variable.
        //2004+ brain cases (C700-C701, C709-729, C751-C753) with ICD-O-3 behavior of 1 (borderline) are coded as 1 (borderline malignancy) in the new variable.
        if (iDxYear >= 2004 && (iSite == 700 || iSite == 701 || (iSite >= 709 && iSite <= 729) || (iSite >= 751 && iSite <= 753)) && (iBehavior == 0 || iBehavior == 1))
            behaviorRecode = iBehavior == 0 ? BENIGN : BORDER_LINE;
            //Cases with ICD-O-3 behavior code of 2 (in situ) are coded as 2 (in situ) in the new variable.
        else if (iBehavior == 2)
            behaviorRecode = INSITU;
            //Cases diagnosed  2001+ with an ICD-O-3 behavior code of 3 (malignant) and 9393,9538,9950,9960-9962,9980,9982-9987,9989 histology codes are coded as 4 (only malignant in ICD-O-3) in the new variable.
        else if (iDxYear >= 2001 && iBehavior == 3 && (iHist == 9393 || iHist == 9538 || iHist == 9950 || (iHist >= 9960 && iHist <= 9962) || iHist == 9980 || (iHist >= 9982 && iHist <= 9987)
                || iHist == 9989))
            behaviorRecode = ONLY_MALIGNANT_IN_ICDO3;
            //Lung cases (C340-C349) diagnosed  2001+ with an ICD-O-3 behavior code of 3 and ICD-O-3 histology code of 9133 are coded as 4 (only malignant in ICD-O-3) in the new variable.
        else if (iSite >= 340 && iSite <= 349 && iDxYear >= 2001 && iBehavior == 3 && iHist == 9133)
            behaviorRecode = ONLY_MALIGNANT_IN_ICDO3;
            //Non-brain cases (C000-C699, C730-C750, C754-C809) with an ICD-O-3 behavior code of 1 (borderline) are coded as 5 (no longer reportable in ICD-O-3) in the new variable.
        else if (iBehavior == 1 && (iSite <= 699 || (iSite >= 730 && iSite <= 750) || (iSite >= 754 && iSite <= 809)))
            behaviorRecode = NO_LONGER_REPORTABLE_IN_ICDO3;
            //All cases not covered above, with an ICD-O-3 behavior code of 3 (malignant) are coded to 3 (malignant) in the new variable.  These are the only cases included in most analysis.
        else if (iBehavior == 3)
            behaviorRecode = MALIGNANT;

        //Cases diagnosed 2010+ with an ICD-O-3 behavior code of 3 (malignant) and 9724,9751,9759,9831,9975,9991,9992 histology codes are coded as 6 (Only Malignant 2010+).
        if (iBehavior == 3 && iDxYear >= 2010 && (iHist == 9724 || iHist == 9751 || iHist == 9759 || iHist == 9831 || iHist == 9975 || iHist == 9991 || iHist == 9992))
            behaviorRecode = ONLY_MALIGNANT_2010_AND_AFTER;

        return behaviorRecode;
    }
}
