/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.race;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.AbstractAlgorithm;
import com.imsweb.algorithms.AlgorithmInput;
import com.imsweb.algorithms.AlgorithmOutput;
import com.imsweb.algorithms.Algorithms;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.Algorithms.FIELD_DX_DATE;
import static com.imsweb.algorithms.Algorithms.FIELD_IHS;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE1;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE1_RECODE;
import static com.imsweb.algorithms.Algorithms.FIELD_RACE2;
import static com.imsweb.algorithms.Algorithms.FIELD_STATE_DX;

public final class Race1RecodeAlgorithm extends AbstractAlgorithm {

    private static final int _RACE_WHITE = 1;
    private static final int _RACE_BLACK = 2;
    private static final int _RACE_AIAN = 3;
    private static final int _RACE_NONE = 88;
    private static final int _RACE_LAST_SPECIFIC = 97;
    private static final int _RACE_OTHER = 98;
    private static final int _RACE_UNKNOWN = 99;

    private static final String _IHS_INVALID = "9";
    private static final String _IHS_MATCH = "1";

    public Race1RecodeAlgorithm() {
        super(Algorithms.ALG_RACE_1_RECODE, "SEER Race 1 Recode", "v1 released in August 2024");

        _inputFields.add(Algorithms.getField(FIELD_DX_DATE));
        _inputFields.add(Algorithms.getField(FIELD_RACE1));
        _inputFields.add(Algorithms.getField(FIELD_RACE2));
        _inputFields.add(Algorithms.getField(FIELD_STATE_DX));
        _inputFields.add(Algorithms.getField(FIELD_IHS));

        _outputFields.add(Algorithms.getField(FIELD_RACE1_RECODE));

        _unknownValues.put(FIELD_RACE1_RECODE, Collections.singletonList(String.valueOf(_RACE_UNKNOWN)));
    }

    @Override
    public AlgorithmOutput execute(AlgorithmInput input) {
        Map<String, Object> inputPatient = Utils.extractPatient(input);

        boolean hasRecordBefore1991 = false;
        Set<String> states = new HashSet<>();
        for (Map<String, Object> inputTumor : Utils.extractTumors(inputPatient)) {
            String dxYear = Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE));
            int iDxYear = NumberUtils.isDigits(dxYear) && dxYear.length() >= 4 ? Integer.parseInt(dxYear.substring(0, 4)) : 9999;
            if (iDxYear < 1991)
                hasRecordBefore1991 = true;
            String state = (String)inputTumor.get(FIELD_STATE_DX);
            if (state != null)
                states.add(state.toUpperCase());
        }

        String race1 = (String)inputPatient.get(FIELD_RACE1);
        int iRace1 = NumberUtils.isDigits(race1) ? Integer.parseInt(race1) : _RACE_UNKNOWN;
        String race2 = (String)inputPatient.get(FIELD_RACE2);
        int iRace2 = NumberUtils.isDigits(race2) ? Integer.parseInt(race2) : _RACE_UNKNOWN;

        // not recode by default
        int race1Recode = iRace1;
        
        // don't recode for AK state
        if (!states.contains("AK")) {

            // switch race to favor non-whites, if a patient who's first record is after 1990 has race other, code to race 2 if known
            if ((race1Recode == _RACE_WHITE && _RACE_BLACK <= iRace2 && iRace2 <= _RACE_LAST_SPECIFIC && iRace2 != _RACE_NONE) ||
                (!hasRecordBefore1991 && race1Recode == _RACE_OTHER && _RACE_WHITE <= iRace2 && iRace2 <= _RACE_LAST_SPECIFIC && iRace2 != _RACE_NONE))
                race1Recode = iRace2;

            // adjustment for IHS link
            String ihsLink = Objects.toString(inputPatient.get(FIELD_IHS), " ");
            if (ihsLink.equals(_IHS_INVALID))
                race1Recode = _RACE_UNKNOWN;
            else if (ihsLink.equals(_IHS_MATCH) && (race1Recode == 1 || race1Recode == 98 || race1Recode == 99))
                race1Recode = _RACE_AIAN;
        }

        Map<String, Object> outputPatient = new HashMap<>();
        outputPatient.put(FIELD_RACE1_RECODE, StringUtils.leftPad(String.valueOf(race1Recode), 2, '0'));

        return AlgorithmOutput.of(outputPatient);
    }
}
