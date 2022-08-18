/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.StateCountyTractInputDto;

public class TractEstCongressDistUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(TractEstCongressDistUtils.ALG_VERSION);
        Assert.assertNotNull(TractEstCongressDistUtils.ALG_NAME);
    }

    @Test
    public void testComputeTractEstCongressDist() {

        StateCountyTractInputDto input = new StateCountyTractInputDto();

        // test a SEER city recode
        input.setAddressAtDxState("LO");
        input.setCountyAtDxAnalysis("071");
        input.setCensusTract2010("007903");
        Assert.assertEquals("08", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());

        // test Puerto Rico
        input.setAddressAtDxState("PR");
        input.setCountyAtDxAnalysis("001");
        input.setCensusTract2010("956300");
        Assert.assertEquals("98", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());

        // test unknown A (state, county, or tract are invalid)
        for (String state : Arrays.asList("WA", "INVALID")) {
            for (String county : Arrays.asList("067", "INVALID")) {
                for (String tract : Arrays.asList("012720", "INVALID")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract))
                        Assert.assertEquals(key, "03", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());
                    else
                        Assert.assertEquals(key, "A", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());
                }
            }
        }

        // test unknown B (county was not reported)
        input.setAddressAtDxState("WA");
        input.setCountyAtDxAnalysis("000");
        input.setCensusTract2010("012720");
        Assert.assertEquals("B", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());

        //test unknown C (the state+county+tract combination was not found in lookup table or there was a blank entry in the table)
        for (String state : Arrays.asList("WA", "SK")) {
            for (String county : Arrays.asList("067", "555")) {
                for (String tract : Arrays.asList("012720", "555555")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract))
                        Assert.assertEquals(key, "03", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());
                    else
                        Assert.assertEquals(key, "C", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());
                }
            }
        }

        // test unknown D (missing value)
        for (String state : Arrays.asList("WA", "ZZ", null)) {
            for (String county : Arrays.asList("067", "999", null)) {
                for (String tract : Arrays.asList("012720", "999999", null)) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract))
                        Assert.assertEquals(key, "03", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());
                    else
                        Assert.assertEquals(key, "D", TractEstCongressDistUtils.computeTractEstCongressDist(input).getTractEstCongressDist());
                }
            }
        }
    }
}
