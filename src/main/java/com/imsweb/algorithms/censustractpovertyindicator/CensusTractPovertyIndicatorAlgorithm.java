/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.AlgorithmParam;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2000;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_POVERTY_INDICTR;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.PARAM_CENSUS_POVERTY_INC_RECENT_YEARS;

public class CensusTractPovertyIndicatorAlgorithm extends AbstractAlgorithm {

    public CensusTractPovertyIndicatorAlgorithm() {
        super(Algorithms.ALG_CENSUS_POVERTY, CensusTractPovertyIndicatorUtils.ALG_NAME, CensusTractPovertyIndicatorUtils.ALG_VERSION);
        
        _url = "https://www.naaccr.org/analysis-and-data-improvement-tools/#POVERTY";
        
        _params.add(AlgorithmParam.of(PARAM_CENSUS_POVERTY_INC_RECENT_YEARS, "Include Recent Years", Boolean.class));

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2000));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2020));

        _outputFields.add(Algorithms.getField(FIELD_CENSUS_POVERTY_INDICTR));

        _unknownValues.put(FIELD_CENSUS_POVERTY_INDICTR, Collections.singletonList(CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Boolean includeRecentYears = (Boolean)input.getParameter(PARAM_CENSUS_POVERTY_INC_RECENT_YEARS);
        if (includeRecentYears == null)
            includeRecentYears = Boolean.TRUE;

        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            CensusTractPovertyIndicatorInputDto inputDto = new CensusTractPovertyIndicatorInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
            inputDto.setDateOfDiagnosisYear(Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE)));
            inputDto.setCensusTract2000((String)inputTumor.get(FIELD_CENSUS_2000));
            inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));
            inputDto.setCensusTract2020((String)inputTumor.get(FIELD_CENSUS_2020));

            CensusTractPovertyIndicatorOutputDto outputDto = CensusTractPovertyIndicatorUtils.computePovertyIndicator(inputDto, includeRecentYears);
            outputTumors.add(Collections.singletonMap(FIELD_CENSUS_POVERTY_INDICTR, outputDto.getCensusTractPovertyIndicator()));
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
