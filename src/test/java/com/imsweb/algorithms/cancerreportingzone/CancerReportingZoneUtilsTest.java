/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CancerReportingZoneUtilsTest {

    private static final String _PROP_STATE_DX = "addressAtDxState";
    private static final String _PROP_COUNTY_DX_ANALYSIS = "countyAtDxAnalysis";
    private static final String _PROP_CENSUS_TRACT_2010 = "censusTract2010";

    @Test
    public void assertInfo() {
        Assert.assertNotNull(CancerReportingZoneUtils.ALG_VERSION);
        Assert.assertNotNull(CancerReportingZoneUtils.ALG_NAME);
    }

    @Test
    public void testComputeTractEstCongressDist() {

        Map<String, String> record = new HashMap<>();
        record.put(_PROP_STATE_DX, "WA");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "067");
        record.put(_PROP_CENSUS_TRACT_2010, "012721");

        //test unknown C (not found)
        Assert.assertEquals("C", computeCancerReportingZone(record).getCancerReportingZone());

        // test unknown A (invalid value)
        record.put(_PROP_STATE_DX, "AA");
        Assert.assertEquals("A", computeCancerReportingZone(record).getCancerReportingZone());
        record.put(_PROP_STATE_DX, "WY");

        // test unknown B (county not reported)
        record.put(_PROP_COUNTY_DX_ANALYSIS, "000");
        Assert.assertEquals("B", computeCancerReportingZone(record).getCancerReportingZone());

        // test unknown D (missing value)
        record.put(_PROP_COUNTY_DX_ANALYSIS, null);
        Assert.assertEquals("D", computeCancerReportingZone(record).getCancerReportingZone());

        // test a case that will return a code
        record.clear();
        record.put(_PROP_STATE_DX, "WA");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "067");
        record.put(_PROP_CENSUS_TRACT_2010, "012720");
        Assert.assertEquals("A9071za", computeCancerReportingZone(record).getCancerReportingZone());

        record.clear();
        record.put(_PROP_STATE_DX, "ID");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        record.put(_PROP_CENSUS_TRACT_2010, "000100");
        Assert.assertEquals("A9014zd", computeCancerReportingZone(record).getCancerReportingZone());
    }

    private CancerReportingZoneOutputDto computeCancerReportingZone(Map<String, String> record) {
        CancerReportingZoneInputDto input = new CancerReportingZoneInputDto();
        input.setAddressAtDxState(record.get(_PROP_STATE_DX));
        input.setCountyAtDxAnalysis(record.get(_PROP_COUNTY_DX_ANALYSIS));
        input.setCensusTract2010(record.get(_PROP_CENSUS_TRACT_2010));

        return CancerReportingZoneUtils.computeCancerReportingZone(input);
    }
}
