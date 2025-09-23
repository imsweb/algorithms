/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.Algorithms;

import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

public class UtilsTest {

    @Test
    public void testExpandSites() {
        List<String> result = new ArrayList<>();

        Assert.assertNull(Utils.expandSites(null));
        Assert.assertNull(Utils.expandSites(""));
        Assert.assertNull(Utils.expandSites("    "));

        result.add("C340");
        Assert.assertEquals(result, Utils.expandSites("C340"));

        result.add("C341");
        result.add("C342");
        result.add("C343");
        result.add("C344");
        result.add("C345");
        result.add("C346");
        result.add("C347");
        result.add("C348");
        result.add("C349");
        Assert.assertEquals(result, Utils.expandSites("C340-C349"));

        Assert.assertEquals(result, Utils.expandSites("C34"));
    }

    @Test
    public void testExtractPatient() {
        AlgorithmInput input = new AlgorithmInput();
        Assert.assertTrue(Utils.extractPatient(input).isEmpty());
        input.setPatient(Collections.singletonMap("key", "value"));
        Assert.assertFalse(Utils.extractPatient(input).isEmpty());
    }

    @Test
    public void testExtractTumors() {
        Map<String, Object> patMap = new HashMap<>();
        Assert.assertTrue(Utils.extractTumors(patMap).isEmpty());

        List<Map<String, Object>> tumList = new ArrayList<>();
        patMap.put(Algorithms.FIELD_TUMORS, tumList);
        Assert.assertTrue(Utils.extractTumors(patMap).isEmpty());

        tumList.add(Collections.singletonMap("key", "value"));
        Assert.assertFalse(Utils.extractTumors(patMap).isEmpty());
    }

    @Test
    public void testCreatePatientOutput() {
        Map<String, Object> patient = Utils.createPatientOutput();
        Assert.assertEquals(1, patient.size());
        Assert.assertTrue(patient.containsKey(FIELD_TUMORS));
        Assert.assertEquals(new ArrayList<>(), patient.get(FIELD_TUMORS));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddTumorOutput() {
        Map<String, Object> tumor = new HashMap<>();

        Map<String, Object> patient = Utils.createPatientOutput();
        Utils.addTumorOutput(patient, tumor);
        Assert.assertEquals(1, ((List<Map<String, Object>>)patient.get(FIELD_TUMORS)).size());

        patient = new HashMap<>();
        Utils.addTumorOutput(patient, tumor);
        Assert.assertEquals(1, ((List<Map<String, Object>>)patient.get(FIELD_TUMORS)).size());
    }

    @Test
    public void testExtractYear() {
        Assert.assertNull(Utils.extractYear(null));
        Assert.assertNull(Utils.extractYear(""));
        Assert.assertEquals("    ", Utils.extractYear("    "));
        Assert.assertNull(Utils.extractYear("0"));
        Assert.assertNull(Utils.extractYear("123"));
        Assert.assertEquals("1234", Utils.extractYear("1234"));
    }

    @Test
    public void testExtractMonth() {
        Assert.assertNull(Utils.extractMonth(null));
        Assert.assertNull(Utils.extractMonth(""));
        Assert.assertEquals("  ", Utils.extractMonth("      "));
        Assert.assertNull(Utils.extractMonth("0"));
        Assert.assertNull(Utils.extractMonth("12345"));
        Assert.assertEquals("56", Utils.extractMonth("123456"));
    }

    @Test
    public void testExtractDay() {
        Assert.assertNull(Utils.extractDay(null));
        Assert.assertNull(Utils.extractDay(""));
        Assert.assertEquals("  ", Utils.extractDay("        "));
        Assert.assertNull(Utils.extractDay("0"));
        Assert.assertNull(Utils.extractDay("1234567"));
        Assert.assertEquals("78", Utils.extractDay("12345678"));
    }

}
