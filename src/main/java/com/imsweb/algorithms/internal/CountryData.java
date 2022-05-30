/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
public class CountryData {

    private static final Map<String, String> _STATES = new HashMap<>();

    static {
        _STATES.put("AK", "02");
        _STATES.put("AL", "01");
        _STATES.put("AR", "05");
        _STATES.put("AS", "60");
        _STATES.put("AZ", "04");
        _STATES.put("CA", "06");
        _STATES.put("CO", "08");
        _STATES.put("CT", "09");
        _STATES.put("DC", "11");
        _STATES.put("DE", "10");
        _STATES.put("FL", "12");
        _STATES.put("FM", "64");
        _STATES.put("GA", "13");
        _STATES.put("GU", "66");
        _STATES.put("HI", "15");
        _STATES.put("IA", "19");
        _STATES.put("ID", "16");
        _STATES.put("IL", "17");
        _STATES.put("IN", "18");
        _STATES.put("KS", "20");
        _STATES.put("KY", "21");
        _STATES.put("LA", "22");
        _STATES.put("MA", "25");
        _STATES.put("MD", "24");
        _STATES.put("ME", "23");
        _STATES.put("MH", "68");
        _STATES.put("MI", "26");
        _STATES.put("MN", "27");
        _STATES.put("MO", "29");
        _STATES.put("MP", "69");
        _STATES.put("MS", "28");
        _STATES.put("MT", "30");
        _STATES.put("NC", "37");
        _STATES.put("ND", "38");
        _STATES.put("NE", "31");
        _STATES.put("NH", "33");
        _STATES.put("NJ", "34");
        _STATES.put("NM", "35");
        _STATES.put("NV", "32");
        _STATES.put("NY", "36");
        _STATES.put("OH", "39");
        _STATES.put("OK", "40");
        _STATES.put("OR", "41");
        _STATES.put("PA", "42");
        _STATES.put("PR", "43");
        _STATES.put("PW", "70");
        _STATES.put("RI", "44");
        _STATES.put("SC", "45");
        _STATES.put("SD", "46");
        _STATES.put("TN", "47");
        _STATES.put("TX", "48");
        _STATES.put("UM", "74");
        _STATES.put("UT", "49");
        _STATES.put("VA", "51");
        _STATES.put("VI", "78");
        _STATES.put("VT", "50");
        _STATES.put("WA", "53");
        _STATES.put("WI", "55");
        _STATES.put("WV", "54");
        _STATES.put("WY", "56");
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
    public static final int PERCENT_BEL_POV_OTHER_MULT_START = 43; // 5 char
    public static final int PERCENT_BEL_POV_OTHER_MULT_END = 47;
    public static final int PERCENT_BEL_POV_WHILE_NOT_HISP_START = 48; // 5 char
    public static final int PERCENT_BEL_POV_WHILE_NOT_HISP_END = 52;
    public static final int PERCENT_BEL_POV_HISP_START = 53; // 5 char
    public static final int PERCENT_BEL_POV_HISP_END = 57;

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

    // the different data type that can be registered
    private boolean _rucaInitialized = false; // in file
    private boolean _uricInitialized = false; // in file
    private boolean _continuumInitialized = false; // NOT IN FILE???
    private boolean _povertyInitialized = false; // in file
    private boolean _countyAtDxAnalysisInitialized = false;
    private boolean _prcdaInitialized = false;
    private boolean _uihoInitialized = false;
    private boolean _yostAcsPovertyInitialized = false; // in file
    private boolean _tractEstCongressDistInitialized = false; // ???
    private boolean _ephtSubCountyInitialized = false; // in file
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
            _rucaInitialized = false;
            _uricInitialized = false;
            _continuumInitialized = false;
            _povertyInitialized = false;
            _countyAtDxAnalysisInitialized = false;
            _prcdaInitialized = false;
            _uihoInitialized = false;
            _yostAcsPovertyInitialized = false;
            _cancerReportingZoneInitialized = false;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the RUCA algorithm.
     */
    public StateData getRucaStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_rucaInitialized)
                throw new RuntimeException("RUCA data cannot be access before it has been initialized!");
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
                throw new RuntimeException("URIC data cannot be access before it has been initialized!");
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
                throw new RuntimeException("Continuum data cannot be access before it has been initialized!");
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
                throw new RuntimeException("Poverty indicator data cannot be access before it has been initialized!");
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
                throw new RuntimeException("County at diagnosis analysis data cannot be access before it has been initialized!");
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
                throw new RuntimeException("PRCDA data cannot be access before it has been initialized!");
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
                throw new RuntimeException("UIHO data cannot be access before it has been initialized!");
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
                throw new RuntimeException("Tract-Estimated Congressional District data cannot be access before it has been initialized!");
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
     * Returns requested state data to be used by the Yost/ACS Poverty algorithm.
     */
    public StateData getYostAcsPovertyData(String state) {
        _lock.readLock().lock();
        try {
            if (!_yostAcsPovertyInitialized)
                throw new RuntimeException("Yost/ACS Poverty data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the Yost/ACS Poverty data has been initialized, false otherwise.
     */
    public boolean isYostAcsPovertyDataInitialized() {
        _lock.readLock().lock();
        try {
            return _yostAcsPovertyInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given Yost/ACS Poverty data (this call will make all other access to the data structure block).
     */
    public void initializeYostAcsPovertyData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
            if (!_yostAcsPovertyInitialized) {
                for (Map.Entry<String, Map<String, Map<String, CensusData>>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, Map<String, CensusData>> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        for (Map.Entry<String, CensusData> censusEntry : countyEntry.getValue().entrySet()) {
                            CensusData censusData = countyData.getData().computeIfAbsent(censusEntry.getKey(), k -> new CensusData());

                            censusData.setAcsPctPov0610AIAN(censusEntry.getValue().getAcsPctPov0610AIAN());
                            censusData.setAcsPctPov0610AllRaces(censusEntry.getValue().getAcsPctPov0610AllRaces());
                            censusData.setAcsPctPov0610AsianNHOPI(censusEntry.getValue().getAcsPctPov0610AsianNHOPI());
                            censusData.setAcsPctPov0610Black(censusEntry.getValue().getAcsPctPov0610Black());
                            censusData.setAcsPctPov0610Hispanic(censusEntry.getValue().getAcsPctPov0610Hispanic());
                            censusData.setAcsPctPov0610OtherMulti(censusEntry.getValue().getAcsPctPov0610OtherMulti());
                            censusData.setAcsPctPov0610White(censusEntry.getValue().getAcsPctPov0610White());
                            censusData.setAcsPctPov0610WhiteNonHisp(censusEntry.getValue().getAcsPctPov0610WhiteNonHisp());
                            censusData.setYostQuintile0610State(censusEntry.getValue().getYostQuintile0610State());
                            censusData.setYostQuintile0610US(censusEntry.getValue().getYostQuintile0610US());

                            censusData.setAcsPctPov1014AIAN(censusEntry.getValue().getAcsPctPov1014AIAN());
                            censusData.setAcsPctPov1014AllRaces(censusEntry.getValue().getAcsPctPov1014AllRaces());
                            censusData.setAcsPctPov1014AsianNHOPI(censusEntry.getValue().getAcsPctPov1014AsianNHOPI());
                            censusData.setAcsPctPov1014Black(censusEntry.getValue().getAcsPctPov1014Black());
                            censusData.setAcsPctPov1014Hispanic(censusEntry.getValue().getAcsPctPov1014Hispanic());
                            censusData.setAcsPctPov1014OtherMulti(censusEntry.getValue().getAcsPctPov1014OtherMulti());
                            censusData.setAcsPctPov1014White(censusEntry.getValue().getAcsPctPov1014White());
                            censusData.setAcsPctPov1014WhiteNonHisp(censusEntry.getValue().getAcsPctPov1014WhiteNonHisp());
                            censusData.setYostQuintile1014State(censusEntry.getValue().getYostQuintile1014State());
                            censusData.setYostQuintile1014US(censusEntry.getValue().getYostQuintile1014US());

                            censusData.setAcsPctPov1418AIAN(censusEntry.getValue().getAcsPctPov1418AIAN());
                            censusData.setAcsPctPov1418AllRaces(censusEntry.getValue().getAcsPctPov1418AllRaces());
                            censusData.setAcsPctPov1418AsianNHOPI(censusEntry.getValue().getAcsPctPov1418AsianNHOPI());
                            censusData.setAcsPctPov1418Black(censusEntry.getValue().getAcsPctPov1418Black());
                            censusData.setAcsPctPov1418Hispanic(censusEntry.getValue().getAcsPctPov1418Hispanic());
                            censusData.setAcsPctPov1418OtherMulti(censusEntry.getValue().getAcsPctPov1418OtherMulti());
                            censusData.setAcsPctPov1418White(censusEntry.getValue().getAcsPctPov1418White());
                            censusData.setAcsPctPov1418WhiteNonHisp(censusEntry.getValue().getAcsPctPov1418WhiteNonHisp());
                            censusData.setYostQuintile1418State(censusEntry.getValue().getYostQuintile1418State());
                            censusData.setYostQuintile1418US(censusEntry.getValue().getYostQuintile1418US());
                        }
                    }
                }
            }
            _yostAcsPovertyInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * Returns requested state data to be used by the EPHT SubCounty algorithm.
     */
    public StateData getEphtSubCountyData(String state) {
        _lock.readLock().lock();
        try {
            if (!_ephtSubCountyInitialized)
                throw new RuntimeException("EPHT SubCounty data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the EPHT SubCounty data has been initialized, false otherwise.
     */
    public boolean isEphtSubCountyDataInitialized() {
        _lock.readLock().lock();
        try {
            return _ephtSubCountyInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given EPHT SubCounty data (this call will make all other access to the data structure block).
     */
    public void initializeEphtSubCountyData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
            if (!_ephtSubCountyInitialized) {
                for (Map.Entry<String, Map<String, Map<String, CensusData>>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, Map<String, CensusData>> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        for (Map.Entry<String, CensusData> censusEntry : countyEntry.getValue().entrySet()) {
                            CensusData censusData = countyData.getData().computeIfAbsent(censusEntry.getKey(), k -> new CensusData());
                            censusData.setEpht2010GeoId5k(censusEntry.getValue().getEpht2010GeoId5k());
                            censusData.setEpht2010GeoId20k(censusEntry.getValue().getEpht2010GeoId20k());
                        }
                    }
                }
            }
            _ephtSubCountyInitialized = true;
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
                throw new RuntimeException("CancerReportingZone data cannot be access before it has been initialized!");
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
