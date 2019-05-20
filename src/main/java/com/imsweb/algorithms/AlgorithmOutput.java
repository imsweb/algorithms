/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Map;

public class AlgorithmOutput {

    public static AlgorithmOutput of(Map<String, Object> patient) {
        AlgorithmOutput output = new AlgorithmOutput();
        output.setPatient(patient);
        return output;
    }

    private Map<String, Object> _patient;

    public Map<String, Object> getPatient() {
        return _patient;
    }

    public void setPatient(Map<String, Object> patient) {
        _patient = patient;
    }
}
