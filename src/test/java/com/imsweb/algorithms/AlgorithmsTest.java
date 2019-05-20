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

        // NAPIIA
        alg = Algorithms.getAlgorithm(Algorithms.ALG_NAPIIA);
        Assert.assertTrue(alg.getParameters().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        patMap.put(Algorithms.FIELD_RACE1, "96");
        input.setPatient(patMap);
        Assert.assertEquals("96", alg.execute(input).getPatient().get(Algorithms.FIELD_NAPIIA));

        // SEER Death Classification
        alg = Algorithms.getAlgorithm(Algorithms.ALG_DEATH_CLASSIFICATION);
        Assert.assertFalse(alg.getParameters().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        patMap.put(Algorithms.FIELD_ICD_REV_NUM, "1");
        patMap.put(Algorithms.FIELD_DOLC, "2013");
        patMap.put(Algorithms.FIELD_COD, "C001");
        input.setPatient(patMap);
        Map<String, Object> tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_SEQ_NUM_CTRL, "00");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("1", AlgorithmsUtils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_CLASS));
        Assert.assertEquals("0", AlgorithmsUtils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_OTHER));
        input.setParameters(Collections.singletonMap(Algorithms.PARAM_SEER_COD_CLASS_CUTOFF_YEAR, 2012));
        Assert.assertEquals("0", AlgorithmsUtils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_CLASS));
        Assert.assertEquals("0", AlgorithmsUtils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_OTHER));

        // Census Tract Poverty
        alg = Algorithms.getAlgorithm(Algorithms.ALG_CENSUS_POVERTY);
        Assert.assertFalse(alg.getParameters().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "HI");
        tumMap.put(Algorithms.FIELD_COUNTY_DX, "003");
        tumMap.put(Algorithms.FIELD_CENSUS_2000, "003405");
        tumMap.put(Algorithms.FIELD_DX_DATE, "20070101");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("3", AlgorithmsUtils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_CENSUS_POVERTY_INDICTR));

        // Survival Time
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SURVIVAL_TIME);
        Assert.assertFalse(alg.getParameters().isEmpty());
        input = new AlgorithmInput();
        input.setParameters(Collections.singletonMap(Algorithms.PARAM_SURV_CUTOFF_YEAR, 2018));
        patMap = new HashMap<>();
        patMap.put(Algorithms.FIELD_PAT_ID_NUMBER, "00000001");
        patMap.put(Algorithms.FIELD_DATE_OF_BIRTH, "19500101");
        patMap.put(Algorithms.FIELD_VS, "1");
        patMap.put(Algorithms.FIELD_DOLC, "20180101");
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_DX_DATE, "20080101");
        tumMap.put(Algorithms.FIELD_SEQ_NUM_CTRL, "00");
        tumMap.put(Algorithms.FIELD_TYPE_RPT_SRC, "1");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("0120", AlgorithmsUtils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SURV_MONTH_ACTIVE_FUP));
    }
}
