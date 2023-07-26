/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.countyatdiagnosisanalysis;

import org.junit.Assert;
import org.junit.Test;

public class CountyAtDxAnalysisUtilsTest {

    @Test
    public void testComputeCountyAtDiagnosis() {

        CountyAtDxAnalysisInputDto input = new CountyAtDxAnalysisInputDto();

        input.setDateOfDiagnosis("20150101");
        input.setAddrAtDxState("MD");
        input.setCountyAtDx("005");
        input.setCountyAtDxGeocode1990("001");
        input.setCountyAtDxGeocode2000("003");
        input.setCountyAtDxGeocode2010("005");
        input.setCountyAtDxGeocode2020("009");
        input.setCensusTrCert19708090("1");
        input.setCensusTrCertainty2000("1");
        input.setCensusTrCertainty2010("1");
        input.setCensusTrCertainty2020("1");

        // Verify that a valid input yields expected results.
        // If reported and geocoder values agree, use reported value
        CountyAtDxAnalysisOutputDto output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_REP_GEO_EQUAL, output.getCountyAtDxAnalysisFlag());

        // If diagnosisYear is invalid, return 999/10
        input.setDateOfDiagnosis(null);
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("        ");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("18");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("YYYYMMDD");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("20150101");

        // If state is invalid, return 999/10
        input.setAddrAtDxState(null);
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setAddrAtDxState("  ");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        // If state is Canadian, return 998/9
        input.setAddrAtDxState("AB");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.CANADIAN_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_CANADIAN_STATE, output.getCountyAtDxAnalysisFlag());

        input.setAddrAtDxState("MD");

        // Verify that the geocoder info is correct for given diagnosisYear
        // Also verify if geocoder value has non blank/9 certainty and county is blank/9, use geocoder value
        input.setCountyAtDx(null);
        input.setDateOfDiagnosis("19950101");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("001", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("20050101");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("003", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("20150101");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("20230101");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("009", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        // Verify that if both reported and geocoder values are blank/9, return 999/10.1
        input.setCountyAtDxGeocode2020(null);
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDxAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.OTHER_REP_AND_GEO_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDx("005");
        input.setDateOfDiagnosis("20150101");
        input.setCountyAtDxGeocode2010(null);

        // Verify that if geocoder county is blank/9, reported value is used
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_GEO_BLANK_OR_UNK, output.getCountyAtDxAnalysisFlag());

        // Verify that if the geocoder county code is invalid, reported county is used
        input.setCountyAtDxGeocode2010("00123");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_GEO_INVALID_FOR_STATE, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDxGeocode2010("005");

        // Verify that if geocoder certainty is 1 or 6 and reported county is not blank/9, use geocoder value
        input.setCountyAtDx("24001");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_1_OR_6, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("6");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_1_OR_6, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDx("005");

        // Verify that if geocoder certainty is 5 and reported county is not blank/9, use reported value
        input.setCensusTrCertainty2010("5");
        input.setCountyAtDxGeocode2010("001");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_CERT_5, output.getCountyAtDxAnalysisFlag());

        // Verify that if geocoder certainty is 2, 3, or 4 and reported county is not blank/9, use reported value
        input.setCensusTrCertainty2010("2");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("3");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("4");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        // Verify that if geocoder certainty is blank/9 and reported value is not blank/9, use reported value
        input.setCensusTrCertainty2010(null);
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_CERT_UNK, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("9");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_CERT_UNK, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDxGeocode2010("005");

        // Verify that if geocoder certainty is blank/9 and reported value is also blank/9, use geocoder value
        input.setCountyAtDx(null);
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_UNK_REP_UNK, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDx("999");
        output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDxAnalysisUtils.GEO_CERT_UNK_REP_UNK, output.getCountyAtDxAnalysisFlag());
    }

    @Test
    public void testIsBlankOrNineFilled() {
        Assert.assertTrue(CountyAtDxAnalysisUtils.isBlankOrNineFilled(""));
        Assert.assertTrue(CountyAtDxAnalysisUtils.isBlankOrNineFilled(" "));
        Assert.assertTrue(CountyAtDxAnalysisUtils.isBlankOrNineFilled("     "));
        Assert.assertTrue(CountyAtDxAnalysisUtils.isBlankOrNineFilled("\t"));
        Assert.assertTrue(CountyAtDxAnalysisUtils.isBlankOrNineFilled("9"));
        Assert.assertTrue(CountyAtDxAnalysisUtils.isBlankOrNineFilled("99999"));
        Assert.assertTrue(CountyAtDxAnalysisUtils.isBlankOrNineFilled(null));

        Assert.assertFalse(CountyAtDxAnalysisUtils.isBlankOrNineFilled("0"));
        Assert.assertFalse(CountyAtDxAnalysisUtils.isBlankOrNineFilled(" 9"));
        Assert.assertFalse(CountyAtDxAnalysisUtils.isBlankOrNineFilled(" 0"));
        Assert.assertFalse(CountyAtDxAnalysisUtils.isBlankOrNineFilled("09"));
    }
}
