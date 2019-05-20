package com.imsweb.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class AlgorithmsUtilsTest {

    @Test
    public void testExpandSites() {
        List<String> result = new ArrayList<>();

        Assert.assertNull(AlgorithmsUtils.expandSites(null));
        Assert.assertNull(AlgorithmsUtils.expandSites(""));
        Assert.assertNull(AlgorithmsUtils.expandSites("    "));

        result.add("C340");
        Assert.assertEquals(result, AlgorithmsUtils.expandSites("C340"));

        result.add("C341");
        result.add("C342");
        result.add("C343");
        result.add("C344");
        result.add("C345");
        result.add("C346");
        result.add("C347");
        result.add("C348");
        result.add("C349");
        Assert.assertEquals(result, AlgorithmsUtils.expandSites("C340-C349"));

        Assert.assertEquals(result, AlgorithmsUtils.expandSites("C34"));
    }

    @Test
    public void testExtractPatient() {
        AlgorithmInput input = new AlgorithmInput();
        Assert.assertTrue(AlgorithmsUtils.extractPatient(input).isEmpty());
        input.setPatient(Collections.singletonMap("key", "value"));
        Assert.assertFalse(AlgorithmsUtils.extractPatient(input).isEmpty());
    }

    @Test
    public void testExtractTumors() {
        Map<String, Object> patMap = new HashMap<>();
        Assert.assertTrue(AlgorithmsUtils.extractTumors(patMap).isEmpty());
        Assert.assertFalse(AlgorithmsUtils.extractTumors(patMap, true).isEmpty());

        List<Map<String, Object>> tumList = new ArrayList<>();
        patMap.put(Algorithms.FIELD_TUMORS, tumList);
        Assert.assertTrue(AlgorithmsUtils.extractTumors(patMap).isEmpty());
        Assert.assertFalse(AlgorithmsUtils.extractTumors(patMap, true).isEmpty());

        tumList.add(Collections.singletonMap("key", "value"));
        Assert.assertFalse(AlgorithmsUtils.extractTumors(patMap).isEmpty());
        Assert.assertFalse(AlgorithmsUtils.extractTumors(patMap, true).isEmpty());
    }

    @Test
    public void testExtractYear() {
        Assert.assertNull(AlgorithmsUtils.extractYear(null));
        Assert.assertNull(AlgorithmsUtils.extractYear(""));
        Assert.assertEquals("    ", AlgorithmsUtils.extractYear("    "));
        Assert.assertNull(AlgorithmsUtils.extractYear("0"));
        Assert.assertNull(AlgorithmsUtils.extractYear("123"));
        Assert.assertEquals("1234", AlgorithmsUtils.extractYear("1234"));
    }

    @Test
    public void testExtractMonth() {
        Assert.assertNull(AlgorithmsUtils.extractMonth(null));
        Assert.assertNull(AlgorithmsUtils.extractMonth(""));
        Assert.assertEquals("  ", AlgorithmsUtils.extractMonth("      "));
        Assert.assertNull(AlgorithmsUtils.extractMonth("0"));
        Assert.assertNull(AlgorithmsUtils.extractMonth("12345"));
        Assert.assertEquals("56", AlgorithmsUtils.extractMonth("123456"));
    }

    @Test
    public void testExtractDay() {
        Assert.assertNull(AlgorithmsUtils.extractDay(null));
        Assert.assertNull(AlgorithmsUtils.extractDay(""));
        Assert.assertEquals("  ", AlgorithmsUtils.extractDay("        "));
        Assert.assertNull(AlgorithmsUtils.extractDay("0"));
        Assert.assertNull(AlgorithmsUtils.extractDay("1234567"));
        Assert.assertEquals("78", AlgorithmsUtils.extractDay("12345678"));
    }
}
