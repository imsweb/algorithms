/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

public class StateCountyTractInputDto extends StateCountyInputDto {

    public enum CensusTract {
        CENSUS_2000, CENSUS_2010
    }

    //NAACCR values for missing or unknown census tract
    private static final List<String> _MISSING_OR_UNKNOWN_CENSUS_TRACTS = Arrays.asList("", "000000", "999999");

    private String _censusTract2000;
    private String _censusTract2010;

    @Override
    public void applyRecodes() {
        super.applyRecodes();
        _censusTract2000 = _censusTract2000 == null ? "" : _censusTract2000.trim();
        _censusTract2010 = _censusTract2010 == null ? "" : _censusTract2010.trim();
    }

    private String getCensusTractVariable(CensusTract censusTract) {
        if (CensusTract.CENSUS_2000.equals(censusTract)) {
            return _censusTract2000;
        }
        else if (CensusTract.CENSUS_2010.equals(censusTract)) {
            return _censusTract2010;
        }
        else {
            return null;
        }
    }

    public boolean hasInvalidStateCountyOrCensusTract(CensusTract censusTract) {
        return isInvalidStateCountyOrCensusTract(_addressAtDxState, _countyAtDxAnalysis, getCensusTractVariable(censusTract));
    }

    public boolean hasUnknownStateCountyOrCensusTract(CensusTract censusTract) {
        return isUnknownStateCountyOrCensusTract(_addressAtDxState, _countyAtDxAnalysis, getCensusTractVariable(censusTract));
    }

    public static boolean isInvalidCensusTract(String censusTract) {
        return !(isValidCensusTract(censusTract) || isUnknownCensusTract(censusTract));
    }

    public static boolean isInvalidStateCountyOrCensusTract(String state, String county, String censusTract) {
        return isInvalidState(state) || isInValidCounty(county) || isInvalidCensusTract(censusTract);
    }

    public static boolean isUnknownCensusTract(String censusTract) {
        return _MISSING_OR_UNKNOWN_CENSUS_TRACTS.contains(censusTract);
    }

    public static boolean isUnknownStateCountyOrCensusTract(String state, String county, String censusTract) {
        return isUnknownState(state) || isUnknownCounty(county) || isUnknownCensusTract(censusTract);
    }

    // this is private because it's meant to be more of a helper function
    // I want users of this class to use the "Invalid" and "Unknown" functions
    private static boolean isValidCensusTract(String censusTract) {
        return censusTract != null && censusTract.length() == 6 && NumberUtils.isDigits(censusTract) && Integer.parseInt(censusTract) >= 100;
    }

    public String getCensusTract2000() {
        return _censusTract2000;
    }

    public String getCensusTract2010() {
        return _censusTract2010;
    }

    public void setCensusTract2000(String censusTract2000) {
        _censusTract2000 = censusTract2000;
    }

    public void setCensusTract2010(String censusTract2010) {
        _censusTract2010 = censusTract2010;
    }
}
