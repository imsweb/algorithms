/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.surgery;

public class SurgeryRowDto {

    private String _code;
    private String _description;
    private Integer _level;
    private Boolean _lineBreak;

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        this._code = code;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public Integer getLevel() {
        return _level;
    }

    public void setLevel(Integer level) {
        this._level = level;
    }

    public Boolean isLineBreak() {
        return _lineBreak;
    }

    public void setLineBreak(Boolean lineBreak) {
        this._lineBreak = lineBreak;
    }

}
