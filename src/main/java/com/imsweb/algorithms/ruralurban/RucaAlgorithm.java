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
import static com.imsweb.algorithms.Algorithms.FIELD_RUCA_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_RUCA_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RUCA_VAL_UNK_A;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RUCA_VAL_UNK_D;

public class RucaAlgorithm extends AbstractAlgorithm {

    public RucaAlgorithm() {
        super(Algorithms.ALG_RUCA, RuralUrbanUtils.ALG_NAME + " - RUCA", RuralUrbanUtils.ALG_VERSION, RuralUrbanUtils.ALG_INFO);

        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#RURAL";

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2000));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_RUCA_2000));
        _outputFields.add(Algorithms.getField(FIELD_RUCA_2010));

        _unknownValues.put(FIELD_RUCA_2000, Arrays.asList(RUCA_VAL_UNK_A, RUCA_VAL_UNK_D));
        _unknownValues.put(FIELD_RUCA_2010, Arrays.asList(RUCA_VAL_UNK_A, RUCA_VAL_UNK_D));
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

            RuralUrbanOutputDto outputDto = RuralUrbanUtils.computeRuralUrbanCommutingArea(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_RUCA_2000, outputDto.getRuralUrbanCommutingArea2000());
            outputTumor.put(FIELD_RUCA_2010, outputDto.getRuralUrbanCommutingArea2010());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);

    }
}
