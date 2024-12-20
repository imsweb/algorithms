/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

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
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, 2012).getCauseSpecificDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(input, 2012).getCauseOtherDeathClassification());

        //All other cases are covered in the testCsvFile() method.
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

    //     The following methods are used to compare the results against sas.
    
    /*
    public static void main(String[] args) throws Exception {
        //createNaaccrRecordFromCsv();
        testSasOutput();
    }

    //This method is used to compare java implementation against sas. Run the sas code and use the output file in the following code.
    //The sas code don't use vital status and dolc year.
    
    private static void testSasOutput() throws Exception {

        File sasOutput = new File("H:\\Cause-specific\\sas_results.txt");
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(SeerUtils.createInputStream(sasOutput)));
        Layout layout = LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
        String line = reader.readLine();
        long totalCases = 0;
        long failures = 0;
        while (line != null) {
            totalCases++;
            Map<String, String> rec = layout.createRecordFromLine(line);
            String causeSpecific = CauseSpecificUtils.computeCauseSpecific(rec).getCauseSpecificDeathClassification();
            String sasCauseSpecific = line.substring(2938, 2939);
            if (!causeSpecific.equals(sasCauseSpecific)) {
                failures++;
                String site = rec.get("primarySite");
                String hist = rec.get("histologyIcdO3");
                String seq = rec.get("sequenceNumberCentral");
                String icd = rec.get("icdRevisionNumber");
                String cod = rec.get("causeOfDeath");                
                System.out.println(seq + ", " + icd + ", " + cod + ", " + site + ", " + hist + ", " + sasCauseSpecific + ", " + causeSpecific);
            }
            line = reader.readLine();
        }
        reader.close();
        System.out.println(failures + " cases fail from " + totalCases);
    }

    //This method is used to create naaccr record file from csv which will be used as an input for sas, after running sas with this input use the above method to compare the result.
    private static void createNaaccrRecordFromCsv() throws Exception {

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (String[] row : new CSVReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("tools-test-data/testCauseSpecific.csv")), ',', '\"', 1).readAll()) {
            Map<String, String> rec = new HashMap<String, String>();
            rec.put(CauseSpecificUtils.PROP_SEQ_NUM_CENTRAL, row[0]);
            rec.put(CauseSpecificUtils.PROP_ICD_REVISION_NUM, row[1]);
            rec.put(CauseSpecificUtils.PROP_COD, row[2]);
            rec.put(CauseSpecificUtils.PROP_PRIMARY_SITE, row[3]);
            rec.put(CauseSpecificUtils.PROP_HISTOLOGY_ICDO3, row[4]);
            rec.put(CauseSpecificUtils.PROP_DOLC_YEAR, row[5]);
            rec.put(CauseSpecificUtils.PROP_VITAL_STATUS, row[6]);
            list.add(rec);
        }

        Layout layout = LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
        layout.writeRecords(new File("H:\\Cause-specific\\sas-input.txt"), list);
    }
    */
}