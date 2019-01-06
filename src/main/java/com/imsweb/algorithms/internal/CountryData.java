package com.imsweb.algorithms.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// TODO FD add comments
public class CountryData {

    private static final CountryData _INSTANCE = new CountryData();

    public static CountryData getInstance() {
        return _INSTANCE;
    }

    private Map<String, StateData> _stateData = new HashMap<>();

    private boolean _rucaInitialized = false;

    private boolean _uricInitialized = false;

    private boolean _continuumInitialized = false;

    private boolean _povertyInitialied = false;

    private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();

    public StateData getRucaStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_rucaInitialized)
                throw new RuntimeException("RUCA data cannot be access before it has been initialized!");
            return _stateData.get(state);
        } finally {
            _lock.readLock().unlock();
        }
    }

    public boolean isRucaDataInitialized() {
        _lock.readLock().lock();
        try {
            return _rucaInitialized;
        } finally {
            _lock.readLock().unlock();
        }
    }

    public void initializeRucaData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
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

            _rucaInitialized = true;
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public StateData getUricStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_uricInitialized)
                throw new RuntimeException("URIC data cannot be access before it has been initialized!");
            return _stateData.get(state);
        } finally {
            _lock.readLock().unlock();
        }
    }

    public boolean isUricDataInitialized() {
        _lock.readLock().lock();
        try {
            return _uricInitialized;
        } finally {
            _lock.readLock().unlock();
        }
    }

    public void initializeUricData(Map<String, Map<String, Map<String, CensusData>>> data) {
        _lock.writeLock().lock();
        try {
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

            _uricInitialized = true;
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public StateData getContinuumStateData(String state) {
        _lock.readLock().lock();
        try {
            if (!_continuumInitialized)
                throw new RuntimeException("Continuum data cannot be access before it has been initialized!");
            return _stateData.get(state);
        } finally {
            _lock.readLock().unlock();
        }
    }

    public boolean isContinuumDataInitialized() {
        _lock.readLock().lock();
        try {
            return _continuumInitialized;
        } finally {
            _lock.readLock().unlock();
        }
    }

    public void initializeContinuumData(Map<String, Map<String, CountyData>> data) {
        _lock.writeLock().lock();
        try {
            for (Map.Entry<String, Map<String, CountyData>> stateEntry : data.entrySet()) {
                StateData stateData = _stateData.computeIfAbsent(stateEntry.getKey(), k -> new StateData());
                for (Map.Entry<String, CountyData> countyEntry : stateEntry.getValue().entrySet()) {
                    CountyData countyData = stateData.getData().computeIfAbsent(countyEntry.getKey(), k -> new CountyData());
                    countyData.setUrbanContinuum1993(countyEntry.getValue().getUrbanContinuum1993());
                    countyData.setUrbanContinuum2003(countyEntry.getValue().getUrbanContinuum2003());
                    countyData.setUrbanContinuum2013(countyEntry.getValue().getUrbanContinuum2013());
                }
            }

            _continuumInitialized = true;
        } finally {
            _lock.writeLock().unlock();
        }
    }
}
