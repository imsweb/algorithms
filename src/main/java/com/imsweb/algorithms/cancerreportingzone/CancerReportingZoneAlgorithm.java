/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_CANCER_REPORTING_ZONE;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.cancerreportingzone.CancerReportingZoneUtils.CANCER_REPORTING_ZONE_UNK_A;
import static com.imsweb.algorithms.cancerreportingzone.CancerReportingZoneUtils.CANCER_REPORTING_ZONE_UNK_D;

public class CancerReportingZoneAlgorithm extends AbstractAlgorithm {

    public CancerReportingZoneAlgorithm() {
        super(Algorithms.ALG_CANCER_REPORTING_ZONE, CancerReportingZoneUtils.ALG_NAME, CancerReportingZoneUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_CANCER_REPORTING_ZONE));

        _unknownValues.put(FIELD_CANCER_REPORTING_ZONE, Arrays.asList(CANCER_REPORTING_ZONE_UNK_A, CANCER_REPORTING_ZONE_UNK_D));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            CancerReportingZoneInputDto inputDto = new CancerReportingZoneInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
            inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));

            CancerReportingZoneOutputDto outputDto = CancerReportingZoneUtils.computeCancerReportingZone(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_CANCER_REPORTING_ZONE, outputDto.getCancerReportingZone());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
