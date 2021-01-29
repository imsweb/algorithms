/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.neoadjuvant;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("ConstantConditions")
public class NeoAdjuvantTherapyTxEffectUtilsTest {

    @Test
    public void testGetLookup() {
        Assert.assertNull(NeoAdjuvantTherapyTxEffectUtils.getLookup(null));
        Assert.assertNull(NeoAdjuvantTherapyTxEffectUtils.getLookup(""));

        // test B schema
        Assert.assertEquals("No definite response to presurgical therapy in the invasive carcinoma\n"
                        + "Stated as No response (NR)\n"
                        + "Stated as poor response",
                NeoAdjuvantTherapyTxEffectUtils.getLookup("00480").get("4"));

        // test G schema
        Assert.assertEquals(1, NeoAdjuvantTherapyTxEffectUtils.getLookup("00812").size());

    }

    @Test
    public void testGetAllAllowedValues() {
        Assert.assertTrue(NeoAdjuvantTherapyTxEffectUtils.getAllAllowedValues().contains("0"));
        Assert.assertFalse(NeoAdjuvantTherapyTxEffectUtils.getAllAllowedValues().contains("5"));
    }

}
