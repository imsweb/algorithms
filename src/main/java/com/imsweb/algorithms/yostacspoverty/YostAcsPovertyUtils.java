package com.imsweb.algorithms.yostacspoverty;

import com.imsweb.algorithms.internal.CensusData;
import com.imsweb.algorithms.internal.CountryData;
import com.imsweb.algorithms.internal.CountyData;
import com.imsweb.algorithms.internal.StateData;
import com.imsweb.algorithms.internal.YearData;

import java.time.LocalDate;

/**
 * This class can be used to calculate ACS Linkage variables.
 * Created on Oct 13, 2017 by howew
 * @author howew
 */
public final class YostAcsPovertyUtils {

    public static final String ALG_NAME = "NAACCR Yost Quintile & Area-Based Social Measures Linkage Program";
    public static final String ALG_VERSION = "August 13, 2026";

    public static final int YOST_ACS_CENSUS_TRACT_PIVOT_YEAR = 2017;

    //Unknown values for each code
    public static final String YOST_ACS_UNK_A = "A";
    public static final String YOST_ACS_UNK_B = "B";
    public static final String YOST_ACS_UNK_C = "C";
    public static final String YOST_ACS_UNK_D = "D";

    private YostAcsPovertyUtils() {
        // no instances of this class allowed!
    }

    /**
     * Helper method to compute the lookup year.
     * The algorithm uses data from 2006 to 2024, but it uses 5-years estimate data points, meaning it only has data points from 2008 to 2022.
     * Different DX years use different data points:
     *   - 2006-2007 use the 2008 data point from 2010 census
     *   - 2008-2017 use the provided year data point for 2010 census (so 2008 uses the 2008 data point, 2009 uses the 2009 data point, etc...)
     *   - 2018-2022 use the provided year data point for 2020 census (so 2018 uses the 2018 data point, etc...)
     *   - 2023+ use the 2022 data point from 2020 census
     * @param dxYear the year of diagnosis from the input dto
     * @return the dx year to use for the lookup
     */
    private static int computeYearForLookup(int dxYear) {
        int yearForLookup = dxYear;
        if (dxYear == 2006 || dxYear == 2007)
            yearForLookup = 2008;
        else if (dxYear >= 2023)
            yearForLookup = 2022;
        return yearForLookup;
    }

    /**
     * Calculates the ACS data for the provided ACS data input dto
     * <br/><br/>
     * The returned dto will contain yost indices and acs poverty percentages across a number of time periods
     * or one of the unknown values listed below:
     * <ul>
     * <li>A = State, county, tract, or year of dx are invalid</li>
     * <li>B = State, tract, and year of dx are valid, but county was not reported</li>
     * <li>C = State + county + tract + year combination was not found</li>
     * <li>D = State, county, tract, or year of dx are blank or unknown/li>
     * </ul>
     * <br/><br/>
     * @param input a <code>ACSLinkageInputDto</code> input object
     * @return the computed ACS data
     */
    public static YostAcsPovertyOutputDto computeYostAcsPovertyData(YostAcsPovertyInputDto input) {
        YostAcsPovertyOutputDto result = new YostAcsPovertyOutputDto();

        input.applyRecodes();

        if (input.hasInvalidStateCountyCensusTractOrYear())
            result.setAllFields(YOST_ACS_UNK_A);
        else if (input.hasUnknownStateCountyCensusTractOrYear())
            result.setAllFields(YOST_ACS_UNK_D);
        else if (input.countyIsNotReported())
            result.setAllFields(YOST_ACS_UNK_B);
        else {
            int dxYear = input.computeYearOfDiagnosis();
            if (dxYear >= 2006 && dxYear <= LocalDate.now().getYear()) {
                String state = input.getAddressAtDxState();
                String county = input.getCountyAtDxAnalysis();
                String census2010 = input.getCensusTract2010();
                String census2020 = input.getCensusTract2020();

                if (!CountryData.getInstance().isYearBasedTractDataInitialized(state))
                    CountryData.getInstance().initializeYearBasedTractData(state);

                StateData stateData = CountryData.getInstance().getYearBasedTractData(state);
                if (stateData != null) {
                    CountyData countyData = stateData.getCountyData(county);
                    if (countyData != null) {
                        CensusData censusData = countyData.getCensusData(dxYear <= YOST_ACS_CENSUS_TRACT_PIVOT_YEAR ? census2010 : census2020);
                        if (censusData != null) {
                            int yearForLookup = computeYearForLookup(dxYear);
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
        if (result.getYostQuintileUS() == null)
            result.setYostQuintileUS(YOST_ACS_UNK_C);
        if (result.getYostQuintileState() == null)
            result.setYostQuintileState(YOST_ACS_UNK_C);
        if (result.getAcsPctPovAllRaces() == null)
            result.setAcsPctPovAllRaces(YOST_ACS_UNK_C);
        if (result.getAcsPctPovWhite() == null)
            result.setAcsPctPovWhite(YOST_ACS_UNK_C);
        if (result.getAcsPctPovBlack() == null)
            result.setAcsPctPovBlack(YOST_ACS_UNK_C);
        if (result.getAcsPctPovAsianNHOPI() == null)
            result.setAcsPctPovAsianNHOPI(YOST_ACS_UNK_C);
        if (result.getAcsPctPovAIAN() == null)
            result.setAcsPctPovAIAN(YOST_ACS_UNK_C);
        if (result.getAcsPctPovWhiteNonHisp() == null)
            result.setAcsPctPovWhiteNonHisp(YOST_ACS_UNK_C);
        if (result.getAcsPctPovHispanic() == null)
            result.setAcsPctPovHispanic(YOST_ACS_UNK_C);

        return result;
    }
}