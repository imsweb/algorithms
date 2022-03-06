/*
 * Copyright (C) 2016 Information Management Services, Inc.
 */
package com.imsweb.algorithms.behavrecode;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class BehaviorRecodeUtilsTest {

    private static final String _PROP_PRIMARY_SITE = "primarySite";
    private static final String _PROP_HISTOLOGY_3 = "histologyIcdO3";
    private static final String _PROP_BEHAVIOR_3 = "behaviorIcdO3";
    private static final String _PROP_DATE_OF_DIAGNOSIS_YEAR = "dateOfDiagnosisYear";

    @Test
    public void assertAlgInfo() {
        Assert.assertNotNull(BehaviorRecodeUtils.ALG_NAME);
        Assert.assertNotNull(BehaviorRecodeUtils.ALG_VERSION);
    }

    @Test
    public void testComputeBehaviorRecode() {
        //Blank and Invalid fields
        Assert.assertEquals("9", computeBehaviorRecode(null));
        Map<String, String> rec = new HashMap<>();
        Assert.assertEquals("9", computeBehaviorRecode(rec));
        rec.put(_PROP_PRIMARY_SITE, "C659");
        rec.put(_PROP_HISTOLOGY_3, "8000");
        rec.put(_PROP_BEHAVIOR_3, "3");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "9999");
        Assert.assertEquals("9", computeBehaviorRecode(rec)); //9999 year
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2005");
        rec.put(_PROP_PRIMARY_SITE, "X659");
        Assert.assertEquals("9", computeBehaviorRecode(rec)); //invalid site
        rec.put(_PROP_PRIMARY_SITE, "C659");
        rec.put(_PROP_BEHAVIOR_3, "13");
        Assert.assertEquals("9", computeBehaviorRecode(rec)); //invalid behavior
        rec.put(_PROP_BEHAVIOR_3, "3");
        rec.put(_PROP_HISTOLOGY_3, "800");
        Assert.assertEquals("9", computeBehaviorRecode(rec)); //invalid hist
        rec.put(_PROP_HISTOLOGY_3, "8000");
        Assert.assertEquals("3", computeBehaviorRecode(rec));

        //Testing urinary bladder conversion
        rec.put(_PROP_PRIMARY_SITE, "C672");
        rec.put(_PROP_BEHAVIOR_3, "1");
        rec.put(_PROP_HISTOLOGY_3, "8000");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2005");
        //All cases not covered above, with an ICD-O-3 behavior code of 3 (malignant) are coded to 3 (malignant) in the new variable.
        Assert.assertEquals("3", computeBehaviorRecode(rec)); //behavior is changed from 1 to 3
        rec.put(_PROP_HISTOLOGY_3, "9985");
        //Cases diagnosed  2001+ with an ICD-O-3 behavior code of 3 (malignant) and 9393,9538,9950,9960-9962,9980,9982-9987,9989 histology codes are coded as 4 (only malignant in ICD-O-3) in the new variable.
        Assert.assertEquals("4", computeBehaviorRecode(rec)); //behavior is changed from 1 to 3
        // Not urinary
        rec.put(_PROP_PRIMARY_SITE, "C669");
        Assert.assertEquals("5", computeBehaviorRecode(rec));

        //Testing histology conversion
        rec.put(_PROP_PRIMARY_SITE, "C669");
        rec.put(_PROP_BEHAVIOR_3, "1");
        rec.put(_PROP_HISTOLOGY_3, "9421");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2005");
        //All cases not covered above, with an ICD-O-3 behavior code of 3 (malignant) are coded to 3 (malignant) in the new variable.
        Assert.assertEquals("5", computeBehaviorRecode(rec)); //behavior NO LONGER changed from 1 to 3
        //Not in the special histology 9421 & 9422
        rec.put(_PROP_HISTOLOGY_3, "9420");
        Assert.assertEquals("5", computeBehaviorRecode(rec));

        //2004+ brain cases (C700-C701, C709-729, C751-C753) with ICD-O-3 behavior of 0 (benign) are coded as 0 (benign) in the new variable.
        rec.put(_PROP_PRIMARY_SITE, "C714");
        rec.put(_PROP_BEHAVIOR_3, "0");
        rec.put(_PROP_HISTOLOGY_3, "8000");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2005");
        Assert.assertEquals("0", computeBehaviorRecode(rec));
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2003");
        Assert.assertEquals("9", computeBehaviorRecode(rec));

        //2004+ brain cases (C700-C701, C709-729, C751-C753) with ICD-O-3 behavior of 1 (borderline) are coded as 1 (borderline malignancy) in the new variable.
        rec.put(_PROP_PRIMARY_SITE, "C714");
        rec.put(_PROP_BEHAVIOR_3, "1");
        rec.put(_PROP_HISTOLOGY_3, "8000");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2005");
        Assert.assertEquals("1", computeBehaviorRecode(rec));

        //Cases with ICD-O-3 behavior code of 2 (in situ) are coded as 2 (in situ) in the new variable.
        rec.put(_PROP_PRIMARY_SITE, "C669");
        rec.put(_PROP_BEHAVIOR_3, "2");
        rec.put(_PROP_HISTOLOGY_3, "9422");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "1975");
        Assert.assertEquals("2", computeBehaviorRecode(rec));
        rec.put(_PROP_PRIMARY_SITE, "C679");
        Assert.assertEquals("3", computeBehaviorRecode(rec));

        //Cases diagnosed  2001+ with an ICD-O-3 behavior code of 3 (malignant) and 9393,9538,9950,9960-9962,9980,9982-9987,9989 histology codes are coded as 4 (only malignant in ICD-O-3) in the new variable.
        rec.put(_PROP_PRIMARY_SITE, "C669");
        rec.put(_PROP_BEHAVIOR_3, "3");
        rec.put(_PROP_HISTOLOGY_3, "9962");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2001");
        Assert.assertEquals("4", computeBehaviorRecode(rec));
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        Assert.assertEquals("3", computeBehaviorRecode(rec));

        //Lung cases (C340-C349) diagnosed  2001+ with an ICD-O-3 behavior code of 3 and ICD-O-3 histology code of 9133 are coded as 4 (only malignant in ICD-O-3) in the new variable.
        rec.put(_PROP_PRIMARY_SITE, "C344");
        rec.put(_PROP_BEHAVIOR_3, "3");
        rec.put(_PROP_HISTOLOGY_3, "9133");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2001");
        Assert.assertEquals("4", computeBehaviorRecode(rec));
        rec.put(_PROP_HISTOLOGY_3, "8000");
        Assert.assertEquals("3", computeBehaviorRecode(rec));

        //Non-brain cases (C000-C699, C730-C750, C754-C809) with an ICD-O-3 behavior code of 1 (borderline) are coded as 5 (no longer reportable in ICD-O-3) in the new variable.
        rec.put(_PROP_PRIMARY_SITE, "C344");
        rec.put(_PROP_BEHAVIOR_3, "1");
        rec.put(_PROP_HISTOLOGY_3, "9133");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2006");
        Assert.assertEquals("5", computeBehaviorRecode(rec));
        rec.put(_PROP_PRIMARY_SITE, "C700");
        Assert.assertEquals("1", computeBehaviorRecode(rec));
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2002");
        Assert.assertEquals("9", computeBehaviorRecode(rec));

        //All cases not covered above, with an ICD-O-3 behavior code of 3 (malignant) are coded to 3 (malignant) in the new variable.
        rec.put(_PROP_PRIMARY_SITE, "C344");
        rec.put(_PROP_BEHAVIOR_3, "3");
        rec.put(_PROP_HISTOLOGY_3, "9724");
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2006");
        Assert.assertEquals("3", computeBehaviorRecode(rec));
        //Cases diagnosed 2010+ with an ICD-O-3 behavior code of 3 (malignant) and 9724,9751,9759,9831,9975,9991,9992 histology codes are coded as 6 (Only Malignant 2010+).
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2010");
        Assert.assertEquals("6", computeBehaviorRecode(rec));
        rec.put(_PROP_HISTOLOGY_3, "9831");
        Assert.assertEquals("6", computeBehaviorRecode(rec));
        rec.put(_PROP_HISTOLOGY_3, "9990");
        Assert.assertEquals("3", computeBehaviorRecode(rec));
    }

    private String computeBehaviorRecode(Map<String, String> record) {
        if (record == null || record.isEmpty())
            return BehaviorRecodeUtils.UNKNOWN;
        return BehaviorRecodeUtils.computeBehaviorRecode(record.get(_PROP_PRIMARY_SITE), record.get(_PROP_HISTOLOGY_3), record.get(_PROP_BEHAVIOR_3), record.get(_PROP_DATE_OF_DIAGNOSIS_YEAR));
    }
}
