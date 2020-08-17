package com.imsweb.algorithms.yostacspoverty;

/**
 * The purpose of the <code>ACSLinkageDataProvider</code> is to get the data for provided year range, state
 * of dx, county of dx, and census tract either from the database or csv lookup based on the implementation
 * <p/>
 * Created on Oct 12, 2017 by howew
 * @author howew
 */
public interface YostAcsPovertyDataProvider {

    /**
     * Returns ACS data for provided range, state of dx, county of dx, and census tract.
     * <p/>
     * Created Oct 12, 2017 by howew
     * @param state state at DX
     * @param county county at DX
     * @param censusTract census tract (2000 or 2010)
     * @return the corresponding ACS Data
     */
    YostAcsPovertyOutputDto getYostAcsPovertyData(String state, String county, String censusTract);
}

