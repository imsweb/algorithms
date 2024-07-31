/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

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

import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TRACT_EST_CONGRESS_DIST;
import static com.imsweb.algorithms.tractestcongressdist.TractEstCongressDistUtils.TRACT_EST_CONGRESS_DIST_UNK_A;
import static com.imsweb.algorithms.tractestcongressdist.TractEstCongressDistUtils.TRACT_EST_CONGRESS_DIST_UNK_D;

public class TractEstCongressDistAlgorithm extends AbstractAlgorithm {

    public TractEstCongressDistAlgorithm() {
        super(Algorithms.ALG_TRACT_EST_CONGRESS_DIST, TractEstCongressDistUtils.ALG_NAME, TractEstCongressDistUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_TRACT_EST_CONGRESS_DIST));

        _unknownValues.put(FIELD_TRACT_EST_CONGRESS_DIST, Arrays.asList(TRACT_EST_CONGRESS_DIST_UNK_A, TRACT_EST_CONGRESS_DIST_UNK_D));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {

        List<Map<String, Object>> outputTumors = new ArrayList<>();
        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            TractEstCongressDistOutputDto outputDto = TractEstCongressDistUtils.computeTractEstCongressDist(createStateCountyTractInputDto(inputTumor));

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_TRACT_EST_CONGRESS_DIST, outputDto.getTractEstCongressDist());
            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputTumors);
    }
}
