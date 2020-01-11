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

import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_RURAL_CONT_1993;
import static com.imsweb.algorithms.Algorithms.FIELD_RURAL_CONT_2003;
import static com.imsweb.algorithms.Algorithms.FIELD_RURAL_CONT_2013;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_96;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_97;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_98;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_99;

public class UrbanContinuumAlgorithm extends AbstractAlgorithm {

    public UrbanContinuumAlgorithm() {
        super(Algorithms.ALG_URBAN_CONTINUUM, RuralUrbanUtils.ALG_NAME + " - Urban Continuum", RuralUrbanUtils.ALG_VERSION, RuralUrbanUtils.ALG_INFO);

        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#RURAL";

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));

        _outputFields.add(Algorithms.getField(FIELD_RURAL_CONT_1993));
        _outputFields.add(Algorithms.getField(FIELD_RURAL_CONT_2003));
        _outputFields.add(Algorithms.getField(FIELD_RURAL_CONT_2013));

        _unknownValues.put(FIELD_RURAL_CONT_1993, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
        _unknownValues.put(FIELD_RURAL_CONT_2003, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
        _unknownValues.put(FIELD_RURAL_CONT_2013, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
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

            RuralUrbanOutputDto outputDto = RuralUrbanUtils.computeRuralUrbanContinuum(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_RURAL_CONT_1993, outputDto.getRuralUrbanContinuum1993());
            outputTumor.put(FIELD_RURAL_CONT_2003, outputDto.getRuralUrbanContinuum2003());
            outputTumor.put(FIELD_RURAL_CONT_2013, outputDto.getRuralUrbanContinuum2013());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);

    }
}
