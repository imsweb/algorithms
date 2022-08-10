/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.util.HashMap;
import java.util.Map;

public class PrcdaInputDto {

    // SEER City recodes
    private static final Map<String, String> _SEER_CITY_RECODES = new HashMap<>();

    static {
        _SEER_CITY_RECODES.put("AT", "GA");
        _SEER_CITY_RECODES.put("DT", "MI");
        _SEER_CITY_RECODES.put("GB", "CA");
        _SEER_CITY_RECODES.put("LO", "CA");
        _SEER_CITY_RECODES.put("SE", "WA");
    }

    private String _addressAtDxCounty;
    private String _addressAtDxState;

    public void applyRecodes() {
        _addressAtDxState = _addressAtDxState == null ? "" : _addressAtDxState.toUpperCase().trim();
        _addressAtDxCounty = _addressAtDxCounty == null ? "" : _addressAtDxCounty.trim();
        // recode state for SEER cities
        _addressAtDxState = _SEER_CITY_RECODES.containsKey(_addressAtDxState) ? _SEER_CITY_RECODES.get(_addressAtDxState) : _addressAtDxState;
    }

    // getters
    public String getAddressAtDxCounty() {
        return _addressAtDxCounty;
    }

    public String getAddressAtDxState() {
        return _addressAtDxState;
    }

    // setters
    public void setAddressAtDxCounty(String addressAtDxCounty) {
        _addressAtDxCounty = addressAtDxCounty;
    }

    public void setAddressAtDxState(String addressAtDxState) {
        _addressAtDxState = addressAtDxState;
    }

}
