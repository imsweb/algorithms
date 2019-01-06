/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.HashMap;
import java.util.Map;

public class StateData {

    private Map<String, CountyData> _countyData = new HashMap<>();

    public CountyData getCountyData(String county) {
        return _countyData.get(county);
    }

    Map<String, CountyData> getData() {
        return _countyData;
    }
}
