/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.util.Map;

import static com.imsweb.algorithms.ruralurban.RuralUrbanDataProvider.TRACT_CATEGORY_1;
import static com.imsweb.algorithms.ruralurban.RuralUrbanDataProvider.TRACT_CATEGORY_2;

/**
 * This class can be used to calculate the rural urban census code, the rural urban commuting area code, and the rural urban continuum code.
 * Created on Aug 12, 2014 by HoweW
 */
public final class RuralUrbanUtils {

    public static final String ALG_NAME = "NAACCR Rural Urban Program";
    public static final String ALG_VERSION = "2018";
    public static final String ALG_INFO = "NAACCR Rural Urban Program released in August 2018";

    //NAACCR Items Used for calculation
    public static final String PROP_STATE_DX = "addressAtDxState";
    public static final String PROP_COUNTY_DX = "addressAtDxCounty";
    public static final String PROP_CENSUS_TRACT_2000 = "censusTract2000";
    public static final String PROP_CENSUS_TRACT_2010 = "censusTract2010";

    //Unknown values for each code
    public static final String URBAN_RURAL_INDICATOR_CODE_UNKNOWN = "C";
    public static final String RURAL_URBAN_COMMUTING_AREA_UNKNOWN = "C";
    public static final String RURAL_URBAN_CONTINUUM_UNKNOWN = "98";

    private static RuralUrbanDataProvider _PROVIDER;

    /**
     * Calculates the urban rural indicator code (uric2000, uric2010) for the provided record.
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
     * <li>censusTract2000 (#130)</li>
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
     * <li>A = Invalid state, county code, or census tract</li>
     * <li>B = Insufficient population data for county to determine census code</li>
     * <li>C = Valid state/county/tract combination but census code not found</li>
     * <li>D = Missing or unknown state, county, or census tract</li>
     * </ul>
     * <br/><br/>
     * @param record a map of properties representing a NAACCR line
     * @return the computed rural urban census value
     */
    public static RuralUrbanOutputDto computeUrbanRuralIndicatorCode(Map<String, String> record) {
        RuralUrbanInputDto input = new RuralUrbanInputDto();
        input.setAddressAtDxState(record.get(PROP_STATE_DX));
        input.setAddressAtDxCounty(record.get(PROP_COUNTY_DX));
        input.setCensusTract2000(record.get(PROP_CENSUS_TRACT_2000));
        input.setCensusTract2010(record.get(PROP_CENSUS_TRACT_2010));
        return computeUrbanRuralIndicatorCode(input);
    }

    /**
     * Calculates the urban rural indicator code (uric2000, uric2010) for the provided input DTO
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
     * <li>censusTract2000 (#130)</li>
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
     * <li>A = Invalid state, county code, or census tract</li>
     * <li>B = Insufficient population data for county to determine census code</li>
     * <li>C = Valid state/county/tract combination but census code not found</li>
     * <li>D = Missing or unknown state, county, or census tract</li>
     * </ul>
     * <br/><br/>
     * @param input a <code>RuralUrbanContinuumInputDto</code> input object
     * @return the computed rural urban census value
     */
    public static RuralUrbanOutputDto computeUrbanRuralIndicatorCode(RuralUrbanInputDto input) {
        RuralUrbanOutputDto result = new RuralUrbanOutputDto();

        input.applyRecodes();

        // 2000
        if (!input.isStateAtDxValidOrMissingOrUnknown() || !input.isCountyAtDxValidOrMissingOrUnknown() || !input.isCensusTract2000ValidOrMissingOrUnknown())
            result.setUrbanRuralIndicatorCode2000("A");
        else if (input.isStateAtDxMissingOrUnknown() || input.isCountyAtDxMissingOrUnknown() || input.isCensusTract2000MissingOrUnknown())
            result.setUrbanRuralIndicatorCode2000("D");
        else if ("000".equals(input.getAddressAtDxCounty()))
            result.setUrbanRuralIndicatorCode2000("B");
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            result.setUrbanRuralIndicatorCode2000(_PROVIDER.getUrbanRuralIndicatorCode(TRACT_CATEGORY_1, input.getAddressAtDxState(), input.getAddressAtDxCounty(), input.getCensusTract2000()));
            result.setUrbanRuralIndicatorCode2000Percentage(_PROVIDER.getRuralUrbanCensusPercentage(TRACT_CATEGORY_1, input.getAddressAtDxState(), input.getAddressAtDxCounty(), input.getCensusTract2000()));
        }
        if (result.getUrbanRuralIndicatorCode2000() == null)
            result.setUrbanRuralIndicatorCode2000(URBAN_RURAL_INDICATOR_CODE_UNKNOWN);

        // 2010
        if (!input.isStateAtDxValidOrMissingOrUnknown() || !input.isCountyAtDxValidOrMissingOrUnknown() || !input.isCensusTract2010ValidOrMissingOrUnknown())
            result.setUrbanRuralIndicatorCode2010("A");
        else if (input.isStateAtDxMissingOrUnknown() || input.isCountyAtDxMissingOrUnknown() || input.isCensusTract2010MissingOrUnknown())
            result.setUrbanRuralIndicatorCode2010("D");
        else if ("000".equals(input.getAddressAtDxCounty()))
            result.setUrbanRuralIndicatorCode2010("B");
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            result.setUrbanRuralIndicatorCode2010(_PROVIDER.getUrbanRuralIndicatorCode(TRACT_CATEGORY_2, input.getAddressAtDxState(), input.getAddressAtDxCounty(), input.getCensusTract2010()));
            result.setUrbanRuralIndicatorCode2010Percentage(_PROVIDER.getRuralUrbanCensusPercentage(TRACT_CATEGORY_2, input.getAddressAtDxState(), input.getAddressAtDxCounty(), input.getCensusTract2010()));
        }
        if (result.getUrbanRuralIndicatorCode2010() == null)
            result.setUrbanRuralIndicatorCode2010(URBAN_RURAL_INDICATOR_CODE_UNKNOWN);

        return result;
    }

    /**
     * Calculates the rural urban commuting area (ruca2000, ruca2010) for the provided record.
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
     * <li>censusTract2000 (#130)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned urban rural commuting area will have the following values depending on the census tract (2000, or 2010):
     * <ul>
     * <li>1 = Urban commuting area - RUCA codes 1.0, 1.1, 2.0, 2.1, 3.0, 4.1, 5.1, 7.1, 8.1, and 10.1</li>
     * <li>2 = Not an urban commuting area - all other RUCA codes except 99</li>
     * <li>9 = RUCA code is 99</li>
     * <li>A = Invalid state, county, or census tract</li>
     * <li>B = Insufficient population data for county to determine RUCA code</li>
     * <li>C = Valid state/county/tract combination but RUCA code not found</li>
     * <li>D = Missing or unknown state, county, or census tract</li>
     * </ul>
     * <br/><br/>
     * @param record a map of properties representing a NAACCR line
     * @return the computed rural urban commuting area value
     */
    public static RuralUrbanOutputDto computeRuralUrbanCommutingArea(Map<String, String> record) {
        RuralUrbanInputDto input = new RuralUrbanInputDto();
        input.setAddressAtDxState(record.get(PROP_STATE_DX));
        input.setAddressAtDxCounty(record.get(PROP_COUNTY_DX));
        input.setCensusTract2000(record.get(PROP_CENSUS_TRACT_2000));
        input.setCensusTract2010(record.get(PROP_CENSUS_TRACT_2010));
        return computeRuralUrbanCommutingArea(input);
    }

    /**
     * Calculates the rural urban commuting area (ruca2000, ruca2010) for the provided input DTO.
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
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
     * <li>A = Invalid state, county, or census tract</li>
     * <li>B = Insufficient population data for county to determine RUCA code</li>
     * <li>C = Valid state/county/tract combination but RUCA code not found</li>
     * <li>D = Missing or unknown state, county, or census tract</li>
     * </ul>
     * <br/><br/>
     * @param input a <code>RuralUrbanContinuumInputDto</code> input object
     * @return the computed rural urban commuting area value
     */
    public static RuralUrbanOutputDto computeRuralUrbanCommutingArea(RuralUrbanInputDto input) {
        RuralUrbanOutputDto result = new RuralUrbanOutputDto();

        input.applyRecodes();

        // 2000
        if (!input.isStateAtDxValidOrMissingOrUnknown() || !input.isCountyAtDxValidOrMissingOrUnknown() || !input.isCensusTract2000ValidOrMissingOrUnknown())
            result.setRuralUrbanCommutingArea2000("A");
        else if (input.isStateAtDxMissingOrUnknown() || input.isCountyAtDxMissingOrUnknown() || input.isCensusTract2000MissingOrUnknown())
            result.setRuralUrbanCommutingArea2000("D");
        else if ("000".equals(input.getAddressAtDxCounty()))
            result.setRuralUrbanCommutingArea2000("B");
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            result.setRuralUrbanCommutingArea2000(_PROVIDER.getRuralUrbanCommutingArea(TRACT_CATEGORY_1, input.getAddressAtDxState(), input.getAddressAtDxCounty(), input.getCensusTract2000()));
        }
        if (result.getRuralUrbanCommutingArea2000() == null)
            result.setRuralUrbanCommutingArea2000(RURAL_URBAN_COMMUTING_AREA_UNKNOWN);

        // 2010
        if (!input.isStateAtDxValidOrMissingOrUnknown() || !input.isCountyAtDxValidOrMissingOrUnknown() || !input.isCensusTract2010ValidOrMissingOrUnknown())
            result.setRuralUrbanCommutingArea2010("A");
        else if (input.isStateAtDxMissingOrUnknown() || input.isCountyAtDxMissingOrUnknown() || input.isCensusTract2010MissingOrUnknown())
            result.setRuralUrbanCommutingArea2010("D");
        else if ("000".equals(input.getAddressAtDxCounty()))
            result.setRuralUrbanCommutingArea2010("B");
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();
            result.setRuralUrbanCommutingArea2010(_PROVIDER.getRuralUrbanCommutingArea(TRACT_CATEGORY_2, input.getAddressAtDxState(), input.getAddressAtDxCounty(), input.getCensusTract2010()));
        }
        if (result.getRuralUrbanCommutingArea2010() == null)
            result.setRuralUrbanCommutingArea2010(RURAL_URBAN_COMMUTING_AREA_UNKNOWN);

        return result;
    }

    /**
     * Calculates the rural urban continuum (ruralUrbanContinuum1993, ruralUrbanContinuum2003, ruralUrbanContinuum2013) for the provided record.
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
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
     * @param record a map of properties representing a NAACCR line
     * @return the computed rural urban continuum value
     */
    public static RuralUrbanOutputDto computeRuralUrbanContinuum(Map<String, String> record) {
        RuralUrbanInputDto input = new RuralUrbanInputDto();
        input.setAddressAtDxState(record.get(PROP_STATE_DX));
        input.setAddressAtDxCounty(record.get(PROP_COUNTY_DX));
        return computeRuralUrbanContinuum(input);
    }

    /**
     * Calculates the rural urban continuum (ruralUrbanContinuum1993, ruralUrbanContinuum2003, ruralUrbanContinuum2013) input DTO.
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>addressAtDxCounty (#90)</li>
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
    public static RuralUrbanOutputDto computeRuralUrbanContinuum(RuralUrbanInputDto input) {
        RuralUrbanOutputDto result = new RuralUrbanOutputDto();

        input.applyRecodes();

        if (!input.isStateAtDxValidOrMissingOrUnknown() || !input.isCountyAtDxValidOrMissingOrUnknown()) {
            result.setRuralUrbanContinuum1993("96");
            result.setRuralUrbanContinuum2003("96");
            result.setRuralUrbanContinuum2013("96");
        }
        else if (input.isStateAtDxMissingOrUnknown() || input.isCountyAtDxMissingOrUnknown()) {
            result.setRuralUrbanContinuum1993("99");
            result.setRuralUrbanContinuum2003("99");
            result.setRuralUrbanContinuum2013("99");
        }
        else if ("000".equals(input.getAddressAtDxCounty())) {
            result.setRuralUrbanContinuum1993("97");
            result.setRuralUrbanContinuum2003("97");
            result.setRuralUrbanContinuum2013("97");
        }
        else {
            if (_PROVIDER == null)
                initializeInternalDataProvider();

            result.setRuralUrbanContinuum1993(_PROVIDER.getRuralUrbanContinuum(RuralUrbanDataProvider.BEALE_CATEGORY_1, input.getAddressAtDxState(), input.getAddressAtDxCounty()));
            result.setRuralUrbanContinuum2003(_PROVIDER.getRuralUrbanContinuum(RuralUrbanDataProvider.BEALE_CATEGORY_2, input.getAddressAtDxState(), input.getAddressAtDxCounty()));
            result.setRuralUrbanContinuum2013(_PROVIDER.getRuralUrbanContinuum(RuralUrbanDataProvider.BEALE_CATEGORY_3, input.getAddressAtDxState(), input.getAddressAtDxCounty()));
        }

        if (result.getRuralUrbanContinuum1993() == null)
            result.setRuralUrbanContinuum1993(RURAL_URBAN_CONTINUUM_UNKNOWN);
        if (result.getRuralUrbanContinuum2003() == null)
            result.setRuralUrbanContinuum2003(RURAL_URBAN_CONTINUUM_UNKNOWN);
        if (result.getRuralUrbanContinuum2013() == null)
            result.setRuralUrbanContinuum2013(RURAL_URBAN_CONTINUUM_UNKNOWN);

        return result;
    }

    /**
     * Use this method to register your own data provider instead of using the internal one that is entirely in memory.
     * <br/><br/>
     * This has to be done before the first call to the compute method, or the internal one will be registered by default.
     * <br/><br/>
     * Once a provider has been set, this method cannot be called (it will throw an exception).
     * @param provider the <code>RuralUrbanDataProvider</code> to set
     */
    public static synchronized void setDataProvider(RuralUrbanDataProvider provider) {
        if (_PROVIDER != null)
            throw new RuntimeException("The data provider has already been set!");
        _PROVIDER = provider;
    }

    private static synchronized void initializeInternalDataProvider() {
        if (_PROVIDER != null)
            return;
        _PROVIDER = new RuralUrbanCsvData();
    }
}
