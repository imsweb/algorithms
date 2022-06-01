/*
 * Copyright (C) 2020 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAlgorithm implements Algorithm {

    protected String _id;
    protected String _name;
    protected String _version;
    protected String _url;

    protected List<AlgorithmParam<?>> _params;

    protected List<AlgorithmField> _inputFields;
    protected List<AlgorithmField> _outputFields;

    protected Map<String, List<String>> _unknownValues;

    public AbstractAlgorithm(String id, String name, String version) {
        if (id == null)
            throw new IllegalStateException("ID is required");
        if (name == null)
            throw new IllegalStateException("Name is required");
        if (version == null)
            throw new IllegalStateException("Version is required (use N/A if your algorithm doesn't support a version)");

        _id = id;
        _name = name;
        _version = version;

        _params = new ArrayList<>();

        _inputFields = new ArrayList<>();
        _outputFields = new ArrayList<>();

        _unknownValues = new HashMap<>();
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getVersion() {
        return _version;
    }

    @Override
    public String getDocumentationUrl() {
        return _url;
    }

    @Override
    public List<AlgorithmParam<?>> getParameters() {
        return _params;
    }

    @Override
    public List<AlgorithmField> getInputFields() {
        return _inputFields;
    }

    @Override
    public List<AlgorithmField> getOutputFields() {
        return _outputFields;
    }

    @Override
    public Map<String, List<String>> getUnknownValues() {
        return _unknownValues;
    }

    @Override
    public abstract AlgorithmOutput execute(AlgorithmInput input);
}
