/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_CANCER_RPT_ZONE_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_CANCER_RPT_ZONE_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_CANCER_RPT_ZONE_TRACT_REQ_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_CANCER_RPT_ZONE_TRACT_REQ_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.cancerreportingzone.CancerReportingZoneUtils.CANCER_REPORTING_ZONE_UNK_A;
import static com.imsweb.algorithms.cancerreportingzone.CancerReportingZoneUtils.CANCER_REPORTING_ZONE_UNK_D;

public class CancerReportingZoneAlgorithm extends AbstractAlgorithm {

    public CancerReportingZoneAlgorithm() {
        super(Algorithms.ALG_CANCER_REPORTING_ZONE, CancerReportingZoneUtils.ALG_NAME, CancerReportingZoneUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2020));

        _outputFields.add(Algorithms.getField(FIELD_CANCER_RPT_ZONE_2010));
        _outputFields.add(Algorithms.getField(FIELD_CANCER_RPT_ZONE_TRACT_REQ_2010));
        _outputFields.add(Algorithms.getField(FIELD_CANCER_RPT_ZONE_2020));
        _outputFields.add(Algorithms.getField(FIELD_CANCER_RPT_ZONE_TRACT_REQ_2020));

        _unknownValues.put(FIELD_CANCER_RPT_ZONE_2010, Arrays.asList(CANCER_REPORTING_ZONE_UNK_A, CANCER_REPORTING_ZONE_UNK_D));
        _unknownValues.put(FIELD_CANCER_RPT_ZONE_TRACT_REQ_2010, Arrays.asList(CANCER_REPORTING_ZONE_UNK_A, CANCER_REPORTING_ZONE_UNK_D));
        _unknownValues.put(FIELD_CANCER_RPT_ZONE_2020, Arrays.asList(CANCER_REPORTING_ZONE_UNK_A, CANCER_REPORTING_ZONE_UNK_D));
        _unknownValues.put(FIELD_CANCER_RPT_ZONE_TRACT_REQ_2020, Arrays.asList(CANCER_REPORTING_ZONE_UNK_A, CANCER_REPORTING_ZONE_UNK_D));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = Utils.createPatientOutput();

        for (Map<String, Object> inputTumor : Utils.extractTumors(input)) {
            StateCountyTractInputDto inputDto = createStateCountyTractInputDto(inputTumor);

            CancerReportingZoneOutputDto outputDto = CancerReportingZoneUtils.computeCancerReportingZone(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_CANCER_RPT_ZONE_2010, outputDto.getCancerReportingZone2010());
            outputTumor.put(FIELD_CANCER_RPT_ZONE_TRACT_REQ_2010, outputDto.getCancerReportingZoneTractReq2010());
            outputTumor.put(FIELD_CANCER_RPT_ZONE_2020, outputDto.getCancerReportingZone2020());
            outputTumor.put(FIELD_CANCER_RPT_ZONE_TRACT_REQ_2020, outputDto.getCancerReportingZoneTractReq2020());

            Utils.addTumorOutput(outputPatient, outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
