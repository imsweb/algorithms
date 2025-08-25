/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class NhiaUtilsTest {

    // using the properties as map keys is deprecated, but it would be too much work to properly fix these tests, so it uses a method to translate the maps into proper input objects...
    private static final String _PROP_SPANISH_HISPANIC_ORIGIN = "spanishHispanicOrigin";
    private static final String _PROP_NAME_LAST = "nameLast";
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

        NhiaInputRecordDto dto = new NhiaInputRecordDto();
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(dto, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        Map<String, String> rec = new HashMap<>();

        // special record values
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // bad options
        boolean exception = false;
        try {
            computeNhia(rec, "3");
        }
        catch (RuntimeException e) {
            exception = true;
        }
        if (!exception)
            Assert.fail("Was expecting an exception, didn't get it!");

        // direct identification
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //direct identification in cases of special race 96 & 97
        rec.put(_PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_RACE1, "04");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_RACE1, "97");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "NIC");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // direct identification when origin is 6
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //direct+indirect identification when origin is 6
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //birthplace within in a range 253-257
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "NIC");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // indirect identification based on birth place
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, " ");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, null);
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "10");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "rr");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "3");
        Assert.assertEquals(NhiaUtils.NHIA_CUBAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //indirect identification based on birthplace with low probability.
        rec.clear();
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Indirect identification based on race
        rec.clear();
        rec.put(_PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_RACE1, "99");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Indirect Identification based on name and county
        //test with county higher that 5% hispanic
        //for male, if sex not given, always non-hispanic
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_STATE_DX, "AL");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_LAST, "AdOrno");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //female
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_STATE_DX, "AL");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //if race is in excluded race
        rec.put(_PROP_RACE1, "03");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //test if ihs == 1
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        rec.put(_PROP_STATE_DX, "AL");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_LAST, "flint");
        rec.put(_PROP_NAME_BIRTH_SURNAME, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_IHS, "1");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //test with county less that 5% hispanic
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_STATE_DX, "AL");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "001");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_BIRTH_SURNAME, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //random name
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_STATE_DX, "AL");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "009");
        rec.put(_PROP_SEX, "1");
        rec.put(_PROP_NAME_LAST, "sseewwbbeesseeww");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Added to test modifications related to squish issue 207
        rec.clear();
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        rec.put(_PROP_STATE_DX, "AL");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "005");
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZP");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PAN");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "UMI");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZS");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZU");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_STATE_DX, "GA");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "073");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_STATE_DX, "IL");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "103");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_STATE_DX, "AK");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "232");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_STATE_DX, "NE");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "007");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_STATE_DX, "AK");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "105");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_STATE_DX, "AK");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "280");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_STATE_DX, "AK");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "195");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        //Testing Git issue #97
        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "UMI");
        rec.put(_PROP_STATE_DX, "WA");
        rec.put(_PROP_COUNTY_DX_ANALYSIS, "005");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "4");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "5");
        Assert.assertEquals(NhiaUtils.NHIA_OTHER_SPANISH, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        rec.put(_PROP_RACE1, "01");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, computeNhia(rec, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
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

    @Test
    public void testGetLowHispanicCountiesPerState() {
        Assert.assertFalse(NhiaUtils.getLowHispanicCountiesPerState().isEmpty());
    }

    // so many calls in this test were using the deprecated method that it was easier to create a private method that keeps using the deprecated logic...
    public static NhiaResultsDto computeNhia(Map<String, String> rec, String option) {
        NhiaInputRecordDto input = new NhiaInputRecordDto();
        input.setSpanishHispanicOrigin(rec.get(_PROP_SPANISH_HISPANIC_ORIGIN));
        input.setBirthplaceCountry(rec.get(_PROP_BIRTH_PLACE_COUNTRY));
        input.setSex(rec.get(_PROP_SEX));
        input.setRace1(rec.get(_PROP_RACE1));
        input.setIhs(rec.get(_PROP_IHS));
        input.setNameLast(rec.get(_PROP_NAME_LAST));
        input.setNameBirthSurname(rec.get(_PROP_NAME_BIRTH_SURNAME));
        input.setCountyAtDxAnalysis(rec.get(_PROP_COUNTY_DX_ANALYSIS));
        input.setStateAtDx(rec.get(_PROP_STATE_DX));
        return NhiaUtils.computeNhia(input, option);
    }

    // so many calls in this test were using the deprecated method that it was easier to create a private method that keeps using the deprecated logic...
    public static NhiaResultsDto computeNhia(List<Map<String, String>> patient, String option) {
        NhiaInputPatientDto input = new NhiaInputPatientDto();
        input.setNhiaInputPatientDtoList(new ArrayList<>());
        for (Map<String, String> rec : patient) {
            NhiaInputRecordDto dto = new NhiaInputRecordDto();
            dto.setSpanishHispanicOrigin(rec.get(_PROP_SPANISH_HISPANIC_ORIGIN));
            dto.setBirthplaceCountry(rec.get(_PROP_BIRTH_PLACE_COUNTRY));
            dto.setSex(rec.get(_PROP_SEX));
            dto.setRace1(rec.get(_PROP_RACE1));
            dto.setIhs(rec.get(_PROP_IHS));
            dto.setNameLast(rec.get(_PROP_NAME_LAST));
            dto.setNameBirthSurname(rec.get(_PROP_NAME_BIRTH_SURNAME));
            dto.setCountyAtDxAnalysis(rec.get(_PROP_COUNTY_DX_ANALYSIS));
            dto.setStateAtDx(rec.get(_PROP_STATE_DX));
            input.getNhiaInputPatientDtoList().add(dto);
        }

        return NhiaUtils.computeNhia(input, option);
    }
}
