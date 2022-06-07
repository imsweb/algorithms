/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ephtsubcounty;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class EphtSubCountyUtilsTest {

    private static final String _PROP_STATE_DX = "addressAtDxState";
    private static final String _PROP_COUNTY_DX_ANALYSIS = "countyAtDxAnalysis";
    private static final String _PROP_CENSUS_TRACT_2010 = "censusTract2010";

    @Test
    public void testComputeEphtSubCounty() {
        Map<String, String> record = new HashMap<>();

        // test invalid state
        record.put(_PROP_STATE_DX, "AA");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "039");
        Assert.assertEquals("A", computeEphtSubCounty(record).getEpht2010GeoId5k());
        // test invalid county
        record.put(_PROP_STATE_DX, "WY");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "abc");
        Assert.assertEquals("A", computeEphtSubCounty(record).getEpht2010GeoId5k());
        // test county not reported
        record.put(_PROP_COUNTY_DX_ANALYSIS, "000");
        record.put(_PROP_CENSUS_TRACT_2010, "997600");
        Assert.assertEquals("B", computeEphtSubCounty(record).getEpht2010GeoId5k());
        //test missing census tract
        record.put(_PROP_COUNTY_DX_ANALYSIS, "039");
        record.put(_PROP_CENSUS_TRACT_2010, null);
        Assert.assertEquals("D", computeEphtSubCounty(record).getEpht2010GeoId5k());
        // test unknown census tract
        record.put(_PROP_CENSUS_TRACT_2010, "999999");
        Assert.assertEquals("D", computeEphtSubCounty(record).getEpht2010GeoId5k());
        //test combination of state, county and census 2010 not in the lookup
        record.put(_PROP_CENSUS_TRACT_2010, "997600");
        Assert.assertEquals("C", computeEphtSubCounty(record).getEpht2010GeoId5k());
        //test combination of state, county and census 2010 in the lookup
        record.put(_PROP_CENSUS_TRACT_2010, "967702");
        Assert.assertEquals("00005603955", computeEphtSubCounty(record).getEpht2010GeoId5k());
        
        //test combinations of state, county and census 2010 in the lookup
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        record.put(_PROP_CENSUS_TRACT_2010, "020900");
        Assert.assertEquals("01001020900", computeEphtSubCounty(record).getEpht2010GeoId5k());
        record.clear();
        record.put(_PROP_STATE_DX, "WY");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "039");
        record.put(_PROP_CENSUS_TRACT_2010, "967702");
        Assert.assertEquals("00005603955", computeEphtSubCounty(record).getEpht2010GeoId5k());
        Assert.assertEquals("00005603926", computeEphtSubCounty(record).getEpht2010GeoId20k());
        record.clear();
        record.put(_PROP_STATE_DX, "HI");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "003");
        record.put(_PROP_CENSUS_TRACT_2010, "003405");
        Assert.assertEquals("00001500392", computeEphtSubCounty(record).getEpht2010GeoId5k());
        Assert.assertEquals("00015003106", computeEphtSubCounty(record).getEpht2010GeoId20k());
        record.clear();
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "003");
        record.put(_PROP_CENSUS_TRACT_2010, "990000");
        Assert.assertEquals("C", computeEphtSubCounty(record).getEpht2010GeoId5k()); // used to be 99999999999 in old data, became C when we switched to big SEER census data file
        Assert.assertEquals("C", computeEphtSubCounty(record).getEpht2010GeoId20k());
    }

    private EphtSubCountyOutputDto computeEphtSubCounty(Map<String, String> record) {
        EphtSubCountyInputDto input = new EphtSubCountyInputDto();
        input.setAddressAtDxState(record.get(_PROP_STATE_DX));
        input.setCountyAtDxAnalysis(record.get(_PROP_COUNTY_DX_ANALYSIS));
        input.setCensusTract2010(record.get(_PROP_CENSUS_TRACT_2010));

        return EphtSubCountyUtils.computeEphtSubCounty(input);
    }
}
