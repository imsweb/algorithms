/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RURAL_URBAN_CENSUS_UNKNOWN;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN;

/**
 * The purpose of this class is to get the rural urban continuum for the provided year category, state of dx, and county of dx
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * <br/><br/>
 * The data used by this provider comes from the official census website. See the following SEER website for more information:
 * https://seer.cancer.gov/seerstat/variables/countyattribs/
 * In particular, the rural/urban percentage comes from these sections:
 * 2010: https://seer.cancer.gov/seerstat/variables/countyattribs/#10
 * 2000: https://seer.cancer.gov/seerstat/variables/countyattribs/#ca2000
 * <br/><br/>
 * Created on Aug 12, 2014 by HoweW
 *
 * @author howew
 */
public class RuralUrbanCsvData implements RuralUrbanDataProvider {

    // main (and unique) cached data
    private Map<String, CountyDataDto> _lookup = new HashMap<>();

    @Override
    public String getRuralUrbanCensus(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return RURAL_URBAN_CENSUS_UNKNOWN;

        if (_lookup.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _lookup.get(state + county);
        if (countyData == null)
            return RURAL_URBAN_CENSUS_UNKNOWN;
        CensusDataDto censusData = countyData.getCensusData().get(censusTract);
        if (censusData == null)
            return RURAL_URBAN_CENSUS_UNKNOWN;

        String result = null;
        if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_1))
            result = censusData.getUrbanCensus2000();
        else if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_2)) {
            result = censusData.getUrbanCensus2010();

            // if you didn't find a match in the 2010 lookup, check the 2000 lookup
            if (result == null || result.equals(RURAL_URBAN_CENSUS_UNKNOWN))
                result = censusData.getUrbanCensus2000();
        }

        return result == null ? RURAL_URBAN_CENSUS_UNKNOWN : result;
    }

    @Override
    public Float getRuralUrbanCensusPercentage(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return null;

        if (_lookup.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _lookup.get(state + county);
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

    private void initializeRuralUrbanCensusLookup() {

        // load 2000 data
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-census-2000.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], percent = row[3], census = row[4];

                CensusDataDto dto = _lookup.computeIfAbsent(state + county, k -> new CountyDataDto()).getCensusData().computeIfAbsent(tract, k -> new CensusDataDto());
                if (!".".equals(percent))
                    dto.setUrbanPercentage2000(Float.valueOf(percent));
                dto.setUrbanCensus2000(StringUtils.leftPad(census, 2, '0'));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // load 2010 data
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-census-2010.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], percent = row[3], census = row[4];

                CensusDataDto dto = _lookup.computeIfAbsent(state + county, k -> new CountyDataDto()).getCensusData().computeIfAbsent(tract, k -> new CensusDataDto());
                if (!".".equals(percent))
                    dto.setUrbanPercentage2010(Float.valueOf(percent));
                dto.setUrbanCensus2010(StringUtils.leftPad(census, 2, '0'));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRuralUrbanCommutingArea(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;

        if (_lookup.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _lookup.get(state + county);
        if (countyData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
        CensusDataDto censusData = countyData.getCensusData().get(censusTract);
        if (censusData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;

        String result = null;
        if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_1))
            result = censusData.getUrbanCommuting2000();
        else if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_2)) {
            result = censusData.getUrbanCommuting2010();

            // if you didn't find a match in the 2010 lookup, check the 2000 lookup
            if (result == null || result.equals(RURAL_URBAN_COMMUTING_AREA_UNKNOWN))
                result = censusData.getUrbanCommuting2000();
        }

        return result == null ? RURAL_URBAN_COMMUTING_AREA_UNKNOWN : result;
    }

    private void initializeRuralUrbanCommutingAreaLookup() {
        List<String> urbanCommutingAreas = Arrays.asList("1.0", "1.1", "2.0", "2.1", "3.0", "4.1", "5.1", "7.1", "8.1", "10.1");

        // load 2000 data
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2000.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], primary = row[3], secondary = row[4];

                CensusDataDto dto = _lookup.computeIfAbsent(state + county, k -> new CountyDataDto()).getCensusData().computeIfAbsent(tract, k -> new CensusDataDto());
                if (primary.equals("99"))
                    dto.setUrbanCommuting2000("09");
                else if (urbanCommutingAreas.contains(secondary))
                    dto.setUrbanCommuting2000("01");
                else
                    dto.setUrbanCommuting2000("02");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // load 2010 data
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2010.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], tract = row[2], primary = row[3], secondary = row[4];

                CensusDataDto dto = _lookup.computeIfAbsent(state + county, k -> new CountyDataDto()).getCensusData().computeIfAbsent(tract, k -> new CensusDataDto());
                if (primary.equals("99"))
                    dto.setUrbanCommuting2010("09");
                else if (urbanCommutingAreas.contains(secondary))
                    dto.setUrbanCommuting2010("01");
                else
                    dto.setUrbanCommuting2010("02");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRuralUrbanContinuum(String bealeCategory, String state, String county) {
        if (bealeCategory == null || state == null || county == null)
            return RURAL_URBAN_CONTINUUM_UNKNOWN;

        if (_lookup.isEmpty())
            initializeAllLookups();

        CountyDataDto countyData = _lookup.get(state + county);
        if (countyData == null)
            return RURAL_URBAN_CONTINUUM_UNKNOWN;

        String result;
        switch (bealeCategory) {
            case RuralUrbanDataProvider.BEALE_CATEGORY_1:
                result = countyData.getUrbanContinuum1993();
                break;
            case RuralUrbanDataProvider.BEALE_CATEGORY_2:
                result = countyData.getUrbanContinuum2003();

                // if you didn't find a match in the 2003 lookup, check the 1993 lookup
                if (result == null || result.equals(RURAL_URBAN_CONTINUUM_UNKNOWN))
                    result = countyData.getUrbanContinuum1993();
                break;
            case RuralUrbanDataProvider.BEALE_CATEGORY_3:
                result = countyData.getUrbanContinuum2013();

                // if you didn't find a match in the 2013 lookup, check the 2003 lookup
                if (result == null || result.equals(RURAL_URBAN_CONTINUUM_UNKNOWN))
                    result = countyData.getUrbanContinuum2003();

                // if you didn't find a match in the 2003 lookup, check the 1993 lookup
                if (result == null || result.equals(RURAL_URBAN_CONTINUUM_UNKNOWN))
                    result = countyData.getUrbanContinuum1993();
                break;
            default:
                throw new RuntimeException("Invalid beale category: " + bealeCategory);
        }

        return result == null ? RURAL_URBAN_CONTINUUM_UNKNOWN : result;
    }

    private void initializeRuralUrbanContinuumLookup() {

        // load 1993 data
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-1993.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], val = row[2];

                CountyDataDto dto = _lookup.computeIfAbsent(state + county, k -> new CountyDataDto());
                dto.setUrbanContinuum1993(StringUtils.leftPad(val, 2, '0'));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // load 2003 data
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-2003.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], val = row[2];

                CountyDataDto dto = _lookup.computeIfAbsent(state + county, k -> new CountyDataDto());
                dto.setUrbanContinuum2003(StringUtils.leftPad(val, 2, '0'));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // load 2013 data
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-2013.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReader(reader, ',', '\"', 1).readAll()) {
                String state = row[0], county = row[1], val = row[2];

                CountyDataDto dto = _lookup.computeIfAbsent(state + county, k -> new CountyDataDto());
                dto.setUrbanContinuum2013(StringUtils.leftPad(val, 2, '0'));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // all the data is initialized at once; if we need a more fine-grain initialization, we will need more variables to keep track of which data has been initialized...
    private synchronized void initializeAllLookups() {
        initializeRuralUrbanCensusLookup();
        initializeRuralUrbanCommutingAreaLookup();
        initializeRuralUrbanContinuumLookup();
    }

    // data structure for the values keyed by state+county only
    private static class CountyDataDto {

        private String _urbanContinuum1993;
        private String _urbanContinuum2003;
        private String _urbanContinuum2013;
        private Map<String, CensusDataDto> _censusData;

        public String getUrbanContinuum1993() {
            return _urbanContinuum1993;
        }

        public void setUrbanContinuum1993(String urbanContinuum1993) {
            _urbanContinuum1993 = urbanContinuum1993;
        }

        public String getUrbanContinuum2003() {
            return _urbanContinuum2003;
        }

        public void setUrbanContinuum2003(String urbanContinuum2003) {
            _urbanContinuum2003 = urbanContinuum2003;
        }

        public String getUrbanContinuum2013() {
            return _urbanContinuum2013;
        }

        public void setUrbanContinuum2013(String urbanContinuum2013) {
            _urbanContinuum2013 = urbanContinuum2013;
        }

        public Map<String, CensusDataDto> getCensusData() {
            if (_censusData == null)
                _censusData = new HashMap<>();
            return _censusData;
        }
    }

    // data structure for the values keyed by state+county+census
    private static class CensusDataDto {

        private String _urbanCensus2000;
        private String _urbanCommuting2000;
        private Float _urbanPercentage2000;

        private String _urbanCensus2010;
        private String _urbanCommuting2010;
        private Float _urbanPercentage2010;

        public String getUrbanCensus2000() {
            return _urbanCensus2000;
        }

        public void setUrbanCensus2000(String urbanCensus2000) {
            _urbanCensus2000 = urbanCensus2000;
        }

        public String getUrbanCommuting2000() {
            return _urbanCommuting2000;
        }

        public void setUrbanCommuting2000(String urbanCommuting2000) {
            _urbanCommuting2000 = urbanCommuting2000;
        }

        public Float getUrbanPercentage2000() {
            return _urbanPercentage2000;
        }

        public void setUrbanPercentage2000(Float urbanPercentage2000) {
            _urbanPercentage2000 = urbanPercentage2000;
        }

        public String getUrbanCensus2010() {
            return _urbanCensus2010;
        }

        public void setUrbanCensus2010(String urbanCensus2010) {
            _urbanCensus2010 = urbanCensus2010;
        }

        public String getUrbanCommuting2010() {
            return _urbanCommuting2010;
        }

        public void setUrbanCommuting2010(String urbanCommuting2010) {
            _urbanCommuting2010 = urbanCommuting2010;
        }

        public Float getUrbanPercentage2010() {
            return _urbanPercentage2010;
        }

        public void setUrbanPercentage2010(Float urbanPercentage2010) {
            _urbanPercentage2010 = urbanPercentage2010;
        }
    }

}