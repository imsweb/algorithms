/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.imsweb.layout.Field;
import com.imsweb.layout.Layout;
import com.imsweb.layout.LayoutFactory;

public class CauseSpecificUtilsTest {

    //test for checking that every constant corresponds to an existing property in the default NAACCR layout
    @Test
    public void testPropertyConstants() {
        Layout layout = LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_15);
        Field field = layout.getFieldByName(CauseSpecificUtils.PROP_PRIMARY_SITE);
        Assert.assertEquals(CauseSpecificUtils.PROP_PRIMARY_SITE, field.getName());
        field = layout.getFieldByName(CauseSpecificUtils.PROP_SEQ_NUM_CENTRAL);
        Assert.assertEquals(CauseSpecificUtils.PROP_SEQ_NUM_CENTRAL, field.getName());
        field = layout.getFieldByName(CauseSpecificUtils.PROP_HISTOLOGY_ICDO3);
        Assert.assertEquals(CauseSpecificUtils.PROP_HISTOLOGY_ICDO3, field.getName());
        field = layout.getFieldByName(CauseSpecificUtils.PROP_DOLC_YEAR);
        Assert.assertEquals(CauseSpecificUtils.PROP_DOLC_YEAR, field.getName());
        field = layout.getFieldByName(CauseSpecificUtils.PROP_COD);
        Assert.assertEquals(CauseSpecificUtils.PROP_COD, field.getName());
        field = layout.getFieldByName(CauseSpecificUtils.PROP_ICD_REVISION_NUM);
        Assert.assertEquals(CauseSpecificUtils.PROP_ICD_REVISION_NUM, field.getName());
        field = layout.getFieldByName(CauseSpecificUtils.PROP_VITAL_STATUS);
        Assert.assertEquals(CauseSpecificUtils.PROP_VITAL_STATUS, field.getName());
    }

    @Test
    public void testComputeCauseSpecific() {
        //test all flavors of methods and dolc case (which is not supported on SAS)
        Map<String, String> record = new HashMap<>();
        record.put(CauseSpecificUtils.PROP_SEQ_NUM_CENTRAL, "00");
        record.put(CauseSpecificUtils.PROP_ICD_REVISION_NUM, "1");
        record.put(CauseSpecificUtils.PROP_DOLC_YEAR, "2013");
        record.put(CauseSpecificUtils.PROP_COD, "C001");
        Assert.assertEquals("1", CauseSpecificUtils.computeCauseSpecific(record).getCauseSpecificDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(record).getCauseOtherDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(record, 2012).getCauseSpecificDeathClassification());
        Assert.assertEquals("0", CauseSpecificUtils.computeCauseSpecific(record, 2012).getCauseOtherDeathClassification());

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
    public void testCsvFile() throws IOException {
        int count = 0;
        for (String[] row : new CSVReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("causespecific/testCauseSpecific.csv"), "US-ASCII"), ',', '\"', 1)
                .readAll()) {
            Map<String, String> rec = new HashMap<>();
            rec.put(CauseSpecificUtils.PROP_SEQ_NUM_CENTRAL, row[0]);
            rec.put(CauseSpecificUtils.PROP_ICD_REVISION_NUM, row[1]);
            rec.put(CauseSpecificUtils.PROP_COD, row[2]);
            rec.put(CauseSpecificUtils.PROP_PRIMARY_SITE, row[3]);
            rec.put(CauseSpecificUtils.PROP_HISTOLOGY_ICDO3, row[4]);
            rec.put(CauseSpecificUtils.PROP_DOLC_YEAR, row[5]);
            rec.put(CauseSpecificUtils.PROP_VITAL_STATUS, row[6]);
            String causeSpecificExpected = row[7];
            String causeOtherExpected = row[8];
            String causeSpecificCalculated = CauseSpecificUtils.computeCauseSpecific(rec).getCauseSpecificDeathClassification();
            String causeOtherCalculated = CauseSpecificUtils.computeCauseSpecific(rec).getCauseOtherDeathClassification();
            count++;
            if (!causeSpecificExpected.equals(causeSpecificCalculated) || !causeOtherExpected.equals(causeOtherCalculated)) {
                //System.out.println(SeerSiteRecodeUtils.calculateSiteRecode(row[3], row[4]));
                Assert.fail("Unexpected result for row number " + (count + 1) + " " + Arrays.asList(row) + "\nExpected results: " + causeSpecificExpected + ", " +
                        "" + causeOtherExpected + " But found: " + causeSpecificCalculated + ", " + causeOtherCalculated);
            }
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