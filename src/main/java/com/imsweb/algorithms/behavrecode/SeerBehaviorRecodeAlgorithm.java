/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.behavrecode;

import java.util.Collections;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_BEHAV_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_SEER_BEHAV_RECODE;

public class SeerBehaviorRecodeAlgorithm extends AbstractAlgorithm {

    public SeerBehaviorRecodeAlgorithm() {
        super(Algorithms.ALG_SEER_BEHAVIOR_RECODE, BehaviorRecodeUtils.ALG_NAME, BehaviorRecodeUtils.ALG_VERSION);

        _url = "https://seer.cancer.gov/behavrecode/";

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));
        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));

        _outputFields.add(Algorithms.getField(FIELD_SEER_BEHAV_RECODE));

        _unknownValues.put(FIELD_SEER_BEHAV_RECODE, Collections.singletonList(BehaviorRecodeUtils.UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = Utils.createPatientOutput();

        for (Map<String, Object> inputTumor : Utils.extractTumors(input)) {
            String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
            String hist = (String)inputTumor.get(FIELD_HIST_O3);
            String beh = (String)inputTumor.get(FIELD_BEHAV_O3);
            String dxYear = Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE));
            Utils.addTumorOutput(outputPatient, Collections.singletonMap(FIELD_SEER_BEHAV_RECODE, BehaviorRecodeUtils.computeBehaviorRecode(site, hist, beh, dxYear)));
        }

        return AlgorithmOutput.of(outputPatient);

    }
}
