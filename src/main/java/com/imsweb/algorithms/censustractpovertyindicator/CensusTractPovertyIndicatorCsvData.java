/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

/**
 * The purpose of this class is to get the poverty indicator for provided year category, state of dx, county of dx, and census tract
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * Created on Oct 18, 2013 by bekeles
 * @author bekeles
 */
public class CensusTractPovertyIndicatorCsvData implements CensusTractPovertyIndicatorDataProvider {

    private static Map<String, Short> _POVERTY_INDICATOR_LOOK_UP = new HashMap<>();

    @Override
    public String getPovertyIndicator(String yearCatagory, String state, String county, String census) {
        String povertyIndicator = CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN;

        if (yearCatagory == null || state == null || county == null || census == null)
            return povertyIndicator;

        if (_POVERTY_INDICATOR_LOOK_UP.isEmpty())
            initializeLookup();

        Short povertyIndicatorValue = _POVERTY_INDICATOR_LOOK_UP.get(state + county + census);

        if (povertyIndicatorValue != null) {
            if (CensusTractPovertyIndicatorDataProvider.YEAR_CATEGORY_1.equals(yearCatagory))
                povertyIndicator = String.valueOf(povertyIndicatorValue / 1000);
            else if (CensusTractPovertyIndicatorDataProvider.YEAR_CATEGORY_2.equals(yearCatagory))
                povertyIndicator = String.valueOf((povertyIndicatorValue / 100) % 10);
            else if (CensusTractPovertyIndicatorDataProvider.YEAR_CATEGORY_3.equals(yearCatagory))
                povertyIndicator = String.valueOf((povertyIndicatorValue / 10) % 10);
            else if (CensusTractPovertyIndicatorDataProvider.YEAR_CATEGORY_4.equals(yearCatagory))
                povertyIndicator = String.valueOf(povertyIndicatorValue % 10);
        }

        return StringUtils.isBlank(povertyIndicator) ? CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN : povertyIndicator;
    }

    private static synchronized void initializeLookup() {
        int yearRange1 = 0, yearRange2 = 1, yearRange3 = 2, yearRange4 = 3;

        Map<String, byte[]> tmp = new HashMap<>();

        try {
            Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("censustractpovertyindicator/poverty-indicator-1995-2004.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], census = row[2], val = row[3];
                byte[] values = tmp.get(state + county + census);
                if (values == null) {
                    values = new byte[4];
                    values[0] = values[1] = values[2] = values[3] = (byte)9;
                    tmp.put(state + county + census, values);
                }
                values[yearRange1] = Byte.valueOf(val);
            }
            reader.close();
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("censustractpovertyindicator/poverty-indicator-2005-2007.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], census = row[2], val = row[3];
                byte[] values = tmp.get(state + county + census);
                if (values == null) {
                    values = new byte[4];
                    values[0] = values[1] = values[2] = values[3] = (byte)9;
                    tmp.put(state + county + census, values);
                }
                values[yearRange2] = Byte.valueOf(val);
            }
            reader.close();
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("censustractpovertyindicator/poverty-indicator-2008.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], census = row[2], val = row[3];
                byte[] values = tmp.get(state + county + census);
                if (values == null) {
                    values = new byte[4];
                    values[0] = values[1] = values[2] = values[3] = (byte)9;
                    tmp.put(state + county + census, values);
                }
                values[yearRange3] = Byte.valueOf(val);
            }
            reader.close();
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("censustractpovertyindicator/poverty-indicator-2009-2011.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], census = row[2], val = row[3];
                byte[] values = tmp.get(state + county + census);
                if (values == null) {
                    values = new byte[4];
                    values[0] = values[1] = values[2] = values[3] = (byte)9;
                    tmp.put(state + county + census, values);
                }
                values[yearRange4] = Byte.valueOf(val);
            }
            reader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Entry<String, byte[]> entry : tmp.entrySet()) {
            byte[] values = entry.getValue();
            short result = (short)(values[yearRange1] * 1000 + values[yearRange2] * 100 + values[yearRange3] * 10 + values[yearRange4]);
            _POVERTY_INDICATOR_LOOK_UP.put(entry.getKey(), result);
        }
    }
}