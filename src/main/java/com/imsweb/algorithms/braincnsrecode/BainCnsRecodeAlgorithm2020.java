/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms.braincnsrecode;

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

import static com.imsweb.algorithms.Algorithms.FIELD_BEHAV_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_SEER_BRAIN_CSN_RECODE_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

public class BainCnsRecodeAlgorithm2020 extends AbstractAlgorithm {

    public BainCnsRecodeAlgorithm2020() {
        super(Algorithms.ALG_SEER_BRAIN_CNS_RECODE, BrainCnsRecodeUtils.ALG_NAME, BrainCnsRecodeUtils.ALG_VERSION_2020, BrainCnsRecodeUtils.ALG_INFO);

        _url = "https://seer.cancer.gov/seerstat/variables/seer/brain_cns-recode/";

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));

        _outputFields.add(Algorithms.getField(FIELD_SEER_BRAIN_CSN_RECODE_2020));

        _unknownValues.put(FIELD_SEER_BRAIN_CSN_RECODE_2020, Collections.singletonList(BrainCnsRecodeUtils.UNKNOWN_2020));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
            String hist = (String)inputTumor.get(FIELD_HIST_O3);
            String beh = (String)inputTumor.get(FIELD_BEHAV_O3);
            outputTumors.add(Collections.singletonMap(FIELD_SEER_BRAIN_CSN_RECODE_2020, BrainCnsRecodeUtils.computeBrainCsnRecode(BrainCnsRecodeUtils.ALG_VERSION_2020, site, hist, beh)));
        }

        return AlgorithmOutput.of(outputPatient);

    }
}
