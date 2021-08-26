/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ephtsubcounty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_EPHT_2010_GEOID_20K;
import static com.imsweb.algorithms.Algorithms.FIELD_EPHT_2010_GEOID_5K;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;
import static com.imsweb.algorithms.ephtsubcounty.EphtSubCountyUtils.EPHT_2010_GEO_ID_UNK_A;
import static com.imsweb.algorithms.ephtsubcounty.EphtSubCountyUtils.EPHT_2010_GEO_ID_UNK_D;

public class EphtSubCountyAlgorithm extends AbstractAlgorithm {

    public EphtSubCountyAlgorithm() {
        super(Algorithms.ALG_EPHT_SUBCOUNTY, EphtSubCountyUtils.ALG_NAME, EphtSubCountyUtils.ALG_VERSION, EphtSubCountyUtils.ALG_INFO);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_EPHT_2010_GEOID_5K));
        _outputFields.add(Algorithms.getField(FIELD_EPHT_2010_GEOID_20K));

        _unknownValues.put(FIELD_EPHT_2010_GEOID_5K, Arrays.asList(EPHT_2010_GEO_ID_UNK_A, EPHT_2010_GEO_ID_UNK_D));
        _unknownValues.put(FIELD_EPHT_2010_GEOID_20K, Arrays.asList(EPHT_2010_GEO_ID_UNK_A, EPHT_2010_GEO_ID_UNK_D));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            EphtSubCountyInputDto inputDto = new EphtSubCountyInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
            inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));

            EphtSubCountyOutputDto outputDto = EphtSubCountyUtils.computeEphtSubCounty(inputDto);

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_EPHT_2010_GEOID_5K, outputDto.getEpht2010GeoId5k());
            outputTumor.put(FIELD_EPHT_2010_GEOID_20K, outputDto.getEpht2010GeoId20k());

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
