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

@SuppressWarnings("DanglingJavadoc")
public class SurvivalTimeUtilsTest {

    @Test
    public void assertAlgInfo() {
        Assert.assertNotNull(SurvivalTimeUtils.VERSION);
        Assert.assertNotNull(SurvivalTimeUtils.ALG_NAME);
    }

    @Test
    @SuppressWarnings("java:S5961") // too much complexity
    public void testLogic() {

        List<SurvivalTimeInputRecordDto> records = new ArrayList<>();

        //different dolc
        records.add(createRecord(2008, 1, 1, 2010, 10, 11, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(2011, 1, 2, 2010, 10, 11, 1956, 3, 4, 2, 0, 1));
        records.add(createRecord(2010, 3, 1, 2010, 10, 21, 1956, 3, 4, 3, 0, 1));
        records.add(createRecord(2007, 11, 11, 2010, 10, 11, 1956, 3, 4, 4, 0, 1));
        SurvivalTimeOutputPatientDto results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "9999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "9", "9999", "9", 4);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "9999", "9", "9999", "9", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(2), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(3), "9999", "9", "9999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(3), null, null, null, null, null, null, null, null, null);

        //different dolc
        records.clear();
        records.add(createRecord(2008, 1, 1, 2010, 10, 11, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(2010, 1, 2, 2010, 7, 11, 1956, 3, 4, 2, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "9999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "9", "9999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);

        //different dolc
        records.clear();
        records.add(createRecord(2008, 1, 1, 2009, 10, 11, 1956, 3, 4, 1, 0, 1));
        records.add(createRecord(2010, 1, 2, 2010, 10, 11, 1956, 3, 4, 2, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "9999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "9", "9999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);

        //different vs
        records.clear();
        records.add(createRecord(2008, 1, 1, 2010, 10, 11, 1956, 3, 4, 1, 1, 1));
        records.add(createRecord(2010, 1, 2, 2010, 10, 11, 1956, 3, 4, 2, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertNull(results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "9999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), null, null, null, null, null, null, null, null, null);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "9", "9999", "9", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), null, null, null, null, null, null, null, null, null);

        //Dolc is in the future (next year) and patient is dead
        records.clear();
        records.add(createRecord(2010, 9, 15, LocalDate.now().getYear() + 1, 1, 1, 9999, 99, 99, 1, 0, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("0", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "9999", "9", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2010", "9", "15", null, null, null, null, null, null);
        //Dolc is in the future (tomorrow) and patient is alive
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        records.clear();
        records.add(createRecord(2010, 9, 15, tomorrow.getYear(), tomorrow.getMonthValue(), tomorrow.getDayOfMonth(), 9999, 99, 99, -1, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("1", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "0003", "1", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2010", "9", "15", null, null, null, "2010", "12", "31");
        //Dx is after end point year (invalid), dolc year is valid
        records.clear();
        records.add(createRecord(2011, 9, 15, 2010, 99, 99, 9999, 99, 99, 1, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        Assert.assertEquals("1", results.getVitalStatusRecode());
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "9999", "9", 1);
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
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "2", "3", "15", "1", 4);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2009", "9", "1", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "20", "3", "33", "3", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), "2008", "3", "10", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "20", "3", "33", "3", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(2), "2008", "3", "16", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(3), "9999", "9", "9999", "9", 5);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(3), null, null, null, "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(4), "1", "2", "14", "3", 6);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(4), "2009", "10", "08", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(5), "19", "3", "32", "1", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(5), "2008", "4", "5", "2009", "11", "22", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(6), "0", "2", "13", "3", 7);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(6), "2009", "11", "15", "2009", "11", "22", "2010", "12", "31");

        records.clear();
        //complete info some survival, flag 1
        records.add(createRecord(2009, 11, 10, 2009, 11, 11, 2008, 3, 4, 1, 1, 1));
        //complete info no survival, flag 0, complete info some survival presumed alive flag 1
        records.add(createRecord(2009, 11, 11, 2009, 11, 11, 2008, 3, 4, 2, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0", "1", "13", "1", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2009", "11", "10", "2009", "11", "11", "2010", "12", "31");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "0", "0", "13", "1", 2);
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
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "26", "3", "26", "3", 1);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2008", "8", "2", "2010", "10", "19", "2010", "10", "19");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "8", "9999", "8", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), "2010", "2", "07", "2010", "10", "19", "2010", "10", "19");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "9999", "8", "9999", "8", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(2), "2010", "3", "16", "2010", "10", "19", "2010", "10", "19");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(3), "2", "2", "2", "2", 4);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(3), "2010", "8", "8", "2010", "10", "19", "2010", "10", "19");
        //Vital status recode is vital status at the study cutoff date.
        results = calculateSurvivalTime(records, 2008);
        Assert.assertEquals("1", results.getVitalStatusRecode());

        // special case: using a time that doesn't exist because of daylight saving gaps (#329); this used to fail...
        records.clear();
        records.add(createRecord(2010, 3, 14, 2010, 3, 14, 2008, 3, 4, 1, 1, 1));
        results = calculateSurvivalTime(records, 2010);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0", "0", "9", "1", 1);

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
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "9999", "9", "9999", "9", 2);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "9999", "9", "9999", "9", 1);

        //Testing record number recode
        records.clear();
        records.add(createRecord(2015, 3, 5, 2015, 4, 27, 1937, 1, 99, 1, 0, 1));
        records.add(createRecord(2015, 99, 99, 2015, 4, 27, 1937, 1, 99, 2, 0, 1));
        records.add(createRecord(2015, 2, 23, 2015, 4, 27, 1937, 1, 99, 60, 0, 1));
        results = calculateSurvivalTime(records, 2016);
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(0), "0001", "1", "0001", "1", 2);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(0), "2015", "03", "05", "2015", "04", "27", "2015", "04", "27");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(1), "0000", "2", "0000", "2", 3);
        assertSurvivalDates(results.getSurvivalTimeOutputPatientDtoList().get(1), "2015", "03", "31", "2015", "04", "27", "2015", "04", "27");
        assertResults(results.getSurvivalTimeOutputPatientDtoList().get(2), "0002", "1", "0002", "1", 1);
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

    private void assertResults(SurvivalTimeOutputRecordDto rec, String survival, String flag, String surivalPresumedAlive, String flagPrsumedAlive, Integer sortedIndex) {
        Assert.assertEquals(StringUtils.leftPad(survival, 4, "0"), rec.getSurvivalMonths());
        Assert.assertEquals(flag, rec.getSurvivalMonthsFlag());
        Assert.assertEquals(StringUtils.leftPad(surivalPresumedAlive, 4, "0"), rec.getSurvivalMonthsPresumedAlive());
        Assert.assertEquals(flagPrsumedAlive, rec.getSurvivalMonthsFlagPresumedAlive());
        Assert.assertEquals(sortedIndex, rec.getSortedIndex());
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

    /**
     * FPD - I didn't move the file "survival-test.csv" over to this new project; it's not OK to commit a 100MB file to a small project, expecially 
     * a file that is used once in a while. I put the file on csb (\\ommi\csb\seerutils\data\survival-test.csv.gz) and I compressed it. If you need 
     * to re-run this test, just get it and uncompress it locally (or change this code to access it directly on csb)...
     */

    /******************************************************************************************************************
     public void testCsvFile() throws Exception {
     String dataFile = "tools-test-data/survival-tests.csv";

     // we are going to use the following properties
     List<String> properties = new ArrayList<String>();
     properties.add("patientIdNumber");
     properties.add("sequenceNumberCentral");
     properties.add("dateOfDiagnosisYear");
     properties.add("dateOfDiagnosisMonth");
     properties.add("dateOfDiagnosisDay");
     properties.add("dateOfLastContactYear");
     properties.add("dateOfLastContactMonth");
     properties.add("dateOfLastContactDay");
     properties.add("vitalStatus");
     properties.add("survivalMonths");
     properties.add("survivalMonthsFlag");
     properties.add("survivalMonthsPresumedAlive");
     properties.add("survivalMonthsFlagPresumedAlive");
     properties.add("survivalDxYear");
     properties.add("survivalDxMonth");
     properties.add("survivalDxDay");
     properties.add("survivalDolcYear");
     properties.add("survivalDolcMonth");
     properties.add("survivalDolcDay");
     properties.add("survivalDolcYearPres");
     properties.add("survivalDolcMonthPres");
     properties.add("survivalDolcDayPres");

     // create fake layout
     CommaSeparatedLayout layout = new CommaSeparatedLayout();
     layout.setLayoutId("survival-test");
     layout.setLayoutName("Survival Test");
     layout.setLayoutNumberOfFields(properties.size());
     layout.setSeparator(',');
     List<Field> fields = new ArrayList<Field>();
     for (int i = 0; i < properties.size(); i++) {
     Field field = new Field();
     field.setName(properties.get(i));
     field.setShortLabel(properties.get(i));
     field.setLongLabel(properties.get(i));
     field.setTrim(false);
     field.setIndex(i + 1);
     fields.add(field);
     }
     layout.setFields(fields);

     // read the data file
     LineNumberReader reader = new LineNumberReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(dataFile)));
     String currentPatIdNum = null;
     List<Map<String, String>> patient = new ArrayList<Map<String, String>>();
     Map<String, String> record = layout.readNextRecord(reader);
     //skip the first line
     record = layout.readNextRecord(reader);
     while (record != null) {
     record.put("sasSurvival", record.remove(SurvivalTimeUtils.PROP_SURVIVAL_MONTHS));
     record.put("sasSurvivalFlag", record.remove(SurvivalTimeUtils.PROP_SURVIVAL_MONTHS_FLAG));
     record.put("sasSurvivalPresumedAlive", record.remove(SurvivalTimeUtils.PROP_SURVIVAL_MONTHS_PRESUMED_ALIVE));
     record.put("sasSurvivalFlagPresumedAlive", record.remove(SurvivalTimeUtils.PROP_SURVIVAL_MONTHS_FLAG_PRESUMED_ALIVE));
     record.put("sasDxYear", record.remove("survivalDxYear"));
     record.put("sasDxMonth", record.remove("survivalDxMonth"));
     record.put("sasDxDay", record.remove("survivalDxDay"));
     record.put("sasDolcYear", record.remove("survivalDolcYear"));
     record.put("sasDolcMonth", record.remove("survivalDolcMonth"));
     record.put("sasDolcDay", record.remove("survivalDolcDay"));
     record.put("sasDolcYearPres", record.remove("survivalDolcYearPres"));
     record.put("sasDolcMonthPres", record.remove("survivalDolcMonthPres"));
     record.put("sasDolcDayPres", record.remove("survivalDolcDayPres"));

     String patIdNum = record.get("patientIdNumber");
     if (currentPatIdNum == null || !currentPatIdNum.equals(patIdNum)) {
     if (!patient.isEmpty())
     checkPatient(patient, reader.getLineNumber());
     patient.clear();
     currentPatIdNum = patIdNum;
     }
     patient.add(record);
     record = layout.readNextRecord(reader);
     }
     if (!patient.isEmpty())
     checkPatient(patient, reader.getLineNumber());
     System.out.println(reader.getLineNumber() + " records tested!");
     reader.close();
     }

     private void checkPatient(List<Map<String, String>> patient, long lineNumber) {
     SurvivalTimeOutputPatientDto results = SurvivalTimeUtils.calculateSurvivalTime(patient, 2010);
     List<SurvivalTimeOutputRecordDto> resultList = results.getSurvivalTimeOutputPatientDtoList();
     boolean same = true;
     for (int i = 0; i < patient.size(); i++) {
     boolean sameSurv = patient.get(i).get("sasSurvival").equals(resultList.get(i).getSurvivalMonths());
     boolean sameSurvFg = patient.get(i).get("sasSurvivalFlag").equals(resultList.get(i).getSurvivalMonthsFlag());
     boolean sameSurvPA = patient.get(i).get("sasSurvivalPresumedAlive").equals(resultList.get(i).getSurvivalMonthsPresumedAlive());
     boolean sameSurvFgPA = patient.get(i).get("sasSurvivalFlagPresumedAlive").equals(resultList.get(i).getSurvivalMonthsFlagPresumedAlive());
     boolean sameDxYear = patient.get(i).get("sasDxYear").equals(resultList.get(i).getSurvivalTimeDxYear());
     boolean sameDxMonth = patient.get(i).get("sasDxMonth").equals(resultList.get(i).getSurvivalTimeDxMonth());
     boolean sameDxDay = patient.get(i).get("sasDxDay").equals(resultList.get(i).getSurvivalTimeDxDay());
     boolean sameDolcYear = patient.get(i).get("sasDolcYear").equals(resultList.get(i).getSurvivalTimeDolcYear());
     boolean sameDolcMonth = patient.get(i).get("sasDolcMonth").equals(resultList.get(i).getSurvivalTimeDolcMonth());
     boolean sameDolcDay = patient.get(i).get("sasDolcDay").equals(resultList.get(i).getSurvivalTimeDolcDay());
     boolean sameDolcYearPres = patient.get(i).get("sasDolcYearPres").equals(resultList.get(i).getSurvivalTimeDolcYearPresumedAlive());
     boolean sameDolcMonthPres = patient.get(i).get("sasDolcMonthPres").equals(resultList.get(i).getSurvivalTimeDolcMonthPresumedAlive());
     boolean sameDolcDayPres = patient.get(i).get("sasDolcDayPres").equals(resultList.get(i).getSurvivalTimeDolcDayPresumedAlive());
     same &= sameSurv && sameSurvFg && sameSurvPA && sameSurvFgPA && sameDxYear && sameDxMonth && sameDxDay && sameDolcYear && sameDolcMonth && sameDolcDay & sameDolcYearPres &&
     sameDolcMonthPres && sameDolcDayPres;
     }
     if (!same) {
     StringBuilder buf = new StringBuilder();
     buf.append("Patient ID # ").append(patient.get(0).get("patientIdNumber")).append(":\n");
     for (int i = 0; i < patient.size(); i++) {
     buf.append("   line ").append(lineNumber).append(" (").append(patient.get(i).get("patientIdNumber")).append(") - ");
     buf.append("seq: ").append(StringUtils.leftPad(patient.get(i).get(SurvivalTimeUtils.PROP_SEQUENCE_NUMBER), 2, "0", true));
     String year = patient.get(i).get(SurvivalTimeUtils.PROP_DX_YEAR);
     String month = patient.get(i).get(SurvivalTimeUtils.PROP_DX_MONTH);
     String day = patient.get(i).get(SurvivalTimeUtils.PROP_DX_DAY);
     buf.append("; ORGDX: ").append(year).append("-").append(StringUtils.leftPad(String.valueOf(month), 2, "0", true)).append("-").append(StringUtils.leftPad(String.valueOf(day), 2, "0", true));
     year = patient.get(i).get(SurvivalTimeUtils.PROP_DOLC_YEAR);
     month = patient.get(i).get(SurvivalTimeUtils.PROP_DOLC_MONTH);
     day = patient.get(i).get(SurvivalTimeUtils.PROP_DOLC_DAY);
     buf.append("; ORGDOLC: ").append(year).append("-").append(month).append("-").append(day);
     year = patient.get(i).get("sasDxYear");
     month = patient.get(i).get("sasDxMonth");
     day = patient.get(i).get("sasDxDay");
     buf.append("; SASDX: ").append(year).append("-").append(StringUtils.leftPad(String.valueOf(month), 2, "0", true)).append("-").append(StringUtils.leftPad(String.valueOf(day), 2, "0", true));
     year = patient.get(i).get("sasDolcYear");
     month = patient.get(i).get("sasDolcMonth");
     day = patient.get(i).get("sasDolcDay");
     buf.append("; SASDOLC: ").append(year).append("-").append(month).append("-").append(day);
     year = patient.get(i).get("sasDolcYearPres");
     month = patient.get(i).get("sasDolcMonthPres");
     day = patient.get(i).get("sasDolcDayPres");
     buf.append("; SASDOLCPRES: ").append(year).append("-").append(month).append("-").append(day);
     year = resultList.get(i).getSurvivalTimeDxYear();
     month = resultList.get(i).getSurvivalTimeDxMonth();
     day = resultList.get(i).getSurvivalTimeDxDay();
     buf.append("; JAVADX: ").append(year).append("-").append(StringUtils.leftPad(String.valueOf(month), 2, "0", true)).append("-").append(StringUtils.leftPad(String.valueOf(day), 2, "0", true));
     year = resultList.get(i).getSurvivalTimeDolcYear();
     month = resultList.get(i).getSurvivalTimeDolcMonth();
     day = resultList.get(i).getSurvivalTimeDolcDay();
     buf.append("; JAVADOLC: ").append(year).append("-").append(month).append("-").append(day);
     year = resultList.get(i).getSurvivalTimeDolcYearPresumedAlive();
     month = resultList.get(i).getSurvivalTimeDolcMonthPresumedAlive();
     day = resultList.get(i).getSurvivalTimeDolcDayPresumedAlive();
     buf.append("; JAVADOLCPRES: ").append(year).append("-").append(month).append("-").append(day);
     buf.append("; vs: ").append(patient.get(i).get("vitalStatus"));
     buf.append("; SASsurvival=").append(patient.get(i).get("sasSurvival")).append("/").append(patient.get(i).get("sasSurvivalFlag"));
     buf.append("; SASsurvivalPA=").append(patient.get(i).get("sasSurvivalPresumedAlive")).append("/").append(patient.get(i).get("sasSurvivalFlagPresumedAlive"));
     buf.append("; JAVAsurvival=").append(resultList.get(i).getSurvivalMonths()).append("/").append(resultList.get(i).getSurvivalMonthsFlag());
     buf.append("; JAVAsurvivalPA=").append(resultList.get(i).getSurvivalMonthsPresumedAlive()).append("/").append(resultList.get(i).getSurvivalMonthsFlagPresumedAlive());
     buf.append("\n");
     }
     fail(buf.toString());
     }
     }
     */

    // **************************************************************************
    //   following code was used to compare results from the SAS program to the Java
    //   version, it expects a NAACCR Incidence file on which the SAS was already run
    // **************************************************************************
    /*******************************************************************************************************
     public static void testSasOutput() throws Exception {
     //File file = new File("E:\\miscellaneous\\test.txd.gz");
     File sasFile = new File("H:\\survival\\sas-results.txt");

     //Logger.getLogger(SurvivalTimeUtils.class).setLevel(Level.DEBUG);
     //String patToInvestigate = "09406730";
     String patToInvestigate = null;

     // extend NAACCR layout
     Layout layout = LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_INCIDENCE);

     // read the file
     long count = 0;
     LineNumberReader is = new LineNumberReader(new InputStreamReader(SeerUtils.createInputStream(sasFile)));
     String line = is.readLine();
     List<Map<String, String>> patient = new ArrayList<Map<String, String>>();
     String currentPatIdNum = null;
     while (line != null) {
     Map<String, String> rec = layout.createRecordFromLine(line);
     rec.put("sasSurvival", line.substring(2592, 2596));
     rec.put("sasSurvivalFlag", line.substring(2596, 2597));
     rec.put("sasSurvivalPresumedAlive", line.substring(2597, 2601));
     rec.put("sasSurvivalFlagPresumedAlive", line.substring(2601, 2602));
     rec.put("sasDxYear", line.substring(2602, 2606).equals(null) ? null : line.substring(2602, 2606));
     rec.put("sasDxMonth", line.substring(2606, 2608).equals(null) ? null : line.substring(2606, 2608));
     rec.put("sasDxDay", line.substring(2608, 2610).equals(null) ? null : line.substring(2608, 2610));
     rec.put("sasDolcYear", line.substring(2610, 2614).equals(null) ? null : line.substring(2610, 2614));
     rec.put("sasDolcMonth", line.substring(2614, 2616).equals(null) ? null : line.substring(2614, 2616));
     rec.put("sasDolcDay", line.substring(2616, 2618).equals(null) ? null : line.substring(2616, 2618));
     rec.put("sasDolcYearPres", line.substring(2618, 2622).equals(null) ? null : line.substring(2618, 2622));
     rec.put("sasDolcMonthPres", line.substring(2622, 2624).equals(null) ? null : line.substring(2622, 2624));
     rec.put("sasDolcDayPres", line.substring(2624, 2626).equals(null) ? null : line.substring(2624, 2626));
     String patIdNum = rec.get("patientIdNumber");
     if (currentPatIdNum == null || !currentPatIdNum.equals(patIdNum)) {
     if (!patient.isEmpty())
     if (patToInvestigate == null || patToInvestigate.equals(patient.get(0).get("patientIdNumber")))
     handlePatient(patient, is.getLineNumber() - 1);
     patient.clear();
     currentPatIdNum = patIdNum;
     }
     patient.add(rec);
     count++;
     line = is.readLine();
     }
     if (!patient.isEmpty())
     handlePatient(patient, is.getLineNumber());
     is.close();
     System.out.println("DONE - processed " + count + " records");
     }

     private static void handlePatient(List<Map<String, String>> patient, long lineNumber) {
     SurvivalTimeOutputPatientDto results = SurvivalTimeUtils.calculateSurvivalTime(patient, 2010);
     List<SurvivalTimeOutputRecordDto> resultList = results.getSurvivalTimeOutputPatientDtoList();
     boolean same = true;
     for (int i = 0; i < patient.size(); i++) {

     boolean sameSurv = patient.get(i).get("sasSurvival").equals(resultList.get(i).getSurvivalMonths());
     boolean sameSurvFg = patient.get(i).get("sasSurvivalFlag").equals(resultList.get(i).getSurvivalMonthsFlag());
     boolean sameSurvPA = patient.get(i).get("sasSurvivalPresumedAlive").equals(resultList.get(i).getSurvivalMonthsPresumedAlive());
     boolean sameSurvFgPA = patient.get(i).get("sasSurvivalFlagPresumedAlive").equals(resultList.get(i).getSurvivalMonthsFlagPresumedAlive());
     boolean sameDxYear = patient.get(i).get("sasDxYear") == null ? resultList.get(i).getSurvivalTimeDxYear() == null : patient.get(i).get("sasDxYear").equals(resultList.get(i)
     .getSurvivalTimeDxYear());
     boolean sameDxMonth = patient.get(i).get("sasDxMonth") == null ? resultList.get(i).getSurvivalTimeDxMonth() == null : patient.get(i).get("sasDxMonth").equals(resultList.get(i)
     .getSurvivalTimeDxMonth());
     boolean sameDxDay = patient.get(i).get("sasDxDay") == null ? resultList.get(i).getSurvivalTimeDxDay() == null : patient.get(i).get("sasDxDay").equals(resultList.get(i)
     .getSurvivalTimeDxDay());
     boolean sameDolcYear = patient.get(i).get("sasDolcYear") == null ? resultList.get(i).getSurvivalTimeDolcYear() == null : patient.get(i).get("sasDolcYear").equals(resultList.get(i)
     .getSurvivalTimeDolcYear());
     boolean sameDolcMonth = patient.get(i).get("sasDolcMonth") == null ? resultList.get(i).getSurvivalTimeDolcMonth() == null : patient.get(i).get("sasDolcMonth").equals(resultList.get(i)
     .getSurvivalTimeDolcMonth());
     boolean sameDolcDay = patient.get(i).get("sasDolcDay") == null ? resultList.get(i).getSurvivalTimeDolcDay() == null : patient.get(i).get("sasDolcDay").equals(resultList.get(i)
     .getSurvivalTimeDolcDay());
     boolean sameDolcYearPres = patient.get(i).get("sasDolcYearPres") == null ? resultList.get(i).getSurvivalTimeDolcYearPresumedAlive() == null : patient.get(i).get("sasDolcYearPres").equals(
     resultList.get(i).getSurvivalTimeDolcYearPresumedAlive());
     boolean sameDolcMonthPres = patient.get(i).get("sasDolcMonthPres") == null ? resultList.get(i).getSurvivalTimeDolcMonthPresumedAlive() == null : patient.get(i).get("sasDolcMonthPres")
     .equals(resultList.get(i).getSurvivalTimeDolcMonthPresumedAlive());
     boolean sameDolcDayPres = patient.get(i).get("sasDolcDayPres") == null ? resultList.get(i).getSurvivalTimeDolcDayPresumedAlive() == null : patient.get(i).get("sasDolcDayPres").equals(
     resultList.get(i).getSurvivalTimeDolcDayPresumedAlive());
     same &= sameSurv && sameSurvFg && sameSurvPA && sameSurvFgPA && sameDxYear && sameDxMonth && sameDxDay && sameDolcYear && sameDolcMonth && sameDolcDay & sameDolcYearPres &&
     sameDolcMonthPres && sameDolcDayPres;
     }
     if (!same) {
     System.out.println("Patient ID # " + patient.get(0).get("patientIdNumber") + ":");
     for (int i = 0; i < patient.size(); i++) {
     StringBuilder buf = new StringBuilder();
     buf.append("   line ").append(lineNumber).append(" (").append(patient.get(i).get("patientIdNumber")).append(") - ");
     buf.append("seq: ").append(StringUtils.leftPad(patient.get(i).get(SurvivalTimeUtils.PROP_SEQUENCE_NUMBER), 2, "0", true));
     String year = patient.get(i).get(SurvivalTimeUtils.PROP_DX_YEAR);
     String month = patient.get(i).get(SurvivalTimeUtils.PROP_DX_MONTH);
     String day = patient.get(i).get(SurvivalTimeUtils.PROP_DX_DAY);
     buf.append("; ORGDX: ").append(year).append("-").append(StringUtils.leftPad(String.valueOf(month), 2, "0", true)).append("-").append(StringUtils.leftPad(String.valueOf(day), 2, "0", true));
     year = patient.get(i).get(SurvivalTimeUtils.PROP_DOLC_YEAR);
     month = patient.get(i).get(SurvivalTimeUtils.PROP_DOLC_MONTH);
     day = patient.get(i).get(SurvivalTimeUtils.PROP_DOLC_DAY);
     buf.append("; ORGDOLC: ").append(year).append("-").append(month).append("-").append(day);
     year = patient.get(i).get(SurvivalTimeUtils.PROP_BIRTH_YEAR);
     month = patient.get(i).get(SurvivalTimeUtils.PROP_BIRTH_MONTH);
     day = patient.get(i).get(SurvivalTimeUtils.PROP_BIRTH_DAY);
     buf.append("; ORGBIRTH: ").append(year).append("-").append(month).append("-").append(day);
     year = patient.get(i).get("sasDxYear");
     month = patient.get(i).get("sasDxMonth");
     day = patient.get(i).get("sasDxDay");
     buf.append("; SASDX: ").append(year).append("-").append(StringUtils.leftPad(String.valueOf(month), 2, "0", true)).append("-").append(StringUtils.leftPad(String.valueOf(day), 2, "0", true));
     year = patient.get(i).get("sasDolcYear");
     month = patient.get(i).get("sasDolcMonth");
     day = patient.get(i).get("sasDolcDay");
     buf.append("; SASDOLC: ").append(year).append("-").append(month).append("-").append(day);
     year = patient.get(i).get("sasDolcYearPres");
     month = patient.get(i).get("sasDolcMonthPres");
     day = patient.get(i).get("sasDolcDayPres");
     buf.append("; SASDOLCPRES: ").append(year).append("-").append(month).append("-").append(day);
     year = resultList.get(i).getSurvivalTimeDxYear();
     month = resultList.get(i).getSurvivalTimeDxMonth();
     day = resultList.get(i).getSurvivalTimeDxDay();
     buf.append("; JAVADX: ").append(year).append("-").append(StringUtils.leftPad(String.valueOf(month), 2, "0", true)).append("-").append(StringUtils.leftPad(String.valueOf(day), 2, "0", true));
     year = resultList.get(i).getSurvivalTimeDolcYear();
     month = resultList.get(i).getSurvivalTimeDolcMonth();
     day = resultList.get(i).getSurvivalTimeDolcDay();
     buf.append("; JAVADOLC: ").append(year).append("-").append(month).append("-").append(day);
     year = resultList.get(i).getSurvivalTimeDolcYearPresumedAlive();
     month = resultList.get(i).getSurvivalTimeDolcMonthPresumedAlive();
     day = resultList.get(i).getSurvivalTimeDolcDayPresumedAlive();
     buf.append("; JAVADOLCPRES: ").append(year).append("-").append(month).append("-").append(day);
     buf.append("; vs: ").append(patient.get(i).get("vitalStatus"));
     buf.append("; SASsurvival=").append(patient.get(i).get("sasSurvival")).append("/").append(patient.get(i).get("sasSurvivalFlag"));
     buf.append("; SASsurvivalPA=").append(patient.get(i).get("sasSurvivalPresumedAlive")).append("/").append(patient.get(i).get("sasSurvivalFlagPresumedAlive"));
     buf.append("; JAVAsurvival=").append(resultList.get(i).getSurvivalMonths()).append("/").append(resultList.get(i).getSurvivalMonthsFlag());
     buf.append("; JAVAsurvivalPA=").append(resultList.get(i).getSurvivalMonthsPresumedAlive()).append("/").append(resultList.get(i).getSurvivalMonthsFlagPresumedAlive());
     System.out.println(buf.toString());
     }
     }
     }
     ******************************************************************************************************************/

    //The following code used to create naaccr record from csv file
    // And after running the records against sas, createCsvFromSasOutput converts the naaccr record to csv with sas calculated survival time results

    /****************************************************************************************
     public static void main(String[] args) throws Exception {
     createRecordFromCsv();
     //createCsvFromSasOutput();
     }

     public static void createRecordFromCsv() throws Exception {

     List<Map<String, String>> list = new ArrayList<Map<String, String>>();
     File file = new File("E:\\Project Docs\\survival docs\\survival-fake-tests.csv");

     int count = 0;
     for (String[] row : new CSVReader(new InputStreamReader(SeerUtils.createInputStream(file)), ',', '\"', 1).readAll()) {
     count++;
     if (row.length < 13)
     continue;

     Map<String, String> rec = new HashMap<String, String>();
     rec.put("patientIdNumber", row[0]);
     rec.put(SurvivalTimeUtils.PROP_SEQUENCE_NUMBER, row[1].trim());
     rec.put(SurvivalTimeUtils.PROP_DX_YEAR, row[2].trim());
     rec.put(SurvivalTimeUtils.PROP_DX_MONTH, row[3].trim());
     rec.put(SurvivalTimeUtils.PROP_DX_DAY, row[4].trim());
     rec.put(SurvivalTimeUtils.PROP_DOLC_YEAR, row[5].trim());
     rec.put(SurvivalTimeUtils.PROP_DOLC_MONTH, row[6].trim());
     rec.put(SurvivalTimeUtils.PROP_DOLC_DAY, row[7].trim());
     rec.put(SurvivalTimeUtils.PROP_BIRTH_YEAR, row[8].trim());
     rec.put(SurvivalTimeUtils.PROP_BIRTH_MONTH, row[9].trim());
     rec.put(SurvivalTimeUtils.PROP_BIRTH_DAY, row[10].trim());
     rec.put(SurvivalTimeUtils.PROP_VITAL_STATUS, row[11].trim());
     rec.put(SurvivalTimeUtils.PROP_REPORTING_SOURCE, row[12].trim());
     list.add(rec);
     }
     Layout layout = LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
     layout.writeRecords(new File("H:\\survival\\test1.txt"), list); // all cases (All Records)

     }

     public static void createCsvFromSasOutput() throws Exception {
     File file = new File("H:\\survival\\sas-results.txt");
     File testCsv = new File("E:\\Project Docs\\survival docs\\sas-results.csv");
     CSVWriter writer = new CSVWriter(new FileWriter(testCsv));
     Layout layout = LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
     BufferedReader reader;
     long count = 0;
     try {
     reader = new BufferedReader(new FileReader(file));
     List<String[]> survivalTests = new ArrayList<String[]>();
     survivalTests.add(
     new String[] {"patientIdNumber", "sequenceNumberCentral", "dateOfDiagnosisYear", "dateOfDiagnosisMonth", "dateOfDiagnosisDay", "dateOfLastContactYear", "dateOfLastContactMonth",
     "dateOfLastContactDay", "birthDateYear", "birthDateMonth", "birthDateDay", "vitalStatus", "typeOfReportingSource", "sasSurvival", "sasSurvivalFlag", "sasSurvivalPresumedAlive", "sasSurvivalFlagPresumedAlive", "sasSurvivalDxYear",
     "sasSurvivalDxMonth", "sasSurvivalDxDay", "sasSurvivalDolcYear", "sasSurvivalDolcMonth", "sasSurvivalDolcDay", "sasSurvivalDolcYearPres", "sasSurvivalDolcMonthPres",
     "sasSurvivalDolcDayPres"});
     String line = reader.readLine();
     while (line != null) {
     count++;
     Map<String, String> rec = layout.createRecordFromLine(line);
     String patId = rec.get("patientIdNumber");
     String seq = rec.get(SurvivalTimeUtils.PROP_SEQUENCE_NUMBER);
     String dxYr = rec.get(SurvivalTimeUtils.PROP_DX_YEAR);
     String dxMon = rec.get(SurvivalTimeUtils.PROP_DX_MONTH);
     String dxDay = rec.get(SurvivalTimeUtils.PROP_DX_DAY);
     String dolcYr = rec.get(SurvivalTimeUtils.PROP_DOLC_YEAR);
     String dolcMon = rec.get(SurvivalTimeUtils.PROP_DOLC_MONTH);
     String dolcDay = rec.get(SurvivalTimeUtils.PROP_DOLC_DAY);
     String birthYr = rec.get(SurvivalTimeUtils.PROP_BIRTH_YEAR);
     String birthMon = rec.get(SurvivalTimeUtils.PROP_BIRTH_MONTH);
     String birthDay = rec.get(SurvivalTimeUtils.PROP_BIRTH_DAY);
     String vs = rec.get(SurvivalTimeUtils.PROP_VITAL_STATUS);
     String source = rec.get(SurvivalTimeUtils.PROP_REPORTING_SOURCE); 
     String surv = line.substring(2592, 2596);
     String survFlag = line.substring(2596, 2597);
     String survPre = line.substring(2597, 2601);
     String survFlagPre = line.substring(2601, 2602);
     String modDxYr = line.substring(2602, 2606);
     String modDxMon = line.substring(2606, 2608);
     String modDxDay = line.substring(2608, 2610);
     String modDolcYr = line.substring(2610, 2614);
     String modDolcMon = line.substring(2614, 2616);
     String modDolcDay = line.substring(2616, 2618);
     String modDolcYrPres = line.substring(2618, 2622);
     String modDolcMonPres = line.substring(2622, 2624);
     String modDolcDayPres = line.substring(2624, 2626);
     survivalTests.add(new String[] {patId, seq, dxYr, dxMon, dxDay, dolcYr, dolcMon, dolcDay, birthYr, birthMon, birthDay, vs, surv, source, survFlag, survPre, survFlagPre, modDxYr, modDxMon, modDxDay, modDolcYr, modDolcMon,
     modDolcDay, modDolcYrPres, modDolcMonPres, modDolcDayPres});
     line = reader.readLine();
     }
     writer.writeAll(survivalTests);
     writer.close();
     System.out.println(count);
     }
     catch (IOException e) {
     throw new IllegalStateException(e);
     }
     }
     ******************************************************************************************************/
}
