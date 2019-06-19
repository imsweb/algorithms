/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

/**
 * Abstraction of an "field" that is used either as input or output (or both) for an given algorithm.
 */
public class AlgorithmField {

    public static AlgorithmField of(String id, Integer number, Integer length) {
        AlgorithmField field = new AlgorithmField();
        field.setId(id);
        field.setNumber(number);
        field.setLength(length);
        return field;
    }

    // field ID (for standard fields, it should be the NAACCR XML ID)
    private String _id;

    // NAACCR Number (this will be available only for standard fields)
    private Integer _number;

    // field length
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

    public Integer getLength() {
        return _length;
    }

    public void setLength(Integer length) {
        _length = length;
    }
}
