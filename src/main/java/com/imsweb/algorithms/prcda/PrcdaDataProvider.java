/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;
import com.imsweb.algorithms.internal.Utils;

import static com.imsweb.algorithms.StateCountyInputDto.isInvalidStateOrCounty;
import static com.imsweb.algorithms.StateCountyInputDto.isUnknownStateOrCounty;
import static com.imsweb.algorithms.prcda.PrcdaUtils.ENTIRE_STATE_NON_PRCDA;
import static com.imsweb.algorithms.prcda.PrcdaUtils.ENTIRE_STATE_PRCDA;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_YES;

/**
 * The purpose of this class is to get the PRCDA for the provided
 * state of dx and county of dx from the csv file lookup.  This implementation is memory
 * consumer. If there is a database, it is better to use another implementation.
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public class PrcdaDataProvider {

    public String getPrcda(String state, String county) {
        if (ENTIRE_STATE_PRCDA.contains(state))
            return PRCDA_YES;
        else if (ENTIRE_STATE_NON_PRCDA.contains(state))
            return PRCDA_NO;
        else if (isInvalidStateOrCounty(state, county) || isUnknownStateOrCounty(state, county) || "000".equals(county))
            return PRCDA_UNKNOWN;

        if (!CountryData.getInstance().isPrcdaDataInitialized())
            CountryData.getInstance().initializePrcdaData(loadPrcdaData());

        StateData stateData = CountryData.getInstance().getPrcdaData(state);
        if (stateData == null)
            return PRCDA_NO;

        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return PRCDA_NO;

        return countyData.getPrcda();
    }

    private Map<String, Map<String, CountyData>> loadPrcdaData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();

        Utils.processInternalFile("prcda/prcda.csv", line -> {

            String state = line.getField(0);
            String county = line.getField(1);
            String prcda2024 = line.getField(2);
            CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
            dto.setPrcda(StringUtils.leftPad(prcda2024, 1, '0'));
        });

        return result;
    }
}