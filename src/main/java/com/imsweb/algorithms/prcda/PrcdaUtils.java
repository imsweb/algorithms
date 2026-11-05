/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.imsweb.algorithms.StateCountyInputDto;

/**
 * This class can be used to calculate PRCDA.
 * <br/><br/>
 * <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/static.html#chsda">View documentation</a>
 * <br/><br/>
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public final class PrcdaUtils {

    public static final String ALG_NAME = "NPCR PRCDA Linkage Program";
    public static final String ALG_VERSION = "July 30, 2026";

    public static final String PRCDA_NO = "0";
    public static final String PRCDA_YES = "1";
    public static final String PRCDA_UNKNOWN = "9";

    // States where every county is PRCDA
    public static final Set<String> ENTIRE_STATE_PRCDA = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("AK", "CT", "NV", "OK", "SC")));

    private static final PrcdaDataProvider _PROVIDER = new PrcdaDataProvider();

    private PrcdaUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates PRCDA for the provided record
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
     * @param input a <code>StateCountyInputDto</code> input object
     * @return <code>PrcdaOutputDto</code> output object
     */
    public static PrcdaOutputDto computePrcda(StateCountyInputDto input) {
        PrcdaOutputDto result = new PrcdaOutputDto();
        
        input.applyRecodes();

        if (ENTIRE_STATE_PRCDA.contains(input.getAddressAtDxState())) {
            result.setPrcda(PRCDA_YES);
        }
        else if (_PROVIDER.isEntireStateNonPrcda(input.getAddressAtDxState())) {
            result.setPrcda(PRCDA_NO);
        }
        else {
            if (input.hasInvalidStateOrCounty() || input.hasUnknownStateOrCounty() || input.countyIsNotReported()) {
                result.setPrcda(PRCDA_UNKNOWN);
            }
            else {
                result.setPrcda(_PROVIDER.getPrcda(input.getAddressAtDxState(), input.getCountyAtDxAnalysis()));
            }
        }

        // get methods should never return null, but let's make sure we don't return null value anyway
        if (result.getPrcda() == null)
            result.setPrcda(PRCDA_NO);

        return result;
    }
}
