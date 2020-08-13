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

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN;

/**
 * The purpose of this class is to get the poverty indicator for provided year category, state of dx, county of dx, and census tract
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * Created on Oct 18, 2013 by bekeles
 * @author bekeles
 */
public class CensusTractPovertyIndicatorCsvData implements CensusTractPovertyIndicatorDataProvider {

    @Override
    public String getPovertyIndicator(String yearCategory, String state, String county, String census) {

        // make sure we have enough information to lookup the value
        if (yearCategory == null || state == null || county == null || census == null)
            return POVERTY_INDICATOR_UNKNOWN;

        // lazily initialize the data
        if (!CountryData.getInstance().isPovertyDataInitialized()) {
            Map<String, Map<String, Map<String, CensusData>>> data = new HashMap<>();
            // note that the year range in the filename is not always the same as the year range represented by the category...
            readCsvData("poverty-indicator-1995-2004.csv", YEAR_CATEGORY_1, data);
            readCsvData("poverty-indicator-2005-2007.csv", YEAR_CATEGORY_2, data);
            readCsvData("poverty-indicator-2006-2010.csv", YEAR_CATEGORY_3, data);
            readCsvData("poverty-indicator-2007-2011.csv", YEAR_CATEGORY_4, data);
            readCsvData("poverty-indicator-2008-2012.csv", YEAR_CATEGORY_5, data);
            readCsvData("poverty-indicator-2009-2013.csv", YEAR_CATEGORY_6, data);
            readCsvData("poverty-indicator-2010-2014.csv", YEAR_CATEGORY_7, data);
            readCsvData("poverty-indicator-2011-2015.csv", YEAR_CATEGORY_8, data);
            readCsvData("poverty-indicator-2012-2016.csv", YEAR_CATEGORY_9, data);
            readCsvData("poverty-indicator-2013-2017.csv", YEAR_CATEGORY_10, data);
            readCsvData("poverty-indicator-2014-2018.csv", YEAR_CATEGORY_11, data);
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

        return povertyData.getOrDefault(yearCategory, POVERTY_INDICATOR_UNKNOWN);
    }

    // helper to handle a single CSV data file
    @SuppressWarnings("ConstantConditions")
    private static void readCsvData(String datafile, String yearRangeCategory, Map<String, Map<String, Map<String, CensusData>>> data) {
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("censustractpovertyindicator/" + datafile), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll()) {
                String state = row[0], county = row[1], tract = row[2], indicator = row[3];
                CensusData dto = data.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                if (dto.getPovertyIndicators() == null)
                    dto.setPovertyIndicators(new HashMap<>());
                dto.getPovertyIndicators().putIfAbsent(yearRangeCategory, indicator);
            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}