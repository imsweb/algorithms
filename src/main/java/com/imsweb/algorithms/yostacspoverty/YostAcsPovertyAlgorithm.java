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

import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_AIAN;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_ALL_RACES;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_ASIAN_NHOPI;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_BLACK;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_HISP;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_OTHER_MULTI;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_WHITE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV0610_WHITE_NON_HISP;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_AIAN;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_ALL_RACES;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_ASIAN_NHOPI;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_BLACK;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_HISP;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_OTHER_MULTI;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_WHITE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1014_WHITE_NON_HISP;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_AIAN;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_ALL_RACES;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_ASIAN_NHOPI;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_BLACK;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_HISPANIC;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_OTHER_MULTI;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_WHITE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_POV1418_WHITE_NON_HISP;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_Q0610_STATE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_Q0610_US;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_Q1014_STATE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_Q1014_US;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_Q1418_STATE;
import static com.imsweb.algorithms.Algorithms.FIELD_ACS_YOST_Q1418_US;
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

        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_Q0610_US));
        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_Q0610_STATE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_ALL_RACES));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_WHITE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_BLACK));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_AIAN));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_ASIAN_NHOPI));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_OTHER_MULTI));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_WHITE_NON_HISP));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV0610_HISP));
        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_Q1014_US));
        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_Q1014_STATE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_ALL_RACES));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_WHITE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_BLACK));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_AIAN));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_ASIAN_NHOPI));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_OTHER_MULTI));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_WHITE_NON_HISP));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1014_HISP));
        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_Q1418_US));
        _outputFields.add(Algorithms.getField(FIELD_ACS_YOST_Q1418_STATE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_ALL_RACES));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_WHITE));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_BLACK));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_AIAN));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_ASIAN_NHOPI));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_OTHER_MULTI));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_WHITE_NON_HISP));
        _outputFields.add(Algorithms.getField(FIELD_ACS_POV1418_HISPANIC));
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
            outputTumor.put(FIELD_ACS_YOST_Q0610_US, resultDto.getYostQuintile0610US());
            outputTumor.put(FIELD_ACS_YOST_Q0610_STATE, resultDto.getYostQuintile0610State());
            outputTumor.put(FIELD_ACS_POV0610_ALL_RACES, resultDto.getAcsPctPov0610AllRaces());
            outputTumor.put(FIELD_ACS_POV0610_WHITE, resultDto.getAcsPctPov0610White());
            outputTumor.put(FIELD_ACS_POV0610_BLACK, resultDto.getAcsPctPov0610Black());
            outputTumor.put(FIELD_ACS_POV0610_AIAN, resultDto.getAcsPctPov0610AIAN());
            outputTumor.put(FIELD_ACS_POV0610_ASIAN_NHOPI, resultDto.getAcsPctPov0610AsianNHOPI());
            outputTumor.put(FIELD_ACS_POV0610_OTHER_MULTI, resultDto.getAcsPctPov0610OtherMulti());
            outputTumor.put(FIELD_ACS_POV0610_WHITE_NON_HISP, resultDto.getAcsPctPov0610WhiteNonHisp());
            outputTumor.put(FIELD_ACS_POV0610_HISP, resultDto.getAcsPctPov0610Hispanic());

            outputTumor.put(FIELD_ACS_YOST_Q1014_US, resultDto.getYostQuintile1014US());
            outputTumor.put(FIELD_ACS_YOST_Q1014_STATE, resultDto.getYostQuintile1014State());
            outputTumor.put(FIELD_ACS_POV1014_ALL_RACES, resultDto.getAcsPctPov1014AllRaces());
            outputTumor.put(FIELD_ACS_POV1014_WHITE, resultDto.getAcsPctPov1014White());
            outputTumor.put(FIELD_ACS_POV1014_BLACK, resultDto.getAcsPctPov1014Black());
            outputTumor.put(FIELD_ACS_POV1014_AIAN, resultDto.getAcsPctPov1014AIAN());
            outputTumor.put(FIELD_ACS_POV1014_ASIAN_NHOPI, resultDto.getAcsPctPov1014AsianNHOPI());
            outputTumor.put(FIELD_ACS_POV1014_OTHER_MULTI, resultDto.getAcsPctPov1014OtherMulti());
            outputTumor.put(FIELD_ACS_POV1014_WHITE_NON_HISP, resultDto.getAcsPctPov1014WhiteNonHisp());
            outputTumor.put(FIELD_ACS_POV1014_HISP, resultDto.getAcsPctPov1014Hispanic());

            outputTumor.put(FIELD_ACS_YOST_Q1418_US, resultDto.getYostQuintile1418US());
            outputTumor.put(FIELD_ACS_YOST_Q1418_STATE, resultDto.getYostQuintile1418State());
            outputTumor.put(FIELD_ACS_POV1418_ALL_RACES, resultDto.getAcsPctPov1418AllRaces());
            outputTumor.put(FIELD_ACS_POV1418_WHITE, resultDto.getAcsPctPov1418White());
            outputTumor.put(FIELD_ACS_POV1418_BLACK, resultDto.getAcsPctPov1418Black());
            outputTumor.put(FIELD_ACS_POV1418_AIAN, resultDto.getAcsPctPov1418AIAN());
            outputTumor.put(FIELD_ACS_POV1418_ASIAN_NHOPI, resultDto.getAcsPctPov1418AsianNHOPI());
            outputTumor.put(FIELD_ACS_POV1418_OTHER_MULTI, resultDto.getAcsPctPov1418OtherMulti());
            outputTumor.put(FIELD_ACS_POV1418_WHITE_NON_HISP, resultDto.getAcsPctPov1418WhiteNonHisp());
            outputTumor.put(FIELD_ACS_POV1418_HISPANIC, resultDto.getAcsPctPov1418Hispanic());

            outputTumors.add(outputTumor);
        }
        return AlgorithmOutput.of(outputPatient);
    }
}
