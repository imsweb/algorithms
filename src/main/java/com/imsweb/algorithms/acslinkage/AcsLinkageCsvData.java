package com.imsweb.algorithms.acslinkage;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.opencsv.CSVReaderBuilder;

/**
 * The purpose of this class is to get the ACS Data for the provided state of dx, county of dx, and census tract
 * from the csv file lookup.  This implementation is a memory consumer. If there is a database, it is better to use another implementation.
 * Created on Oct 12, 2017 by howew
 * @author howew
 */
public class AcsLinkageCsvData implements AcsLinkageDataProvider {

    // TODO this algorithm needs to use the shared country/state/county/census data structure and not repeat all the keys in its own data structure...

    // keys are combination of state/county/census; values are maps where keys are the year range categories and the values are the resulting indicator
    private static Map<String, Map<String, Map<String, AcsLinkageOutputDto>>> _ACS_DATA_LOOKUP = new ConcurrentHashMap<>();

    @Override
    public AcsLinkageOutputDto getAcsData(String state, String county, String tract) {

        // make sure we have enough information to lookup the value
        if (state == null || county == null || tract == null)
            return new AcsLinkageOutputDto();

        // lazily initialize the data (it's not 100% tread-safe, but the worst that can happen is that we read the same data twice)
        if (_ACS_DATA_LOOKUP.isEmpty()) {
            // note that the year range in the filename is not always the same as the year range represented by the category...
            readCsvData("acslinkage/yost-poverty-0610-1014-1317.csv", _ACS_DATA_LOOKUP);
        }

        // lookup and return the resulting value
        return _ACS_DATA_LOOKUP.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new AcsLinkageOutputDto());
    }

    // helper to handle a single CSV data file
    private static void readCsvData(String datafile, Map<String, Map<String, Map<String, AcsLinkageOutputDto>>> data) {
        try (Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("acslinkage/" + datafile), StandardCharsets.US_ASCII)) {
            for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll()) {
                String state = row[0], county = row[1], tract = row[2];
                AcsLinkageOutputDto dto = data.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new AcsLinkageOutputDto());
                dto.setYostQuintile0610US(row[3]);
                dto.setYostQuintile0610State(row[4]);
                dto.setAcsPctPov0610AllRaces(row[5]);
                dto.setAcsPctPov0610White(row[6]);
                dto.setAcsPctPov0610Black(row[7]);
                dto.setAcsPctPov0610AIAN(row[8]);
                dto.setAcsPctPov0610AsianNHOPI(row[9]);
                dto.setAcsPctPov0610OtherMulti(row[10]);
                dto.setAcsPctPov0610WhiteNonHisp(row[11]);
                dto.setAcsPctPov0610Hispanic(row[12]);

                dto.setYostQuintile1014US(row[13]);
                dto.setYostQuintile1014State(row[14]);
                dto.setAcsPctPov1014AllRaces(row[15]);
                dto.setAcsPctPov1014White(row[16]);
                dto.setAcsPctPov1014Black(row[17]);
                dto.setAcsPctPov1014AIAN(row[18]);
                dto.setAcsPctPov1014AsianNHOPI(row[19]);
                dto.setAcsPctPov1014OtherMulti(row[20]);
                dto.setAcsPctPov1014WhiteNonHisp(row[21]);
                dto.setAcsPctPov1014Hispanic(row[22]);

                dto.setYostQuintile1317US(row[23]);
                dto.setYostQuintile1317State(row[24]);
                dto.setAcsPctPov1317AllRaces(row[25]);
                dto.setAcsPctPov1317White(row[26]);
                dto.setAcsPctPov1317Black(row[27]);
                dto.setAcsPctPov1317AIAN(row[28]);
                dto.setAcsPctPov1317AsianNHOPI(row[29]);
                dto.setAcsPctPov1317OtherMulti(row[30]);
                dto.setAcsPctPov1317WhiteNonHisp(row[31]);
                dto.setAcsPctPov1317Hispanic(row[32]);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

