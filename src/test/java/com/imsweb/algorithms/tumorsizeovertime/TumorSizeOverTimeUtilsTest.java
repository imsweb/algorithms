/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tumorsizeovertime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

public class TumorSizeOverTimeUtilsTest {

    @Test
    public void testComputeTumorSizeOverTime() {
        Assert.assertNull(TumorSizeOverTimeUtils.computeTumorSizeOverTime(null));
        TumorSizeOverTimeInputDto inputDto = new TumorSizeOverTimeInputDto();
        Assert.assertNull(TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setDxYear("1985");
        inputDto.setEodTumorSize("999");
        Assert.assertNull(TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        //Tumor not available
        inputDto.setHist("8720");
        Assert.assertEquals(TumorSizeOverTimeUtils.TUMOR_SIZE_NOT_AVAILABLE, TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setHist("8000");
        Assert.assertNull(TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C424");
        Assert.assertEquals(TumorSizeOverTimeUtils.TUMOR_SIZE_NOT_AVAILABLE, TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        //401-989
        inputDto.setDxYear("2000");
        inputDto.setSite("C000");
        inputDto.setEodTumorSize("500");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setDxYear("2005");
        inputDto.setCsTumorSize("401");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setDxYear("2016");
        inputDto.setTumorSizeSummary("989");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));

        //1988-2003
        inputDto.setDxYear("2000");
        inputDto.setSite("C000");
        inputDto.setHist("8000");
        inputDto.setBehavior("3");
        inputDto.setEodTumorSize("990");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setEodTumorSize("001");
        Assert.assertEquals("990", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C500");
        inputDto.setEodTumorSize("997");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setEodTumorSize("002");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));

        //2004-2015
        inputDto.setDxYear("2010");
        inputDto.setSite("C000");
        inputDto.setHist("8000");
        inputDto.setBehavior("3");
        inputDto.setCsTumorSize("991");
        Assert.assertEquals("005", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setCsTumorSize("992");
        Assert.assertEquals("015", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setCsTumorSize("993");
        Assert.assertEquals("025", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setCsTumorSize("994");
        Assert.assertEquals("035", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setCsTumorSize("995");
        Assert.assertEquals("045", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setCsTumorSize("996");
        Assert.assertEquals("055", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C340");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C409");
        Assert.assertEquals("065", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C000");
        inputDto.setCsTumorSize("997");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C649");
        Assert.assertEquals("075", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C150");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setHist("8935");
        Assert.assertEquals("105", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C400");
        Assert.assertEquals("085", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C340");
        Assert.assertEquals("998", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        //2016+
        inputDto.setDxYear("2016");
        inputDto.setSite("C000");
        inputDto.setHist("8000");
        inputDto.setBehavior("3");
        inputDto.setTumorSizeSummary("991");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setTumorSizeSummary("060");
        Assert.assertEquals("060", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setTumorSizeSummary("080");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setTumorSizeSummary("998");
        Assert.assertEquals("999", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
        inputDto.setSite("C169");
        Assert.assertEquals("998", TumorSizeOverTimeUtils.computeTumorSizeOverTime(inputDto));
    }

    @Test
    public void testValid998() {
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8070, 151, "3"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8070, 169, "3"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8070, 169, "2"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(8720, 169, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(9140, 169, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(9590, 169, "3"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8000, 340, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(8760, 340, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(9140, 341, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(9990, 342, "3"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8000, 500, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(8760, 500, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(9140, 509, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(9990, 509, "3"));

        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(8070, 180, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(8220, 180, "2"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8220, 180, "3"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8221, 180, "3"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8221, 199, "3"));
        Assert.assertTrue(TumorSizeOverTimeUtils.valid998(8221, 209, "3"));
        Assert.assertFalse(TumorSizeOverTimeUtils.valid998(8223, 209, "3"));
    }

    @Test
    public void testTumorSizeNotAvailable() {
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(8223, 420));
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(9000, 422));
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(8000, 690));
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(8000, 769));
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(8000, 809));
        Assert.assertFalse(TumorSizeOverTimeUtils.tumorSizeNotAvailable(8000, 209));
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(8740, 209));
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(9140, 209));
        Assert.assertTrue(TumorSizeOverTimeUtils.tumorSizeNotAvailable(9700, 209));
    }

    @Test
    public void testIsValidTumorSize() {
        //C000 External Upper Lip, 000-060, 990, 999
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C000", 0));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C000", 20));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C000", 60));
        Assert.assertFalse(TumorSizeOverTimeUtils.isValidTumorSize("C000", 70));
        Assert.assertFalse(TumorSizeOverTimeUtils.isValidTumorSize("C000", 500));
        Assert.assertFalse(TumorSizeOverTimeUtils.isValidTumorSize("C000", 991));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C000", 990));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C000", 999));
        //C420 Blood, Tumor size always 999
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C420", 999));
        Assert.assertFalse(TumorSizeOverTimeUtils.isValidTumorSize("C420", 990));
        Assert.assertFalse(TumorSizeOverTimeUtils.isValidTumorSize("C420", 10));
        Assert.assertFalse(TumorSizeOverTimeUtils.isValidTumorSize("C420", 0));
        //C759 Endocrine NOS, Not enough cases
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C759", 0));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C759", 50));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C759", 200));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C759", 990));
        Assert.assertTrue(TumorSizeOverTimeUtils.isValidTumorSize("C759", 999));

    }

    @Test
    public void testCsvFile() throws IOException {
        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder().ofNamedCsvRecord(new LineNumberReader(new InputStreamReader(
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("tumorsizeovertime/tumorsize.test.data.csv")), StandardCharsets.US_ASCII)))) {
            csvReader.stream().forEach(line -> {
                TumorSizeOverTimeInputDto input = new TumorSizeOverTimeInputDto();
                input.setDxYear(line.getField(0));
                input.setSite(line.getField(1));
                input.setHist(line.getField(2));
                input.setBehavior(line.getField(3));
                input.setEodTumorSize(line.getField(4));
                input.setCsTumorSize(line.getField(5));
                input.setTumorSizeSummary(line.getField(6));

                String expectedResult = line.getField(7);
                if ("XX2".equals(expectedResult))
                    expectedResult = null;
                String result = TumorSizeOverTimeUtils.computeTumorSizeOverTime(input);

                if (!Objects.equals(expectedResult, result))
                    Assert.fail("Line " + line.getStartingLineNumber() + " - Unexpected result in CSV data file for row " + Arrays.asList(line.getFields().toArray(new String[0])) + " vs " + result);
            });
        }
    }
}
