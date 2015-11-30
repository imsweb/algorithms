/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

/**
 * The purpose of the <code>RuralUrbanContinuumDataProvider</code> is to:
 * <ul><li>get the rural urban continuum code for the provided year category, state of dx, and county of dx</li>
 * <li>get the rural urban commuting area code for the provided year category, state of dx, county of dx, and census tract</li>
 * <li>get the rural urban census code for the provided year category, state of dx, county of dx, and census tract</li></ul>
 * either from the database or csv lookup based on the implementation.
 * <p/>
 * Created on Aug 11, 2014 by HoweW
 * @author howew
 */
public interface RuralUrbanDataProvider {

    String BEALE_CATEGORY_1 = "1993";
    String BEALE_CATEGORY_2 = "2003";
    String BEALE_CATEGORY_3 = "2013";

    String TRACT_CATEGORY_1 = "2000";
    String TRACT_CATEGORY_2 = "2010";

    /**
     * Returns rural urban census code for provided year category, state of dx, county of dx, and census tract.
     * <p/>
     * Created Aug 11, 2014 by HoweW
     * @param state state at DX
     * @param county county at DX
     * @param tractCategory census tract
     * @return the corresponding rural urban census code
     */
    String getRuralUrbanCensus(String tractCategory, String state, String county, String censusTract);

    /**
     * Returns rural urban census code for provided year category, state of dx, county of dx, and census tract.
     * <p/>
     * Created Feb 28, 2015 by depryf
     * @param state state at DX
     * @param county county at DX
     * @param tractCategory census tract
     * @return the corresponding rural urban census percentage
     */
    Float getRuralUrbanCensusPercentage(String tractCategory, String state, String county, String censusTract);

    /**
     * Returns rural urban commuting area code for provided year category, state of dx, county of dx, and census tract.
     * <p/>
     * Created Aug 11, 2014 by HoweW
     * @param state state at DX
     * @param county county at DX
     * @param tractCategory census tract
     * @return the corresponding rural urban commuting area code
     */
    String getRuralUrbanCommutingArea(String tractCategory, String state, String county, String censusTract);

    /**
     * Returns rural urban continuum code for provided year category, state of dx, and county of dx.
     * <p/>
     * Created Aug 11, 2014 by HoweW
     * @param bealeCategory year of beale
     * @param state state at DX
     * @param county county at DX
     * @return the corresponding rural urban continuum code
     */
    String getRuralUrbanContinuum(String bealeCategory, String state, String county);
}
