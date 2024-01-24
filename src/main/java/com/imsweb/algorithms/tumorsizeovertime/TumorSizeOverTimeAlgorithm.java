/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tumorsizeovertime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.internal.Utils;
import com.imsweb.algorithms.tractestcongressdist.TractEstCongressDistOutputDto;
import com.imsweb.algorithms.tractestcongressdist.TractEstCongressDistUtils;

import static com.imsweb.algorithms.Algorithms.FIELD_BEHAV_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_EOD_TUMOR_SIZE;
import static com.imsweb.algorithms.Algorithms.FIELD_HIST_O3;
import static com.imsweb.algorithms.Algorithms.FIELD_PRIMARY_SITE;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TRACT_EST_CONGRESS_DIST;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMOR_SIZE;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMOR_SIZE_OVER_TIME;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMOR_SIZE_SUMMARY;

public class TumorSizeOverTimeAlgorithm extends AbstractAlgorithm {

    public TumorSizeOverTimeAlgorithm() {
        super(Algorithms.ALG_TUMOR_SIZE_OVER_TIME, TumorSizeOverTimeUtils.ALG_NAME, TumorSizeOverTimeUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_PRIMARY_SITE));
        _inputFields.add(Algorithms.getField(FIELD_HIST_O3));
        _inputFields.add(Algorithms.getField(FIELD_BEHAV_O3));
        _inputFields.add(Algorithms.getField(FIELD_TUMOR_SIZE));
        _inputFields.add(Algorithms.getField(FIELD_EOD_TUMOR_SIZE));
        _inputFields.add(Algorithms.getField(FIELD_TUMOR_SIZE_SUMMARY));

        _outputFields.add(Algorithms.getField(FIELD_TUMOR_SIZE_OVER_TIME));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            TumorSizeOverTimeInputDto inputDto = new TumorSizeOverTimeInputDto();
            inputDto.setDxYear(Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE)));
            inputDto.setEodTumorSize((String)inputTumor.get(FIELD_EOD_TUMOR_SIZE));
            inputDto.setCsTumorSize((String)inputTumor.get(FIELD_TUMOR_SIZE));
            inputDto.setTumorSizeSummary((String)inputTumor.get(FIELD_TUMOR_SIZE_SUMMARY));
            inputDto.setSite((String)inputTumor.get(FIELD_PRIMARY_SITE));
            inputDto.setHist((String)inputTumor.get(FIELD_HIST_O3));
            inputDto.setBehavior((String)inputTumor.get(FIELD_BEHAV_O3));

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_TUMOR_SIZE_OVER_TIME, TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
