/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.lymphoma;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.lymphoma.LymphoidNeoplasmRecodeUtils.ALG_VERSION_2021;

public class LymphoidNeoplasmRecodeUtilsTest {

    @Test
    public void testCalculateSiteRecode2021Revision() {
        Assert.assertEquals("99", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, null, null));
        Assert.assertEquals("99", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "", ""));
        Assert.assertEquals("99", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", null));
        Assert.assertEquals("01", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9651"));
        Assert.assertEquals("02", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9652"));
        Assert.assertEquals("03", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9654"));
        Assert.assertEquals("04", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9663"));
        Assert.assertEquals("05", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9661"));
        Assert.assertEquals("06", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9659"));
        Assert.assertEquals("07", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9812"));
        Assert.assertEquals("08", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9823"));
        Assert.assertEquals("09", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9833"));
        Assert.assertEquals("10", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9673"));
        Assert.assertEquals("11", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9671"));
        Assert.assertEquals("12", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9761"));
        Assert.assertEquals("13", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9688"));
        Assert.assertEquals("13", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9684"));
        Assert.assertEquals("14", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C499", "9688"));
        Assert.assertEquals("14", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9712"));
        Assert.assertEquals("15", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9678"));
        Assert.assertEquals("16", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9679"));
        Assert.assertEquals("17", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9826"));
        Assert.assertEquals("18", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9689"));
        Assert.assertEquals("19", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C769", "9699"));
        Assert.assertEquals("19", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9764"));
        Assert.assertEquals("20", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C775", "9699"));
        Assert.assertEquals("21", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9691"));
        Assert.assertEquals("22", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9940"));
        Assert.assertEquals("23", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9734"));
        Assert.assertEquals("24", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9732"));
        Assert.assertEquals("25", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9762"));
        Assert.assertEquals("26", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9597"));
        Assert.assertEquals("27", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9837"));
        Assert.assertEquals("28", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9700"));
        Assert.assertEquals("29", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9701"));
        Assert.assertEquals("30", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9702"));
        Assert.assertEquals("31", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9705"));
        Assert.assertEquals("32", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9708"));
        Assert.assertEquals("33", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9714"));
        Assert.assertEquals("34", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9716"));
        Assert.assertEquals("35", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9717"));
        Assert.assertEquals("36", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9726"));
        Assert.assertEquals("37", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9718"));
        Assert.assertEquals("38", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9827"));
        Assert.assertEquals("39", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9948"));
        Assert.assertEquals("40", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9831"));
        Assert.assertEquals("41", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9834"));
        Assert.assertEquals("42", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9727"));
        Assert.assertEquals("43", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9596"));
        Assert.assertEquals("44", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "9971"));
        Assert.assertEquals("99", LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, "C809", "8000"));
    }
}
