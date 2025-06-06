/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.StateCountyTractInputDto.CensusTract;

/**
 * This class can be used to calculate the rural urban census code, the rural urban commuting area code, and the rural urban continuum code.
 * Created on Aug 12, 2014 by HoweW
 */
public final class RuralUrbanUtils {

    public static final String ALG_NAME = "NAACCR Rural Urban Program";
    public static final String ALG_VERSION = "released in August 2019";

    // unknown values for each code
    public static final String URBAN_RURAL_INDICATOR_CODE_UNKNOWN = "C";
    public static final String RURAL_URBAN_COMMUTING_AREA_UNKNOWN = "C";
    public static final String RURAL_URBAN_CONTINUUM_UNKNOWN = "98";

    // other unknown values (there is overlap with the previous block of code, but I don't want to remove those other constants)
    // I know it's not great ot use the code in the constant name, but I just don't know how to abbreviate what they all mean!
    public static final String URIC_VAL_UNK_A = "A";
    public static final String URIC_VAL_UNK_D = "D";
    public static final String RUCA_VAL_UNK_A = "A";
    public static final String RUCA_VAL_UNK_D = "D";
    public static final String CONTINUUM_UNK_96 = "96";
    public static final String CONTINUUM_UNK_97 = "97";
    public static final String CONTINUUM_UNK_98 = "98";
    public static final String CONTINUUM_UNK_99 = "99";

    public static final String BEALE_CATEGORY_1993 = "1993";
    public static final String BEALE_CATEGORY_2003 = "2003";
    public static final String BEALE_CATEGORY_2013 = "2013";

    public static final String TRACT_CATEGORY_2000 = "2000";
    public static final String TRACT_CATEGORY_2010 = "2010";

    // data provider
    private static final RuralUrbanDataProvider _PROVIDER = new RuralUrbanDataProvider();

    private RuralUrbanUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates the urban rural indicator code (uric2000, uric2010) for the provided input DTO
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * <li>censusTract2000 (#130)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned urban rural census will have the following values depending on the census tract (2000, or 2010):
     * <ul>
     * <li>1 = All urban - the percentage of the population in an urban area is 100%</li>
     * <li>2 = Mostly urban - the percentage of the population in an urban area is between &gt;=50% and &lt;100%</li>
     * <li>3 = Mostly rural - the percentage of the population in an urban area is between &gt;0% and &lt;50%</li>
     * <li>4 = All rural - the percentage of the population in an urban area is 0%</li>
     * <li>9 = The percentage of the population in an urban or rural area is unknown</li>
     * <li>A = State, county, or tract are invalid</li>
     * <li>B = State and tract are valid, but county was not reported</li>
     * <li>C = State + county + tract combination was not found</li>
     * <li>D = State, county, or tract are blank or unknown/li>
     * </ul>
     * <br/><br/>
     * @param input a <code>RuralUrbanContinuumInputDto</code> input object
     * @return the computed rural urban census value
     */
    public static RuralUrbanOutputDto computeUrbanRuralIndicatorCode(StateCountyTractInputDto input) {
        RuralUrbanOutputDto result = new RuralUrbanOutputDto();

        input.applyRecodes();

        // 2000
        if (input.hasInvalidStateCountyOrCensusTract(CensusTract.CENSUS_2000))
            result.setUrbanRuralIndicatorCode2000("A");
        else if (input.hasUnknownStateCountyOrCensusTract(CensusTract.CENSUS_2000))
            result.setUrbanRuralIndicatorCode2000("D");
        else if (input.countyIsNotReported())
            result.setUrbanRuralIndicatorCode2000("B");
        else
            result.setUrbanRuralIndicatorCode2000(_PROVIDER.getUrbanRuralIndicatorCode(TRACT_CATEGORY_2000, input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2000()));

        if (result.getUrbanRuralIndicatorCode2000() == null)
            result.setUrbanRuralIndicatorCode2000(URBAN_RURAL_INDICATOR_CODE_UNKNOWN);

        // 2010
        if (input.hasInvalidStateCountyOrCensusTract(CensusTract.CENSUS_2010))
            result.setUrbanRuralIndicatorCode2010("A");
        else if (input.hasUnknownStateCountyOrCensusTract(CensusTract.CENSUS_2010))
            result.setUrbanRuralIndicatorCode2010("D");
        else if (input.countyIsNotReported())
            result.setUrbanRuralIndicatorCode2010("B");
        else
            result.setUrbanRuralIndicatorCode2010(_PROVIDER.getUrbanRuralIndicatorCode(TRACT_CATEGORY_2010, input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010()));

        if (result.getUrbanRuralIndicatorCode2010() == null)
            result.setUrbanRuralIndicatorCode2010(URBAN_RURAL_INDICATOR_CODE_UNKNOWN);

        return result;
    }

    /**
     * Calculates the rural urban commuting area (ruca2000, ruca2010) for the provided input DTO.
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * <li>censusTract2000 (#130)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned urban rural commuting area will have the following values depending on the year (1993, 2003, or 2013):
     * The returned urban rural commuting area will have the following values depending on the census tract (2000, or 2010):
     * <ul>
     * <li>1 = Urban commuting area - RUCA codes 1.0, 1.1, 2.0, 2.1, 3.0, 4.1, 5.1, 7.1, 8.1, and 10.1</li>
     * <li>2 = Not an urban commuting area - all other RUCA codes except 99</li>
     * <li>9 = RUCA code is 99</li>
     * <li>A = State, county, or tract are invalid</li>
     * <li>B = State and tract are valid, but county was not reported</li>
     * <li>C = State + county + tract combination was not found</li>
     * <li>D = State, county, or tract are blank or unknown</li>
     * </ul>
     * <br/><br/>
     * @param input a <code>RuralUrbanContinuumInputDto</code> input object
     * @return the computed rural urban commuting area value
     */
    public static RuralUrbanOutputDto computeRuralUrbanCommutingArea(StateCountyTractInputDto input) {
        RuralUrbanOutputDto result = new RuralUrbanOutputDto();

        input.applyRecodes();

        // 2000
        if (input.hasInvalidStateCountyOrCensusTract(CensusTract.CENSUS_2000))
            result.setRuralUrbanCommutingArea2000("A");
        else if (input.hasUnknownStateCountyOrCensusTract(CensusTract.CENSUS_2000))
            result.setRuralUrbanCommutingArea2000("D");
        else if (input.countyIsNotReported())
            result.setRuralUrbanCommutingArea2000("B");
        else
            result.setRuralUrbanCommutingArea2000(_PROVIDER.getRuralUrbanCommutingArea(TRACT_CATEGORY_2000, input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2000()));

        if (result.getRuralUrbanCommutingArea2000() == null)
            result.setRuralUrbanCommutingArea2000(RURAL_URBAN_COMMUTING_AREA_UNKNOWN);

        // 2010
        if (input.hasInvalidStateCountyOrCensusTract(CensusTract.CENSUS_2010))
            result.setRuralUrbanCommutingArea2010("A");
        else if (input.hasUnknownStateCountyOrCensusTract(CensusTract.CENSUS_2010))
            result.setRuralUrbanCommutingArea2010("D");
        else if (input.countyIsNotReported())
            result.setRuralUrbanCommutingArea2010("B");
        else
            result.setRuralUrbanCommutingArea2010(_PROVIDER.getRuralUrbanCommutingArea(TRACT_CATEGORY_2010, input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010()));

        if (result.getRuralUrbanCommutingArea2010() == null)
            result.setRuralUrbanCommutingArea2010(RURAL_URBAN_COMMUTING_AREA_UNKNOWN);

        return result;
    }

    /**
     * Calculates the rural urban continuum (ruralUrbanContinuum1993, ruralUrbanContinuum2003, ruralUrbanContinuum2013) input DTO.
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned urban rural continuum will have the following values depending on the year (1993, 2003, or 2013):
     * <ul>
     * <li>00 = Central counties of metro areas of 1 million population or more (1993 only)</li>
     * <li>01 = Counties of metro areas of 1 million population or more</li>
     * <li>02 = Counties in metro areas of 250,000 to 1 million population</li>
     * <li>03 = Counties in metro areas of fewer than 250,000 population</li>
     * <li>04 = Urban population of 20,000 or more, adjacent to a metro area</li>
     * <li>05 = Urban population of 20,000 or more, not adjacent to a metro area</li>
     * <li>06 = Urban population of 2,500 to 19,999, adjacent to a metro area</li>
     * <li>07 = Urban population of 2,500 to 19,999, not adjacent to a metro area</li>
     * <li>08 = Completely rural or less than 2,500 urban population, adjacent to a metro area</li>
     * <li>09 = Completely rural or less than 2,500 urban population, not adjacent to a metro area</li>
     * <li>96 = Invalid state abbreviation or county code</li>
     * <li>97 = Insufficient population data for county to determine rural urban code</li>
     * <li>98 = Valid state/county combination but rural urban code not found</li>
     * <li>99 = Missing or unknown state or county</li>
     * </ul>
     * <br/><br/>
     * @param input a <code>RuralUrbanContinuumInputDto</code> input object
     * @return the computed rural urban continuum value
     */
    public static RuralUrbanOutputDto computeRuralUrbanContinuum(StateCountyTractInputDto input) {
        RuralUrbanOutputDto result = new RuralUrbanOutputDto();

        input.applyRecodes();

        if (input.hasInvalidStateOrCounty()) {
            result.setRuralUrbanContinuum1993("96");
            result.setRuralUrbanContinuum2003("96");
            result.setRuralUrbanContinuum2013("96");
        }
        else if (input.hasUnknownStateOrCounty()) {
            result.setRuralUrbanContinuum1993("99");
            result.setRuralUrbanContinuum2003("99");
            result.setRuralUrbanContinuum2013("99");
        }
        else if (input.countyIsNotReported()) {
            result.setRuralUrbanContinuum1993("97");
            result.setRuralUrbanContinuum2003("97");
            result.setRuralUrbanContinuum2013("97");
        }
        else {
            result.setRuralUrbanContinuum1993(_PROVIDER.getRuralUrbanContinuum(BEALE_CATEGORY_1993, input.getAddressAtDxState(), input.getCountyAtDxAnalysis()));
            result.setRuralUrbanContinuum2003(_PROVIDER.getRuralUrbanContinuum(BEALE_CATEGORY_2003, input.getAddressAtDxState(), input.getCountyAtDxAnalysis()));
            result.setRuralUrbanContinuum2013(_PROVIDER.getRuralUrbanContinuum(BEALE_CATEGORY_2013, input.getAddressAtDxState(), input.getCountyAtDxAnalysis()));
        }

        if (result.getRuralUrbanContinuum1993() == null)
            result.setRuralUrbanContinuum1993(RURAL_URBAN_CONTINUUM_UNKNOWN);
        if (result.getRuralUrbanContinuum2003() == null)
            result.setRuralUrbanContinuum2003(RURAL_URBAN_CONTINUUM_UNKNOWN);
        if (result.getRuralUrbanContinuum2013() == null)
            result.setRuralUrbanContinuum2013(RURAL_URBAN_CONTINUUM_UNKNOWN);

        return result;
    }
}
