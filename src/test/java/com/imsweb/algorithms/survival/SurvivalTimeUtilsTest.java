/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.survival;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class SurvivalTimeUtilsTest {

    @Test
    public void assertAlgInfo() {
        Assert.assertNotNull(SurvivalTimeUtils.VERSION);
        Assert.assertNotNull(SurvivalTimeUtils.ALG_NAME);
    }

    @Test
    @SuppressWarnings("java:S5961") // too much complexity
    public void testLogic() {
        Assert.assertTrue(SurvivalTimeUtils.calculateSurvivalTime(new SurvivalTimeInputPatientDto(), 2020).getSurvivalTimeOutputPatientDtoList().isEmpty());

        List<SurvivalTimeInputRecordDto> records = new ArrayList<>();

        //different dolc
        records.add(createRecord(2008, 1, 1, 2010, 10, 11, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(2011, 1, 2, 2010, 10, 11, 1956, 3, 4, 2, 0, 1));
        records.add(createRecord(2010, 3, 1, 2010, 10, 21, 1956, 3, 4, 3, 0, 1));
        records.add(createRecord(2007, 11, 11, 2010, 10, 11, 1956, 3, 4, 4, 0, 1));
        SurvivalTimeOutputPatientDto results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "9999", "99999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "99999", "9", "9999", "99999", "9", 4);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "9999", "99999", "9", "9999", "99999", "9", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(2), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(3), "9999", "99999", "9", "9999", "99999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(3), null, null, null, null, null, null, null, null, null);

        //different dolc
        records.clear();
        records.add(createRecord(2008, 1, 1, 2010, 10, 11, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(2010, 1, 2, 2010, 7, 11, 1956, 3, 4, 2, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "9999", "99999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "99999", "9", "9999", "99999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);

        //different dolc
        records.clear();
        records.add(createRecord(2008, 1, 1, 2009, 10, 11, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(2010, 1, 2, 2010, 10, 11, 1956, 3, 4, 2, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "9999", "99999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "99999", "9", "9999", "99999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);

        //different vs
        records.clear();
        records.add(createRecord(2008, 1, 1, 2010, 10, 11, 1956, 3, 4, 1, 1, 1));
        records.add(createRecord(2010, 1, 2, 2010, 10, 11, 1956, 3, 4, 2, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertNull(results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "9999", "99999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "99999", "9", "9999", "99999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);

        //Dolc is in the future (next year) and patient is dead
        records.clear();
        records.add(createRecord(2010, 9, 15, LocalDate.now().getYear() + 1, 1, 1, 9999, 99, 99, 1, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "9999", "99999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2010", "9", "15", null, null, null, null, null, null);
        //Dolc is in the future (tomorrow) and patient is alive
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        records.clear();
        records.add(createRecord(2010, 9, 15, tomorrow.getYear(), tomorrow.getMonthValue(), tomorrow.getDayOfMonth(), 9999, 99, 99, -1, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("1", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "0003", "00107", "1", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2010", "9", "15", null, null, null, "2010", "12", "31");
        //Dx is after end point year (invalid), dolc year is valid
        records.clear();
        records.add(createRecord(2011, 9, 15, 2010, 99, 99, 9999, 99, 99, 1, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("1", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "9999", "99999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, "2010", "07", "02", "2010", "12", "31");

        //Valid and Invalid years in same patient
        records.clear();
        //missing info some survival, flag 3, complete info presumed alive some survival, flag 1
        records.add(createRecord(2009, 9, 1, 2009, 11, 99, 2008, 3, 4, 1, 1, 1));
        //missing info some survival, flag 3
        records.add(createRecord(2008, 99, 99, 2009, 11, 99, 2008, 3, 4, 2, 1, 1));
        //missing info some survival, flag 3
        records.add(createRecord(2008, 3, 99, 2009, 11, 99, 2008, 3, 4, 3, 1, 1));
        //invalid dx year
        records.add(createRecord(9999, 99, 99, 2009, 11, 99, 2008, 3, 4, 4, 1, 1));
        //missing info no survival, flag 2, missing info some survival presumed alive. flag 3
        records.add(createRecord(2009, 99, 23, 2009, 11, 99, 2008, 3, 4, 5, 1, 1));
        //missing info some survival, flag 3, complete info presumed alive some survival, flag 1
        records.add(createRecord(2008, 4, 5, 2009, 11, 99, 2008, 3, 4, 6, 1, 1));
        //missing info no survival, flag 2, missing info some survival presumed alive. flag 3
        records.add(createRecord(2009, 11, 99, 2009, 11, 99, 2008, 3, 4, 7, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("1", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0002", "00082", "3", "0015", "00486", "1", 4);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2009", "9", "1", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "0020", "00622", "3", "0033", "01026", "3", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), "2008", "3", "10", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "0020", "00616", "3", "0033", "01020", "3", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(2), "2008", "3", "16", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(3), "9999", "99999", "9", "9999", "99999", "9", 5);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(3), null, null, null, "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(4), "0001", "00045", "2", "0014", "00449", "3", 6);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(4), "2009", "10", "08", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(5), "0019", "00596", "3", "0032", "01000", "1", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(5), "2008", "4", "5", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(6), "0000", "00007", "2", "0013", "00411", "3", 7);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(6), "2009", "11", "15", "2009", "11", "22", "2010", "12", "31");

        records.clear();
        //complete info some survival, flag 1
        records.add(createRecord(2009, 11, 10, 2009, 11, 11, 2008, 3, 4, 1, 1, 1));
        //complete info no survival, flag 0, complete info some survival presumed alive flag 1
        records.add(createRecord(2009, 11, 11, 2009, 11, 11, 2008, 3, 4, 2, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0000", "00001", "1", "0013", "00416", "1", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2009", "11", "10", "2009", "11", "11", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "0000", "00000", "0", "0013", "00415", "1", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), "2009", "11", "11", "2009", "11", "11", "2010", "12", "31");

        //DCO/Autopsy and using birthday
        records.clear();
        //diagnosis in birth year but missing date
        records.add(createRecord(2008, 99, 99, 2010, 99, 99, 2008, 3, 4, 1, 0, 1));
        //dco/autopsy
        records.add(createRecord(2010, 99, 99, 2010, 99, 99, 2008, 3, 4, 2, 0, 6));
        //dco/autopsy
        records.add(createRecord(2010, 3, 99, 2010, 99, 99, 2008, 3, 4, 3, 0, 7));
        //not dco/autopsy
        records.add(createRecord(2010, 99, 99, 2010, 99, 99, 2008, 3, 4, 4, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0026", "00808", "3", "0026", "00808", "3", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2008", "8", "2", "2010", "10", "19", "2010", "10", "19");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "99999", "8", "9999", "99999", "8", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), "2010", "2", "07", "2010", "10", "19", "2010", "10", "19");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "9999", "99999", "8", "9999", "99999", "8", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(2), "2010", "3", "16", "2010", "10", "19", "2010", "10", "19");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(3), "0002", "00072", "2", "0002", "00072", "2", 4);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(3), "2010", "8", "8", "2010", "10", "19", "2010", "10", "19");
        //Vital status recode is vital status at the study cutoff date.
        results = calculateSurvivalTime(records, 2008);
        Assert.assertEquals("1", results.getVitalStatusRecode());

        // special case: using a time that doesn't exist because of daylight saving gaps (#329); this used to fail...
        records.clear();
        records.add(createRecord(2010, 3, 14, 2010, 3, 14, 2008, 3, 4, 1, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0000", "00000", "0", "0009", "00292", "1", 1);

        //test all corner cases of the sortedIndex (#359)
        records.clear();
        records.add(createRecord(9999, 1, 1, 2012, 12, 31, 1956, 3, 4, 0, 0, 1));
        records.add(createRecord(2013, 1, 2, 2012, 12, 31, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(2013, 1, 2, 2012, 12, 31, 1956, 3, 4, 2, 0, 1));
        records.add(createRecord(2010, 3, 1, 2012, 12, 31, 1956, 3, 4, 61, 0, 1)); //non-federal
        records.add(createRecord(2010, 99, 99, 2012, 12, 31, 1956, 3, 4, 99, 0, 1)); // federal should be before non-federal
        records.add(createRecord(9999, 1, 1, 2012, 12, 31, 1956, 3, 4, 3, 0, 1));
        records.add(createRecord(2009, 11, 11, 2012, 12, 31, 1956, 3, 4, 4, 0, 1));
        records.add(createRecord(9999, 11, 11, 2012, 12, 31, 1956, 3, 4, 5, 0, 1));
        records.add(createRecord(9999, 12, 12, 2012, 12, 31, 1956, 3, 4, 6, 0, 1));
        records.add(createRecord(2008, 3, 1, 2012, 12, 31, 1956, 3, 4, 7, 0, 1));
        records.add(createRecord(2010, 1, 1, 2012, 12, 31, 1956, 3, 4, 9, 0, 1));
        records.add(createRecord(2011, 1, 2, 2012, 12, 31, 1956, 3, 4, 8, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals(1, results.getSurvivalTimeOutputPatientDtoList().get(0).getSortedIndex().intValue());
        Assert.assertEquals(8, results.getSurvivalTimeOutputPatientDtoList().get(1).getSortedIndex().intValue());
        Assert.assertEquals(9, results.getSurvivalTimeOutputPatientDtoList().get(2).getSortedIndex().intValue());
        Assert.assertEquals(6, results.getSurvivalTimeOutputPatientDtoList().get(3).getSortedIndex().intValue());
        Assert.assertEquals(5, results.getSurvivalTimeOutputPatientDtoList().get(4).getSortedIndex().intValue());
        Assert.assertEquals(10, results.getSurvivalTimeOutputPatientDtoList().get(5).getSortedIndex().intValue());
        Assert.assertEquals(3, results.getSurvivalTimeOutputPatientDtoList().get(6).getSortedIndex().intValue());
        Assert.assertEquals(11, results.getSurvivalTimeOutputPatientDtoList().get(7).getSortedIndex().intValue());
        Assert.assertEquals(12, results.getSurvivalTimeOutputPatientDtoList().get(8).getSortedIndex().intValue());
        Assert.assertEquals(2, results.getSurvivalTimeOutputPatientDtoList().get(9).getSortedIndex().intValue());
        Assert.assertEquals(4, results.getSurvivalTimeOutputPatientDtoList().get(10).getSortedIndex().intValue());
        Assert.assertEquals(7, results.getSurvivalTimeOutputPatientDtoList().get(11).getSortedIndex().intValue());

        //Testing record number recode
        records.clear();
        records.add(createRecord(2017, 11, 14, 2018, 7, 13, 1956, 3, 4, 0, 1, 1));
        records.add(createRecord(2017, 1, 11, 2018, 7, 13, 1956, 3, 4, 60, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "99999", "9", "9999", "99999", "9", 2);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "99999", "9", "9999", "99999", "9", 1);

        //Testing record number recode
        records.clear();
        records.add(createRecord(2015, 3, 5, 2015, 4, 27, 1937, 1, 99, 1, 0, 1));
        records.add(createRecord(2015, 99, 99, 2015, 4, 27, 1937, 1, 99, 2, 0, 1));
        records.add(createRecord(2015, 2, 23, 2015, 4, 27, 1937, 1, 99, 60, 0, 1));
        results = calculateSurvivalTime(records, 2016);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0001", "00053", "1", "0001", "00053", "1", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2015", "03", "05", "2015", "04", "27", "2015", "04", "27");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "0000", "00027", "2", "0000", "00027", "2", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), "2015", "03", "31", "2015", "04", "27", "2015", "04", "27");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "0002", "00063", "1", "0002", "00063", "1", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(2), "2015", "02", "23", "2015", "04", "27", "2015", "04", "27");

        records.clear();
        records.add(createRecord(1987, 1, 14, 2012, 12, 31, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(1989, 2, 27, 2012, 12, 31, 1956, 3, 4, 2, 0, 1));
        records.add(createRecord(1990, 10, 22, 2012, 12, 31, 1956, 3, 4, 3, 0, 1));
        records.add(createRecord(9999, 99, 99, 2012, 12, 31, 1956, 3, 4, 61, 0, 1));
        records.add(createRecord(1989, 2, 27, 2012, 12, 31, 1956, 3, 4, 62, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals(1, results.getSurvivalTimeOutputPatientDtoList().get(0).getSortedIndex().intValue());
        Assert.assertEquals(2, results.getSurvivalTimeOutputPatientDtoList().get(1).getSortedIndex().intValue());
        Assert.assertEquals(5, results.getSurvivalTimeOutputPatientDtoList().get(2).getSortedIndex().intValue());
        Assert.assertEquals(3, results.getSurvivalTimeOutputPatientDtoList().get(3).getSortedIndex().intValue());
        Assert.assertEquals(4, results.getSurvivalTimeOutputPatientDtoList().get(4).getSortedIndex().intValue());

        records.clear();
        records.add(createRecord(1987, 10, 14, 2012, 12, 31, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(1987, 99, 99, 2012, 12, 31, 1956, 3, 4, 61, 0, 1));
        records.add(createRecord(1987, 5, 27, 2012, 12, 31, 1956, 3, 4, 62, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals(3, results.getSurvivalTimeOutputPatientDtoList().get(0).getSortedIndex().intValue());
        Assert.assertEquals(1, results.getSurvivalTimeOutputPatientDtoList().get(1).getSortedIndex().intValue());
        Assert.assertEquals(2, results.getSurvivalTimeOutputPatientDtoList().get(2).getSortedIndex().intValue());

        records.clear();
        records.add(createRecord(1987, 10, 14, 2012, 12, 31, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(1987, 99, 99, 2012, 12, 31, 1956, 3, 4, 2, 0, 1));
        records.add(createRecord(1987, 5, 27, 2012, 12, 31, 1956, 3, 4, 3, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals(2, results.getSurvivalTimeOutputPatientDtoList().get(0).getSortedIndex().intValue());
        Assert.assertEquals(3, results.getSurvivalTimeOutputPatientDtoList().get(1).getSortedIndex().intValue());
        Assert.assertEquals(1, results.getSurvivalTimeOutputPatientDtoList().get(2).getSortedIndex().intValue());

        records.clear();
        records.add(createRecord(1987, 10, 14, 2012, 12, 31, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(1987, 5, 27, 2012, 12, 31, 1956, 3, 4, 61, 0, 1));
        records.add(createRecord(1987, 99, 99, 2012, 12, 31, 1956, 3, 4, 62, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals(2, results.getSurvivalTimeOutputPatientDtoList().get(0).getSortedIndex().intValue());
        Assert.assertEquals(1, results.getSurvivalTimeOutputPatientDtoList().get(1).getSortedIndex().intValue());
        Assert.assertEquals(3, results.getSurvivalTimeOutputPatientDtoList().get(2).getSortedIndex().intValue());

        records.clear();
        records.add(createRecord(1990, 1, 17, 2012, 12, 31, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(1990, 1, 2, 2012, 12, 31, 1956, 3, 4, 2, 0, 1));
        records.add(createRecord(1990, 1, 99, 2012, 12, 31, 1956, 3, 4, 61, 0, 1));
        records.add(createRecord(1990, 1, 15, 2012, 12, 31, 1956, 3, 4, 62, 0, 1));
        records.add(createRecord(1990, 1, 99, 2012, 12, 31, 1956, 3, 4, 63, 0, 1));
        records.add(createRecord(1990, 1, 10, 2012, 12, 31, 1956, 3, 4, 64, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals(4, results.getSurvivalTimeOutputPatientDtoList().get(0).getSortedIndex().intValue());
        Assert.assertEquals(1, results.getSurvivalTimeOutputPatientDtoList().get(1).getSortedIndex().intValue());
        Assert.assertEquals(5, results.getSurvivalTimeOutputPatientDtoList().get(2).getSortedIndex().intValue());
        Assert.assertEquals(3, results.getSurvivalTimeOutputPatientDtoList().get(3).getSortedIndex().intValue());
        Assert.assertEquals(6, results.getSurvivalTimeOutputPatientDtoList().get(4).getSortedIndex().intValue());
        Assert.assertEquals(2, results.getSurvivalTimeOutputPatientDtoList().get(5).getSortedIndex().intValue());

        records.clear();
        records.add(createRecord(1990, 1, 17, 2012, 12, 31, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(1990, 1, 2, 2012, 12, 31, 1956, 3, 4, 2, 0, 1));
        records.add(createRecord(1990, 1, 99, 2012, 12, 31, 1956, 3, 4, 61, 0, 1));
        records.add(createRecord(1990, 1, 15, 2012, 12, 31, 1956, 3, 4, 62, 0, 1));
        records.add(createRecord(1990, 1, 99, 2012, 12, 31, 1956, 3, 4, 63, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals(4, results.getSurvivalTimeOutputPatientDtoList().get(0).getSortedIndex().intValue());
        Assert.assertEquals(1, results.getSurvivalTimeOutputPatientDtoList().get(1).getSortedIndex().intValue());
        Assert.assertEquals(2, results.getSurvivalTimeOutputPatientDtoList().get(2).getSortedIndex().intValue());
        Assert.assertEquals(3, results.getSurvivalTimeOutputPatientDtoList().get(3).getSortedIndex().intValue());
        Assert.assertEquals(5, results.getSurvivalTimeOutputPatientDtoList().get(4).getSortedIndex().intValue());

        records.clear();
        records.add(createRecord(1998, 1, 7, 1926, 6, 2, 1962, 11, 27, 0, 0, 1));
        results = calculateSurvivalTime(records, 2023);
        Assert.assertEquals("9999", results.getSurvivalTimeOutputPatientDtoList().get(0).getSurvivalMonths());
        Assert.assertEquals("99999", results.getSurvivalTimeOutputPatientDtoList().get(0).getSurvivalDays());
        Assert.assertEquals("9999", results.getSurvivalTimeOutputPatientDtoList().get(0).getSurvivalMonthsPresumedAlive());
        Assert.assertEquals("99999", results.getSurvivalTimeOutputPatientDtoList().get(0).getSurvivalDaysPresumedAlive());
    }

    private SurvivalTimeOutputPatientDto calculateSurvivalTime(List<SurvivalTimeInputRecordDto> records, int endPointYear) {
        SurvivalTimeInputPatientDto input = new SurvivalTimeInputPatientDto();
        input.setSurvivalTimeInputPatientDtoList(records);
        return SurvivalTimeUtils.calculateSurvivalTime(input, endPointYear);
    }

    private SurvivalTimeInputRecordDto createRecord(int year, int month, int day, int dolcYear, int dolcMonth, int dolcDay, int birthYear, int birthMonth, int birthDay, int seq, int vs, int source) {
        SurvivalTimeInputRecordDto rec = new SurvivalTimeInputRecordDto();

        rec.setDateOfDiagnosisYear(String.valueOf(year));
        rec.setDateOfDiagnosisMonth(String.valueOf(month));
        rec.setDateOfDiagnosisDay(String.valueOf(day));
        rec.setSequenceNumberCentral(String.valueOf(seq));
        rec.setDateOfLastContactYear(String.valueOf(dolcYear));
        rec.setDateOfLastContactMonth(String.valueOf(dolcMonth));
        rec.setDateOfLastContactDay(String.valueOf(dolcDay));
        rec.setBirthYear(String.valueOf(birthYear));
        rec.setBirthMonth(String.valueOf(birthMonth));
        rec.setBirthDay(String.valueOf(birthDay));
        rec.setVitalStatus(String.valueOf(vs));
        rec.setTypeOfReportingSource(String.valueOf(source));

        return rec;
    }

    private void assertResults(SurvivalTimeOutputRecordDto rec, String survival, String days, String flag, String surivalPresumedAlive, String daysPresumedAlive, String flagPrsumedAlive, Integer sortedIndex) {
        Assert.assertEquals("Survival Months", survival, rec.getSurvivalMonths());
        Assert.assertEquals("Survival Days", days, rec.getSurvivalDays());
        Assert.assertEquals("Survival Flag", flag, rec.getSurvivalMonthsFlag());
        Assert.assertEquals("Survival Months PA", surivalPresumedAlive, rec.getSurvivalMonthsPresumedAlive());
        Assert.assertEquals("Survival Days PA", daysPresumedAlive, rec.getSurvivalDaysPresumedAlive());
        Assert.assertEquals("Survival Flag PA", flagPrsumedAlive, rec.getSurvivalMonthsFlagPresumedAlive());
        Assert.assertEquals("Sorted Index", sortedIndex, rec.getSortedIndex());
    }

    private void assertSurvivalDates(SurvivalTimeOutputRecordDto rec, String dxYear, String dxMonth, String dxDay, String dolcYear, String dolcMonth, String dolcDay, String dolcYearPres, String dolcMonthPres, String dolcDayPres) {
        Assert.assertEquals(StringUtils.leftPad(dxYear, 4, "0"), StringUtils.leftPad(rec.getSurvivalTimeDxYear(), 4, "0"));
        Assert.assertEquals(StringUtils.leftPad(dxMonth, 2, "0"), StringUtils.leftPad(rec.getSurvivalTimeDxMonth(), 2, "0"));
        Assert.assertEquals(StringUtils.leftPad(dxDay, 2, "0"), StringUtils.leftPad(rec.getSurvivalTimeDxDay(), 2, "0"));
        Assert.assertEquals(StringUtils.leftPad(dolcYear, 4, "0"), StringUtils.leftPad(rec.getSurvivalTimeDolcYear(), 4, "0"));
        Assert.assertEquals(StringUtils.leftPad(dolcMonth, 2, "0"), StringUtils.leftPad(rec.getSurvivalTimeDolcMonth(), 2, "0"));
        Assert.assertEquals(StringUtils.leftPad(dolcDay, 2, "0"), StringUtils.leftPad(rec.getSurvivalTimeDolcDay(), 2, "0"));
        Assert.assertEquals(StringUtils.leftPad(dolcYearPres, 4, "0"), StringUtils.leftPad(rec.getSurvivalTimeDolcYearPresumedAlive(), 4, "0"));
        Assert.assertEquals(StringUtils.leftPad(dolcMonthPres, 2, "0"), StringUtils.leftPad(rec.getSurvivalTimeDolcMonthPresumedAlive(), 2, "0"));
        Assert.assertEquals(StringUtils.leftPad(dolcDayPres, 2, "0"), StringUtils.leftPad(rec.getSurvivalTimeDolcDayPresumedAlive(), 2, "0"));
    }
}
