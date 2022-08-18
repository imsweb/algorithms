/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ephtsubcounty;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.StateCountyTractInputDto;

public class EphtSubCountyUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(EphtSubCountyUtils.ALG_VERSION);
        Assert.assertNotNull(EphtSubCountyUtils.ALG_NAME);
    }

    @Test
    public void testComputeEphtSubCounty() {
        StateCountyTractInputDto input = new StateCountyTractInputDto();

        // test a SEER city recode
        input.setAddressAtDxState("LO");
        input.setCountyAtDxAnalysis("071");
        input.setCensusTract2010("007903");
        Assert.assertEquals("00060713015", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
        Assert.assertEquals("00060713188", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
        Assert.assertEquals("00060717452", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());

        // test Puerto Rico
        input.setAddressAtDxState("PR");
        input.setCountyAtDxAnalysis("001");
        input.setCensusTract2010("956300");
        Assert.assertEquals("C", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
        Assert.assertEquals("C", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
        Assert.assertEquals("C", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());

        // test unknown A (state, county, or tract are invalid)
        for (String state : Arrays.asList("WA", "INVALID")) {
            for (String county : Arrays.asList("067", "INVALID")) {
                for (String tract : Arrays.asList("012720", "INVALID")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract)) {
                        Assert.assertEquals(key, "00005306787", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
                        Assert.assertEquals(key, "00053067466", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
                        Assert.assertEquals(key, "00530671089", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());
                    }
                    else {
                        Assert.assertEquals(key, "A", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
                        Assert.assertEquals(key, "A", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
                        Assert.assertEquals(key, "A", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());
                    }
                }
            }
        }

        // test unknown B (county was not reported)
        input.setAddressAtDxState("WA");
        input.setCountyAtDxAnalysis("000");
        input.setCensusTract2010("012720");
        Assert.assertEquals("B", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
        Assert.assertEquals("B", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
        Assert.assertEquals("B", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());

        //test unknown C (the state+county+tract combination was not found in lookup table or there was a blank entry in the table)
        for (String state : Arrays.asList("WA", "SK")) {
            for (String county : Arrays.asList("067", "555")) {
                for (String tract : Arrays.asList("012720", "555555")) {
                    input.setAddressAtDxState(state);
                    input.setCountyAtDxAnalysis(county);
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract)) {
                        Assert.assertEquals(key, "00005306787", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
                        Assert.assertEquals(key, "00053067466", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
                        Assert.assertEquals(key, "00530671089", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());
                    }
                    else {
                        Assert.assertEquals(key, "C", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
                        Assert.assertEquals(key, "C", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
                        Assert.assertEquals(key, "C", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());
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
                    input.setCensusTract2010(tract);
                    String key = String.format("%s|%s|%s", state, county, tract);

                    if ("WA".equals(state) && "067".equals(county) && "012720".equals(tract)) {
                        Assert.assertEquals(key, "00005306787", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
                        Assert.assertEquals(key, "00053067466", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
                        Assert.assertEquals(key, "00530671089", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());
                    }
                    else {
                        Assert.assertEquals(key, "D", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId5k());
                        Assert.assertEquals(key, "D", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId20k());
                        Assert.assertEquals(key, "D", EphtSubCountyUtils.computeEphtSubCounty(input).getEpht2010GeoId50k());
                    }
                }
            }
        }
    }
}