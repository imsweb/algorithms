/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.algorithms.derivedgrade;

import org.apache.commons.lang3.ArrayUtils;

public final class DerivedSummaryGradeUtils {

    public static final String ALG_NAME = "SEER Derived Summary Grade";

    public static final String ALG_VERSION_2018 = "2018";

    public static final String UNKNOWN = "9";

    private static final String[] _PRIO_BREAST_IN_SITU = new String[] {"H", "M", "L", "3", "2", "1", "D", "C", "B", "A", "9", null};
    private static final String[] _PRIO_BREAST_MALIGNANT = new String[] {"3", "2", "1", "H", "M", "L", "D", "C", "B", "A", "9", null};
    private static final String[] _PRIO_NON_BREAST = new String[] {"S", "5", "4", "3", "2", "1", "E", "D", "C", "B", "A", "H", "M", "L", "9", null};

    private DerivedSummaryGradeUtils() {
        // no instances of this class allowed!
    }

    public static String deriveSummaryGrade(String schemaId, String behavior, String gradeClinical, String gradePathological) {
        String[] priorities = null;
        if ("00480".equals(schemaId)) {
            if ("2".equals(behavior))
                priorities = _PRIO_BREAST_IN_SITU;
            else if ("3".equals(behavior))
                priorities = _PRIO_BREAST_MALIGNANT;
        }
        else
            priorities = _PRIO_NON_BREAST;

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
