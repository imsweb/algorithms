/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.imsweb.algorithms.nhia.NhiaUtils;

public class Algorithms {

    private static Map<String, Algorithm> _CACHED_ALGORITHMS = new HashMap<>();

    private static ReentrantReadWriteLock _LOCK = new ReentrantReadWriteLock();

    public static void initialize() {
        _LOCK.writeLock().lock();
        try {
            _CACHED_ALGORITHMS.put(NhiaUtils.ALG_INFO, createAlgorithmNhia());
        }
        finally {
            _LOCK.writeLock().unlock();
        }
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
        };
    }
}
