/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.uiho;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.StateCountyInputDto;

public class UihoUtilsTest {

    @Test
    public void assertInfo() {
        Assert.assertNotNull(UihoUtils.ALG_VERSION);
        Assert.assertNotNull(UihoUtils.ALG_NAME);
    }

    @Test
    public void testComputePrcdaUiho() {

        StateCountyInputDto input = new StateCountyInputDto();

        // test missing or invalid state or county - or county not reported
        for (String state : Arrays.asList("MN", "ZZ", "INVALID", null)) {
            for (String county : Arrays.asList("035", "000", "999", "INVALID", null)) {
                input.setAddressAtDxState(state);
                input.setCountyAtDxAnalysis(county);
                String key = String.format("%s|%s", state, county);
                if ("MN".equals(state) && "035".equals(county)) {
                    Assert.assertEquals(key, "1", UihoUtils.computeUiho(input).getUiho());
                    Assert.assertEquals(key, "0", UihoUtils.computeUiho(input).getUihoCity());
                }
                else {
                    Assert.assertEquals(key, "9", UihoUtils.computeUiho(input).getUiho());
                    Assert.assertEquals(key, "9", UihoUtils.computeUiho(input).getUihoCity());
                }
            }
        }
    }
}
