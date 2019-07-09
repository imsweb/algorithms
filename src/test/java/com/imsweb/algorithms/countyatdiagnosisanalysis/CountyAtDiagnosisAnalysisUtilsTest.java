/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.countyatdiagnosisanalysis;

import org.junit.Assert;
import org.junit.Test;

public class CountyAtDiagnosisAnalysisUtilsTest {

    @Test
    public void testComputeCountyAtDiagnosis() {

        CountyAtDiagnosisAnalysisInputDto input = new CountyAtDiagnosisAnalysisInputDto();

        input.setDateOfDiagnosis("20150101");
        input.setAddrAtDxState("MD");
        input.setCountyAtDx("005");
        input.setCountyAtDxGeocode1990("001");
        input.setCountyAtDxGeocode2000("003");
        input.setCountyAtDxGeocode2010("005");
        input.setCountyAtDxGeocode2020("007");
        input.setStateAtDxGeocode19708090("MD");
        input.setStateAtDxGeocode2000("MD");
        input.setStateAtDxGeocode2010("MD");
        input.setStateAtDxGeocode2020("MD");
        input.setCensusTrCert19708090("1");
        input.setCensusTrCertainty2000("1");
        input.setCensusTrCertainty2010("1");
        input.setCensusTrCertainty2020("1");

        // Verify that a valid input yields expected results.
        // If reported and geocoder values agree, use reported value
        CountyAtDiagnosisAnalysisOutputDto output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_REP_GEO_EQUAL, output.getCountyAtDxAnalysisFlag());

        // If diagnosisYear is invalid, return 999/10
        input.setDateOfDiagnosis(null);
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("        ");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("18");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("YYYYMMDD");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("20150101");

        // If state is invalid, return 999/10
        input.setAddrAtDxState(null);
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setAddrAtDxState("  ");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        // If state is Canadian, return 998/9
        input.setAddrAtDxState("AB");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.CANADIAN_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_CANADIAN_STATE, output.getCountyAtDxAnalysisFlag());

        input.setAddrAtDxState("MD");

        // Verify that the geocoder info is correct for given diagnosisYear
        // Also verify if geocoder value has non blank/9 certainty and county is blank/9, use geocoder value
        input.setCountyAtDx(null);
        input.setDateOfDiagnosis("19950101");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("001", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("20050101");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("003", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        input.setDateOfDiagnosis("20150101");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        // Verify that if both reported and geocoder values are blank/9, return 999/10.1
        input.setCountyAtDxGeocode2010(null);
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_REP_AND_GEO_BLANK, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDx("005");

        // Verify that if geocoder county is blank/9, reported value is used
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_GEO_BLANK_OR_UNK, output.getCountyAtDxAnalysisFlag());

        // Verify that if the geocoder county code is invalid, reported county is used
        input.setCountyAtDxGeocode2010("00123");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_GEO_INVALID_FOR_STATE, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDxGeocode2010("005");

        // Verify that if geocoder certainty is 1 or 6 and reported county is not blank/9, use geocoder value
        input.setCountyAtDx("24001");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_1_OR_6, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("6");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_1_OR_6, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDx("005");

        // Verify that if geocoder certainty is 5 and reported county is not blank/9, use reported value
        input.setCensusTrCertainty2010("5");
        input.setCountyAtDxGeocode2010("001");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_5, output.getCountyAtDxAnalysisFlag());

        // Verify that if geocoder certainty is 2, 3, or 4 and reported county is not blank/9, use reported value
        input.setCensusTrCertainty2010("2");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("3");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("4");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        // Verify that if geocoder certainty is blank/9 and reported value is not blank/9, use reported value
        input.setCensusTrCertainty2010(null);
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_UNK, output.getCountyAtDxAnalysisFlag());

        input.setCensusTrCertainty2010("9");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_UNK, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDxGeocode2010("005");

        // Verify that if geocoder certainty is blank/9 and reported value is also blank/9, use geocoder value
        input.setCountyAtDx(null);
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_UNK_REP_UNK, output.getCountyAtDxAnalysisFlag());

        input.setCountyAtDx("999");
        output = CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
        Assert.assertEquals("005", output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_UNK_REP_UNK, output.getCountyAtDxAnalysisFlag());
    }

    @Test
    public void testIsBlankOrNineFilled() {
        Assert.assertTrue(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled(""));
        Assert.assertTrue(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled(" "));
        Assert.assertTrue(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled("     "));
        Assert.assertTrue(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled("\t"));
        Assert.assertTrue(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled("9"));
        Assert.assertTrue(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled("99999"));
        Assert.assertTrue(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled(null));

        Assert.assertFalse(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled("0"));
        Assert.assertFalse(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled(" 9"));
        Assert.assertFalse(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled(" 0"));
        Assert.assertFalse(CountyAtDiagnosisAnalysisUtils.isBlankOrNineFilled("09"));
    }
}
