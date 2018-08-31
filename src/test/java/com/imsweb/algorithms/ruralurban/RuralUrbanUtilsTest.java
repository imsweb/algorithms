/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class RuralUrbanUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(RuralUrbanUtils.ALG_VERSION);
        Assert.assertNotNull(RuralUrbanUtils.ALG_NAME);
        Assert.assertNotNull(RuralUrbanUtils.ALG_INFO);
    }

    @Test
    public void testGetRuralUrbanCensusPercentage() {
        RuralUrbanInputDto input = new RuralUrbanInputDto();
        input.setAddressAtDxState("AL");
        input.setAddressAtDxCounty("001");
        input.setCensusTract2010("020200");
        // only provided the 2010 census, so there should be no result for the 2000 percentage...
        Assert.assertNull(RuralUrbanUtils.computeUrbanRuralIndicatorCode(input).getUrbanRuralIndicatorCode2000Percentage());
        Assert.assertNotNull(RuralUrbanUtils.computeUrbanRuralIndicatorCode(input).getUrbanRuralIndicatorCode2010Percentage());
        input.setAddressAtDxState("PR");
        input.setAddressAtDxCounty("151");
        input.setCensusTract2000("000000");
        input.setCensusTract2010("000000");
        // PR 151 000000 is in the 2000 excel file, but the percentage is missing --- only the indicator value is given
        Assert.assertNull(RuralUrbanUtils.computeUrbanRuralIndicatorCode(input).getUrbanRuralIndicatorCode2000Percentage());
        // PR 151 000000 is not in the 2010 excel file
        Assert.assertNull(RuralUrbanUtils.computeUrbanRuralIndicatorCode(input).getUrbanRuralIndicatorCode2010Percentage());
    }

    @Test
    public void testComputeRuralUrbanContinuum() {

        Map<String, String> record = new HashMap<>();

        List<String> states = Arrays.asList("AL", "MP", "--", null, "", "ZZ");
        List<String> counties = Arrays.asList("003", "777", "---", null, "", "000", "999");
        List<String> tracts = Arrays.asList("021100", "123456", "000001", null, "", "999999");

        for (String state : states) {
            for (String county : counties) {
                record.put(RuralUrbanUtils.PROP_STATE_DX, state);
                record.put(RuralUrbanUtils.PROP_COUNTY_DX, county);

                if ("--".equals(state) || "---".equals(county)) {
                    Assert.assertEquals("96", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
                    Assert.assertEquals("96", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
                    Assert.assertEquals("96", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());
                }
                else if (state == null || state.isEmpty() || state.equals("ZZ") || county == null || county.isEmpty() || county.equals("999")) {
                    Assert.assertEquals("99", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
                    Assert.assertEquals("99", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
                    Assert.assertEquals("99", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());
                }
                else if (county.equals("000")) {
                    Assert.assertEquals("97", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
                    Assert.assertEquals("97", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
                    Assert.assertEquals("97", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());
                }
                else if (state.equals("MP") || county.equals("777")) {
                    Assert.assertEquals("98", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
                    Assert.assertEquals("98", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
                    Assert.assertEquals("98", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());
                }
                else {
                    Assert.assertEquals("02", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
                    Assert.assertEquals("04", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
                    Assert.assertEquals("03", RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());
                }

                for (String tract : tracts) {
                    record.put(RuralUrbanUtils.PROP_CENSUS_TRACT_2000, tract);
                    record.put(RuralUrbanUtils.PROP_CENSUS_TRACT_2010, tract);

                    if ("--".equals(state) || "---".equals(county) || "000001".equals(tract)) {
                        Assert.assertEquals("A", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("A", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("A", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("A", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2010());
                    }
                    else if (state == null || state.isEmpty() || state.equals("ZZ") || county == null || county.isEmpty() || county.equals("999") || tract == null || tract.isEmpty() || tract.equals(
                            "999999")) {
                        Assert.assertEquals("D", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("D", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("D", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("D", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2010());
                    }
                    else if (county.equals("000")) {
                        Assert.assertEquals("B", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("B", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("B", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("B", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2010());
                    }
                    else if (state.equals("MP") || county.equals("777") || tract.equals("123456")) {
                        Assert.assertEquals("C", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("C", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("C", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("C", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2010());
                    }
                    else {
                        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "001");
                        record.put(RuralUrbanUtils.PROP_CENSUS_TRACT_2000, "020800");
                        Assert.assertEquals("3", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2000());
                        record.put(RuralUrbanUtils.PROP_CENSUS_TRACT_2000, tract);
                        Assert.assertEquals("4", RuralUrbanUtils.computeUrbanRuralIndicatorCode(record).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("2", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("1", RuralUrbanUtils.computeRuralUrbanCommutingArea(record).getRuralUrbanCommutingArea2010());
                    }
                }
            }
        }

        ////////////////////////////////////////////////////////////
        // TESTS FOR SEER CITY SPECIAL CASES
        ////////////////////////////////////////////////////////////
        // test recoding of Seattle to Washington
        record.put(RuralUrbanUtils.PROP_STATE_DX, "SE");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "001");
        String result1993 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993();
        String result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        String result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        record.put(RuralUrbanUtils.PROP_STATE_DX, "WA");
        Assert.assertEquals(result1993, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());

        // test recoding of Los Angeles to California
        record.put(RuralUrbanUtils.PROP_STATE_DX, "LO");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "001");
        result1993 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993();
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        record.put(RuralUrbanUtils.PROP_STATE_DX, "CA");
        Assert.assertEquals(result1993, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());

        // test recoding of Greater Bay to California
        record.put(RuralUrbanUtils.PROP_STATE_DX, "GB");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "001");
        result1993 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993();
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        record.put(RuralUrbanUtils.PROP_STATE_DX, "CA");
        Assert.assertEquals(result1993, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());

        // test recoding of Atlanta to Georgia
        record.put(RuralUrbanUtils.PROP_STATE_DX, "AT");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "001");
        result1993 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993();
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        record.put(RuralUrbanUtils.PROP_STATE_DX, "GA");
        Assert.assertEquals(result1993, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());

        // test recoding of Detroit to Michigan
        record.put(RuralUrbanUtils.PROP_STATE_DX, "DT");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "001");
        result1993 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993();
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        record.put(RuralUrbanUtils.PROP_STATE_DX, "MI");
        Assert.assertEquals(result1993, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013());

        //////////////////////////////////////////////////////////////////////////////////////////////
        // TESTS FOR LOOKING IN THE PRIOR YEAR TABLE WHEN THE VALUE YOU ARE LOOKING FOR ISN'T FOUND
        //////////////////////////////////////////////////////////////////////////////////////////////
        // test for Clifton Forge, VA
        record.put(RuralUrbanUtils.PROP_STATE_DX, "VA");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "560");
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        Assert.assertEquals(result2013, result2003);

        // test for Miscellaneous AK counties
        record.put(RuralUrbanUtils.PROP_STATE_DX, "AK");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "201");
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        Assert.assertEquals(result2013, result2003);
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "232");
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        Assert.assertEquals(result2013, result2003);
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "280");
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        result2013 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2013();
        Assert.assertEquals(result2013, result2003);

        //////////////////////////////////////////////////////////////////////////////////////////////
        // TEST KALAWAO, HAWAII - 2003
        //////////////////////////////////////////////////////////////////////////////////////////////
        record.put(RuralUrbanUtils.PROP_STATE_DX, "HI");
        record.put(RuralUrbanUtils.PROP_COUNTY_DX, "005");
        result2003 = RuralUrbanUtils.computeRuralUrbanContinuum(record).getRuralUrbanContinuum2003();
        Assert.assertEquals("05", result2003);
    }
}
