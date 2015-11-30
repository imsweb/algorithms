package com.imsweb.algorithms;

import java.util.ArrayList;
import java.util.List;

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
}
