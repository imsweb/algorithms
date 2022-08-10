/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.uiho;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_CITY_NONE;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_CITY_UNKNOWN;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_NO;
import static com.imsweb.algorithms.uiho.UihoUtils.UIHO_UNKNOWN;
import static com.imsweb.algorithms.uiho.UihoUtils.isCountyAtDxValid;
import static com.imsweb.algorithms.uiho.UihoUtils.isStateAtDxValid;

/**
 * The purpose of this class is to get the PRCDA, UIHO, and UIHO facility for the provided
 * state of dx and county of dx from the csv file lookup.  This implementation is memory
 * consumer. If there is a database, it is better to use another implementation.
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public class UihoDataProvider {

    public String getUIHO(String state, String county) {

        if (!isStateAtDxValid(state) || !isCountyAtDxValid(county))
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

        if (!isStateAtDxValid(state) || !isCountyAtDxValid(county))
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
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("uiho/uiho.csv")) {
            if (is == null)
                throw new IllegalStateException("Unable to find UIHO data!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String uiho = row[2];
                    String city = row[3];
                    CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                    dto.setUiho(StringUtils.leftPad(uiho, 1, '0'));
                    dto.setUihoCity(StringUtils.leftPad(city, 2, '0'));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}