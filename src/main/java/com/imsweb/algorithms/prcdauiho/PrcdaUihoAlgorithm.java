/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_PRCDA_COUNTY_2017;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.Algorithms.FIELD_UIHO_COUNTY_2017;
import static com.imsweb.algorithms.Algorithms.FIELD_UIHO_FACILITY_2017;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_UNKNOWN;

public class PrcdaUihoAlgorithm extends AbstractAlgorithm {

    public PrcdaUihoAlgorithm() {
        super(Algorithms.ALG_PRCDA_UIHO, PrcdaUihoUtils.ALG_NAME, PrcdaUihoUtils.ALG_VERSION, PrcdaUihoUtils.ALG_INFO);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_DX));

        _outputFields.add(Algorithms.getField(FIELD_PRCDA_COUNTY_2017));
        _outputFields.add(Algorithms.getField(FIELD_UIHO_COUNTY_2017));
        _outputFields.add(Algorithms.getField(FIELD_UIHO_FACILITY_2017));

        _unknownValues.put(FIELD_PRCDA_COUNTY_2017, Collections.singletonList(PRCDA_UNKNOWN));
        _unknownValues.put(FIELD_UIHO_COUNTY_2017, Collections.singletonList(UIHO_UNKNOWN));
        _unknownValues.put(FIELD_UIHO_FACILITY_2017, Collections.singletonList(UIHO_FACILITY_UNKNOWN));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            PrcdaUihoInputDto inputDto = new PrcdaUihoInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setAddressAtDxCounty((String)inputTumor.get(FIELD_COUNTY_DX));

            PrcdaUihoOutputDto outputDto = PrcdaUihoUtils.computePrcdaUiho(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_PRCDA_COUNTY_2017, outputDto.getPRCDA());
            outputTumor.put(FIELD_UIHO_COUNTY_2017, outputDto.getUIHO());
            outputTumor.put(FIELD_UIHO_FACILITY_2017, outputDto.getUIHOFacility());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}