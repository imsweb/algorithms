/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.StateCountyInputDto;

public class PrcdaUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(PrcdaUtils.ALG_VERSION);
        Assert.assertNotNull(PrcdaUtils.ALG_NAME);
    }

    @Test
    public void testComputePrcda() {

        StateCountyInputDto input = new StateCountyInputDto();

        // test a SEER city recode
        input.setAddressAtDxState("LO");
        input.setCountyAtDxAnalysis("071");
        Assert.assertEquals("1", PrcdaUtils.computePrcda(input).getPrcda());
        Assert.assertEquals("1", PrcdaUtils.computePrcda(input).getPrcda2017());

        // test Puerto Rico
        input.setAddressAtDxState("PR");
        input.setCountyAtDxAnalysis("001");
        Assert.assertEquals("0", PrcdaUtils.computePrcda(input).getPrcda());
        Assert.assertEquals("0", PrcdaUtils.computePrcda(input).getPrcda2017());

        // test for when the entire state is PRCDA
        // if the entire state is PRCDA then we should always return 1 regardless of what the county is
        input.setAddressAtDxState("AK");
        for (String county : Arrays.asList("001", "999", "INVALID", null)) {
            input.setCountyAtDxAnalysis(county);
            String key = String.format("%s|%s", input.getAddressAtDxState(), county);
            Assert.assertEquals(key, "1", PrcdaUtils.computePrcda(input).getPrcda());
            Assert.assertEquals(key, "1", PrcdaUtils.computePrcda(input).getPrcda2017());
        }

        // test for when the entire state is NOT PRCDA
        // if the entire state is NOT PRCDA then we should always return 0 regardless of what the county is
        input.setAddressAtDxState("DE");
        for (String county : Arrays.asList("001", "999", "INVALID", null)) {
            input.setCountyAtDxAnalysis(county);
            String key = String.format("%s|%s", input.getAddressAtDxState(), county);
            Assert.assertEquals(key, "0", PrcdaUtils.computePrcda(input).getPrcda());
            Assert.assertEquals(key, "0", PrcdaUtils.computePrcda(input).getPrcda2017());
        }

        // test missing or invalid state or county - or county not reported
        for (String state : Arrays.asList("MN", "ZZ", "INVALID", null)) {
            for (String county : Arrays.asList("035", "000", "999", "INVALID", null)) {
                input.setAddressAtDxState(state);
                input.setCountyAtDxAnalysis(county);
                String key = String.format("%s|%s", state, county);
                if ("MN".equals(state) && "035".equals(county)) {
                    Assert.assertEquals(key, "1", PrcdaUtils.computePrcda(input).getPrcda());
                    Assert.assertEquals(key, "0", PrcdaUtils.computePrcda(input).getPrcda2017());
                }
                else {
                    Assert.assertEquals(key, "9", PrcdaUtils.computePrcda(input).getPrcda());
                    Assert.assertEquals(key, "9", PrcdaUtils.computePrcda(input).getPrcda2017());
                }
            }
        }
    }
}
