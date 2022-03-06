/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.ENTIRE_STATE_NON_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.ENTIRE_STATE_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.MIXED_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_YES;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_UNKNOWN;

public class PrcdaUihoUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(PrcdaUihoUtils.ALG_VERSION);
        Assert.assertNotNull(PrcdaUihoUtils.ALG_NAME);
    }

    @Test
    public void testComputePrcdaUiho() {

        List<String> validStates = Arrays.asList("AL", "AK", "AZ", "CO", "GA", "MN", "NV", "SE", "VA");
        List<String> invalStates = Arrays.asList("", "99", null);
        List<String> validCounty = Arrays.asList("001", "003", "005", "007", "017", "097", "999");
        List<String> invalCounty = Arrays.asList("", "ABC", "01", null);

        List<String> prcdaYes = Arrays.asList("AL003", "AZ001", "AZ003", "AZ005", "AZ007", "AZ017", "CO007", "MN001", "MN005", "MN007", "MN017", "MN097", "NV001", "NV003", "NV005", "NV007", "NV017", "SE007", "SE017",
                "VA003", "VA097");
        List<String> prcdaNo = Arrays.asList("AZ097", "CO001", "CO003", "CO005", "CO017", "CO097", "MN003", "NV097", "SE001", "SE003", "SE005", "SE097", "VA001", "VA005", "VA007", "VA017");
        List<String> uihoNo = Arrays.asList("AK001", "AK003", "AK005", "AK007", "AK017", "AK097", "AZ001", "AZ003", "AZ007", "AZ017", "AZ097", "CO003", "CO007", "CO017", "CO097", "GA001", "GA003",
                "GA005", "GA007", "GA017", "GA097", "MN001", "MN003", "MN005", "MN007", "MN097", "NV003", "NV007", "NV017", "NV097", "SE001", "SE003", "SE005", "SE007", "SE017", "SE097", "VA001",
                "VA003", "VA005", "VA007", "VA017", "VA097");

        List<String> states = new ArrayList<>();
        states.addAll(validStates);
        states.addAll(invalStates);
        states.addAll(ENTIRE_STATE_PRCDA);

        List<String> counties = new ArrayList<>();
        counties.addAll(validCounty);
        counties.addAll(invalCounty);

        PrcdaUihoInputDto input = new PrcdaUihoInputDto();

        for (String state : states) {
            for (String county : counties) {
                input.setAddressAtDxState(state);
                input.setAddressAtDxCounty(county);
                input.applyRecodes();
                PrcdaUihoOutputDto output = PrcdaUihoUtils.computePrcdaUiho(input);

                if (invalStates.contains(input.getAddressAtDxState())) {
                    Assert.assertEquals(PRCDA_UNKNOWN, output.getPRCDA());
                    Assert.assertEquals(UIHO_UNKNOWN, output.getUIHO());
                }
                else {
                    if (ENTIRE_STATE_PRCDA.contains(input.getAddressAtDxState())) {
                        Assert.assertEquals(PRCDA_YES, output.getPRCDA());
                    }
                    else if (ENTIRE_STATE_NON_PRCDA.contains(input.getAddressAtDxState())) {
                        Assert.assertEquals(PRCDA_NO, output.getPRCDA());
                    }
                    else if (invalCounty.contains(input.getAddressAtDxCounty()) || (MIXED_PRCDA.contains(input.getAddressAtDxState()) && "999".equals(input.getAddressAtDxCounty()))) {
                        Assert.assertEquals(PRCDA_UNKNOWN, output.getPRCDA());
                    }
                    else {
                        String stCnty = state + county;
                        if (prcdaYes.contains(stCnty)) {
                            Assert.assertEquals(PRCDA_YES, output.getPRCDA());
                        }
                        else if (prcdaNo.contains(stCnty)) {
                            Assert.assertEquals(PRCDA_NO, output.getPRCDA());
                        }
                        // note: if AK or GA ever change to "MIXED_PRCDA" add them here
                        else if ("AZ999".equals(stCnty) || "CO999".equals(stCnty) || "MN999".equals(stCnty) || "SE999".equals(stCnty) || "VA999".equals(stCnty)) {
                            Assert.assertEquals(PRCDA_UNKNOWN, output.getPRCDA());
                        }
                    }

                    // UIHO, UIHO FACILITY
                    if (invalCounty.contains(input.getAddressAtDxCounty()))
                        Assert.assertEquals(UIHO_UNKNOWN, output.getUIHO());
                    else {
                        String stCnty = state + county;
                        if ("999".equals(county) || uihoNo.contains(stCnty))
                            Assert.assertEquals(UIHO_NO, output.getUIHO());
                        else if ("AZ005".equals(stCnty))
                            Assert.assertEquals("1", output.getUIHO());
                        else if ("CO001".equals(stCnty) || "CO005".equals(stCnty))
                            Assert.assertEquals("1", output.getUIHO());
                        else if ("MN017".equals(stCnty))
                            Assert.assertEquals("1", output.getUIHO());
                        else if ("NV001".equals(stCnty) || "NV005".equals(stCnty))
                            Assert.assertEquals("1", output.getUIHO());
                    }
                }
            }
        }
    }
}
