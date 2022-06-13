/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Several algorithms need to use data related to either states, counties or census trace codes.
 * To optimize the memory usage, this class was introduced so those algorithms can use a shared
 * data structure.
 * <br/><br/>
 * This class is the root of the data structure which goes like this:
 * CountryData -> StateData -> CountyData -> CensusData
 * Algorithms can register data at either one of those levels.
 * <br/><br/>
 * This class provides concurrency to safely register the data and use it in a thread-safe manner.
 * But it is the responsibility of the algorithms to check that the data has been properly
 * initialized before it's being accessed.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class CountryData {

    private static final String _SEER_CENSUS_DATA_FILE = "tract/tract-data.txt.gz";

    private static final Map<String, Integer> _TRACT_FIELDS = new LinkedHashMap<>();

    static {
        _TRACT_FIELDS.put("stateAbbreviation", 2);
        _TRACT_FIELDS.put("countyFips", 3);
        _TRACT_FIELDS.put("censusTract", 6);
        _TRACT_FIELDS.put("yearData", 370);
        _TRACT_FIELDS.put("ruca2000", 1);
        _TRACT_FIELDS.put("ruca2010", 1);
        _TRACT_FIELDS.put("uric2000", 1);
        _TRACT_FIELDS.put("uric2010", 1);
        _TRACT_FIELDS.put("cancerReportingZone", 10);
        _TRACT_FIELDS.put("naaccrPovertyIndicator9504", 1);
        _TRACT_FIELDS.put("naaccrPovertyIndicator0507", 1);
        _TRACT_FIELDS.put("npcrEphtSubcounty5k", 11);
        _TRACT_FIELDS.put("npcrEphtSubcounty20k", 11);
    }

    public static Map<String, Integer> getTractFields() {
        return Collections.unmodifiableMap(_TRACT_FIELDS);
    }

    private static final Map<String, Integer> _TRACT_YEAR_BASED_FIELDS = new LinkedHashMap<>();

    static {
        _TRACT_YEAR_BASED_FIELDS.put("yostUsBasedQuintile", 1);
        _TRACT_YEAR_BASED_FIELDS.put("yostStateBasedQuintile", 1);
        _TRACT_YEAR_BASED_FIELDS.put("percentBelowPovertyAllRaces", 5);
        _TRACT_YEAR_BASED_FIELDS.put("percentBelowPovertyWhite", 5);
        _TRACT_YEAR_BASED_FIELDS.put("percentBelowPovertyBlack", 5);
        _TRACT_YEAR_BASED_FIELDS.put("percentBelowPovertyAmIndian", 5);
        _TRACT_YEAR_BASED_FIELDS.put("percentBelowPovertyAsian", 5);
        _TRACT_YEAR_BASED_FIELDS.put("percentBelowPovertyWhiteNotHisp", 5);
        _TRACT_YEAR_BASED_FIELDS.put("percentBelowPovertyHisp", 5);
    }

    public static Map<String, Integer> getTractYearBasedFields() {
        return Collections.unmodifiableMap(_TRACT_YEAR_BASED_FIELDS);
    }

    public static final int TRACT_YEAR_MIN_VAL = 2008;
    public static final int TRACT_YEAR_MAX_VAL = 2017;

    // singleton instance
    private static final CountryData _INSTANCE = new CountryData();

    // unique access to the singleton
    public static CountryData getInstance() {
        return _INSTANCE;
    }

    // shared internal data structure; sates mapped by state abbreviation
    private final Map<String, StateData> _stateData = new HashMap<>();

    // the states that had their census-related data initialized
    private final Set<String> _stateTractDataInitialized = new HashSet<>();

    // the states that had their year-based census-related data initialized
    private final Set<String> _stateTractDataYearBasedInitialized = new HashSet<>();

    // the states that had their Continuum 1993/2003/2013 data initialized
    private final Set<String> _continuumStateInitialized = new HashSet<>();
    private boolean _countyAtDxAnalysisInitialized = false;
    private boolean _prcdaInitialized = false;
    private boolean _uihoInitialized = false;
    private boolean _tractEstCongressDistInitialized = false;

    // internal lock to control concurrency
    private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();

    /**
     * Unregister all data.
     */
    public void uninitializeAllData() {
        _lock.writeLock().lock();
        try {
            _stateData.clear();
            _stateTractDataInitialized.clear();
            _stateTractDataYearBasedInitialized.clear();
            _continuumStateInitialized.clear();
            _countyAtDxAnalysisInitialized = false;
            _prcdaInitialized = false;
            _uihoInitialized = false;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    public boolean isTractDataInitialized(String requestedState) {
        _lock.readLock().lock();
        try {
            return _stateTractDataInitialized.contains(requestedState);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    public void initializeTractData(String requestedState) {
        _lock.writeLock().lock();
        try {
            if (!_stateTractDataInitialized.contains(requestedState)) {
                try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(_SEER_CENSUS_DATA_FILE)) {
                    if (is == null)
                        throw new IllegalStateException("Unable to get SEER census tract data file");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(is), StandardCharsets.US_ASCII))) {
                        String line = reader.readLine();
                        while (line != null) {

                            int index = 0;
                            Map<String, String> values = new HashMap<>();
                            for (Entry<String, Integer> entry : _TRACT_FIELDS.entrySet()) {
                                values.put(entry.getKey(), line.substring(index, index + entry.getValue()));
                                index += entry.getValue();
                            }

                            String state = values.get("stateAbbreviation");
                            String county = values.get("countyFips");
                            String tract = values.get("censusTract");

                            if (Objects.equals(state, requestedState)) {
                                StateData stateData = _stateData.computeIfAbsent(state, k -> new StateData());
                                CountyData countyData = stateData.getData().computeIfAbsent(county, k -> new CountyData());
                                CensusData censusData = countyData.getData().computeIfAbsent(tract, k -> new CensusData());

                                // NAACCR Poverty Indicator (only "old" years, the "recent" years are computed from the ACS poverty percentages for "all races")
                                censusData.setNaaccrPovertyIndicator9504(StringUtils.trimToNull(values.get("naaccrPovertyIndicator9504")));
                                censusData.setNaaccrPovertyIndicator0507(StringUtils.trimToNull(values.get("naaccrPovertyIndicator0507")));

                                // RUCA
                                censusData.setCommutingArea2000(Objects.toString(StringUtils.trimToNull(values.get("ruca2000")), "9"));
                                censusData.setCommutingArea2010(Objects.toString(StringUtils.trimToNull(values.get("ruca2010")), "9"));

                                // URIC
                                censusData.setIndicatorCode2000(Objects.toString(StringUtils.trimToNull(values.get("uric2000")), "9"));
                                censusData.setIndicatorCode2010(Objects.toString(StringUtils.trimToNull(values.get("uric2010")), "9"));

                                // NPCR EPHT SubCounty
                                censusData.setEpht2010GeoId5k(StringUtils.leftPad(StringUtils.trimToNull(values.get("npcrEphtSubcounty5k")), 11, '0'));
                                censusData.setEpht2010GeoId20k(StringUtils.leftPad(StringUtils.trimToNull(values.get("npcrEphtSubcounty20k")), 11, '0'));

                                // Cancer Reporting Zone
                                censusData.setCancerReportingZone(StringUtils.trimToNull(values.get("cancerReportingZone")));
                            }

                            line = reader.readLine();
                        }
                    }
                }
                catch (IOException e) {
                    throw new IllegalStateException("Unable to initialize tract data", e);
                }
            }
            _stateTractDataInitialized.add(requestedState);
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    public StateData getTractData(String state) {
        _lock.readLock().lock();
        try {
            if (!isTractDataInitialized(state))
                throw new IllegalStateException("Census tract data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    public boolean isYearBasedTractDataInitialized(String requestedState) {
        _lock.readLock().lock();
        try {
            return _stateTractDataYearBasedInitialized.contains(requestedState);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    public void initializeYearBasedTractData(String requestedState) {
        _lock.writeLock().lock();
        try {
            if (!_stateTractDataYearBasedInitialized.contains(requestedState)) {
                try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(_SEER_CENSUS_DATA_FILE)) {
                    if (is == null)
                        throw new IllegalStateException("Unable to get year-based SEER census tract data file");
                    try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new GZIPInputStream(is), StandardCharsets.US_ASCII))) {
                        String line = reader.readLine();
                        while (line != null) {
                            int lineNum = reader.getLineNumber();

                            int index = 0;
                            Map<String, String> values = new HashMap<>();
                            for (Entry<String, Integer> entry : _TRACT_FIELDS.entrySet()) {
                                values.put(entry.getKey(), line.substring(index, index + entry.getValue()));
                                index += entry.getValue();
                            }

                            String state = values.get("stateAbbreviation");
                            String county = values.get("countyFips");
                            String tract = values.get("censusTract");
                            String rawYearData = values.get("yearData");

                            if (Objects.equals(state, requestedState)) {
                                StateData stateData = _stateData.computeIfAbsent(state, k -> new StateData());
                                CountyData countyData = stateData.getData().computeIfAbsent(county, k -> new CountyData());
                                CensusData censusData = countyData.getData().computeIfAbsent(tract, k -> new CensusData());

                                index = 0;
                                for (int year = TRACT_YEAR_MIN_VAL; year <= TRACT_YEAR_MAX_VAL; year++) {
                                    YearData yearData = censusData.getData().computeIfAbsent(String.valueOf(year), k -> new YearData());

                                    Map<String, String> yearValues = new HashMap<>();
                                    for (Entry<String, Integer> entry : _TRACT_YEAR_BASED_FIELDS.entrySet()) {
                                        yearValues.put(entry.getKey(), rawYearData.substring(index, index + entry.getValue()));
                                        index += entry.getValue();
                                    }

                                    // YOST
                                    yearData.setYostQuintileState(StringUtils.trim(yearValues.get("yostStateBasedQuintile")));
                                    yearData.setYostQuintileUS(StringUtils.trim(yearValues.get("yostUsBasedQuintile")));

                                    // ACS Poverty
                                    yearData.setAcsPctPovAllRaces(cleanPoverty(lineNum, StringUtils.trim(yearValues.get("percentBelowPovertyAllRaces"))));
                                    yearData.setAcsPctPovWhite(cleanPoverty(lineNum, StringUtils.trim(yearValues.get("percentBelowPovertyWhite"))));
                                    yearData.setAcsPctPovBlack(cleanPoverty(lineNum, StringUtils.trim(yearValues.get("percentBelowPovertyBlack"))));
                                    yearData.setAcsPctPovAIAN(cleanPoverty(lineNum, StringUtils.trim(yearValues.get("percentBelowPovertyAmIndian"))));
                                    yearData.setAcsPctPovAsianNHOPI(cleanPoverty(lineNum, StringUtils.trim(yearValues.get("percentBelowPovertyAsian"))));
                                    yearData.setAcsPctPovWhiteNonHisp(cleanPoverty(lineNum, StringUtils.trim(yearValues.get("percentBelowPovertyWhiteNotHisp"))));
                                    yearData.setAcsPctPovHispanic(cleanPoverty(lineNum, StringUtils.trim(yearValues.get("percentBelowPovertyHisp"))));
                                }

                            }

                            line = reader.readLine();
                        }
                    }
                }
                catch (IOException e) {
                    throw new IllegalStateException("Unable to initialize year based tract data", e);
                }
            }
            _stateTractDataYearBasedInitialized.add(requestedState);
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    private String cleanPoverty(int lineNum, String value) {
        if (value.isEmpty())
            return value;

        if (value.length() != 5 || !NumberUtils.isDigits(value))
            throw new IllegalStateException("Invalid ACS poverty value at line " + lineNum + ": " + value);

        String left = value.substring(0, 3);
        String right = value.substring(3);

        if (left.startsWith("00"))
            left = left.substring(2);
        else if (left.startsWith("0"))
            left = left.substring(1);

        return left + "." + right;
    }

    public StateData getYearBasedTractData(String state) {
        _lock.readLock().lock();
        try {
            if (!isYearBasedTractDataInitialized(state))
                throw new IllegalStateException("Year-based census tract data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the Continuum algorithm.
     */
    public StateData getContinuumStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_continuumStateInitialized.contains(state))
                throw new IllegalStateException("Continuum data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the Continuum data has been initialized, false otherwise.
     */
    public boolean isContinuumDataInitialized(String requestedState) {
        _lock.readLock().lock();
        try {
            return _continuumStateInitialized.contains(requestedState);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given Continuum data (this call will make all other access to the data structure block).
     */
    public void initializeContinuumData(String requestedState, Map<String, Map<String, CountyData>> data) {
        _lock.writeLock().lock();
        try {
            if (!_continuumStateInitialized.contains(requestedState)) {
                for (Map.Entry<String, Map<String, CountyData>> stateEntry : data.entrySet()) {
                    if (!Objects.equals(stateEntry.getKey(), requestedState))
                        continue;
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, CountyData> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        countyData.setUrbanContinuum1993(countyEntry.getValue().getUrbanContinuum1993());
                        countyData.setUrbanContinuum2003(countyEntry.getValue().getUrbanContinuum2003());
                        countyData.setUrbanContinuum2013(countyEntry.getValue().getUrbanContinuum2013());
                    }
                }
            }
            _continuumStateInitialized.add(requestedState);
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the county at diagnosis analysis algorithm
     */
    public StateData getCountyAtDxAnalysisData(String state) {
        _lock.readLock().lock();
        try {
            if (!_countyAtDxAnalysisInitialized)
                throw new IllegalStateException("County at diagnosis analysis data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    public boolean isCountyAtDxAnalysisInitialized() {
        _lock.readLock().lock();
        try {
            return _countyAtDxAnalysisInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    public void initializeCountyAtDxAnalysisData(Map<String, Map<String, CountyData>> data) {
        _lock.writeLock().lock();
        try {
            if (!_countyAtDxAnalysisInitialized) {
                for (Map.Entry<String, Map<String, CountyData>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, CountyData> countyEntry : stateEntry.getValue().entrySet())
                        stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                }
            }
            _countyAtDxAnalysisInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the PRCDA algorithm.
     */
    public StateData getPrcdaData(String state) {
        _lock.readLock().lock();
        try {
            if (!_prcdaInitialized)
                throw new IllegalStateException("PRCDA data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the PRCDA data has been initialized, false otherwise.
     */
    public boolean isPrcdaDataInitialized() {
        _lock.readLock().lock();
        try {
            return _prcdaInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given PRCDA data (this call will make all other access to the data structure block).
     */
    public void initializePrcdaData(Map<String, Map<String, CountyData>> data) {
        _lock.writeLock().lock();
        try {
            if (!_prcdaInitialized) {
                for (Map.Entry<String, Map<String, CountyData>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, CountyData> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        countyData.setPRCDA(countyEntry.getValue().getPRCDA());
                    }
                }
            }
            _prcdaInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the UIHO algorithm.
     */
    public StateData getUihoData(String state) {
        _lock.readLock().lock();
        try {
            if (!_uihoInitialized)
                throw new IllegalStateException("UIHO data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the UIHO data has been initialized, false otherwise.
     */
    public boolean isUihoDataInitialized() {
        _lock.readLock().lock();
        try {
            return _uihoInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given UIHO data (this call will make all other access to the data structure block).
     */
    public void initializeUihoData(Map<String, Map<String, CountyData>> data) {
        _lock.writeLock().lock();
        try {
            if (!_uihoInitialized) {
                for (Map.Entry<String, Map<String, CountyData>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, CountyData> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        countyData.setUIHO(countyEntry.getValue().getUIHO());
                    }
                }
            }
            _uihoInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the Tract-Estimated Congressional District algorithm.
     */
    public StateData getTractEstCongressDistStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_tractEstCongressDistInitialized)
                throw new IllegalStateException("Tract-Estimated Congressional District data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the Tract-Estimated Congressional District data has been initialized, false otherwise.
     */
    public boolean isTractEstCongressDistDataInitialized() {
        _lock.readLock().lock();
        try {
            return _tractEstCongressDistInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given Tract-Estimated Congressional District data (this call will make all other access to the data structure block).
     */
    public void initializeTractEstCongressDistData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
            if (!_tractEstCongressDistInitialized) {
                for (Map.Entry<String, Map<String, Map<String, CensusData>>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, Map<String, CensusData>> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        for (Map.Entry<String, CensusData> censusEntry : countyEntry.getValue().entrySet()) {
                            CensusData censusData = countyData.getData().computeIfAbsent(censusEntry.getKey(), k -> new CensusData());
                            censusData.setTractEstCongressDist(censusEntry.getValue().getTractEstCongressDist());
                        }
                    }
                }
            }
            _tractEstCongressDistInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }
}
