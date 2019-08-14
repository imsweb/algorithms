/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

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

    // singleton instance
    private static final CountryData _INSTANCE = new CountryData();

    // unique access to the singleton
    public static CountryData getInstance() {
        return _INSTANCE;
    }

    // shared internal data structure; sates mapped by state abbreviation
    private Map<String, StateData> _stateData = new HashMap<>();

    // the different data type that can be registered
    private boolean _rucaInitialized = false;
    private boolean _uricInitialized = false;
    private boolean _continuumInitialized = false;
    private boolean _povertyInitialized = false;
    private boolean _countyAtDxAnalysisInitialized = false;
    private boolean _prcdaUihoInitialized = false;

    // internal lock to control concurrency
    private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();

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
            _prcdaUihoInitialized = false;
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
     * Returns requested state data to be used by the PRCDA/UIHO algorithm.
     */
    public StateData getPrcdaUihoData(String state) {
        _lock.readLock().lock();
        try {
            if (!_prcdaUihoInitialized)
                throw new RuntimeException("PRCDA/UIHO data cannot be access before it has been initialized!");
            return _stateData.get(state);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns true if the PRCDA/UIHO data has been initialized, false otherwise.
     */
    public boolean isPrcdaUihoDataInitialized() {
        _lock.readLock().lock();
        try {
            return _prcdaUihoInitialized;
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Initializes the given Continuum data (this call will make all other access to the data structure block).
     */
    public void initializePrcdaUihoData(Map<String, Map<String, CountyData>> data) {
        _lock.writeLock().lock();
        try {
            if (!_prcdaUihoInitialized) {
                for (Map.Entry<String, Map<String, CountyData>> stateEntry : data.entrySet()) {
                    StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                    for (Map.Entry<String, CountyData> countyEntry : stateEntry.getValue().entrySet()) {
                        CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                        countyData.setPRCDA(countyEntry.getValue().getPRCDA());
                        countyData.setUIHO(countyEntry.getValue().getUIHO());
                        countyData.setUIHOFacility(countyEntry.getValue().getUIHOFacility());
                    }
                }
            }
            _prcdaUihoInitialized = true;
        }
        finally {
            _lock.writeLock().unlock();
        }
    }
}
