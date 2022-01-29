/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms.braincnsrecode;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.braincnsrecode.BrainCnsRecodeUtils.ALG_VERSION_2020;

public class BrainCnsRecodeUtilsTest {

    @Test
    public void computeBrainCsnRecode2020Revision () {
        Assert.assertEquals("99", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, null, null, null));

        Assert.fail("Finish me!");
    }

}
