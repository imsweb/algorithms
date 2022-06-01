package com.imsweb.algorithms.yostacspoverty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;

/**
 * The purpose of this class is to get the ACS Data for the provided state of dx, county of dx, and census tract
 * from the csv file lookup.  This implementation is a memory consumer. If there is a database, it is better to use another implementation.
 * Created on Oct 12, 2017 by howew
 * @author howew
 */
public class YostAcsPovertyDataProvider {

    public YostAcsPovertyOutputDto getYostAcsPovertyData(String state, String county, String censusTract) {
        YostAcsPovertyOutputDto odto = new YostAcsPovertyOutputDto();

        // make sure we have enough information to lookup the value
        if (state == null || county == null || censusTract == null)
            return odto;

        if (!CountryData.getInstance().isYostAcsPovertyDataInitialized())
            CountryData.getInstance().initializeYostAcsPovertyData(loadYostAcsPovertyData());

        StateData stateData = CountryData.getInstance().getYostAcsPovertyData(state);
        if (stateData == null)
            return odto;
        CountyData countyData = stateData.getCountyData(county);
        if (countyData == null)
            return odto;
        CensusData censusData = countyData.getCensusData(censusTract);
        if (censusData == null)
            return odto;

        odto.setYostQuintile0610US(censusData.getYostQuintile0610US());
        odto.setYostQuintile0610State(censusData.getYostQuintile0610State());
        odto.setAcsPctPov0610AllRaces(censusData.getAcsPctPov0610AllRaces());
        odto.setAcsPctPov0610White(censusData.getAcsPctPov0610White());
        odto.setAcsPctPov0610Black(censusData.getAcsPctPov0610Black());
        odto.setAcsPctPov0610AsianNHOPI(censusData.getAcsPctPov0610AsianNHOPI());
        odto.setAcsPctPov0610AIAN(censusData.getAcsPctPov0610AIAN());
        odto.setAcsPctPov0610OtherMulti(censusData.getAcsPctPov0610OtherMulti());
        odto.setAcsPctPov0610WhiteNonHisp(censusData.getAcsPctPov0610WhiteNonHisp());
        odto.setAcsPctPov0610Hispanic(censusData.getAcsPctPov0610Hispanic());

        odto.setYostQuintile1014US(censusData.getYostQuintile1014US());
        odto.setYostQuintile1014State(censusData.getYostQuintile1014State());
        odto.setAcsPctPov1014AllRaces(censusData.getAcsPctPov1014AllRaces());
        odto.setAcsPctPov1014White(censusData.getAcsPctPov1014White());
        odto.setAcsPctPov1014Black(censusData.getAcsPctPov1014Black());
        odto.setAcsPctPov1014AsianNHOPI(censusData.getAcsPctPov1014AsianNHOPI());
        odto.setAcsPctPov1014AIAN(censusData.getAcsPctPov1014AIAN());
        odto.setAcsPctPov1014OtherMulti(censusData.getAcsPctPov1014OtherMulti());
        odto.setAcsPctPov1014WhiteNonHisp(censusData.getAcsPctPov1014WhiteNonHisp());
        odto.setAcsPctPov1014Hispanic(censusData.getAcsPctPov1014Hispanic());

        odto.setYostQuintile1418US(censusData.getYostQuintile1418US());
        odto.setYostQuintile1418State(censusData.getYostQuintile1418State());
        odto.setAcsPctPov1418AllRaces(censusData.getAcsPctPov1418AllRaces());
        odto.setAcsPctPov1418White(censusData.getAcsPctPov1418White());
        odto.setAcsPctPov1418Black(censusData.getAcsPctPov1418Black());
        odto.setAcsPctPov1418AsianNHOPI(censusData.getAcsPctPov1418AsianNHOPI());
        odto.setAcsPctPov1418AIAN(censusData.getAcsPctPov1418AIAN());
        odto.setAcsPctPov1418OtherMulti(censusData.getAcsPctPov1418OtherMulti());
        odto.setAcsPctPov1418WhiteNonHisp(censusData.getAcsPctPov1418WhiteNonHisp());
        odto.setAcsPctPov1418Hispanic(censusData.getAcsPctPov1418Hispanic());

        return odto;
    }

    private Map<String, Map<String, Map<String, CensusData>>> loadYostAcsPovertyData() {
        Map<String, Map<String, Map<String, CensusData>>> result = new HashMap<>();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("yostacspoverty/yost-acs-poverty-0610-1014-1418.csv.gz")) {
            if (is == null)
                throw new IllegalStateException("Missing data!");
            try (Reader reader = new InputStreamReader(new GZIPInputStream(is), StandardCharsets.US_ASCII);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] row : csvReader.readAll()) {
                    String state = row[0];
                    String county = row[1];
                    String tract = row[2];
                    String yostQuintile0610US = row[3];
                    String yostQuintile0610State = row[4];
                    String pov0610AllRaces = row[5];
                    String pov0610White = row[6];
                    String pov0610Black = row[7];
                    String pov0610AIAN = row[8];
                    String pov0610AsianNHOPI = row[9];
                    String pov0610OtherMulti = row[10];
                    String pov0610WhiteNonHisp = row[11];
                    String pov0610Hispanic = row[12];
                    String yostQuintile1014US = row[13];
                    String yostQuintile1014State = row[14];
                    String pov1014AllRaces = row[15];
                    String pov1014White = row[16];
                    String pov1014Black = row[17];
                    String pov1014AIAN = row[18];
                    String pov1014AsianNHOPI = row[19];
                    String pov1014OtherMulti = row[20];
                    String pov1014WhiteNonHisp = row[21];
                    String pov1014Hispanic = row[22];
                    String yostQuintile1418US = row[23];
                    String yostQuintile1418State = row[24];
                    String pov1418AllRaces = row[25];
                    String pov1418White = row[26];
                    String pov1418Black = row[27];
                    String pov1418AIAN = row[28];
                    String pov1418AsianNHOPI = row[29];
                    String pov1418OtherMulti = row[30];
                    String pov1418WhiteNonHisp = row[31];
                    String pov1418Hispanic = row[32];

                    CensusData dto = result.computeIfAbsent(state, k -> new HashMap<>()).computeIfAbsent(county, k -> new HashMap<>()).computeIfAbsent(tract, k -> new CensusData());
                    dto.setYostQuintile0610US(yostQuintile0610US);
                    dto.setYostQuintile0610State(yostQuintile0610State);
                    dto.setAcsPctPov0610AllRaces(pov0610AllRaces);
                    dto.setAcsPctPov0610White(pov0610White);
                    dto.setAcsPctPov0610Black(pov0610Black);
                    dto.setAcsPctPov0610AIAN(pov0610AIAN);
                    dto.setAcsPctPov0610AsianNHOPI(pov0610AsianNHOPI);
                    dto.setAcsPctPov0610OtherMulti(pov0610OtherMulti);
                    dto.setAcsPctPov0610WhiteNonHisp(pov0610WhiteNonHisp);
                    dto.setAcsPctPov0610Hispanic(pov0610Hispanic);

                    dto.setYostQuintile1014US(yostQuintile1014US);
                    dto.setYostQuintile1014State(yostQuintile1014State);
                    dto.setAcsPctPov1014AllRaces(pov1014AllRaces);
                    dto.setAcsPctPov1014White(pov1014White);
                    dto.setAcsPctPov1014Black(pov1014Black);
                    dto.setAcsPctPov1014AIAN(pov1014AIAN);
                    dto.setAcsPctPov1014AsianNHOPI(pov1014AsianNHOPI);
                    dto.setAcsPctPov1014OtherMulti(pov1014OtherMulti);
                    dto.setAcsPctPov1014WhiteNonHisp(pov1014WhiteNonHisp);
                    dto.setAcsPctPov1014Hispanic(pov1014Hispanic);

                    dto.setYostQuintile1418US(yostQuintile1418US);
                    dto.setYostQuintile1418State(yostQuintile1418State);
                    dto.setAcsPctPov1418AllRaces(pov1418AllRaces);
                    dto.setAcsPctPov1418White(pov1418White);
                    dto.setAcsPctPov1418Black(pov1418Black);
                    dto.setAcsPctPov1418AIAN(pov1418AIAN);
                    dto.setAcsPctPov1418AsianNHOPI(pov1418AsianNHOPI);
                    dto.setAcsPctPov1418OtherMulti(pov1418OtherMulti);
                    dto.setAcsPctPov1418WhiteNonHisp(pov1418WhiteNonHisp);
                    dto.setAcsPctPov1418Hispanic(pov1418Hispanic);
                }
            }
        }
        catch (CsvException | IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}

