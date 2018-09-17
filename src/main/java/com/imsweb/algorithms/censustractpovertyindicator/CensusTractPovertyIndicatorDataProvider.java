/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

/**
 * The purpose of the <code>CensusTractPovertyIndicatorDataProvider</code> is to get the poverty indicator for provided year category, state of dx, county of dx, and census tract
 * either from the database or csv lookup based on the implementation
 * <p/>
 * Created on Oct 18, 2013 by bekeles
 * @author bekeles
 */
public interface CensusTractPovertyIndicatorDataProvider {

    String YEAR_CATEGORY_1 = "1";
    String YEAR_CATEGORY_2 = "2";
    String YEAR_CATEGORY_3 = "3";
    String YEAR_CATEGORY_4 = "4";
    String YEAR_CATEGORY_5 = "5";
    String YEAR_CATEGORY_6 = "6";
    String YEAR_CATEGORY_7 = "7";
    String YEAR_CATEGORY_8 = "8";
    String YEAR_CATEGORY_9 = "9";

    /**
     * Returns census tract poverty indicator for provided year category, state of dx, county of dx, and census tract.
     * <p/>
     * Created Oct 18, 2013 by bekeles
     * @param yearCategory year category
     * @param state state at DX
     * @param county county at DX
     * @param censusTract census tract (2000 or 2010)
     * @return the corresponding census tract poverty indicator
     */
    String getPovertyIndicator(String yearCategory, String state, String county, String censusTract);

}
