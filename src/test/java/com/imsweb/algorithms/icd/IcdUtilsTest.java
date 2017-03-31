/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.icd;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.icd.IcdUtils.REPORTABILITY_OPTIONAL;
import static com.imsweb.algorithms.icd.IcdUtils.REPORTABILITY_YES;
import static com.imsweb.algorithms.icd.IcdUtils.SEX_FEMALE;
import static com.imsweb.algorithms.icd.IcdUtils.SEX_MALE;

public class IcdUtilsTest {

    @Test
    public void testGetIcd9CmToO3Conversions() {
        Assert.assertTrue(IcdUtils.getIcd9CmToO3Conversions().containsKey("140"));
    }

    @Test
    public void testGetIcd10CmToO3Conversions() {
        Assert.assertTrue(IcdUtils.getIcd10CmToO3Conversions().containsKey("C00"));
    }

    @Test
    public void testGetIcd10ToO3Conversions() {
        Assert.assertTrue(IcdUtils.getIcd10ToO3Conversions().containsKey("C000"));
    }

    @Test
    public void testGetIcdo3SiteLookup() {
        Assert.assertTrue(IcdUtils.getIcdo3SiteLookup().containsKey("C000"));
    }

    @Test
    public void testGetIcdo3FromIcd9Cm() {
        IcdConversionEntry icd = IcdUtils.getIcdo3FromIcd9Cm("1400", SEX_MALE);
        Assert.assertEquals("C000", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd9Cm("1734", SEX_FEMALE);
        Assert.assertEquals("C444", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_OPTIONAL, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd9Cm("2396", null);
        Assert.assertEquals("C719", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("1", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        //testing codes where gender matters
        icd = IcdUtils.getIcdo3FromIcd9Cm("2320", SEX_MALE);
        Assert.assertEquals("C440", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("2", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd9Cm("2320", null);
        Assert.assertEquals("C440", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("2", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd9Cm("2320", SEX_FEMALE);
        Assert.assertEquals("C440", icd.getTargetCode());
        Assert.assertEquals("8720", icd.getHistology());
        Assert.assertEquals("2", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        Assert.assertNull(IcdUtils.getIcdo3FromIcd9Cm(null, null));

        Assert.assertNull(IcdUtils.getIcdo3FromIcd9Cm("NOTACODE", ""));
    }

    @Test
    public void testGetIcdo3FromIcd10Cm() {
        IcdConversionEntry icd = IcdUtils.getIcdo3FromIcd10Cm("C00");
        Assert.assertEquals("C009", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd10Cm("C6312");
        Assert.assertEquals("C631", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("2", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd10Cm("Z5112");
        Assert.assertEquals("C809", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd10Cm("C4A52");
        Assert.assertEquals("C445", icd.getTargetCode());
        Assert.assertEquals("8247", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("9", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd10Cm("C8308");
        Assert.assertEquals("C778", icd.getTargetCode());
        Assert.assertEquals("9823", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("6", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        Assert.assertNull(IcdUtils.getIcdo3FromIcd10Cm(null));

        Assert.assertNull(IcdUtils.getIcdo3FromIcd10Cm("NOTACODE"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testGetIcdo3FromIcd10() {
        IcdConversionEntry icd = IcdUtils.getIcdo3FromIcd10("C000");
        Assert.assertEquals("C000", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertNull(icd.getLaterality());
        Assert.assertNull(icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd10("C00");
        Assert.assertEquals("C009", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertNull(icd.getLaterality());
        Assert.assertNull(icd.getReportable());

        icd = IcdUtils.getIcdo3FromIcd10("C999", true);
        Assert.assertNull(icd);

        icd = IcdUtils.getIcdo3FromIcd10("C999", false);
        Assert.assertEquals("C809", icd.getTargetCode());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertNull(icd.getLaterality());
        Assert.assertNull(icd.getReportable());
    }
}
