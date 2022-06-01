package com.imsweb.algorithms.yostacspoverty;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class can be used to calculate ACS Linkage variables.
 * Created on Oct 13, 2017 by howew
 * @author howew
 */
public final class YostAcsPovertyUtils {

    public static final String ALG_NAME = "NAACCR Yost Quintile & Area-Based Social Measures Linkage Program";
    public static final String ALG_VERSION = "version 2.0 released in August 2020";

    private static final YostAcsPovertyDataProvider _PROVIDER = new YostAcsPovertyDataProvider();

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
        if (input.isFullyInitialized()) {
            String dxYear = StringUtils.rightPad(input.getDateOfDiagnosis(), 4).substring(0, 4).trim();
            if (!StringUtils.isBlank(dxYear) && NumberUtils.isDigits(dxYear)) {
                int yearDX = Integer.parseInt(dxYear);
                if (yearDX >= 2005 && yearDX <= LocalDate.now().getYear())
                    result = _PROVIDER.getYostAcsPovertyData(input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010());
            }
        }
        return result;
    }
}
