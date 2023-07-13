/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iccc;

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
import static com.imsweb.algorithms.Algorithms.FIELD_ICCC;
import static com.imsweb.algorithms.Algorithms.FIELD_ICCC_EXT;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

public class IcccAlgorithm extends AbstractAlgorithm {

    public IcccAlgorithm() {
        super(Algorithms.ALG_ICCC, IcccRecodeUtils.ALG_NAME, IcccRecodeUtils.VERSION_THIRD_EDITION_IARC_2017);

        _url = "https://seer.cancer.gov/iccc/iccc-iarc-2017.html";

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));

        _outputFields.add(Algorithms.getField(FIELD_ICCC));
        _outputFields.add(Algorithms.getField(FIELD_ICCC_EXT));

        _unknownValues.put(FIELD_ICCC, Collections.singletonList(IcccRecodeUtils.ICCC_UNKNOWN_RECODE));
        _unknownValues.put(FIELD_ICCC_EXT, Collections.singletonList(IcccRecodeUtils.ICCC_UNKNOWN_RECODE));
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

            String icccCode = IcccRecodeUtils.calculateSiteRecode(getVersion(), site, hist, beh, false);
            String icccExtCode = IcccRecodeUtils.calculateSiteRecode(getVersion(), site, hist, beh, true);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_ICCC, icccCode);
            outputTumor.put(FIELD_ICCC_EXT, icccExtCode);
            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);

    }
}
