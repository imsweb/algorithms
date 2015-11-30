/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class MPGroupTest {

    @Test
    public void testVerify60DaysApart() {
        int unknown = -1, yes1 = 1, yes2 = 2, no = 0;

        MPInput i1 = new MPInput(), i2 = new MPInput();
        //if one of the diagnosis year is unknown or invalid or future year, unknown
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisYear("Invalid");
        i2.setDateOfDiagnosisYear("2002");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisYear(String.valueOf(LocalDate.now().getYear() + 1));
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));

        //2 years difference
        i1.setDateOfDiagnosisYear("2000");
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisYear("2004");
        Assert.assertEquals(yes1, MPGroup.verify60DaysApart(i1, i2, true));

        //1 year difference
        i1.setDateOfDiagnosisYear("2001");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        //if for the earlier year the diagnosis month is before November 1st, yes
        i1.setDateOfDiagnosisMonth("10");
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisMonth("11");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisDay("1");
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisDay("2");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisDay("1");
        i2.setDateOfDiagnosisMonth("1");
        Assert.assertEquals(no, MPGroup.verify60DaysApart(i1, i2, true));
        //If for the later year if diagnosis is after march 2 or march 1 (for leap year), yes
        i1 = new MPInput();
        i2 = new MPInput();
        i2.setDateOfDiagnosisYear("2001");
        i1.setDateOfDiagnosisMonth("2");
        i1.setDateOfDiagnosisYear("2002");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisMonth("4");
        Assert.assertEquals(yes1, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisMonth("3");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisDay("2");
        Assert.assertEquals(yes1, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisDay("1");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisYear("2003");
        i1.setDateOfDiagnosisYear("2004");
        Assert.assertEquals(yes1, MPGroup.verify60DaysApart(i1, i2, true));

        //Same Year
        i1 = new MPInput();
        i2 = new MPInput();
        i1.setDateOfDiagnosisYear("2001");
        i2.setDateOfDiagnosisYear("2001");
        i1.setDateOfDiagnosisMonth("5");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisMonth("2");
        Assert.assertEquals(yes1, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisMonth("8");
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisMonth("7");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisDay("31");
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisDay(null);
        i1.setDateOfDiagnosisDay("1");
        //1 month difference, No
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisMonth("6");
        Assert.assertEquals(no, MPGroup.verify60DaysApart(i1, i2, true));
        //equal month
        i2.setDateOfDiagnosisMonth("5");
        Assert.assertEquals(no, MPGroup.verify60DaysApart(i1, i2, true));
        //1 month difference, July and August, unknown
        i1 = new MPInput();
        i2 = new MPInput();
        i1.setDateOfDiagnosisYear("2001");
        i1.setDateOfDiagnosisMonth("8");
        i2.setDateOfDiagnosisYear("2001");
        i2.setDateOfDiagnosisMonth("7");
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, true));
        //leap year
        i1.setDateOfDiagnosisMonth("2");
        i1.setDateOfDiagnosisDay("2");
        i2.setDateOfDiagnosisMonth("4");
        i2.setDateOfDiagnosisDay("4");
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));
        i2.setDateOfDiagnosisDay("3");
        Assert.assertEquals(no, MPGroup.verify60DaysApart(i1, i2, true));
        i1.setDateOfDiagnosisYear("2004");
        i2.setDateOfDiagnosisYear("2004");
        Assert.assertEquals(yes2, MPGroup.verify60DaysApart(i1, i2, true));

        // If invasive is diagnosed before in situ
        i1 = new MPInput();
        i2 = new MPInput();
        i1.setDateOfDiagnosisYear("2007");
        i1.setBehaviorIcdO3("3");
        i2.setDateOfDiagnosisYear("2008");
        i2.setBehaviorIcdO3("2");
        Assert.assertEquals(no, MPGroup.verify60DaysApart(i1, i2, true));
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, false));

        // If invasive is diagnosed before in situ
        i1.setDateOfDiagnosisYear("2007");
        i1.setDateOfDiagnosisMonth("3");
        i1.setBehaviorIcdO3("3");
        i2.setDateOfDiagnosisYear("2007");
        i2.setDateOfDiagnosisMonth("5");
        i2.setBehaviorIcdO3("2");
        Assert.assertEquals(no, MPGroup.verify60DaysApart(i1, i2, true));
        Assert.assertEquals(unknown, MPGroup.verify60DaysApart(i1, i2, false));
    }

    @Test
    public void testVerifyYearsApart() {
        int unknown = -1, yes = 1, no = 0;
        MPInput i1 = new MPInput(), i2 = new MPInput();
        //if one of the diagnosis year is unknown or invalid or future year, unknown
        Assert.assertEquals(unknown, MPGroup.verifyYearsApart(i1, i2, 3));
        i1.setDateOfDiagnosisYear("Invalid");
        i2.setDateOfDiagnosisYear("2002");
        Assert.assertEquals(unknown, MPGroup.verifyYearsApart(i1, i2, 3));
        i1.setDateOfDiagnosisYear(String.valueOf(LocalDate.now().getYear() + 1));
        Assert.assertEquals(unknown, MPGroup.verifyYearsApart(i1, i2, 3));
        i1.setDateOfDiagnosisYear("2000");
        Assert.assertEquals(no, MPGroup.verifyYearsApart(i1, i2, 3));
        i1.setDateOfDiagnosisYear("2006");
        Assert.assertEquals(yes, MPGroup.verifyYearsApart(i1, i2, 3));
        i1.setDateOfDiagnosisYear("2005");
        Assert.assertEquals(unknown, MPGroup.verifyYearsApart(i1, i2, 3));
        i1.setDateOfDiagnosisMonth("8");
        i2.setDateOfDiagnosisMonth("9");
        Assert.assertEquals(no, MPGroup.verifyYearsApart(i1, i2, 3));
        i2.setDateOfDiagnosisMonth("7");
        Assert.assertEquals(yes, MPGroup.verifyYearsApart(i1, i2, 3));
        i2.setDateOfDiagnosisMonth("8");
        Assert.assertEquals(unknown, MPGroup.verifyYearsApart(i1, i2, 3));
        i1.setDateOfDiagnosisDay("3");
        i2.setDateOfDiagnosisDay("4");
        Assert.assertEquals(no, MPGroup.verifyYearsApart(i1, i2, 3));
        i2.setDateOfDiagnosisDay("1");
        Assert.assertEquals(yes, MPGroup.verifyYearsApart(i1, i2, 3));
    }
}
