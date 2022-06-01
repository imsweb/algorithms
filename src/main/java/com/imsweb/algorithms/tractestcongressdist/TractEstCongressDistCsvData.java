/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tractestcongressdist;

import java.io.IOException;
import java.io.InputStream;
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

import static com.imsweb.algorithms.tractestcongressdist.TractEstCongressDistUtils.TRACT_EST_CONGRESS_DIST_UNK_C;

public class TractEstCongressDistCsvData implements TractEstCongressDistDataProvider {

    @Override
    public String getTractEstCongressDist(String state, String county, String censusTract) {
        if (state == null || county == null || censusTract == null)
            return TRACT_EST_CONGRESS_DIST_UNK_C;

        if (!CountryData.getInstance().isTractEstCongressDistDataInitialized())
            CountryData.getInstance().initializeTractEstCongressDistData(loadTractEstCongressDistData());

        StateData stateData = CountryData.getInstance().getTractEstCongressDistStateData(state);
        if (stateData == null)
            return TRACT_EST_CONGRESS_DIST_UNK_C;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return TRACT_EST_CONGRESS_DIST_UNK_C;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return TRACT_EST_CONGRESS_DIST_UNK_C;

        return censusData.getTractEstCongressDist();
    }

    private Map<String, Map<String, Map<String, CensusData>>> loadTractEstCongressDistData() {
        Map<String, Map<String, Map<String, CensusData>>> result = new HashMap<>();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("tractestcongressdist/tract-estimated-congressional-districts.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String tract = row[2];
                    String tractEst = row[3];

                    CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                    dto.setTractEstCongressDist(tractEst);
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }
}
