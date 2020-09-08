/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ayasiterecode;

import org.junit.Assert;
import org.junit.Test;

public class AyaSiteRecodeUtilsTest {

    @Test
    public void testCalculateSiteRecode () {
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode(null, null, null));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode("", "", ""));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode("C420", null, null));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode("C420", "9827", null));
        Assert.assertEquals("04", AyaSiteRecodeUtils.calculateSiteRecode("C420", "9827", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode("C420", "9811", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode("C420", "9815", "3"));
        Assert.assertEquals("01", AyaSiteRecodeUtils.calculateSiteRecode("C420", "9818", "3"));
        Assert.assertEquals("25", AyaSiteRecodeUtils.calculateSiteRecode("C420", "8800", "3"));
        Assert.assertEquals("56", AyaSiteRecodeUtils.calculateSiteRecode("C420", "8000", "3"));
        Assert.assertEquals("99", AyaSiteRecodeUtils.calculateSiteRecode("C420", "8000", "2"));

    }
    
}
