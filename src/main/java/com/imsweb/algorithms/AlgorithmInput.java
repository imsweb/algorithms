/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Map;

/**
 * Abstraction of the "input" that an algorithm needs to compute it's output.
 */
public class AlgorithmInput {

    // parameters (keyed by parameter ID)
    private Map<String, Object> _parameters;

    // input data (keyed by field IDs, there is a special field representing a list of tumors).
    private Map<String, Object> _patient;

    public Map<String, Object> getParameters() {
        return _parameters;
    }

    public Object getParameter(String paramId) {
        return _parameters == null ? null : _parameters.get(paramId);
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
