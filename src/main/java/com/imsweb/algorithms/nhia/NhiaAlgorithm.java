/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.AlgorithmParam;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_COUNTRY_BIRTH;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_IHS;
import static com.imsweb.algorithms.Algorithms.FIELD_NAME_BIRTH_SURNAME;
import static com.imsweb.algorithms.Algorithms.FIELD_NAME_LAST;
import static com.imsweb.algorithms.Algorithms.FIELD_NHIA;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE1;
import static com.imsweb.algorithms.Algorithms.FIELD_SEX_ASSIGNED_AT_BIRTH;
import static com.imsweb.algorithms.Algorithms.FIELD_SPAN_HISP_OR;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.PARAM_NHIA_OPTION;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_ALL_CASES;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_SEVEN_ONLY;

public class NhiaAlgorithm extends AbstractAlgorithm {

    public NhiaAlgorithm() {
        super(Algorithms.ALG_NHIA, NhiaUtils.ALG_NAME, NhiaUtils.ALG_VERSION);

        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#NHAPIIA";

        _params.add(AlgorithmParam.of(PARAM_NHIA_OPTION, "NHIA Option", String.class, Arrays.asList(NHIA_OPTION_ALL_CASES, NHIA_OPTION_SEVEN_AND_NINE, NHIA_OPTION_SEVEN_ONLY)));

        _inputFields.add(Algorithms.getField(FIELD_SPAN_HISP_OR));
        _inputFields.add(Algorithms.getField(FIELD_NAME_LAST));
        _inputFields.add(Algorithms.getField(FIELD_NAME_BIRTH_SURNAME));
        _inputFields.add(Algorithms.getField(FIELD_COUNTRY_BIRTH));
        _inputFields.add(Algorithms.getField(FIELD_RACE1));
        _inputFields.add(Algorithms.getField(FIELD_SEX_ASSIGNED_AT_BIRTH));
        _inputFields.add(Algorithms.getField(FIELD_IHS));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));

        _outputFields.add(Algorithms.getField(FIELD_NHIA));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> patientMap = Utils.extractPatient(input);

        NhiaInputPatientDto inputPatient = new NhiaInputPatientDto();
        inputPatient.setSpanishHispanicOrigin((String)patientMap.get(FIELD_SPAN_HISP_OR));
        inputPatient.setBirthplaceCountry((String)patientMap.get(FIELD_COUNTRY_BIRTH));
        inputPatient.setSexAssignedAtBirth((String)patientMap.get(FIELD_SEX_ASSIGNED_AT_BIRTH));
        inputPatient.setRace1((String)patientMap.get(FIELD_RACE1));
        inputPatient.setIhs((String)patientMap.get(FIELD_IHS));
        inputPatient.setNameLast((String)patientMap.get(FIELD_NAME_LAST));
        inputPatient.setNameBirthSurname((String)patientMap.get(FIELD_NAME_BIRTH_SURNAME));

        // NHIA uses a couple of tumor-level fields...
        List<NhiaInputTumorDto> inputTumors = new ArrayList<>();
        for (Map<String, Object> tumorMap : Utils.extractTumors(patientMap)) {
            NhiaInputTumorDto inputTumor = new NhiaInputTumorDto();
            inputTumor.setCountyAtDxAnalysis((String)tumorMap.get(FIELD_COUNTY_AT_DX_ANALYSIS));
            inputTumor.setStateAtDx((String)tumorMap.get(FIELD_STATE_DX));
            inputTumors.add(inputTumor);
        }
        inputPatient.setTumors(inputTumors);

        NhiaResultsDto result = NhiaUtils.computeNhia(inputPatient, (String)input.getParameter(PARAM_NHIA_OPTION));

        Map<String, Object> outputPatient = new HashMap<>();
        outputPatient.put(FIELD_NHIA, result.getNhia());

        return AlgorithmOutput.of(outputPatient);
    }
}
