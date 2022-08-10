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

        List<String> validStates = Arrays.asList("AL", "AK", "AZ", "CO", "GA", "MN", "NV", "SE", "VA");
        List<String> invalStates = Arrays.asList("", "99", null);
        List<String> validCounty = Arrays.asList("001", "003", "005", "007", "017", "097", "999");
        List<String> invalCounty = Arrays.asList("", "ABC", "01", null);

        List<String> uihoNo = Arrays.asList("AK001", "AK003", "AK005", "AK007", "AK017", "AK097", "AZ001", "AZ003", "AZ007", "AZ017", "AZ097", "CO003", "CO007", "CO017", "CO097", "GA001", "GA003",
                "GA005", "GA007", "GA017", "GA097", "MN001", "MN003", "MN005", "MN007", "MN097", "NV003", "NV007", "NV017", "NV097", "SE001", "SE003", "SE005", "SE007", "SE017", "SE097", "VA001",
                "VA003", "VA005", "VA007", "VA017", "VA097");

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

                if (invalStates.contains(input.getAddressAtDxState()) || invalCounty.contains(input.getAddressAtDxCounty())) {
                    Assert.assertEquals(UIHO_UNKNOWN, output.getUiho());
                    Assert.assertEquals(UIHO_CITY_UNKNOWN, output.getUihoCity());
                }
                else {
                    String stCnty = state + county;
                    if ("999".equals(county) || uihoNo.contains(stCnty)) {
                        Assert.assertEquals(UIHO_NO, output.getUiho());
                        Assert.assertEquals(UIHO_CITY_NONE, output.getUihoCity());
                    } else if ("AZ005".equals(stCnty)) {
                        Assert.assertEquals("1", output.getUiho());
                        Assert.assertEquals("01", output.getUihoCity());
                    } else if ("CO001".equals(stCnty) || "CO005".equals(stCnty)) {
                        Assert.assertEquals("1", output.getUiho());
                        Assert.assertEquals("12", output.getUihoCity());
                    } else if ("MN017".equals(stCnty)) {
                        Assert.assertEquals("1", output.getUiho());
                        Assert.assertEquals("49", output.getUihoCity());
                    } else if ("NV001".equals(stCnty) || "NV005".equals(stCnty)) {
                        Assert.assertEquals("1", output.getUiho());
                        Assert.assertEquals("24", output.getUihoCity());
                    }
                }
            }
        }
    }
}
