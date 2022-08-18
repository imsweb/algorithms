/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

public class StateCountyInputDto {

    //Valid NAACCR values for state at dx
    private static final List<String> _VALID_STATES = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
            "AS", "GU", "MP", "PW", "PR", "UM", "VI", "FM", "MH", "TT", "AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE",
            "QC", "SK", "YT", "AA", "AE", "AP");

    //NAACCR values for missing or unknown state at dx
    private static final List<String> _MISSING_OR_UNKNOWN_STATES = Arrays.asList("", "CD", "US", "XX", "YY", "ZZ");

    //NAACCR values for missing or unknown county at dx
    private static final List<String> _MISSING_OR_UNKNOWN_COUNTIES = Arrays.asList("", "999");

    // SEER City recodes
    private static final Map<String, String> _SEER_CITY_RECODES = new HashMap<>();
    static {
        _SEER_CITY_RECODES.put("AT", "GA");
        _SEER_CITY_RECODES.put("DT", "MI");
        _SEER_CITY_RECODES.put("GB", "CA");
        _SEER_CITY_RECODES.put("LO", "CA");
        _SEER_CITY_RECODES.put("SE", "WA");
    }

    protected String _addressAtDxState;
    protected String _countyAtDxAnalysis;

    public void applyRecodes() {
        _addressAtDxState = _addressAtDxState == null ? "" : _addressAtDxState.toUpperCase().trim();
        _addressAtDxState = _SEER_CITY_RECODES.containsKey(_addressAtDxState) ? _SEER_CITY_RECODES.get(_addressAtDxState) : _addressAtDxState;
        _countyAtDxAnalysis = _countyAtDxAnalysis == null ? "" : _countyAtDxAnalysis.trim();
    }

    public boolean countyIsNotReported() {
        return "000".equals(_countyAtDxAnalysis);
    }

    public boolean hasInvalidStateOrCounty() {
        return isInvalidStateOrCounty(_addressAtDxState, _countyAtDxAnalysis);
    }

    public boolean hasUnknownStateOrCounty() {
        return isUnknownStateOrCounty(_addressAtDxState, _countyAtDxAnalysis);
    }

    public static boolean isInValidCounty(String county) {
        return !(isValidCounty(county) || isUnknownCounty(county));
    }

    public static boolean isInvalidState(String state) {
        return !(isValidState(state) || isUnknownState(state));
    }

    public static boolean isInvalidStateOrCounty(String state, String county) {
        return isInvalidState(state) || isInValidCounty(county);
    }

    public static boolean isUnknownCounty(String county) {
        return _MISSING_OR_UNKNOWN_COUNTIES.contains(county);
    }

    public static boolean isUnknownState(String state) {
        return _MISSING_OR_UNKNOWN_STATES.contains(state);
    }

    public static boolean isUnknownStateOrCounty(String state, String county) {
        return isUnknownState(state) || isUnknownCounty(county);
    }

    // this is private because it's meant to be more of a helper function
    // I want users of this class to use the "Invalid" and "Unknown" functions
    private static boolean isValidCounty(String county) {
        return county != null && county.length() == 3 && NumberUtils.isDigits(county);
    }

    // this is private because it's meant to be more of a helper function
    // I want users of this class to use the "Invalid" and "Unknown" functions
    private static boolean isValidState(String state) {
        return _VALID_STATES.contains(state);
    }

    public String getAddressAtDxState() {
        return _addressAtDxState;
    }

    public void setAddressAtDxState(String addressAtDxState) {
        _addressAtDxState = addressAtDxState;
    }

    public String getCountyAtDxAnalysis() {
        return _countyAtDxAnalysis;
    }

    public void setCountyAtDxAnalysis(String countyAtDxAnalysis) {
        _countyAtDxAnalysis = countyAtDxAnalysis;
    }


}
