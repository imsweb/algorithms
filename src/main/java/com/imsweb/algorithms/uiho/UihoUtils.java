/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.uiho;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class can be used to calculate UIHO, and UIHO city.
 * <br/><br/>
 * <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/static.html#chsda"</a>
 * <br/><br/>
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public final class UihoUtils {

    public static final String ALG_NAME = "NPCR UIHO Linkage Program";
    public static final String ALG_VERSION = "version 2.2 released in June 2022";

    private static final List<String> _STATES = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL",
            "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH",
            "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY");

    private static final List<String> _TERRITORIES = Arrays.asList("AS", "GU", "MP", "PW", "PR", "UM", "VI", "FM", "MH", "TT");

    private static final List<String> _PROVINCES = Arrays.asList("AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT");

    private static final List<String> _ARMED_FORCES = Arrays.asList("AA", "AE", "AP");

    // Just sort of acknowledging the existence of these codes to show I didn't forget about them.
    //private static final List<String> _UNKNOWN_STATES = Arrays.asList("CD", "US", "XX", "YY", "ZZ");

    public static final String UIHO_NO = "0";
    public static final String UIHO_UNKNOWN = "9";
    public static final String UIHO_CITY_NONE = "00";
    public static final String UIHO_CITY_UNKNOWN = "99";

    private static final UihoDataProvider _PROVIDER = new UihoDataProvider();

    private UihoUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates UIHO and UIHO City for the provided record
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addrAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * UIHO will have one the following values:
     * 0 = Not UIHO
     * 1 = UIHO
     * 9 = Unknown
     * <br/><br/>
     * UIHO City will have one the following values:
     * 00 = No UIHO City
     * 01-98 = UIHO City number
     * 99 = Unknown
     * <br/><br/>
     * @param input a <code>UihoInputDto</code> input object
     * @return <code>UihoOutputDto</code> output object
     */
    public static UihoOutputDto computeUiho(UihoInputDto input) {
        UihoOutputDto result = new UihoOutputDto();
        input.applyRecodes();

        if (!isStateAtDxValid(input.getAddressAtDxState()) || !isCountyAtDxValid(input.getAddressAtDxCounty())) {
            result.setUIHO(UIHO_UNKNOWN);
            result.setUihoCity(UIHO_CITY_UNKNOWN);
        }
        else {
            result.setUIHO(_PROVIDER.getUIHO(input.getAddressAtDxState(), input.getAddressAtDxCounty()));
            result.setUihoCity(_PROVIDER.getUIHOCity(input.getAddressAtDxState(), input.getAddressAtDxCounty()));
        }

        // get methods should never return null, but let's make sure we don't return null value anyway
        if (result.getUiho() == null) {
            result.setUIHO(UIHO_NO);
        }
        if (result.getUihoCity() == null) {
            result.setUihoCity(UIHO_CITY_NONE);
        }

        return result;
    }

    static boolean isCountyAtDxValid(String county) {
        return county != null && county.length() == 3 && !("999".equals(county)) && NumberUtils.isDigits(county);
    }

    static boolean isStateAtDxValid(String state) {
        return _STATES.contains(state)
                || _TERRITORIES.contains(state)
                || _PROVINCES.contains(state)
                || _ARMED_FORCES.contains(state);
    }
}
