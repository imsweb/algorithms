/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

public class TractEstCongressDistUtils {

    public static final String ALG_NAME = "Tract-Estimated Congressional Districts";
    public static final String ALG_VERSION = "1.0";
    public static final String ALG_INFO = "Tract-Estimated Congressional Districts version 1.0 released in September 2018";

    //Unknown values for each code
    public static final String TRACT_EST_CONGRESS_DIST_UNKNOWN = "C";

    // data provider
    private static TractEstCongressDistDataProvider _PROVIDER;

    /**
     * Calculates the urban rural indicator code (uric2000, uric2010) for the provided input DTO
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned urban rural census will have the following values depending on the census tract (2000, or 2010):
     * <ul>
     * <li>1 = All urban - the percentage of the population in an urban area is 100%</li>
     * <li>2 = Mostly urban - the percentage of the population in an urban area is between >=50% and <100%</li>
     * <li>3 = Mostly rural - the percentage of the population in an urban area is between >0% and <50%</li>
     * <li>4 = All rural - the percentage of the population in an urban area is 0%</li>
     * <li>9 = The percentage of the population in an urban or rural area is unknown</li>
     * <li>A = State, county, or tract are invalid</li>
     * <li>B = State and tract are valid, but county was not reported</li>
     * <li>C = State + county + tract combination was not found</li>
     * <li>D = State, county, or tract are blank or unknown/li>
     * </ul>
     * <br/><br/>
     * @param input a <code>TractEstCongressDistInputDto</code> input object
     * @return the computed rural urban census value
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
