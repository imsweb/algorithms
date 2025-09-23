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
import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_RUCA_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_RUCA_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_RUCA_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_RURAL_CONT_1993;
import static com.imsweb.algorithms.Algorithms.FIELD_RURAL_CONT_2003;
import static com.imsweb.algorithms.Algorithms.FIELD_RURAL_CONT_2013;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_URIC_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_URIC_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_URIC_2020;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_96;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_97;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_98;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_99;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RUCA_VAL_UNK_A;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RUCA_VAL_UNK_D;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URIC_VAL_UNK_A;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URIC_VAL_UNK_D;

public class RuralUrbanAlgorithm extends AbstractAlgorithm {

    public RuralUrbanAlgorithm() {
        super(Algorithms.ALG_RURAL_URBAN, RuralUrbanUtils.ALG_NAME, RuralUrbanUtils.ALG_VERSION);

        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#RURAL";

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2000));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2020));

        // RUCA
        _outputFields.add(Algorithms.getField(FIELD_RUCA_2000));
        _outputFields.add(Algorithms.getField(FIELD_RUCA_2010));
        _outputFields.add(Algorithms.getField(FIELD_RUCA_2020));
        _unknownValues.put(FIELD_RUCA_2000, Arrays.asList(RUCA_VAL_UNK_A, RUCA_VAL_UNK_D));
        _unknownValues.put(FIELD_RUCA_2010, Arrays.asList(RUCA_VAL_UNK_A, RUCA_VAL_UNK_D));
        _unknownValues.put(FIELD_RUCA_2020, Arrays.asList(RUCA_VAL_UNK_A, RUCA_VAL_UNK_D));

        // URIC
        _outputFields.add(Algorithms.getField(FIELD_URIC_2000));
        _outputFields.add(Algorithms.getField(FIELD_URIC_2010));
        _outputFields.add(Algorithms.getField(FIELD_URIC_2020));
        _unknownValues.put(FIELD_URIC_2000, Arrays.asList(URIC_VAL_UNK_A, URIC_VAL_UNK_D));
        _unknownValues.put(FIELD_URIC_2010, Arrays.asList(URIC_VAL_UNK_A, URIC_VAL_UNK_D));
        _unknownValues.put(FIELD_URIC_2020, Arrays.asList(URIC_VAL_UNK_A, URIC_VAL_UNK_D));

        // Continuum
        _outputFields.add(Algorithms.getField(FIELD_RURAL_CONT_1993));
        _outputFields.add(Algorithms.getField(FIELD_RURAL_CONT_2003));
        _outputFields.add(Algorithms.getField(FIELD_RURAL_CONT_2013));
        _unknownValues.put(FIELD_RURAL_CONT_1993, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
        _unknownValues.put(FIELD_RURAL_CONT_2003, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
        _unknownValues.put(FIELD_RURAL_CONT_2013, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        for (Map<String, Object> inputTumor : Utils.extractTumors(input)) {
            StateCountyTractInputDto inputDto = createStateCountyTractInputDto(inputTumor);

            Map<String, Object> outputTumor = new HashMap<>();

            // RUCA
            RuralUrbanOutputDto rucaOutputDto = RuralUrbanUtils.computeRuralUrbanCommutingArea(inputDto);
            outputTumor.put(FIELD_RUCA_2000, rucaOutputDto.getRuralUrbanCommutingArea2000());
            outputTumor.put(FIELD_RUCA_2010, rucaOutputDto.getRuralUrbanCommutingArea2010());
            outputTumor.put(FIELD_RUCA_2020, rucaOutputDto.getRuralUrbanCommutingArea2020());

            // URIC
            RuralUrbanOutputDto uricOutputDto = RuralUrbanUtils.computeUrbanRuralIndicatorCode(inputDto);
            outputTumor.put(FIELD_URIC_2000, uricOutputDto.getUrbanRuralIndicatorCode2000());
            outputTumor.put(FIELD_URIC_2010, uricOutputDto.getUrbanRuralIndicatorCode2010());
            outputTumor.put(FIELD_URIC_2020, uricOutputDto.getUrbanRuralIndicatorCode2020());

            // Continuum
            RuralUrbanOutputDto continuumOutputDto = RuralUrbanUtils.computeRuralUrbanContinuum(inputDto);
            outputTumor.put(FIELD_RURAL_CONT_1993, continuumOutputDto.getRuralUrbanContinuum1993());
            outputTumor.put(FIELD_RURAL_CONT_2003, continuumOutputDto.getRuralUrbanContinuum2003());
            outputTumor.put(FIELD_RURAL_CONT_2013, continuumOutputDto.getRuralUrbanContinuum2013());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputTumors);

    }
}
