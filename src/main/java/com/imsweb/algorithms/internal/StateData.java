/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * State-related data.
 */
public class StateData {

    // counties, map by county code
    private Map<String, CountyData> _countyData = new HashMap<>();

    /**
     * Returns the county data for a given county, null if not found.
     */
    public CountyData getCountyData(String county) {
        return _countyData.get(county);
    }

    /**
     * Returns all the data; package private so it's only used during initialization.
     */
    Map<String, CountyData> getData() {
        return _countyData;
    }
}
