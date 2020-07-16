package com.imsweb.algorithms.acslinkage;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class can be used to calculate ACS Linkage variables.
 * Created on Oct 13, 2017 by howew
 * @author howew
 */
public final class AcsLinkageUtils {

    public static final String ALG_NAME = "NAACCR ACS Linkage Program";
    public static final String ALG_VERSION = "3.0";
    public static final String ALG_INFO = "NAACCR ACS Linkage Program version 3.0, released in October 2019";

    private static AcsLinkageDataProvider _PROVIDER;

    /**
     * Calculates the ACS data for the provided ACS data input dto
     * <br/><br/>
     * The returned dto will contain yost indices and acs poverty percentages across a number of time periods
     * <br/><br/>
     * @param input a <code>ACSLinkageInputDto</code> input object
     * @return the computed ACS data
     */
    public static AcsLinkageOutputDto computeACSData(AcsLinkageInputDto input) {
        AcsLinkageOutputDto result = new AcsLinkageOutputDto();
        if (input.isFullyInitialized()) {
            String dxYear = StringUtils.rightPad(input.getDateOfDiagnosis(), 4).substring(0, 4).trim();
            if (!StringUtils.isBlank(dxYear) && NumberUtils.isDigits(dxYear)) {
                int yearDX = Integer.parseInt(dxYear);
                if (yearDX >= 2005 && yearDX <= LocalDate.now().getYear()) {
                    if (_PROVIDER == null)
                        initializeInternalDataProvider();
                    result = _PROVIDER.getAcsData(input.getAddressAtDxState(), input.getCountyAtDxAnalysis(), input.getCensusTract2010());
                }
            }
        }
        return result;
    }

    /**
     * Use this method to register your own data provider instead of using the internal one that is entirely in memory.
     * <br/><br/>
     * This has to be done before the first call to the compute method, or the internal one will be registered by default.
     * <br/><br/>
     * Once a provider has been set, this method cannot be called (it will throw an exception).
     * @param provider the <code>ACSLinkageDataProvider</code> to set
     */
    public static synchronized void setDataProvider(AcsLinkageDataProvider provider) {
        if (_PROVIDER != null)
            throw new RuntimeException("The data provider has already been set!");
        _PROVIDER = provider;
    }

    private static synchronized void initializeInternalDataProvider() {
        if (_PROVIDER != null)
            return;
        _PROVIDER = new AcsLinkageCsvData();
    }
}
