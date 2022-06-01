/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ephtsubcounty;

/**
 * This class can be used to calculate EPHT 2010 GEO ID 5K and EPHT 2010 GEO ID 20K.
 */
public final class EphtSubCountyUtils {

    public static final String ALG_NAME = "NPCR EPHT SubCounty";
    public static final String ALG_VERSION = "version 1.0 released in August 2021";

    public static final String EPHT_2010_GEO_ID_UNK_A = "A";
    public static final String EPHT_2010_GEO_ID_UNK_C = "C";
    public static final String EPHT_2010_GEO_ID_UNK_D = "D";

    private static final EphtSubCountyDataProvider _PROVIDER = new EphtSubCountyDataProvider();

    private EphtSubCountyUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates the EPHT 2010 GEO ID 5K and EPHT 2010 GEO ID 20K for the provided input DTO
     * <br/><br/>
     * The provided input dto has the following parameters used in the calculation:
     * <ul>
     * <li>addressAtDxState (#80)</li>
     * <li>countyAtDxAnalysis (#89)</li>
     * <li>censusTract2010 (#135)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * The returned dto will have either an 11-digit value for EPHT 2010 GEO ID 5K and EPHT 2010 GEO ID 20K, or one of the possible unknown values:
     * <ul>
     * <li>A = State, county, or tract are invalid</li>
     * <li>B = State and tract are valid, but county was not reported</li>
     * <li>C = State + county + tract combination was not found</li>
     * <li>D = State, county, or tract are blank or unknown/li>
     * </ul>
     * <br/><br/>
     * @param input a <code>EphtSubCountyInputDto</code> input object
     * @return a <code>EphtSubCountyOutputDto</code> object
     */
    public static EphtSubCountyOutputDto computeEphtSubCounty(EphtSubCountyInputDto input) {
        EphtSubCountyOutputDto result = new EphtSubCountyOutputDto();
        input.applyRecodes();

        if (!input.isStateValidOrMissingOrUnknown() || !input.isCountyValidOrMissingOrUnknown() || !input.isCensusTract2010ValidOrMissingOrUnknown()) {
            result.setEpht2010GeoId5k(EPHT_2010_GEO_ID_UNK_A);
            result.setEpht2010GeoId20k(EPHT_2010_GEO_ID_UNK_A);
        }
        else if (input.isStateMissingOrUnknown() || input.isCountyMissingOrUnknown() || input.isCensusTract2010MissingOrUnknown()) {
            result.setEpht2010GeoId5k(EPHT_2010_GEO_ID_UNK_D);
            result.setEpht2010GeoId20k(EPHT_2010_GEO_ID_UNK_D);
        }
        else if ("000".equals(input.getCountyAtDxAnalysis())) {
            result.setEpht2010GeoId5k("B");
            result.setEpht2010GeoId20k("B");
        }
        else {
            result.setEpht2010GeoId5k(_PROVIDER.getEPHT2010GeoId5k(input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010()));
            result.setEpht2010GeoId20k(_PROVIDER.getEPHT2010GeoId20k(input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010()));
        }
        if (result.getEpht2010GeoId5k() == null)
            result.setEpht2010GeoId5k(EPHT_2010_GEO_ID_UNK_C);
        if (result.getEpht2010GeoId20k() == null)
            result.setEpht2010GeoId20k(EPHT_2010_GEO_ID_UNK_C);

        return result;
    }
}
