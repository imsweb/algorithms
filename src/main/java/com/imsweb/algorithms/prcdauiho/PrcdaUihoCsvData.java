/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReaderBuilder;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.ENTIRE_STATE_NON_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.ENTIRE_STATE_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.MIXED_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_YES;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_NONE;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.isCountyAtDxValid;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.isStateAtDxValid;

/**
 * The purpose of this class is to get the PRCDA, UIHO, and UIHO facility for the provided
 * state of dx and county of dx from the csv file lookup.  This implementation is memory
 * consumer. If there is a database, it is better to use another implementation.
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public class PrcdaUihoCsvData implements PrcdaUihoDataProvider {

    @Override
    public String getPRCDA(String state, String county) {

        if (!isStateAtDxValid(state))
            return PRCDA_UNKNOWN;
        else if (ENTIRE_STATE_PRCDA.contains(state))
            return PRCDA_YES;
        else if (ENTIRE_STATE_NON_PRCDA.contains(state))
            return PRCDA_NO;
        else if (!isCountyAtDxValid(county) || (MIXED_PRCDA.contains(state) && "999".equals(county))) {
            return PRCDA_UNKNOWN;
        }

        if (!CountryData.getInstance().isPrcdaUihoDataInitialized())
            CountryData.getInstance().initializePrcdaUihoData(loadPrcdaUihoData());

        StateData stateData = CountryData.getInstance().getPrcdaUihoData(state);
        if (stateData == null)
            return PRCDA_NO;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return PRCDA_NO;

        return countyData.getPRCDA();
    }

    @Override
    public String getUIHO(String state, String county) {

        if (!isStateAtDxValid(state) || !isCountyAtDxValid(county))
            return UIHO_UNKNOWN;

        if (!CountryData.getInstance().isPrcdaUihoDataInitialized())
            CountryData.getInstance().initializePrcdaUihoData(loadPrcdaUihoData());

        StateData stateData = CountryData.getInstance().getPrcdaUihoData(state);
        if (stateData == null)
            return UIHO_NO;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return UIHO_NO;

        return countyData.getUIHO();
    }

    @Override
    public String getUIHOFacility(String state, String county) {

        if (!isStateAtDxValid(state) || !isCountyAtDxValid(county))
            return UIHO_FACILITY_UNKNOWN;

        if (!CountryData.getInstance().isPrcdaUihoDataInitialized())
            CountryData.getInstance().initializePrcdaUihoData(loadPrcdaUihoData());

        StateData stateData = CountryData.getInstance().getPrcdaUihoData(state);
        if (stateData == null)
            return UIHO_FACILITY_NONE;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return UIHO_FACILITY_NONE;

        return countyData.getUIHOFacility();
    }

    @SuppressWarnings("ConstantConditions")
    private Map<String, Map<String, CountyData>> loadPrcdaUihoData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("prcdauiho/prcda-uiho-2017.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll()) {
                String state = row[0], county = row[1], prcda = row[2], uiho = row[3], uihoFacility = row[4];

                CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                dto.setPRCDA(StringUtils.leftPad(prcda, 1, '0'));
                dto.setUIHO(StringUtils.leftPad(uiho, 1, '0'));
                dto.setUIHOFacility(StringUtils.leftPad(uihoFacility, 2, '0'));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}