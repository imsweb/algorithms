/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcda;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

import static com.imsweb.algorithms.prcda.PrcdaUtils.ENTIRE_STATE_NON_PRCDA;
import static com.imsweb.algorithms.prcda.PrcdaUtils.ENTIRE_STATE_PRCDA;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_NO;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcda.PrcdaUtils.PRCDA_YES;
import static com.imsweb.algorithms.prcda.PrcdaUtils.isCountyAtDxValid;
import static com.imsweb.algorithms.prcda.PrcdaUtils.isStateAtDxValid;

/**
 * The purpose of this class is to get the PRCDA for the provided
 * state of dx and county of dx from the csv file lookup.  This implementation is memory
 * consumer. If there is a database, it is better to use another implementation.
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public class PrcdaDataProvider {

    private enum PrcdaYear { YR2017, YR2020;
        public static PrcdaYear getDefault() {
            return YR2020;
        }
    }

    public String getPrcda(String state, String county) {
        return getPrcdaData(PrcdaYear.getDefault(), state, county);
    }

    public String getPrcda2017(String state, String county) {
        return getPrcdaData(PrcdaYear.YR2017, state, county);
    }

    private String getPrcdaData(PrcdaYear prcdaYear, String state, String county) {
        prcdaYear = prcdaYear == null ? PrcdaYear.getDefault() : prcdaYear;

        if (!isStateAtDxValid(state)) {
            return PRCDA_UNKNOWN;
        }
        else if (ENTIRE_STATE_PRCDA.contains(state)) {
            return PRCDA_YES;
        }
        else if (ENTIRE_STATE_NON_PRCDA.contains(state)) {
            return PRCDA_NO;
        }
        else if (!isCountyAtDxValid(county)) {
            return PRCDA_UNKNOWN;
        }

        if (!CountryData.getInstance().isPrcdaDataInitialized()) {
            CountryData.getInstance().initializePrcdaData(loadPrcdaData());
        }

        StateData stateData = CountryData.getInstance().getPrcdaData(state);
        if (stateData == null) {
            return PRCDA_NO;
        }

        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null) {
            return PRCDA_NO;
        }

        if (PrcdaYear.YR2017.equals(prcdaYear)) {
            return countyData.getPrcda2017();
        }
        else {
            return countyData.getPrcda();
        }
    }

    private Map<String, Map<String, CountyData>> loadPrcdaData() {
        Map<String, Map<String, CountyData>> result = new HashMap<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("prcdauiho/prcda.csv")) {
            if (is == null) {
                throw new IllegalStateException("Unable to find PRCDA data!");
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String prcda2017 = row[2];
                    String prcda2020 = row[3];
                    CountyData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new CountyData());
                    dto.setPrcda2017(StringUtils.leftPad(prcda2017, 1, '0'));
                    dto.setPrcda(StringUtils.leftPad(prcda2020, 1, '0'));
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}