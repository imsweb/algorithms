/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.acslinkage;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN;

/**
 * The purpose of this class is to get the ACS Data for the provided year range, state of dx, county of dx, and census tract
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * Created on Oct 12, 2017 by howew
 * @author howew
 */
public class ACSLinkageCsvData implements ACSLinkageDataProvider {

    // keys are combination of state/county/census; values are maps where keys are the year range categories and the values are the resulting indicator
    private static Map<DataKey, Map<Range, String>> _ACS_DATA_LOOKUP = new ConcurrentHashMap<>();

    @Override
    public String getACSData(Range range, String state, String county, String census) {

        // make sure we have enough information to lookup the value
        if (state == null || county == null || census == null)
            return ACSLinkageDataProvider.getUnknownValueForRange(range);

        // lazily initialize the data (it's not 100% tread-safe, but the worst that can happen is that we read the same data twice)
        if (_ACS_DATA_LOOKUP.isEmpty()) {
            // note that the year range in the filename is not always the same as the year range represented by the category...
            readCsvData("acs-2007-2011.csv", Range.ACS_2007_2011, _ACS_DATA_LOOKUP);
            readCsvData("acs-2011-2015.csv", Range.ACS_2011_2015, _ACS_DATA_LOOKUP);
        }

        // lookup and return the resulting value
        return _ACS_DATA_LOOKUP.computeIfAbsent(new DataKey(state, county, census), k -> new HashMap<>()).computeIfAbsent(range, k -> POVERTY_INDICATOR_UNKNOWN);
    }

    // helper to handle a single CSV data file
    private static void readCsvData(String datafile, Range range, Map<DataKey, Map<Range, String>> lookup) {
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("acslinkage/" + datafile), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll())
                lookup.computeIfAbsent(new DataKey(row[0], row[1], row[2]), k -> new HashMap<>()).put(range, row[3]);
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