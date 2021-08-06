/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

public interface TractEstCongressDistDataProvider {

    /**
     * Returns Tract Estimated Congressional Districts code for provided state of dx, county of dx for analysis, and census tract.
     * <p/>
     * @param state state at DX
     * @param county county at DX for analysis
     * @param censusTract census tract
     * @return the corresponding Tract Estimated Congressional Districts code
     */
    String getTractEstCongressDist(String state, String county, String censusTract);
}
