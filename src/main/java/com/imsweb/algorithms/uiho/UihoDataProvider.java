/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.uiho;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.StateCountyInputDto.isInvalidStateOrCounty;
import static com.imsweb.algorithms.StateCountyInputDto.isUnknownStateOrCounty;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_CITY_NONE;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_CITY_UNKNOWN;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_NO;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_UNKNOWN;

/**
 * The purpose of this class is to get the UIHO and UIHO facility for the provided
 * state of dx and county of dx from the csv file lookup.  This implementation is memory
 * consumer. If there is a database, it is better to use another implementation.
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public class UihoDataProvider {

    public String getUIHO(String state, String county) {

        if (isInvalidStateOrCounty(state, county) || isUnknownStateOrCounty(state, county) || "000".equals(county))
            return UIHO_UNKNOWN;

        if (!CountryData.getInstance().isUihoDataInitialized())
            CountryData.getInstance().initializeUihoData(loadUihoData());

        StateData stateData = CountryData.getInstance().getUihoData(state);
        if (stateData == null)
            return UIHO_NO;

        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return UIHO_NO;

        return countyData.getUiho();
    }

    public String getUIHOCity(String state, String county) {

        if (isInvalidStateOrCounty(state, county) || isUnknownStateOrCounty(state, county) || "000".equals(county))
            return UIHO_CITY_UNKNOWN;

        if (!CountryData.getInstance().isUihoDataInitialized())
            CountryData.getInstance().initializeUihoData(loadUihoData());

        StateData stateData = CountryData.getInstance().getUihoData(state);
        if (stateData == null)
            return UIHO_CITY_NONE;

        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return UIHO_CITY_NONE;

        return countyData.getUihoCity();
    }

    private Map<String, Map<String, CountyData>> loadUihoData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();

        Utils.processInternalFile("uiho/uiho.csv", line -> {
            String state = line.getField(0);
            String county = line.getField(1);
            String uiho = line.getField(2);
            String city = line.getField(3);
            CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
            dto.setUiho(StringUtils.leftPad(uiho, 1, '0'));
            dto.setUihoCity(StringUtils.leftPad(city, 2, '0'));
        });

        return result;
    }
}