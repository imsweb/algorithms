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
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_YES;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_NONE;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_UNKNOWN;

public class PrcdaUihoUtilsTest {
    @Test
    public void assertInfo() {
        Assert.assertNotNull(PrcdaUihoUtils.ALG_VERSION);
        Assert.assertNotNull(PrcdaUihoUtils.ALG_NAME);
        Assert.assertNotNull(PrcdaUihoUtils.ALG_INFO);
    }

    @Test
    public void testComputePrcdaUiho() {

        List<String> validStates = Arrays.asList("AK", "AZ", "CO", "GA", "NV", "SE");
        List<String> invalStates = Arrays.asList("", "99", null);
        List<String> validCounty = Arrays.asList("001", "003", "005", "007", "999");
        List<String> invalCounty = Arrays.asList("", "ABC", "01", null);

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
                    Assert.assertEquals(UIHO_FACILITY_UNKNOWN, output.getUIHOFacility());
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
                        if ("AZ001".equals(stCnty) || "AZ003".equals(stCnty) || "AZ005".equals(stCnty) || "AZ007".equals(stCnty)
                                || "CO007".equals(stCnty)
                                || "NV001".equals(stCnty) || "NV003".equals(stCnty) || "NV005".equals(stCnty) || "NV007".equals(stCnty)
                                || "SE007".equals(stCnty) || "SE009".equals(stCnty)) {
                            Assert.assertEquals(PRCDA_YES, output.getPRCDA());
                        }
                        else if ("CO001".equals(stCnty) || "CO003".equals(stCnty) || "CO005".equals(stCnty)
                                || "SE001".equals(stCnty) || "SE003".equals(stCnty)) {
                            Assert.assertEquals(PRCDA_NO, output.getPRCDA());
                        }
                        // note: if AK or GA ever change to "MIXED_PRCDA" add them here
                        else if ("AZ999".equals(stCnty) || "CO999".equals(stCnty) || "SE999".equals(stCnty)) {
                            Assert.assertEquals(PRCDA_UNKNOWN, output.getPRCDA());
                        }
                    }

                    // UIHO, UIHO FACILITY
                    if (invalCounty.contains(input.getAddressAtDxCounty())) {
                        Assert.assertEquals(UIHO_UNKNOWN, output.getUIHO());
                        Assert.assertEquals(UIHO_FACILITY_UNKNOWN, output.getUIHOFacility());
                    }
                    else {
                        String stCnty = state + county;
                        if ("999".equals(county)
                                ||"AZ001".equals(stCnty) || "AZ003".equals(stCnty) || "AZ007".equals(stCnty)
                                || "CO003".equals(stCnty) || "CO007".equals(stCnty)
                                || "NV003".equals(stCnty) || "NV007".equals(stCnty)
                                || "SE001".equals(stCnty) || "SE003".equals(stCnty) || "SE007".equals(stCnty)) {
                            Assert.assertEquals(UIHO_NO, output.getUIHO());
                            Assert.assertEquals(UIHO_FACILITY_NONE, output.getUIHOFacility());
                        }
                        else if ("AZ005".equals(stCnty)) {
                            Assert.assertEquals("1", output.getUIHO());
                            Assert.assertEquals("01", output.getUIHOFacility());
                        }
                        else if ("CO001".equals(stCnty) || "CO005".equals(stCnty)) {
                            Assert.assertEquals("1", output.getUIHO());
                            Assert.assertEquals("12", output.getUIHOFacility());
                        }
                        else if ("NV001".equals(stCnty) || "NV005".equals(stCnty)) {
                            Assert.assertEquals("1", output.getUIHO());
                            Assert.assertEquals("24", output.getUIHOFacility());
                        }
                    }
                }
            }
        }
    }
}
