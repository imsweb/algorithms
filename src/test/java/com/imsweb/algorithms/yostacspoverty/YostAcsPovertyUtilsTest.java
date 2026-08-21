/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.yostacspoverty;

import org.junit.Assert;
import org.junit.Test;

public class YostAcsPovertyUtilsTest {

    @Test
    public void testYostAcsPoverty() {
        YostAcsPovertyInputDto idto = new YostAcsPovertyInputDto();
        YostAcsPovertyOutputDto odto;

        // invalid state
        idto.setAddressAtDxState("12345");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2021");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("A", odto.getYostQuintileUS());
        Assert.assertEquals("A", odto.getYostQuintileState());
        Assert.assertEquals("A", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("A", odto.getAcsPctPovWhite());
        Assert.assertEquals("A", odto.getAcsPctPovBlack());
        Assert.assertEquals("A", odto.getAcsPctPovAIAN());
        Assert.assertEquals("A", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("A", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("A", odto.getAcsPctPovHispanic());

        // invalid county
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("ABCDE");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2021");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("A", odto.getYostQuintileUS());
        Assert.assertEquals("A", odto.getYostQuintileState());
        Assert.assertEquals("A", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("A", odto.getAcsPctPovWhite());
        Assert.assertEquals("A", odto.getAcsPctPovBlack());
        Assert.assertEquals("A", odto.getAcsPctPovAIAN());
        Assert.assertEquals("A", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("A", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("A", odto.getAcsPctPovHispanic());

        // invalid 2010 census tract
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("ABCDEF");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2012");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("A", odto.getYostQuintileUS());
        Assert.assertEquals("A", odto.getYostQuintileState());
        Assert.assertEquals("A", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("A", odto.getAcsPctPovWhite());
        Assert.assertEquals("A", odto.getAcsPctPovBlack());
        Assert.assertEquals("A", odto.getAcsPctPovAIAN());
        Assert.assertEquals("A", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("A", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("A", odto.getAcsPctPovHispanic());

        // invalid 2020 census tract
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("ABCDEF");
        idto.setDateOfDiagnosis("2020");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("A", odto.getYostQuintileUS());
        Assert.assertEquals("A", odto.getYostQuintileState());
        Assert.assertEquals("A", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("A", odto.getAcsPctPovWhite());
        Assert.assertEquals("A", odto.getAcsPctPovBlack());
        Assert.assertEquals("A", odto.getAcsPctPovAIAN());
        Assert.assertEquals("A", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("A", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("A", odto.getAcsPctPovHispanic());

        // invalid date of dx
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("ABCDEF");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("A", odto.getYostQuintileUS());
        Assert.assertEquals("A", odto.getYostQuintileState());
        Assert.assertEquals("A", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("A", odto.getAcsPctPovWhite());
        Assert.assertEquals("A", odto.getAcsPctPovBlack());
        Assert.assertEquals("A", odto.getAcsPctPovAIAN());
        Assert.assertEquals("A", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("A", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("A", odto.getAcsPctPovHispanic());

        // county not reported (ie, 000)
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("000");
        idto.setCensusTract2010("");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2020");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("B", odto.getYostQuintileUS());
        Assert.assertEquals("B", odto.getYostQuintileState());
        Assert.assertEquals("B", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("B", odto.getAcsPctPovWhite());
        Assert.assertEquals("B", odto.getAcsPctPovBlack());
        Assert.assertEquals("B", odto.getAcsPctPovAIAN());
        Assert.assertEquals("B", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("B", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("B", odto.getAcsPctPovHispanic());

        // blank state
        idto.setAddressAtDxState("");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2018");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("D", odto.getYostQuintileUS());
        Assert.assertEquals("D", odto.getYostQuintileState());
        Assert.assertEquals("D", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("D", odto.getAcsPctPovWhite());
        Assert.assertEquals("D", odto.getAcsPctPovBlack());
        Assert.assertEquals("D", odto.getAcsPctPovAIAN());
        Assert.assertEquals("D", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("D", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("D", odto.getAcsPctPovHispanic());

        // blank county
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2018");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("D", odto.getYostQuintileUS());
        Assert.assertEquals("D", odto.getYostQuintileState());
        Assert.assertEquals("D", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("D", odto.getAcsPctPovWhite());
        Assert.assertEquals("D", odto.getAcsPctPovBlack());
        Assert.assertEquals("D", odto.getAcsPctPovAIAN());
        Assert.assertEquals("D", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("D", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("D", odto.getAcsPctPovHispanic());

        // blank census tract 2010
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2012");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("D", odto.getYostQuintileUS());
        Assert.assertEquals("D", odto.getYostQuintileState());
        Assert.assertEquals("D", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("D", odto.getAcsPctPovWhite());
        Assert.assertEquals("D", odto.getAcsPctPovBlack());
        Assert.assertEquals("D", odto.getAcsPctPovAIAN());
        Assert.assertEquals("D", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("D", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("D", odto.getAcsPctPovHispanic());

        // blank census tract 2020
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("");
        idto.setDateOfDiagnosis("2018");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("D", odto.getYostQuintileUS());
        Assert.assertEquals("D", odto.getYostQuintileState());
        Assert.assertEquals("D", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("D", odto.getAcsPctPovWhite());
        Assert.assertEquals("D", odto.getAcsPctPovBlack());
        Assert.assertEquals("D", odto.getAcsPctPovAIAN());
        Assert.assertEquals("D", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("D", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("D", odto.getAcsPctPovHispanic());

        // blank date of diagnosis
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("D", odto.getYostQuintileUS());
        Assert.assertEquals("D", odto.getYostQuintileState());
        Assert.assertEquals("D", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("D", odto.getAcsPctPovWhite());
        Assert.assertEquals("D", odto.getAcsPctPovBlack());
        Assert.assertEquals("D", odto.getAcsPctPovAIAN());
        Assert.assertEquals("D", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("D", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("D", odto.getAcsPctPovHispanic());

        // 2005 -> no data available
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setCensusTract2020("");
        idto.setDateOfDiagnosis("2005");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("C", odto.getYostQuintileUS());
        Assert.assertEquals("C", odto.getYostQuintileState());
        Assert.assertEquals("C", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("C", odto.getAcsPctPovWhite());
        Assert.assertEquals("C", odto.getAcsPctPovBlack());
        Assert.assertEquals("C", odto.getAcsPctPovAIAN());
        Assert.assertEquals("C", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("C", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("C", odto.getAcsPctPovHispanic());

        // test a SEER recode
        idto.setAddressAtDxState("LO");
        idto.setCountyAtDxAnalysis("071");
        idto.setCensusTract2010("007903");
        idto.setCensusTract2020("");
        idto.setDateOfDiagnosis("2010");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("5", odto.getYostQuintileUS());
        Assert.assertEquals("4", odto.getYostQuintileState());
        Assert.assertEquals("3.03", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("3.81", odto.getAcsPctPovWhite());
        Assert.assertEquals("4.71", odto.getAcsPctPovBlack());
        Assert.assertEquals("", odto.getAcsPctPovAIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("4.93", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.70", odto.getAcsPctPovHispanic());

        // 2007 using census2010 with yearForLookup 2008
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setCensusTract2020("");
        idto.setDateOfDiagnosis("2007");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("2", odto.getYostQuintileUS());
        Assert.assertEquals("3", odto.getYostQuintileState());
        Assert.assertEquals("14.76", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("0.00", odto.getAcsPctPovWhite());
        Assert.assertEquals("25.06", odto.getAcsPctPovBlack());
        Assert.assertEquals("", odto.getAcsPctPovAIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("0.00", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPovHispanic());

        // 2008 using census2010
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setCensusTract2020("");
        idto.setDateOfDiagnosis("2008");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("2", odto.getYostQuintileUS());
        Assert.assertEquals("3", odto.getYostQuintileState());
        Assert.assertEquals("14.76", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("0.00", odto.getAcsPctPovWhite());
        Assert.assertEquals("25.06", odto.getAcsPctPovBlack());
        Assert.assertEquals("", odto.getAcsPctPovAIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("0.00", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPovHispanic());

        // 2010 using census2010
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setCensusTract2020("");
        idto.setDateOfDiagnosis("2010");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("2", odto.getYostQuintileUS());
        Assert.assertEquals("3", odto.getYostQuintileState());
        Assert.assertEquals("10.51", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("7.85", odto.getAcsPctPovWhite());
        Assert.assertEquals("12.40", odto.getAcsPctPovBlack());
        Assert.assertEquals("", odto.getAcsPctPovAIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("7.85", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPovHispanic());

        // 2015 using census2010
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setCensusTract2020("");
        idto.setDateOfDiagnosis("20150101");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("5", odto.getYostQuintileUS());
        Assert.assertEquals("5", odto.getYostQuintileState());
        Assert.assertEquals("3.85", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("3.04", odto.getAcsPctPovWhite());
        Assert.assertEquals("0.00", odto.getAcsPctPovBlack());
        Assert.assertEquals("6.34", odto.getAcsPctPovAIAN());
        Assert.assertEquals("23.08", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("3.23", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPovHispanic());

        // 2020 using census2020
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2020");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("4", odto.getYostQuintileUS());
        Assert.assertEquals("4", odto.getYostQuintileState());
        Assert.assertEquals("7.35", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("2.51", odto.getAcsPctPovWhite());
        Assert.assertEquals("18.18", odto.getAcsPctPovBlack());
        Assert.assertEquals("37.30", odto.getAcsPctPovAIAN());
        Assert.assertEquals("3.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("2.60", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("11.76", odto.getAcsPctPovHispanic());

        // 2021 using census2020
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2021");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("4", odto.getYostQuintileUS());
        Assert.assertEquals("5", odto.getYostQuintileState());
        Assert.assertEquals("7.12", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("3.03", odto.getAcsPctPovWhite());
        Assert.assertEquals("13.46", odto.getAcsPctPovBlack());
        Assert.assertEquals("34.10", odto.getAcsPctPovAIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("3.09", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("8.09", odto.getAcsPctPovHispanic());

        // 2025 using census2020 with yearForLookup 2022
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2025");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("4", odto.getYostQuintileUS());
        Assert.assertEquals("4", odto.getYostQuintileState());
        Assert.assertEquals("7.11", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("4.10", odto.getAcsPctPovWhite());
        Assert.assertEquals("20.44", odto.getAcsPctPovBlack());
        Assert.assertEquals("25.73", odto.getAcsPctPovAIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("4.19", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("6.52", odto.getAcsPctPovHispanic());
    }
}
