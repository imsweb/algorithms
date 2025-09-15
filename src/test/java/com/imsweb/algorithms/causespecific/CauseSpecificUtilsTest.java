/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2023;

public class CauseSpecificUtilsTest {

    @Test
    public void testComputeCauseSpecific() {
        CauseSpecificInputDto input = new CauseSpecificInputDto();
        input.setSequenceNumberCentral("01");
        input.setIcdRevisionNumber("8");
        input.setCauseOfDeath("199 ");
        input.setDateOfLastContactYear("2013");
        Assert.assertEquals("1", CauseSpecificUtils.computeCauseSpecific(input).getCauseSpecificDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input).getCauseOtherDeathClassification());
        Assert.assertEquals("1", CauseSpecificUtils.computeCauseSpecific(input, Calendar.getInstance().get(Calendar.YEAR)).getCauseSpecificDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, Calendar.getInstance().get(Calendar.YEAR)).getCauseOtherDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, 2012).getCauseSpecificDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, 2012).getCauseOtherDeathClassification());
        // other cases are covered in the testCsvFile() method.

        // test optional SEER Site Recode parameter
        input.setSequenceNumberCentral("01");
        input.setIcdRevisionNumber("1");
        input.setCauseOfDeath("B220");
        input.setDateOfLastContactYear("2013");
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, 2012, VERSION_2023).getCauseSpecificDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, 2012, VERSION_2023).getCauseOtherDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, Calendar.getInstance().get(Calendar.YEAR), VERSION_2023).getCauseSpecificDeathClassification());
        Assert.assertEquals("1", CauseSpecificUtils.computeCauseSpecific(input, Calendar.getInstance().get(Calendar.YEAR), VERSION_2023).getCauseOtherDeathClassification());


    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testCsvFile() throws IOException {
        AtomicInteger count = new AtomicInteger();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("causespecific/testCauseSpecific.csv");
             CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
            reader.stream().forEach(line -> {
                CauseSpecificInputDto input = new CauseSpecificInputDto();
                input.setSequenceNumberCentral(line.getField(0));
                input.setIcdRevisionNumber(line.getField(1));
                input.setCauseOfDeath(line.getField(2));
                input.setPrimarySite(line.getField(3));
                input.setHistologyIcdO3(line.getField(4));
                input.setDateOfLastContactYear(line.getField(5));

                String causeSpecificExpected = line.getField(7);
                String causeOtherExpected = line.getField(8);

                String causeSpecificCalculated = CauseSpecificUtils.computeCauseSpecific(input).getCauseSpecificDeathClassification();
                String causeOtherCalculated = CauseSpecificUtils.computeCauseSpecific(input).getCauseOtherDeathClassification();
                count.getAndIncrement();
                if (!causeSpecificExpected.equals(causeSpecificCalculated) || !causeOtherExpected.equals(causeOtherCalculated)) {
                    Assert.fail("Unexpected result for row number " + (count.get() + 1) + " " + Arrays.asList(line.getFields().toArray(new String[0])) + "\nExpected results: " + causeSpecificExpected
                            + ", " + causeOtherExpected + " But found: " + causeSpecificCalculated + ", " + causeOtherCalculated);
                }
            });
        }
    }
}