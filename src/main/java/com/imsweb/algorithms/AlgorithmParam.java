/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.List;

public class AlgorithmParam<T> {

    public static <C> AlgorithmParam<C> of(String id, String name, Class<C> type) {
        AlgorithmParam<C> param = new AlgorithmParam<>();
        param.setId(id);
        param.setName(name);
        param.setType(type);
        return param;
    }

    public static <C> AlgorithmParam<C> of(String id, String name, Class<C> type, List<C> allowedValues) {
        AlgorithmParam<C> param = new AlgorithmParam<>();
        param.setId(id);
        param.setName(name);
        param.setType(type);
        param.setAllowedValues(allowedValues);
        return param;
    }

    private String _id;
    private String _name;
    private Class<T> _type;
    private List<T> _allowedValues;

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

    public Class<T> getType() {
        return _type;
    }

    public void setType(Class<T> type) {
        _type = type;
    }

    public List<T> getAllowedValues() {
        return _allowedValues;
    }

    public void setAllowedValues(List<T> allowedValues) {
        _allowedValues = allowedValues;
    }
}
