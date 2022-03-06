/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class NhiaUtilsTest {

    // using the properties as map keys is deprecated, but it would be too much work to properly fix these tests, so it uses a method to translate the maps into proper input objects...
    private static final String _PROP_SPANISH_HISPANIC_ORIGIN = "spanishHispanicOrigin";
    private static final String _PROP_NAME_LAST = "nameLast";
    private static final String _PROP_NAME_MAIDEN = "nameMaiden";
    private static final String _PROP_NAME_BIRTH_SURNAME = "nameBirthSurname";
    private static final String _PROP_BIRTH_PLACE_COUNTRY = "birthplaceCountry";
    private static final String _PROP_RACE1 = "race1";
    private static final String _PROP_SEX = "sex";
    private static final String _PROP_IHS = "ihs";
    private static final String _PROP_COUNTY_DX_ANALYSIS = "countyAtDxAnalysis";
    private static final String _PROP_STATE_DX = "addressAtDxState";

    @Test
    public void assertInfo() {
        Assert.assertNotNull(NhiaUtils.ALG_VERSION);
        Assert.assertNotNull(NhiaUtils.ALG_NAME);
    }

    @Test
    public void testComputeNhia() {

        //test empty values for different flavors of the method       
        List<Map<String, String>> patient1 = new ArrayList<>();
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(patient1, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        NhiaInputPatientDto patient2 = new NhiaInputPatientDto();
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(patient2, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        NhiaInputRecordDto rec = new NhiaInputRecordDto();
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        Map<String, String> record = new HashMap<>();

        // special record values
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // bad options
        boolean exception = false;
        try {
            computeNhia(record, "3");
        }
        catch (RuntimeException e) {
            exception = true;
        }
        if (!exception)
            Assert.fail("Was expecting an exception, didn't get it!");

        // direct identification
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //direct identification in cases of special race 96 & 97
        record.put(_PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_RACE1, "04");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_RACE1, "97");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "NIC");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // direct identification when origin is 6
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //direct+indirect identification when origin is 6
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //birthplace within in a range 253-257
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "NIC");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // indirect identification based on birth place
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, " ");
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, null);
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "10");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "rr");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "3");
        Assert.assertEquals(NhiaUtils.NHIA_CUBAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //indirect identification based on birthplace with low probability.
        record.clear();
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Indirect identification based on race
        record.clear();
        record.put(_PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_RACE1, "99");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Indirect Identification based on name and county
        //test with county higher that 5% hispanic
        //for male, if sex not given, always non-hispanic
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_LAST, "AdOrno");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //female
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        record.put(_PROP_SEX, "2");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_BIRTH_SURNAME, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_BIRTH_SURNAME, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        // *** following tests that maiden name can still be used...
        record.put(_PROP_NAME_BIRTH_SURNAME, null);
        record.put(_PROP_NAME_MAIDEN, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_MAIDEN, null);
        record.put(_PROP_NAME_BIRTH_SURNAME, "ADORNO");
        // end maiden name check
        record.put(_PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SEX, "2");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //if race is in excluded race
        record.put(_PROP_RACE1, "03");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //test if ihs == 1
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        record.put(_PROP_SEX, "2");
        record.put(_PROP_NAME_LAST, "flint");
        record.put(_PROP_NAME_BIRTH_SURNAME, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_IHS, "1");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //test with county less that 5% hispanic
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(_PROP_SEX, "2");
        record.put(_PROP_NAME_BIRTH_SURNAME, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //random name
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        record.put(_PROP_SEX, "1");
        record.put(_PROP_NAME_LAST, "sseewwbbeesseeww");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Added to test modifications related to squish issue 207
        record.clear();
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        record.put(_PROP_STATE_DX, "AL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "005");
        record.put(_PROP_SEX, "2");
        record.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZP");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "PAN");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "UMI");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZS");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZU");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_STATE_DX, "GA");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "073");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_STATE_DX, "IL");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "103");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_STATE_DX, "AK");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "232");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_STATE_DX, "NE");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "007");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_STATE_DX, "AK");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "105");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_STATE_DX, "AK");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "280");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_STATE_DX, "AK");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "195");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        //Testing Git issue #97
        record.clear();
        record.put(_PROP_RACE1, "96");
        record.put(_PROP_BIRTH_PLACE_COUNTRY, "UMI");
        record.put(_PROP_STATE_DX, "WA");
        record.put(_PROP_COUNTY_DX_ANALYSIS, "005");
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        record.put(_PROP_SEX, "2");
        record.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "4");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "5");
        Assert.assertEquals(NhiaUtils.NHIA_OTHER_SPANISH, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(_PROP_RACE1, "01");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
    }

    @Test
    public void testPatientSet() {
        List<Map<String, String>> patient1 = new ArrayList<>();
        Map<String, String> rec1 = new HashMap<>();
        rec1.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        rec1.put(_PROP_SEX, "2");
        rec1.put(_PROP_NAME_LAST, "ADORNO");
        rec1.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZP");
        rec1.put(_PROP_STATE_DX, "GA");
        rec1.put(_PROP_COUNTY_DX_ANALYSIS, "073");
        //one record with low hispanic county at DX
        patient1.add(rec1);
        //name option doesn't run.
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(patient1, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        Map<String, String> rec2 = new HashMap<>();
        rec2.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        rec2.put(_PROP_SEX, "2");
        rec2.put(_PROP_NAME_LAST, "ADORNO");
        rec2.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZP");
        rec2.put(_PROP_STATE_DX, "IL");
        rec2.put(_PROP_COUNTY_DX_ANALYSIS, "103");
        //Add one more record with high hispanic county at DX
        patient1.add(rec2);
        //The patient's county at dx considered as high hispanic.
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(patient1, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        NhiaInputPatientDto patient2 = new NhiaInputPatientDto();
        NhiaInputRecordDto rec3 = new NhiaInputRecordDto();
        rec3.setSpanishHispanicOrigin("0");
        rec3.setSex("2");
        rec3.setNameLast("ADORNO");
        rec3.setBirthplaceCountry("ZZP");
        rec3.setStateAtDx("AL");
        rec3.setCountyAtDxAnalysis("007");
        //one record with low hispanic county at DX
        patient2.setNhiaInputPatientDtoList(new ArrayList<>());
        patient2.getNhiaInputPatientDtoList().add(rec3);
        //name option doesn't run.
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(patient2, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        NhiaInputRecordDto rec4 = new NhiaInputRecordDto();
        rec4.setSpanishHispanicOrigin("0");
        rec4.setSex("2");
        rec4.setNameLast("ADORNO");
        rec4.setBirthplaceCountry("ZZP");
        rec4.setStateAtDx("AL");
        rec4.setCountyAtDxAnalysis("005");
        //Add one more record with high hispanic county at DX
        patient2.getNhiaInputPatientDtoList().add(rec4);
        //The patient's county at dx considered as high hispanic.
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(patient2, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testCsvFile() throws IOException, CsvException {
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("nhia/testNHIA.csv"), StandardCharsets.US_ASCII)).withSkipLines(1).build()) {
            for (String[] row : reader.readAll()) {
                Map<String, String> rec = new HashMap<>();
                rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, row[0]);
                rec.put(_PROP_BIRTH_PLACE_COUNTRY, row[1]);
                rec.put(_PROP_RACE1, row[2]);
                rec.put(_PROP_IHS, row[3]);
                rec.put(_PROP_STATE_DX, row[4]);
                rec.put(_PROP_COUNTY_DX_ANALYSIS, row[5]);
                rec.put(_PROP_SEX, row[6]);
                rec.put(_PROP_NAME_LAST, row[7]);
                rec.put(_PROP_NAME_BIRTH_SURNAME, row[8]);

                String option = row[9];
                String nhia = row[10];

                if (!nhia.equals(computeNhia(rec, option).getNhia()))
                    Assert.fail("Unexpected result in CSV data file for row " + Arrays.asList(row));
            }
        }
    }

    @Test
    public void testGetLowHispanicCountiesPerState() {
        //for (Map.Entry<String, List<String>> entry : NhiaUtils.getLowHispanicCountiesPerState().entrySet())
        //    System.out.println(entry.getKey() + ": " + entry.getValue());
        Assert.assertFalse(NhiaUtils.getLowHispanicCountiesPerState().isEmpty());
    }

    // so many calls in this test were using the deprecated method that it was easier to create a private method that keeps using the deprecated logic...
    public static NhiaResultsDto computeNhia(Map<String, String> record, String option) {
        NhiaInputRecordDto input = new NhiaInputRecordDto();
        input.setSpanishHispanicOrigin(record.get(_PROP_SPANISH_HISPANIC_ORIGIN));
        input.setBirthplaceCountry(record.get(_PROP_BIRTH_PLACE_COUNTRY));
        input.setSex(record.get(_PROP_SEX));
        input.setRace1(record.get(_PROP_RACE1));
        input.setIhs(record.get(_PROP_IHS));
        input.setNameLast(record.get(_PROP_NAME_LAST));
        input.setNameMaiden(record.get(_PROP_NAME_MAIDEN));
        input.setNameBirthSurname(record.get(_PROP_NAME_BIRTH_SURNAME));
        input.setCountyAtDxAnalysis(record.get(_PROP_COUNTY_DX_ANALYSIS));
        input.setStateAtDx(record.get(_PROP_STATE_DX));
        return NhiaUtils.computeNhia(input, option);
    }

    // so many calls in this test were using the deprecated method that it was easier to create a private method that keeps using the deprecated logic...
    public static NhiaResultsDto computeNhia(List<Map<String, String>> patient, String option) {
        NhiaInputPatientDto input = new NhiaInputPatientDto();
        input.setNhiaInputPatientDtoList(new ArrayList<>());
        for (Map<String, String> record : patient) {
            NhiaInputRecordDto dto = new NhiaInputRecordDto();
            dto.setSpanishHispanicOrigin(record.get(_PROP_SPANISH_HISPANIC_ORIGIN));
            dto.setBirthplaceCountry(record.get(_PROP_BIRTH_PLACE_COUNTRY));
            dto.setSex(record.get(_PROP_SEX));
            dto.setRace1(record.get(_PROP_RACE1));
            dto.setIhs(record.get(_PROP_IHS));
            dto.setNameLast(record.get(_PROP_NAME_LAST));
            dto.setNameMaiden(record.get(_PROP_NAME_MAIDEN));
            dto.setNameBirthSurname(record.get(_PROP_NAME_BIRTH_SURNAME));
            dto.setCountyAtDxAnalysis(record.get(_PROP_COUNTY_DX_ANALYSIS));
            dto.setStateAtDx(record.get(_PROP_STATE_DX));
            input.getNhiaInputPatientDtoList().add(dto);
        }

        return NhiaUtils.computeNhia(input, option);
    }
}
