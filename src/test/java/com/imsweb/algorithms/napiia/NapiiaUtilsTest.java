/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.napiia;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

public class NapiiaUtilsTest {

    // using the properties as map keys is deprecated, but it would be too much work to properly fix these tests, so it uses a method to translate the maps into proper input objects...
    private static final String _PROP_RACE1 = "race1";
    private static final String _PROP_RACE2 = "race2";
    private static final String _PROP_RACE3 = "race3";
    private static final String _PROP_RACE4 = "race4";
    private static final String _PROP_RACE5 = "race5";
    private static final String _PROP_SPANISH_HISPANIC_ORIGIN = "spanishHispanicOrigin";
    private static final String _PROP_BIRTH_PLACE_COUNTRY = "birthplaceCountry";
    private static final String _PROP_SEX = "sex";
    private static final String _PROP_NAME_LAST = "nameLast";
    private static final String _PROP_NAME_BIRTH_SURNAME = "nameBirthSurname";
    private static final String _PROP_NAME_FIRST = "nameFirst";

    @Test
    public void assertInfo() {
        Assert.assertNotNull(NapiiaUtils.ALG_VERSION);
        Assert.assertNotNull(NapiiaUtils.ALG_NAME);
    }

    @Test
    @SuppressWarnings("java:S5961") // too much complexity
    public void testComputeNapiia() {

        //test different flavor of methods for special cases
        List<Map<String, String>> patient1 = new ArrayList<>();
        Assert.assertEquals("", computeNapiia(patient1).getNapiiaValue());
        Assert.assertTrue(computeNapiia(patient1).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_2_2_3, computeNapiia(patient1).getReasonForReview());

        NapiiaInputPatientDto patient2 = new NapiiaInputPatientDto();
        Assert.assertEquals("", NapiiaUtils.computeNapiia(patient2).getNapiiaValue());
        Assert.assertTrue(NapiiaUtils.computeNapiia(patient2).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_2_2_3, NapiiaUtils.computeNapiia(patient2).getReasonForReview());

        NapiiaInputRecordDto dto = new NapiiaInputRecordDto();
        Assert.assertEquals("", NapiiaUtils.computeNapiia(dto).getNapiiaValue());
        Assert.assertTrue(NapiiaUtils.computeNapiia(dto).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_2_2_3, NapiiaUtils.computeNapiia(dto).getReasonForReview());

        Map<String, String> rec = new HashMap<>();
        //special record value
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_2_2_3, computeNapiia(rec).getReasonForReview());

        //SINGLE RACE: race 96 or race 97 at race1 the remaining empty and Hispanic origin(1-6,8).
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE1, "97");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //race 96 0r 97 at different place other than race 1.
        rec.clear();
        rec.put(_PROP_RACE4, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "1");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_7, computeNapiia(rec).getReasonForReview());

        //SINGLE RACE: race 96 or race 97 at race1 the remaining empty and not-Hispanic origin(0,7,9).
        rec.clear();
        rec.put(_PROP_RACE1, "96");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //Asian birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "IND");
        Assert.assertEquals("16", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals("16", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "x");
        Assert.assertEquals("16", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //Pacific Islander birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ASM");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //Excluded Asian and Pacific Islander birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "MDV");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //Excluded Hispanic birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PAN");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //change to 97
        rec.put(_PROP_RACE1, "97");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ASM");
        Assert.assertEquals("27", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //SINGLE RACE: race 96 or race 97 at race3 the remaining empty and not-Hispanic origin(0,7,9).
        rec.clear();
        rec.put(_PROP_RACE3, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_7, computeNapiia(rec).getReasonForReview());
        //Asian birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "IND");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_7, computeNapiia(rec).getReasonForReview());

        //SINGLE RACE: race 96 or race 97 at race 1, the remaining empty, non-hispanic origin (0,7,9)
        //test applying names for 96
        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "7");
        rec.put(_PROP_NAME_FIRST, "Amrit");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "8");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "tuat");
        Assert.assertEquals("10", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "tezuka");
        Assert.assertEquals("05", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "tovez");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "tezuka");
        Assert.assertEquals("05", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "ffffff");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        // end maiden name check
        rec.put(_PROP_NAME_BIRTH_SURNAME, "NGU");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "ut");
        Assert.assertEquals("10", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "fong");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "NANTHAVONG");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //SINGLE RACE: race 96 or race 97 at race 1, the remaining empty, non-hispanic origin (0,7,9)
        //test applying names for 97
        rec.clear();
        rec.put(_PROP_RACE1, "97");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "keahi");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "keahi");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "x");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "keahi");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "quinene");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "LAULU");
        Assert.assertEquals("27", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "zhuo");
        Assert.assertEquals("27", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //SINGLE RACE: single race other than 96 0r 97 at race1
        rec.clear();
        rec.put(_PROP_RACE1, "13");
        Assert.assertEquals("13", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE1, "45");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE1, "99");
        Assert.assertEquals("99", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //SINGLE RACE: single race other than 96 0r 97 at race3
        rec.clear();
        rec.put(_PROP_RACE3, "13");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE3, "45");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //Multiple races not involving 96 or 97
        rec.clear();
        rec.put(_PROP_RACE5, "15");
        rec.put(_PROP_RACE1, "01");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE3, "24");
        Assert.assertEquals("01", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_2_2_3, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE4, "07");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE4, "07");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE1, "01");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //Multiple races involving 96 or 97
        rec.clear();
        rec.put(_PROP_RACE5, "15");
        rec.put(_PROP_RACE1, "96");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE3, "24");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_3, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE5, "07");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE1, "15");
        rec.put(_PROP_RACE2, "18");
        rec.put(_PROP_RACE4, "96");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_3, computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE1, "03");
        rec.put(_PROP_RACE4, "96");
        Assert.assertEquals("03", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_5, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE2, "02");
        Assert.assertEquals("03", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_5, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE1, "02");
        Assert.assertEquals("02", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_5, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE3, "06");
        Assert.assertEquals("02", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_7, computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE1, "97");
        rec.put(_PROP_RACE5, "96");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_6, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE5, "30");
        Assert.assertEquals("30", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE3, "97");
        rec.put(_PROP_RACE5, "96");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_6, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE1, "03");
        Assert.assertEquals("03", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_6, computeNapiia(rec).getReasonForReview());

        //multiple races applying step 3 and 4
        rec.clear();
        rec.put(_PROP_RACE5, "97");
        rec.put(_PROP_RACE1, "01");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //Asian birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "IND");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //Pacific Islander birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ASM");
        Assert.assertEquals("27", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //Excluded Asian and Pacific Islander birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "MDV");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        //Excluded Hispanic birthplaces
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PAN");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE5, "97");
        rec.put(_PROP_RACE2, "01");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ASM");
        Assert.assertEquals("27", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "IND");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //test applying names for multiple races
        //Pacific Islander
        rec.clear();
        rec.put(_PROP_RACE5, "97");
        rec.put(_PROP_RACE1, "01");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        rec.put(_PROP_NAME_FIRST, "ATOIGUE");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "ATOIGUE");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "ATOIGUE");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "Jhanji");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "3");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "RAJARAMAN");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "ADUSUMILLI");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "DEPANTE");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "SUMAIT");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "CHAMPACO");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "Taijeron");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "aguon");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "ATOIGUE");
        Assert.assertEquals("22", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "KUEHU");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "xiz");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "mafi");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "yandaLL");
        Assert.assertEquals("27", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PAK");
        Assert.assertEquals("27", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "MMR");
        Assert.assertEquals("97", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PLW");
        Assert.assertEquals("20", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE3, "07");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //test applying names for multiple races
        //Asian
        rec.clear();
        rec.put(_PROP_RACE2, "96");
        rec.put(_PROP_RACE4, "01");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "RAJARAMAN");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "edita");
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "8");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "lalita");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "felIpe");
        Assert.assertEquals("06", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "8");
        Assert.assertEquals("06", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        rec.put(_PROP_NAME_LAST, "bbbbbb");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "Moyo");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "Moyo");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "2");
        Assert.assertEquals("05", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "VALLABHANENI");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "KUEHU");
        Assert.assertEquals("05", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "dddddd");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_LAST, "leeWong");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "dddddd");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "we");
        Assert.assertEquals("08", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "kurachi");
        Assert.assertEquals("05", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "GGGG");
        Assert.assertEquals("08", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "Manyvong");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "chitre");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_BIRTH_SURNAME, "Maluia");
        Assert.assertEquals("08", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_NAME_FIRST, "jagir");
        Assert.assertEquals("15", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_SEX, "1");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //test all other multiple races
        rec.clear();
        rec.put(_PROP_RACE2, "10");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE4, "01");
        Assert.assertEquals("10", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE5, "16");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_2_2_3, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE1, "04");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_2_2_3, computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_RACE3, "07");
        Assert.assertEquals("07", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //the following tests added for changes related to squish issue 207
        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "CHN");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PAK");
        Assert.assertEquals("17", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "UMI");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "9");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "BLZ");
        rec.put(_PROP_SEX, "1");
        rec.put(_PROP_NAME_LAST, "AGUYEN");
        Assert.assertEquals("10", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZU");
        Assert.assertEquals("10", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PAN");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZS");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //testing special name LOW and HIGH (the sas xpt file had something to do with this names)
        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_RACE2, "01");
        rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, "0");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "USA");
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());
        Assert.assertFalse(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertNull(computeNapiia(rec).getReasonForReview());

        //testing cases with 1.3.4 and other related to #215
        rec.clear();
        rec.put(_PROP_RACE1, "01");
        rec.put(_PROP_RACE2, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "MDV");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "01");
        rec.put(_PROP_RACE2, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "GTM");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "01");
        rec.put(_PROP_RACE2, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PHL");
        Assert.assertEquals("06", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("06", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "01");
        rec.put(_PROP_RACE2, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "XMC");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "01");
        rec.put(_PROP_RACE2, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZU");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "01");
        rec.put(_PROP_RACE2, "96");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());

        //test r1 = 96
        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "MDV");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "GTM");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PHL");
        Assert.assertEquals("06", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("06", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "XMC");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "ZZU");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "96");
        Assert.assertEquals("96", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("04", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE2, "96");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_BIRTH_PLACE_COUNTRY, "PHL");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());
        rec.put(_PROP_SEX, "2");
        rec.put(_PROP_NAME_FIRST, "VALERIE");
        rec.put(_PROP_NAME_LAST, "LOW");
        Assert.assertEquals("", computeNapiia(rec).getNapiiaValue());

        rec.clear();
        rec.put(_PROP_RACE1, "33");
        rec.put(_PROP_RACE2, "96");
        Assert.assertEquals("33", computeNapiia(rec).getNapiiaValue());
        Assert.assertTrue(computeNapiia(rec).getNeedsHumanReview());
        Assert.assertEquals(NapiiaUtils.REASON_1_3_7, computeNapiia(rec).getReasonForReview());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testCsvFile() throws IOException {
        AtomicInteger count = new AtomicInteger();

        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder().ofNamedCsvRecord(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("napiia/testNAPIIA.csv"), StandardCharsets.US_ASCII))) {
            csvReader.stream().forEach(line -> {
                count.getAndIncrement();

                Map<String, String> rec = new HashMap<>();
                rec.put(_PROP_RACE1, line.getField(0).trim().isEmpty() ? null : line.getField(0));
                rec.put(_PROP_RACE2, line.getField(1).trim().isEmpty() ? null : line.getField(1));
                rec.put(_PROP_RACE3, line.getField(2).trim().isEmpty() ? null : line.getField(2));
                rec.put(_PROP_RACE4, line.getField(3).trim().isEmpty() ? null : line.getField(3));
                rec.put(_PROP_RACE5, line.getField(4).trim().isEmpty() ? null : line.getField(4));
                rec.put(_PROP_SPANISH_HISPANIC_ORIGIN, line.getField(5).trim().isEmpty() ? null : line.getField(5));
                rec.put(_PROP_BIRTH_PLACE_COUNTRY, line.getField(6).trim().isEmpty() ? null : line.getField(6));
                rec.put(_PROP_SEX, line.getField(7).trim().isEmpty() ? null : line.getField(7));
                rec.put(_PROP_NAME_LAST, line.getField(8).trim().isEmpty() ? null : line.getField(8));
                rec.put(_PROP_NAME_BIRTH_SURNAME, line.getField(9).trim().isEmpty() ? null : line.getField(9));
                rec.put(_PROP_NAME_FIRST, line.getField(10).trim().isEmpty() ? null : line.getField(10));

                String napiia = line.getField(11);
                Boolean review = Boolean.valueOf(line.getField(12));
                String reason = line.getField(13).trim().isEmpty() ? null : line.getField(13);
                NapiiaResultsDto results = computeNapiia(rec);

                if (!napiia.equals(results.getNapiiaValue()))
                    Assert.fail("Unexpected napiia result in CSV data file for row #" + count + " " + Arrays.asList(line.getFields().toArray(new String[0])) + "  " + results.getNapiiaValue());
                if (!review.equals(results.getNeedsHumanReview()))
                    Assert.fail("Unexpected needs manual review result in CSV data file for row #" + count + " " + Arrays.asList(line.getFields().toArray(new String[0])) + "  " + results.getNeedsHumanReview());
                if (results.getReasonForReview() == null ? reason != null : !results.getReasonForReview().equals(reason))
                    Assert.fail("Unexpected reason for review result in CSV data file for row #" + count + " " + Arrays.asList(line.getFields().toArray(new String[0])) + "  " + results.getReasonForReview());
            });
        }
    }

    // so many calls in this test were using the deprecated method that it was easier to create a private method that keeps using the deprecated logic...
    private NapiiaResultsDto computeNapiia(Map<String, String> rec) {
        NapiiaInputRecordDto input = new NapiiaInputRecordDto();
        input.setRace1(rec.get(_PROP_RACE1));
        input.setRace2(rec.get(_PROP_RACE2));
        input.setRace3(rec.get(_PROP_RACE3));
        input.setRace4(rec.get(_PROP_RACE4));
        input.setRace5(rec.get(_PROP_RACE5));
        input.setSpanishHispanicOrigin(rec.get(_PROP_SPANISH_HISPANIC_ORIGIN));
        input.setBirthplaceCountry(rec.get(_PROP_BIRTH_PLACE_COUNTRY));
        input.setSex(rec.get(_PROP_SEX));
        input.setNameLast(rec.get(_PROP_NAME_LAST));
        input.setNameBirthSurname(rec.get(_PROP_NAME_BIRTH_SURNAME));
        input.setNameFirst(rec.get(_PROP_NAME_FIRST));
        return NapiiaUtils.computeNapiia(input);
    }

    // so many calls in this test were using the deprecated method that it was easier to create a private method that keeps using the deprecated logic...
    private NapiiaResultsDto computeNapiia(List<Map<String, String>> patient) {
        NapiiaInputRecordDto input = new NapiiaInputRecordDto();
        //Since the following properties are the same for all records lets use one of them and build a record input dto
        if (patient != null && !patient.isEmpty()) {
            input.setRace1(patient.get(0).get(_PROP_RACE1));
            input.setRace2(patient.get(0).get(_PROP_RACE2));
            input.setRace3(patient.get(0).get(_PROP_RACE3));
            input.setRace4(patient.get(0).get(_PROP_RACE4));
            input.setRace5(patient.get(0).get(_PROP_RACE5));
            input.setSpanishHispanicOrigin(patient.get(0).get(_PROP_SPANISH_HISPANIC_ORIGIN));
            input.setBirthplaceCountry(patient.get(0).get(_PROP_BIRTH_PLACE_COUNTRY));
            input.setSex(patient.get(0).get(_PROP_SEX));
            input.setNameLast(patient.get(0).get(_PROP_NAME_LAST));
            input.setNameBirthSurname(patient.get(0).get(_PROP_NAME_BIRTH_SURNAME));
            input.setNameFirst(patient.get(0).get(_PROP_NAME_FIRST));
        }
        return NapiiaUtils.computeNapiia(input);
    }
}