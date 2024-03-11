/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iarc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IarcUtilsTest {

    @Test
    public void testAlgorithmInfo() {
        Assert.assertNotNull(IarcUtils.VERSION);
        Assert.assertNotNull(IarcUtils.ALG_NAME);
    }

    @Test
    public void testCalculateIarcMp() {
        Assert.assertNull(IarcUtils.calculateIarcMp(null));
        Assert.assertNull(IarcUtils.calculateIarcMp(Collections.emptyList()));
        List<IarcMpInputRecordDto> patient = new ArrayList<>();
        Assert.assertNull(IarcUtils.calculateIarcMp(patient));
        //Only one tumor
        patient.add(new IarcMpInputRecordDto());
        //behavior is not 3
        Assert.assertEquals(IarcUtils.INSITU, IarcUtils.calculateIarcMp(patient).get(0).getInternationalPrimaryIndicator());

        patient.clear();
        IarcMpInputRecordDto dto = new IarcMpInputRecordDto();
        dto.setBehavior("2");
        patient.add(dto);
        Assert.assertEquals(IarcUtils.INSITU, IarcUtils.calculateIarcMp(patient).get(0).getInternationalPrimaryIndicator());

        //behavior 3
        patient.clear();
        dto = new IarcMpInputRecordDto();
        dto.setBehavior("3");
        patient.add(dto);
        Assert.assertEquals(IarcUtils.PRIMARY, IarcUtils.calculateIarcMp(patient).get(0).getInternationalPrimaryIndicator());
        patient.clear();
        patient.add(new IarcMpInputRecordDto("2017", "01", "05", 1, "C549", "8000", "3"));
        Assert.assertEquals(IarcUtils.PRIMARY, IarcUtils.calculateIarcMp(patient).get(0).getInternationalPrimaryIndicator());

        //behavior is not 3
        patient.clear();
        patient.add(new IarcMpInputRecordDto("2017", "01", "05", 1, "C549", "8000", "2"));
        Assert.assertEquals(IarcUtils.INSITU, IarcUtils.calculateIarcMp(patient).get(0).getInternationalPrimaryIndicator());
        //bladder
        patient.clear();
        patient.add(new IarcMpInputRecordDto("2017", "01", "05", 1, "C679", "8000", "2"));
        Assert.assertEquals(IarcUtils.PRIMARY, IarcUtils.calculateIarcMp(patient).get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, IarcUtils.calculateIarcMp(patient, true).get(0).getInternationalPrimaryIndicator());

        //Kaposi Sarcoma, site doesn't matter, 2nd record is takes because of sequence number
        patient.clear();
        patient.add(new IarcMpInputRecordDto("2001", "99", "01", 1, "C239", "9140", "3"));
        patient.add(new IarcMpInputRecordDto("2001", "99", "30", 0, "C679", "9140", "3"));
        List<IarcMpInputRecordDto> results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY_WITH_DUPLICATE, results.get(1).getInternationalPrimaryIndicator());

        //Hemato, same group, the second one is diagnosed first
        patient.clear();
        patient.add(new IarcMpInputRecordDto("2001", "02", "27", 0, "C090", "9597", "3"));
        patient.add(new IarcMpInputRecordDto("2001", "02", "26", 1, "C339", "9940", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        Assert.assertEquals("9597", results.get(0).getHistology());
        Assert.assertEquals("9940", results.get(1).getHistology());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY_WITH_DUPLICATE, results.get(1).getInternationalPrimaryIndicator());

        //Hemato, different group but one is NOS, the second one is picked as primary because  federal (00-59, 98, 99) considered first than non-federal (60-97)
        patient.clear();
        patient.add(new IarcMpInputRecordDto("2001", "02", "26", 61, "C090", "9656", "3"));
        patient.add(new IarcMpInputRecordDto("2001", "02", "26", 99, "C339", "9975", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        Assert.assertEquals("9656", results.get(0).getHistology());
        //NOS histology is replaced by more specific
        Assert.assertEquals("9656", results.get(1).getHistology());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY_WITH_DUPLICATE, results.get(1).getInternationalPrimaryIndicator());

        //Hemato, different group
        patient.clear();
        //Group 11
        patient.add(new IarcMpInputRecordDto("2001", "02", "26", 61, "C090", "9656", "3"));
        //Group 9
        patient.add(new IarcMpInputRecordDto("2001", "02", "26", 99, "C339", "9597", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());

        //same site, same hist group
        patient.clear();
        //Group 4, site C069
        patient.add(new IarcMpInputRecordDto("2001", "02", "26", 1, "C000", "8030", "3"));
        patient.add(new IarcMpInputRecordDto("2001", "11", "26", 0, "C065", "8671", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(1).getInternationalPrimaryIndicator());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.PRIMARY_WITH_DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(1).getInternationalPrimaryIndicator());

        //same site, different hist group
        patient.clear();
        //Group 4, site C069
        patient.add(new IarcMpInputRecordDto("2001", "02", "26", 1, "C000", "8030", "3"));
        //Group 2, site C069
        patient.add(new IarcMpInputRecordDto("2001", "11", "26", 0, "C065", "8100", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());

        //same site, different hist group but one is NOS
        patient.clear();
        //Group 5 (NOS), site C069
        patient.add(new IarcMpInputRecordDto("9999", "02", "26", 1, "C000", "8050", "3"));
        //Group 2, site C069
        patient.add(new IarcMpInputRecordDto("9999", "11", "26", 0, "C065", "8100", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY_WITH_DUPLICATE, results.get(1).getInternationalPrimaryIndicator());

        //Site group and hist group unknown
        patient.clear();
        patient.add(new IarcMpInputRecordDto("", "88", "27", 0, "XYZ", "9597", "3"));
        patient.add(new IarcMpInputRecordDto("", "66", "dd", 1, "C339", "HIST", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(1).getInternationalPrimaryIndicator());

        // two input, first sequence is blank (used to cause an exception)
        patient.clear();
        patient.add(new IarcMpInputRecordDto("2001", "99", "01", null, "C239", "9140", "3"));
        patient.add(new IarcMpInputRecordDto("2001", "99", "30", 0, "C679", "9140", "3"));
        results = IarcUtils.calculateIarcMp(patient);
        Assert.assertEquals(IarcUtils.PRIMARY, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(1).getInternationalPrimaryIndicator());
        results = IarcUtils.calculateIarcMp(patient, true);
        Assert.assertEquals(IarcUtils.PRIMARY_WITH_DUPLICATE, results.get(0).getInternationalPrimaryIndicator());
        Assert.assertEquals(IarcUtils.DUPLICATE, results.get(1).getInternationalPrimaryIndicator());
    }
}
