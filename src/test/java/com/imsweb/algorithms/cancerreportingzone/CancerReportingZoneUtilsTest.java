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
        input.setCensusTract2020("007903");
        Assert.assertEquals("06A0274ca", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
        Assert.assertEquals("1", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractReq2010());
        Assert.assertEquals("06A0274ca", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
        Assert.assertEquals("1", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractReq2020());

        // test Puerto Rico
        input.setAddressAtDxState("PR");
        input.setCountyAtDxAnalysis("001");
        input.setCensusTract2020("956300");
        Assert.assertEquals("C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
        Assert.assertEquals("C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractReq2010());
        Assert.assertEquals("C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
        Assert.assertEquals("C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractReq2020());

        // test unknown A (state, county, or tract are invalid)
        for (String state : Arrays.asList("WA", "INVALID")) {
            for (String county : Arrays.asList("067", "INVALID")) {
                for (String tract : Arrays.asList("012720", "INVALID")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2020(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract)) {
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
                    }
                    else {
                        Assert.assertEquals(key, "A", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
                        Assert.assertEquals(key, "A", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
                    }
                }
            }
        }

        // test unknown B (county was not reported)
        input.setAddressAtDxState("WA");
        input.setCountyAtDxAnalysis("000");
        input.setCensusTract2020("012720");
        Assert.assertEquals("B", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
        Assert.assertEquals("B", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractReq2010());
        Assert.assertEquals("B", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
        Assert.assertEquals("B", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZoneTractReq2020());

        //test unknown C (the state+county+tract combination was not found in lookup table or there was a blank entry in the table)
        for (String state : Arrays.asList("WA", "SK")) {
            for (String county : Arrays.asList("067", "555")) {
                for (String tract : Arrays.asList("012720", "555555")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2020(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract)) {
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
                    }
                    else {
                        Assert.assertEquals(key, "C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
                        Assert.assertEquals(key, "C", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
                    }
                }
            }
        }

        // test unknown D (missing value)
        for (String state : Arrays.asList("WA", "ZZ", null)) {
            for (String county : Arrays.asList("067", "999", null)) {
                for (String tract : Arrays.asList("012720", "999999", null)) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2020(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract)) {
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
                        Assert.assertEquals(key, "53A9071za", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
                    }
                    else {
                        Assert.assertEquals(key, "D", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2010());
                        Assert.assertEquals(key, "D", CancerReportingZoneUtils.computeCancerReportingZone(input).getCancerReportingZone2020());
                    }
                }
            }
        }
    }
}
