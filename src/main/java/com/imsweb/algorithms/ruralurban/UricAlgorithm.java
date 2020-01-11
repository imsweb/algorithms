/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.FIELD_URIC_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_URIC_2010;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URIC_VAL_UNK_A;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URIC_VAL_UNK_D;

public class UricAlgorithm extends AbstractAlgorithm {

    public UricAlgorithm() {
        super(Algorithms.ALG_URIC, RuralUrbanUtils.ALG_NAME + " - URIC", RuralUrbanUtils.ALG_VERSION, RuralUrbanUtils.ALG_INFO);

        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#RURAL";

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2000));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_URIC_2000));
        _outputFields.add(Algorithms.getField(FIELD_URIC_2010));

        _unknownValues.put(FIELD_URIC_2000, Arrays.asList(URIC_VAL_UNK_A, URIC_VAL_UNK_D));
        _unknownValues.put(FIELD_URIC_2010, Arrays.asList(URIC_VAL_UNK_A, URIC_VAL_UNK_D));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            RuralUrbanInputDto inputDto = new RuralUrbanInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
            inputDto.setCensusTract2000((String)inputTumor.get(FIELD_CENSUS_2000));
            inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));

            RuralUrbanOutputDto outputDto = RuralUrbanUtils.computeUrbanRuralIndicatorCode(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_URIC_2000, outputDto.getUrbanRuralIndicatorCode2000());
            outputTumor.put(FIELD_URIC_2010, outputDto.getUrbanRuralIndicatorCode2010());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
