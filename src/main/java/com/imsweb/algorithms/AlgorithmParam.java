package com.imsweb.algorithms;/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */

import java.util.List;

public class AlgorithmParam {

    private String _id;
    private String _name;
    private List<String> _allowedValues;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public List<String> getAllowedValues() {
        return _allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        _allowedValues = allowedValues;
    }
}
