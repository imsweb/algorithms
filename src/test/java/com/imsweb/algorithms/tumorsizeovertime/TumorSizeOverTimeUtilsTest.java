/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tumorsizeovertime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.junit.Test;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class TumorSizeOverTimeUtilsTest {

    @Test
    public void testValid998() {
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8070, 151, "3"));
    }

    @Test
    public void testCsvFile() throws IOException, CsvException {
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("tumorsizeovertime/tumorsize.test.data.csv"), StandardCharsets.US_ASCII)).withSkipLines(1)
                .build()) {
            for (String[] row : reader.readAll()) {
                TumorSizeOverTimeInputDto input = new TumorSizeOverTimeInputDto();
                int dxYear = NumberUtils.toInt(row[0], -1);
                input.setDxYear(dxYear);
                input.setSite(row[1]);
                input.setHist(row[2]);
                input.setBehavior(row[3]);
                if (dxYear >= 1988 && dxYear <= 2003)
                    input.setTumorSize(row[4]);
                else if (dxYear >= 2004 && dxYear <= 2015)
                    input.setTumorSize(row[5]);
                else if (dxYear > 2015)
                    input.setTumorSize(row[6]);

                String expectedResult = row[7];
                if ("XX2".equals(expectedResult))
                    expectedResult = null;
                String result = TumorSizeOverTimeUtils.computeTumorSizeOverTime(input);

                if (!Objects.equals(expectedResult, result))
                    Assert.fail("Unexpected result in CSV data file for row " + Arrays.asList(row) + " vs " + result);
            }
        }
    }
}
