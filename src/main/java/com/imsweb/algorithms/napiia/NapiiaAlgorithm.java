/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.napiia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_COUNTRY_BIRTH;
import static com.imsweb.algorithms.Algorithms.FIELD_NAME_BIRTH_SURNAME;
import static com.imsweb.algorithms.Algorithms.FIELD_NAME_LAST;
import static com.imsweb.algorithms.Algorithms.FIELD_NAPIIA;
import static com.imsweb.algorithms.Algorithms.FIELD_NAPIIA_NEEDS_REVIEW;
import static com.imsweb.algorithms.Algorithms.FIELD_NAPIIA_REVIEW_REASON;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE1;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE2;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE3;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE4;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE5;
import static com.imsweb.algorithms.Algorithms.FIELD_SEX;
import static com.imsweb.algorithms.Algorithms.FIELD_SPAN_HISP_OR;

public class NapiiaAlgorithm extends AbstractAlgorithm {

    public NapiiaAlgorithm() {
        super(Algorithms.ALG_NAPIIA, NapiiaUtils.ALG_NAME, NapiiaUtils.ALG_VERSION);

        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#NHAPIIA";

        _inputFields.add(Algorithms.getField(FIELD_SPAN_HISP_OR));
        _inputFields.add(Algorithms.getField(FIELD_NAME_LAST));
        _inputFields.add(Algorithms.getField(FIELD_NAME_BIRTH_SURNAME));
        _inputFields.add(Algorithms.getField(FIELD_COUNTRY_BIRTH));
        _inputFields.add(Algorithms.getField(FIELD_RACE1));
        _inputFields.add(Algorithms.getField(FIELD_RACE2));
        _inputFields.add(Algorithms.getField(FIELD_RACE3));
        _inputFields.add(Algorithms.getField(FIELD_RACE4));
        _inputFields.add(Algorithms.getField(FIELD_RACE5));
        _inputFields.add(Algorithms.getField(FIELD_SEX));

        _outputFields.add(Algorithms.getField(FIELD_NAPIIA));
        _outputFields.add(Algorithms.getField(FIELD_NAPIIA_NEEDS_REVIEW));
        _outputFields.add(Algorithms.getField(FIELD_NAPIIA_REVIEW_REASON));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        NapiiaInputPatientDto inputDto = new NapiiaInputPatientDto();
        inputDto.setNapiiaInputPatientDtoList(new ArrayList<>());

        Map<String, Object> patientMap = Utils.extractPatient(input);
        for (int i = 0; i < Utils.extractTumors(patientMap, true).size(); i++) {
            NapiiaInputRecordDto dto = new NapiiaInputRecordDto();
            dto.setSpanishHispanicOrigin((String)patientMap.get(FIELD_SPAN_HISP_OR));
            dto.setBirthplaceCountry((String)patientMap.get(FIELD_COUNTRY_BIRTH));
            dto.setSex((String)patientMap.get(FIELD_SEX));
            dto.setRace1((String)patientMap.get(FIELD_RACE1));
            dto.setRace2((String)patientMap.get(FIELD_RACE2));
            dto.setRace3((String)patientMap.get(FIELD_RACE3));
            dto.setRace4((String)patientMap.get(FIELD_RACE4));
            dto.setRace5((String)patientMap.get(FIELD_RACE5));
            dto.setNameLast((String)patientMap.get(FIELD_NAME_LAST));
            dto.setNameBirthSurname((String)patientMap.get(FIELD_NAME_BIRTH_SURNAME));
            inputDto.getNapiiaInputPatientDtoList().add(dto);
        }

        NapiiaResultsDto result = NapiiaUtils.computeNapiia(inputDto);

        Map<String, Object> outputPatient = new HashMap<>();
        outputPatient.put(FIELD_NAPIIA, result.getNapiiaValue());
        outputPatient.put(FIELD_NAPIIA_NEEDS_REVIEW, Boolean.TRUE.equals(result.getNeedsHumanReview()) ? "1" : "0");
        outputPatient.put(FIELD_NAPIIA_REVIEW_REASON, result.getReasonForReview());

        return AlgorithmOutput.of(outputPatient);

    }
}
