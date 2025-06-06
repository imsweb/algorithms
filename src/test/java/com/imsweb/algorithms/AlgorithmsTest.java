/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

import com.imsweb.algorithms.countyatdiagnosisanalysis.CountyAtDxAnalysisUtils;
import com.imsweb.algorithms.internal.Utils;
import com.imsweb.algorithms.nhia.NhiaUtils;
import com.imsweb.naaccrxml.NaaccrFormat;
import com.imsweb.naaccrxml.NaaccrXmlDictionaryUtils;
import com.imsweb.naaccrxml.entity.dictionary.NaaccrDictionary;

public class AlgorithmsTest {

    @BeforeClass
    public static void setup() {
        if (!Algorithms.isInitialized())
            Algorithms.initialize();
    }

    @SuppressWarnings("SameParameterValue")
    private static String getBuildProperty(String property) {
        File propsFile = new File(System.getProperty("user.dir") + "/build.properties");
        if (!propsFile.exists())
            throw new IllegalStateException("Unable to read properties from " + propsFile.getPath());

        try (InputStream is = Files.newInputStream(propsFile.toPath())) {
            Properties props = new Properties();
            props.load(is);
            return props.getProperty(property);
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to load build properties file, make sure the file is a legit properties file.", e);
        }
    }

    @Test
    public void testFields() throws IOException {
        NaaccrDictionary dictionary = NaaccrXmlDictionaryUtils.getMergedDictionaries(NaaccrFormat.NAACCR_VERSION_230);

        Set<String> ids = new HashSet<>();
        Set<Integer> nums = new HashSet<>();
        for (AlgorithmField field : Algorithms.getAllFields()) {
            Assert.assertNotNull(field.getId());
            Assert.assertTrue(field.getId() + " is too long!", field.getId().length() <= 32);
            Assert.assertNotNull(field.getId() + " requires a number!", field.getNumber());
            Assert.assertNotNull(field.getId() + " requires a name!", field.getName());
            Assert.assertTrue(field.getId() + " has its name too long!", field.getName().length() <= 50);
            Assert.assertNotNull(field.getId() + " requires a short name!", field.getShortName());
            Assert.assertNotNull(field.getId() + " requires a length!", field.getLength());
            Assert.assertNotNull(field.getId() + " requires a data level!", field.getDataLevel());

            if (ids.contains(field.getId()))
                Assert.fail(field.getId() + " is not unique!");
            ids.add(field.getId());

            if (nums.contains(field.getNumber()))
                Assert.fail(field.getNumber() + " is not unique!");
            nums.add(field.getNumber());

            if (field.isNaaccrStandard()) {
                Assert.assertEquals(field.getId(), dictionary.getItemByNaaccrNum(field.getNumber()).getNaaccrId(), field.getId());
                Assert.assertEquals(field.getId(), dictionary.getItemByNaaccrNum(field.getNumber()).getNaaccrName(), field.getName());
            }
        }

        File nonStandardItemsFile = new File(getBuildProperty("non.standard.data.items.list"));
        if (nonStandardItemsFile.exists()) {
            Map<String, Integer> algNonStandardItems = new HashMap<>();
            for (AlgorithmField field : Algorithms.getAllFields())
                if (!field.isNaaccrStandard())
                    algNonStandardItems.put(field.getId(), field.getNumber());

            try (CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(new InputStreamReader(Files.newInputStream(nonStandardItemsFile.toPath()), StandardCharsets.US_ASCII))) {
                reader.stream().forEach(line -> {
                    Integer algNum = algNonStandardItems.get(line.getField(0));
                    if (algNum != null && algNum.equals(Integer.valueOf(line.getField(1))))
                        Assert.fail("Item '" + line.getField(0) + "' has number " + algNum + " in this library, but " + line.getField(1) + " in the submission dictionaries");
                });
            }
        }

    }

    @Test
    public void testDefaultAlgorithms() {
        Assert.assertTrue(Algorithms.isInitialized());
        Assert.assertFalse(Algorithms.getAlgorithms().isEmpty());

        // NHIA
        Algorithm alg = Algorithms.getAlgorithm(Algorithms.ALG_NHIA);
        Assert.assertEquals(1, alg.getParameters().size());
        Assert.assertTrue(alg.getUnknownValues().isEmpty());
        AlgorithmParam<?> nhiaOption = alg.getParameters().get(0);
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
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(new HashMap<>()));
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
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS, "003");
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

        // census-related fields
        alg = Algorithms.getAlgorithm(Algorithms.ALG_RURAL_URBAN);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "AL");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS, "001");
        tumMap.put(Algorithms.FIELD_CENSUS_2000, "020200");
        tumMap.put(Algorithms.FIELD_CENSUS_2010, "020200");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_URIC_2000));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_URIC_2010));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_RUCA_2000));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_RUCA_2010));
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

        // SEER Site Recode 2023
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SEER_SITE_RECODE_2023);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C340");
        tumMap.put(Algorithms.FIELD_HIST_O3, "8000");
        tumMap.put(Algorithms.FIELD_BEHAV_O3, "3");
        tumMap.put(Algorithms.FIELD_DX_DATE, "2023");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("30", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_SITE_RECODE_2023));
        Assert.assertEquals("30", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_SITE_RECODE_2023_EXPANDED));

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

        // AYA Site Recode (WHO 2008)
        alg = Algorithms.getAlgorithm(Algorithms.ALG_AYA_SITE_RECODE_2008);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C700");
        tumMap.put(Algorithms.FIELD_HIST_O3, "9532");
        tumMap.put(Algorithms.FIELD_BEHAV_O3, "1");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("14", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_AYA_SITE_RECODE_2008));

        // AYA Site Recode (2020 Revision)
        alg = Algorithms.getAlgorithm(Algorithms.ALG_AYA_SITE_RECODE_2020);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C700");
        tumMap.put(Algorithms.FIELD_HIST_O3, "9532");
        tumMap.put(Algorithms.FIELD_BEHAV_O3, "1");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("046", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_AYA_SITE_RECODE_2020));

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
        Assert.assertEquals("115", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_ICCC_EXT));

        // IARC
        alg = Algorithms.getAlgorithm(Algorithms.ALG_IARC);
        Assert.assertFalse(alg.getParameters().isEmpty());
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
        Assert.assertEquals("9", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_IARC_MP_INDICATOR));

        // county at DX
        alg = Algorithms.getAlgorithm(Algorithms.ALG_COUNTY_AT_DIAGNOSIS_ANALYSIS);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertTrue(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_DX_DATE, "20150101");
        tumMap.put(Algorithms.FIELD_STATE_DX, "MD");
        tumMap.put(Algorithms.FIELD_COUNTY_DX, "005");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_1990, "001");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2000, "003");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010, "005");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2020, "007");
        tumMap.put(Algorithms.FIELD_CENSUS_CERTAINTY_708090, "1");
        tumMap.put(Algorithms.FIELD_CENSUS_CERTAINTY_2000, "1");
        tumMap.put(Algorithms.FIELD_CENSUS_CERTAINTY_2010, "1");
        tumMap.put(Algorithms.FIELD_CENSUS_CERTAINTY_2020, "1");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Map<String, Object> tumor = Utils.extractTumors(alg.execute(input).getPatient()).get(0);
        Assert.assertEquals("005", tumor.get(Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS));
        Assert.assertEquals(CountyAtDxAnalysisUtils.REP_REP_GEO_EQUAL, tumor.get(Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS_FLAG));

        // PRCDA
        alg = Algorithms.getAlgorithm(Algorithms.ALG_PRCDA);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "MN");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS, "035");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        tumor = Utils.extractTumors(alg.execute(input).getPatient()).get(0);
        Assert.assertEquals("0", tumor.get(Algorithms.FIELD_IHS_PRCDA_2017));
        Assert.assertEquals("1", tumor.get(Algorithms.FIELD_IHS_PRCDA));

        // UIHO
        alg = Algorithms.getAlgorithm(Algorithms.ALG_UIHO);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "CA");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS, "013");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        tumor = Utils.extractTumors(alg.execute(input).getPatient()).get(0);
        Assert.assertEquals("1", tumor.get(Algorithms.FIELD_UIHO));
        Assert.assertEquals("07", tumor.get(Algorithms.FIELD_UIHO_CITY));

        // Brain/CNS
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SEER_BRAIN_CNS_RECODE);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C700");
        tumMap.put(Algorithms.FIELD_HIST_O3, "9385");
        tumMap.put(Algorithms.FIELD_BEHAV_O3, "3");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("03", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_BRAIN_CSN_RECODE_2020));

        // Lymphoid Neoplasm 2021
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SEER_LYMPH_NEO_RECODE_2021);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_PRIMARY_SITE, "C700");
        tumMap.put(Algorithms.FIELD_HIST_O3, "9651");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("01", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_SEER_LYMPH_NEO_RECODE_2021));

        // Derived Summary Grade 2018
        alg = Algorithms.getAlgorithm(Algorithms.ALG_SEER_DERIVED_SUMMARY_STAGE_2018);
        Assert.assertTrue(alg.getParameters().isEmpty());
        Assert.assertFalse(alg.getUnknownValues().isEmpty());
        input = new AlgorithmInput();
        patMap = new HashMap<>();
        input.setPatient(patMap);
        tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_GRADE_CLINICAL, "1");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));
        Assert.assertEquals("1", Utils.extractTumors(alg.execute(input).getPatient()).get(0).get(Algorithms.FIELD_DERIVED_SUMMARY_GRADE_2018));
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
            public List<AlgorithmParam<?>> getParameters() {
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
