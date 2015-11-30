/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

/**
 * The purpose of this class is to get the rural urban continuum for the provided year category, state of dx, and county of dx
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * Created on Aug 12, 2014 by HoweW
 * @author howew
 */
public class RuralUrbanCsvData implements RuralUrbanDataProvider {

    // data structure for the values keyed by state+county only
    private static class CountyDataDto {

        private Long _urbanContinuum;
        private Map<String, CensusDataDto> _censusData;

        public Long getUrbanContinuum() {
            return _urbanContinuum;
        }

        public void setUrbanContinuum(Long urbanContinuum) {
            _urbanContinuum = urbanContinuum;
        }

        public Map<String, CensusDataDto> getCensusData() {
            if (_censusData == null)
                _censusData = new HashMap<>();
            return _censusData;
        }
    }

    // data structure for the values keyed by state+county+census
    private static class CensusDataDto {

        private Long _urbanCensus;
        private Long _urbanCommuting;
        private Float _urbanPercentage2000;
        private Float _urbanPercentage2010;

        public Long getUrbanCensus() {
            return _urbanCensus;
        }

        public void setUrbanCensus(Long urbanCensus) {
            _urbanCensus = urbanCensus;
        }

        public Long getUrbanCommuting() {
            return _urbanCommuting;
        }

        public void setUrbanCommuting(Long urbanCommuting) {
            _urbanCommuting = urbanCommuting;
        }

        public Float getUrbanPercentage2000() {
            return _urbanPercentage2000;
        }

        public void setUrbanPercentage2000(Float urbanPercentage2000) {
            _urbanPercentage2000 = urbanPercentage2000;
        }

        public Float getUrbanPercentage2010() {
            return _urbanPercentage2010;
        }

        public void setUrbanPercentage2010(Float urbanPercentage2010) {
            _urbanPercentage2010 = urbanPercentage2010;
        }
    }

    // main (and unique) cached data
    private static Map<String, CountyDataDto> _RURAL_URBAN_LOOK_UP = new HashMap<>();

    // all the data is initialized at once; if we need a more fine-grain initialization, we will need more variables to keep track of which data has been initialized...
    private static synchronized void initializeAllLookups() {
        initializeRuralUrbanCensusLookup();
        initializeRuralUrbanCommutingAreaLookup();
        initializeRuralUrbanContinuumLookup();
    }

    //////////////////////////////////////////////////////////////////////////////////
    // RURAL URBAN CENSUS CODE SECTION STARTS HERE
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getRuralUrbanCensus(String tractCategory, String state, String county, String censusTract) {
        String ruralUrbanCensus = RuralUrbanUtils.RURAL_URBAN_CENSUS_UNKNOWN;

        if (tractCategory == null || state == null || county == null || censusTract == null)
            return ruralUrbanCensus;

        if (_RURAL_URBAN_LOOK_UP.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _RURAL_URBAN_LOOK_UP.get(state + county);
        CensusDataDto censusData = countyData == null ? null : countyData.getCensusData().get(censusTract);
        Long ruralUrbanCensusValue = censusData == null ? null : censusData.getUrbanCensus();

        if (ruralUrbanCensusValue != null) {
            if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_1))
                ruralUrbanCensus = String.valueOf((ruralUrbanCensusValue / 100) % 100);

            else if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_2)) {
                ruralUrbanCensus = String.valueOf(ruralUrbanCensusValue % 100);

                // if you didn't find a match in the 2010 lookup, check the 2000 lookup
                if (ruralUrbanCensus.equals(RuralUrbanUtils.RURAL_URBAN_CENSUS_UNKNOWN))
                    ruralUrbanCensus = String.valueOf((ruralUrbanCensusValue / 100) % 100);
            }
        }

        return StringUtils.isBlank(ruralUrbanCensus) ? RuralUrbanUtils.RURAL_URBAN_CENSUS_UNKNOWN : StringUtils.leftPad(ruralUrbanCensus, 2, '0');
    }

    @Override
    public Float getRuralUrbanCensusPercentage(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return null;

        if (_RURAL_URBAN_LOOK_UP.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _RURAL_URBAN_LOOK_UP.get(state + county);
        if (countyData == null)
            return null;
        CensusDataDto censusData = countyData.getCensusData().get(censusTract);
        if (censusData == null)
            return null;

        if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_1))
            return censusData.getUrbanPercentage2000();
        if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_2))
            return censusData.getUrbanPercentage2010();
        
        return null;
    }

    private static synchronized void initializeRuralUrbanCensusLookup() {
        int year1 = 0, year2 = 1;

        Map<String, byte[]> tmp = new HashMap<>();
        Map<String, Float[]> tmp2 = new HashMap<>();

        try {
            Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-census-2000.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], percent = row[3], census = row[4];
                String key = state + county + "#" + tract; // that will allow us to split the key later in the process...
                byte[] values = tmp.get(key);
                if (values == null) {
                    values = new byte[2];
                    values[0] = values[1] = Byte.valueOf(RuralUrbanUtils.RURAL_URBAN_CENSUS_UNKNOWN);
                    tmp.put(key, values);
                }
                values[year1] = Byte.valueOf(census);
                if (!".".equals(percent)) {
                    Float[] percentValues = tmp2.get(key);
                    if (percentValues == null) {
                        percentValues = new Float[2];
                        tmp2.put(key, percentValues);
                    }
                    percentValues[year1] = Float.valueOf(percent);
                }
            }
            reader.close();
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-census-2010.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], percent = row[3], census = row[4];
                String key = state + county + "#" + tract; // that will allow us to split the key later in the process...
                byte[] values = tmp.get(key);
                if (values == null) {
                    values = new byte[2];
                    values[0] = values[1] = Byte.valueOf(RuralUrbanUtils.RURAL_URBAN_CENSUS_UNKNOWN);
                    tmp.put(key, values);
                }
                values[year2] = Byte.valueOf(census);
                if (!".".equals(percent)) {
                    Float[] percentValues = tmp2.get(key);
                    if (percentValues == null) {
                        percentValues = new Float[2];
                        tmp2.put(key, percentValues);
                    }
                    percentValues[year2] = Float.valueOf(percent);
                }
            }
            reader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Entry<String, byte[]> entry : tmp.entrySet()) {
            String[] key = StringUtils.split(entry.getKey(), '#');
            byte[] values = entry.getValue();
            long result = (long)(values[0] * 100 + values[1]);
            CountyDataDto countyData = _RURAL_URBAN_LOOK_UP.get(key[0]);
            if (countyData == null) {
                countyData = new CountyDataDto();
                _RURAL_URBAN_LOOK_UP.put(key[0], countyData);
            }
            CensusDataDto censusData = countyData.getCensusData().get(key[1]);
            if (censusData == null) {
                censusData = new CensusDataDto();
                countyData.getCensusData().put(key[1], censusData);
            }
            censusData.setUrbanCensus(result);
            Float[] percentValues = tmp2.get(entry.getKey());
            if (percentValues != null) {
                censusData.setUrbanPercentage2000(percentValues[year1]);
                censusData.setUrbanPercentage2010(percentValues[year2]);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    // RURAL URBAN COMMUTING AREA CODE SECTION STARTS HERE
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getRuralUrbanCommutingArea(String tractCategory, String state, String county, String censusTract) {
        String ruralUrbanCommutingArea = RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN;

        if (tractCategory == null || state == null || county == null || censusTract == null)
            return ruralUrbanCommutingArea;

        if (_RURAL_URBAN_LOOK_UP.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _RURAL_URBAN_LOOK_UP.get(state + county);
        CensusDataDto censusData = countyData == null ? null : countyData.getCensusData().get(censusTract);
        Long ruralUrbanCommutingAreaValue = censusData == null ? null : censusData.getUrbanCommuting();

        if (ruralUrbanCommutingAreaValue != null) {
            if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_1))
                ruralUrbanCommutingArea = String.valueOf((ruralUrbanCommutingAreaValue / 100) % 100);

            else if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_2)) {
                ruralUrbanCommutingArea = String.valueOf(ruralUrbanCommutingAreaValue % 100);

                // if you didn't find a match in the 2010 lookup, check the 2000 lookup
                if (ruralUrbanCommutingArea.equals(RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN))
                    ruralUrbanCommutingArea = String.valueOf((ruralUrbanCommutingAreaValue / 100) % 100);
            }
        }

        return StringUtils.isBlank(ruralUrbanCommutingArea) ? RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN : StringUtils.leftPad(ruralUrbanCommutingArea, 2, '0');
    }

    private static void initializeRuralUrbanCommutingAreaLookup() {
        int year1 = 0, year2 = 1;

        List<String> urbanCommutingAreas = Arrays.asList("1.0", "1.1", "2.0", "2.1", "3.0", "4.1", "5.1", "7.1", "8.1", "10.1");

        Map<String, byte[]> tmp = new HashMap<>();

        try {
            Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2000.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], primary = row[3], secondary = row[4];
                String key = state + county + "#" + tract; // that will allow us to split the key later in the process...
                byte[] values = tmp.get(key);
                if (values == null) {
                    values = new byte[2];
                    values[0] = values[1] = Byte.valueOf(RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN);
                    tmp.put(key, values);
                }
                if (primary.equals("99"))
                    values[year1] = 9;
                else if (urbanCommutingAreas.contains(secondary))
                    values[year1] = 1;
                else
                    values[year1] = 2;
            }
            reader.close();
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2010.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], primary = row[3], secondary = row[4];
                String key = state + county + "#" + tract; // that will allow us to split the key later in the process...
                byte[] values = tmp.get(key);
                if (values == null) {
                    values = new byte[2];
                    values[0] = values[1] = Byte.valueOf(RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN);
                    tmp.put(key, values);
                }
                if (primary.equals("99")) {
                    values[year2] = 9;
                }
                else if (urbanCommutingAreas.contains(secondary)) {
                    values[year2] = 1;
                }
                else {
                    values[year2] = 2;
                }
            }
            reader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Entry<String, byte[]> entry : tmp.entrySet()) {
            String[] key = StringUtils.split(entry.getKey(), '#');
            byte[] values = entry.getValue();
            long result = (long)(values[0] * 100 + values[1]);
            CountyDataDto countyData = _RURAL_URBAN_LOOK_UP.get(key[0]);
            if (countyData == null) {
                countyData = new CountyDataDto();
                _RURAL_URBAN_LOOK_UP.put(key[0], countyData);
            }
            CensusDataDto censusData = countyData.getCensusData().get(key[1]);
            if (censusData == null) {
                censusData = new CensusDataDto();
                countyData.getCensusData().put(key[1], censusData);
            }
            censusData.setUrbanCommuting(result);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    // RURAL URBAN CONTINUUM CODE SECTION STARTS HERE
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getRuralUrbanContinuum(String bealeCategory, String state, String county) {

        String ruralUrbanContinuum = RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN;

        if (bealeCategory == null || state == null || county == null)
            return ruralUrbanContinuum;

        if (_RURAL_URBAN_LOOK_UP.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _RURAL_URBAN_LOOK_UP.get(state + county);
        Long ruralUrbanContinuumValue = countyData == null ? null : countyData.getUrbanContinuum();

        if (ruralUrbanContinuumValue != null) {
            switch (bealeCategory) {
                case RuralUrbanDataProvider.BEALE_CATEGORY_1:
                    ruralUrbanContinuum = String.valueOf((ruralUrbanContinuumValue / 10000) % 100);
                    break;
                case RuralUrbanDataProvider.BEALE_CATEGORY_2:
                    ruralUrbanContinuum = String.valueOf((ruralUrbanContinuumValue / 100) % 100);

                    // if you didn't find a match in the 2003 lookup, check the 1993 lookup
                    if (ruralUrbanContinuum.equals(RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN))
                        ruralUrbanContinuum = String.valueOf((ruralUrbanContinuumValue / 10000) % 100);
                    break;
                case RuralUrbanDataProvider.BEALE_CATEGORY_3:
                    ruralUrbanContinuum = String.valueOf(ruralUrbanContinuumValue % 100);

                    // if you didn't find a match in the 2013 lookup, check the 2003 lookup
                    if (ruralUrbanContinuum.equals(RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN))
                        ruralUrbanContinuum = String.valueOf((ruralUrbanContinuumValue / 100) % 100);

                    // if you didn't find a match in the 2003 lookup, check the 1993 lookup
                    if (ruralUrbanContinuum.equals(RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN))
                        ruralUrbanContinuum = String.valueOf((ruralUrbanContinuumValue / 10000) % 100);
                    break;
                default:
                    throw new RuntimeException("Unsupported category: " + bealeCategory);
            }
        }

        return StringUtils.isBlank(ruralUrbanContinuum) ? RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN : StringUtils.leftPad(ruralUrbanContinuum, 2, '0');
    }

    private static void initializeRuralUrbanContinuumLookup() {
        int year1 = 0, year2 = 1, year3 = 2;

        Map<String, byte[]> tmp = new HashMap<>();

        try {
            Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-1993.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], val = row[2];
                byte[] values = tmp.get(state + county);
                if (values == null) {
                    values = new byte[3];
                    values[0] = values[1] = values[2] = Byte.valueOf(RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN);
                    tmp.put(state + county, values);
                }
                values[year1] = Byte.valueOf(val);
            }
            reader.close();
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-2003.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], val = row[2];
                byte[] values = tmp.get(state + county);
                if (values == null) {
                    values = new byte[3];
                    values[0] = values[1] = values[2] = Byte.valueOf(RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN);
                    tmp.put(state + county, values);
                }
                values[year2] = Byte.valueOf(val);
            }
            reader.close();
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-2013.csv"), "US-ASCII");
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], val = row[2];
                byte[] values = tmp.get(state + county);
                if (values == null) {
                    values = new byte[3];
                    values[0] = values[1] = values[2] = Byte.valueOf(RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN);
                    tmp.put(state + county, values);
                }
                values[year3] = Byte.valueOf(val);
            }
            reader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Entry<String, byte[]> entry : tmp.entrySet()) {
            byte[] values = entry.getValue();
            long result = (long)(values[0] * 10000 + values[1] * 100 + values[2]);
            CountyDataDto countyData = _RURAL_URBAN_LOOK_UP.get(entry.getKey());
            if (countyData == null) {
                countyData = new CountyDataDto();
                _RURAL_URBAN_LOOK_UP.put(entry.getKey(), countyData);
            }
            countyData.setUrbanContinuum(result);
        }
    }
}