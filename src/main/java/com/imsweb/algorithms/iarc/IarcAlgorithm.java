/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iarc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_BEHAV_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_IARC_MP_INDICATOR;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_SEQ_NUM_CTRL;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

public class IarcAlgorithm extends AbstractAlgorithm {

    public IarcAlgorithm() {
        super(Algorithms.ALG_IARC, IarcUtils.ALG_NAME, IarcUtils.VERSION);

        _url = "http://www.iacr.com.fr/images/doc/MPrules_july2004.pdf";

        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));
        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_SEQ_NUM_CTRL));

        _outputFields.add(Algorithms.getField(FIELD_IARC_MP_INDICATOR));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        List<IarcMpInputRecordDto> inputRecordDtoList = new ArrayList<>();
        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            IarcMpInputRecordDto inputRecordDto = new IarcMpInputRecordDto();

            inputRecordDto.setSite((String)inputTumor.get(FIELD_PRIMARY_SITE));
            inputRecordDto.setHistology((String)inputTumor.get(FIELD_HIST_O3));
            inputRecordDto.setBehavior((String)inputTumor.get(FIELD_BEHAV_O3));
            inputRecordDto.setDateOfDiagnosisYear(Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE)));
            inputRecordDto.setDateOfDiagnosisMonth(Utils.extractMonth((String)inputTumor.get(FIELD_DX_DATE)));
            inputRecordDto.setDateOfDiagnosisDay(Utils.extractDay((String)inputTumor.get(FIELD_DX_DATE)));
            String seqNum = (String)inputTumor.get(FIELD_SEQ_NUM_CTRL);
            inputRecordDto.setSequenceNumber(NumberUtils.isDigits(seqNum) ? Integer.valueOf(seqNum) : null);

            inputRecordDtoList.add(inputRecordDto);
        }

        IarcUtils.calculateIarcMp(inputRecordDtoList);

        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);
        for (IarcMpInputRecordDto dto : inputRecordDtoList) {
            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_IARC_MP_INDICATOR, Objects.toString(dto.getInternationalPrimaryIndicator(), null));
            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
