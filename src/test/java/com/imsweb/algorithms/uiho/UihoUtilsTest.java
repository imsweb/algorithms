/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.uiho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.prcda.PrcdaUtils;

import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_CITY_NONE;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_CITY_UNKNOWN;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_NO;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_UNKNOWN;

public class UihoUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(PrcdaUtils.ALG_VERSION);
        Assert.assertNotNull(PrcdaUtils.ALG_NAME);
    }

    @Test
    public void testComputePrcdaUiho() {

        List<String> validStates = Arrays.asList("AL", "AK", "AZ", "CO", "GA", "MN", "NV", "SE", "VA", "ON", "ZZ");
        List<String> invalStates = Arrays.asList("", "99", null);
        List<String> validCounty = Arrays.asList("001", "003", "005", "007", "017", "097");
        List<String> invalCounty = Arrays.asList("", "ABC", "01", "999", null);

        List<String> states = new ArrayList<>();
        states.addAll(validStates);
        states.addAll(invalStates);

        List<String> counties = new ArrayList<>();
        counties.addAll(validCounty);
        counties.addAll(invalCounty);

        UihoInputDto input = new UihoInputDto();

        for (String state : states) {
            for (String county : counties) {
                input.setAddressAtDxState(state);
                input.setAddressAtDxCounty(county);
                input.applyRecodes();
                UihoOutputDto output = UihoUtils.computeUiho(input);
                String stCnty = state + county;

                if (invalStates.contains(input.getAddressAtDxState()) || invalCounty.contains(input.getAddressAtDxCounty()) || "ZZ".equals(input.getAddressAtDxState())) {
                    Assert.assertEquals(UIHO_UNKNOWN, output.getUiho());
                    Assert.assertEquals(UIHO_CITY_UNKNOWN, output.getUihoCity());
                }
                else if ("AZ005".equals(stCnty)) {
                    Assert.assertEquals("1", output.getUiho());
                    Assert.assertEquals("01", output.getUihoCity());
                }
                else if ("CO001".equals(stCnty) || "CO005".equals(stCnty)) {
                    Assert.assertEquals("1", output.getUiho());
                    Assert.assertEquals("12", output.getUihoCity());
                }
                else if ("MN017".equals(stCnty)) {
                    Assert.assertEquals("1", output.getUiho());
                    Assert.assertEquals("49", output.getUihoCity());
                }
                else if ("NV001".equals(stCnty) || "NV005".equals(stCnty)) {
                    Assert.assertEquals("1", output.getUiho());
                    Assert.assertEquals("24", output.getUihoCity());
                }
                else {
                    Assert.assertEquals(UIHO_NO, output.getUiho());
                    Assert.assertEquals(UIHO_CITY_NONE, output.getUihoCity());
                }
            }
        }
    }
}
