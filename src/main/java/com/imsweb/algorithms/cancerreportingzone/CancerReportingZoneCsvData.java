/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.cancerreportingzone;

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

import static com.imsweb.algorithms.cancerreportingzone.CancerReportingZoneUtils.CANCER_REPORTING_ZONE_UNKNOWN;

public class CancerReportingZoneCsvData implements CancerReportingZoneDataProvider {

    @Override
    public String getCancerReportingZone(String state, String county, String censusTract) {
        if (state == null || county == null || censusTract == null)
            return CANCER_REPORTING_ZONE_UNKNOWN;

        if (!CountryData.getInstance().isCancerReportingZoneDataInitialized())
            CountryData.getInstance().initializeCancerReportingZoneData(loadCancerReportingZoneData());

        StateData stateData = CountryData.getInstance().getCancerReportingZoneData(state);
        if (stateData == null)
            return CANCER_REPORTING_ZONE_UNKNOWN;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return CANCER_REPORTING_ZONE_UNKNOWN;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return CANCER_REPORTING_ZONE_UNKNOWN;

        return censusData.getCancerReportingZone();
    }

    @SuppressWarnings("ConstantConditions")
    private Map<String, Map<String, Map<String, CensusData>>> loadCancerReportingZoneData() {
        Map<String, Map<String, Map<String, CensusData>>> result = new HashMap<>();

        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("cancerreportingzone/cancer-reporting-zones.csv"), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll()) {
                String state = row[0], county = row[1], tract = row[2], tractEst = row[3];

                CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                dto.setCancerReportingZone(tractEst);
            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
