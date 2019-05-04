/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.imsweb.algorithms.nhia.NhiaUtils;

public class Algorithms {

    private static Map<String, AlgorithmField> _CACHED_FIELDS = new HashMap<>();

    static {
        addField(_CACHED_FIELDS, AlgorithmField.of("spanishHispanicOrigin", 190, "Spanish/Hispanic Origin", 1));
    }

    private static void addField(Map<String, AlgorithmField> cache, AlgorithmField field) {
        cache.put(field.getId(), field);
    }

    private static Map<String, Algorithm> _CACHED_ALGORITHMS = new HashMap<>();

    private static ReentrantReadWriteLock _LOCK = new ReentrantReadWriteLock();

    public static void initialize() {
        _LOCK.writeLock().lock();
        try {
            addAlgorithm(_CACHED_ALGORITHMS, createAlgorithmNhia());
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    private static void addAlgorithm(Map<String, Algorithm> cache, Algorithm algorithm) {
        cache.put(algorithm.getId(), algorithm);
    }

    public static void registerAlgorithm(Algorithm algorithm) {

        // TODO validate what can be validated...

        _LOCK.writeLock().lock();
        try {
            _CACHED_ALGORITHMS.put(algorithm.getId(), algorithm);
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    private static Algorithm createAlgorithmNhia() {
        return new Algorithm() {

            @Override
            public String getId() {
                return NhiaUtils.ALG_INFO;
            }

            @Override
            public String getName() {
                return NhiaUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return NhiaUtils.ALG_VERSION;
            }

            @Override
            public List<AlgorithmParam> getParameters() {
                return null;
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                return null;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                return null;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {
                return null;
            }
        };
    }
}
