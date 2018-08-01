/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iarc;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.junit.Test;

public class IarcUtilsTest {

    @Test
    public void testCalculateIarc() {
        Assert.assertNull(IarcUtils.calculateIarc(null));
        Assert.assertNull(IarcUtils.calculateIarc(Collections.emptyList()));
        List<IarcInputRecordDto> patient = new ArrayList<>();
        Assert.assertNull(IarcUtils.calculateIarc(patient));
        //Only one tumor
        patient.add(new IarcInputRecordDto());
        //behavior is not 3
        Assert.assertEquals(IarcUtils.INSITU, IarcUtils.calculateIarc(patient).get(0).getInternationalPrimaryIndicator());

        patient.clear();
        IarcInputRecordDto dto = new IarcInputRecordDto();
        dto.setBehavior("2");
        patient.add(dto);
        Assert.assertEquals(IarcUtils.INSITU, IarcUtils.calculateIarc(patient).get(0).getInternationalPrimaryIndicator());

        //behavior 3
        patient.clear();
        dto = new IarcInputRecordDto();
        dto.setBehavior("3");
        patient.add(dto);
        Assert.assertEquals(IarcUtils.PRIMARY, IarcUtils.calculateIarc(patient).get(0).getInternationalPrimaryIndicator());
        patient.clear();
        patient.add(new IarcInputRecordDto("2017", "01", "05", 1, "C549", "8000", "3"));
        Assert.assertEquals(IarcUtils.PRIMARY, IarcUtils.calculateIarc(patient).get(0).getInternationalPrimaryIndicator());

        //behavior is not 3
        patient.clear();
        patient.add(new IarcInputRecordDto("2017", "01", "05", 1, "C549", "8000", "2"));
        Assert.assertEquals(IarcUtils.INSITU, IarcUtils.calculateIarc(patient).get(0).getInternationalPrimaryIndicator());
        //bladder
        patient.clear();
        patient.add(new IarcInputRecordDto("2017", "01", "05", 1, "C679", "8000", "2"));
        Assert.assertEquals(IarcUtils.PRIMARY, IarcUtils.calculateIarc(patient).get(0).getInternationalPrimaryIndicator());

        //Kaposi Sarcoma, site doesn't matter, 2nd record is takes because of sequence number
        patient.clear();
        patient.add(new IarcInputRecordDto("2001", "99", "01", 1, "C239", "9140", "3"));
        patient.add(new IarcInputRecordDto("2001", "99", "30", 0, "C679", "9140", "3"));
        List<IarcInputRecordDto> results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());

        //Hemato, same group, the second one is diagnosed first
        patient.clear();
        patient.add(new IarcInputRecordDto("2001", "02", "27", 0, "C090", "9597", "3"));
        patient.add(new IarcInputRecordDto("2001", "02", "26", 1, "C339", "9940", "3"));
        results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        Assert.assertEquals("9597", results.get(0).getHistology());
        Assert.assertEquals("9940", results.get(1).getHistology());

        //Hemato, different group but one is NOS, the second one is picked as primary because  federal (00-59, 98, 99) considered first than non-federal (60-97)
        patient.clear();
        patient.add(new IarcInputRecordDto("2001", "02", "26", 61, "C090", "9656", "3"));
        patient.add(new IarcInputRecordDto("2001", "02", "26", 99, "C339", "9975", "3"));
        results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        Assert.assertEquals("9656", results.get(0).getHistology());
        //NOS histology is replaced by more specific
        Assert.assertEquals("9656", results.get(1).getHistology());

        //Hemato, different group
        patient.clear();
        //Group 11
        patient.add(new IarcInputRecordDto("2001", "02", "26", 61, "C090", "9656", "3"));
        //Group 9
        patient.add(new IarcInputRecordDto("2001", "02", "26", 99, "C339", "9597", "3"));
        results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());

        //same site, same hist group
        patient.clear();
        //Group 4, site C069
        patient.add(new IarcInputRecordDto("2001", "02", "26", 1, "C000", "8030", "3"));
        patient.add(new IarcInputRecordDto("2001", "11", "26", 0, "C065", "8671", "3"));
        results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(1).getInternationalPrimaryIndicator());

        //same site, different hist group
        patient.clear();
        //Group 4, site C069
        patient.add(new IarcInputRecordDto("2001", "02", "26", 1, "C000", "8030", "3"));
        //Group 2, site C069
        patient.add(new IarcInputRecordDto("2001", "11", "26", 0, "C065", "8100", "3"));
        results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());

        //same site, different hist group but one is NOS
        patient.clear();
        //Group 5 (NOS), site C069
        patient.add(new IarcInputRecordDto("9999", "02", "26", 1, "C000", "8050", "3"));
        //Group 2, site C069
        patient.add(new IarcInputRecordDto("9999", "11", "26", 0, "C065", "8100", "3"));
        results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());

        //Site group and hist group unknown
        patient.clear();
        patient.add(new IarcInputRecordDto("", "88", "27", 0, "XYZ", "9597", "3"));
        patient.add(new IarcInputRecordDto("", "66", "dd", 1, "C339", "HIST", "3"));
        results = IarcUtils.calculateIarc(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
    }

    @Test
    public void compareWithSas() throws Exception {
        String dataFile = "iarc/iarc.txt";
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(dataFile)));
        String currentPatIdNum = null;
        List<IarcInputRecordDto> patient = new ArrayList<>();
        List<Integer> sasResults = new ArrayList<>();
        String rec = reader.readLine();
        while (rec != null) {
            IarcInputRecordDto dto = new IarcInputRecordDto();
            dto.setSequenceNumber(NumberUtils.toInt(rec.substring(10, 12)));
            dto.setDateOfDiagnosisMonth(rec.substring(16, 18));
            dto.setDateOfDiagnosisDay(rec.substring(18, 20));
            dto.setDateOfDiagnosisYear(rec.substring(20, 24));
            dto.setSite(rec.substring(24, 28));
            dto.setHistology(rec.substring(28, 32));
            dto.setBehavior(rec.substring(32, 33));

            String patIdNum = rec.substring(2, 10);
            if (currentPatIdNum == null || !currentPatIdNum.equals(patIdNum)) {
                if (!patient.isEmpty())
                    checkPatient(patient, sasResults, currentPatIdNum);
                patient.clear();
                sasResults.clear();
                currentPatIdNum = patIdNum;
            }
            patient.add(dto);
            sasResults.add(NumberUtils.toInt(rec.substring(52, 53)));
            rec = reader.readLine();
        }
        if (!patient.isEmpty())
            checkPatient(patient, sasResults, currentPatIdNum);
        reader.close();
    }

    private void checkPatient(List<IarcInputRecordDto> patient, List<Integer> sasResults, String patId) {
        List<IarcInputRecordDto> results = IarcUtils.calculateIarc(patient);
        boolean same = true;
        for (int i = 0; i < results.size(); i++) {
            same &= results.get(i).getInternationalPrimaryIndicator().equals(sasResults.get(i));
        }
        if (!same)
            Assert.fail("Different result from SAS for patient Id: " + patId);
    }
}
