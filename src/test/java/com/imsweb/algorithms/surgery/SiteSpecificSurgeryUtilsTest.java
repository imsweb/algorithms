/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.surgery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.imsweb.seerutils.SeerUtils;

public class SiteSpecificSurgeryUtilsTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testData() {
        Pattern sitePattern = Pattern.compile("C\\d\\d\\d(-C\\d\\d\\d)?"), histPattern = Pattern.compile("\\d\\d\\d\\d(-\\d\\d\\d\\d)?");

        for (int year = 1998; year <= LocalDate.now().getYear(); year++) {

            // make sure the content of the XML only uses ASCII characters
            try {
                String file = "surgery/site-specific-surgery-tables-" + year + ".xml";
                String xml = SeerUtils.readUrl(Thread.currentThread().getContextClassLoader().getResource(file), StandardCharsets.US_ASCII.name());
                if (!SeerUtils.isPureAscii(xml))
                    Assert.fail(file + " contains non-ASCII characters");
            }
            catch (IOException e) {
                // ignored, just means the XML file doesn't exist for that year...
            }

            // register the instance we want
            SurgeryTablesDto data = SiteSpecificSurgeryUtils.getInstance().getTables(year);

            // make sure that are some tables
            Assert.assertNotNull(data.getVersion());
            Assert.assertNotNull(data.getVersionName());
            Assert.assertFalse(data.getTables().isEmpty());

            // test data validity
            for (SurgeryTableDto table : data.getTables()) {
                Assert.assertNotNull(table.getTitle());

                if (table.getSiteInclusion() != null)
                    for (String s : StringUtils.split(table.getSiteInclusion(), ','))
                        Assert.assertTrue(year + " - " + table.getTitle() + " - wrong site inclusion format: " + table.getSiteInclusion(), sitePattern.matcher(s).matches());

                if (table.getHistInclusion() != null)
                    for (String s : StringUtils.split(table.getHistInclusion(), ','))
                        Assert.assertTrue(year + " - " + table.getTitle() + " - wrong hist inclusion format: " + table.getHistInclusion(), histPattern.matcher(s).matches());

                if (table.getHistExclusion() != null)
                    for (String s : StringUtils.split(table.getHistExclusion(), ','))
                        Assert.assertTrue(year + " - " + table.getTitle() + " - wrong hist exclusion format: " + table.getHistExclusion(), histPattern.matcher(s).matches());

                for (SurgeryRowDto row : table.getRow()) {
                    Assert.assertNotNull(row.isLineBreak());
                    Assert.assertNotNull(row.getLevel());
                    if (row.isLineBreak()) {
                        Assert.assertNull(row.getCode());
                        Assert.assertNull(row.getDescription());
                    }
                }
            }
        }

        // test a few common searches
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, null, null));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, null, "8000"));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, "C000", null));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, "", ""));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, "", "8000"));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, "C000", ""));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, "?", "?"));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, "?", "8000"));
        Assert.assertNull(SiteSpecificSurgeryUtils.getInstance().getTable(2014, "C000", "whatever"));

        SurgeryTableDto table = SiteSpecificSurgeryUtils.getInstance().getTable(2014, "C000", "8000");
        Assert.assertEquals("Oral Cavity", table.getTitle());
        Assert.assertEquals(37, table.getRow().size());

        // C421 with any histology should be the Hemato table
        String hematoTitle = "Hematopoietic/Reticuloendothelial/Immunoproliferative/Myeloproliferative Disease";
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2009, "C421", "9875").getTitle());
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2018, "C421", "9875").getTitle());
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2018, "C421", "9732").getTitle());
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2018, "C421", "8000").getTitle());

        // specific hemato histology with any site should be the Hemato table
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2009, "C421", "9732").getTitle());
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2018, "C421", "9732").getTitle());
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2018, "C000", "9732").getTitle());
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2018, "C123", "9732").getTitle());
        Assert.assertEquals(hematoTitle, SiteSpecificSurgeryUtils.getInstance().getTable(2018, "C150", "9732").getTitle());
    }
}
