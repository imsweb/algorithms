/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.algorithms.derivedgrade;

import org.junit.Assert;
import org.junit.Test;

public class DerivedSummaryGradeUtilsTest {

    @Test
    public void testDeriveSummaryStage() {
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, "S", null));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, null, "S"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, "S", "S"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, "S", "1"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, "1", "S"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, "S", "X"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, "X", "S"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade(null, null, "X", "X"));

        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, "S", null));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, null, "S"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, "S", "S"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, "S", "1"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, "1", "S"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, "S", "X"));
        Assert.assertEquals("S", DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, "X", "S"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00001", null, "X", "X"));

        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, "H", null));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, null, "H"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, "H", "H"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, "H", "1"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, "1", "H"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, "H", "X"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, "X", "H"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null,  "X", "X"));

        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", "H", null));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", null, "H"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", "H", "H"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", "H", "1"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", "1", "H"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", "H", "X"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", "X", "H"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "2", "X", "X"));

        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "3", "H", null));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "3", null, "H"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "3", "H", "H"));
        Assert.assertEquals("1", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "3", "H", "1"));
        Assert.assertEquals("1", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "3", "1", "H"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "3", "H", "X"));
        Assert.assertEquals("H", DerivedSummaryGradeUtils.deriveSummaryGrade("00480", "3", "X", "H"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00480", null, "X", "X"));

        Assert.assertEquals("8", DerivedSummaryGradeUtils.deriveSummaryGrade("00790", null, "8", null));
        Assert.assertEquals("8", DerivedSummaryGradeUtils.deriveSummaryGrade("00790", null, null, "8"));
        Assert.assertEquals("8", DerivedSummaryGradeUtils.deriveSummaryGrade("00790", null, "8", "X"));
        Assert.assertEquals("8", DerivedSummaryGradeUtils.deriveSummaryGrade("00790", null, "X", "8"));
        Assert.assertNull(DerivedSummaryGradeUtils.deriveSummaryGrade("00790", null, "X", null));
        Assert.assertNull("8", DerivedSummaryGradeUtils.deriveSummaryGrade("00790", null, null, "X"));
    }
}
