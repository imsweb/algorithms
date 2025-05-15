/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class HistoricStageUtilsTest {

    // this properties have been deprecated in the main class but so many tests use them that it was easier to copy them here
    private static final String _PROP_DATE_OF_DIAGNOSIS_YEAR = "dateOfDiagnosisYear";
    private static final String _PROP_PRIMARY_SITE = "primarySite";
    private static final String _PROP_HISTOLOGY_3 = "histologyIcdO3";
    private static final String _PROP_BEHAVIOR_3 = "behaviorIcdO3";
    private static final String _PROP_TYPE_OF_REPORTING_SOURCE = "typeOfReportingSource";
    private static final String _PROP_REGISTRY_ID = "registryId";
    private static final String _PROP_CS_EXTENSION = "csExtension";
    private static final String _PROP_CS_LYMPH_NODES = "csLymphNodes";
    private static final String _PROP_CS_METS_AT_DX = "csMetsAtDx";
    private static final String _PROP_CS_SITE_SPECIFIC_FACTOR1 = "csSiteSpecificFactor1";
    private static final String _PROP_CS_SITE_SPECIFIC_FACTOR2 = "csSiteSpecificFactor2";
    private static final String _PROP_EOD_CODING_SYSTEM = "eodCodingSys";
    private static final String _PROP_EOD10_LYMPH_NODES = "eodLymphNodeInvolv";
    private static final String _PROP_EOD10_EXTENSION = "eodExtension";
    private static final String _PROP_EOD4_DIGIT_EXTENSION = "eodOld4DigitExtent";
    private static final String _PROP_EOD4_DIGIT_NODES = "eodOld4DigitNodes";
    private static final String _PROP_EOD4_DIGIT_SIZE = "eodOld4DigitSize";
    private static final String _PROP_EOD13_DIGIT = "eodOld13Digit";
    private static final String _PROP_EOD2_DIGIT = "eodOld2Digit";

    @Test
    public void testIsLeukemia() {
        Assert.assertTrue(HistoricStageUtils.isLeukemia("424", "9823"));
        Assert.assertFalse(HistoricStageUtils.isLeukemia("425", "9823"));
        Assert.assertTrue(HistoricStageUtils.isLeukemia("425", "9741"));
        Assert.assertFalse(HistoricStageUtils.isLeukemia("AAA", "9741"));
        Assert.assertFalse(HistoricStageUtils.isLeukemia("", ""));
        Assert.assertFalse(HistoricStageUtils.isLeukemia(null, null));
    }

    @Test
    public void testCalculateHistoricStageSchema() {
        Assert.assertEquals("93", HistoricStageUtils.calculateHistoricStageSchema("424", "9823"));
        Assert.assertEquals("91", HistoricStageUtils.calculateHistoricStageSchema("424", "9140"));
        Assert.assertEquals("62", HistoricStageUtils.calculateHistoricStageSchema("542", "9998"));
        Assert.assertEquals("05", HistoricStageUtils.calculateHistoricStageSchema("028", "9878"));
        Assert.assertNull(HistoricStageUtils.calculateHistoricStageSchema("1028", "9878"));
        Assert.assertNull(HistoricStageUtils.calculateHistoricStageSchema("028", "6878"));
        Assert.assertNull(HistoricStageUtils.calculateHistoricStageSchema("", ""));
        Assert.assertNull(HistoricStageUtils.calculateHistoricStageSchema(null, null));
    }

    @Test
    public void testRunCsTables() {
        //test without cs ext recode exceptions
        HistoricStageInputDto input = new HistoricStageInputDto();
        input.setPrimarysite("C999");
        input.setHistologyIcdO3("9999");
        input.setCsSiteSpecificFactor1("999");
        input.setCsSiteSpecificFactor2("999");
        input.setBehaviorIcdO3("9");
        input.setDateOfDiagnosisYear("1990");
        input.setCsExtension("350");
        input.setCsLymphNodes("310");
        input.setCsMetsAtDx("50");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "18"));
        input.setCsExtension("000");
        input.setCsLymphNodes("000");
        input.setCsMetsAtDx("00");
        Assert.assertEquals("0", HistoricStageUtils.runCsTables(input, "31"));

        //test special case for longevity consistency
        input.setPrimarysite("C422");
        input.setHistologyIcdO3("9827");
        input.setCsExtension("800");
        Assert.assertEquals("9", HistoricStageUtils.runCsTables(input, "31"));

        //Special case for corpus schemas per Lynn Ries/Jennifer Ruhl starting Nov11 sub
        input = new HistoricStageInputDto();
        input.setPrimarysite("C999");
        input.setHistologyIcdO3("9999");
        input.setCsSiteSpecificFactor1("999");
        input.setCsSiteSpecificFactor2("999");
        input.setBehaviorIcdO3("9");
        input.setDateOfDiagnosisYear("1990");
        input.setCsExtension("530");
        input.setCsLymphNodes("000");
        input.setCsMetsAtDx("00");
        Assert.assertEquals("1", HistoricStageUtils.runCsTables(input, "62"));
        input.setCsSiteSpecificFactor2("010");
        Assert.assertEquals("2", HistoricStageUtils.runCsTables(input, "62"));

        //Special Breast Processing
        input = new HistoricStageInputDto();
        input.setPrimarysite("C999");
        input.setHistologyIcdO3("9999");
        input.setCsSiteSpecificFactor1("999");
        input.setCsSiteSpecificFactor2("999");
        input.setBehaviorIcdO3("2");
        input.setDateOfDiagnosisYear("1990");
        input.setCsExtension("070");
        input.setCsLymphNodes("050");
        input.setCsMetsAtDx("99");
        Assert.assertEquals("0", HistoricStageUtils.runCsTables(input, "58"));
        input.setBehaviorIcdO3("3");
        Assert.assertEquals("1", HistoricStageUtils.runCsTables(input, "58"));
        input.setBehaviorIcdO3("9");
        Assert.assertEquals("6", HistoricStageUtils.runCsTables(input, "58"));
        input.setCsExtension("720");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "58"));
        //Special Pleura Processing
        input = new HistoricStageInputDto();
        input.setPrimarysite("C999");
        input.setHistologyIcdO3("9999");
        input.setCsSiteSpecificFactor1("ABC");
        input.setCsSiteSpecificFactor2("999");
        input.setBehaviorIcdO3("2");
        input.setDateOfDiagnosisYear("1990");
        input.setCsExtension("100");
        input.setCsLymphNodes("100");
        input.setCsMetsAtDx("40");
        Assert.assertEquals("6", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("010");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("050");
        Assert.assertEquals("6", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("020");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsExtension("600");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("050");
        Assert.assertEquals("6", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("999");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsExtension("850");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("050");
        Assert.assertEquals("6", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsExtension("988");
        Assert.assertEquals("6", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("020");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsSiteSpecificFactor1("000");
        Assert.assertEquals("4", HistoricStageUtils.runCsTables(input, "49"));
        input.setCsExtension("666");
        Assert.assertEquals("6", HistoricStageUtils.runCsTables(input, "49"));
    }

    @Test
    public void testMakeCsExtRecodeExceptions() {
        HistoricStageInputDto input = new HistoricStageInputDto();
        //Special case for longevity consistency
        input.setPrimarysite("C422");
        input.setHistologyIcdO3("9827");
        input.setCsExtension("800");
        input.setCsSiteSpecificFactor1("010");
        input.setCsSiteSpecificFactor2("120");
        input.setBehaviorIcdO3("3");
        Assert.assertEquals("9", HistoricStageUtils.makeCsExtRecodeExceptions(input, "0", "20"));
        //special pleura processing
        Assert.assertEquals("7", HistoricStageUtils.makeCsExtRecodeExceptions(input, "0", "49"));
        //Special case for corpus schemas per Lynn Ries/Jennifer Ruhl starting Nov11 sub
        input = new HistoricStageInputDto();
        input.setPrimarysite("C422");
        input.setHistologyIcdO3("9827");
        input.setCsExtension("700");
        input.setCsSiteSpecificFactor1("120");
        input.setCsSiteSpecificFactor2("010");
        input.setBehaviorIcdO3("3");
        Assert.assertEquals("2", HistoricStageUtils.makeCsExtRecodeExceptions(input, "1", "62"));
        Assert.assertEquals("4", HistoricStageUtils.makeCsExtRecodeExceptions(input, "4", "62"));
        //Special Breast Processing
        input = new HistoricStageInputDto();
        input.setPrimarysite("C422");
        input.setHistologyIcdO3("9827");
        input.setCsExtension("050");
        input.setCsSiteSpecificFactor1("120");
        input.setCsSiteSpecificFactor2("010");
        input.setBehaviorIcdO3("2");
        Assert.assertEquals("0", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "58"));
        input.setCsExtension("090");
        Assert.assertEquals("9", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "58"));
        input.setCsExtension("070");
        Assert.assertEquals("0", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "58"));
        input.setBehaviorIcdO3("3");
        Assert.assertEquals("1", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "58"));
        //Bad Breast Behavior
        input.setBehaviorIcdO3("5");
        Assert.assertNull(HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "58"));
        //Special Pleura Processing
        input = new HistoricStageInputDto();
        input.setPrimarysite("C422");
        input.setHistologyIcdO3("9827");
        input.setCsExtension("150");
        input.setCsSiteSpecificFactor1("AAA");
        input.setCsSiteSpecificFactor2("010");
        input.setBehaviorIcdO3("2");
        Assert.assertNull(HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("010");
        Assert.assertEquals("1", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("000");
        Assert.assertEquals("1", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("020");
        Assert.assertEquals("7", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("050");
        Assert.assertNull(HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsExtension("420");
        input.setCsSiteSpecificFactor1("020");
        Assert.assertEquals("7", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("999");
        Assert.assertEquals("2", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("050");
        Assert.assertNull(HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsExtension("850");
        input.setCsSiteSpecificFactor1("010");
        Assert.assertEquals("7", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("030");
        Assert.assertEquals("7", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("050");
        Assert.assertNull(HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsExtension("966");
        input.setCsSiteSpecificFactor1("000");
        Assert.assertEquals("9", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("050");
        Assert.assertNull(HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsSiteSpecificFactor1("020");
        Assert.assertEquals("7", HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
        input.setCsExtension("900");
        Assert.assertNull(HistoricStageUtils.makeCsExtRecodeExceptions(input, "9", "49"));
    }

    @Test
    public void testRunEod10Tables() {
        HistoricStageInputDto input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1990");
        input.setPrimarysite("C005");
        input.setHistologyIcdO3("9142");
        input.setEodLymphNodeInvolv("1");
        input.setEodExtension("75");
        Assert.assertEquals(HistoricStageUtils.DISTANT, HistoricStageUtils.runEod10Tables(input));

        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1982");
        input.setPrimarysite("C005");
        input.setHistologyIcdO3("9142");
        input.setEodLymphNodeInvolv("1");
        input.setEodExtension("75");
        Assert.assertNull(HistoricStageUtils.runEod10Tables(input));

        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("2003");
        input.setPrimarysite("C080");
        input.setHistologyIcdO3("9300");
        input.setEodLymphNodeInvolv("0");
        input.setEodExtension("40");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, HistoricStageUtils.runEod10Tables(input));
        input.setEodLymphNodeInvolv("7");
        Assert.assertEquals(HistoricStageUtils.DISTANT, HistoricStageUtils.runEod10Tables(input));
        //test breast cases
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("2003");
        input.setPrimarysite("C503");
        input.setHistologyIcdO3("9300");
        input.setBehaviorIcdO3("2");
        input.setEodLymphNodeInvolv("0");
        input.setEodExtension("05");
        Assert.assertEquals(HistoricStageUtils.IN_SITU, HistoricStageUtils.runEod10Tables(input));
        input.setBehaviorIcdO3("3");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, HistoricStageUtils.runEod10Tables(input));
        //test special melanoma cases
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("2000");
        input.setPrimarysite("C632");
        input.setHistologyIcdO3("8744");
        input.setBehaviorIcdO3("2");
        input.setEodLymphNodeInvolv("3");
        input.setEodExtension("99");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, HistoricStageUtils.runEod10Tables(input));
    }

    @Test
    public void testRunEodPatchTable() {
        Assert.assertEquals("9", HistoricStageUtils.runEodPatchTable("4", "717", "9529"));
        Assert.assertEquals("9", HistoricStageUtils.runEodPatchTable("7", "717", "9529"));
        Assert.assertEquals("9", HistoricStageUtils.runEodPatchTable("7", "717", "9629"));
        Assert.assertEquals("9", HistoricStageUtils.runEodPatchTable("1", "000", "9731"));
        Assert.assertEquals("4", HistoricStageUtils.runEodPatchTable("3", "717", "9821"));
        Assert.assertEquals("9", HistoricStageUtils.runEodPatchTable("3", "717", "9823"));
        Assert.assertEquals("4", HistoricStageUtils.runEodPatchTable("3", "424", "9823"));
        Assert.assertEquals("9", HistoricStageUtils.runEodPatchTable("3", "419", "9827"));
        Assert.assertNull(HistoricStageUtils.runEodPatchTable("2", "717", "9529"));
        Assert.assertNull(HistoricStageUtils.runEodPatchTable("3", "720", "9529"));
        Assert.assertNull(HistoricStageUtils.runEodPatchTable("1", "717", "9530"));
        Assert.assertNull(HistoricStageUtils.runEodPatchTable("", "", ""));
        Assert.assertNull(HistoricStageUtils.runEodPatchTable(null, null, null));
    }

    @Test
    public void testRunEod13LungTable() {
        Assert.assertEquals("0", HistoricStageUtils.runEod13LungTable("1110-00&0-000"));
        Assert.assertEquals("1", HistoricStageUtils.runEod13LungTable("1115-008-0000"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13LungTable("111-50100-000"));
        Assert.assertEquals("4", HistoricStageUtils.runEod13LungTable("111-50140-11&"));
        Assert.assertEquals("9", HistoricStageUtils.runEod13LungTable("999&-0070-000"));
        Assert.assertNull(HistoricStageUtils.runEod13LungTable("1111234567890"));
        Assert.assertNull(HistoricStageUtils.runEod13LungTable("AAAAAAAAAAAAA"));
    }

    @Test
    public void testRunEod13MelanomaTable() {
        Assert.assertEquals("0", HistoricStageUtils.runEod13MelanomaTable("440", "1110103&3-000"));
        Assert.assertEquals("1", HistoricStageUtils.runEod13MelanomaTable("441", "1110700&00000"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13MelanomaTable("447", "-----13&1-000"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13MelanomaTable("446", "999993994-000"));
        Assert.assertEquals("4", HistoricStageUtils.runEod13MelanomaTable("447", "8888740021110"));
        Assert.assertEquals("9", HistoricStageUtils.runEod13MelanomaTable("444", "&&&&-00040000"));
        Assert.assertEquals("9", HistoricStageUtils.runEod13MelanomaTable("600", "4444-44076733"));
        Assert.assertEquals("9", HistoricStageUtils.runEod13MelanomaTable("605", "4444444444444"));
        Assert.assertEquals("9", HistoricStageUtils.runEod13MelanomaTable("519", "4444-44076733"));
        Assert.assertNull(HistoricStageUtils.runEod13MelanomaTable("600", "&&&&&&&&&&&&&"));
        Assert.assertNull(HistoricStageUtils.runEod13MelanomaTable("440", "4444-44076733"));
        Assert.assertNull(HistoricStageUtils.runEod13MelanomaTable("999", "1110103&3-000"));
        Assert.assertNull(HistoricStageUtils.runEod13MelanomaTable("444", "AAAAAAAAAAAAA"));
    }

    @Test
    public void testRunEod13BladderTable() {
        Assert.assertEquals("1", HistoricStageUtils.runEod13BladderTable("66660&-&20100"));
        Assert.assertEquals("1", HistoricStageUtils.runEod13BladderTable("&-&-230010000"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13BladderTable("&&&&&1-920000"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13BladderTable("222232-63-000"));
        Assert.assertEquals("4", HistoricStageUtils.runEod13BladderTable("555557-51-110"));
        Assert.assertEquals("9", HistoricStageUtils.runEod13BladderTable("9999-0--21000"));
        Assert.assertNull(HistoricStageUtils.runEod13BladderTable("1111234567890"));
        Assert.assertNull(HistoricStageUtils.runEod13BladderTable("AAAAAAAAAAAAA"));
    }

    @Test
    public void testRunEod13GeneralStageTable() {
        Assert.assertEquals("1", HistoricStageUtils.runEod13GeneralStageTable("160", "33377-0&30300"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13GeneralStageTable("160", "33377-0&31300"));
        Assert.assertEquals("4", HistoricStageUtils.runEod13GeneralStageTable("160", "33377-0&31310"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13GeneralStageTable("508", "33338-2015900"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13GeneralStageTable("508", "33338-2000000"));
        Assert.assertEquals("2", HistoricStageUtils.runEod13GeneralStageTable("508", "33338-40--000"));
        Assert.assertEquals("4", HistoricStageUtils.runEod13GeneralStageTable("508", "33336-2015900"));
        Assert.assertEquals("4", HistoricStageUtils.runEod13GeneralStageTable("508", "33336-2000000"));
        Assert.assertEquals("4", HistoricStageUtils.runEod13GeneralStageTable("508", "33336-40--000"));
        Assert.assertNull(HistoricStageUtils.runEod13GeneralStageTable("349", "33336-40--000"));
        Assert.assertNull(HistoricStageUtils.runEod13GeneralStageTable("160", "&&&&&&&&&&&&&"));
    }

    @Test
    public void testRunEod4DigStageTable() {
        Assert.assertEquals("0", HistoricStageUtils.runEod4DigStageTable("9132", "019", "0", "9"));
        Assert.assertEquals("9", HistoricStageUtils.runEod4DigStageTable("9140", "809", "7", "8"));
        Assert.assertEquals("4", HistoricStageUtils.runEod4DigStageTable("9132", "328", "8", "1"));
        Assert.assertEquals("1", HistoricStageUtils.runEod4DigStageTable("8778", "698", "2", "0"));
        Assert.assertEquals("1", HistoricStageUtils.runEod4DigStageTable("8778", "698", "2", "9"));
        Assert.assertEquals("2", HistoricStageUtils.runEod4DigStageTable("8778", "698", "2", "8"));
        Assert.assertNull(HistoricStageUtils.runEod4DigStageTable("9589", "729", "2", "8"));
        Assert.assertNull(HistoricStageUtils.runEod4DigStageTable("9999", "698", "2", "8"));
        Assert.assertNull(HistoricStageUtils.runEod4DigStageTable("", "", "2", "8"));
        Assert.assertNull(HistoricStageUtils.runEod4DigStageTable(null, "", "2", "8"));
    }

    @Test
    public void testRunEod2DigStageTables() {
        //apply eod 2 dig direct stage table
        Assert.assertEquals("0", HistoricStageUtils.runEod2DigTables("403", "8000", "0-"));
        Assert.assertEquals("1", HistoricStageUtils.runEod2DigTables("601", "8800", "3&"));
        Assert.assertEquals("2", HistoricStageUtils.runEod2DigTables("171", "8888", "64"));
        Assert.assertEquals("4", HistoricStageUtils.runEod2DigTables("505", "9589", "&2"));
        Assert.assertEquals("4", HistoricStageUtils.runEod2DigTables("505", "9589", "&7"));
        Assert.assertEquals("2", HistoricStageUtils.runEod2DigTables("505", "9589", "97"));
        Assert.assertEquals("9", HistoricStageUtils.runEod2DigTables("677", "8565", "--"));
        //apply eod 2 dig ext node stage table
        Assert.assertEquals("0", HistoricStageUtils.runEod2DigTables("164", "8234", "0-"));
        Assert.assertEquals("1", HistoricStageUtils.runEod2DigTables("169", "9589", "35"));
        Assert.assertEquals("2", HistoricStageUtils.runEod2DigTables("209", "9589", "65"));
        Assert.assertEquals("4", HistoricStageUtils.runEod2DigTables("183", "9500", "&7"));
        Assert.assertEquals("9", HistoricStageUtils.runEod2DigTables("209", "9500", "--"));
        //Inputs not handled by both tables
        Assert.assertEquals("9", HistoricStageUtils.runEod2DigTables("448", "8234", "0-"));
        Assert.assertEquals("9", HistoricStageUtils.runEod2DigTables("619", "9589", "35"));
    }

    @Test
    public void testRunEod0StageTable() {
        Assert.assertEquals("0", HistoricStageUtils.runEod0StageTable("403", "8000", "0-"));
        Assert.assertEquals("9", HistoricStageUtils.runEod0StageTable("403", "9600", "0-"));
        Assert.assertEquals("1", HistoricStageUtils.runEod0StageTable("403", "8000", "4-"));
        Assert.assertEquals("1", HistoricStageUtils.runEod0StageTable("679", "8000", "4-"));
        Assert.assertEquals("9", HistoricStageUtils.runEod0StageTable("679", "9970", "4-"));
        Assert.assertEquals("2", HistoricStageUtils.runEod0StageTable("676", "8000", "6-"));
        Assert.assertEquals("9", HistoricStageUtils.runEod0StageTable("765", "8000", "9&"));
        Assert.assertEquals("9", HistoricStageUtils.runEod0StageTable("000", "9800", "9&"));
        Assert.assertEquals("4", HistoricStageUtils.runEod0StageTable("678", "8000", "&-"));
        Assert.assertEquals("4", HistoricStageUtils.runEod0StageTable("750", "8000", "&-"));
        Assert.assertEquals("9", HistoricStageUtils.runEod0StageTable("670", "8000", "9&"));
        Assert.assertEquals("9", HistoricStageUtils.runEod0StageTable("800", "9990", "9&"));
        Assert.assertEquals("9", HistoricStageUtils.runEod0StageTable("400", "8000", "4&"));
    }

    @Test
    public void testFormattedSite() {
        Assert.assertEquals("456", HistoricStageUtils.formattedSite("C456"));
        Assert.assertEquals("456", HistoricStageUtils.formattedSite("456"));
        Assert.assertEquals("000", HistoricStageUtils.formattedSite("c000"));
        Assert.assertEquals("99999", HistoricStageUtils.formattedSite("C99999"));
        Assert.assertEquals("AT", HistoricStageUtils.formattedSite("CAT"));
    }

    @Test
    public void testApply2004Earlier() {
        HistoricStageInputDto input = new HistoricStageInputDto();
        input.setPrimarysite("C808");
        input.setHistologyIcdO3("9834");
        Assert.assertNull(HistoricStageUtils.apply2004Earlier(input).getResult());
        //coding system 4
        input.setEodCodingSys("4");
        input.setDateOfDiagnosisYear("1990");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        //Los-angeles cases 1988-1991
        input.setRegistryId("000001535");
        Assert.assertEquals(HistoricStageUtils.NOT_APPLICABLE, HistoricStageUtils.apply2004Earlier(input).getResult());
        //Leukemia dco cases
        input.setDateOfDiagnosisYear("1992");
        input.setTypeOfReportingSource("7");
        Assert.assertEquals(HistoricStageUtils.DISTANT, HistoricStageUtils.apply2004Earlier(input).getResult());
        //Non leukemia DCO cases
        input.setHistologyIcdO3("9763");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        input.setDateOfDiagnosisYear("1988");
        Assert.assertEquals(HistoricStageUtils.NOT_APPLICABLE, HistoricStageUtils.apply2004Earlier(input).getResult());
        input.setRegistryId("000001533");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        //EOD10 tables
        input.setTypeOfReportingSource("6");
        input.setPrimarysite("C101");
        input.setHistologyIcdO3("9130");
        input.setEodExtension("85");
        input.setEodLymphNodeInvolv("3");
        Assert.assertEquals(HistoricStageUtils.DISTANT, HistoricStageUtils.apply2004Earlier(input).getResult());

        //- Bladder cases (primary site: 670-679) staged to IS (0) get changed to Local (1)
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1992");
        input.setPrimarysite("C675");
        input.setHistologyIcdO3("9143");
        input.setEodCodingSys("4");
        input.setEodExtension("05");
        input.setEodLymphNodeInvolv("3");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, HistoricStageUtils.apply2004Earlier(input).getResult());

        //- Certain brain cases (primary site: 710-719 , hist icd-o-3: 8000-9529, 9540-9589) get set to unstaged (9)
        input.setPrimarysite("C711");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());

        //If anything is not handled
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1992");
        input.setEodCodingSys("4");
        input.setPrimarysite("C475");
        input.setHistologyIcdO3("9143");
        input.setEodExtension("05");
        input.setEodLymphNodeInvolv("3");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());

        //coding system 3
        //Eod Patch Table
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1992");
        input.setEodCodingSys("3");
        input.setPrimarysite("C475");
        input.setHistologyIcdO3("9829");
        input.setTypeOfReportingSource("6");
        Assert.assertEquals(HistoricStageUtils.DISTANT, HistoricStageUtils.apply2004Earlier(input).getResult());
        //Run EOD 4dig Stage table
        input.setPrimarysite("C025");
        input.setHistologyIcdO3("9100");
        input.setEodOld4DigitExtent("3");
        input.setEodOld4DigitNodes("8");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, HistoricStageUtils.apply2004Earlier(input).getResult());
        //If anything is not handled
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1992");
        input.setEodCodingSys("3");
        input.setPrimarysite("C569");
        input.setHistologyIcdO3("9100");
        input.setEodOld4DigitExtent("1");
        input.setEodOld4DigitNodes("6");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        //If site = 569 (ovary) and EOD 4dig Size = ‘02’ & EOD 4dig Ext > 0, set stage to distant (4)
        input.setEodOld4DigitSize("02");
        Assert.assertEquals(HistoricStageUtils.DISTANT, HistoricStageUtils.apply2004Earlier(input).getResult());

        //coding system 2
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1992");
        input.setEodCodingSys("2");
        input.setPrimarysite("C569");
        input.setHistologyIcdO3("9590");
        input.setEodOld13Digit("1111111111111");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        //Lung cases (Site 340-349):
        input.setPrimarysite("C344");
        input.setHistologyIcdO3("9000");
        input.setEodOld13Digit("111-&808-1000");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, HistoricStageUtils.apply2004Earlier(input).getResult());
        //Melanoma skin/vulva/penis cases (Sites 440-447, 510-519, 600-609; Histologies 8720 – 8799):
        input.setPrimarysite("C344");
        input.setHistologyIcdO3("8722");
        input.setEodOld13Digit("111--2&&-110&");
        Assert.assertEquals(HistoricStageUtils.DISTANT, HistoricStageUtils.apply2004Earlier(input).getResult());
        //Bladder cases (Site 670-679):
        input.setPrimarysite("C675");
        input.setHistologyIcdO3("9000");
        input.setEodOld13Digit("111-222220000");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, HistoricStageUtils.apply2004Earlier(input).getResult());
        //All other sites:
        input.setPrimarysite("C619");
        input.setHistologyIcdO3("9000");
        input.setEodOld13Digit("00001&7800000");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, HistoricStageUtils.apply2004Earlier(input).getResult());
        //If anything not handled
        input.setPrimarysite("C610");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());

        //coding system 1
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1992");
        input.setEodCodingSys("1");
        input.setPrimarysite("C152");
        input.setHistologyIcdO3("9544");
        //invalid 2-dig
        input.setEodOld2Digit("333");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        input.setEodOld2Digit("3");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        //2 dig direct stage
        input.setEodOld2Digit("5&");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, HistoricStageUtils.apply2004Earlier(input).getResult());
        //2 dig ext node stage
        input.setPrimarysite("C183");
        input.setHistologyIcdO3("9544");
        input.setEodOld2Digit("55");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, HistoricStageUtils.apply2004Earlier(input).getResult());
        //If anything not handled,
        input.setPrimarysite("C200");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());

        //coding system 0
        input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear("1992");
        input.setEodCodingSys("0");
        input.setPrimarysite("C152");
        input.setHistologyIcdO3("9600");
        input.setEodOld2Digit("5-");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
        input.setHistologyIcdO3("9500");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, HistoricStageUtils.apply2004Earlier(input).getResult());
        input.setEodOld2Digit("4-");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, HistoricStageUtils.apply2004Earlier(input).getResult());
        input.setPrimarysite("C425");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, HistoricStageUtils.apply2004Earlier(input).getResult());
    }

    @Test
    public void testComputeHistoricStage() {

        //Test 2004+ non leukemia DCO cases
        Map<String, String> rec = new HashMap<>();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2012");
        rec.put(_PROP_TYPE_OF_REPORTING_SOURCE, "7");
        rec.put(_PROP_PRIMARY_SITE, "808");
        rec.put(_PROP_HISTOLOGY_3, "9823");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        //Test 2004+ leukemia DCO cases
        rec.put(_PROP_PRIMARY_SITE, "424");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "100");
        rec.put(_PROP_CS_LYMPH_NODES, "100");
        rec.put(_PROP_CS_METS_AT_DX, "10");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_METS_AT_DX, "00");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        //Test 2004+ Non DCO cases
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2004");
        rec.put(_PROP_TYPE_OF_REPORTING_SOURCE, "1");
        rec.put(_PROP_PRIMARY_SITE, "003");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_HISTOLOGY_3, "9130");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "405");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_LYMPH_NODES, "110");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_METS_AT_DX, "00");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());

        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2013");
        rec.put(_PROP_PRIMARY_SITE, "099");
        rec.put(_PROP_HISTOLOGY_3, "9935");
        rec.put(_PROP_CS_EXTENSION, "900");
        rec.put(_PROP_CS_LYMPH_NODES, "310");
        rec.put(_PROP_CS_METS_AT_DX, "40");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "800");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());

        //Test Special case for longevity consistency
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2007");
        rec.put(_PROP_PRIMARY_SITE, "400");
        rec.put(_PROP_CS_EXTENSION, "800");
        rec.put(_PROP_CS_LYMPH_NODES, "100");
        rec.put(_PROP_CS_METS_AT_DX, "99");
        rec.put(_PROP_HISTOLOGY_3, "9827");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());

        //Test special case for corpus schemas per Lynn Ries/Jennifer Ruhl starting Nov11 sub
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2010");
        rec.put(_PROP_PRIMARY_SITE, "541");
        rec.put(_PROP_HISTOLOGY_3, "9882");
        rec.put(_PROP_CS_EXTENSION, "530");
        rec.put(_PROP_CS_LYMPH_NODES, "000");
        rec.put(_PROP_CS_METS_AT_DX, "00");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_SITE_SPECIFIC_FACTOR2, "010");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());

        //test Special Breast Processing
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2009");
        rec.put(_PROP_PRIMARY_SITE, "505");
        rec.put(_PROP_HISTOLOGY_3, "8000");
        rec.put(_PROP_CS_EXTENSION, "710");
        rec.put(_PROP_CS_LYMPH_NODES, "130");
        rec.put(_PROP_CS_METS_AT_DX, "00");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "070");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_BEHAVIOR_3, "2");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_BEHAVIOR_3, "3");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "050");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "710");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());

        //Test special Pleura Processing
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2008");
        rec.put(_PROP_PRIMARY_SITE, "384");
        rec.put(_PROP_HISTOLOGY_3, "9995");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "400");
        rec.put(_PROP_CS_LYMPH_NODES, "800");
        rec.put(_PROP_CS_METS_AT_DX, "10");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "100");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_SITE_SPECIFIC_FACTOR1, "40");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_SITE_SPECIFIC_FACTOR1, "10");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_SITE_SPECIFIC_FACTOR1, "999");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_SITE_SPECIFIC_FACTOR1, "20");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "500");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_SITE_SPECIFIC_FACTOR1, "10");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "670");
        Assert.assertEquals(HistoricStageUtils.NEED_REVIEW, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "770");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        rec.put(_PROP_CS_EXTENSION, "999");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());

        //Test 2004 earlier cases
        rec.clear();
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_PRIMARY_SITE, "");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "1899");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "1990");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_PRIMARY_SITE, "805");
        rec.put(_PROP_HISTOLOGY_3, "8733");
        Assert.assertEquals(HistoricStageUtils.NOT_APPLICABLE, computeHistoricStage(rec).getResult());
        //coding system 4
        rec.put(_PROP_EOD_CODING_SYSTEM, "4");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        //test DCO cases
        rec.put(_PROP_TYPE_OF_REPORTING_SOURCE, "7");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_PRIMARY_SITE, "505");
        rec.put(_PROP_HISTOLOGY_3, "9733");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        //test Los angeles 1988-1991 cases
        rec.put(_PROP_REGISTRY_ID, "000001535");
        Assert.assertEquals(HistoricStageUtils.NOT_APPLICABLE, computeHistoricStage(rec).getResult());
        //Non dco cases, coding system 4
        rec.put(_PROP_REGISTRY_ID, "000001536");
        rec.put(_PROP_TYPE_OF_REPORTING_SOURCE, "3");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_PRIMARY_SITE, "C005");
        rec.put(_PROP_HISTOLOGY_3, "9542");
        rec.put(_PROP_EOD10_LYMPH_NODES, "3");
        rec.put(_PROP_EOD10_EXTENSION, "10");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
        //coding system 4, breast cases
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "1990");
        rec.put(_PROP_EOD_CODING_SYSTEM, "4");
        rec.put(_PROP_PRIMARY_SITE, "C505");
        rec.put(_PROP_EOD10_EXTENSION, "05");
        rec.put(_PROP_HISTOLOGY_3, "9127");
        rec.put(_PROP_EOD10_LYMPH_NODES, "0");
        Assert.assertEquals(HistoricStageUtils.IN_SITU, computeHistoricStage(rec).getResult());
        rec.put(_PROP_BEHAVIOR_3, "2");
        Assert.assertEquals(HistoricStageUtils.IN_SITU, computeHistoricStage(rec).getResult());
        rec.put(_PROP_BEHAVIOR_3, "3");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, computeHistoricStage(rec).getResult());
        //coding system 4, special melanoma cases
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "1990");
        rec.put(_PROP_EOD_CODING_SYSTEM, "4");
        rec.put(_PROP_HISTOLOGY_3, "8770");
        rec.put(_PROP_PRIMARY_SITE, "C444");
        rec.put(_PROP_EOD10_EXTENSION, "99");
        rec.put(_PROP_EOD10_LYMPH_NODES, "3");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
        //coding system 3, EOD patch table
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "3");
        rec.put(_PROP_TYPE_OF_REPORTING_SOURCE, "3");
        rec.put(_PROP_HISTOLOGY_3, "9805");
        rec.put(_PROP_PRIMARY_SITE, "C444");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        //coding system 3, EOD 4 dig tables
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "3");
        rec.put(_PROP_HISTOLOGY_3, "9133");
        rec.put(_PROP_PRIMARY_SITE, "C035");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        rec.put(_PROP_EOD4_DIGIT_EXTENSION, "0");
        rec.put(_PROP_EOD4_DIGIT_NODES, "0");
        Assert.assertEquals(HistoricStageUtils.IN_SITU, computeHistoricStage(rec).getResult());
        rec.put(_PROP_EOD4_DIGIT_EXTENSION, "1");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, computeHistoricStage(rec).getResult());
        //coding system 3, ovary
        rec.put(_PROP_PRIMARY_SITE, "C569");
        rec.put(_PROP_EOD4_DIGIT_SIZE, "02");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        //coding system 2, Lung cases
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "2");
        rec.put(_PROP_HISTOLOGY_3, "9133");
        rec.put(_PROP_PRIMARY_SITE, "C347");
        rec.put(_PROP_EOD13_DIGIT, "111-1&986-000");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
        //coding system 2, Melanoma cases
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "2");
        rec.put(_PROP_HISTOLOGY_3, "8733");
        rec.put(_PROP_PRIMARY_SITE, "C447");
        rec.put(_PROP_EOD13_DIGIT, "111-23-8--100");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        //coding system 2, Bladder cases
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "2");
        rec.put(_PROP_HISTOLOGY_3, "8733");
        rec.put(_PROP_PRIMARY_SITE, "C677");
        rec.put(_PROP_EOD13_DIGIT, "111-83-821000");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
        //coding system 2, general stage cases
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "2");
        rec.put(_PROP_HISTOLOGY_3, "8733");
        rec.put(_PROP_PRIMARY_SITE, "C502");
        rec.put(_PROP_EOD13_DIGIT, "1111-&-821000");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
        //coding system 2,other histologies than 8000-9589
        rec.put(_PROP_HISTOLOGY_3, "9590");
        Assert.assertEquals(HistoricStageUtils.UNSTAGED, computeHistoricStage(rec).getResult());
        //coding system 1, EOD 2 dig direct stage
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "1");
        rec.put(_PROP_HISTOLOGY_3, "8733");
        rec.put(_PROP_PRIMARY_SITE, "C645");
        rec.put(_PROP_EOD2_DIGIT, "2&");
        Assert.assertEquals(HistoricStageUtils.LOCALIZED, computeHistoricStage(rec).getResult());
        //coding system 1, EOD 2 dig Ext Node stage
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "1");
        rec.put(_PROP_HISTOLOGY_3, "8700");
        rec.put(_PROP_PRIMARY_SITE, "C160");
        rec.put(_PROP_EOD2_DIGIT, "&2");
        Assert.assertEquals(HistoricStageUtils.DISTANT, computeHistoricStage(rec).getResult());
        //coding system 0
        rec.clear();
        rec.put(_PROP_DATE_OF_DIAGNOSIS_YEAR, "2000");
        rec.put(_PROP_EOD_CODING_SYSTEM, "0");
        rec.put(_PROP_HISTOLOGY_3, "8700");
        rec.put(_PROP_PRIMARY_SITE, "C560");
        rec.put(_PROP_EOD2_DIGIT, "7-");
        Assert.assertEquals(HistoricStageUtils.REGIONAL, computeHistoricStage(rec).getResult());
    }

    // using a map of properties has been deprecated, but so many calls are used in this unit test that it was easier to reproduce the deprecated method here...
    private HistoricStageResultsDto computeHistoricStage(Map<String, String> rec) {
        HistoricStageInputDto input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear(rec.get(_PROP_DATE_OF_DIAGNOSIS_YEAR));
        input.setPrimarysite(rec.get(_PROP_PRIMARY_SITE));
        input.setHistologyIcdO3(rec.get(_PROP_HISTOLOGY_3));
        input.setBehaviorIcdO3(rec.get(_PROP_BEHAVIOR_3));
        input.setTypeOfReportingSource(rec.get(_PROP_TYPE_OF_REPORTING_SOURCE));
        input.setRegistryId(rec.get(_PROP_REGISTRY_ID));
        input.setCsExtension(rec.get(_PROP_CS_EXTENSION));
        input.setCsLymphNodes(rec.get(_PROP_CS_LYMPH_NODES));
        input.setCsMetsAtDx(rec.get(_PROP_CS_METS_AT_DX));
        input.setCsSiteSpecificFactor1(rec.get(_PROP_CS_SITE_SPECIFIC_FACTOR1));
        input.setCsSiteSpecificFactor2(rec.get(_PROP_CS_SITE_SPECIFIC_FACTOR2));
        input.setEodCodingSys(rec.get(_PROP_EOD_CODING_SYSTEM));
        input.setEodExtension(rec.get(_PROP_EOD10_EXTENSION));
        input.setEodLymphNodeInvolv(rec.get(_PROP_EOD10_LYMPH_NODES));
        input.setEodOld4DigitExtent(rec.get(_PROP_EOD4_DIGIT_EXTENSION));
        input.setEodOld4DigitNodes(rec.get(_PROP_EOD4_DIGIT_NODES));
        input.setEodOld4DigitSize(rec.get(_PROP_EOD4_DIGIT_SIZE));
        input.setEodOld13Digit(rec.get(_PROP_EOD13_DIGIT));
        input.setEodOld2Digit(rec.get(_PROP_EOD2_DIGIT));
        return HistoricStageUtils.computeHistoricStage(input);

    }
}
