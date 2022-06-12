/*
 * Copyright (C) 2022 Information Management Services, Inc.
 */
package lab;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.layout.LayoutUtils;
import com.imsweb.layout.record.fixed.FixedColumnsLayout;

public class TractDataLab {

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

    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException {

        // I created a layout to be able to easily read the big fixed-column census tract file; that layout needs to agree on the "dictionary" provided on the SEER website:
        //    https://seer.cancer.gov/seerstat/variables/countyattribs/ctattrdict.html
        FixedColumnsLayout layout;
        try (InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("tract/seer-tract-data-layout-2022.xml")) {
            layout = new FixedColumnsLayout(LayoutUtils.readFixedColumnsLayout(fis));
        }

        // load the data from the big SEER tract data file (it was not added to the project, too big)
        Path inputFile = Paths.get("C:\\dev\\tract.level.ses.2008_17.txt.gz");
        Map<DataKey, Map<String, String>> tractValues = new TreeMap<>();
        Map<DataKey, Map<Integer, String>> tractYearBasedValues = new HashMap<>();
        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(inputFile)), StandardCharsets.US_ASCII))) {

            Map<String, String> line = layout.readNextRecord(reader);
            while (line != null) {

                int lineNum = reader.getLineNumber();

                String state = line.get("stateFipsCode");
                String county = line.get("countyFipsCode");
                String tract = line.get("tract");
                String year = line.get("year");

                if (state == null)
                    throw new RuntimeException("Line " + lineNum + ": missing state");
                if (!_STATES.containsKey(state))
                    throw new RuntimeException("Line " + lineNum + ": invalid state: " + state);
                if (county == null)
                    throw new RuntimeException("Line " + lineNum + ": missing county");
                if (tract == null)
                    throw new RuntimeException("Line " + lineNum + ": missing tract");
                if (year == null)
                    throw new RuntimeException("Line " + lineNum + ": missing year");
                if (Integer.valueOf(year).compareTo(CountryData.TRACT_YEAR_MIN_VAL) < 0 || Integer.valueOf(year).compareTo(CountryData.TRACT_YEAR_MAX_VAL) > 0)
                    throw new RuntimeException("Line " + lineNum + ": invalid/unexpected year: " + year);

                DataKey key = new DataKey(_STATES.get(state), county, tract);

                StringBuilder buf = new StringBuilder();
                for (String field : CountryData.getTractYearBasedFields().keySet())
                    buf.append(cleanYearBasedTractValue(lineNum, line, field));
                tractYearBasedValues.computeIfAbsent(key, k -> new HashMap<>()).put(Integer.valueOf(year), buf.toString());

                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("ruca2000", cleanTractValue(lineNum, line, "", "ruca2000"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("ruca2010", cleanTractValue(lineNum, line, "ruca2010C", "ruca2010"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("uric2000", cleanTractValue(lineNum, line, "", "uric2000"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("uric2010", cleanTractValue(lineNum, line, "uric2010A", "uric2010"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("continuum1993", cleanTractValue(lineNum, line, "", "continuum1993"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("continuum2003", cleanTractValue(lineNum, line, "", "continuum2003"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("continuum2013", cleanTractValue(lineNum, line, "", "continuum2013"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("cancerReportingZone", cleanTractValue(lineNum, line, "zoneId", "cancerReportingZone"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("naaccrPovertyIndicator", cleanTractValue(lineNum, line, "naaccrPovertyIndicator", "naaccrPovertyIndicator"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty5k", cleanTractValue(lineNum, line, "cdcSubcounty5k", "npcrEphtSubcounty5k"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty20k", cleanTractValue(lineNum, line, "cdcSubcounty20k", "npcrEphtSubcounty20k"));

                line = layout.readNextRecord(reader);
            }
        }

        // URCA 2000
        Map<DataKey, String> ruca2000Values = new HashMap<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2000.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

                List<String> urbanCommutingAreas = Arrays.asList("1.0", "1.1", "2.0", "2.1", "3.0", "4.1", "5.1", "7.1", "8.1", "10.1");

                for (String[] row : csvReader.readAll()) {
                    String primary = row[3];
                    String secondary = row[4];

                    String val;
                    if (primary.equals("99"))
                        val = "9";
                    else if (urbanCommutingAreas.contains(secondary))
                        val = "1";
                    else
                        val = "2";

                    ruca2000Values.put(new DataKey(row[0], row[1], row[2]), val);
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        // URIC 2000
        Map<DataKey, String> uric2000Values = new HashMap<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/urban-rural-indicator-code-2000.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");
            try (Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    if (row[4].length() != 1)
                        throw new IllegalStateException("Found unexpected format for URIC value: " + row[4]);
                    uric2000Values.put(new DataKey(row[0], row[1], row[2]), row[4]);
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }

        // since we merge information from multiple sources, it's important to process the super set of all the keys!
        Set<DataKey> allKeys = new TreeSet<>(tractValues.keySet());
        allKeys.addAll(ruca2000Values.keySet());
        allKeys.addAll(uric2000Values.keySet());

        Path outputFile = Paths.get(System.getProperty("user.dir") + "\\src\\main\\resources\\tract\\tract-data.txt.gz");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outputFile)), StandardCharsets.US_ASCII))) {
            for (DataKey key : allKeys) {
                StringBuilder buf = new StringBuilder();

                for (String field : CountryData.getTractFields().keySet()) {
                    if ("stateAbbreviation".equals(field))
                        buf.append(key.getState());
                    else if ("countyFips".equals(field))
                        buf.append(key.getCounty());
                    else if ("censusTract".equals(field))
                        buf.append(key.getTract());
                    else if ("ruca2000".equals(field))
                        buf.append(ruca2000Values.getOrDefault(key, " "));
                    else if ("uric2000".equals(field))
                        buf.append(uric2000Values.getOrDefault(key, " "));
                    else if ("yearData".equals(field)) {
                        Map<Integer, String> yearData = tractYearBasedValues.get(key);
                        if (yearData != null) {
                            for (Entry<Integer, String> yearEntry : yearData.entrySet())
                                buf.append(yearEntry.getValue());
                        }
                        else
                            buf.append(StringUtils.rightPad("", CountryData.getTractFields().get(field), " "));
                    }
                    else {
                        String val = tractValues.getOrDefault(key, new HashMap<>()).getOrDefault(field, "");
                        buf.append(StringUtils.rightPad(val, CountryData.getTractFields().get(field), " "));
                    }
                }

                String line = buf.toString();
                if (line.length() != CountryData.getTractFields().values().stream().mapToInt(Integer::intValue).sum())
                    throw new IllegalStateException("Expected length of " + CountryData.getTractFields().values().stream().mapToInt(Integer::intValue).sum() + " but got " + line.length());
                if (line.contains("null"))
                    throw new IllegalStateException("Line contains null: " + line);

                writer.write(buf.toString());
                writer.newLine();
            }
        }
    }

    private static String cleanTractValue(int lineNum, Map<String, String> line, String sourceField, String targetField) {
        String value = line.get(sourceField);
        if (value == null)
            value = "";
        int length = CountryData.getTractFields().get(targetField);
        if (value.length() > length)
            throw new RuntimeException("Line " + lineNum + ": value too long: " + value);
        return StringUtils.rightPad(value, length, " ");
    }

    private static String cleanYearBasedTractValue(int lineNum, Map<String, String> line, String field) {
        String value = line.get(field);
        if (value == null)
            value = "";
        int length = CountryData.getTractYearBasedFields().get(field);
        if (value.length() > length)
            throw new RuntimeException("Line " + lineNum + ": value too long: " + value);
        return StringUtils.rightPad(value, length, " ");
    }

    private static class DataKey implements Comparable<DataKey> {

        private final String _state;
        private final String _county;
        private final String _tract;

        public DataKey(String state, String county, String tract) {
            _state = state;
            _county = county;
            _tract = tract;
        }

        public String getState() {
            return _state;
        }

        public String getCounty() {
            return _county;
        }

        public String getTract() {
            return _tract;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass())
                return false;
            DataKey dataKey = (DataKey)o;
            return _state.equals(dataKey._state) && _county.equals(dataKey._county) && _tract.equals(dataKey._tract);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_state, _county, _tract);
        }

        @Override
        public int compareTo(DataKey o) {
            int result = _state.compareTo(o._state);
            if (result == 0) {
                result = _county.compareTo(o._county);
                if (result == 0)
                    result = _tract.compareTo(o._tract);
            }
            return result;
        }
    }
}
