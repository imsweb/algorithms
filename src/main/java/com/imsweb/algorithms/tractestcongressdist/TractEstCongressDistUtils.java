/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

public class TractEstCongressDistUtils {

    public static final String ALG_NAME = "Tract-Estimated Congressional Districts";
    public static final String ALG_VERSION = "1.0";
    public static final String ALG_INFO = "Tract-Estimated Congressional Districts version 1.0 released in August 2021";

    //Unknown values for each code
    public static final String TRACT_EST_CONGRESS_DIST_UNKNOWN = "C";

    // data provider
    private static TractEstCongressDistDataProvider _PROVIDER;

    /**
     * Calculates the Tract Estimated Congressional Districts code for the provided input DTO
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned Tract Estimated Congressional Districts will have the following values :
     * <ul>
     * <li>00-53 = the Tract Estimated Congressional District value</li>
     * <li>98 = Unknown value</li>
     * <li>ZZ = Unknown value</li>
     * <li>A = State, county, or tract are invalid</li>
     * <li>B = State and tract are valid, but county was not reported</li>
     * <li>C = State + county + tract combination was not found</li>
     * <li>D = State, county, or tract are blank or unknown/li>
     * </ul>
     * <br/><br/>
     * @param input a <code>TractEstCongressDistInputDto</code> input object
     * @return the computed Tract Estimated Congressional Districts value
     */
    public static TractEstCongressDistOutputDto computeTractEstCongressDist(TractEstCongressDistInputDto input) {
        TractEstCongressDistOutputDto result = new TractEstCongressDistOutputDto();
        input.applyRecodes();
        
        if (!input.isStateValidOrMissingOrUnknown() || !input.isCountyValidOrMissingOrUnknown() || !input.isCensusTract2010ValidOrMissingOrUnknown())
            result.setTractEstCongressDist("A");
        else if (input.isStateMissingOrUnknown() || input.isCountyMissingOrUnknown() || input.isCensusTract2010MissingOrUnknown())
            result.setTractEstCongressDist("D");
        else if ("000".equals(input.getCountyAtDxAnalysis()))
            result.setTractEstCongressDist("B");
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            result.setTractEstCongressDist(_PROVIDER.getTractEstCongressDist(input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010()));
        }
        if (result.getTractEstCongressDist() == null)
            result.setTractEstCongressDist(TRACT_EST_CONGRESS_DIST_UNKNOWN);

        return result;
    }

    /**
     * Use this method to register your own data provider instead of using the internal one that is entirely in memory.
     * <br/><br/>
     * This has to be done before the first call to the compute method, or the internal one will be registered by default.
     * <br/><br/>
     * Once a provider has been set, this method cannot be called (it will throw an exception).
     * @param provider the <code>TractEstCongressDistDataProvider</code> to set
     */
    public static synchronized void setDataProvider(TractEstCongressDistDataProvider provider) {
        if (_PROVIDER != null)
            throw new RuntimeException("The data provider has already been set!");
        _PROVIDER = provider;
    }

    private static synchronized void initializeInternalDataProvider() {
        if (_PROVIDER != null)
            return;
        _PROVIDER = new TractEstCongressDistCsvData();
    }
}
