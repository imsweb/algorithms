/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

public class NhiaUtilsTest {

    @Test
    public void testComputeNhia() {

        //test empty values for different flavors of the method       
        List<Map<String, String>> patient1 = new ArrayList<>();
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(patient1, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        NhiaInputPatientDto patient2 = new NhiaInputPatientDto();
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(patient2, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        NhiaInputRecordDto rec = new NhiaInputRecordDto();
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(rec, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        Map<String, String> record = new HashMap<>();

        // special record values
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // bad options
        boolean exception = false;
        try {
            NhiaUtils.computeNhia(record, "3");
        }
        catch (RuntimeException e) {
            exception = true;
        }
        if (!exception)
            Assert.fail("Was expecting an exception, didn't get it!");

        // direct identification
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //direct identification in cases of special race 96 & 97
        record.put(NhiaUtils.PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_RACE1, "04");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_RACE1, "97");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "NIC");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // direct identification when origin is 6
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //direct+indirect identification when origin is 6
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //birthplace within in a range 253-257
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "NIC");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        // indirect identification based on birth place
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, " ");
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, null);
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "10");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "rr");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "9");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "3");
        Assert.assertEquals(NhiaUtils.NHIA_CUBAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //indirect identification based on birthplace with low probability.
        record.clear();
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "6");
        Assert.assertEquals(NhiaUtils.NHIA_SPANISH_NOS, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Indirect identification based on race
        record.clear();
        record.put(NhiaUtils.PROP_RACE1, "96");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_RACE1, "99");
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Indirect Identification based on name and county
        //test with county higher that 5% hispanic
        //for male, if sex not given, always non-hispanic
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(NhiaUtils.PROP_STATE_DX, "AL");
        record.put(NhiaUtils.PROP_COUNTY_DX, "009");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_LAST, "AdOrno");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //female
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(NhiaUtils.PROP_STATE_DX, "AL");
        record.put(NhiaUtils.PROP_COUNTY_DX, "009");
        record.put(NhiaUtils.PROP_SEX, "2");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_MAIDEN, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_MAIDEN, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_LAST, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SEX, "2");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //if race is in excluded race
        record.put(NhiaUtils.PROP_RACE1, "03");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        //test if ihs == 1
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "0");
        record.put(NhiaUtils.PROP_STATE_DX, "AL");
        record.put(NhiaUtils.PROP_COUNTY_DX, "009");
        record.put(NhiaUtils.PROP_SEX, "2");
        record.put(NhiaUtils.PROP_NAME_LAST, "flint");
        record.put(NhiaUtils.PROP_NAME_MAIDEN, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_IHS, "1");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //test with county less that 5% hispanic
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(NhiaUtils.PROP_STATE_DX, "AL");
        record.put(NhiaUtils.PROP_COUNTY_DX, "001");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_SEX, "1");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "7");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
        record.put(NhiaUtils.PROP_SEX, "2");
        record.put(NhiaUtils.PROP_NAME_MAIDEN, "FLINT");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "GUY");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "PRI");
        Assert.assertEquals(NhiaUtils.NHIA_PUERTO_RICAN, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //random name
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "9");
        record.put(NhiaUtils.PROP_STATE_DX, "AL");
        record.put(NhiaUtils.PROP_COUNTY_DX, "009");
        record.put(NhiaUtils.PROP_SEX, "1");
        record.put(NhiaUtils.PROP_NAME_LAST, "sseewwbbeesseeww");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());

        //Added to test modifications related to squish issue 207
        record.clear();
        record.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "0");
        record.put(NhiaUtils.PROP_STATE_DX, "AL");
        record.put(NhiaUtils.PROP_COUNTY_DX, "005");
        record.put(NhiaUtils.PROP_SEX, "2");
        record.put(NhiaUtils.PROP_NAME_LAST, "ADORNO");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "ZZP");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "PAN");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "UMI");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "ZZS");
        Assert.assertEquals(NhiaUtils.NHIA_SOUTH_CENTRAL_AMER, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "ZZU");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_STATE_DX, "GA");
        record.put(NhiaUtils.PROP_COUNTY_DX, "073");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_STATE_DX, "IL");
        record.put(NhiaUtils.PROP_COUNTY_DX, "103");
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_STATE_DX, "AK");
        record.put(NhiaUtils.PROP_COUNTY_DX, "232");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_STATE_DX, "NE");
        record.put(NhiaUtils.PROP_COUNTY_DX, "007");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_STATE_DX, "AK");
        record.put(NhiaUtils.PROP_COUNTY_DX, "105");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_STATE_DX, "AK");
        record.put(NhiaUtils.PROP_COUNTY_DX, "280");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
        record.put(NhiaUtils.PROP_STATE_DX, "AK");
        record.put(NhiaUtils.PROP_COUNTY_DX, "195");
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(record, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

    }

    @Test
    public void testPatientSet() {
        List<Map<String, String>> patient1 = new ArrayList<>();
        Map<String, String> rec1 = new HashMap<>();
        rec1.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "0");
        rec1.put(NhiaUtils.PROP_SEX, "2");
        rec1.put(NhiaUtils.PROP_NAME_LAST, "ADORNO");
        rec1.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "ZZP");
        rec1.put(NhiaUtils.PROP_STATE_DX, "GA");
        rec1.put(NhiaUtils.PROP_COUNTY_DX, "073");
        //one record with low hispanic county at DX
        patient1.add(rec1);
        //name option doesn't run.
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(patient1, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        Map<String, String> rec2 = new HashMap<>();
        rec2.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, "0");
        rec2.put(NhiaUtils.PROP_SEX, "2");
        rec2.put(NhiaUtils.PROP_NAME_LAST, "ADORNO");
        rec2.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, "ZZP");
        rec2.put(NhiaUtils.PROP_STATE_DX, "IL");
        rec2.put(NhiaUtils.PROP_COUNTY_DX, "103");
        //Add one more record with high hispanic county at DX
        patient1.add(rec2);
        //The patient's county at dx considered as high hispanic.
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(patient1, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        NhiaInputPatientDto patient2 = new NhiaInputPatientDto();
        NhiaInputRecordDto rec3 = new NhiaInputRecordDto();
        rec3.setSpanishHispanicOrigin("0");
        rec3.setSex("2");
        rec3.setNameLast("ADORNO");
        rec3.setBirthplaceCountry("ZZP");
        rec3.setStateAtDx("AL");
        rec3.setCountyAtDx("007");
        //one record with low hispanic county at DX
        patient2.setNhiaInputPatientDtoList(new ArrayList<NhiaInputRecordDto>());
        patient2.getNhiaInputPatientDtoList().add(rec3);
        //name option doesn't run.
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, NhiaUtils.computeNhia(patient2, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());

        NhiaInputRecordDto rec4 = new NhiaInputRecordDto();
        rec4.setSpanishHispanicOrigin("0");
        rec4.setSex("2");
        rec4.setNameLast("ADORNO");
        rec4.setBirthplaceCountry("ZZP");
        rec4.setStateAtDx("AL");
        rec4.setCountyAtDx("005");
        //Add one more record with high hispanic county at DX
        patient2.getNhiaInputPatientDtoList().add(rec4);
        //The patient's county at dx considered as high hispanic.
        Assert.assertEquals(NhiaUtils.NHIA_SURNAME_ONLY, NhiaUtils.computeNhia(patient2, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
    }

    @Test
    public void testCsvFile() throws IOException {
        int count = 0;
        for (String[] row : new CSVReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("nhia/testNHIA.csv"), "US-ASCII"), ',', '\"', 1).readAll()) {
            Map<String, String> rec = new HashMap<>();
            rec.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, row[0]);
            rec.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, row[1]);
            rec.put(NhiaUtils.PROP_RACE1, row[2]);
            rec.put(NhiaUtils.PROP_IHS, row[3]);
            rec.put(NhiaUtils.PROP_STATE_DX, row[4]);
            rec.put(NhiaUtils.PROP_COUNTY_DX, row[5]);
            rec.put(NhiaUtils.PROP_SEX, row[6]);
            rec.put(NhiaUtils.PROP_NAME_LAST, row[7]);
            rec.put(NhiaUtils.PROP_NAME_MAIDEN, row[8]);

            String option = row[9];
            String nhia = row[10];

            if (!nhia.equals(NhiaUtils.computeNhia(rec, option).getNhia()))
                Assert.fail("Unexpected result in CSV data file for row " + Arrays.asList(row));
            count++;
        }
        System.out.println(count + " cases tested!");
    }

    @Test
    public void testGetLowHispanicCountiesPerState() {
        //for (Map.Entry<String, List<String>> entry : NhiaUtils.getLowHispanicCountiesPerState().entrySet())
        //    System.out.println(entry.getKey() + ": " + entry.getValue());
        Assert.assertFalse(NhiaUtils.getLowHispanicCountiesPerState().isEmpty());
    }
}
