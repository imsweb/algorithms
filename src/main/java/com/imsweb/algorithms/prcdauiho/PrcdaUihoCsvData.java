/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

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

import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.ENTIRE_STATE_NON_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.ENTIRE_STATE_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.MIXED_PRCDA;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_YES;
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

        if (!CountryData.getInstance().isPrcdaDataInitialized())
            CountryData.getInstance().initializePrcdaData(loadPrcdaData());

        StateData stateData = CountryData.getInstance().getPrcdaData(state);
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

        if (!CountryData.getInstance().isUihoDataInitialized())
            CountryData.getInstance().initializeUihoData(loadUihoData());

        StateData stateData = CountryData.getInstance().getUihoData(state);
        if (stateData == null)
            return UIHO_NO;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return UIHO_NO;

        return countyData.getUIHO();
    }

    private Map<String, Map<String, CountyData>> loadPrcdaData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("prcdauiho/prcda20.csv")) {
            if (is == null)
                throw new IllegalStateException("Unable to find PRCDA data!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String prcda = row[2];
                    CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                    dto.setPRCDA(StringUtils.leftPad(prcda, 1, '0'));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private Map<String, Map<String, CountyData>> loadUihoData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("prcdauiho/uiho20.csv")) {
            if (is == null)
                throw new IllegalStateException("Unable to find UIHO data!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String uiho = row[2];
                    CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                    dto.setUIHO(StringUtils.leftPad(uiho, 1, '0'));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}