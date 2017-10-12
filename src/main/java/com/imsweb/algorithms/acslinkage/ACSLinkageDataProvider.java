/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.acslinkage;

import org.apache.commons.lang3.StringUtils;

/**
 * The purpose of the <code>ACSLinkageDataProvider</code> is to get the data for provided year range, state
 * of dx, county of dx, and census tract either from the database or csv lookup based on the implementation
 * <p/>
 * Created on Oct 12, 2017 by howew
 * @author howew
 */
public interface ACSLinkageDataProvider {

    enum Range { ACS_2007_2011, ACS_2011_2015 }

    /**
     * Returns ACS data for provided range, state of dx, county of dx, and census tract.
     * <p/>
     * Created Oct 12, 2017 by howew
     * @param range range of data to retrieve
     * @param state state at DX
     * @param county county at DX
     * @param censusTract census tract (2000 or 2010)
     * @return the corresponding ACS Data
     */
    String getACSData(Range range, String state, String county, String censusTract);

    /**
     * Returns the unknown value for the specified range.
     * <p/>
     * Created Oct 12, 2017 by howew
     * @param range range of data to retrieve
     * @return the unknown value for the range
     */
    static String getUnknownValueForRange(Range range) {
        if (range == Range.ACS_2007_2011) { return StringUtils.leftPad("",350); }
        else if (range == Range.ACS_2011_2015) { return StringUtils.leftPad("",440); }
        return "";
    }

}
