/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Abstraction of an "algorithm" that takes input fields, can be executed, and returns output fields.
 */
@SuppressWarnings("unused")
public interface Algorithm {

    /**
     * Returns the ID that uniquely identifiers this algorithm.
     */
    String getId();

    /**
     * Returns the algorithm name.
     */
    String getName();

    /**
     * Returns the algorithm version (if no version is applicable, you can use the string "N/A" or any value you want).
     */
    String getVersion();

    /**
     * Returns a short sentence describing the algorithm (if you don't have one, you can just return the algorithm name).
     */
    String getInfo();

    /**
     * Returns the list of parameters (options) for this algorithm.
     */
    List<AlgorithmParam<?>> getParameters();

    /**
     * Returns the list of input fields for this algorithm.
     */
    List<AlgorithmField> getInputFields();

    /**
     * Returns the list of output fields for this algorithm.
     */
    List<AlgorithmField> getOutputFields();

    /**
     * Returns the list of unknown value, per field.
     */
    default Map<String, List<String>> getUnknownValues() {
        return Collections.emptyMap();
    }

    /**
     * Executes this algorithm using the given input fields.
     */
    AlgorithmOutput execute(AlgorithmInput input);
}
