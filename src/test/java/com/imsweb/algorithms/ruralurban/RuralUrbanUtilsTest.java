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

import com.imsweb.algorithms.StateCountyTractInputDto;

public class RuralUrbanUtilsTest {

    // these properties have been deprecated in the main class but so many tests use them that it was easier to copy them here
    private static final String _PROP_STATE_DX = "addressAtDxState";
    private static final String _PROP_COUNTY_DX_ANALYSIS = "countyAtDxAnalysis";
    private static final String _PROP_CENSUS_TRACT_2000 = "censusTract2000";
    private static final String _PROP_CENSUS_TRACT_2010 = "censusTract2010";

    @Test
    public void assertInfo() {
        Assert.assertNotNull(RuralUrbanUtils.ALG_VERSION);
        Assert.assertNotNull(RuralUrbanUtils.ALG_NAME);
    }

    @Test
    public void testCompute() {

        Map<String, String> rec = new HashMap<>();

        List<String> states = Arrays.asList("AL", "MP", "--", null, "", "ZZ");
        List<String> counties = Arrays.asList("003", "777", "---", null, "", "000", "999");
        List<String> tracts = Arrays.asList("021100", "123456", "000001", null, "", "999999");

        for (String state : states) {
            for (String county : counties) {
                rec.put(_PROP_STATE_DX, state);
                rec.put(_PROP_COUNTY_DX_ANALYSIS, county);

                if ("--".equals(state) || "---".equals(county)) {
                    Assert.assertEquals("96", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
                    Assert.assertEquals("96", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
                    Assert.assertEquals("96", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());
                }
                else if (state == null || state.isEmpty() || state.equals("ZZ") || county == null || county.isEmpty() || county.equals("999")) {
                    Assert.assertEquals("99", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
                    Assert.assertEquals("99", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
                    Assert.assertEquals("99", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());
                }
                else if (county.equals("000")) {
                    Assert.assertEquals("97", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
                    Assert.assertEquals("97", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
                    Assert.assertEquals("97", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());
                }
                else if (state.equals("MP") || county.equals("777")) {
                    Assert.assertEquals("98", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
                    Assert.assertEquals("98", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
                    Assert.assertEquals("98", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());
                }
                else {
                    Assert.assertEquals("02", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
                    Assert.assertEquals("04", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
                    Assert.assertEquals("03", computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());
                }

                for (String tract : tracts) {
                    rec.put(_PROP_CENSUS_TRACT_2000, tract);
                    rec.put(_PROP_CENSUS_TRACT_2010, tract);

                    if ("--".equals(state) || "---".equals(county) || "000001".equals(tract)) {
                        Assert.assertEquals("A", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("A", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("A", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("A", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2010());
                    }
                    else if (state == null || state.isEmpty() || state.equals("ZZ") || county == null || county.isEmpty() || county.equals("999") || tract == null || tract.isEmpty() || tract.equals(
                            "999999")) {
                        Assert.assertEquals("D", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("D", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("D", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("D", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2010());
                    }
                    else if (county.equals("000")) {
                        Assert.assertEquals("B", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("B", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("B", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("B", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2010());
                    }
                    else if (state.equals("MP") || county.equals("777") || tract.equals("123456")) {
                        Assert.assertEquals("C", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2000());
                        Assert.assertEquals("C", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("C", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("C", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2010());
                    }
                    else {
                        rec.put(_PROP_COUNTY_DX_ANALYSIS, "001");
                        rec.put(_PROP_CENSUS_TRACT_2000, "020800");
                        Assert.assertEquals("3", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2000());
                        rec.put(_PROP_CENSUS_TRACT_2000, tract);
                        Assert.assertEquals("4", computeUrbanRuralIndicatorCode(rec).getUrbanRuralIndicatorCode2010());
                        Assert.assertEquals("2", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2000());
                        Assert.assertEquals("1", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2010());
                        rec.put(_PROP_STATE_DX, "WY");
                        rec.put(_PROP_COUNTY_DX_ANALYSIS, "041");
                        rec.put(_PROP_CENSUS_TRACT_2010, "975200");
                        Assert.assertEquals("2", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2010());
                        rec.put(_PROP_STATE_DX, "WA");
                        rec.put(_PROP_COUNTY_DX_ANALYSIS, "067");
                        rec.put(_PROP_CENSUS_TRACT_2010, "010200");
                        Assert.assertEquals("1", computeRuralUrbanCommutingArea(rec).getRuralUrbanCommutingArea2010());
                    }
                }
            }
        }

        ////////////////////////////////////////////////////////////
        // TESTS FOR SEER CITY SPECIAL CASES
        ////////////////////////////////////////////////////////////
        // test recoding of Seattle to Washington
        rec.put(_PROP_STATE_DX, "SE");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        String result1993 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993();
        String result2003 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003();
        String result2013 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013();
        rec.put(_PROP_STATE_DX, "WA");
        Assert.assertEquals(result1993, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());

        // test recoding of Los Angeles to California
        rec.put(_PROP_STATE_DX, "LO");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        result1993 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993();
        result2003 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003();
        result2013 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013();
        rec.put(_PROP_STATE_DX, "CA");
        Assert.assertEquals(result1993, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());

        // test recoding of Greater Bay to California
        rec.put(_PROP_STATE_DX, "GB");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        result1993 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993();
        result2003 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003();
        result2013 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013();
        rec.put(_PROP_STATE_DX, "CA");
        Assert.assertEquals(result1993, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());

        // test recoding of Atlanta to Georgia
        rec.put(_PROP_STATE_DX, "AT");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        result1993 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993();
        result2003 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003();
        result2013 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013();
        rec.put(_PROP_STATE_DX, "GA");
        Assert.assertEquals(result1993, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());

        // test recoding of Detroit to Michigan
        rec.put(_PROP_STATE_DX, "DT");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        result1993 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993();
        result2003 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003();
        result2013 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013();
        rec.put(_PROP_STATE_DX, "MI");
        Assert.assertEquals(result1993, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum1993());
        Assert.assertEquals(result2003, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003());
        Assert.assertEquals(result2013, computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2013());

        //////////////////////////////////////////////////////////////////////////////////////////////
        // TEST KALAWAO, HAWAII - 2003
        //////////////////////////////////////////////////////////////////////////////////////////////
        rec.put(_PROP_STATE_DX, "HI");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "005");
        result2003 = computeRuralUrbanContinuum(rec).getRuralUrbanContinuum2003();
        Assert.assertEquals("05", result2003);
    }

    // using a map of properties has been deprecated, but so many calls are used in this unit test that it was easier to reproduce the deprecated method here...
    private RuralUrbanOutputDto computeUrbanRuralIndicatorCode(Map<String, String> rec) {
        StateCountyTractInputDto input = new StateCountyTractInputDto();
        input.setAddressAtDxState(rec.get(_PROP_STATE_DX));
        input.setCountyAtDxAnalysis(rec.get(_PROP_COUNTY_DX_ANALYSIS));
        input.setCensusTract2000(rec.get(_PROP_CENSUS_TRACT_2000));
        input.setCensusTract2010(rec.get(_PROP_CENSUS_TRACT_2010));
        return RuralUrbanUtils.computeUrbanRuralIndicatorCode(input);
    }

    // using a map of properties has been deprecated, but so many calls are used in this unit test that it was easier to reproduce the deprecated method here...
    private RuralUrbanOutputDto computeRuralUrbanCommutingArea(Map<String, String> rec) {
        StateCountyTractInputDto input = new StateCountyTractInputDto();
        input.setAddressAtDxState(rec.get(_PROP_STATE_DX));
        input.setCountyAtDxAnalysis(rec.get(_PROP_COUNTY_DX_ANALYSIS));
        input.setCensusTract2000(rec.get(_PROP_CENSUS_TRACT_2000));
        input.setCensusTract2010(rec.get(_PROP_CENSUS_TRACT_2010));
        return RuralUrbanUtils.computeRuralUrbanCommutingArea(input);
    }

    // using a map of properties has been deprecated, but so many calls are used in this unit test that it was easier to reproduce the deprecated method here...
    private RuralUrbanOutputDto computeRuralUrbanContinuum(Map<String, String> rec) {
        StateCountyTractInputDto input = new StateCountyTractInputDto();
        input.setAddressAtDxState(rec.get(_PROP_STATE_DX));
        input.setCountyAtDxAnalysis(rec.get(_PROP_COUNTY_DX_ANALYSIS));
        return RuralUrbanUtils.computeRuralUrbanContinuum(input);
    }
}
