/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ayasiterecode;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.ayasiterecode.AyaSiteRecodeUtils.ALG_VERSION_2008;
import static com.imsweb.algorithms.ayasiterecode.AyaSiteRecodeUtils.ALG_VERSION_2020;

public class AyaSiteRecodeUtilsTest {

    @Test
    public void testCalculateSiteRecodeWho2008 () {
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, null, null, null));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "", "", ""));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", null, null));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "9827", null));
        Assert.assertEquals("04", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "9827", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "9811", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "9815", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "9818", "3"));
        Assert.assertEquals("25", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "8800", "3"));
        Assert.assertEquals("56", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "8000", "3"));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, "C420", "8000", "2"));
    }

    @Test
    public void testCalculateSiteRecode2020Revision () {
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, null, null, null));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "", "", ""));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", null, null));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "9827", null));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "9827", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "9811", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "9815", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "9818", "3"));
        Assert.assertEquals("25", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "8800", "3"));
        Assert.assertEquals("56", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "8000", "3"));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, "C420", "8000", "2"));
    }
}
