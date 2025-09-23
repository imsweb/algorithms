/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ayasiterecode;

import java.util.Collections;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_AYA_SITE_RECODE_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_BEHAV_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.ayasiterecode.AyaSiteRecodeUtils.ALG_NAME;
import static com.imsweb.algorithms.ayasiterecode.AyaSiteRecodeUtils.ALG_VERSION_2020;

public class AyaSiteRecodeAlgorithm2020 extends AbstractAlgorithm {

    public AyaSiteRecodeAlgorithm2020() {
        super(Algorithms.ALG_AYA_SITE_RECODE_2020, ALG_NAME, ALG_VERSION_2020);

        _url = "https://seer.cancer.gov/ayarecode/aya-2020.html";

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));

        _outputFields.add(Algorithms.getField(FIELD_AYA_SITE_RECODE_2020));

        _unknownValues.put(FIELD_AYA_SITE_RECODE_2020, Collections.singletonList(AyaSiteRecodeUtils.AYA_SITE_RECODE_UNKNOWN_2020));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = Utils.createPatientOutput();

        for (Map<String, Object> inputTumor : Utils.extractTumors(input)) {
            String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
            String hist = (String)inputTumor.get(FIELD_HIST_O3);
            String beh = (String)inputTumor.get(FIELD_BEHAV_O3);
            Utils.addTumorOutput(outputPatient, Collections.singletonMap(FIELD_AYA_SITE_RECODE_2020, AyaSiteRecodeUtils.calculateSiteRecode(ALG_VERSION_2020, site, hist, beh)));
        }

        return AlgorithmOutput.of(outputPatient);

    }

}
