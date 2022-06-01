/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class can be used to calculate PRCDA, UIHO, and UIHO facility.
 * <br/><br/>
 * <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/static.html#chsda"</a>
 * <br/><br/>
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public final class PrcdaUihoUtils {

    public static final String ALG_NAME = "NPCR PRCDA & UIHO Linkage Program";
    public static final String ALG_VERSION = "version 2.1 released in June 2021";

    private static final List<String> _STATES = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL",
            "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH",
            "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY");

    private static final List<String> _TERRITORIES = Arrays.asList("AS", "GU", "MP", "PW", "PR", "UM", "VI", "FM", "MH", "TT");

    private static final List<String> _PROVINCES = Arrays.asList("AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT");

    private static final List<String> _ARMED_FORCES = Arrays.asList("AA", "AE", "AP");

    private static final List<String> _UNKNOWN_STATES = Arrays.asList("CD", "US", "XX", "YY", "ZZ");

    public static final String PRCDA_NO = "0";
    public static final String PRCDA_YES = "1";
    public static final String PRCDA_UNKNOWN = "9";
    public static final String UIHO_NO = "0";
    public static final String UIHO_UNKNOWN = "9";

    // States where every county is PRCDA
    public static final List<String> ENTIRE_STATE_PRCDA = Collections.unmodifiableList(Arrays.asList("AK", "CT", "NV", "OK", "SC"));

    // States where every county is non-PRCDA
    public static final List<String> ENTIRE_STATE_NON_PRCDA = Collections.unmodifiableList(Arrays.asList("AR", "DE", "DC", "GA",
            "HI", "IL", "KY", "MD", "MO", "NH", "NJ", "OH", "TN", "VT", "WV"));

    // States with a mix of PRCDA and non-PRCDA counties
    public static final List<String> MIXED_PRCDA;
    static {
        List<String> temp = new ArrayList<>(_STATES);
        temp.removeAll(ENTIRE_STATE_PRCDA);
        temp.removeAll(ENTIRE_STATE_NON_PRCDA);
        MIXED_PRCDA = Collections.unmodifiableList(new ArrayList<>(temp));
    }

    private static PrcdaUihoDataProvider _PROVIDER;

    private PrcdaUihoUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates PRCDA, UIHO, and UIHO facility for the provided record
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addrAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * PRCDA will have one the following values:
     * 0 = Not a PRCDA county
     * 1 = PRCDA county
     * 9 = Unknown
     * <br/><br/>
     * UIHO will have one the following values:
     * 0 = Not UIHO
     * 1 = UIHO
     * 9 = Unknown
     * <br/><br/>
     * UIHO Facility will have one the following values:
     * 00 = No UIHO facility
     * 01-98 = UIHO facility number
     * 99 = Unknown
     * <br/><br/>
     * @param input a <code>PrcdaUihoInputDto</code> input object
     * @return <code>PrcdaUihoOutputDto</code> output object
     */
    public static PrcdaUihoOutputDto computePrcdaUiho(PrcdaUihoInputDto input) {
        PrcdaUihoOutputDto result = new PrcdaUihoOutputDto();
        input.applyRecodes();

        if (!isStateAtDxValid(input.getAddressAtDxState())) {
            result.setPRCDA(PRCDA_UNKNOWN);
            result.setUIHO(UIHO_UNKNOWN);
        }
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            // PRCDA
            if (ENTIRE_STATE_PRCDA.contains(input.getAddressAtDxState())) {
                result.setPRCDA(PRCDA_YES);
            }
            else if (ENTIRE_STATE_NON_PRCDA.contains(input.getAddressAtDxState())) {
                result.setPRCDA(PRCDA_NO);
            }
            else if (!isCountyAtDxValid(input.getAddressAtDxCounty()) || (MIXED_PRCDA.contains(input.getAddressAtDxState()) && "999".equals(input.getAddressAtDxCounty()))) {
                result.setPRCDA(PRCDA_UNKNOWN);
            }
            else {
                result.setPRCDA(_PROVIDER.getPRCDA(input.getAddressAtDxState(), input.getAddressAtDxCounty()));
            }
            // UIHO, UIHO FACILITY
            if (!isCountyAtDxValid(input.getAddressAtDxCounty()))
                result.setUIHO(UIHO_UNKNOWN);
            else
                result.setUIHO(_PROVIDER.getUIHO(input.getAddressAtDxState(), input.getAddressAtDxCounty()));
        }

        // get methods should never return null, but lets make sure we don't return null value anyways
        if (result.getPRCDA() == null)
            result.setPRCDA(PRCDA_NO);
        if (result.getUIHO() == null)
            result.setUIHO(UIHO_NO);

        return result;
    }

    static boolean isCountyAtDxValid(String county) {
        return (NumberUtils.isDigits(county)) && county.length() == 3;
    }

    static boolean isStateAtDxValid(String state) {
        return _STATES.contains(state)
                || _TERRITORIES.contains(state)
                || _PROVINCES.contains(state)
                || _ARMED_FORCES.contains(state)
                || _UNKNOWN_STATES.contains(state);
    }

    /**
     * Use this method to register your own data provider instead of using the internal one that is entirely in memory.
     * <br/><br/>
     * This has to be done before the first call to the compute method, or the internal one will be registered by default.
     * <br/><br/>
     * Once a provider has been set, this method cannot be called (it will throw an exception).
     * @param provider the <code>PrcdaUihoDataProvider</code> to set
     */
    @SuppressWarnings("unused")
    public static synchronized void setDataProvider(PrcdaUihoDataProvider provider) {
        if (_PROVIDER != null)
            throw new IllegalStateException("The data provider has already been set!");
        _PROVIDER = provider;
    }

    private static synchronized void initializeInternalDataProvider() {
        if (_PROVIDER != null)
            return;
        _PROVIDER = new PrcdaUihoCsvData();
    }
}
