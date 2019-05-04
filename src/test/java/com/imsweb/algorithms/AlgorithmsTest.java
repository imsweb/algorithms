/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.nhia.NhiaUtils;

public class AlgorithmsTest {

    @Test
    public void testAlgorithms() {
        Assert.assertTrue(Algorithms.getAlgorithms().isEmpty());
        Algorithms.initialize();
        Assert.assertFalse(Algorithms.getAlgorithms().isEmpty());

        // NHIA
        Algorithm alg = Algorithms.getAlgorithm(Algorithms.ALG_NHIA);
        Assert.assertEquals(1, alg.getParameters().size());
        AlgorithmParam nhiaOption = alg.getParameters().get(0);
        Assert.assertEquals(Algorithms.PARAM_NHIA_OPTION, nhiaOption.getId());
        Assert.assertNotNull(nhiaOption.getName());
        Assert.assertEquals(String.class, nhiaOption.getType());
        Assert.assertNotNull(nhiaOption.getAllowedValues());
        AlgorithmInput input = new AlgorithmInput();
        input.setParameters(Collections.singletonMap(Algorithms.PARAM_NHIA_OPTION, NhiaUtils.NHIA_OPTION_ALL_CASES));
        Assert.assertEquals(NhiaUtils.NHIA_NON_HISPANIC, alg.execute(input).getPatient().get(Algorithms.FIELD_NHIA));
        Map<String, Object> patMap = new HashMap<>();
        patMap.put(Algorithms.FIELD_SPAN_HISP_OR, "1");
        input.setPatient(patMap);
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, alg.execute(input).getPatient().get(Algorithms.FIELD_NHIA));
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(new HashMap()));
        Assert.assertEquals(NhiaUtils.NHIA_MEXICAN, alg.execute(input).getPatient().get(Algorithms.FIELD_NHIA));
    }
}
