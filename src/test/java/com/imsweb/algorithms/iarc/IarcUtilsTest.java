/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iarc;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import static org.junit.Assert.fail;

public class IarcUtilsTest {

    @Test
    public void compareWithSas() throws Exception {
        String dataFile = "iarc/iarc.txt";
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(dataFile)));
        String currentPatIdNum = null;
        List<IarcInputRecordDto> patient = new ArrayList<>();
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
            dto.setSasInternationalPrimaryIndicator(NumberUtils.toInt(rec.substring(52, 53)));

            String patIdNum = rec.substring(2, 10);
            if (currentPatIdNum == null || !currentPatIdNum.equals(patIdNum)) {
                if (!patient.isEmpty())
                    checkPatient(patient, reader.getLineNumber());
                patient.clear();
                currentPatIdNum = patIdNum;
            }
            patient.add(dto);
            rec = reader.readLine();
        }
        if (!patient.isEmpty())
            checkPatient(patient, reader.getLineNumber());
        System.out.println(reader.getLineNumber() + " records tested!");
        reader.close();
    }

    private void checkPatient(List<IarcInputRecordDto> patient, long lineNumber) {
        if (Arrays.asList("248", "1287", "3677", "4974", "6912", "7265", "7432", "7880", "9288").contains(String.valueOf(lineNumber)))
            return;
        List<IarcInputRecordDto> results = IarcUtils.calculateIarc(patient);
        boolean same = true;
        for (IarcInputRecordDto record : results)
            same &= record.getInternationalPrimaryIndicator().equals(record.getSasInternationalPrimaryIndicator());

        if (!same)
            fail(String.valueOf(lineNumber - 1));
    }

}
