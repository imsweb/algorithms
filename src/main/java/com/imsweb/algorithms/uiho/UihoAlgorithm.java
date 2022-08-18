/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.uiho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.StateCountyInputDto;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.FIELD_UIHO;
import static com.imsweb.algorithms.Algorithms.FIELD_UIHO_CITY;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_CITY_UNKNOWN;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_UNKNOWN;

public class UihoAlgorithm extends AbstractAlgorithm {

    public UihoAlgorithm() {
        super(Algorithms.ALG_UIHO, UihoUtils.ALG_NAME, UihoUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));

        _outputFields.add(Algorithms.getField(FIELD_UIHO));
        _outputFields.add(Algorithms.getField(FIELD_UIHO_CITY));

        _unknownValues.put(FIELD_UIHO, Collections.singletonList(UIHO_UNKNOWN));
        _unknownValues.put(FIELD_UIHO_CITY, Collections.singletonList(UIHO_CITY_UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            StateCountyInputDto inputDto = new StateCountyInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));

            UihoOutputDto outputDto = UihoUtils.computeUiho(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_UIHO, outputDto.getUiho());
            outputTumor.put(FIELD_UIHO_CITY, outputDto.getUihoCity());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
