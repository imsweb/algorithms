/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URBAN_RURAL_INDICATOR_CODE_UNKNOWN;

/**
 * The purpose of this class is to get the rural urban continuum for the provided year category, state of dx, and county of dx
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * <br/><br/>
 * The data used by this provider comes from the official census website. See the following SEER website for more information:
 * <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/">View documentation</a>
 * In particular, the rural/urban percentage comes from these sections:
 * 2010: <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/#10">View documentation</a>
 * 2000: <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/#ca2000">View documentation</a>
 * <br/><br/>
 * Created on Aug 12, 2014 by HoweW
 * @author howew
 */
public class RuralUrbanDataProvider {

    public String getUrbanRuralIndicatorCode(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;

        // 2010 comes from the SEER tract data, 2000 comes from specific files...
        if (!CountryData.getInstance().isTractDataInitialized(state))
            CountryData.getInstance().initializeTractData(state);

        StateData stateData = CountryData.getInstance().getTractData(state);
        if (stateData == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;

        String result = null;
        switch (tractCategory) {
            case RuralUrbanUtils.TRACT_CATEGORY_2000:
                result = censusData.getIndicatorCode2000();
                break;
            case RuralUrbanUtils.TRACT_CATEGORY_2010:
                result = censusData.getIndicatorCode2010();
                break;
            case RuralUrbanUtils.TRACT_CATEGORY_2020:
                result = censusData.getIndicatorCode2020();
                break;
        }

        return result == null ? URBAN_RURAL_INDICATOR_CODE_UNKNOWN : result;
    }

    public String getRuralUrbanCommutingArea(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;

        // 2010 comes from the SEER tract data, 2000 comes from specific files...
        if (!CountryData.getInstance().isTractDataInitialized(state))
            CountryData.getInstance().initializeTractData(state);

        StateData stateData = CountryData.getInstance().getTractData(state);
        if (stateData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;

        String result = null;
        switch (tractCategory) {
            case RuralUrbanUtils.TRACT_CATEGORY_2000:
                result = censusData.getCommutingArea2000();
                break;
            case RuralUrbanUtils.TRACT_CATEGORY_2010:
                result = censusData.getCommutingArea2010();
                break;
            case RuralUrbanUtils.TRACT_CATEGORY_2020:
                result = censusData.getCommutingArea2020();
                break;
        }

        return result == null ? RURAL_URBAN_COMMUTING_AREA_UNKNOWN : result;
    }

    public String getRuralUrbanContinuum(String bealeCategory, String state, String county) {
        if (bealeCategory == null || state == null || county == null)
            return RURAL_URBAN_CONTINUUM_UNKNOWN;

        if (!CountryData.getInstance().isContinuumDataInitialized(state))
            CountryData.getInstance().initializeContinuumData(state, loadRuralUrbanContinuumData());

        StateData stateData = CountryData.getInstance().getContinuumStateData(state);
        if (stateData == null)
            return RURAL_URBAN_CONTINUUM_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return RURAL_URBAN_CONTINUUM_UNKNOWN;

        String result;
        switch (bealeCategory) {
            case RuralUrbanUtils.BEALE_CATEGORY_1993:
                result = countyData.getUrbanContinuum1993();
                break;
            case RuralUrbanUtils.BEALE_CATEGORY_2003:
                result = countyData.getUrbanContinuum2003();
                break;
            case RuralUrbanUtils.BEALE_CATEGORY_2013:
                result = countyData.getUrbanContinuum2013();
                break;
            default:
                throw new IllegalStateException("Invalid beale category: " + bealeCategory);
        }

        return result == null ? RURAL_URBAN_CONTINUUM_UNKNOWN : result;
    }

    private Map<String, Map<String, CountyData>> loadRuralUrbanContinuumData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();

        // load 1993 data
        Utils.processInternalFile("ruralurban/rural-urban-continuum-1993.csv", line -> {
            String state = line.getField(0);
            String county = line.getField(1);
            String val = line.getField(2);

            CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
            dto.setUrbanContinuum1993(StringUtils.leftPad(val, 2, '0'));
        });

        // load 2003 data
        Utils.processInternalFile("ruralurban/rural-urban-continuum-2003.csv", line -> {
            String state = line.getField(0);
            String county = line.getField(1);
            String val = line.getField(2);

            CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
            dto.setUrbanContinuum2003(StringUtils.leftPad(val, 2, '0'));
        });

        // load 2013 data
        Utils.processInternalFile("ruralurban/rural-urban-continuum-2013.csv", line -> {
            String state = line.getField(0);
            String county = line.getField(1);
            String val = line.getField(2);

            CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
            dto.setUrbanContinuum2013(StringUtils.leftPad(val, 2, '0'));
        });

        return result;
    }
}