/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.race;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.Algorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.Algorithms;

public class Race1RecodeAlgorithmTest {

    @Test
    public void testExecute() {
        if (!Algorithms.isInitialized())
            Algorithms.initialize();

        // white in race1
        runAndAssert("01", "01", null);
        runAndAssert("01", "01", "01");
        runAndAssert("02", "01", "02");
        runAndAssert("01", "01", "88");
        runAndAssert("97", "01", "97");
        runAndAssert("01", "01", "98");
        runAndAssert("01", "01", "99");

        // white in race2
        runAndAssert("99", null, "01");
        runAndAssert("01", "01", "01");
        runAndAssert("02", "02", "01");
        runAndAssert("88", "88", "01");
        runAndAssert("97", "97", "01");
        runAndAssert("01", "98", "01");
        runAndAssert("99", "99", "01");

        // black in race1
        runAndAssert("02", "02", null);
        runAndAssert("02", "02", "01");
        runAndAssert("02", "02", "02");
        runAndAssert("02", "02", "88");
        runAndAssert("02", "02", "97");
        runAndAssert("02", "02", "98");
        runAndAssert("02", "02", "99");

        // black in race2
        runAndAssert("99", null, "02");
        runAndAssert("02", "01", "02");
        runAndAssert("02", "02", "02");
        runAndAssert("88", "88", "02");
        runAndAssert("97", "97", "02");
        runAndAssert("02", "98", "02");
        runAndAssert("99", "99", "02");
        
        // can't recode 88 if cases prior to 1991
        runAndAssert("98", "98", "01", null, "1990", null);
        runAndAssert("01", "98", "01", null, "1991", null);
        runAndAssert("01", "98", "01", null, "1992", null);
        
        // can't recode AK
        runAndAssert("02", "01", "02", null, null, "MD");
        runAndAssert("01", "01", "02", null, null, "AK");
        
        // IHS adjustment
        runAndAssert("02", "01", "02", null);
        runAndAssert("02", "01", "02", "0");
        runAndAssert("02", "01", "02", "1");
        runAndAssert("03", "01", "88", "1");
        runAndAssert("02", "98", "02", "1");
        runAndAssert("03", "98", "88", "1");
        runAndAssert("03", "99", "02", "1");
        runAndAssert("03", "99", "88", "1");
        runAndAssert("99", "01", "02", "9");
    }

    public void runAndAssert(String expected, String race1, String race2) {
        runAndAssert(expected, race1, race2, null);
    }
    
    public void runAndAssert(String expected, String race1, String race2, String ish) {
        runAndAssert(expected, race1, race2, ish, "2020", "MD");
    }

    public void runAndAssert(String expected, String race1, String race2, String ish, String year, String state) {
        Algorithm alg = Algorithms.getAlgorithm(Algorithms.ALG_RACE_1_RECODE);

        AlgorithmInput input = new AlgorithmInput();
        Map<String, Object> patMap = new HashMap<>();
        patMap.put(Algorithms.FIELD_RACE1, race1);
        patMap.put(Algorithms.FIELD_RACE2, race2);
        patMap.put(Algorithms.FIELD_IHS, ish);
        input.setPatient(patMap);
        Map<String, Object> tumMap = new HashMap<>();
        tumMap.put(Algorithms.FIELD_DX_DATE, year + "0101");
        tumMap.put(Algorithms.FIELD_STATE_DX, state);
        patMap.put(Algorithms.FIELD_TUMORS, Collections.singletonList(tumMap));

        Assert.assertEquals(expected, alg.execute(input).getPatient().get(Algorithms.FIELD_RACE1_RECODE));
    }
}
