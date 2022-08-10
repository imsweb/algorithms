/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_IHS_PRCDA;
import static com.imsweb.algorithms.Algorithms.FIELD_IHS_PRCDA_2017;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_UNKNOWN;

public class PrcdaAlgorithm extends AbstractAlgorithm {

    public PrcdaAlgorithm() {
        super(Algorithms.ALG_PRCDA, PrcdaUtils.ALG_NAME, PrcdaUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));

        _outputFields.add(Algorithms.getField(FIELD_IHS_PRCDA));
        _outputFields.add(Algorithms.getField(FIELD_IHS_PRCDA_2017));

        _unknownValues.put(FIELD_IHS_PRCDA, Collections.singletonList(PRCDA_UNKNOWN));
        _unknownValues.put(FIELD_IHS_PRCDA_2017, Collections.singletonList(PRCDA_UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            PrcdaInputDto inputDto = new PrcdaInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setAddressAtDxCounty((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));

            PrcdaOutputDto outputDto = PrcdaUtils.computePrcda(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_IHS_PRCDA, outputDto.getPrcda());
            outputTumor.put(FIELD_IHS_PRCDA_2017, outputDto.getPrcda2017());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
