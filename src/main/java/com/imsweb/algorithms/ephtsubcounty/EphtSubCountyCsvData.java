/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ephtsubcounty;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.ephtsubcounty.EphtSubCountyUtils.EPHT_2010_GEO_ID_UNK_C;

/**
 * The purpose of this class is to get the EPHT 2010 GEO ID 5K and EPHT 2010 GEO ID 20K for the provided
 * state of dx, county of dx, and census tract 2010 from the csv file lookup.  This implementation is memory
 * consumer. If there is a database, it is better to use another implementation.
 * Created on Aug 3, 2021 by kirbyk
 * @author kirbyk
 */
public class EphtSubCountyCsvData implements EphtSubCountyDataProvider {

    @Override
    public String getEPHT2010GeoId5k(String state, String county, String censusTract) {
        if (!CountryData.getInstance().isEphtSubCountyDataInitialized())
            CountryData.getInstance().initializeEphtSubCountyData(loadEphtSubCountyData());

        StateData stateData = CountryData.getInstance().getEphtSubCountyData(state);
        if (stateData == null)
            return EPHT_2010_GEO_ID_UNK_C;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return EPHT_2010_GEO_ID_UNK_C;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return EPHT_2010_GEO_ID_UNK_C;

        return censusData.getEpht2010GeoId5k();
    }

    @Override
    public String getEPHT2010GeoId20k(String state, String county, String censusTract) {
        if (!CountryData.getInstance().isEphtSubCountyDataInitialized())
            CountryData.getInstance().initializeEphtSubCountyData(loadEphtSubCountyData());

        StateData stateData = CountryData.getInstance().getEphtSubCountyData(state);
        if (stateData == null)
            return EPHT_2010_GEO_ID_UNK_C;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return EPHT_2010_GEO_ID_UNK_C;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return EPHT_2010_GEO_ID_UNK_C;

        return censusData.getEpht2010GeoId20k();
    }

    @SuppressWarnings("ConstantConditions")
    private Map<String, Map<String, Map<String, CensusData>>> loadEphtSubCountyData() {
        Map<String, Map<String, Map<String, CensusData>>> result = new HashMap<>();
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ephtsubcounty/epht-sub-counties.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll()) {
                String state = row[0], county = row[1], censusTract = row[2], epht5k = row[3], epht20k = row[4];
                CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(censusTract, k -> new CensusData());
                dto.setEpht2010GeoId20k(StringUtils.leftPad(epht20k, 11, '0'));
                dto.setEpht2010GeoId5k(StringUtils.leftPad(epht5k, 11, '0'));
            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}