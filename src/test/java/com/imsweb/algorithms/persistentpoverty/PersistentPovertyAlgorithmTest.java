/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.persistentpoverty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.Algorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

public class PersistentPovertyAlgorithmTest {

    @Test
    public void testExecute() {
        if (!Algorithms.isInitialized())
            Algorithms.initialize();

        Algorithm alg = Algorithms.getAlgorithm(Algorithms.ALG_PERSISTENT_POVERTY);

        AlgorithmInput input = new AlgorithmInput();
        Map<String, Object> patMap = new HashMap<>();
        input.setPatient(patMap);
        Map<String, Object> tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_STATE_DX, "AL");
        tumMap.put(Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS, "057");
        tumMap.put(Algorithms.FIELD_CENSUS_2010, "020200");
        tumMap.put(Algorithms.FIELD_CENSUS_2020, "020200");
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));

        // that combination of input computes a value that is different in each of the data file...
        Assert.assertEquals("2", Utils.extractTumors(alg.execute(input).getPatient()).getFirst().get(Algorithms.FIELD_PERSISTENT_POVERTY_0711));
        Assert.assertEquals("0", Utils.extractTumors(alg.execute(input).getPatient()).getFirst().get(Algorithms.FIELD_PERSISTENT_POVERTY_1721));
    }

}
