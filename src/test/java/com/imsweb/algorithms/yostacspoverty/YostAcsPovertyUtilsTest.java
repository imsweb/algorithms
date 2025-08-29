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

        // 2007 using census2010 -> no data available
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setDateOfDiagnosis("2007");
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

        // 2017 using census2010
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setDateOfDiagnosis("20170101");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("5", odto.getYostQuintileUS());
        Assert.assertEquals("4", odto.getYostQuintileState());
        Assert.assertEquals("3.59", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("2.65", odto.getAcsPctPovWhite());
        Assert.assertEquals("0.00", odto.getAcsPctPovBlack());
        Assert.assertEquals("20.65", odto.getAcsPctPovAIAN());
        Assert.assertEquals("7.81", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("2.88", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPovHispanic());

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

        // 2025 using census2020 -> no data available
        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2020("001702");
        idto.setDateOfDiagnosis("2025");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertNull(odto.getYostQuintileState());
        Assert.assertNull(odto.getAcsPctPovAllRaces());
        Assert.assertNull(odto.getAcsPctPovWhite());
        Assert.assertNull(odto.getAcsPctPovBlack());
        Assert.assertNull(odto.getAcsPctPovAIAN());
        Assert.assertNull(odto.getAcsPctPovAsianNHOPI());
        Assert.assertNull(odto.getAcsPctPovWhiteNonHisp());
        Assert.assertNull(odto.getAcsPctPovHispanic());
    }
}
