/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.StateCountyTractInputDto;

public class TractEstCongressDistUtilsTest {

    // this properties have been deprecated in the main class but so many tests use them that it was easier to copy them here
    private static final String _PROP_STATE_DX = "addressAtDxState";
    private static final String _PROP_COUNTY_DX_ANALYSIS = "countyAtDxAnalysis";
    private static final String _PROP_CENSUS_TRACT_2010 = "censusTract2010";

    @Test
    public void assertInfo() {
        Assert.assertNotNull(TractEstCongressDistUtils.ALG_VERSION);
        Assert.assertNotNull(TractEstCongressDistUtils.ALG_NAME);
    }

    @Test
    public void testComputeTractEstCongressDist() {

        Map<String, String> record = new HashMap<>();
        record.put(_PROP_STATE_DX, "WY");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "039");
        record.put(_PROP_CENSUS_TRACT_2010, "997600");

        //test unknown C (not found)
        Assert.assertEquals("C", computeTractEstCongressDist(record).getTractEstCongressDist());
        
        // test unknown A (invalid value)
        record.put(_PROP_STATE_DX, "QQ");
        Assert.assertEquals("A", computeTractEstCongressDist(record).getTractEstCongressDist());
        record.put(_PROP_STATE_DX, "WY");
        
        // test unknown B (county not reported)
        record.put(_PROP_COUNTY_DX_ANALYSIS, "000");
        Assert.assertEquals("B", computeTractEstCongressDist(record).getTractEstCongressDist());
        
        // test unknown D (missing value)
        record.put(_PROP_COUNTY_DX_ANALYSIS, null);
        Assert.assertEquals("D", computeTractEstCongressDist(record).getTractEstCongressDist());

        //test PR
        record.clear();
        record.put(_PROP_STATE_DX, "PR");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        record.put(_PROP_CENSUS_TRACT_2010, "956300");
        Assert.assertEquals("98", computeTractEstCongressDist(record).getTractEstCongressDist());
        record.put(_PROP_CENSUS_TRACT_2010, "555555");
        Assert.assertEquals("C", computeTractEstCongressDist(record).getTractEstCongressDist());

        //test 2019+ years
        record.clear();
        record.put(_PROP_STATE_DX, "WY");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "039");
        record.put(_PROP_CENSUS_TRACT_2010, "967702");
        Assert.assertEquals("00", computeTractEstCongressDist(record).getTractEstCongressDist());

        // test a real case from DMS (this case failed when we generated unique keys from the concatenated state/county/census)
        record.clear();
        record.put(_PROP_STATE_DX, "HI");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "003");
        record.put(_PROP_CENSUS_TRACT_2010, "003405");
        Assert.assertEquals("01", computeTractEstCongressDist(record).getTractEstCongressDist());
    }

    private TractEstCongressDistOutputDto computeTractEstCongressDist(Map<String, String> record) {

        StateCountyTractInputDto input = new StateCountyTractInputDto();
        input.setAddressAtDxState(record.get(_PROP_STATE_DX));
        input.setCountyAtDxAnalysis(record.get(_PROP_COUNTY_DX_ANALYSIS));
        input.setCensusTract2010(record.get(_PROP_CENSUS_TRACT_2010));

        return TractEstCongressDistUtils.computeTractEstCongressDist(input);
    }
}
