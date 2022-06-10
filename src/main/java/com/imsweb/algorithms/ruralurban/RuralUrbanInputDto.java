/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class RuralUrbanInputDto {

    //Valid NAACCR values for state at dx
    private static final List<String> _VALID_STATES = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA",
            "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "AS", "GU",
            "MP", "PW", "PR", "UM", "VI", "FM", "MH", "TT", "AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT", "AA", "AE", "AP");

    //NAACCR values for missing or unknown state at dx
    private static final List<String> _MISSING_OR_UNKNOWN_STATES = Arrays.asList("", "CD", "US", "XX", "YY", "ZZ");

    //NAACCR values for missing or unknown county at dx
    private static final List<String> _MISSING_OR_UNKNOWN_COUNTIES = Arrays.asList("", "999");

    //NAACCR values for missing or unknown census tract
    private static final List<String> _MISSING_OR_UNKNOWN_CENSUS_TRACTS = Arrays.asList("", "000000", "999999");

    // SEER City recodes
    private static final Map<String, String> _SEER_CITY_RECODES = new HashMap<>();

    static {
        _SEER_CITY_RECODES.put("AT", "GA");
        _SEER_CITY_RECODES.put("DT", "MI");
        _SEER_CITY_RECODES.put("GB", "CA");
        _SEER_CITY_RECODES.put("LO", "CA");
        _SEER_CITY_RECODES.put("SE", "WA");
    }

    private String _countyAtDxAnalysis;
    private String _addressAtDxState;
    private String _censusTract2000;
    private String _censusTract2010;

    public void applyRecodes() {
        _addressAtDxState = _addressAtDxState == null ? "" : _addressAtDxState.toUpperCase().trim();
        _countyAtDxAnalysis = _countyAtDxAnalysis == null ? "" : _countyAtDxAnalysis.trim();
        _censusTract2000 = _censusTract2000 == null ? "" : _censusTract2000.trim();
        _censusTract2010 = _censusTract2010 == null ? "" : _censusTract2010.trim();

        // recode state for SEER cities
        _addressAtDxState = _SEER_CITY_RECODES.containsKey(_addressAtDxState) ? _SEER_CITY_RECODES.get(_addressAtDxState) : _addressAtDxState;
    }

    public boolean isCensusTract2000MissingOrUnknown() {
        return _MISSING_OR_UNKNOWN_CENSUS_TRACTS.contains(_censusTract2000);
    }

    public boolean isCensusTract2000ValidOrMissingOrUnknown() {
        return (NumberUtils.isDigits(_censusTract2000) && Integer.parseInt(_censusTract2000) >= 100) || isCensusTract2000MissingOrUnknown();
    }

    public boolean isCensusTract2010MissingOrUnknown() {
        return _MISSING_OR_UNKNOWN_CENSUS_TRACTS.contains(_censusTract2010);
    }

    public boolean isCensusTract2010ValidOrMissingOrUnknown() {
        return (NumberUtils.isDigits(_censusTract2010) && Integer.parseInt(_censusTract2010) >= 100) || isCensusTract2010MissingOrUnknown();
    }

    public boolean isCountyMissingOrUnknown() {
        return _MISSING_OR_UNKNOWN_COUNTIES.contains(_countyAtDxAnalysis);
    }

    public boolean isCountyValidOrMissingOrUnknown() {
        return (NumberUtils.isDigits(_countyAtDxAnalysis)) || isCountyMissingOrUnknown();
    }

    public boolean isStateMissingOrUnknown() {
        return _MISSING_OR_UNKNOWN_STATES.contains(_addressAtDxState);
    }

    public boolean isStateValidOrMissingOrUnknown() {
        return _VALID_STATES.contains(_addressAtDxState) || isStateMissingOrUnknown();
    }

    // getters
    public String getCountyAtDxAnalysis() {
        return _countyAtDxAnalysis;
    }

    public String getAddressAtDxState() {
        return _addressAtDxState;
    }

    public String getCensusTract2000() {
        return _censusTract2000;
    }

    public String getCensusTract2010() {
        return _censusTract2010;
    }

    // setters
    public void setCountyAtDxAnalysis(String countyAtDxAnalysis) {
        _countyAtDxAnalysis = countyAtDxAnalysis;
    }

    public void setAddressAtDxState(String addressAtDxState) {
        _addressAtDxState = addressAtDxState;
    }

    public void setCensusTract2000(String censusTract2000) {
        _censusTract2000 = censusTract2000;
    }

    public void setCensusTract2010(String censusTract2010) {
        _censusTract2010 = censusTract2010;
    }

}
