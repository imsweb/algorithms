/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.neoadjuvant;

import org.junit.Assert;
import org.junit.Test;

public class NeoAdjuvantTherapyTxEffectUtilsTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    public void testGetLookup() {
        Assert.assertNull(NeoAdjuvantTherapyTxEffectUtils.getLookup(null));
        Assert.assertNull(NeoAdjuvantTherapyTxEffectUtils.getLookup(""));

        // test B schema
        Assert.assertEquals("""
                        No definite response to presurgical therapy in the invasive carcinoma
                        Stated as No response (NR)
                        Stated as poor response""",
                NeoAdjuvantTherapyTxEffectUtils.getLookup("00480").get("4"));

        // test G schema
        Assert.assertEquals(2, NeoAdjuvantTherapyTxEffectUtils.getLookup("00812").size());

        // test newly added schema
        Assert.assertEquals(NeoAdjuvantTherapyTxEffectUtils.getLookup("00360"), NeoAdjuvantTherapyTxEffectUtils.getLookup("09360"));

    }

    @Test
    public void testGetAllAllowedValues() {
        Assert.assertTrue(NeoAdjuvantTherapyTxEffectUtils.getAllAllowedValues().contains("0"));
        Assert.assertFalse(NeoAdjuvantTherapyTxEffectUtils.getAllAllowedValues().contains("5"));
    }

}
