/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ephtsubcounty;

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

import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_EPHT_2010_GEOID_20K;
import static com.imsweb.algorithms.Algorithms.FIELD_EPHT_2010_GEOID_50K;
import static com.imsweb.algorithms.Algorithms.FIELD_EPHT_2010_GEOID_5K;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.ephtsubcounty.EphtSubCountyUtils.EPHT_2010_GEO_ID_UNK_A;
import static com.imsweb.algorithms.ephtsubcounty.EphtSubCountyUtils.EPHT_2010_GEO_ID_UNK_D;

public class EphtSubCountyAlgorithm extends AbstractAlgorithm {

    public EphtSubCountyAlgorithm() {
        super(Algorithms.ALG_EPHT_SUBCOUNTY, EphtSubCountyUtils.ALG_NAME, EphtSubCountyUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_EPHT_2010_GEOID_5K));
        _outputFields.add(Algorithms.getField(FIELD_EPHT_2010_GEOID_20K));
        _outputFields.add(Algorithms.getField(FIELD_EPHT_2010_GEOID_50K));

        _unknownValues.put(FIELD_EPHT_2010_GEOID_5K, Arrays.asList(EPHT_2010_GEO_ID_UNK_A, EPHT_2010_GEO_ID_UNK_D));
        _unknownValues.put(FIELD_EPHT_2010_GEOID_20K, Arrays.asList(EPHT_2010_GEO_ID_UNK_A, EPHT_2010_GEO_ID_UNK_D));
        _unknownValues.put(FIELD_EPHT_2010_GEOID_50K, Arrays.asList(EPHT_2010_GEO_ID_UNK_A, EPHT_2010_GEO_ID_UNK_D));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {

        List<Map<String, Object>> outputTumors = new ArrayList<>();
        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            EphtSubCountyOutputDto outputDto = EphtSubCountyUtils.computeEphtSubCounty(createStateCountyTractInputDto(inputTumor));

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_EPHT_2010_GEOID_5K, outputDto.getEpht2010GeoId5k());
            outputTumor.put(FIELD_EPHT_2010_GEOID_20K, outputDto.getEpht2010GeoId20k());
            outputTumor.put(FIELD_EPHT_2010_GEOID_50K, outputDto.getEpht2010GeoId50k());
            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputTumors);
    }
}
