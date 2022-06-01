/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.survival;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.AlgorithmParam;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_DATE_OF_BIRTH;
import static com.imsweb.algorithms.Algorithms.FIELD_DOLC;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_PAT_ID_NUMBER;
import static com.imsweb.algorithms.Algorithms.FIELD_SEQ_NUM_CTRL;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_DATE_ACTIVE_FUP;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_DATE_PRESUMED_ALIVE;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_DX_DATE_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_FLAG_ACTIVE_FUP;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_FLAG_PRESUMED_ALIVE;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_MONTH_ACTIVE_FUP;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_MONTH_PRESUMED_ALIVE;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_REC_NUM_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_SURV_VS_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.FIELD_TYPE_RPT_SRC;
import static com.imsweb.algorithms.Algorithms.FIELD_VS;
import static com.imsweb.algorithms.Algorithms.PARAM_SURV_CUTOFF_YEAR;

public class SurvivalTimeAlgorithm extends AbstractAlgorithm {

    public SurvivalTimeAlgorithm() {
        super(Algorithms.ALG_SURVIVAL_TIME, SurvivalTimeUtils.ALG_NAME, SurvivalTimeUtils.VERSION);

        _url = "https://seer.cancer.gov/survivaltime/";

        _params.add(AlgorithmParam.of(PARAM_SURV_CUTOFF_YEAR, "Cutoff Year", Integer.class));

        _inputFields.add(Algorithms.getField(FIELD_PAT_ID_NUMBER));
        _inputFields.add(Algorithms.getField(FIELD_DATE_OF_BIRTH));
        _inputFields.add(Algorithms.getField(FIELD_DOLC));
        _inputFields.add(Algorithms.getField(FIELD_VS));
        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_SEQ_NUM_CTRL));
        _inputFields.add(Algorithms.getField(FIELD_TYPE_RPT_SRC));

        _outputFields.add(Algorithms.getField(FIELD_SURV_MONTH_ACTIVE_FUP));
        _outputFields.add(Algorithms.getField(FIELD_SURV_FLAG_ACTIVE_FUP));
        _outputFields.add(Algorithms.getField(FIELD_SURV_DATE_ACTIVE_FUP));
        _outputFields.add(Algorithms.getField(FIELD_SURV_MONTH_PRESUMED_ALIVE));
        _outputFields.add(Algorithms.getField(FIELD_SURV_FLAG_PRESUMED_ALIVE));
        _outputFields.add(Algorithms.getField(FIELD_SURV_DATE_PRESUMED_ALIVE));
        _outputFields.add(Algorithms.getField(FIELD_SURV_DX_DATE_RECODE));
        _outputFields.add(Algorithms.getField(FIELD_SURV_VS_RECODE));
        _outputFields.add(Algorithms.getField(FIELD_SURV_REC_NUM_RECODE));

        _unknownValues.put(FIELD_SURV_MONTH_ACTIVE_FUP, Collections.singletonList(SurvivalTimeUtils.UNKNOWN_SURVIVAL));
        _unknownValues.put(FIELD_SURV_FLAG_ACTIVE_FUP, Collections.singletonList(SurvivalTimeUtils.SURVIVAL_FLAG_UNKNOWN));
        _unknownValues.put(FIELD_SURV_MONTH_PRESUMED_ALIVE, Collections.singletonList(SurvivalTimeUtils.UNKNOWN_SURVIVAL));
        _unknownValues.put(FIELD_SURV_FLAG_PRESUMED_ALIVE, Collections.singletonList(SurvivalTimeUtils.SURVIVAL_FLAG_UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Integer cutoffYear = (Integer)input.getParameter(PARAM_SURV_CUTOFF_YEAR);
        if (cutoffYear == null)
            throw new RuntimeException("This algorithm requires a cutoff year!");

        Map<String, Object> inputPatient = Utils.extractPatient(input);

        List<SurvivalTimeInputRecordDto> recDtoList = new ArrayList<>();
        for (Map<String, Object> inputTumor : Utils.extractTumors(inputPatient)) {
            SurvivalTimeInputRecordDto recDto = new SurvivalTimeInputRecordDto();
            recDto.setPatientIdNumber((String)inputPatient.get(FIELD_PAT_ID_NUMBER));
            recDto.setDateOfDiagnosisYear(Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE)));
            recDto.setDateOfDiagnosisMonth(Utils.extractMonth((String)inputTumor.get(FIELD_DX_DATE)));
            recDto.setDateOfDiagnosisDay(Utils.extractDay((String)inputTumor.get(FIELD_DX_DATE)));
            recDto.setDateOfLastContactYear(Utils.extractYear((String)inputPatient.get(FIELD_DOLC)));
            recDto.setDateOfLastContactMonth(Utils.extractMonth((String)inputPatient.get(FIELD_DOLC)));
            recDto.setDateOfLastContactDay(Utils.extractDay((String)inputPatient.get(FIELD_DOLC)));
            recDto.setBirthYear(Utils.extractYear((String)inputPatient.get(FIELD_DATE_OF_BIRTH)));
            recDto.setBirthMonth(Utils.extractMonth((String)inputPatient.get(FIELD_DATE_OF_BIRTH)));
            recDto.setBirthDay(Utils.extractDay((String)inputPatient.get(FIELD_DATE_OF_BIRTH)));
            recDto.setVitalStatus((String)inputPatient.get(FIELD_VS));
            recDto.setSequenceNumberCentral((String)inputTumor.get(FIELD_SEQ_NUM_CTRL));
            recDto.setTypeOfReportingSource((String)inputTumor.get(FIELD_TYPE_RPT_SRC));
            recDtoList.add(recDto);
        }

        SurvivalTimeInputPatientDto patDto = new SurvivalTimeInputPatientDto();
        patDto.setSurvivalTimeInputPatientDtoList(recDtoList);

        SurvivalTimeOutputPatientDto patResultDto = SurvivalTimeUtils.calculateSurvivalTime(patDto, cutoffYear);

        Map<String, Object> outputPatient = new HashMap<>();
        outputPatient.put(FIELD_SURV_VS_RECODE, patResultDto.getVitalStatusRecode());

        List<Map<String, Object>> outputTumorList = new ArrayList<>();
        for (SurvivalTimeOutputRecordDto dto : patResultDto.getSurvivalTimeOutputPatientDtoList()) {
            Map<String, Object> outputTumor = new HashMap<>();

            outputTumor.put(FIELD_SURV_MONTH_ACTIVE_FUP, dto.getSurvivalMonths());
            outputTumor.put(FIELD_SURV_FLAG_ACTIVE_FUP, dto.getSurvivalMonthsFlag());
            outputTumor.put(FIELD_SURV_DATE_ACTIVE_FUP, Utils.combineDate(dto.getSurvivalTimeDolcYear(), dto.getSurvivalTimeDolcMonth(), dto.getSurvivalTimeDolcDay()));
            outputTumor.put(FIELD_SURV_MONTH_PRESUMED_ALIVE, dto.getSurvivalMonthsPresumedAlive());
            outputTumor.put(FIELD_SURV_FLAG_PRESUMED_ALIVE, dto.getSurvivalMonthsFlagPresumedAlive());
            outputTumor.put(FIELD_SURV_DATE_PRESUMED_ALIVE,
                    Utils.combineDate(dto.getSurvivalTimeDolcYearPresumedAlive(), dto.getSurvivalTimeDolcMonthPresumedAlive(), dto.getSurvivalTimeDolcDayPresumedAlive()));
            outputTumor.put(FIELD_SURV_DX_DATE_RECODE, Utils.combineDate(dto.getSurvivalTimeDxYear(), dto.getSurvivalTimeDxMonth(), dto.getSurvivalTimeDxDay()));
            outputTumor.put(FIELD_SURV_REC_NUM_RECODE, dto.getSortedIndex() == null ? null : StringUtils.leftPad(dto.getSortedIndex().toString(), 2, '0'));

            outputTumorList.add(outputTumor);
        }
        outputPatient.put(FIELD_TUMORS, outputTumorList);

        return AlgorithmOutput.of(outputPatient);
    }
}
