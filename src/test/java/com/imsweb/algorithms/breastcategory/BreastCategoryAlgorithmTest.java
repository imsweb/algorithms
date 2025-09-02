/*
 * Copyright (C) 2025 Information Management Services, Inc.
 */
package com.imsweb.algorithms.breastcategory;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.BREAST_SUBTYPE_HR_NEG_HER2_NEG;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.BREAST_SUBTYPE_HR_NEG_HER2_POS;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.BREAST_SUBTYPE_HR_POS_HER2_NEG;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.BREAST_SUBTYPE_HR_POS_HER2_POS;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.BREAST_SUBTYPE_NOT_CODED;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.BREAST_SUBTYPE_UNKNOWN;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.ER_PR_HER2_NEGATIVE;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.ER_PR_HER2_POSITIVE;
import static com.imsweb.algorithms.breastcategory.BreastCategoryAlgorithm.ER_PR_HER2_UNKNOWN;

public class BreastCategoryAlgorithmTest {

    @Test
    public void testIsBreastCase() {
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C500", "8000"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C500", "8699"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C500", "8700"));
        Assert.assertFalse(BreastCategoryAlgorithm.isBreastCase("C500", "8701"));
        Assert.assertFalse(BreastCategoryAlgorithm.isBreastCase("C500", "8981"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C500", "8982"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C500", "8983"));
        Assert.assertFalse(BreastCategoryAlgorithm.isBreastCase("C500", "8984"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C501", "8000"));
        Assert.assertFalse(BreastCategoryAlgorithm.isBreastCase("C501", "8719"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C501", "8720"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C501", "8790"));
        Assert.assertFalse(BreastCategoryAlgorithm.isBreastCase("C501", "8791"));
        Assert.assertFalse(BreastCategoryAlgorithm.isBreastCase("C501", "9999"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C506", "8000"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C507", "8000"));
        Assert.assertTrue(BreastCategoryAlgorithm.isBreastCase("C509", "8000"));
    }

    @Test
    public void testComputeErPr() {
        Assert.assertEquals(ER_PR_HER2_UNKNOWN, BreastCategoryAlgorithm.computeErPr(1990, null, null, null));
        Assert.assertEquals(ER_PR_HER2_UNKNOWN, BreastCategoryAlgorithm.computeErPr(2000, null, null, null));
        Assert.assertEquals(ER_PR_HER2_UNKNOWN, BreastCategoryAlgorithm.computeErPr(2010, null, null, null));
        Assert.assertEquals(ER_PR_HER2_UNKNOWN, BreastCategoryAlgorithm.computeErPr(2020, null, null, null));

        // use tumor marker for extra early years
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeErPr(2000, "1", null, null));
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeErPr(2000, "2", null, null));

        // use SSF for early years
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeErPr(2010, null, "010", null));
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeErPr(2010, null, "020", null));

        // use SSDI for later years
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeErPr(2020, null, null, "1"));
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeErPr(2020, null, null, "0"));
    }

    @Test
    public void testComputeHer2() {

        // use SSF15 if possible
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeHer2(2010, null, null, null, null, "010", null));
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeHer2(2010, null, null, null, null, "020", null));
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeHer2(2010, "010", null, null, null, "020", null));

        // use SSF9 if possible
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeHer2(2010, "010", null, null, null, null, null));
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeHer2(2010, "010", null, null, null, "", null));
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeHer2(2010, "010", null, null, null, "988", null));
        Assert.assertEquals(ER_PR_HER2_UNKNOWN, BreastCategoryAlgorithm.computeHer2(2010, null, null, null, null, "988", null));

        // use SSF11, SSF13 or SSF14 if possible
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeHer2(2010, "010", "020", null, null, null, null));
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeHer2(2010, null, null, "020", null, null, null));
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeHer2(2010, null, null, null, "020", null, null));
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeHer2(2010, null, "020", "020", "010", null, null));

        // use SSDI
        Assert.assertEquals(ER_PR_HER2_NEGATIVE, BreastCategoryAlgorithm.computeHer2(2020, null, null, null, null, null, "0"));
        Assert.assertEquals(ER_PR_HER2_POSITIVE, BreastCategoryAlgorithm.computeHer2(2020, null, null, null, null, null, "1"));
        Assert.assertEquals(ER_PR_HER2_UNKNOWN, BreastCategoryAlgorithm.computeHer2(2020, null, null, null, null, null, null));
    }

    @Test
    public void testComputeBreastSubtype() {
        Assert.assertEquals(BREAST_SUBTYPE_NOT_CODED, BreastCategoryAlgorithm.computeBreastSubtype(2008, null, null, null));
        Assert.assertEquals(BREAST_SUBTYPE_NOT_CODED, BreastCategoryAlgorithm.computeBreastSubtype(2008, "1", "1", "1"));
        Assert.assertEquals(BREAST_SUBTYPE_NOT_CODED, BreastCategoryAlgorithm.computeBreastSubtype(2008, "2", "2", "2"));

        Assert.assertEquals(BREAST_SUBTYPE_UNKNOWN, BreastCategoryAlgorithm.computeBreastSubtype(2020, null, null, null));
        Assert.assertEquals(BREAST_SUBTYPE_UNKNOWN, BreastCategoryAlgorithm.computeBreastSubtype(2020, "1", null, null));
        Assert.assertEquals(BREAST_SUBTYPE_UNKNOWN, BreastCategoryAlgorithm.computeBreastSubtype(2020, null, "1", null));
        Assert.assertEquals(BREAST_SUBTYPE_UNKNOWN, BreastCategoryAlgorithm.computeBreastSubtype(2020, null, null, "1"));
        Assert.assertEquals(BREAST_SUBTYPE_UNKNOWN, BreastCategoryAlgorithm.computeBreastSubtype(2020, "2", null, null));
        Assert.assertEquals(BREAST_SUBTYPE_UNKNOWN, BreastCategoryAlgorithm.computeBreastSubtype(2020, null, "2", null));
        Assert.assertEquals(BREAST_SUBTYPE_UNKNOWN, BreastCategoryAlgorithm.computeBreastSubtype(2020, null, null, "2"));

        // HER2 positive (1)
        Assert.assertEquals(BREAST_SUBTYPE_HR_POS_HER2_POS, BreastCategoryAlgorithm.computeBreastSubtype(2020, "1", "1", "1"));
        Assert.assertEquals(BREAST_SUBTYPE_HR_POS_HER2_POS, BreastCategoryAlgorithm.computeBreastSubtype(2020, "1", "2", "1"));
        Assert.assertEquals(BREAST_SUBTYPE_HR_POS_HER2_POS, BreastCategoryAlgorithm.computeBreastSubtype(2020, "2", "1", "1"));
        Assert.assertEquals(BREAST_SUBTYPE_HR_NEG_HER2_POS, BreastCategoryAlgorithm.computeBreastSubtype(2020, "2", "2", "1"));

        // HER2 negative (2)
        Assert.assertEquals(BREAST_SUBTYPE_HR_NEG_HER2_NEG, BreastCategoryAlgorithm.computeBreastSubtype(2020, "2", "2", "2"));
        Assert.assertEquals(BREAST_SUBTYPE_HR_POS_HER2_NEG, BreastCategoryAlgorithm.computeBreastSubtype(2020, "1", "1", "2"));
        Assert.assertEquals(BREAST_SUBTYPE_HR_POS_HER2_NEG, BreastCategoryAlgorithm.computeBreastSubtype(2020, "1", "2", "2"));
        Assert.assertEquals(BREAST_SUBTYPE_HR_POS_HER2_NEG, BreastCategoryAlgorithm.computeBreastSubtype(2020, "2", "1", "2"));
    }
}
