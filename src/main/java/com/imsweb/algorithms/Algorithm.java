/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Algorithm {

    String getId();

    String getName();

    String getVersion();

    String getInfo();

    List<AlgorithmParam> getParameters();

    List<AlgorithmField> getInputFields();

    List<AlgorithmField> getOutputFields();

    default Map<String, List<String>> getUnknownValues() {
        return Collections.emptyMap();
    }

    AlgorithmOutput execute(AlgorithmInput input);
}
