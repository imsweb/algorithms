/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN;

/**
 * Only "old years" (1995-2007) are provided by this data provider; the more recent years come from the "tract" data.
 */
public class CensusTractPovertyIndicatorDataProvider {

    private static final String _YEAR_CATEGORY_1995_2004 = "1";
    private static final String _YEAR_CATEGORY_2005_2007 = "2";

    public String getPovertyIndicator(int year, String state, String county, String census) {

        // make sure we have enough information to lookup the value
        if (state == null || county == null || census == null)
            return POVERTY_INDICATOR_UNKNOWN;

        // should not happen, this provider should not be called for "recent' years...
        if (year < 1995 || year > 2007)
            return POVERTY_INDICATOR_UNKNOWN;

        // lazily initialize the data
        if (!CountryData.getInstance().isPovertyDataInitialized()) {
            Map<String, Map<String, Map<String, CensusData>>> data = new HashMap<>();
            readCsvData("poverty-indicator-1995-2004.csv", _YEAR_CATEGORY_1995_2004, data);
            readCsvData("poverty-indicator-2005-2007.csv", _YEAR_CATEGORY_2005_2007, data);
            CountryData.getInstance().initializePovertyData(data);
        }

        StateData stateData = CountryData.getInstance().getPovertyData(state);
        if (stateData == null)
            return POVERTY_INDICATOR_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return POVERTY_INDICATOR_UNKNOWN;
        CensusData censusData = countyData.getCensusData(census);
        if (censusData == null)
            return POVERTY_INDICATOR_UNKNOWN;
        Map<String, String> povertyData = censusData.getPovertyIndicators();
        if (povertyData == null)
            return POVERTY_INDICATOR_UNKNOWN;

        return povertyData.getOrDefault(year <= 2004 ? _YEAR_CATEGORY_1995_2004 : _YEAR_CATEGORY_2005_2007, POVERTY_INDICATOR_UNKNOWN);
    }

    // helper to handle a single CSV data file
    @SuppressWarnings("ConstantConditions")
    private static void readCsvData(String datafile, String yearRangeCategory, Map<String, Map<String, Map<String, CensusData>>> data) {
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("censustractpovertyindicator/" + datafile), StandardCharsets.US_ASCII);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            for (String[] row : csvReader.readAll()) {
                String state = row[0];
                String county = row[1];
                String tract = row[2];
                String indicator = row[3];
                CensusData dto = data.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                if (dto.getPovertyIndicators() == null)
                    dto.setPovertyIndicators(new HashMap<>());
                dto.getPovertyIndicators().putIfAbsent(yearRangeCategory, indicator);
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}