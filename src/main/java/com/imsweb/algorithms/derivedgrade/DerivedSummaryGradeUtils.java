/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.algorithms.derivedgrade;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

public final class DerivedSummaryGradeUtils {

    public static final String ALG_NAME = "SEER Derived Summary Grade";

    public static final String ALG_VERSION_2018 = "2018";

    public static final String UNKNOWN = "9";

    private static final String _BREAST_SCHEMA = "00480";

    private static final Set<String> _SPECIAL_SCHEMAS = new HashSet<>(Arrays.asList("00790", "00795", "00811", "00812", "00821", "00822", "00830"));

    private static final String[] _PRIORITY_BREAST_IN_SITU = new String[] {"H", "M", "L", "3", "2", "1", "D", "C", "B", "A", "9", null};
    private static final String[] _PRIORITY_BREAST_MALIGNANT = new String[] {"3", "2", "1", "H", "M", "L", "D", "C", "B", "A", "9", null};
    private static final String[] _PRIORITY_SPECIAL = new String[] {"8", null};
    private static final String[] _PRIORITY_OTHER = new String[] {"S", "5", "4", "3", "2", "1", "E", "D", "C", "B", "A", "H", "M", "L", "9", null};

    private DerivedSummaryGradeUtils() {
        // no instances of this class allowed!
    }

    public static String deriveSummaryGrade(String schemaId, String behavior, String gradeClinical, String gradePathological) {
        String[] priorities = null;
        if (_BREAST_SCHEMA.equals(schemaId)) {
            if ("2".equals(behavior))
                priorities = _PRIORITY_BREAST_IN_SITU;
            else if ("3".equals(behavior))
                priorities = _PRIORITY_BREAST_MALIGNANT;
        }
        else if (_SPECIAL_SCHEMAS.contains(schemaId))
            priorities = _PRIORITY_SPECIAL;
        else
            priorities = _PRIORITY_OTHER;

        if (priorities != null) {

            int clinIdx = ArrayUtils.indexOf(priorities, gradeClinical);
            int pathIdx = ArrayUtils.indexOf(priorities, gradePathological);

            if (clinIdx != -1 && (pathIdx == -1 || clinIdx <= pathIdx))
                return gradeClinical;

            if (pathIdx != -1)
                return gradePathological;
        }

        return null;
    }
}
