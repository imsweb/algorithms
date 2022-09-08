/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms.daystotreatment;

import org.junit.Assert;
import org.junit.Test;

public class DaysToTreatmentAlgorithmTest {

    @Test
    public void testComputeDaysToTreatment() {
        Assert.assertEquals("001", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "20200102"));
        Assert.assertEquals("031", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "20200201"));
        Assert.assertEquals("366", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "20210101"));

        Assert.assertEquals("000", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200102", "20200101"));
        Assert.assertEquals("000", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200201", "20200101"));
        Assert.assertEquals("000", DaysToTreatmentAlgorithm.computeDaysToTreatment("20210101", "20200101"));

        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment(null, "20200102"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("202001", "20200102"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("ABC", "20200102"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("999999999", "20200102"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("000000000", "20200102"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200230", "20210101"));

        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", null));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "202001"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "ABC"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "99999999"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "00000000"));
        Assert.assertEquals("999", DaysToTreatmentAlgorithm.computeDaysToTreatment("20200101", "20200230"));
    }
}
