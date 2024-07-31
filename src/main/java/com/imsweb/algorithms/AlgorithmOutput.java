/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imsweb.algorithms.Algorithms.FIELD_TUMORS;

/**
 * Abstraction of the "output" that an algorithm produces when executed.
 */
public class AlgorithmOutput {

    public static AlgorithmOutput of(Map<String, Object> patient) {
        AlgorithmOutput output = new AlgorithmOutput();
        output.setPatient(patient);
        return output;
    }

    public static AlgorithmOutput of(List<Map<String, Object>> tumors) {
        Map<String, Object> patient = new HashMap<>();
        patient.put(FIELD_TUMORS, tumors);
        return of(patient);
    }

    private Map<String, Object> _patient;

    public Map<String, Object> getPatient() {
        return _patient;
    }

    public void setPatient(Map<String, Object> patient) {
        _patient = patient;
    }
}
