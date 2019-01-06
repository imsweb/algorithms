package com.imsweb.algorithms.internal;

import java.util.HashMap;
import java.util.Map;

public class CountyData {

    private String _urbanContinuum1993;
    private String _urbanContinuum2003;
    private String _urbanContinuum2013;

    private Map<String, CensusData> _censusData = new HashMap<>();

    public String getUrbanContinuum1993() {
        return _urbanContinuum1993;
    }

    public void setUrbanContinuum1993(String urbanContinuum1993) {
        _urbanContinuum1993 = urbanContinuum1993;
    }

    public String getUrbanContinuum2003() {
        return _urbanContinuum2003;
    }

    public void setUrbanContinuum2003(String urbanContinuum2003) {
        _urbanContinuum2003 = urbanContinuum2003;
    }

    public String getUrbanContinuum2013() {
        return _urbanContinuum2013;
    }

    public void setUrbanContinuum2013(String urbanContinuum2013) {
        _urbanContinuum2013 = urbanContinuum2013;
    }

    public CensusData getCensusData(String census) {
        return _censusData.get(census);
    }

    Map<String, CensusData> getData() {
        return _censusData;
    }
}
