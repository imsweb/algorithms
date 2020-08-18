/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.yostacspoverty;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.ruralurban.RuralUrbanUtils;

public class YostAcsPovertyUtilsTest {

    // this properties have been deprecated in the main class but so many tests use them that it was easier to copy them here
    private static final String _PROP_STATE_DX = "addressAtDxState";
    private static final String _PROP_COUNTY_DX_ANALYSIS = "countyAtDxAnalysis";
    private static final String _PROP_CENSUS_TRACT_2010 = "censusTract2010";

    @Test
    public void assertInfo() {
        Assert.assertNotNull(RuralUrbanUtils.ALG_VERSION);
        Assert.assertNotNull(RuralUrbanUtils.ALG_NAME);
        Assert.assertNotNull(RuralUrbanUtils.ALG_INFO);
    }

    @Test
    public void testYostAcsPoverty() {
        YostAcsPovertyInputDto idto = new YostAcsPovertyInputDto();
        idto.setAddressAtDxState("AL");
        idto.setCountyAtDxAnalysis("001");
        idto.setCensusTract2010("020200");
        idto.setDateOfDiagnosis("20100101");

        YostAcsPovertyOutputDto odto = YostAcsPovertyUtils.computeYostAcsPovertyData(idto);

        Assert.assertEquals("2", odto.getYostQuintile0610US());
        Assert.assertEquals("3", odto.getYostQuintile0610State());
        Assert.assertEquals("14.76", odto.getAcsPctPov0610AllRaces());
        Assert.assertEquals("0.00", odto.getAcsPctPov0610White());
        Assert.assertEquals("25.06", odto.getAcsPctPov0610Black());
        Assert.assertEquals("", odto.getAcsPctPov0610AIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPov0610AsianNHOPI());
        Assert.assertEquals("", odto.getAcsPctPov0610OtherMulti());
        Assert.assertEquals("0.00", odto.getAcsPctPov0610WhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPov0610Hispanic());
        Assert.assertEquals("2", odto.getYostQuintile1014US());
        Assert.assertEquals("3", odto.getYostQuintile1014State());
        Assert.assertEquals("18.16", odto.getAcsPctPov1014AllRaces());
        Assert.assertEquals("27.76", odto.getAcsPctPov1014White());
        Assert.assertEquals("13.45", odto.getAcsPctPov1014Black());
        Assert.assertEquals("", odto.getAcsPctPov1014AIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPov1014AsianNHOPI());
        Assert.assertEquals("6.58", odto.getAcsPctPov1014OtherMulti());
        Assert.assertEquals("28.25", odto.getAcsPctPov1014WhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPov1014Hispanic());
        Assert.assertEquals("2", odto.getYostQuintile1418US());
        Assert.assertEquals("3", odto.getYostQuintile1418State());
        Assert.assertEquals("17.88", odto.getAcsPctPov1418AllRaces());
        Assert.assertEquals("4.94", odto.getAcsPctPov1418White());
        Assert.assertEquals("27.49", odto.getAcsPctPov1418Black());
        Assert.assertEquals("", odto.getAcsPctPov1418AIAN());
        Assert.assertEquals("0.00", odto.getAcsPctPov1418AsianNHOPI());
        Assert.assertEquals("0.00", odto.getAcsPctPov1418OtherMulti());
        Assert.assertEquals("5.01", odto.getAcsPctPov1418WhiteNonHisp());
        Assert.assertEquals("0.00", odto.getAcsPctPov1418Hispanic());

    }
}
