package com.imsweb.algorithms.iccc;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.iccc.IcccRecodeUtils.VERSION_THIRD_EDITION;
import static com.imsweb.algorithms.iccc.IcccRecodeUtils.VERSION_THIRD_EDITION_IARC_2017;
import static com.imsweb.algorithms.iccc.IcccRecodeUtils.VERSION_WHO_2008;

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
        Assert.assertFalse(IcccRecodeUtils.getRawData(VERSION_WHO_2008).isEmpty());
        Assert.assertFalse(IcccRecodeUtils.getRawData(VERSION_THIRD_EDITION).isEmpty());
        Assert.assertFalse(IcccRecodeUtils.getRawData(VERSION_THIRD_EDITION_IARC_2017).isEmpty());
    }

    @Test
    public void testThirdEdition() {
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "", "8000", null));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C182", "", null));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, null, null, null));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C182", "8000", null));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C182", "8000", null));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "8000", null));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "9673", null));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C098", "9673", null));
        Assert.assertEquals("058", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "9140", null));
        Assert.assertEquals("112", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "9055", null));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C008", "8000", null));
        Assert.assertEquals("002", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C424", "9823", null));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C809", "8000", null));

        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C182", "", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, null, null, null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C182", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C182", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "9673", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C098", "9673", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "9140", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C239", "9055", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C008", "8000", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C424", "9823", null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION, "C809", "8000", null, true));
    }

    @Test
    public void testWho2008Edition() {
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "", "8000", "2"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "", "1"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "8000", ""));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, null, null, null));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "8000", null));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "8000", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "8000", "3"));
        Assert.assertEquals("022", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9673", "3"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9673", "1"));
        Assert.assertEquals("101", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C700", "9080", "0"));
        Assert.assertEquals("022", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C098", "9673", "3"));
        Assert.assertEquals("093", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9140", "3"));
        Assert.assertEquals("121", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9055", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C008", "8000", "3"));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C424", "9823", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C809", "8000", "3"));

        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "", "8000", "2", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "", "1", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "8000", "", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, null, null, null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "8000", null, true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C182", "8000", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "8000", "3", true));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9673", "3", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9673", "1", true));
        Assert.assertEquals("072", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C700", "9080", "0", true));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C098", "9673", "3", true));
        Assert.assertEquals("058", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9140", "3", true));
        Assert.assertEquals("112", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C239", "9055", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C008", "8000", "3", true));
        Assert.assertEquals("002", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C424", "9823", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_WHO_2008, "C809", "8000", "3", true));
    }

    @Test
    public void testThirdEditionIarc2017Edition() {
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "", "8000", "2"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "", "1"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "8000", ""));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, null, null, null));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "8000", null));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "8000", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "8000", "3"));
        Assert.assertEquals("022", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9673", "3"));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9673", "1"));
        Assert.assertEquals("101", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C700", "9080", "0"));
        Assert.assertEquals("022", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C098", "9673", "3"));
        Assert.assertEquals("093", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9140", "3"));
        Assert.assertEquals("121", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9055", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C008", "8000", "3"));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C424", "9823", "3"));
        Assert.assertEquals("122", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C809", "8000", "3"));

        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "", "8000", "2", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "", "1", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "8000", "", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, null, null, null, true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "8000", null, true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C182", "8000", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "8000", "3", true));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9673", "3", true));
        Assert.assertEquals("999", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9673", "1", true));
        Assert.assertEquals("072", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C700", "9080", "0", true));
        Assert.assertEquals("011", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C098", "9673", "3", true));
        Assert.assertEquals("058", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9140", "3", true));
        Assert.assertEquals("112", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C239", "9055", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C008", "8000", "3", true));
        Assert.assertEquals("002", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C424", "9823", "3", true));
        Assert.assertEquals("114", IcccRecodeUtils.calculateSiteRecode(VERSION_THIRD_EDITION_IARC_2017, "C809", "8000", "3", true));
    }

    @Test
    public void testIcccMajorCategory() {
        // Test ICCC Major Category
        Assert.assertEquals("99", IcccRecodeUtils.calculateIcccMajorCategory("999"));
        Assert.assertEquals("99", IcccRecodeUtils.calculateIcccMajorCategory("999"));
        Assert.assertEquals("99", IcccRecodeUtils.calculateIcccMajorCategory("999"));
        Assert.assertEquals("99", IcccRecodeUtils.calculateIcccMajorCategory("999"));
        Assert.assertEquals("99", IcccRecodeUtils.calculateIcccMajorCategory("999"));
        Assert.assertEquals("15", IcccRecodeUtils.calculateIcccMajorCategory("122"));
        Assert.assertEquals("15", IcccRecodeUtils.calculateIcccMajorCategory("122"));
        Assert.assertEquals("02", IcccRecodeUtils.calculateIcccMajorCategory("022"));
        Assert.assertEquals("99", IcccRecodeUtils.calculateIcccMajorCategory("999"));
        Assert.assertEquals("10", IcccRecodeUtils.calculateIcccMajorCategory("101"));
        Assert.assertEquals("02", IcccRecodeUtils.calculateIcccMajorCategory("022"));
        Assert.assertEquals("09", IcccRecodeUtils.calculateIcccMajorCategory("093"));
        Assert.assertEquals("15", IcccRecodeUtils.calculateIcccMajorCategory("121"));
        Assert.assertEquals("15", IcccRecodeUtils.calculateIcccMajorCategory("122"));
        Assert.assertEquals("01", IcccRecodeUtils.calculateIcccMajorCategory("011"));
        Assert.assertEquals("15", IcccRecodeUtils.calculateIcccMajorCategory("122"));
    }
}
