/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

import com.imsweb.algorithms.ruralurban.RuralUrbanInputDto;
import com.imsweb.algorithms.ruralurban.RuralUrbanOutputDto;
import com.imsweb.algorithms.ruralurban.RuralUrbanUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class PrcdaUihoUtilsTest {

    private static final String _PROP_STATE_DX = "addressAtDxState";
    private static final String _PROP_COUNTY_DX = "addressAtDxCounty";

    @Test
    public void assertInfo() {
        Assert.assertNotNull(PrcdaUihoUtils.ALG_VERSION);
        Assert.assertNotNull(PrcdaUihoUtils.ALG_NAME);
        Assert.assertNotNull(PrcdaUihoUtils.ALG_INFO);
    }

    @Test
    public void testComputeRuralUrbanContinuum() {

        List<String> valid_states = Arrays.asList("AZ", "GA", "NV", "SE", "TX");
        List<String> prcda_states = Arrays.asList("AK", "CT", "NV", "OK", "SC");
        List<String> inval_states = Arrays.asList("", "99", null);
        List<String> valid_county = Arrays.asList("001", "003", "005", "007", "999");
        List<String> inval_county = Arrays.asList("", "ABC", "01", null);

        List<String> states = new ArrayList<>();
        states.addAll(valid_states);
        states.addAll(inval_states);
        states.addAll(prcda_states);

        List<String> counties = new ArrayList<>();
        counties.addAll(valid_county);
        counties.addAll(inval_county);

        PrcdaUihoInputDto input = new PrcdaUihoInputDto();

        for (String state : states) {
            for (String county : counties) {
                input.setAddressAtDxState(state);
                input.setAddressAtDxCounty(county);

                if (inval_states.contains(state) || inval_county.contains(county)) {
                    Assert.assertEquals("9", PrcdaUihoUtils.computerPrcdaUiho(input).getPRCDA());
                    Assert.assertEquals("9", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHO());
                    Assert.assertEquals("99", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHOFacility());
                } else {
                    String stCnty = state + county;
                    if (prcda_states.contains(state)) {
                        Assert.assertEquals("1", PrcdaUihoUtils.computerPrcdaUiho(input).getPRCDA());
                    } else if ("AZ001".equals(stCnty) || "AZ003".equals(stCnty) || "AZ007".equals(stCnty)) {
                        Assert.assertEquals("1", PrcdaUihoUtils.computerPrcdaUiho(input).getPRCDA());
                        Assert.assertEquals("0", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHO());
                        Assert.assertEquals("00", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHOFacility());
                    } else if ("AZ005".equals(stCnty)) {
                        Assert.assertEquals("1", PrcdaUihoUtils.computerPrcdaUiho(input).getPRCDA());
                        Assert.assertEquals("1", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHO());
                        Assert.assertEquals("01", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHOFacility());
                    } else if ("AZ999".equals(stCnty)) {
                        Assert.assertEquals("0", PrcdaUihoUtils.computerPrcdaUiho(input).getPRCDA());
                        Assert.assertEquals("0", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHO());
                        Assert.assertEquals("00", PrcdaUihoUtils.computerPrcdaUiho(input).getUIHOFacility());
                    }
                }
            }
        }
    }
}
