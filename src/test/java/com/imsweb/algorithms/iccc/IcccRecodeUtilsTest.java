package com.imsweb.algorithms.iccc;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: depryf
 * Date: 8/22/12
 */
public class IcccRecodeUtilsTest {

    @Test
    public void testAvailableVersions() {
        Assert.assertFalse(IcccRecodeUtils.getAvailableVersions().isEmpty());
    }

    @Test
    public void testRawData() {
        Assert.assertFalse(IcccRecodeUtils.getRawData(IcccRecodeUtils.VERSION_WHO_2008).isEmpty());
        Assert.assertFalse(IcccRecodeUtils.getRawData(IcccRecodeUtils.VERSION_THIRD_EDITION).isEmpty());
    }

    @Test
    public void testData() {

        // third edition
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "", "8000"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C182", ""));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, null, null, null));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C182", "8000", null));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C182", "8000"));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "8000"));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "9673"));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C098", "9673"));
        Assert.assertEquals("058", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "9140"));
        Assert.assertEquals("112", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "9055"));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C008", "8000"));
        Assert.assertEquals("002", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C424", "9823"));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C809", "8000"));

        //repeat the test for recode extended, all should return 999
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C182", "", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, null, null, null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C182", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C182", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "9673", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C098", "9673", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "9140", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C239", "9055", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C008", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C424", "9823", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_THIRD_EDITION, "C809", "8000", null, true));

        //Who 2008 classification
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "", "8000", "2"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "", "1"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "8000", ""));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, null, null, null));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "8000"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "8000", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "8000", "3"));
        Assert.assertEquals("022", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9673", "3"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9673", "1"));
        Assert.assertEquals("101", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C700", "9080", "0"));
        Assert.assertEquals("022", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C098", "9673", "3"));
        Assert.assertEquals("093", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9140", "3"));
        Assert.assertEquals("121", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9055", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C008", "8000", "3"));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C424", "9823", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C809", "8000", "3"));

        //repeat the test for recode extended
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "", "8000", "2", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "", "1", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "8000", "", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, null, null, null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "8000", null, true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C182", "8000", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "8000", "3", true));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9673", "3", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9673", "1", true));
        Assert.assertEquals("072", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C700", "9080", "0", true));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C098", "9673", "3", true));
        Assert.assertEquals("058", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9140", "3", true));
        Assert.assertEquals("112", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C239", "9055", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C008", "8000", "3", true));
        Assert.assertEquals("002", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C424", "9823", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(IcccRecodeUtils.VERSION_WHO_2008, "C809", "8000", "3", true));
    }
}
