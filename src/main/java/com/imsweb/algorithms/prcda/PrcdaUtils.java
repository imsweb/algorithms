/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    public static final String ALG_VERSION = "version 2.2 released in June 2022";

    public static final String PRCDA_NO = "0";
    public static final String PRCDA_YES = "1";
    public static final String PRCDA_UNKNOWN = "9";

    // States where every county is PRCDA
    public static final Set<String> ENTIRE_STATE_PRCDA = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("AK", "CT", "NV", "OK", "SC")));

    // States where every county is non-PRCDA
    public static final Set<String> ENTIRE_STATE_NON_PRCDA;
    static {
        List<String> nonPrcda = new ArrayList<>(Arrays.asList("AR", "DE", "DC", "GA", "HI", "IL", "KY", "MD", "MO", "NH", "NJ", "OH", "TN", "VT", "WV"));
        List<String> territory = Arrays.asList("AS", "GU", "MP", "PW", "PR", "UM", "VI", "FM", "MH", "TT");
        List<String> province = Arrays.asList("AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT");
        List<String> military = Arrays.asList("AA", "AE", "AP");
        //List<String> UNKNOWN_VALUES_DO_NOT_INCLUDE_IN_NON_PRCDA = Arrays.asList("CD", "US", "XX", "YY", "ZZ");
        nonPrcda.addAll(territory);
        nonPrcda.addAll(province);
        nonPrcda.addAll(military);
        ENTIRE_STATE_NON_PRCDA = Collections.unmodifiableSet(new HashSet<>(nonPrcda));
    }

    private static final PrcdaDataProvider _PROVIDER = new PrcdaDataProvider();

    private PrcdaUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates PRCDA and PRCDA2017 for the provided record
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addrAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * PRCDA and PRCDA2017 will have one the following values:
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
            result.setPrcda2017(PRCDA_YES);
        }
        else if (ENTIRE_STATE_NON_PRCDA.contains(input.getAddressAtDxState())) {
            result.setPrcda(PRCDA_NO);
            result.setPrcda2017(PRCDA_NO);
        }
        else {
            if (input.hasInvalidStateOrCounty() || input.hasUnknownStateOrCounty() || input.countyIsNotReported()) {
                result.setPrcda(PRCDA_UNKNOWN);
                result.setPrcda2017(PRCDA_UNKNOWN);
            }
            else {
                result.setPrcda(_PROVIDER.getPrcda(input.getAddressAtDxState(), input.getCountyAtDxAnalysis()));
                result.setPrcda2017(_PROVIDER.getPrcda2017(input.getAddressAtDxState(), input.getCountyAtDxAnalysis()));
            }
        }

        // get methods should never return null, but let's make sure we don't return null value anyway
        if (result.getPrcda() == null)
            result.setPrcda(PRCDA_NO);
        if (result.getPrcda2017() == null)
            result.setPrcda2017(PRCDA_NO);

        return result;
    }
}
