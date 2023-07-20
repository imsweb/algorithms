package com.imsweb.algorithms.seersiterecode;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertFalse(SeerSiteRecodeUtils.getRawData(SeerSiteRecodeUtils.VERSION_2010).isEmpty());
        Assert.assertFalse(SeerSiteRecodeUtils.getRawData(SeerSiteRecodeUtils.VERSION_2003).isEmpty());
        Assert.assertFalse(SeerSiteRecodeUtils.getRawData(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM).isEmpty());

        // make sure groups are unique
        Set<String> names = new HashSet<>(), codes = new HashSet<>();
        for (SeerSiteGroupDto dto : SeerSiteRecodeUtils.getRawData(SeerSiteRecodeUtils.VERSION_2010)) {
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
        Assert.fail("Finish these tests");
    }

    @Test
    public void testCalculateSiteRecode2023Expanded() {
        Assert.fail("Finish these tests");
    }

    @Test
    public void testCalculateSiteRecode2010() {
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C182", ""));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, null, null));
        Assert.assertEquals("21043", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C182", "8000"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C239", "8000"));
        Assert.assertEquals("33042", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C239", "9673"));
        Assert.assertEquals("33041", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C098", "9673"));
        Assert.assertEquals("36020", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C239", "9140"));
        Assert.assertEquals("36010", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C239", "9055"));
        Assert.assertEquals("20010", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C008", "8000"));
        Assert.assertEquals("35011", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C424", "9811"));
        Assert.assertEquals("35012", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2010, "C424", "9823"));
    }

    @Test
    public void testCalculateSiteRecode2003() {
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C182", ""));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, null, null));
        Assert.assertEquals("21043", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C182", "8000"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C239", "8000"));
        Assert.assertEquals("33042", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C239", "9673"));
        Assert.assertEquals("33041", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C098", "9673"));
        Assert.assertEquals("36020", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C239", "9140"));
        Assert.assertEquals("36010", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C239", "9055"));
        Assert.assertEquals("20010", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C008", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C424", "9811"));
        Assert.assertEquals("35012", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003, "C424", "9823"));
    }

    @Test
    public void testCalculateSiteRecode2003WithoutKsm() {
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "", "8000"));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C182", ""));
        Assert.assertEquals("99999", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, null, null));
        Assert.assertEquals("21043", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C182", "8000"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C239", "8000"));
        Assert.assertEquals("33042", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C239", "9673"));
        Assert.assertEquals("33041", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C098", "9673"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C239", "9140"));
        Assert.assertEquals("21080", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C239", "9055"));
        Assert.assertEquals("20010", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C008", "8000"));
        Assert.assertEquals("35012", SeerSiteRecodeUtils.calculateSiteRecode(SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM, "C424", "9823"));
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
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, SeerSiteRecodeUtils.VERSION_2023));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", SeerSiteRecodeUtils.VERSION_2023));
        Assert.assertEquals("Lip", SeerSiteRecodeUtils.getRecodeName("001", SeerSiteRecodeUtils.VERSION_2023));

        // 2023 expanded
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, SeerSiteRecodeUtils.VERSION_2023_EXPANDED));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", SeerSiteRecodeUtils.VERSION_2023_EXPANDED));
        Assert.assertEquals("Lip", SeerSiteRecodeUtils.getRecodeName("001", SeerSiteRecodeUtils.VERSION_2023_EXPANDED));

        //2010
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, SeerSiteRecodeUtils.VERSION_2010));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", SeerSiteRecodeUtils.VERSION_2010));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20100A", SeerSiteRecodeUtils.VERSION_2010));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20200", SeerSiteRecodeUtils.VERSION_2010));
        Assert.assertEquals("Hodgkin - Extranodal", SeerSiteRecodeUtils.getRecodeName("33012", SeerSiteRecodeUtils.VERSION_2010));
        Assert.assertEquals("Miscellaneous", SeerSiteRecodeUtils.getRecodeName("37000", SeerSiteRecodeUtils.VERSION_2010));
        Assert.assertEquals("Acute Monocytic Leukemia", SeerSiteRecodeUtils.getRecodeName("35031", SeerSiteRecodeUtils.VERSION_2010));

        //2003
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, SeerSiteRecodeUtils.VERSION_2003));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", SeerSiteRecodeUtils.VERSION_2003));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20100A", SeerSiteRecodeUtils.VERSION_2003));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20200", SeerSiteRecodeUtils.VERSION_2003));
        Assert.assertEquals("Splenic Flexure", SeerSiteRecodeUtils.getRecodeName("21046", SeerSiteRecodeUtils.VERSION_2003));
        Assert.assertEquals("Cervix Uteri", SeerSiteRecodeUtils.getRecodeName("27010", SeerSiteRecodeUtils.VERSION_2003));
        Assert.assertEquals("Cranial Nerves Other Nervous System", SeerSiteRecodeUtils.getRecodeName("31040", SeerSiteRecodeUtils.VERSION_2003));

        // 2003 without Kaposi Sarcoma and Mesothelioma
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName(null, SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("", SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20100A", SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals(unknown, SeerSiteRecodeUtils.getRecodeName("20200", SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals("Retroperitoneum", SeerSiteRecodeUtils.getRecodeName("21110", SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals("Other Oral Cavity and Pharynx", SeerSiteRecodeUtils.getRecodeName("20100", SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM));
        Assert.assertEquals("Descending Colon", SeerSiteRecodeUtils.getRecodeName("21047", SeerSiteRecodeUtils.VERSION_2003_WITHOUT_KSM));
    }
}
