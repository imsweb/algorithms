/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.tractestcongressdist.TractEstCongressDistUtils.TRACT_EST_CONGRESS_DIST_UNKNOWN;

public class TractEstCongressDistCsvData implements TractEstCongressDistDataProvider {

    @Override
    public String getTractEstCongressDist(String state, String county, String censusTract) {
        if (state == null || county == null || censusTract == null)
            return TRACT_EST_CONGRESS_DIST_UNKNOWN;

        if (!CountryData.getInstance().isTractEstCongressDistDataInitialized())
            CountryData.getInstance().initializeTractEstCongressDistData(loadTractEstCongressDistData());

        StateData stateData = CountryData.getInstance().getTractEstCongressDistStateData(state);
        if (stateData == null)
            return TRACT_EST_CONGRESS_DIST_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return TRACT_EST_CONGRESS_DIST_UNKNOWN;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return TRACT_EST_CONGRESS_DIST_UNKNOWN;

        return censusData.getTractEstCongressDist();
    }

    @SuppressWarnings("ConstantConditions")
    private Map<String, Map<String, Map<String, CensusData>>> loadTractEstCongressDistData() {
        Map<String, Map<String, Map<String, CensusData>>> result = new HashMap<>();

        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("tractestcongressdist/tract-estimated-congressional-districts.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll()) {
                String state = row[0], county = row[1], tract = row[2], tractEst = row[3];

                CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                dto.setTractEstCongressDist(tractEst);
            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
