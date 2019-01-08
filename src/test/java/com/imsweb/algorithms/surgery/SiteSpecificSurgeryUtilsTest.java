/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.surgery;

import java.io.IOException;

import org.junit.Test;

public class SiteSpecificSurgeryUtilsTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testData() throws IOException {

        // TODO clean this up

        //        // register the instance we want
        //        SurgeryTablesXmlDto data = SiteSpecificSurgeryUtils.readSiteSpecificSurgeryData(SiteSpecificSurgeryUtils.getInternalSiteSpecificSurgeryDataUrl(2014).openStream());
        //
        //        // make sure that are some tables
        //        Assert.assertFalse(SiteSpecificSurgeryUtils.getInstance().getAllTableTitles().isEmpty());
        //        Assert.assertNotNull(SiteSpecificSurgeryUtils.getInstance().getTable(SiteSpecificSurgeryUtils.getInstance().getAllTableTitles().get(0)));
        //        Assert.assertNotNull(SiteSpecificSurgeryUtils.getInstance().getVersion());
        //
        //        Pattern sitePattern = Pattern.compile("C\\d\\d\\d(-C\\d\\d\\d)?"), histPattern = Pattern.compile("\\d\\d\\d\\d(-\\d\\d\\d\\d)?");
        //
        //        // test data validity
        //        for (SurgeryTableDto table : SiteSpecificSurgeryUtils.getInstance().getAllTables()) {
        //            Assert.assertNotNull(table.getTitle());
        //
        //            if (table.getSiteInclusion() != null)
        //                for (String s : table.getSiteInclusion().split(","))
        //                    Assert.assertTrue(table.getTitle() + " - wrong site inclusion format: " + table.getSiteInclusion(), sitePattern.matcher(s).matches());
        //
        //            if (table.getHistInclusion() != null)
        //                for (String s : table.getHistInclusion().split(","))
        //                    Assert.assertTrue(table.getTitle() + " - wrong hist inclusion format: " + table.getHistInclusion(), histPattern.matcher(s).matches());
        //
        //            if (table.getHistExclusion() != null)
        //                for (String s : table.getHistExclusion().split(","))
        //                    Assert.assertTrue(table.getTitle() + " - wrong hist exclusion format: " + table.getHistExclusion(), histPattern.matcher(s).matches());
        //
        //            for (SurgeryRowDto row : table.getRow()) {
        //                Assert.assertNotNull(row.isLineBreak());
        //                Assert.assertNotNull(row.getLevel());
        //                if (row.isLineBreak()) {
        //                    Assert.assertNull(row.getCode());
        //                    Assert.assertNull(row.getDescription());
        //                }
        //            }
        //        }
        //
        //        // test a few common searches
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(null, null));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(null, "8000"));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable("C000", null));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable("", ""));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable("", "8000"));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable("C000", ""));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable("?", "?"));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable("?", "8000"));
        //        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable("C000", "whatever"));
        //
        //        SurgeryTableDto table = SiteSpecificSurgeryUtils.getInstance().getTable("C000", "8000");
        //        Assert.assertEquals("Oral Cavity", table.getTitle());
        //        Assert.assertEquals(37, table.getRow().size());
    }
}
