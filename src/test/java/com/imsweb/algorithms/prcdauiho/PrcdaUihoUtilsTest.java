/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_INVALID;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_YES;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_INVALID;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_NONE;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_INVALID;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_NO;

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

        List<String> valid_states = Arrays.asList("AZ", "CO", "GA", "NV", "SE");
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
                PrcdaUihoOutputDto output = PrcdaUihoUtils.computerPrcdaUiho(input);
                if (inval_states.contains(state) || inval_county.contains(county)) {
                    Assert.assertEquals(PRCDA_INVALID, output.getPRCDA());
                    Assert.assertEquals(UIHO_INVALID, output.getUIHO());
                    Assert.assertEquals(UIHO_FACILITY_INVALID, output.getUIHOFacility());
                }
                else {
                    String stCnty = state + county;
                    if (prcda_states.contains(state)) {
                        Assert.assertEquals(PRCDA_YES, output.getPRCDA());
                    }
                    else if ("AZ001".equals(stCnty) || "AZ003".equals(stCnty) || "AZ007".equals(stCnty)
                            || "CO007".equals(stCnty) || "NV003".equals(stCnty) || "NV007".equals(stCnty)
                            || "NV009".equals(stCnty) || "SE007".equals(stCnty) || "SE009".equals(stCnty)) {
                        Assert.assertEquals("1", output.getPRCDA());
                        Assert.assertEquals("0", output.getUIHO());
                        Assert.assertEquals("00", output.getUIHOFacility());
                    }
                    else if ("AZ005".equals(stCnty)) {
                        Assert.assertEquals("1", output.getPRCDA());
                        Assert.assertEquals("1", output.getUIHO());
                        Assert.assertEquals("01", output.getUIHOFacility());
                    }
                    else if ("CO001".equals(stCnty) || "CO005".equals(stCnty)) {
                        Assert.assertEquals("0", output.getPRCDA());
                        Assert.assertEquals("1", output.getUIHO());
                        Assert.assertEquals("12", output.getUIHOFacility());
                    }
                    else if ("NV001".equals(stCnty) || "NV005".equals(stCnty)) {
                        Assert.assertEquals("1", output.getPRCDA());
                        Assert.assertEquals("1", output.getUIHO());
                        Assert.assertEquals("24", output.getUIHOFacility());
                    }
                    else {
                        Assert.assertEquals(PRCDA_NO, output.getPRCDA());
                        Assert.assertEquals(UIHO_NO, output.getUIHO());
                        Assert.assertEquals(UIHO_FACILITY_NONE, output.getUIHOFacility());
                    }
                }
            }
        }
    }
}
