/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.StateCountyTractInputDto;
import com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorInputDto;
import com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorUtils;
import com.imsweb.algorithms.ruralurban.RuralUrbanUtils;

public class CountryDataTest {

    @Test
    @SuppressWarnings({"java:S2925", "ResultOfMethodCallIgnored"}) // calling Thread.sleep
    public void testConcurrency() throws InterruptedException {

        // individual algorithms test their own data; this test is making sure the combined access
        // is working as expected from a concurrency point of view

        // we will use this simple URAC calculation...
        final StateCountyTractInputDto rucaInput = new StateCountyTractInputDto();
        rucaInput.setAddressAtDxState("AL");
        rucaInput.setCountyAtDxAnalysis("001");
        rucaInput.setCensusTract2010(null);
        final String rucaExpectedValue = "D";
        Assert.assertEquals(rucaExpectedValue, RuralUrbanUtils.computeRuralUrbanCommutingArea(rucaInput).getRuralUrbanCommutingArea2010());

        // we will use this simple URIC calculation...
        final StateCountyTractInputDto uricInput = new StateCountyTractInputDto();
        uricInput.setAddressAtDxState("AL");
        uricInput.setCountyAtDxAnalysis("001");
        uricInput.setCensusTract2010("020200");
        final String uricExpectedValue = "1";
        Assert.assertEquals(uricExpectedValue, RuralUrbanUtils.computeUrbanRuralIndicatorCode(uricInput).getUrbanRuralIndicatorCode2010());

        // we will use this simple Continuum calculation...
        final StateCountyTractInputDto continuumInput = new StateCountyTractInputDto();
        continuumInput.setAddressAtDxState("AL");
        continuumInput.setCountyAtDxAnalysis("001");
        final String continuumExpectedValue = "02";
        Assert.assertEquals(continuumExpectedValue, RuralUrbanUtils.computeRuralUrbanContinuum(continuumInput).getRuralUrbanContinuum2013());

        // we will use this simple poverty indicator calculation...
        final CensusTractPovertyIndicatorInputDto povertyInput = new CensusTractPovertyIndicatorInputDto();
        povertyInput.setAddressAtDxState("AL");
        povertyInput.setCountyAtDxAnalysis("001");
        povertyInput.setCensusTract2010("020200");
        povertyInput.setDateOfDiagnosisYear("2010");
        final String povertyExpectedValue = "3";
        Assert.assertEquals(povertyExpectedValue, CensusTractPovertyIndicatorUtils.computePovertyIndicator(povertyInput).getCensusTractPovertyIndicator());

        Random random = new Random();
        final AtomicInteger numReset = new AtomicInteger();
        final List<String> errors = new ArrayList<>();
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < 50; i++) {
                pool.submit(() -> {
                    // sleep a random (although small) amount of time
                    try {
                        Thread.sleep(random.nextLong() * 100);
                    }
                    catch (InterruptedException e) {
                        errors.add("Threads got interrupted?");
                    }

                    // either compute one of the variables, or reset all the data
                    switch (random.nextInt() * 5) {
                        case 0:
                            CountryData.getInstance().uninitializeAllData();
                            numReset.getAndIncrement();
                            break;
                        case 1:
                            String uricVal = RuralUrbanUtils.computeUrbanRuralIndicatorCode(uricInput).getUrbanRuralIndicatorCode2010();
                            if (!uricExpectedValue.equals(uricVal))
                                errors.add("Bad URIC value: " + uricVal);
                            break;
                        case 2:
                            String rucaVal = RuralUrbanUtils.computeRuralUrbanCommutingArea(rucaInput).getRuralUrbanCommutingArea2010();
                            if (!rucaExpectedValue.equals(rucaVal))
                                errors.add("Bad RUCA value: " + rucaVal);
                            break;
                        case 3:
                            String continuumVal = RuralUrbanUtils.computeRuralUrbanContinuum(continuumInput).getRuralUrbanContinuum2013();
                            if (!continuumExpectedValue.equals(continuumVal))
                                errors.add("Bad Continuum value: " + continuumVal);
                            break;
                        case 4:
                            String povertyVal = CensusTractPovertyIndicatorUtils.computePovertyIndicator(povertyInput).getCensusTractPovertyIndicator();
                            if (!povertyExpectedValue.equals(povertyVal))
                                errors.add("Bad Poverty value: " + povertyVal);
                            break;
                        default:
                            errors.add("SHOULD NEVER HIT DEFAULT CASE!");
                    }
                });
            }
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
            if (!errors.isEmpty())
                Assert.fail(String.join("\n", errors));
            if (numReset.get() == 0)
                Assert.fail("There should have been at least one reset, this test is not running as expected");
        }
    }
}
