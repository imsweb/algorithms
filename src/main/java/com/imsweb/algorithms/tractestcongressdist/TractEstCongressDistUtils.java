/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

import org.apache.commons.lang3.StringUtils;

import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.StateCountyTractInputDto.CensusTract;
import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;

public final class TractEstCongressDistUtils {

    public static final String ALG_NAME = "NAACCR Tract-Estimated Congressional Districts";
    public static final String ALG_VERSION = "version 1.0 released in August 2021";

    //Unknown values for each code
    public static final String TRACT_EST_CONGRESS_DIST_UNK_A = "A";
    public static final String TRACT_EST_CONGRESS_DIST_UNK_B = "B";
    public static final String TRACT_EST_CONGRESS_DIST_UNK_C = "C";
    public static final String TRACT_EST_CONGRESS_DIST_UNK_D = "D";

    private TractEstCongressDistUtils() {
        // no instances of this class allowed!
    }

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
     * @param input a <code>StateCountyTractInputDto</code> input object
     * @return the computed Tract Estimated Congressional Districts value
     */
    public static TractEstCongressDistOutputDto computeTractEstCongressDist(StateCountyTractInputDto input) {
        TractEstCongressDistOutputDto result = new TractEstCongressDistOutputDto();

        input.applyRecodes();

        if (input.hasInvalidStateCountyOrCensusTract(CensusTract.CENSUS_2010))
            result.setTractEstCongressDist(TRACT_EST_CONGRESS_DIST_UNK_A);
        else if (input.hasUnknownStateCountyOrCensusTract(CensusTract.CENSUS_2010))
            result.setTractEstCongressDist(TRACT_EST_CONGRESS_DIST_UNK_D);
        else if (input.countyIsNotReported())
            result.setTractEstCongressDist(TRACT_EST_CONGRESS_DIST_UNK_B);
        else {
            CensusData censusData = CountryData.getCensusData(input, CensusTract.CENSUS_2010);
            if (censusData != null) {
                String val = censusData.getCongressionalDistrict();
                if (StringUtils.isNotBlank(val) && val.length() == 4)
                    val = val.substring(2);
                result.setTractEstCongressDist(val);
            }
        }

        if (result.getTractEstCongressDist() == null)
            result.setTractEstCongressDist(TRACT_EST_CONGRESS_DIST_UNK_C);

        return result;
    }
}
