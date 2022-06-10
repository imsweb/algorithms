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
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.layout.LayoutUtils;
import com.imsweb.layout.record.fixed.FixedColumnsLayout;

public class TimeDependentTractDataLab {

    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException {

        // I created a layout to be able to easily read the big fixed-column census tract file; that layout needs to agree on the "dictionary" provided on the SEER website:
        //    https://seer.cancer.gov/seerstat/variables/countyattribs/ctattrdict.html

        // The input file was not added to the project (the files have a timestamp, and I didn't see the point of adding all that data to the project).
        // This class creates the minimized output files in their final location in this project, they retain the timestamp of the full data file...

        FixedColumnsLayout layout;
        try (InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("tract/time-dependent-tract-data-layout-2022.xml")) {
            layout = new FixedColumnsLayout(LayoutUtils.readFixedColumnsLayout(fis));
        }

        Path inputFile = Paths.get("<change me>\\tract.level.ses.2008_17.txt.gz");
        Path outputFile = Paths.get(System.getProperty("user.dir") + "\\src\\main\\resources\\tract\\tract.level.ses.2008_17.minimized.txt.gz");
        Path outputFileYear = Paths.get(System.getProperty("user.dir") + "\\src\\main\\resources\\tract\\tract.level.ses.2008_17.minimized.year.based.txt.gz");
        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(inputFile)), StandardCharsets.US_ASCII));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outputFile)), StandardCharsets.US_ASCII));
             BufferedWriter writerYear = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outputFileYear)), StandardCharsets.US_ASCII))) {

            StringBuilder buf = new StringBuilder();
            StringBuilder bufYear = new StringBuilder();

            Map<String, String> line = layout.readNextRecord(reader);
            while (line != null) {

                int lineNum = reader.getLineNumber();

                String state = line.get("stateFipsCode");
                String county = line.get("countyFipsCode");
                String tract = line.get("tract");
                String year = line.get("year");

                if (state == null)
                    throw new RuntimeException("Line " + lineNum + ": missing state");
                if (!CountryData.getStates().containsKey(state))
                    throw new RuntimeException("Line " + lineNum + ": invalid state: " + state);
                if (county == null)
                    throw new RuntimeException("Line " + lineNum + ": missing county");
                if (tract == null)
                    throw new RuntimeException("Line " + lineNum + ": missing tract");
                if (year == null)
                    throw new RuntimeException("Line " + lineNum + ": missing year");
                if (year.compareTo("2008") < 0 || year.compareTo("2017") > 0)
                    throw new RuntimeException("Line " + lineNum + ": invalid year: " + year);

                // year-based data
                addValue(lineNum, state, bufYear, CountryData.STATE_FIPS_START, CountryData.STATE_FIPS_END);
                addValue(lineNum, county, bufYear, CountryData.COUNTY_FIPS_START, CountryData.COUNTY_FIPS_END);
                addValue(lineNum, tract, bufYear, CountryData.TRACT_START, CountryData.TRACT_END);
                addValue(lineNum, year, bufYear, CountryData.YEAR_START, CountryData.YEAR_END);
                addValue(lineNum, line.get("yostUsBasedQuintile"), bufYear, CountryData.YOST_US_BASED_QUINTILE_START, CountryData.YOST_US_BASED_QUINTILE_END);
                addValue(lineNum, line.get("yostStateBasedQuintile"), bufYear, CountryData.YOST_STATE_BASED_QUINTILE_START, CountryData.YOST_STATE_BASED_QUINTILE_END);
                addValue(lineNum, line.get("percentBelowPovertyAllRaces"), bufYear, CountryData.PERCENT_BEL_POV_ALL_RACES_START, CountryData.PERCENT_BEL_POV_ALL_RACES_END);
                addValue(lineNum, line.get("percentBelowPovertyWhite"), bufYear, CountryData.PERCENT_BEL_POV_WHITE_START, CountryData.PERCENT_BEL_POV_WHITE_END);
                addValue(lineNum, line.get("percentBelowPovertyBlack"), bufYear, CountryData.PERCENT_BEL_POV_BLACK_START, CountryData.PERCENT_BEL_POV_BLACK_END);
                addValue(lineNum, line.get("percentBelowPovertyAmIndian"), bufYear, CountryData.PERCENT_BEL_POV_AM_INDIAN_START, CountryData.PERCENT_BEL_POV_AM_INDIAN_END);
                addValue(lineNum, line.get("percentBelowPovertyAsian"), bufYear, CountryData.PERCENT_BEL_POV_ASIAN_START, CountryData.PERCENT_BEL_POV_ASIAN_END);
                addValue(lineNum, line.get("percentBelowPovertyWhiteNotHisp"), bufYear, CountryData.PERCENT_BEL_POV_WHILE_NOT_HISP_START, CountryData.PERCENT_BEL_POV_WHILE_NOT_HISP_END);
                addValue(lineNum, line.get("percentBelowPovertyHisp"), bufYear, CountryData.PERCENT_BEL_POV_HISP_START, CountryData.PERCENT_BEL_POV_HISP_END);
                if (bufYear.length() != CountryData.PERCENT_BEL_POV_HISP_END)
                    throw new RuntimeException("Line " + lineNum + ": unexpected line length");
                writerYear.write(bufYear.toString());
                bufYear.setLength(0);
                writerYear.newLine();

                // non-year-based data
                addValue(lineNum, state, buf, CountryData.STATE_FIPS_START, CountryData.STATE_FIPS_END);
                addValue(lineNum, county, buf, CountryData.COUNTY_FIPS_START, CountryData.COUNTY_FIPS_END);
                addValue(lineNum, tract, buf, CountryData.TRACT_START, CountryData.TRACT_END);
                addValue(lineNum, line.get("race2010C"), buf, CountryData.RUCA_2010_START, CountryData.RUCA_2010_END); // categorization C (2 categories)
                addValue(lineNum, line.get("uric2010A"), buf, CountryData.URIC_2010_START, CountryData.URIC_2010_END); // categorization A (4 categories)
                addValue(lineNum, line.get("zoneId"), buf, CountryData.ZONE_ID_START, CountryData.ZONE_ID_END);
                addValue(lineNum, line.get("naaccrPovertyIndicator"), buf, CountryData.NAACCR_POV_INDICATOR_START, CountryData.NAACCR_POV_INDICATOR_END);
                addValue(lineNum, line.get("cdcSubcounty5k"), buf, CountryData.CDC_SUBCOUNTY_5K_START, CountryData.CDC_SUBCOUNTY_5K_END);
                addValue(lineNum, line.get("cdcSubcounty20k"), buf, CountryData.CDC_SUBCOUNTY_20K_START, CountryData.CDC_SUBCOUNTY_20K_END);
                if (buf.length() != CountryData.CDC_SUBCOUNTY_20K_END)
                    throw new RuntimeException("Line " + lineNum + ": unexpected line length");
                writer.write(buf.toString());
                buf.setLength(0);
                writer.newLine();

                line = layout.readNextRecord(reader);
            }
        }
    }

    private static void addValue(int lineNum, String value, StringBuilder buf, int start, int end) {
        if (value == null)
            value = "";
        if (value.length() > end - start + 1)
            throw new RuntimeException("Line " + lineNum + ": value too long: " + value);
        buf.append(StringUtils.rightPad(value, end - start + 1, " "));
    }
}
