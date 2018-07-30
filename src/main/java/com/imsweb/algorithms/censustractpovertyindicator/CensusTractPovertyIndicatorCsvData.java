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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.opencsv.CSVReaderBuilder;

import static com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN;

/**
 * The purpose of this class is to get the poverty indicator for provided year category, state of dx, county of dx, and census tract
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * Created on Oct 18, 2013 by bekeles
 * @author bekeles
 */
public class CensusTractPovertyIndicatorCsvData implements CensusTractPovertyIndicatorDataProvider {

    // keys are combination of state/county/census; values are maps where keys are the year range categories and the values are the resulting indicator
    private static Map<DataKey, Map<String, String>> _POVERTY_INDICATOR_LOOKUP = new ConcurrentHashMap<>();

    @Override
    public String getPovertyIndicator(String yearCategory, String state, String county, String census) {

        // make sure we have enough information to lookup the value
        if (yearCategory == null || state == null || county == null || census == null)
            return POVERTY_INDICATOR_UNKNOWN;

        // lazily initialize the data (it's not 100% tread-safe, but the worst that can happen is that we read the same data twice)
        if (_POVERTY_INDICATOR_LOOKUP.isEmpty()) {
            // note that the year range in the filename is not always the same as the year range represented by the category...
            readCsvData("poverty-indicator-1995-2004.csv", YEAR_CATEGORY_1, _POVERTY_INDICATOR_LOOKUP);
            readCsvData("poverty-indicator-2005-2007.csv", YEAR_CATEGORY_2, _POVERTY_INDICATOR_LOOKUP);
            readCsvData("poverty-indicator-2006-2010.csv", YEAR_CATEGORY_3, _POVERTY_INDICATOR_LOOKUP);
            readCsvData("poverty-indicator-2007-2011.csv", YEAR_CATEGORY_4, _POVERTY_INDICATOR_LOOKUP);
            readCsvData("poverty-indicator-2008-2012.csv", YEAR_CATEGORY_5, _POVERTY_INDICATOR_LOOKUP);
            readCsvData("poverty-indicator-2009-2013.csv", YEAR_CATEGORY_6, _POVERTY_INDICATOR_LOOKUP);
            readCsvData("poverty-indicator-2010-2014.csv", YEAR_CATEGORY_7, _POVERTY_INDICATOR_LOOKUP);
            readCsvData("poverty-indicator-2011-2015.csv", YEAR_CATEGORY_8, _POVERTY_INDICATOR_LOOKUP);
        }

        // lookup and return the resulting value
        return _POVERTY_INDICATOR_LOOKUP.computeIfAbsent(new DataKey(state, county, census), k -> new HashMap<>()).computeIfAbsent(yearCategory, k -> POVERTY_INDICATOR_UNKNOWN);
    }

    // helper to handle a single CSV data file
    private static void readCsvData(String datafile, String yearRangeCategory, Map<DataKey, Map<String, String>> lookup) {
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("censustractpovertyindicator/" + datafile), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll())
                lookup.computeIfAbsent(new DataKey(row[0], row[1], row[2]), k -> new HashMap<>()).put(yearRangeCategory, row[3]);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // helper to wrap a csv data key (all the files use the same key structure)
    private static class DataKey {

        private String _state;
        private String _county;
        private String _census;

        public DataKey(String state, String county, String census) {
            _state = state;
            _county = county;
            _census = census;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataKey that = (DataKey)o;
            return Objects.equals(_state, that._state) &&
                    Objects.equals(_county, that._county) &&
                    Objects.equals(_census, that._census);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_state, _county, _census);
        }
    }
}