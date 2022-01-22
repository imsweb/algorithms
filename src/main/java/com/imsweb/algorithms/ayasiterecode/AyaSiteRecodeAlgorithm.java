/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ayasiterecode;

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

import static com.imsweb.algorithms.Algorithms.FIELD_AYA_SITE_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_BEHAV_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.ayasiterecode.AyaSiteRecodeUtils.ALG_VERSION_2008;

public class AyaSiteRecodeAlgorithm extends AbstractAlgorithm {

    public AyaSiteRecodeAlgorithm() {
        super(Algorithms.ALG_AYA_SITE_RECODE, AyaSiteRecodeUtils.ALG_NAME, AbstractAlgorithm.MULTIPLE_VERSIONS, AyaSiteRecodeUtils.ALG_INFO);

        _url = "https://seer.cancer.gov/ayarecode/aya-who2008.html";

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));

        _outputFields.add(Algorithms.getField(FIELD_AYA_SITE_RECODE));

        _unknownValues.put(FIELD_AYA_SITE_RECODE, Collections.singletonList(AyaSiteRecodeUtils.AYA_SITE_RECODE_UNKNOWN));
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
            outputTumors.add(Collections.singletonMap(FIELD_AYA_SITE_RECODE, AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2008, site, hist, beh)));
        }

        return AlgorithmOutput.of(outputPatient);

    }

}
