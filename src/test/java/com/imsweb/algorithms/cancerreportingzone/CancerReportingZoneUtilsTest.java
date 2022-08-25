/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.StateCountyTractInputDto;

public class CancerReportingZoneUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(CancerReportingZoneUtils.ALG_VERSION);
        Assert.assertNotNull(CancerReportingZoneUtils.ALG_NAME);
    }

    @Test
    public void testComputeTractEstCongressDist() {

        StateCountyTractInputDto input = new StateCountyTractInputDto();

        // test a SEER city recode
        input.setAddressAtDxState("LO");
        input.setCountyAtDxAnalysis("071");
        input.setCensusTract2010("007903");
        Assert.assertEquals("06A0274", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
        Assert.assertEquals("1", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractCert());

        // test Puerto Rico
        input.setAddressAtDxState("PR");
        input.setCountyAtDxAnalysis("001");
        input.setCensusTract2010("956300");
        Assert.assertEquals("C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
        Assert.assertNull(CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractCert()); // not available for PR?

        // test unknown A (state, county, or tract are invalid)
        for (String state : Arrays.asList("WA", "INVALID")) {
            for (String county : Arrays.asList("067", "INVALID")) {
                for (String tract : Arrays.asList("012720", "INVALID")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract))
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
                    else
                        Assert.assertEquals(key, "A", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
                }
            }
        }

        // test unknown B (county was not reported)
        input.setAddressAtDxState("WA");
        input.setCountyAtDxAnalysis("000");
        input.setCensusTract2010("012720");
        Assert.assertEquals("B", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());

        //test unknown C (the state+county+tract combination was not found in lookup table or there was a blank entry in the table)
        for (String state : Arrays.asList("WA", "SK")) {
            for (String county : Arrays.asList("067", "555")) {
                for (String tract : Arrays.asList("012720", "555555")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract))
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
                    else
                        Assert.assertEquals(key, "C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
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
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
                    else
                        Assert.assertEquals(key, "D", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone());
                }
            }
        }
    }
}
