/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.nhia;

public class NhiaResultsDto {

    private String _nhia;

    public NhiaResultsDto() {
    }

    public NhiaResultsDto(String nhia) {
        this._nhia = nhia;
    }

    public String getNhia() {
        return _nhia;
    }

    public void setNhia(String nhia) {
        this._nhia = nhia;
    }
}
