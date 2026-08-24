/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.persistentpoverty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.StateCountyTractInputDto.CensusTract;
import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.ALG_PERSISTENT_POVERTY;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2020;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_PERSISTENT_POVERTY_0711;
import static com.imsweb.algorithms.Algorithms.FIELD_PERSISTENT_POVERTY_1721;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;

public class PersistentPovertyAlgorithm extends AbstractAlgorithm {

    public static final String PERSISTENT_POVERTY_UNK_A = "A";
    public static final String PERSISTENT_POVERTY_UNK_B = "B";
    public static final String PERSISTENT_POVERTY_UNK_C = "C";
    public static final String PERSISTENT_POVERTY_UNK_D = "D";

    public PersistentPovertyAlgorithm() {
        super(ALG_PERSISTENT_POVERTY, "NAACCR Persistent Poverty", "August 21, 2026");

        _url = "https://www.ers.usda.gov/data-products/poverty-area-measures/";

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2020));

        _outputFields.add(Algorithms.getField(FIELD_PERSISTENT_POVERTY_0711));
        _outputFields.add(Algorithms.getField(FIELD_PERSISTENT_POVERTY_1721));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        for (Map<String, Object> inputTumor : Utils.extractTumors(input)) {
            StateCountyTractInputDto inputDto = createStateCountyTractInputDto(inputTumor);

            inputDto.applyRecodes();

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_PERSISTENT_POVERTY_0711, computeValue(inputDto, CensusTract.CENSUS_2010));
            outputTumor.put(FIELD_PERSISTENT_POVERTY_1721, computeValue(inputDto, CensusTract.CENSUS_2020));

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputTumors);
    }

    private String computeValue(StateCountyTractInputDto inputDto, CensusTract census) {
        String result = null;
        if (inputDto.hasInvalidStateCountyOrCensusTract(census))
            result = PERSISTENT_POVERTY_UNK_A;
        else if (inputDto.hasUnknownStateCountyOrCensusTract(census))
            result = PERSISTENT_POVERTY_UNK_D;
        else if (inputDto.countyIsNotReported())
            result = PERSISTENT_POVERTY_UNK_B;
        else {
            CensusData censusData = CountryData.getCensusData(inputDto, census);
            if (censusData != null) {
                if (census == CensusTract.CENSUS_2010)
                    result = censusData.getPersistentPoverty0711();
                else if (census == CensusTract.CENSUS_2020)
                    result = censusData.getPersistentPoverty1721();
                else
                    throw new IllegalStateException("Invalid census: " + census);
            }
        }

        if (result == null)
            result = PERSISTENT_POVERTY_UNK_C;

        return result;
    }
}
