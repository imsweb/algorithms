package com.imsweb.algorithms.yostacspoverty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV_AIAN;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV_ALL_RACES;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV_ASIAN_NHOPI;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV_BLACK;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV_HISP;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV_WHITE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV_WHITE_NON_HISP;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_QUINTILE_STATE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_QUINTILE_US;
import static com.imsweb.algorithms.Algorithms.FIELD_CENSUS_2010;
import static com.imsweb.algorithms.Algorithms.FIELD_COUNTY_AT_DX_ANALYSIS;
import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;

public class YostAcsPovertyAlgorithm extends AbstractAlgorithm {

    public YostAcsPovertyAlgorithm() {
        super(Algorithms.ALG_ACS_LINKAGE, YostAcsPovertyUtils.ALG_NAME, YostAcsPovertyUtils.ALG_VERSION);

        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_COUNTY_AT_DX_ANALYSIS));
        _inputFields.add(Algorithms.getField(FIELD_CENSUS_2010));

        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_QUINTILE_US));
        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_QUINTILE_STATE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV_ALL_RACES));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV_WHITE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV_BLACK));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV_AIAN));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV_ASIAN_NHOPI));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV_WHITE_NON_HISP));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV_HISP));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> outputPatient = new HashMap<>();
        List<Map<String, Object>> outputTumors = new ArrayList<>();
        outputPatient.put("tumors", outputTumors);

        Map<String, Object> inputPatient = Utils.extractPatient(input);
        List<Map<String, Object>> tumors = Utils.extractTumors(inputPatient);

        for (Map<String, Object> tumor : tumors) {
            YostAcsPovertyInputDto inputDto = new YostAcsPovertyInputDto();

            inputDto.setAddressAtDxState((String)tumor.get(FIELD_STATE_DX));
            inputDto.setCountyAtDxAnalysis((String)tumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
            inputDto.setCensusTract2010((String)tumor.get(FIELD_CENSUS_2010));
            inputDto.setDateOfDiagnosis((String)tumor.get(FIELD_DX_DATE));

            YostAcsPovertyOutputDto resultDto;
            Map<String, Object> outputTumor = new HashMap<>();
            resultDto = YostAcsPovertyUtils.computeYostAcsPovertyData(inputDto);
            outputTumor.put(FIELD_ACS_YOST_QUINTILE_US, resultDto.getYostQuintileUS());
            outputTumor.put(FIELD_ACS_YOST_QUINTILE_STATE, resultDto.getYostQuintileState());
            outputTumor.put(FIELD_ACS_POV_ALL_RACES, resultDto.getAcsPctPovAllRaces());
            outputTumor.put(FIELD_ACS_POV_WHITE, resultDto.getAcsPctPovWhite());
            outputTumor.put(FIELD_ACS_POV_BLACK, resultDto.getAcsPctPovBlack());
            outputTumor.put(FIELD_ACS_POV_AIAN, resultDto.getAcsPctPovAIAN());
            outputTumor.put(FIELD_ACS_POV_ASIAN_NHOPI, resultDto.getAcsPctPovAsianNHOPI());
            outputTumor.put(FIELD_ACS_POV_WHITE_NON_HISP, resultDto.getAcsPctPovWhiteNonHisp());
            outputTumor.put(FIELD_ACS_POV_HISP, resultDto.getAcsPctPovHispanic());

            outputTumors.add(outputTumor);
        }
        return AlgorithmOutput.of(outputPatient);
    }
}
