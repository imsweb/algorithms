/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.internal.Utils;
import com.imsweb.algorithms.nhia.NhiaUtils;

public class AlgorithmsTest {

    @Test
    public void testDefaultAlgorithms() {
        Assert.assertTrue(Algorithms.getAlgorithms().isEmpty());
        Algorithms.initialize();
        Assert.assertFalse(Algorithms.getAlgorithms().isEmpty());

        // NHIA
        Algorithm alg = Algorithms.getAlgorithm(Algorithms.ALG_NHIA);
        Assert.assertEquals(1, alg.getParameters().size());
        Assert.assertTrue(alg.getUnknownValues().isEmpty());
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
        Assert.assertTrue(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        patMap.put(Algorithms.FIELD_RACE1, "96");
        input.setPatient(patMap);
        Assert.assertEquals("96", alg.execute(input).getPatient().get(Algorithms.FIELD_NAPIIA));

        // SEER Death Classification
        alg = Algorithms.getAlgorithm(Algorithms.ALG_DEATH_CLASSIFICATION);
        Assert.assertFalse(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        patMap.put(Algorithms.FIELD_ICD_REV_NUM, "1");
        patMap.put(Algorithms.FIELD_DOLC, "2013");
        patMap.put(Algorithms.FIELD_COD, "C001");
        input.setPatient(patMap);
        Map<String, Object> tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_SEQ_NUM_CTRL, "00");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_CLASS));
        Assert.assertEquals("0", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_OTHER));
        input.setParameters(Collections.singletonMap(Algorithms.PARAM_SEER_COD_CLASS_CUTOFF_YEAR, 2012));
        Assert.assertEquals("0", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_CLASS));
        Assert.assertEquals("0", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_COD_OTHER));

        // Census Tract Poverty
        alg = Algorithms.getAlgorithm(Algorithms.ALG_CENSUS_POVERTY);
        Assert.assertFalse(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "HI");
        tumMap.put(Algorithms.FIELD_COUNTY_DX, "003");
        tumMap.put(Algorithms.FIELD_CENSUS_2000, "003405");
        tumMap.put(Algorithms.FIELD_DX_DATE, "20070101");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("3", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_CENSUS_POVERTY_INDICTR));

        // Survival Time
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SURVIVAL_TIME);
        Assert.assertFalse(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
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
        Assert.assertEquals("0120", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SURV_MONTH_ACTIVE_FUP));

        // URIC
        alg = Algorithms.getAlgorithm(Algorithms.ALG_URIC);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "AL");
        tumMap.put(Algorithms.FIELD_COUNTY_DX, "001");
        tumMap.put(Algorithms.FIELD_CENSUS_2000, "020200");
        tumMap.put(Algorithms.FIELD_CENSUS_2010, "020200");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_URIC_2000));
        Assert.assertEquals("100.0", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_URIC_2000_PERCENTAGE));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_URIC_2010));
        Assert.assertEquals("100.0", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_URIC_2010_PERCENTAGE));

        // RUCA
        alg = Algorithms.getAlgorithm(Algorithms.ALG_RUCA);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "AL");
        tumMap.put(Algorithms.FIELD_COUNTY_DX, "001");
        tumMap.put(Algorithms.FIELD_CENSUS_2000, "020200");
        tumMap.put(Algorithms.FIELD_CENSUS_2010, "020200");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_RUCA_2000));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_RUCA_2010));

        // Urban Continuum
        alg = Algorithms.getAlgorithm(Algorithms.ALG_URBAN_CONTINUUM);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "AL");
        tumMap.put(Algorithms.FIELD_COUNTY_DX, "001");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("02", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_RURAL_CONT_1993));
        Assert.assertEquals("02", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_RURAL_CONT_2003));
        Assert.assertEquals("02", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_RURAL_CONT_2013));

        // SEER Site Recode
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SEER_SITE_RECODE);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C340");
        tumMap.put(Algorithms.FIELD_HIST_O3, "8000");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("22030", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_SITE_RECODE));

        // SEER Behavior Recode
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SEER_BEHAVIOR_RECODE);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C672");
        tumMap.put(Algorithms.FIELD_HIST_O3, "8000");
        tumMap.put(Algorithms.FIELD_BEHAV_O3, "1");
        tumMap.put(Algorithms.FIELD_DX_DATE, "2005");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("3", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_BEHAV_RECODE));

        // ICCC
        alg = Algorithms.getAlgorithm(Algorithms.ALG_ICCC);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C182");
        tumMap.put(Algorithms.FIELD_HIST_O3, "8000");
        tumMap.put(Algorithms.FIELD_BEHAV_O3, "3");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("122", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_ICCC));

        // IARC
        alg = Algorithms.getAlgorithm(Algorithms.ALG_IARC);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertTrue(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C549");
        tumMap.put(Algorithms.FIELD_HIST_O3, "8000");
        tumMap.put(Algorithms.FIELD_BEHAV_O3, "2");
        tumMap.put(Algorithms.FIELD_DX_DATE, "20170101");
        tumMap.put(Algorithms.FIELD_SEQ_NUM_CTRL, "01");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("9", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_IARC));
    }

    @Test
    public void testAlgorithms() {

        // test a fake algorithm
        Algorithm fakeAlg = new Algorithm() {
            @Override
            public String getId() {
                return "fake-id";
            }

            @Override
            public String getName() {
                return "Fake Name";
            }

            @Override
            public String getVersion() {
                return "1.0";
            }

            @Override
            public String getInfo() {
                return "Some fake algorithm...";
            }

            @Override
            public List<AlgorithmParam> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                return Collections.emptyList();
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {
                return null;
            }
        };

        Assert.assertFalse(Algorithms.getAlgorithms().stream().anyMatch(alg -> "fake-id".equals(alg.getId())));
        Algorithms.registerAlgorithm(fakeAlg);
        Assert.assertTrue(Algorithms.getAlgorithms().stream().anyMatch(alg -> "fake-id".equals(alg.getId())));
        Algorithms.unregisterAlgorithm(fakeAlg);
        Assert.assertFalse(Algorithms.getAlgorithms().stream().anyMatch(alg -> "fake-id".equals(alg.getId())));
    }
}
