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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

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
        _STATES.put("72", "PR");
        _STATES.put("74", "UM");
        _STATES.put("78", "VI");
        _STATES.put("99", "YY");
    }

    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException {

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //   Make sure to read this if you are not familiar with this class or if you haven't used it in a while...
        //   This class reads multiple census-related data files and merges them into a single "census-data.gz.txt" file.
        //   All the data files exists in the project in the test resource folder, except the SEER one (that one was too big).
        //   This class reads all the data from all sources and puts it into data structures keyed by state/county/tract;
        //   it then has a final loop that uses all the data structures and writes the final file.
        //   If you add a new source, don't forget to add its keys to the set of "all keys" since not all the files have
        //   the same keys (it's important to iterate over the super-set of all keys).
        //
        //   The final file is a fixed-column file, but nothing defines the start/end columns. Instead, the fields are
        //   defines in the CountryData class, in the order they appear, with their specific length. And so to add a new
        //   source and/or field, start by adding the field to the list of fields in CountryData.
        //
        //   A handful of fields are "year based"; instead of having a second file that repeats the state/county/tract, those
        //   were added to the regular data file as a single "yearData" field which itself is a fixed-column value.
        //   The CountryData class defines a second list of "year-based" fields, as well as a min/max value for the years.
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // I created a layout to be able to easily read the big fixed-column census tract file; that layout needs to agree on the "dictionary" provided on the SEER website:
        //    https://seer.cancer.gov/seerstat/variables/countyattribs/ctattrdict.html
        FixedColumnsLayout layout2022;
        try (InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("tract/time-dependent-tract-data-layout-2022.xml")) {
            layout2022 = new FixedColumnsLayout(LayoutUtils.readFixedColumnsLayout(fis));
        }
        FixedColumnsLayout layout2025;
        try (InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("tract/time-dependent-tract-data-layout-2025.xml")) {
            layout2025 = new FixedColumnsLayout(LayoutUtils.readFixedColumnsLayout(fis));
        }

        // load the data from the big SEER tract data file (it was not added to the project, too big)
        //    https://seer.cancer.gov/seerstat/variables/countyattribs/census-tract-attribs.html
        // 8/15/22 - new version of the SEER data was provided, but it's not posted online yet; a Puerto-Rico version of the data file was also provided (it won't be posted)
        // 8/29/25 - an extra version of the SEER data was provided for years 2018-2021, using census2020 boundaries; Puerto-Rico file not available yet.
        Map<DataKey, Map<String, String>> tractValues = new TreeMap<>();
        Map<DataKey, Map<Integer, String>> tractYearBasedValues = new HashMap<>();
        processMainSeerDataFile_2008_2017(Paths.get("C:\\dev\\temp\\tract.level.ses.2008_17.txt.gz"), layout2022, tractValues, tractYearBasedValues);
        processMainSeerDataFile_2008_2017(Paths.get("C:\\dev\\temp\\tract.level.ses.2008_17.puerto.rico.dt20230818.txt.gz"), layout2022, tractValues, tractYearBasedValues);
        processMainSeerDataFile_2018_2021(Paths.get("C:\\dev\\temp\\tract.level.ses.2018_21.vint2023.pops.dat.txd.gz"), layout2025, tractValues, tractYearBasedValues);

        // NAACCR Poverty Indicator 1995-2004
        Map<DataKey, String> naaccrPovertyIndicator9504 = new HashMap<>();

        try (CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("povertyindicator/poverty-indicator-1995-2004.csv"), StandardCharsets.US_ASCII))) {
            reader.stream().forEach(line -> naaccrPovertyIndicator9504.put(new DataKey(line.getField(0), line.getField(1), line.getField(2)), line.getField(3)));
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // NAACCR Poverty Indicator 2005-2007
        Map<DataKey, String> naaccrPovertyIndicator0507 = new HashMap<>();

        try (CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("povertyindicator/poverty-indicator-2005-2007.csv"), StandardCharsets.US_ASCII))) {
            reader.stream().forEach(line -> naaccrPovertyIndicator0507.put(new DataKey(line.getField(0), line.getField(1), line.getField(2)), line.getField(3)));
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // URCA 2000
        Map<DataKey, String> ruca2000Values = new HashMap<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/rural-urban-commuting-area-2000.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");

            try (CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
                List<String> urbanCommutingAreas = Arrays.asList("1.0", "1.1", "2.0", "2.1", "3.0", "4.1", "5.1", "7.1", "8.1", "10.1");
                reader.stream().forEach(line -> {
                    String primary = line.getField(3);
                    String secondary = line.getField(4);

                    String val;
                    if (primary.equals("99"))
                        val = "9";
                    else if (urbanCommutingAreas.contains(secondary))
                        val = "1";
                    else
                        val = "2";

                    ruca2000Values.put(new DataKey(line.getField(0), line.getField(1), line.getField(2)), val);
                });
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // URIC 2000
        Map<DataKey, String> uric2000Values = new HashMap<>();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ruralurban/urban-rural-indicator-code-2000.csv")) {
            if (is == null)
                throw new IllegalStateException("Missing data file!");

            try (CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
                reader.stream().forEach(line -> {
                    if (line.getField(4).length() != 1)
                        throw new IllegalStateException("Found unexpected format for URIC value: " + line.getField(4));
                    uric2000Values.put(new DataKey(line.getField(0), line.getField(1), line.getField(2)), line.getField(4));
                });
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // since we merge information from multiple sources, it's important to process the super set of all the keys!
        Set<DataKey> allKeys = new TreeSet<>(tractValues.keySet());
        allKeys.addAll(ruca2000Values.keySet());
        allKeys.addAll(uric2000Values.keySet());
        allKeys.addAll(naaccrPovertyIndicator9504.keySet());
        allKeys.addAll(naaccrPovertyIndicator0507.keySet());

        Path outputFile = Paths.get(System.getProperty("user.dir") + "\\src\\main\\resources\\tract\\tract-data.txt.gz");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outputFile)), StandardCharsets.US_ASCII))) {
            for (DataKey key : allKeys) {
                StringBuilder buf = new StringBuilder();

                // the fields are returned and processed in a very specific order; the order of the if/else statements doesn't matter
                // also, most fields come from the big shared SEER data file, those don't have their own if/else statements
                for (String field : CountryData.getTractFields().keySet()) {
                    if ("stateAbbreviation".equals(field))
                        buf.append(key.getState());
                    else if ("countyFips".equals(field))
                        buf.append(key.getCounty());
                    else if ("censusTract".equals(field))
                        buf.append(key.getTract());
                    else if ("naaccrPovertyIndicator9504".equals(field))
                        buf.append(naaccrPovertyIndicator9504.getOrDefault(key, " "));
                    else if ("naaccrPovertyIndicator0507".equals(field))
                        buf.append(naaccrPovertyIndicator0507.getOrDefault(key, " "));
                    else if ("ruca2000".equals(field))
                        buf.append(ruca2000Values.getOrDefault(key, " "));
                    else if ("uric2000".equals(field))
                        buf.append(uric2000Values.getOrDefault(key, " "));
                    else if ("yearData".equals(field)) {
                        Map<Integer, String> yearData = tractYearBasedValues.get(key);
                        if (yearData != null) {
                            for (Integer year : IntStream.rangeClosed(CountryData.TRACT_YEAR_MIN_VAL, CountryData.TRACT_YEAR_MAX_VAL).boxed().collect(Collectors.toList()))
                                buf.append(yearData.getOrDefault(year, StringUtils.rightPad("", CountryData.getTractYearBasedFields().values().stream().mapToInt(Integer::intValue).sum(), " ")));
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

                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static String cleanTractValue(int lineNum, Map<String, String> line, String sourceField, String targetField) {
        return cleanTractValue(lineNum, line, sourceField, targetField, null);
    }

    @SuppressWarnings("SameParameterValue")
    private static String cleanTractValue(int lineNum, Map<String, String> line, String sourceField, String targetField, UnaryOperator<String> operator) {
        String value = line.get(sourceField);
        if (operator != null)
            value = operator.apply(value);
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

    private static void processMainSeerDataFile_2008_2017(Path inputFile, FixedColumnsLayout layout, Map<DataKey, Map<String, String>> tractValues, Map<DataKey, Map<Integer, String>> tractYearBasedValues) throws IOException {
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

                // year-based data for 2016-2017 need to use the 2020 census boundaries; this fiel contains the data for those years for the 2010 boundaries, which is not used by
                // any algorithm anymore, so it can be ignored (algorithms use a DX year of 2018, which is the first year for the 2020 boundaries) for those two DX years
                // this was agreed in https://squishlist.com/naaccr/cfd/138/
                if ("2016".equals(year) || "2017".equals(year)) {
                    line = layout.readNextRecord(reader);
                    continue;
                }

                DataKey key = new DataKey(_STATES.get(state), county, tract);

                StringBuilder buf = new StringBuilder();
                for (String field : CountryData.getTractYearBasedFields().keySet())
                    buf.append(cleanYearBasedTractValue(lineNum, line, field));
                tractYearBasedValues.computeIfAbsent(key, k -> new HashMap<>()).put(Integer.valueOf(year), buf.toString());

                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("ruca2010", cleanTractValue(lineNum, line, "ruca2010C", "ruca2010"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("uric2010", cleanTractValue(lineNum, line, "uric2010A", "uric2010"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("cancerReportingZone", cleanTractValue(lineNum, line, "zoneId", "cancerReportingZone"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("cancerReportingZoneTractCert", cleanTractValue(lineNum, line, "zoneTractCertainty", "cancerReportingZoneTractCert"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty5k", cleanTractValue(lineNum, line, "cdcSubcounty5k", "npcrEphtSubcounty5k"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty20k", cleanTractValue(lineNum, line, "cdcSubcounty20k", "npcrEphtSubcounty20k"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty50k", cleanTractValue(lineNum, line, "cdcSubcounty50k", "npcrEphtSubcounty50k"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("sviOverallStateBased2018", cleanTractValue(lineNum, line, "sviOverallState2018", "sviOverallStateBased2018"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("congressionalDistrict118", cleanTractValue(lineNum, line, "congressionalDistrict118", "congressionalDistrict118"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("persistentPoverty", cleanTractValue(lineNum, line, "persistentPoverty", "persistentPoverty"));

                line = layout.readNextRecord(reader);
            }
        }
    }

    private static void processMainSeerDataFile_2018_2021(Path inputFile, FixedColumnsLayout layout, Map<DataKey, Map<String, String>> tractValues, Map<DataKey, Map<Integer, String>> tractYearBasedValues) throws IOException {
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

                // the years data structure is exactly the same in the provided 2018-2021 data (it's just different years)

                StringBuilder buf = new StringBuilder();
                for (String field : CountryData.getTractYearBasedFields().keySet())
                    buf.append(cleanYearBasedTractValue(lineNum, line, field));
                tractYearBasedValues.computeIfAbsent(key, k -> new HashMap<>()).put(Integer.valueOf(year), buf.toString());

                // commented out values are the ones that were "not available" in the provided 2018-2021 data

                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("ruca2020", cleanTractValue(lineNum, line, "ruca2020C", "ruca2020"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("uric2020", cleanTractValue(lineNum, line, "uric2020A", "uric2020"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("cancerReportingZone", cleanTractValue(lineNum, line, "zoneId", "cancerReportingZone"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("cancerReportingZoneTractCert", cleanTractValue(lineNum, line, "zoneTractCertainty", "cancerReportingZoneTractCert"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty5k", cleanTractValue(lineNum, line, "cdcSubcounty5k", "npcrEphtSubcounty5k"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty20k", cleanTractValue(lineNum, line, "cdcSubcounty20k", "npcrEphtSubcounty20k"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("npcrEphtSubcounty50k", cleanTractValue(lineNum, line, "cdcSubcounty50k", "npcrEphtSubcounty50k"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("sviOverallStateBased2022", cleanTractValue(lineNum, line, "sviOverallState2022", "sviOverallStateBased2022"));
                tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("congressionalDistrict119", cleanTractValue(lineNum, line, "congressionalDistrict119", "congressionalDistrict119"));
                //tractValues.computeIfAbsent(key, k -> new HashMap<>()).put("persistentPoverty", cleanTractValue(lineNum, line, "persistentPoverty", "persistentPoverty"));

                line = layout.readNextRecord(reader);
            }
        }
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
