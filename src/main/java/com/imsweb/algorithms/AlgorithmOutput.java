/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Map;

public class AlgorithmOutput {

    private Map<String, Object> _patient;

    public Map<String, Object> getPatient() {
        return _patient;
    }

    public void setPatient(Map<String, Object> patient) {
        _patient = patient;
    }
}
