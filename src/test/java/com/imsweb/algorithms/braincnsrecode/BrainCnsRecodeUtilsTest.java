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
        Assert.assertEquals("03", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C729", "9385", "3"));
        Assert.assertEquals("99", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "", "9385", "3"));
        Assert.assertEquals("99", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "", "3"));
        Assert.assertEquals("99", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9385", ""));

        Assert.assertEquals("01", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9401", "3"));
        Assert.assertEquals("01", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9410", "3"));
        Assert.assertEquals("01", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9420", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9420", "0"));

        Assert.assertEquals("02", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9440", "3"));
        Assert.assertEquals("02", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9445", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9445", "0"));

        Assert.assertEquals("03", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9385", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9385", "0"));

        Assert.assertEquals("04", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9450", "3"));
        Assert.assertEquals("04", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9451", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9451", "0"));

        Assert.assertEquals("05", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9382", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9382", "0"));

        Assert.assertEquals("06", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9421", "3"));
        Assert.assertEquals("06", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9425", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9425", "0"));

        Assert.assertEquals("07", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9430", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9430", "0"));

        Assert.assertEquals("08", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9391", "3"));
        Assert.assertEquals("08", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9396", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9396", "0"));

        Assert.assertEquals("09", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9380", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9380", "0"));

        Assert.assertEquals("10", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9381", "3"));
        Assert.assertEquals("10", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9426", "3"));
        Assert.assertEquals("10", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9435", "3"));
        Assert.assertEquals("10", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9449", "3"));
        Assert.assertEquals("10", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9460", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9460", "0"));

        Assert.assertEquals("11", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8963", "3"));
        Assert.assertEquals("11", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9501", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9501", "0"));

        Assert.assertEquals("12", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9530", "3"));
        Assert.assertEquals("12", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9539", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9539", "0"));

        Assert.assertEquals("13", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9390", "3"));
        Assert.assertEquals("19", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9390", "0"));

        Assert.assertEquals("14", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9505", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9505", "0"));

        Assert.assertEquals("15", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8000", "3"));
        Assert.assertEquals("27", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9590", "3"));
        Assert.assertEquals("27", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9990", "3"));
        Assert.assertEquals("27", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9992", "3"));
        Assert.assertEquals("27", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9993", "3"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9505", "0"));

        Assert.assertEquals("16", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9530", "0"));
        Assert.assertEquals("16", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9537", "0"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9538", "0"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9530", "1"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9537", "1"));
        Assert.assertEquals("16", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9538", "1"));

        Assert.assertEquals("17", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9540", "0"));
        Assert.assertEquals("17", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9571", "0"));
        Assert.assertEquals("17", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9560", "0"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9540", "1"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9571", "1"));
        Assert.assertEquals("17", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9560", "1"));

        Assert.assertEquals("18", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9383", "1"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9383", "0"));

        Assert.assertEquals("19", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9390", "0"));
        Assert.assertEquals("19", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9390", "1"));

        Assert.assertEquals("20", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9413", "0"));
        Assert.assertEquals("20", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9493", "0"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9505", "0"));
        Assert.assertEquals("20", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8693", "1"));
        Assert.assertEquals("20", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9509", "1"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9492", "1"));

        Assert.assertEquals("21", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8815", "0"));
        Assert.assertEquals("21", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9120", "0"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8821", "0"));
        Assert.assertEquals("21", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8815", "1"));
        Assert.assertEquals("21", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8825", "1"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "8830", "1"));

        Assert.assertEquals("22", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9384", "1"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9384", "0"));

        Assert.assertEquals("23", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9431", "1"));
        Assert.assertEquals("24", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C700", "9431", "0"));

        Assert.assertEquals("25", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C753", "9362", "3"));

        Assert.assertEquals("26", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C753", "9361", "1"));

        Assert.assertEquals("27", BrainCnsRecodeUtils.computeBrainCsnRecode(ALG_VERSION_2020, "C7523", "9361", "1"));
    }

}
