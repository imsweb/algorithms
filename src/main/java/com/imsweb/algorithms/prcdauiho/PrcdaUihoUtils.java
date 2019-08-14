/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class can be used to calculate PRCDA, UIHO, and UIHO facility.
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public final class PrcdaUihoUtils {

    public static final String ALG_NAME = "NPCR PRCDA & UIHO Linkage Program";
    public static final String ALG_VERSION = "1.0";
    public static final String ALG_INFO = "NPCR PRCDA & UIHO Linkage Program version 1.0, released in August 2019";

    //Valid NAACCR values for state at dx
    private static final List<String> _VALID_STATES = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA",
            "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "AS", "GU",
            "MP", "PW", "PR", "UM", "VI", "FM", "MH", "TT", "AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT", "AA", "AE", "AP");

    //NAACCR values for missing or unknown state at dx
    private static final List<String> _MISSING_OR_UNKNOWN_STATES = Arrays.asList("CD", "US", "XX", "YY", "ZZ");

    public static final String PRCDA_NO = "0";
    public static final String PRCDA_YES = "1";
    public static final String PRCDA_INVALID = "9";
    public static final String UIHO_NO = "0";
    public static final String UIHO_INVALID = "9";
    public static final String UIHO_FACILITY_NONE = "00";
    public static final String UIHO_FACILITY_INVALID = "99";

    // States where every county is PRCDA
    protected static final List<String> ENTIRE_STATE_PRCDA = Arrays.asList("AK", "CT", "NV", "OK", "SC");

    private static PrcdaUihoDataProvider _PROVIDER;

    /**
     * Calculates PRCDA, UIHO, and UIHO facility for the provided record
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
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
    public static PrcdaUihoOutputDto computerPrcdaUiho(PrcdaUihoInputDto input) {
        PrcdaUihoOutputDto result = new PrcdaUihoOutputDto();
        input.applyRecodes();

        if (!isStateAtDxValid(input.getAddressAtDxState()) || !isCountyAtDxValid(input.getAddressAtDxCounty())) {
            result.setPRCDA(PRCDA_INVALID);
            result.setUIHO(UIHO_INVALID);
            result.setUIHOFacility(UIHO_FACILITY_INVALID);
        }
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();

            if (ENTIRE_STATE_PRCDA.contains(input.getAddressAtDxState())) {
                result.setPRCDA(PRCDA_YES);
            }
            else {
                result.setPRCDA(_PROVIDER.getPRCDA(input.getAddressAtDxState(), input.getAddressAtDxCounty()));
            }
            result.setUIHO(_PROVIDER.getUIHO(input.getAddressAtDxState(), input.getAddressAtDxCounty()));
            result.setUIHOFacility(_PROVIDER.getUIHOFacility(input.getAddressAtDxState(), input.getAddressAtDxCounty()));
        }

        // get methods should never return null, but lets make sure we don't return null value anyways
        if (result.getPRCDA() == null)
            result.setPRCDA(PRCDA_NO);
        if (result.getUIHO() == null)
            result.setUIHO(UIHO_NO);
        if (result.getUIHOFacility() == null)
            result.setUIHOFacility(UIHO_FACILITY_NONE);

        return result;
    }

    public static boolean isCountyAtDxValid(String county) {
        return (NumberUtils.isDigits(county)) && county.length() == 3;
    }

    public static boolean isStateAtDxValid(String state) {
        return _VALID_STATES.contains(state) || _MISSING_OR_UNKNOWN_STATES.contains(state);
    }

    /**
     * Use this method to register your own data provider instead of using the internal one that is entirely in memory.
     * <br/><br/>
     * This has to be done before the first call to the compute method, or the internal one will be registered by default.
     * <br/><br/>
     * Once a provider has been set, this method cannot be called (it will throw an exception).
     * @param provider the <code>PrcdaUihoDataProvider</code> to set
     */
    public static synchronized void setDataProvider(PrcdaUihoDataProvider provider) {
        if (_PROVIDER != null)
            throw new RuntimeException("The data provider has already been set!");
        _PROVIDER = provider;
    }

    private static synchronized void initializeInternalDataProvider() {
        if (_PROVIDER != null)
            return;
        _PROVIDER = new PrcdaUihoCsvData();
    }
}
