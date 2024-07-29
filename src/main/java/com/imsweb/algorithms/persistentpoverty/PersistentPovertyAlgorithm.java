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
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.ALG_PERSISTENT_POVERTY;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_PERSISTENT_POVERTY;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;
import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

public class PersistentPovertyAlgorithm extends AbstractAlgorithm {

    public static final String PERSISTENT_POVERTY_UNK_A = "A";
    public static final String PERSISTENT_POVERTY_UNK_B = "B";
    public static final String PERSISTENT_POVERTY_UNK_C = "C";
    public static final String PERSISTENT_POVERTY_UNK_D = "D";

    public PersistentPovertyAlgorithm() {
        super(ALG_PERSISTENT_POVERTY, "NPCR Persistent Poverty", "v1 released in August 2024");

        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_PERSISTENT_POVERTY));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put(FIELD_TUMORS, outputTumors);

        for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
            StateCountyTractInputDto inputDto = new StateCountyTractInputDto();
            inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
            inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));

            String result = null;
            if (inputDto.hasInvalidStateCountyOrCensusTract(CensusTract.CENSUS_2010))
                result = PERSISTENT_POVERTY_UNK_A;
            else if (inputDto.hasUnknownStateCountyOrCensusTract(CensusTract.CENSUS_2010))
                result = PERSISTENT_POVERTY_UNK_D;
            else if (inputDto.countyIsNotReported())
                result = PERSISTENT_POVERTY_UNK_B;
            else {

                if (!CountryData.getInstance().isTractDataInitialized(inputDto.getAddressAtDxState()))
                    CountryData.getInstance().initializeTractData(inputDto.getAddressAtDxState());

                StateData stateData = CountryData.getInstance().getTractData(inputDto.getAddressAtDxState());
                if (stateData != null) {
                    CountyData countyData = stateData.getCountyData(inputDto.getCountyAtDxAnalysis());
                    if (countyData != null) {
                        CensusData censusData = countyData.getCensusData(inputDto.getCensusTract2010());
                        if (censusData != null)
                            result = censusData.getPersistentPoverty();
                    }
                }
            }
            if (result == null)
                result = PERSISTENT_POVERTY_UNK_C;

            Map<String, Object> outputTumor = new HashMap<>();
            outputTumor.put(FIELD_PERSISTENT_POVERTY, result);

            outputTumors.add(outputTumor);
        }

        return AlgorithmOutput.of(outputPatient);
    }
}
