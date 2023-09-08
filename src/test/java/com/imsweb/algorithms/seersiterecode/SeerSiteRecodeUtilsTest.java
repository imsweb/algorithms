package com.imsweb.algorithms.seersiterecode;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2003;
import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM;
import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2008;
import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2023;
import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2023_EXPANDED;

/**
 * User: depryf
 * Date: 8/22/12
 */
public class SeerSiteRecodeUtilsTest {

    @Test
    public void testAvailableVersions() {
        Assert.assertFalse(SeerSiteRecodeUtils.getAvailableVersions().isEmpty());
    }

    @Test
    public void testRawData() {
        Assert.assertFalse(SeerSiteRecodeUtils.getRawData(VERSION_2008).isEmpty());
        Assert.assertFalse(SeerSiteRecodeUtils.getRawData(VERSION_2003).isEmpty());
        Assert.assertFalse(SeerSiteRecodeUtils.getRawData(VERSION_2003_WITHOUT_KSM).isEmpty());

        // make sure groups are unique
        Set<String> names = new HashSet<>(), codes = new HashSet<>();
        for (SeerSiteGroupDto dto : SeerSiteRecodeUtils.getRawData(VERSION_2008)) {
            if (names.contains(dto.getName()))
                Assert.fail("Got duplicate name: " + dto.getName());
            names.add(dto.getName());

            if (dto.getRecode() != null) {
                if (codes.contains(dto.getRecode()))
                    Assert.fail("Got duplicate recode: " + dto.getRecode());
                codes.add(dto.getRecode());
            }
        }
    }

    @Test
    public void testCalculateSiteRecode2023() {
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "", "8000", "3", "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C182", "", "3", "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, null, null, "3", "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C182", "8000", null, "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C182", "8000", "3", null));
        Assert.assertEquals("20", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C182", "8000", "3", "2023"));
        Assert.assertEquals("24", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C239", "8000", "3", "2023"));
        Assert.assertEquals("70", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C239", "9673", "3", "2023"));
        Assert.assertEquals("10", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C098", "8000", "3", "2023"));
        Assert.assertEquals("70", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C098", "9673", "3", "2023"));
        Assert.assertEquals("78", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C239", "9140", "3", "2023"));
        Assert.assertEquals("77", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C239", "9055", "3", "2023"));
        Assert.assertEquals("01", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C008", "8000", "3", "2023"));
        Assert.assertEquals("79", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C424", "8000", "3", "2023"));
        Assert.assertEquals("67", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C424", "9811", "3", "2023"));
        Assert.assertEquals("68", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C424", "9823", "3", "2023"));
        Assert.assertEquals("77", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C020", "9050", "3", "2023"));

        // testing behavior
        Assert.assertEquals("81", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C700", "8000", "0", "2023"));
        Assert.assertEquals("81", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C700", "8000", "1", "2023"));
        Assert.assertEquals("59", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C700", "8000", "2", "2023"));
        Assert.assertEquals("59", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C700", "8000", "3", "2023"));

        // testing year min/max
        Assert.assertEquals("67", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C000", "9727", "3", "2008"));
        Assert.assertEquals("67", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C000", "9727", "3", "2009"));
        Assert.assertEquals("70", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C000", "9727", "3", "2010"));
        Assert.assertEquals("70", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C000", "9727", "3", "2011"));

        // other cases
        Assert.assertEquals("79", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023, "C809", "8140", "3", "2023"));

    }

    @Test
    public void testCalculateSiteRecode2023Expanded() {
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "", "8000", "3", "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C182", "", "3", "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, null, null, "3", "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C182", "8000", null, "2023"));
        Assert.assertEquals("99", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C182", "8000", "3", null));
        Assert.assertEquals("20", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C182", "8000", "3", "2023"));
        Assert.assertEquals("24", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C239", "8000", "3", "2023"));
        Assert.assertEquals("71", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C239", "9673", "3", "2023"));
        Assert.assertEquals("10", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C098", "8000", "3", "2023"));
        Assert.assertEquals("71", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C098", "9673", "3", "2023"));
        Assert.assertEquals("81", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C239", "9140", "3", "2023"));
        Assert.assertEquals("80", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C239", "9055", "3", "2023"));
        Assert.assertEquals("01", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C008", "8000", "3", "2023"));
        Assert.assertEquals("82", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C424", "8000", "3", "2023"));
        Assert.assertEquals("74", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C424", "9811", "3", "2023"));
        Assert.assertEquals("69", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C424", "9823", "3", "2023"));
        Assert.assertEquals("80", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C020", "9050", "3", "2023"));

        // testing behavior
        Assert.assertEquals("84", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C700", "8000", "0", "2023"));
        Assert.assertEquals("84", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C700", "8000", "1", "2023"));
        Assert.assertEquals("59", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C700", "8000", "2", "2023"));
        Assert.assertEquals("59", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C700", "8000", "3", "2023"));

        // testing year min/max
        Assert.assertEquals("74", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C000", "9727", "3", "2008"));
        Assert.assertEquals("74", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C000", "9727", "3", "2009"));
        Assert.assertEquals("71", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C000", "9727", "3", "2010"));
        Assert.assertEquals("71", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C000", "9727", "3", "2011"));

        // other cases
        Assert.assertEquals("82", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2023_EXPANDED, "C809", "8140", "3", "2023"));
    }

    @Test
    public void testCalculateSiteRecode2010() {
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C182", ""));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, null, null));
        Assert.assertEquals("21043", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C182", "8000"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C239", "8000"));
        Assert.assertEquals("33042", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C239", "9673"));
        Assert.assertEquals("33041", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C098", "9673"));
        Assert.assertEquals("36020", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C239", "9140"));
        Assert.assertEquals("36010", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C239", "9055"));
        Assert.assertEquals("20010", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C008", "8000"));
        Assert.assertEquals("35011", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C424", "9811"));
        Assert.assertEquals("35012", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2008, "C424", "9823"));
    }

    @Test
    public void testCalculateSiteRecode2003() {
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C182", ""));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, null, null));
        Assert.assertEquals("21043", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C182", "8000"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C239", "8000"));
        Assert.assertEquals("33042", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C239", "9673"));
        Assert.assertEquals("33041", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C098", "9673"));
        Assert.assertEquals("36020", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C239", "9140"));
        Assert.assertEquals("36010", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C239", "9055"));
        Assert.assertEquals("20010", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C008", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C424", "9811"));
        Assert.assertEquals("35012", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003, "C424", "9823"));
    }

    @Test
    public void testCalculateSiteRecode2003WithoutKsm() {
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C182", ""));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, null, null));
        Assert.assertEquals("21043", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C182", "8000"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C239", "8000"));
        Assert.assertEquals("33042", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C239", "9673"));
        Assert.assertEquals("33041", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C098", "9673"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C239", "9140"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C239", "9055"));
        Assert.assertEquals("20010", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C008", "8000"));
        Assert.assertEquals("35012", SeerSiteRecodeUtils.calculateSiteRecode(VERSION_2003_WITHOUT_KSM, "C424", "9823"));
    }

    @Test
    public void testGetRecodeName() {
        String unknown = "Unknown";

        //default version
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(""));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20100A"));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("2010"));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20200"));
        Assert.assertEquals("Peritoneum, Omentum and Mesentery", SeerSiteRecodeUtils.getRecodeName("21120"));
        Assert.assertEquals("Cervix Uteri", SeerSiteRecodeUtils.getRecodeName("27010"));

        // 2023
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, VERSION_2023));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", VERSION_2023));
        Assert.assertEquals("Lip", SeerSiteRecodeUtils.getRecodeName("01", VERSION_2023));

        // 2023 expanded
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, VERSION_2023_EXPANDED));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", VERSION_2023_EXPANDED));
        Assert.assertEquals("Lip", SeerSiteRecodeUtils.getRecodeName("01", VERSION_2023_EXPANDED));

        //2010
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, VERSION_2008));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", VERSION_2008));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20100A", VERSION_2008));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20200", VERSION_2008));
        Assert.assertEquals("Hodgkin - Extranodal", SeerSiteRecodeUtils.getRecodeName("33012", VERSION_2008));
        Assert.assertEquals("Miscellaneous", SeerSiteRecodeUtils.getRecodeName("37000", VERSION_2008));
        Assert.assertEquals("Acute Monocytic Leukemia", SeerSiteRecodeUtils.getRecodeName("35031", VERSION_2008));

        //2003
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, VERSION_2003));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", VERSION_2003));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20100A", VERSION_2003));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20200", VERSION_2003));
        Assert.assertEquals("Splenic Flexure", SeerSiteRecodeUtils.getRecodeName("21046", VERSION_2003));
        Assert.assertEquals("Cervix Uteri", SeerSiteRecodeUtils.getRecodeName("27010", VERSION_2003));
        Assert.assertEquals("Cranial Nerves Other Nervous System", SeerSiteRecodeUtils.getRecodeName("31040", VERSION_2003));

        // 2003 without Kaposi Sarcoma and Mesothelioma
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20100A", VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20200", VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals("Retroperitoneum", SeerSiteRecodeUtils.getRecodeName("21110", VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals("Other Oral Cavity and Pharynx", SeerSiteRecodeUtils.getRecodeName("20100", VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals("Descending Colon", SeerSiteRecodeUtils.getRecodeName("21047", VERSION_2003_WITHOUT_KSM));
    }
}
