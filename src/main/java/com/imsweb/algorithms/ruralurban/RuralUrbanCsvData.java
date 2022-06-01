/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RURAL_URBAN_CONTINUUM_UNKNOWN;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URBAN_RURAL_INDICATOR_CODE_UNKNOWN;

/**
 * The purpose of this class is to get the rural urban continuum for the provided year category, state of dx, and county of dx
 * from the csv file lookup.  This implementation is memory consumer. If there is a database, it is better to use another implementation.
 * <br/><br/>
 * The data used by this provider comes from the official census website. See the following SEER website for more information:
 * <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/"</a>
 * In particular, the rural/urban percentage comes from these sections:
 * 2010: <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/#10"</a>
 * 2000: <a href="https://seer.cancer.gov/seerstat/variables/countyattribs/#ca2000"</a>
 * <br/><br/>
 * Created on Aug 12, 2014 by HoweW
 * @author howew
 */
public class RuralUrbanCsvData implements RuralUrbanDataProvider {

    @Override
    public String getUrbanRuralIndicatorCode(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;

        if (!CountryData.getInstance().isUricDataInitialized())
            CountryData.getInstance().initializeUricData(loadUrbanRuralIndicatorCodeData());

        StateData stateData = CountryData.getInstance().getUricStateData(state);
        if (stateData == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return URBAN_RURAL_INDICATOR_CODE_UNKNOWN;

        String result = null;
        if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_1))
            result = censusData.getIndicatorCode2000();
        else if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_2)) {
            result = censusData.getIndicatorCode2010();

            // if you didn't find a match in the 2010 lookup, check the 2000 lookup
            if (result == null || result.equals(URBAN_RURAL_INDICATOR_CODE_UNKNOWN))
                result = censusData.getIndicatorCode2000();
        }

        return result == null ? URBAN_RURAL_INDICATOR_CODE_UNKNOWN : result;
    }

    @Override
    public Float getRuralUrbanCensusPercentage(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return null;

        if (!CountryData.getInstance().isUricDataInitialized())
            CountryData.getInstance().initializeUricData(loadUrbanRuralIndicatorCodeData());

        StateData stateData = CountryData.getInstance().getUricStateData(state);
        if (stateData == null)
            return null;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return null;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return null;

        switch (tractCategory) {
            case RuralUrbanDataProvider.TRACT_CATEGORY_1:
                return censusData.getIndicatorCodePercentage2000();
            case RuralUrbanDataProvider.TRACT_CATEGORY_2:
                return censusData.getIndicatorCodePercentage2010();
            default:
                return null;
        }
    }

    private Map<String, Map<String, Map<String, CensusData>>> loadUrbanRuralIndicatorCodeData() {
        Map<String, Map<String, Map<String, CensusData>>> result = new HashMap<>();

        // load 2000 data
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/urban-rural-indicator-code-2000.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String tract = row[2];
                    String percent = row[3];
                    String indicator = row[4];

                    if (indicator.length() != 1)
                        throw new IllegalStateException("Found unexpected format for URIC value: " + indicator);

                    CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                    dto.setIndicatorCode2000(indicator);
                    if (!".".equals(percent))
                        dto.setIndicatorCodePercentage2000(Float.valueOf(percent));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        // load 2010 data
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/urban-rural-indicator-code-2010.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String tract = row[2];
                    String percent = row[3];
                    String indicator = row[4];

                    if (indicator.length() != 1)
                        throw new IllegalStateException("Found unexpected format for URIC value: " + indicator);

                    CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                    dto.setIndicatorCode2010(indicator);
                    if (!".".equals(percent))
                        dto.setIndicatorCodePercentage2010(Float.valueOf(percent));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }

    @Override
    public String getRuralUrbanCommutingArea(String tractCategory, String state, String county, String censusTract) {
        if (tractCategory == null || state == null || county == null || censusTract == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;

        if (!CountryData.getInstance().isRucaDataInitialized())
            CountryData.getInstance().initializeRucaData(loadRuralUrbanCommutingAreaData());

        StateData stateData = CountryData.getInstance().getRucaStateData(state);
        if (stateData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return RURAL_URBAN_COMMUTING_AREA_UNKNOWN;

        String result = null;
        if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_1))
            result = censusData.getCommutingArea2000();
        else if (tractCategory.equals(RuralUrbanDataProvider.TRACT_CATEGORY_2)) {
            result = censusData.getCommutingArea2010();

            // if you didn't find a match in the 2010 lookup, check the 2000 lookup
            if (result == null || result.equals(RURAL_URBAN_COMMUTING_AREA_UNKNOWN))
                result = censusData.getCommutingArea2000();
        }

        return result == null ? RURAL_URBAN_COMMUTING_AREA_UNKNOWN : result;
    }

    private Map<String, Map<String, Map<String, CensusData>>> loadRuralUrbanCommutingAreaData() {
        Map<String, Map<String, Map<String, CensusData>>> result = new HashMap<>();

        List<String> urbanCommutingAreas = Arrays.asList("1.0", "1.1", "2.0", "2.1", "3.0", "4.1", "5.1", "7.1", "8.1", "10.1");

        // load 2000 data
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2000.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String tract = row[2];
                    String primary = row[3];
                    String secondary = row[4];

                    CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                    if (primary.equals("99"))
                        dto.setCommutingArea2000("9");
                    else if (urbanCommutingAreas.contains(secondary))
                        dto.setCommutingArea2000("1");
                    else
                        dto.setCommutingArea2000("2");
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        // load 2010 data
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2010.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String tract = row[2];
                    String primary = row[3];
                    String secondary = row[4];

                    CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                    if (primary.equals("99"))
                        dto.setCommutingArea2010("9");
                    else if (urbanCommutingAreas.contains(secondary))
                        dto.setCommutingArea2010("1");
                    else
                        dto.setCommutingArea2010("2");
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }

    @Override
    public String getRuralUrbanContinuum(String bealeCategory, String state, String county) {
        if (bealeCategory == null || state == null || county == null)
            return RURAL_URBAN_CONTINUUM_UNKNOWN;

        if (!CountryData.getInstance().isContinuumDataInitialized())
            CountryData.getInstance().initializeContinuumData(loadRuralUrbanContinuumData());

        StateData stateData = CountryData.getInstance().getContinuumStateData(state);
        if (stateData == null)
            return RURAL_URBAN_CONTINUUM_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
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
                throw new IllegalStateException("Invalid beale category: " + bealeCategory);
        }

        return result == null ? RURAL_URBAN_CONTINUUM_UNKNOWN : result;
    }

    private Map<String, Map<String, CountyData>> loadRuralUrbanContinuumData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();

        // load 1993 data
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-1993.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String val = row[2];

                    CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                    dto.setUrbanContinuum1993(StringUtils.leftPad(val, 2, '0'));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        // load 2003 data
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-2003.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String val = row[2];

                    CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                    dto.setUrbanContinuum2003(StringUtils.leftPad(val, 2, '0'));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        // load 2013 data
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-continuum-2013.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String val = row[2];

                    CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                    dto.setUrbanContinuum2013(StringUtils.leftPad(val, 2, '0'));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }
}