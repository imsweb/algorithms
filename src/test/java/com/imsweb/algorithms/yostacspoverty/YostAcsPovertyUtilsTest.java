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

        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setDateOfDiagnosis("20100101");
        YostAcsPovertyOutputDto odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("2", odto.getYostQuintileUS());
        Assert.assertEquals("3", odto.getYostQuintileState());
        Assert.assertEquals("10.51", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("7.85", odto.getAcsPctPovWhite());
        Assert.assertEquals("12.40", odto.getAcsPctPovBlack());
        Assert.assertEquals("", odto.getAcsPctPovAIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("20.00", odto.getAcsPctPovOtherMulti());
        Assert.assertEquals("7.85", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPovHispanic());

        idto.setAddressAtDxState("AK");
        idto.setCountyAtDxAnalysis("020");
        idto.setCensusTract2010("001702");
        idto.setDateOfDiagnosis("2017");
        odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);
        Assert.assertEquals("5", odto.getYostQuintileUS());
        Assert.assertEquals("4", odto.getYostQuintileState());
        Assert.assertEquals("3.59", odto.getAcsPctPovAllRaces());
        Assert.assertEquals("2.65", odto.getAcsPctPovWhite());
        Assert.assertEquals("0.00", odto.getAcsPctPovBlack());
        Assert.assertEquals("20.65", odto.getAcsPctPovAIAN());
        Assert.assertEquals("7.81", odto.getAcsPctPovAsianNHOPI());
        Assert.assertEquals("1.15", odto.getAcsPctPovOtherMulti());
        Assert.assertEquals("2.88", odto.getAcsPctPovWhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPovHispanic());
    }
}
