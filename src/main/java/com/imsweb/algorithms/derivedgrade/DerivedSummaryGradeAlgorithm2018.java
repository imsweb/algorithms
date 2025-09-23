/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.derivedgrade;

import java.util.Collections;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_BEHAV_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_DERIVED_SUMMARY_GRADE_2018;
import static com.imsweb.algorithms.Algorithms.FIELD_GRADE_CLINICAL;
import static com.imsweb.algorithms.Algorithms.FIELD_GRADE_PATHOLOGICAL;
import static com.imsweb.algorithms.Algorithms.FIELD_SCHEMA_ID;
import static com.imsweb.algorithms.derivedgrade.DerivedSummaryGradeUtils.ALG_NAME;
import static com.imsweb.algorithms.derivedgrade.DerivedSummaryGradeUtils.ALG_VERSION_2018;

public class DerivedSummaryGradeAlgorithm2018 extends AbstractAlgorithm {

    public DerivedSummaryGradeAlgorithm2018() {
        super(Algorithms.ALG_SEER_DERIVED_SUMMARY_STAGE_2018, ALG_NAME, ALG_VERSION_2018);

        _url = "https://staging.seer.cancer.gov/";

        _inputFields.add(Algorithms.getField(FIELD_SCHEMA_ID));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));
        _inputFields.add(Algorithms.getField(FIELD_GRADE_CLINICAL));
        _inputFields.add(Algorithms.getField(FIELD_GRADE_PATHOLOGICAL));

        _outputFields.add(Algorithms.getField(FIELD_DERIVED_SUMMARY_GRADE_2018));

        _unknownValues.put(FIELD_DERIVED_SUMMARY_GRADE_2018, Collections.singletonList(DerivedSummaryGradeUtils.UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = Utils.createPatientOutput();

        for (Map<String, Object> inputTumor : Utils.extractTumors(input)) {
            String schemaId = (String)inputTumor.get(FIELD_SCHEMA_ID);
            String beh = (String)inputTumor.get(FIELD_BEHAV_O3);
            String gradeClin = (String)inputTumor.get(FIELD_GRADE_CLINICAL);
            String gradePath = (String)inputTumor.get(FIELD_GRADE_PATHOLOGICAL);
            Utils.addTumorOutput(outputPatient, Collections.singletonMap(FIELD_DERIVED_SUMMARY_GRADE_2018, DerivedSummaryGradeUtils.deriveSummaryGrade(schemaId, beh, gradeClin, gradePath)));
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
