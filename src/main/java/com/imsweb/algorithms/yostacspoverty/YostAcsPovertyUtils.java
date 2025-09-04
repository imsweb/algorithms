package com.imsweb.algorithms.yostacspoverty;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;
import com.imsweb.algorithms.internal.YearData;

/**
 * This class can be used to calculate ACS Linkage variables.
 * Created on Oct 13, 2017 by howew
 * @author howew
 */
public final class YostAcsPovertyUtils {

    public static final String ALG_NAME = "NAACCR Yost Quintile & Area-Based Social Measures Linkage Program";
    public static final String ALG_VERSION = "released in September 2025";

    private YostAcsPovertyUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates the ACS data for the provided ACS data input dto
     * <br/><br/>
     * The returned dto will contain yost indices and acs poverty percentages across a number of time periods
     * <br/><br/>
     * @param input a <code>ACSLinkageInputDto</code> input object
     * @return the computed ACS data
     */
    public static YostAcsPovertyOutputDto computeYostAcsPovertyData(YostAcsPovertyInputDto input) {
        YostAcsPovertyOutputDto result = new YostAcsPovertyOutputDto();

        String state = input.getAddressAtDxState();
        String county = input.getCountyAtDxAnalysis();
        String census2010 = input.getCensusTract2010();
        String census2020 = input.getCensusTract2020();
        String year = input.getDateOfDiagnosis() == null ? null : StringUtils.rightPad(input.getDateOfDiagnosis(), 4).substring(0, 4).trim();

        if (state != null && county != null && (census2010 != null || census2020 != null) && NumberUtils.isDigits(year)) {
            int dxYear = Integer.parseInt(year);

            // 1. 2006-2007 use 2008 from 2010 census
            // 2. 2008-2015 use data provided by year for 2010 census
            // 3. 2016-2017 use 2018 data from 2020 census
            // 4. 2018-2021 use provided by year for 2020 census
            // 5. 2022+ use 2021 data from 2020 census

            if (dxYear >= 2006 && dxYear <= LocalDate.now().getYear()) {

                if (!CountryData.getInstance().isYearBasedTractDataInitialized(state))
                    CountryData.getInstance().initializeYearBasedTractData(state);

                StateData stateData = CountryData.getInstance().getYearBasedTractData(state);
                if (stateData != null) {
                    CountyData countyData = stateData.getCountyData(county);
                    if (countyData != null) {
                        CensusData censusData = countyData.getCensusData(dxYear <= 2015 ? census2010 : census2020);
                        if (censusData != null) {

                            int yearForLookup = dxYear;
                            if (dxYear == 2006 || dxYear == 2007)
                                yearForLookup = 2008;
                            else if (dxYear == 2016 || dxYear == 2017)
                                yearForLookup = 2018;
                            else if (dxYear >= 2022)
                                yearForLookup = 2021;

                            YearData yearData = censusData.getYearData(String.valueOf(yearForLookup));
                            if (yearData != null) {
                                result.setYostQuintileUS(yearData.getYostQuintileUS());
                                result.setYostQuintileState(yearData.getYostQuintileState());
                                result.setAcsPctPovAllRaces(yearData.getAcsPctPovAllRaces());
                                result.setAcsPctPovWhite(yearData.getAcsPctPovWhite());
                                result.setAcsPctPovBlack(yearData.getAcsPctPovBlack());
                                result.setAcsPctPovAsianNHOPI(yearData.getAcsPctPovAsianNHOPI());
                                result.setAcsPctPovAIAN(yearData.getAcsPctPovAIAN());
                                result.setAcsPctPovWhiteNonHisp(yearData.getAcsPctPovWhiteNonHisp());
                                result.setAcsPctPovHispanic(yearData.getAcsPctPovHispanic());
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
