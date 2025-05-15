/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CensusTractPovertyIndicatorUtilsTest {

    @Test
    public void testDeriveValueFromPercentage() {
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage(null));
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage(""));
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("  "));
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("xxxx"));

        Assert.assertEquals("1", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("0.00"));
        Assert.assertEquals("1", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("3.20"));
        Assert.assertEquals("1", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("4.99"));
        Assert.assertEquals("2", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("5.00"));
        Assert.assertEquals("2", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("5.01"));
        Assert.assertEquals("2", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("7.5"));
        Assert.assertEquals("2", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("9.99"));
        Assert.assertEquals("3", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("15.00"));
        Assert.assertEquals("4", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("25.00"));
        Assert.assertEquals("4", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("99.99"));
        Assert.assertEquals("4", CensusTractPovertyIndicatorUtils.deriveValueFromPercentage("100.00"));
    }

    @Test
    public void testComputePovertyIndicator() {

        Map<String, String> rec = new HashMap<>();
        rec.put("addressAtDxState", "WY");
        rec.put("countyAtDxAnalysis", "039");
        rec.put("censusTract2000", "997600");

        //test not enough information
        Assert.assertEquals("9", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test non-integer diagnosis year
        rec.put("dateOfDiagnosisYear", "xxxx");
        Assert.assertEquals("9", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test diagnosis year before 95 
        rec.put("dateOfDiagnosisYear", "1994");
        Assert.assertEquals("9", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test diagnosis year after 2011
        rec.put("dateOfDiagnosisYear", "2012");
        Assert.assertEquals("9", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 1
        rec.put("dateOfDiagnosisYear", "1999");
        Assert.assertEquals("2", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 2
        rec.put("dateOfDiagnosisYear", "2005");
        Assert.assertEquals("1", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 3
        rec.put("dateOfDiagnosisYear", "2008");
        Assert.assertEquals("9", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 3 with a combination of state, county and census 2010 not in the lookup
        rec.put("censusTract2010", "997600");
        Assert.assertEquals("9", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 3 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "967702");
        Assert.assertEquals("3", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 4 with a combination of state, county and census 2010 in the lookup
        rec.put("addressAtDxState", "AL");
        rec.put("countyAtDxAnalysis", "001");
        rec.put("censusTract2010", "020900");
        rec.put("dateOfDiagnosisYear", "2009");
        Assert.assertEquals("2", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 5 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "020900");
        rec.put("dateOfDiagnosisYear", "2010");
        Assert.assertEquals("3", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 6 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "020900");
        rec.put("dateOfDiagnosisYear", "2011");
        Assert.assertEquals("2", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 7 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "020900");
        rec.put("dateOfDiagnosisYear", "2012");
        Assert.assertEquals("3", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 8 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "020700");
        rec.put("dateOfDiagnosisYear", "2013");
        Assert.assertEquals("4", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 9 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "020700");
        rec.put("dateOfDiagnosisYear", "2014");
        Assert.assertEquals("3", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 10 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "020600");
        rec.put("dateOfDiagnosisYear", "2015");
        Assert.assertEquals("3", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
        //test year category 11 with a combination of state, county and census 2010 in the lookup
        rec.put("censusTract2010", "020600");
        rec.put("dateOfDiagnosisYear", "2016");
        Assert.assertEquals("4", computePovertyIndicator(rec).getCensusTractPovertyIndicator());

        //test 2019+ years
        rec.clear();
        rec.put("addressAtDxState", "WY");
        rec.put("countyAtDxAnalysis", "039");
        rec.put("censusTract2010", "967702");
        rec.put("dateOfDiagnosisYear", "2019");
        Assert.assertEquals("9", computePovertyIndicator(rec, false).getCensusTractPovertyIndicator());
        Assert.assertEquals("2", computePovertyIndicator(rec, true).getCensusTractPovertyIndicator());

        //test unknown year
        rec.clear();
        rec.put("addressAtDxState", "WY");
        rec.put("countyAtDxAnalysis", "039");
        rec.put("censusTract2010", "967702");
        rec.put("dateOfDiagnosisYear", "999");
        Assert.assertEquals("9", computePovertyIndicator(rec, false).getCensusTractPovertyIndicator());
        Assert.assertEquals("9", computePovertyIndicator(rec, true).getCensusTractPovertyIndicator());

        // test a real case from DMS (this case failed when we generated unique keys from the concatenated state/county/census)
        rec.clear();
        rec.put("addressAtDxState", "HI");
        rec.put("countyAtDxAnalysis", "003");
        rec.put("censusTract2000", "003405");
        rec.put("dateOfDiagnosisYear", "2007");
        Assert.assertEquals("3", computePovertyIndicator(rec).getCensusTractPovertyIndicator());
    }

    private CensusTractPovertyIndicatorOutputDto computePovertyIndicator(Map<String, String> rec) {
        return computePovertyIndicator(rec, true);
    }

    private CensusTractPovertyIndicatorOutputDto computePovertyIndicator(Map<String, String> rec, boolean includeRecentYears) {

        CensusTractPovertyIndicatorInputDto input = new CensusTractPovertyIndicatorInputDto();
        input.setAddressAtDxState(rec.get("addressAtDxState"));
        input.setCountyAtDxAnalysis(rec.get("countyAtDxAnalysis"));
        input.setDateOfDiagnosisYear(rec.get("dateOfDiagnosisYear"));
        input.setCensusTract2000(rec.get("censusTract2000"));
        input.setCensusTract2010(rec.get("censusTract2010"));

        return CensusTractPovertyIndicatorUtils.computePovertyIndicator(input, includeRecentYears);
    }
}
