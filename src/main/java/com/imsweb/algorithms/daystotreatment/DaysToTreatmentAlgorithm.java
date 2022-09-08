/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms.daystotreatment;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_DATE_INITIAL_RX_SEER;
import static com.imsweb.algorithms.Algorithms.FIELD_DAYS_TO_TREATMENT;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

public class DaysToTreatmentAlgorithm extends AbstractAlgorithm {

    public DaysToTreatmentAlgorithm() {
        super(Algorithms.ALG_DAYS_TO_TREATMENT, "Days from Diagnosis to Treatment", "Version 1.0 released in September 2022");

        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_DATE_INITIAL_RX_SEER));

        _outputFields.add(Algorithms.getField(FIELD_DAYS_TO_TREATMENT));

        _unknownValues.put(FIELD_DAYS_TO_TREATMENT, Collections.singletonList("999"));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_DAYS_TO_TREATMENT, computeDaysToTreatment((String)inputTumor.get(FIELD_DX_DATE), (String)inputTumor.get(FIELD_DATE_INITIAL_RX_SEER)));
            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }

    public static String computeDaysToTreatment(String dxDate, String txDate) {
        if (dxDate == null || dxDate.length() != 8 || !NumberUtils.isDigits(dxDate) || txDate == null || txDate.length() != 8 || !NumberUtils.isDigits(txDate))
            return "999";

        try {
            int dxYear = Integer.parseInt(dxDate.substring(0, 4));
            int dxMonth = Integer.parseInt(dxDate.substring(4, 6));
            int dxDay = Integer.parseInt(dxDate.substring(6, 8));
            LocalDate dateFrom = LocalDate.of(dxYear, dxMonth, dxDay);

            int txYear = Integer.parseInt(txDate.substring(0, 4));
            int txMonth = Integer.parseInt(txDate.substring(4, 6));
            int txDay = Integer.parseInt(txDate.substring(6, 8));
            LocalDate dateTo = LocalDate.of(txYear, txMonth, txDay);

            int result = (int)ChronoUnit.DAYS.between(dateFrom, dateTo);

            if (result < 0)
                result = 0;

            if (result > 998)
                result = 998;

            return StringUtils.leftPad(String.valueOf(result), 3, '0');
        }
        catch (DateTimeException e) {
            return "999";
        }
    }
}
