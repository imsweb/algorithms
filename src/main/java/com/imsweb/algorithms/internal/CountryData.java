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
import java.util.Map;
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

    private static final String _SEER_CENSUS_DATA_FILE = "tract/tract.level.ses.2008_17.minimized.txt.gz";
    private static final String _SEER_CENSUS_DATA_FILE_YEAR_BASED = "tract/tract.level.ses.2008_17.minimized.year.based.txt.gz";

    private static final Map<String, String> _STATES = new HashMap<>();

    static {
        _STATES.put("01", "AL");
        _STATES.put("02", "AK");
        _STATES.put("04", "AZ");
        _STATES.put("05", "AR");
        _STATES.put("06", "CA");
        _STATES.put("08", "CO");
        _STATES.put("09", "CT");
        _STATES.put("10", "DE");
        _STATES.put("11", "DC");
        _STATES.put("12", "FL");
        _STATES.put("13", "GA");
        _STATES.put("15", "HI");
        _STATES.put("16", "ID");
        _STATES.put("17", "IL");
        _STATES.put("18", "IN");
        _STATES.put("19", "IA");
        _STATES.put("20", "KS");
        _STATES.put("21", "KY");
        _STATES.put("22", "LA");
        _STATES.put("23", "ME");
        _STATES.put("24", "MD");
        _STATES.put("25", "MA");
        _STATES.put("26", "MI");
        _STATES.put("27", "MN");
        _STATES.put("28", "MS");
        _STATES.put("29", "MO");
        _STATES.put("30", "MT");
        _STATES.put("31", "NE");
        _STATES.put("32", "NV");
        _STATES.put("33", "NH");
        _STATES.put("34", "NJ");
        _STATES.put("35", "NM");
        _STATES.put("36", "NY");
        _STATES.put("37", "NC");
        _STATES.put("38", "ND");
        _STATES.put("39", "OH");
        _STATES.put("40", "OK");
        _STATES.put("41", "OR");
        _STATES.put("42", "PA");
        _STATES.put("43", "PR");
        _STATES.put("44", "RI");
        _STATES.put("45", "SC");
        _STATES.put("46", "SD");
        _STATES.put("47", "TN");
        _STATES.put("48", "TX");
        _STATES.put("49", "UT");
        _STATES.put("50", "VT");
        _STATES.put("51", "VA");
        _STATES.put("53", "WA");
        _STATES.put("54", "WV");
        _STATES.put("55", "WI");
        _STATES.put("56", "WY");
        _STATES.put("60", "AS");
        _STATES.put("64", "FM");
        _STATES.put("66", "GU");
        _STATES.put("68", "MH");
        _STATES.put("69", "MP");
        _STATES.put("70", "PW");
        _STATES.put("74", "UM");
        _STATES.put("78", "VI");
        _STATES.put("99", "YY");
    }

    public static Map<String, String> getStates() {
        return Collections.unmodifiableMap(_STATES);
    }

    // following start/end columns are shared between the two tract data files
    public static final int STATE_FIPS_START = 1; // 2 char
    public static final int STATE_FIPS_END = 2;
    public static final int COUNTY_FIPS_START = 3; // 3 char
    public static final int COUNTY_FIPS_END = 5;
    public static final int TRACT_START = 6; // 6 char
    public static final int TRACT_END = 11;

    // following start/end columns are only applicable to the year-based tract data file
    public static final int YEAR_START = 12; // 4 char
    public static final int YEAR_END = 15;

    public static final int YOST_US_BASED_QUINTILE_START = 16; // 1 char
    public static final int YOST_US_BASED_QUINTILE_END = 16;
    public static final int YOST_STATE_BASED_QUINTILE_START = 17; // 1 char
    public static final int YOST_STATE_BASED_QUINTILE_END = 17;
    public static final int PERCENT_BEL_POV_ALL_RACES_START = 18; // 5 char
    public static final int PERCENT_BEL_POV_ALL_RACES_END = 22;
    public static final int PERCENT_BEL_POV_WHITE_START = 23; // 5 char
    public static final int PERCENT_BEL_POV_WHITE_END = 27;
    public static final int PERCENT_BEL_POV_BLACK_START = 28; // 5 char
    public static final int PERCENT_BEL_POV_BLACK_END = 32;
    public static final int PERCENT_BEL_POV_AM_INDIAN_START = 33; // 5 char
    public static final int PERCENT_BEL_POV_AM_INDIAN_END = 37;
    public static final int PERCENT_BEL_POV_ASIAN_START = 38; // 5 char
    public static final int PERCENT_BEL_POV_ASIAN_END = 42;
    public static final int PERCENT_BEL_POV_WHILE_NOT_HISP_START = 43; // 5 char
    public static final int PERCENT_BEL_POV_WHILE_NOT_HISP_END = 47;
    public static final int PERCENT_BEL_POV_HISP_START = 48; // 5 char
    public static final int PERCENT_BEL_POV_HISP_END = 52;

    // following start/end columns are only applicable to the non-year-based tract data file
    public static final int RUCA_2010_A_START = 12; // 1 char
    public static final int RUCA_2010_A_END = 12;
    public static final int RUCA_2010_C_START = 13; // 1 char
    public static final int RUCA_2010_C_END = 13;
    public static final int URIC_2010_A_START = 14; // 1 char
    public static final int URIC_2010_A_END = 14;
    public static final int URIC_2010_B_START = 15; // 1 char
    public static final int URIC_2010_B_END = 15;
    public static final int URIC_2010_C_START = 16; // 1 char
    public static final int URIC_2010_C_END = 16;
    public static final int ZONE_ID_START = 17; // 10 char
    public static final int ZONE_ID_END = 26;
    public static final int NAACCR_POV_INDICATOR_START = 27; // 1 char
    public static final int NAACCR_POV_INDICATOR_END = 27;
    public static final int CDC_SUBCOUNTY_5K_START = 28; // 11 char
    public static final int CDC_SUBCOUNTY_5K_END = 38;
    public static final int CDC_SUBCOUNTY_20K_START = 39; // 11 char
    public static final int CDC_SUBCOUNTY_20K_END = 49;

    // singleton instance
    private static final CountryData _INSTANCE = new CountryData();

    // unique access to the singleton
    public static CountryData getInstance() {
        return _INSTANCE;
    }

    // shared internal data structure; sates mapped by state abbreviation
    private final Map<String, StateData> _stateData = new HashMap<>();

    private final Set<String> _stateTractDataInitialized = new HashSet<>();

    private final Set<String> _stateTractDataYearBasedInitialized = new HashSet<>();

    // the different data type that can be registered
    private boolean _rucaInitialized = false; // in file
    private boolean _uricInitialized = false; // in file
    private boolean _continuumInitialized = false;
    private boolean _povertyInitialized = false; // in file
    private boolean _countyAtDxAnalysisInitialized = false;
    private boolean _prcdaInitialized = false;
    private boolean _uihoInitialized = false;
    private boolean _tractEstCongressDistInitialized = false;
    private boolean _cancerReportingZoneInitialized = false; // in file

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
            _rucaInitialized = false;
            _uricInitialized = false;
            _continuumInitialized = false;
            _povertyInitialized = false;
            _countyAtDxAnalysisInitialized = false;
            _prcdaInitialized = false;
            _uihoInitialized = false;
            _cancerReportingZoneInitialized = false;
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
                            String state = _STATES.get(line.substring(STATE_FIPS_START - 1, STATE_FIPS_END));
                            String county = line.substring(COUNTY_FIPS_START - 1, COUNTY_FIPS_END);
                            String tract = line.substring(TRACT_START - 1, TRACT_END);

                            if (Objects.equals(state, requestedState)) {
                                StateData stateData = _stateData.computeIfAbsent(state, k -> new StateData());
                                CountyData countyData = stateData.getData().computeIfAbsent(county, k -> new CountyData());
                                CensusData censusData = countyData.getData().computeIfAbsent(tract, k -> new CensusData());

                                // URAC

                                // URIC

                                // NPCR EPHT SubCounty
                                censusData.setEpht2010GeoId5k(StringUtils.leftPad(StringUtils.trimToNull(line.substring(CDC_SUBCOUNTY_5K_START - 1, CDC_SUBCOUNTY_5K_END)), 11, '0'));
                                censusData.setEpht2010GeoId20k(StringUtils.leftPad(StringUtils.trimToNull(line.substring(CDC_SUBCOUNTY_20K_START - 1, CDC_SUBCOUNTY_20K_END)), 11, '0'));

                                // Cancer Reporting Zone

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
                try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(_SEER_CENSUS_DATA_FILE_YEAR_BASED)) {
                    if (is == null)
                        throw new IllegalStateException("Unable to get year-based SEER census tract data file");
                    try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new GZIPInputStream(is), StandardCharsets.US_ASCII))) {
                        String line = reader.readLine();
                        while (line != null) {
                            int lineNum = reader.getLineNumber();

                            String state = _STATES.get(line.substring(STATE_FIPS_START - 1, STATE_FIPS_END));
                            String county = line.substring(COUNTY_FIPS_START - 1, COUNTY_FIPS_END);
                            String tract = line.substring(TRACT_START - 1, TRACT_END);
                            String year = line.substring(YEAR_START - 1, YEAR_END);

                            if (Objects.equals(state, requestedState)) {
                                StateData stateData = _stateData.computeIfAbsent(state, k -> new StateData());
                                CountyData countyData = stateData.getData().computeIfAbsent(county, k -> new CountyData());
                                CensusData censusData = countyData.getData().computeIfAbsent(tract, k -> new CensusData());
                                YearData yearData = censusData.getData().computeIfAbsent(year, k -> new YearData());

                                // YOST
                                yearData.setYostQuintileState(StringUtils.trim(line.substring(YOST_STATE_BASED_QUINTILE_START - 1, YOST_STATE_BASED_QUINTILE_END)));
                                yearData.setYostQuintileUS(StringUtils.trim(line.substring(YOST_US_BASED_QUINTILE_START - 1, YOST_US_BASED_QUINTILE_END)));

                                // ACS Poverty
                                yearData.setAcsPctPovAllRaces(cleanPoverty(lineNum, StringUtils.trim(line.substring(PERCENT_BEL_POV_ALL_RACES_START - 1, PERCENT_BEL_POV_ALL_RACES_END))));
                                yearData.setAcsPctPovWhite(cleanPoverty(lineNum, StringUtils.trim(line.substring(PERCENT_BEL_POV_WHITE_START - 1, PERCENT_BEL_POV_WHITE_END))));
                                yearData.setAcsPctPovBlack(cleanPoverty(lineNum, StringUtils.trim(line.substring(PERCENT_BEL_POV_BLACK_START - 1, PERCENT_BEL_POV_BLACK_END))));
                                yearData.setAcsPctPovAIAN(cleanPoverty(lineNum, StringUtils.trim(line.substring(PERCENT_BEL_POV_AM_INDIAN_START - 1, PERCENT_BEL_POV_AM_INDIAN_END))));
                                yearData.setAcsPctPovAsianNHOPI(cleanPoverty(lineNum, StringUtils.trim(line.substring(PERCENT_BEL_POV_ASIAN_START - 1, PERCENT_BEL_POV_ASIAN_END))));
                                yearData.setAcsPctPovWhiteNonHisp(
                                        cleanPoverty(lineNum, StringUtils.trim(line.substring(PERCENT_BEL_POV_WHILE_NOT_HISP_START - 1, PERCENT_BEL_POV_WHILE_NOT_HISP_END))));
                                yearData.setAcsPctPovHispanic(cleanPoverty(lineNum, StringUtils.trim(line.substring(PERCENT_BEL_POV_HISP_START - 1, PERCENT_BEL_POV_HISP_END))));

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
     * Returns requested state data to be used by the RUCA algorithm.
     */
    public StateData getRucaStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_rucaInitialized)
                throw new IllegalStateException("RUCA data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the RUCA data has been initialized, false otherwise.
     */
    public boolean isRucaDataInitialized() {
        _lock.readLock().lock();
        try {
            return _rucaInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given RUCA data (this call will make all other access to the data structure block).
     */
    public void initializeRucaData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
            if (!_rucaInitialized) {
                for (Map.Entry<String, Map<String, Map<String, CensusData>>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, Map<String, CensusData>> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        for (Map.Entry<String, CensusData> censusEntry : countyEntry.getValue().entrySet()) {
                            CensusData censusData = countyData.getData().computeIfAbsent(censusEntry.getKey(), k -> new CensusData());
                            censusData.setCommutingArea2000(censusEntry.getValue().getCommutingArea2000());
                            censusData.setCommutingArea2010(censusEntry.getValue().getCommutingArea2010());
                        }
                    }
                }
            }
            _rucaInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the URIC algorithm.
     */
    public StateData getUricStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_uricInitialized)
                throw new IllegalStateException("URIC data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the URIC data has been initialized, false otherwise.
     */
    public boolean isUricDataInitialized() {
        _lock.readLock().lock();
        try {
            return _uricInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given URIC data (this call will make all other access to the data structure block).
     */
    public void initializeUricData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
            if (!_uricInitialized) {
                for (Map.Entry<String, Map<String, Map<String, CensusData>>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, Map<String, CensusData>> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        for (Map.Entry<String, CensusData> censusEntry : countyEntry.getValue().entrySet()) {
                            CensusData censusData = countyData.getData().computeIfAbsent(censusEntry.getKey(), k -> new CensusData());
                            censusData.setIndicatorCode2000(censusEntry.getValue().getIndicatorCode2000());
                            censusData.setIndicatorCodePercentage2000(censusEntry.getValue().getIndicatorCodePercentage2000());
                            censusData.setIndicatorCode2010(censusEntry.getValue().getIndicatorCode2010());
                            censusData.setIndicatorCodePercentage2010(censusEntry.getValue().getIndicatorCodePercentage2010());
                        }
                    }
                }
            }
            _uricInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the Continuum algorithm.
     */
    public StateData getContinuumStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_continuumInitialized)
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
    public boolean isContinuumDataInitialized() {
        _lock.readLock().lock();
        try {
            return _continuumInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given Continuum data (this call will make all other access to the data structure block).
     */
    public void initializeContinuumData(Map<String, Map<String, CountyData>> data) {
        _lock.writeLock().lock();
        try {
            if (!_continuumInitialized) {
                for (Map.Entry<String, Map<String, CountyData>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, CountyData> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        countyData.setUrbanContinuum1993(countyEntry.getValue().getUrbanContinuum1993());
                        countyData.setUrbanContinuum2003(countyEntry.getValue().getUrbanContinuum2003());
                        countyData.setUrbanContinuum2013(countyEntry.getValue().getUrbanContinuum2013());
                    }
                }
            }
            _continuumInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the poverty indicator algorithm.
     */
    public StateData getPovertyData(String state) {
        _lock.readLock().lock();
        try {
            if (!_povertyInitialized)
                throw new IllegalStateException("Poverty indicator data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the poverty indicator data has been initialized, false otherwise.
     */
    public boolean isPovertyDataInitialized() {
        _lock.readLock().lock();
        try {
            return _povertyInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given poverty indicator data (this call will make all other access to the data structure block).
     */
    public void initializePovertyData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
            if (!_povertyInitialized) {
                for (Map.Entry<String, Map<String, Map<String, CensusData>>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, Map<String, CensusData>> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        for (Map.Entry<String, CensusData> censusEntry : countyEntry.getValue().entrySet()) {
                            CensusData censusData = countyData.getData().computeIfAbsent(censusEntry.getKey(), k -> new CensusData());
                            censusData.setPovertyIndicators(censusEntry.getValue().getPovertyIndicators());
                        }
                    }
                }
            }
            _povertyInitialized = true;
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

    /**
     * Returns requested state data to be used by the CancerReportingZone algorithm.
     */
    public StateData getCancerReportingZoneData(String state) {
        _lock.readLock().lock();
        try {
            if (!_cancerReportingZoneInitialized)
                throw new IllegalStateException("CancerReportingZone data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the CancerReportingZone data has been initialized, false otherwise.
     */
    public boolean isCancerReportingZoneDataInitialized() {
        _lock.readLock().lock();
        try {
            return _cancerReportingZoneInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given CancerReportingZone data (this call will make all other access to the data structure block).
     */
    public void initializeCancerReportingZoneData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
            if (!_cancerReportingZoneInitialized) {
                for (Map.Entry<String, Map<String, Map<String, CensusData>>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, Map<String, CensusData>> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        for (Map.Entry<String, CensusData> censusEntry : countyEntry.getValue().entrySet()) {
                            CensusData censusData = countyData.getData().computeIfAbsent(censusEntry.getKey(), k -> new CensusData());
                            censusData.setCancerReportingZone(censusEntry.getValue().getCancerReportingZone());
                        }
                    }
                }
            }
            _cancerReportingZoneInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

}
