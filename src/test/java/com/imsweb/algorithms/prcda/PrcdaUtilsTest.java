/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.prcda.PrcdaUtils.ENTIRE_STATE_NON_PRCDA;
import static com.imsweb.algorithms.prcda.PrcdaUtils.ENTIRE_STATE_PRCDA;
import static com.imsweb.algorithms.prcda.PrcdaUtils.MIXED_PRCDA;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_YES;

public class PrcdaUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(PrcdaUtils.ALG_VERSION);
        Assert.assertNotNull(PrcdaUtils.ALG_NAME);
    }

    @Test
    public void testComputePrcda() {

        List<String> validStates = Arrays.asList("AL", "AK", "AZ", "CO", "GA", "MN", "NV", "SE", "VA");
        List<String> invalStates = Arrays.asList("", "99", null);
        List<String> validCounty = Arrays.asList("001", "003", "005", "007", "013", "017", "035", "097", "999");
        List<String> invalCounty = Arrays.asList("", "ABC", "01", null);

        List<String> prcdaYes = Arrays.asList("AL003", "AL097", "AZ001", "AZ003", "AZ005", "AZ007", "AZ013", "AZ017", "CO007", "MN001", "MN005",
                "MN007", "MN017", "MN035", "MN097", "NV001", "NV003", "NV005", "NV007", "NV017", "VA003", "VA097", "WA007", "WA017", "WA035");

        List<String> prcdaYes2017 = Arrays.asList("AL003", "AL097", "AZ001", "AZ003", "AZ005", "AZ007", "AZ013", "AZ017", "CO007", "MN001", "MN005",
                "MN007", "MN017", "NV001", "NV003", "NV005", "NV007", "NV017", "SE007", "SE017", "VA097", "WA007", "WA017", "WA035");

        List<String> states = new ArrayList<>();
        states.addAll(validStates);
        states.addAll(invalStates);
        states.addAll(ENTIRE_STATE_PRCDA);

        List<String> counties = new ArrayList<>();
        counties.addAll(validCounty);
        counties.addAll(invalCounty);

        PrcdaInputDto input = new PrcdaInputDto();

        for (String state : states) {
            for (String county : counties) {
                input.setAddressAtDxState(state);
                input.setAddressAtDxCounty(county);
                input.applyRecodes();
                String st = input.getAddressAtDxState();
                String cnty = input.getAddressAtDxCounty();

                PrcdaOutputDto output = PrcdaUtils.computePrcda(input);
                System.out.println(state + county);

                if (invalStates.contains(input.getAddressAtDxState())) {
                    Assert.assertEquals(PRCDA_UNKNOWN, output.getPrcda());
                    Assert.assertEquals(PRCDA_UNKNOWN, output.getPrcda2017());
                }
                else if (ENTIRE_STATE_PRCDA.contains(input.getAddressAtDxState())) {
                    Assert.assertEquals(PRCDA_YES, output.getPrcda());
                    Assert.assertEquals(PRCDA_YES, output.getPrcda2017());
                }
                else if (ENTIRE_STATE_NON_PRCDA.contains(input.getAddressAtDxState())) {
                    Assert.assertEquals(PRCDA_NO, output.getPrcda());
                    Assert.assertEquals(PRCDA_NO, output.getPrcda2017());
                }
                else if (invalCounty.contains(input.getAddressAtDxCounty()) || (MIXED_PRCDA.contains(input.getAddressAtDxState()) && "999".equals(input.getAddressAtDxCounty()))) {
                    Assert.assertEquals(PRCDA_UNKNOWN, output.getPrcda());
                    Assert.assertEquals(PRCDA_UNKNOWN, output.getPrcda2017());
                }
                else {
                    String stCnty = st + cnty;
                    // note: if AK or GA ever change to "MIXED_PRCDA" add them here
                    if ("AZ999".equals(stCnty) || "CO999".equals(stCnty) || "MN999".equals(stCnty) || "WA999".equals(stCnty) || "VA999".equals(stCnty)) {
                        Assert.assertEquals(PRCDA_UNKNOWN, output.getPrcda());
                        Assert.assertEquals(PRCDA_UNKNOWN, output.getPrcda2017());
                    }
                    else {
                        Assert.assertEquals(prcdaYes.contains(stCnty) ? PRCDA_YES : PRCDA_NO, output.getPrcda());
                        Assert.assertEquals(prcdaYes2017.contains(stCnty) ? PRCDA_YES : PRCDA_NO, output.getPrcda2017());
                    }
                }
            }
        }
    }
}
