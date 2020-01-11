/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.countyatdiagnosisanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_CERTAINTY_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_CERTAINTY_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_CERTAINTY_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_CERTAINTY_708090;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS_FLAG;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_1990;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_GEOCODE_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

public class CountyAtDxAnalysisAlgorithm extends AbstractAlgorithm {

    public CountyAtDxAnalysisAlgorithm() {
        super(Algorithms.ALG_COUNTY_AT_DIAGNOSIS_ANALYSIS, CountyAtDxAnalysisUtils.ALG_NAME, CountyAtDxAnalysisUtils.ALG_VERSION);

        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#1571164427018-84262475-4421";

        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_GEOCODE_1990));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_GEOCODE_2000));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_GEOCODE_2010));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_GEOCODE_2020));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_CERTAINTY_708090));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_CERTAINTY_2000));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_CERTAINTY_2010));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_CERTAINTY_2020));

        _outputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _outputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS_FLAG));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> patient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        patient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            CountyAtDxAnalysisInputDto inputDto = new CountyAtDxAnalysisInputDto();
            inputDto.setDateOfDiagnosis((String)inputTumor.get(FIELD_DX_DATE));
            inputDto.setAddrAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDx((String)inputTumor.get(FIELD_COUNTY_DX));
            inputDto.setCountyAtDxGeocode1990((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_1990));
            inputDto.setCountyAtDxGeocode2000((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_2000));
            inputDto.setCountyAtDxGeocode2010((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_2010));
            inputDto.setCountyAtDxGeocode2020((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_2020));
            inputDto.setCensusTrCert19708090((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_708090));
            inputDto.setCensusTrCertainty2000((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_2000));
            inputDto.setCensusTrCertainty2010((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_2010));
            inputDto.setCensusTrCertainty2020((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_2020));

            CountyAtDxAnalysisOutputDto output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_COUNTY_AT_DX_ANALYSIS, output.getCountyAtDxAnalysis());
            outputTumor.put(FIELD_COUNTY_AT_DX_ANALYSIS_FLAG, output.getCountyAtDxAnalysisFlag());
            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(patient);

    }
}
