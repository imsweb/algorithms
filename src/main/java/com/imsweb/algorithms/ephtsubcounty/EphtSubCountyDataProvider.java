/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ephtsubcounty;

/**
 * The purpose of the <code>EphtSubCountyDataProvider</code> is to get EPHT 2010 GEO ID 5K and EPHT 2010 GEO ID 20K for
 * the provided state of dx, county of dx, and census tract 2010 either from the database or csv lookup based on the
 * implementation
 * <p/>
 * Created Aug 3, 2021 by kirbyk
 * @author kirbyk
 */
public interface EphtSubCountyDataProvider {

    /**
     * Returns EPHT 2010 GEO ID 5K for provided state of dx, county of dx, and census tract 2010.
     * <p/>
     * Created Aug 3, 2021 by kirbyk
     * @param state state at DX
     * @param county county at DX
     * @param censusTract census tract 2010
     * @return the corresponding EPHT 2010 GEO ID 5K value
     */
    String getEPHT2010GeoId5k(String state, String county, String censusTract);

    /**
     * Returns EPHT 2010 GEO ID 20K for provided state of dx, county of dx, and census tract 2010.
     * <p/>
     * Created Aug 3, 2021 by kirbyk
     * @param state state at DX
     * @param county county at DX
     * @param censusTract census tract 2010
     * @return the corresponding EPHT 2010 GEO ID 20K value
     */
    String getEPHT2010GeoId20k(String state, String county, String censusTract);
}
