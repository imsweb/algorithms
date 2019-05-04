/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.List;

public interface Algorithm {

    String getId();

    String getName();

    String getVersion();

    List<AlgorithmParam> getParameters();

    List<AlgorithmField> getInputFields();

    List<AlgorithmField> getOutputFields();

    AlgorithmOutput execute(AlgorithmInput input);
}
