/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.icd;

import org.junit.Assert;
import org.junit.Test;

import static com.imsweb.algorithms.icd.IcdUtils.REPORTABILITY_OPTIONAL;
import static com.imsweb.algorithms.icd.IcdUtils.REPORTABILITY_YES;
import static com.imsweb.algorithms.icd.IcdUtils.SEX_FEMALE;
import static com.imsweb.algorithms.icd.IcdUtils.SEX_MALE;

public class IcdUtilsTest {

    @Test
    public void testGetIcd9CmToO3Conversions() {
        Assert.assertTrue(IcdUtils.getIcd9CmToO3Conversions().containsKey("140"));
    }

    @Test
    public void testGetIcd10CmToO3Conversions() {
        Assert.assertTrue(IcdUtils.getIcd10CmToO3Conversions().containsKey("C00"));
    }

    @Test
    public void testGetIcd10ToO3Conversions() {
        Assert.assertTrue(IcdUtils.getIcd10ToO3Conversions().containsKey("C000"));
    }

    @Test
    public void testGetIcdO3SiteLookup() {
        Assert.assertTrue(IcdUtils.getIcdO3SiteLookup().containsKey("C000"));
    }

    @Test
    public void testGetIcdO3FromIcd9Cm() {
        IcdO3Entry icd = IcdUtils.getIcdO3FromIcd9Cm("1400", SEX_MALE);
        Assert.assertEquals("C000", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd9Cm("1734", SEX_FEMALE);
        Assert.assertEquals("C444", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_OPTIONAL, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd9Cm("2396", null);
        Assert.assertEquals("C719", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("1", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        //testing codes where gender matters
        icd = IcdUtils.getIcdO3FromIcd9Cm("2320", SEX_MALE);
        Assert.assertEquals("C440", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("2", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd9Cm("2320", null);
        Assert.assertEquals("C440", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("2", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd9Cm("2320", SEX_FEMALE);
        Assert.assertEquals("C440", icd.getSite());
        Assert.assertEquals("8720", icd.getHistology());
        Assert.assertEquals("2", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        Assert.assertNull(IcdUtils.getIcdO3FromIcd9Cm(null, null));

        Assert.assertNull(IcdUtils.getIcdO3FromIcd9Cm("NOTACODE", ""));
    }

    @Test
    public void testGetIcdO3FromIcd10Cm() {
        IcdO3Entry icd = IcdUtils.getIcdO3FromIcd10Cm("C00");
        Assert.assertEquals("C009", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd10Cm("C6312");
        Assert.assertEquals("C631", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("2", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd10Cm("Z5112");
        Assert.assertEquals("C809", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd10Cm("C4A52");
        Assert.assertEquals("C445", icd.getSite());
        Assert.assertEquals("8247", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertEquals("9", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd10Cm("C8308");
        Assert.assertEquals("C778", icd.getSite());
        Assert.assertEquals("9823", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("6", icd.getGrade());
        Assert.assertEquals("0", icd.getLaterality());
        Assert.assertEquals(REPORTABILITY_YES, icd.getReportable());

        Assert.assertNull(IcdUtils.getIcdO3FromIcd10Cm(null));

        Assert.assertNull(IcdUtils.getIcdO3FromIcd10Cm("NOTACODE"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testGetIcdO3FromIcd10() {
        IcdO3Entry icd = IcdUtils.getIcdO3FromIcd10("C000");
        Assert.assertEquals("C000", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertNull(icd.getLaterality());
        Assert.assertNull(icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd10("C00");
        Assert.assertEquals("C009", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertNull(icd.getLaterality());
        Assert.assertNull(icd.getReportable());

        icd = IcdUtils.getIcdO3FromIcd10("C999", true);
        Assert.assertNull(icd);

        icd = IcdUtils.getIcdO3FromIcd10("C999", false);
        Assert.assertEquals("C809", icd.getSite());
        Assert.assertEquals("8000", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals("9", icd.getGrade());
        Assert.assertNull(icd.getLaterality());
        Assert.assertNull(icd.getReportable());
    }

    @Test
    public void testGetIcdO2FromIcdO3() {

        // C343 9699 3 9680 3 1000
        IcdO2Entry icd = IcdUtils.getIcdO2FromIcdO3("C343", "9699", "3", false);
        Assert.assertEquals("C343", icd.getSite());
        Assert.assertEquals("9680", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals(IcdO2Entry.ConversionResultType.CONVERSION_SUCCESSFUL_NEEDS_HAND_REVIEW, icd.getConversionResult());

        // C900 8900 9 9999 9 0100
        icd = IcdUtils.getIcdO2FromIcdO3("C900", "8900", "9", false);
        Assert.assertEquals("C900", icd.getSite());
        Assert.assertEquals("9999", icd.getHistology());
        Assert.assertEquals("9", icd.getBehavior());
        Assert.assertEquals(IcdO2Entry.ConversionResultType.CONVERSION_FAILED_INVALID_SITE, icd.getConversionResult());

        // C503 7777 3 9999 9 0010
        icd = IcdUtils.getIcdO2FromIcdO3("C503", "7777", "3", false);
        Assert.assertEquals("C503", icd.getSite());
        Assert.assertEquals("9999", icd.getHistology());
        Assert.assertEquals("9", icd.getBehavior());
        Assert.assertEquals(IcdO2Entry.ConversionResultType.CONVERSION_FAILED_INVALID_HISTOLOGY, icd.getConversionResult());

        // C503 8480 8 9999 9 0001
        icd = IcdUtils.getIcdO2FromIcdO3("C503", "8480", "8", false);
        Assert.assertEquals("C503", icd.getSite());
        Assert.assertEquals("9999", icd.getHistology());
        Assert.assertEquals("9", icd.getBehavior());
        Assert.assertEquals(IcdO2Entry.ConversionResultType.CONVERSION_FAILED_INVALID_BEHAVIOR, icd.getConversionResult());

        // C300 8300 3 8300 3 0000
        icd = IcdUtils.getIcdO2FromIcdO3("C300", "8300", "3", false);
        Assert.assertEquals("C300", icd.getSite());
        Assert.assertEquals("8300", icd.getHistology());
        Assert.assertEquals("3", icd.getBehavior());
        Assert.assertEquals(IcdO2Entry.ConversionResultType.CONVERSION_SUCCESSFUL, icd.getConversionResult());

        // C343 9699 3 9680 3 1000
        icd = IcdUtils.getIcdO2FromIcdO3("C343", "9699", "3", true);
        Assert.assertNotNull(icd);
        // C900 8900 9 9999 9 0100
        icd = IcdUtils.getIcdO2FromIcdO3("C900", "8900", "9", true);
        Assert.assertNull(icd);
        // C503 7777 3 9999 9 0010
        icd = IcdUtils.getIcdO2FromIcdO3("C503", "7777", "3", true);
        Assert.assertNull(icd);
        // C503 8480 8 9999 9 0001
        icd = IcdUtils.getIcdO2FromIcdO3("C503", "8480", "8", true);
        Assert.assertNull(icd);
        // C300 8300 3 8300 3 0000
        icd = IcdUtils.getIcdO2FromIcdO3("C300", "8300", "3", true);
        Assert.assertNotNull(icd);
    }

    // this is a one-time test using a giant file; we are leaving it here in case we need it later

    /*
    @Test
    public void testFileCompareIcdo2FromIcdo3() {

        String sInputFile = "C:\\ICDO3_ICDO2\\Examples\\AllCases\\ICDO3Codes.out.txt";
        final int ICDO3_SITE_POS = 0;
        final int ICDO3_HIST_POS = 5;
        final int ICDO3_BEH_POS = 10;
        final int ICDO2_HIST_POS = 12;
        final int ICDO2_BEH_POS = 17;
        final int ICDO2_FLAGS_POS = 19;
        final int SITE_LENGTH = 4;
        final int HIST_LENGTH = 4;
        final int BEH_LENGTH = 1;
        final int FLAGS_LENGTH = 4;

        IcdO2Entry icd;
        IcdO2Entry.ConversionResultType flagsResult;
        String icdo3Site, icdo3Histology, icdo3Behavior;
        String icdo2Histology, icdo2Behavior, icdo2Flags;
        boolean hasUnknownFlag;
        boolean hasDifference;
        String differentValuesNames;
        int lineProcessedCount = 0;
        int lineTotalCount = 0;
        int lineStatusCount = 0;
        int nonMatchesCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(sInputFile))) {
            String sLine;
            while ((sLine = reader.readLine()) != null)
            {
                lineTotalCount++;
                sLine = sLine.trim();

                if (sLine.length() > 0) {

                    icdo3Site = sLine.substring(ICDO3_SITE_POS, ICDO3_SITE_POS + SITE_LENGTH);
                    icdo3Histology = sLine.substring(ICDO3_HIST_POS, ICDO3_HIST_POS + HIST_LENGTH);
                    icdo3Behavior = sLine.substring(ICDO3_BEH_POS, ICDO3_BEH_POS + BEH_LENGTH);
                    icdo2Histology = sLine.substring(ICDO2_HIST_POS, ICDO2_HIST_POS + HIST_LENGTH);
                    icdo2Behavior = sLine.substring(ICDO2_BEH_POS, ICDO2_BEH_POS + BEH_LENGTH);
                    icdo2Flags = sLine.substring(ICDO2_FLAGS_POS, ICDO2_FLAGS_POS + FLAGS_LENGTH);

                    hasUnknownFlag = false;
                    flagsResult = IcdO2Entry.ConversionResultType.CONVERSION_FAILED_INVALID_BEHAVIOR;
                    if (icdo2Flags.equals("0000")) flagsResult = IcdO2Entry.ConversionResultType.CONVERSION_SUCCESSFUL;
                    else if (icdo2Flags.equals("1000")) flagsResult = IcdO2Entry.ConversionResultType.CONVERSION_SUCCESSFUL_NEEDS_HAND_REVIEW;
                    else if (icdo2Flags.equals("0100")) flagsResult = IcdO2Entry.ConversionResultType.CONVERSION_FAILED_INVALID_SITE;
                    else if (icdo2Flags.equals("0010")) flagsResult = IcdO2Entry.ConversionResultType.CONVERSION_FAILED_INVALID_HISTOLOGY;
                    else if (icdo2Flags.equals("0001")) flagsResult = IcdO2Entry.ConversionResultType.CONVERSION_FAILED_INVALID_BEHAVIOR;
                    else hasUnknownFlag = true;

                    icd = IcdUtils.getIcdO2FromIcdO3(icdo3Site, icdo3Histology, icdo3Behavior, false);

                    hasDifference = false;
                    differentValuesNames = "";
                    if (!icd.getHistology().equals(icdo2Histology)) {
                        hasDifference = true;
                        differentValuesNames += "Histology";
                    }
                    if (!icd.getBehavior().equals(icdo2Behavior)) {
                        hasDifference = true;
                        if (differentValuesNames.length() > 0) differentValuesNames += ", ";
                        differentValuesNames += "Behavior";
                    }
                    if (!icd.getConversionResult().equals(flagsResult)) {
                        hasDifference = true;
                        if (differentValuesNames.length() > 0) differentValuesNames += ", ";
                        differentValuesNames += "Conversion Result";
                    }
                    if (hasUnknownFlag) {
                        hasDifference = true;
                        if (differentValuesNames.length() > 0) differentValuesNames += ", ";
                        differentValuesNames += "Unknown Flag";
                    }


                    if (hasDifference) {
                        System.out.println("Problem on line " + lineTotalCount + ": ");
                        System.out.println("  sICDO3Site:    " + icdo3Site);
                        System.out.println("  sICDO3Hist:    " + icdo3Histology);
                        System.out.println("  sICDO3Beh:     " + icdo3Behavior);
                        System.out.println("  sICDO2Hist:    " + icdo2Histology);
                        System.out.println("  sICDO2Beh:     " + icdo2Behavior);
                        System.out.println("  sICDO2Flags:   " + icdo2Flags);
                        System.out.println("  icd.Hist:      " + icd.getHistology());
                        System.out.println("  icd.Beh:       " + icd.getBehavior());
                        System.out.println("  icd.ConvRes:   " + icd.getConversionResult());
                        System.out.println("  Unknown Flag:  " + (hasUnknownFlag ? "TRUE" : "FALSE"));
                        System.out.println("  Differences:   " + differentValuesNames);
                        nonMatchesCount++;
                    }
                    lineProcessedCount++;
                }

                lineStatusCount++;
                if (lineStatusCount >= 10000) {
                    System.out.println("  Lines Read: " + lineTotalCount);
                    lineStatusCount = 0;
                }

            }
            System.out.println("====================================");
            System.out.println("Lines Processed:       " + lineProcessedCount + " / " + lineTotalCount);
            System.out.println("  Matches:             " + (lineProcessedCount - nonMatchesCount));
            System.out.println("  Non-Matches:         " + nonMatchesCount);
            System.out.println("====================================");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    */

}
