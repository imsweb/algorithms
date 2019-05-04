/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Map;

public class AlgorithmInput {

    private Map<String, Object> _parameters;

    private Map<String, Object> _patient;

    public Map<String, Object> getParameters() {
        return _parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        _parameters = parameters;
    }

    public Map<String, Object> getPatient() {
        return _patient;
    }

    public void setPatient(Map<String, Object> patient) {
        _patient = patient;
    }
}
