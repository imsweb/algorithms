/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.countyatdiagnosisanalysis;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.Algorithms;

public class CountyAtDiagnosisAnalysisUtilsTest {

    private static final String _TESTING_DX_DATE = "20150101";
    private static final String _TESTING_STATE_DX = "MD";
    private static final String _TESTING_COUNTY_DX = "005";
    private static final String _TESTING_COUNTY_AT_DX_GEOCODE_1990 = "001";
    private static final String _TESTING_COUNTY_AT_DX_GEOCODE_2000 = "003";
    private static final String _TESTING_COUNTY_AT_DX_GEOCODE_2010 = "005";
    private static final String _TESTING_COUNTY_AT_DX_GEOCODE_2020 = "007";
    private static final String _TESTING_STATE_AT_DX_GEOCODE_19708090 = "MD";
    private static final String _TESTING_STATE_AT_DX_GEOCODE_2000 = "MD";
    private static final String _TESTING_STATE_AT_DX_GEOCODE_2010 = "MD";
    private static final String _TESTING_STATE_AT_DX_GEOCODE_2020 = "MD";
    private static final String _TESTING_CENSUS_CERTAINTY_708090 = "1";
    private static final String _TESTING_CENSUS_CERTAINTY_2000 = "1";
    private static final String _TESTING_CENSUS_CERTAINTY_2010 = "1";
    private static final String _TESTING_CENSUS_CERTAINTY_2020 = "1";

    @Test
    public void testComputeCountyAtDiagnosis() {

        Map<String, String> record = new HashMap<>();
        record.put(Algorithms.FIELD_DX_DATE, _TESTING_DX_DATE);
        record.put(Algorithms.FIELD_STATE_DX, _TESTING_STATE_DX);
        record.put(Algorithms.FIELD_COUNTY_DX, _TESTING_COUNTY_DX);
        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_1990, _TESTING_COUNTY_AT_DX_GEOCODE_1990);
        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2000, _TESTING_COUNTY_AT_DX_GEOCODE_2000);
        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010, _TESTING_COUNTY_AT_DX_GEOCODE_2010);
        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2020, _TESTING_COUNTY_AT_DX_GEOCODE_2020);
        record.put(Algorithms.FIELD_STATE_AT_DX_GEOCODE_19708090, _TESTING_STATE_AT_DX_GEOCODE_19708090);
        record.put(Algorithms.FIELD_STATE_AT_DX_GEOCODE_2000, _TESTING_STATE_AT_DX_GEOCODE_2000);
        record.put(Algorithms.FIELD_STATE_AT_DX_GEOCODE_2010, _TESTING_STATE_AT_DX_GEOCODE_2010);
        record.put(Algorithms.FIELD_STATE_AT_DX_GEOCODE_2020, _TESTING_STATE_AT_DX_GEOCODE_2020);
        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_708090, _TESTING_CENSUS_CERTAINTY_708090);
        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2000, _TESTING_CENSUS_CERTAINTY_2000);
        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, _TESTING_CENSUS_CERTAINTY_2010);
        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2020, _TESTING_CENSUS_CERTAINTY_2020);

        // Verify that a valid input yields expected results.
        // If reported and geocoder values agree, use reported value
        CountyAtDiagnosisAnalysisOutputDto output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_REP_GEO_EQUAL, output.getCountyAtDxAnalysisFlag());

        // If diagnosisYear is invalid, return 999/10
        record.put(Algorithms.FIELD_DX_DATE, null);
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_DX_DATE, "        ");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_DX_DATE, "18");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_DX_DATE, "YYYYMMDD");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_DX_DATE, _TESTING_DX_DATE);

        // If state is invalid, return 999/10
        record.put(Algorithms.FIELD_STATE_DX, null);
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_STATE_DX, "  ");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_STATE_OR_DX_YEAR_BLANK, output.getCountyAtDxAnalysisFlag());

        // If state is Canadian, return 998/9
        record.put(Algorithms.FIELD_STATE_DX, "AB");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.CANADIAN_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_CANADIAN_STATE, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_STATE_DX, _TESTING_STATE_DX);

        // Verify that the geocoder info is correct for given diagnosisYear
        // Also verify if geocoder value has non blank/9 certainty and county is blank/9, use geocoder value
        record.put(Algorithms.FIELD_COUNTY_DX, null);
        record.put(Algorithms.FIELD_DX_DATE, "19950101");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_AT_DX_GEOCODE_1990, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_DX_DATE, "20050101");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_AT_DX_GEOCODE_2000, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_DX_DATE, _TESTING_DX_DATE);
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_AT_DX_GEOCODE_2010, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_KNOWN_REP_UNK, output.getCountyAtDxAnalysisFlag());

        // Verify that if both reported and geocoder values are blank/9, return 999/10.1
        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010, null);
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.INVALID_COUNTY_CODE, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.OTHER_REP_AND_GEO_BLANK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_COUNTY_DX, _TESTING_COUNTY_DX);

        // Verify that if geocoder county is blank/9, reported value is used
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_GEO_BLANK_OR_UNK, output.getCountyAtDxAnalysisFlag());

        // Verify that if the geocoder county code is invalid, reported county is used
        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010, "00123");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_GEO_INVALID_FOR_STATE, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010, _TESTING_COUNTY_AT_DX_GEOCODE_2010);

        // Verify that if geocoder certainty is 1 or 6 and reported county is not blank/9, use geocoder value
        record.put(Algorithms.FIELD_COUNTY_DX, "24001");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_AT_DX_GEOCODE_2010, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_1_OR_6, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, "6");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_AT_DX_GEOCODE_2010, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_1_OR_6, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_COUNTY_DX, _TESTING_COUNTY_DX);

        // Verify that if geocoder certainty is 5 and reported county is not blank/9, use reported value
        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, "5");
        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010, "001");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_5, output.getCountyAtDxAnalysisFlag());

        // Verify that if geocoder certainty is 2, 3, or 4 and reported county is not blank/9, use reported value
        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, "2");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, "3");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, "4");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_2_3_OR_4, output.getCountyAtDxAnalysisFlag());

        // Verify that if geocoder certainty is blank/9 and reported value is not blank/9, use reported value
        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, null);
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_UNK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, "9");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_DX, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.REP_CERT_UNK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010, _TESTING_COUNTY_AT_DX_GEOCODE_2010);

        // Verify that if geocoder certainty is blank/9 and reported value is also blank/9, use geocoder value
        record.put(Algorithms.FIELD_COUNTY_DX, null);
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_AT_DX_GEOCODE_2010, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_UNK_REP_UNK, output.getCountyAtDxAnalysisFlag());

        record.put(Algorithms.FIELD_COUNTY_DX, "999");
        output = computeCountyAtDiagnosisAnalysis(record);
        Assert.assertEquals(_TESTING_COUNTY_AT_DX_GEOCODE_2010, output.getCountyAtDxAnalysis());
        Assert.assertEquals(CountyAtDiagnosisAnalysisUtils.GEO_CERT_UNK_REP_UNK, output.getCountyAtDxAnalysisFlag());
    }

    private CountyAtDiagnosisAnalysisOutputDto computeCountyAtDiagnosisAnalysis(Map<String, String> record) {
        CountyAtDiagnosisAnalysisInputDto input = new CountyAtDiagnosisAnalysisInputDto();

        input.setDateOfDiagnosis(record.get(Algorithms.FIELD_DX_DATE));
        input.setAddrAtDxState(record.get(Algorithms.FIELD_STATE_DX));
        input.setCountyAtDx(record.get(Algorithms.FIELD_COUNTY_DX));
        input.setCountyAtDxGeocode1990(record.get(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_1990));
        input.setCountyAtDxGeocode2000(record.get(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2000));
        input.setCountyAtDxGeocode2010(record.get(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010));
        input.setCountyAtDxGeocode2020(record.get(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2020));
        input.setStateAtDxGeocode19708090(record.get(Algorithms.FIELD_STATE_AT_DX_GEOCODE_19708090));
        input.setStateAtDxGeocode2000(record.get(Algorithms.FIELD_STATE_AT_DX_GEOCODE_2000));
        input.setStateAtDxGeocode2010(record.get(Algorithms.FIELD_STATE_AT_DX_GEOCODE_2010));
        input.setStateAtDxGeocode2020(record.get(Algorithms.FIELD_STATE_AT_DX_GEOCODE_2020));
        input.setCensusTrCert19708090(record.get(Algorithms.FIELD_CENSUS_CERTAINTY_708090));
        input.setCensusTrCertainty2000(record.get(Algorithms.FIELD_CENSUS_CERTAINTY_2000));
        input.setCensusTrCertainty2010(record.get(Algorithms.FIELD_CENSUS_CERTAINTY_2010));
        input.setCensusTrCertainty2020(record.get(Algorithms.FIELD_CENSUS_CERTAINTY_2020));

        return CountyAtDiagnosisAnalysisUtils.computeCountyAtDiagnosis(input);
    }
}
