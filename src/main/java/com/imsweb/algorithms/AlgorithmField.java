/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Objects;

/**
 * Abstraction of an "field" that is used either as input or output (or both) for an given algorithm.
 */
public class AlgorithmField {

    public static final String DATA_LEVEL_PATIENT = "Patient";
    public static final String DATA_LEVEL_TUMOR = "Tumor";

    public static AlgorithmField of(String id, Integer number, Integer length, String name, String shortName, String dataLevel) {
        return of(id, number, length, name, shortName, dataLevel, true);
    }

    public static AlgorithmField of(String id, Integer number, Integer length, String name, String shortName, String dataLevel, boolean isNaaccrStandard) {
        AlgorithmField field = new AlgorithmField();
        field.setId(id);
        field.setNumber(number);
        field.setLength(length);
        field.setName(name);
        field.setShortName(shortName);
        field.setDataLevel(dataLevel);
        field.setNaaccrStandard(isNaaccrStandard);
        return field;
    }

    // field ID (for standard fields, it should be the NAACCR XML ID)
    private String _id;

    // field name (for standard fields, it should be the NAACCR item name)
    private String _name;

    // field short name
    private String _shortName;

    // field length
    private Integer _length;

    // NAACCR Number (this will be available only for standard fields)
    private Integer _number;

    // data level (corresponds to the NAACCR XML parent tags, usually Patient or Tumor)
    private String _dataLevel;

    // whether the field is a NAACCR standard field
    private boolean isNaaccrStandard;

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

    public String getShortName() {
        return _shortName;
    }

    public void setShortName(String shortName) {
        _shortName = shortName;
    }

    public Integer getLength() {
        return _length;
    }

    public void setLength(Integer length) {
        _length = length;
    }

    public Integer getNumber() {
        return _number;
    }

    public void setNumber(Integer number) {
        _number = number;
    }

    public String getDataLevel() {
        return _dataLevel;
    }

    public void setDataLevel(String dataLevel) {
        _dataLevel = dataLevel;
    }

    public boolean isNaaccrStandard() {
        return isNaaccrStandard;
    }

    public void setNaaccrStandard(boolean naaccrStandard) {
        isNaaccrStandard = naaccrStandard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmField that = (AlgorithmField)o;
        return _id.equals(that._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
