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

        // 2005 -> no data available
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setDateOfDiagnosis("2005");
        YostAcsPovertyOutputDto odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertNull(odto.getYostQuintileUS());
        Assert.assertNull(odto.getYostQuintileState());
        Assert.assertNull(odto.getAcsPctPovAllRaces());
        Assert.assertNull(odto.getAcsPctPovWhite());
        Assert.assertNull(odto.getAcsPctPovBlack());
        Assert.assertNull(odto.getAcsPctPovAIAN());
        Assert.assertNull(odto.getAcsPctPovAsianNHOPI());
        Assert.assertNull(odto.getAcsPctPovWhiteNonHisp());
        Assert.assertNull(odto.getAcsPctPovHispanic());

        // 2007 using census2010 with year 2008
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
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

        // 2016 using census2020 with year 2018
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setDateOfDiagnosis("2016");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertNull(odto.getYostQuintileState());
        Assert.assertNull(odto.getAcsPctPovAllRaces());
        Assert.assertNull(odto.getAcsPctPovWhite());
        Assert.assertNull(odto.getAcsPctPovBlack());
        Assert.assertNull(odto.getAcsPctPovAIAN());
        Assert.assertNull(odto.getAcsPctPovAsianNHOPI());
        Assert.assertNull(odto.getAcsPctPovWhiteNonHisp());
        Assert.assertNull(odto.getAcsPctPovHispanic());

        // 2018 using census2020
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setDateOfDiagnosis("2018");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertNull(odto.getYostQuintileState());
        Assert.assertNull(odto.getAcsPctPovAllRaces());
        Assert.assertNull(odto.getAcsPctPovWhite());
        Assert.assertNull(odto.getAcsPctPovBlack());
        Assert.assertNull(odto.getAcsPctPovAIAN());
        Assert.assertNull(odto.getAcsPctPovAsianNHOPI());
        Assert.assertNull(odto.getAcsPctPovWhiteNonHisp());
        Assert.assertNull(odto.getAcsPctPovHispanic());

        // 2020 using census2020
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
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
        idto.setCensusTract2010("001702");
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

        // 2025 using census2020 with year 2021
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2025");
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
    }
}
