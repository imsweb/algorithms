package com.imsweb.algorithms;/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */

public class AlgorithmField {

    public static AlgorithmField of(String id, Integer number, String name, Integer length) {
        AlgorithmField field = new AlgorithmField();
        field.setId(id);
        field.setNumber(number);
        field.setName(name);
        field.setLength(length);
        return field;
    }

    private String _id;
    private Integer _number;
    private String _name;
    private Integer _length;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public Integer getNumber() {
        return _number;
    }

    public void setNumber(Integer number) {
        _number = number;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public Integer getLength() {
        return _length;
    }

    public void setLength(Integer length) {
        _length = length;
    }
}
