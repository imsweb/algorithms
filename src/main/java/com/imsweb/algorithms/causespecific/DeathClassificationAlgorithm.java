/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.AlgorithmParam;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_COD;
import static com.imsweb.algorithms.Algorithms.FIELD_DOLC;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_ICD_REV_NUM;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_SEER_COD_CLASS;
import static com.imsweb.algorithms.Algorithms.FIELD_SEER_COD_OTHER;
import static com.imsweb.algorithms.Algorithms.FIELD_SEQ_NUM_CTRL;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.PARAM_SEER_COD_CLASS_CUTOFF_YEAR;

public class DeathClassificationAlgorithm extends AbstractAlgorithm {

    public DeathClassificationAlgorithm() {
        super(Algorithms.ALG_DEATH_CLASSIFICATION, CauseSpecificUtils.ALG_NAME, CauseSpecificUtils.ALG_VERSION);

        _url = "https://seer.cancer.gov/causespecific/";

        _params.add(AlgorithmParam.of(PARAM_SEER_COD_CLASS_CUTOFF_YEAR, "Cutoff Year", Integer.class));

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_SEQ_NUM_CTRL));
        _inputFields.add(Algorithms.getField(FIELD_ICD_REV_NUM));
        _inputFields.add(Algorithms.getField(FIELD_COD));
        _inputFields.add(Algorithms.getField(FIELD_DOLC));

        _outputFields.add(Algorithms.getField(FIELD_SEER_COD_CLASS));
        _outputFields.add(Algorithms.getField(FIELD_SEER_COD_OTHER));

        _unknownValues.put(FIELD_SEER_COD_CLASS, Arrays.asList(CauseSpecificUtils.MISSING_UNKNOWN_DEATH_OF_CODE, CauseSpecificUtils.SEQUENCE_NOT_APPLICABLE));
        _unknownValues.put(FIELD_SEER_COD_OTHER, Arrays.asList(CauseSpecificUtils.MISSING_UNKNOWN_DEATH_OF_CODE, CauseSpecificUtils.SEQUENCE_NOT_APPLICABLE));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        Integer cutoffYear = (Integer)input.getParameter(PARAM_SEER_COD_CLASS_CUTOFF_YEAR);
        if (cutoffYear == null)
            cutoffYear = Calendar.getInstance().get(Calendar.YEAR);

        Map<String, Object> inputPatient = Utils.extractPatient(input);
        for (Map<String, Object> inputTumor : Utils.extractTumors(inputPatient)) {
            CauseSpecificInputDto inputDto = new CauseSpecificInputDto();
            inputDto.setPrimarySite((String)inputTumor.get(FIELD_PRIMARY_SITE));
            inputDto.setHistologyIcdO3((String)inputTumor.get(FIELD_HIST_O3));
            inputDto.setSequenceNumberCentral((String)inputTumor.get(FIELD_SEQ_NUM_CTRL));
            inputDto.setIcdRevisionNumber((String)inputPatient.get(FIELD_ICD_REV_NUM));
            inputDto.setCauseOfDeath((String)inputPatient.get(FIELD_COD));
            inputDto.setDateOfLastContactYear(Utils.extractYear((String)inputPatient.get(FIELD_DOLC)));

            CauseSpecificResultDto resultDto = CauseSpecificUtils.computeCauseSpecific(inputDto, cutoffYear);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_SEER_COD_CLASS, resultDto.getCauseSpecificDeathClassification());
            outputTumor.put(FIELD_SEER_COD_OTHER, resultDto.getCauseOtherDeathClassification());
            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
