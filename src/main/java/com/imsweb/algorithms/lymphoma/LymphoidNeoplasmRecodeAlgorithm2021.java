/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.lymphoma;

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

import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_SEER_LYMPH_NEO_RECODE_2021;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.lymphoma.LymphoidNeoplasmRecodeUtils.ALG_NAME;
import static com.imsweb.algorithms.lymphoma.LymphoidNeoplasmRecodeUtils.ALG_VERSION_2021;

public class LymphoidNeoplasmRecodeAlgorithm2021 extends AbstractAlgorithm {

    public LymphoidNeoplasmRecodeAlgorithm2021() {
        super(Algorithms.ALG_SEER_LYMPH_NEO_RECODE_2021, ALG_NAME, ALG_VERSION_2021);

        _url = "https://seer.cancer.gov/lymphomarecode/lymphoma-2021.html";

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));

        _outputFields.add(Algorithms.getField(FIELD_SEER_LYMPH_NEO_RECODE_2021));

        _unknownValues.put(FIELD_SEER_LYMPH_NEO_RECODE_2021, Collections.singletonList(LymphoidNeoplasmRecodeUtils.UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
            String hist = (String)inputTumor.get(FIELD_HIST_O3);
            outputTumors.add(Collections.singletonMap(FIELD_SEER_LYMPH_NEO_RECODE_2021, LymphoidNeoplasmRecodeUtils.calculateSiteRecode(ALG_VERSION_2021, site, hist)));
        }

        return AlgorithmOutput.of(outputPatient);

    }

}
