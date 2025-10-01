/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils;

/**
 * This class is used to calculate the cause specific death classification variables. More information can be found here:
 * <a href="https://seer.cancer.gov/causespecific">https://seer.cancer.gov/causespecific/</a>
 * <br/><br/>
 * Author: Sewbesew Bekele
 * Date: Feb 7, 2014
 */
public final class CauseSpecificUtils {

    public static final String ALG_NAME = "SEER Cause-specific Death Classification";
    public static final String ALG_VERSION = "for 2010+ submissions";

    //values for cause specific and cause others
    public static final String ALIVE_OR_DEAD_OF_OTHER_CAUSES = "0";
    public static final String DEAD = "1";
    public static final String MISSING_UNKNOWN_DEATH_OF_CODE = "8";
    public static final String SEQUENCE_NOT_APPLICABLE = "9";

    //lookup for tables (lazily loaded)
    private static final List<CauseSpecificDataDto> _DATA_SITE_SPECIFIC_2008 = new ArrayList<>();
    private static final List<CauseSpecificDataDto> _DATA_SITE_SPECIFIC_2023 = new ArrayList<>();

    private CauseSpecificUtils() {
        // no instances of this class allowed!
    }

    /**
     * Calculates cause specific and cause other death classification values for the provided input dto.
     * <br/><br/>
     * The input dto may have the following parameters:
     * <ul>
     * <li>sequenceNumberCentral</li>
     * <li>icdRevisionNumber</li>
     * <li>causeOfDeath</li>
     * <li>primarySite</li>
     * <li>histologyIcdO3</li>
     * <li>dateOfLastContactYear</li>
     * </ul>
     * <br/><br/>
     * @param input an input dto which has the fields used to compute cause specific values as parameter.
     * @return the computed cause specific and cause other death classification values. The output values are:
     * <ul>
     * <li>ALIVE OR DEAD OF OTHER CAUSES = "0"</li>
     * <li>DEAD = "1"</li>
     * <li>MISSING UNKNOWN DEATH CODE = "8"</li>
     * <li>SEQUENCE NOT APPLICABLE = "9"</li>
     * </ul>
     */
    public static CauseSpecificResultDto computeCauseSpecific(CauseSpecificInputDto input) {
        return computeCauseSpecific(input, Calendar.getInstance().get(Calendar.YEAR), SeerSiteRecodeUtils.VERSION_DEFAULT);
    }

    /**
     * Calculates cause specific and cause other death classification values for the provided input dto.
     * <br/><br/>
     * The input dto may have the following parameters:
     * <ul>
     * <li>sequenceNumberCentral</li>
     * <li>icdRevisionNumber</li>
     * <li>causeOfDeath</li>
     * <li>primarySite</li>
     * <li>histologyIcdO3</li>
     * <li>dateOfLastContactYear</li>
     * </ul>
     * <br/><br/>
     * @param input an input dto which has the fields used to compute cause specific values as parameter.
     * @param cutOffYear submission year, if date of last contact is beyond this year, patient is assumed alive.
     * @return the computed cause specific and cause other death classification values. The output values are:
     * <ul>
     * <li>ALIVE OR DEAD OF OTHER CAUSES = "0"</li>
     * <li>DEAD = "1"</li>
     * <li>MISSING UNKNOWN DEATH CODE = "8"</li>
     * <li>SEQUENCE NOT APPLICABLE = "9"</li>
     * </ul>
     */
    public static CauseSpecificResultDto computeCauseSpecific(CauseSpecificInputDto input, int cutOffYear) {
        return computeCauseSpecific(input, cutOffYear, SeerSiteRecodeUtils.VERSION_DEFAULT);
    }

    /**
     * Calculates cause specific and cause other death classification values for the provided input dto and cut off year.
     * <br/><br/>
     * The input dto may have the following parameters:
     * <ul>
     * <li>sequenceNumberCentral</li>
     * <li>icdRevisionNumber</li>
     * <li>causeOfDeath</li>
     * <li>primarySite</li>
     * <li>histologyIcdO3</li>
     * <li>dateOfLastContactYear</li>
     * </ul>
     * <br/><br/>
     * @param input an input dto which has the fields used to compute cause specific values as parameter.
     * @param cutOffYear submission year, if date of last contact is beyond this year, patient is assumed alive.
     * @param seerSiteRecodeVersion the SEER Site Recode version to use
     * @return the computed cause specific and cause other death classification values. The output values are:
     * <ul>
     * <li>ALIVE OR DEAD OF OTHER CAUSES = "0"</li>
     * <li>DEAD = "1"</li>
     * <li>MISSING UNKNOWN DEATH CODE = "8"</li>
     * <li>SEQUENCE NOT APPLICABLE = "9"</li>
     * </ul>
     */
    @SuppressWarnings({"DuplicateExpressions", "java:S1871"}) // branches with the same outcome
    public static CauseSpecificResultDto computeCauseSpecific(CauseSpecificInputDto input, int cutOffYear, String seerSiteRecodeVersion) {
        CauseSpecificResultDto result = new CauseSpecificResultDto();

        int seq = NumberUtils.isDigits(input.getSequenceNumberCentral()) ? Integer.parseInt(input.getSequenceNumberCentral()) : -1;
        if (seq < 0 || seq > 59) {
            result.setCauseSpecificDeathClassification(SEQUENCE_NOT_APPLICABLE);
            result.setCauseOtherDeathClassification(SEQUENCE_NOT_APPLICABLE);
            return result;
        }

        int dolc = NumberUtils.toInt(input.getDateOfLastContactYear(), 9999);
        String icd = input.getIcdRevisionNumber();
        //If patient is alive at last fup before submission
        if ("0".equals(icd) || (dolc != 9999 && dolc > cutOffYear)) {
            result.setCauseSpecificDeathClassification(ALIVE_OR_DEAD_OF_OTHER_CAUSES);
            result.setCauseOtherDeathClassification(ALIVE_OR_DEAD_OF_OTHER_CAUSES);
            return result;
        }

        if (input.getCauseOfDeath() == null || input.getCauseOfDeath().length() < 3 || "7777".equals(input.getCauseOfDeath()) || "7797".equals(input.getCauseOfDeath())) {
            result.setCauseSpecificDeathClassification(MISSING_UNKNOWN_DEATH_OF_CODE);
            result.setCauseOtherDeathClassification(MISSING_UNKNOWN_DEATH_OF_CODE);
            return result;
        }

        int hist = NumberUtils.toInt(input.getHistologyIcdO3(), -1);
        String cod = input.getCauseOfDeath().toUpperCase();
        String cod3dig = cod.length() >= 3 ? cod.substring(0, 3) : "";
        String recode = SeerSiteRecodeUtils.calculateSiteRecode(seerSiteRecodeVersion, input.getPrimarySite(), input.getHistologyIcdO3());

        // first do all the non-site-specific checks; some condition could be added to the text file which represents the tables. But I decided to use the same file and same structure of code as SAS
        int causeSpecific = 0;
        if (seq == 0) {
            if ("8".equals(icd)) {
                //any cancer 140-239, coded as dead;
                if ("140".compareTo(cod3dig) <= 0 && "239".compareTo(cod3dig) >= 0)
                    causeSpecific = 1;
            }
            else if ("9".equals(icd)) {
                //any cancer 140-239 and hiv & malignant 042.2, coded as dead
                if (("140".compareTo(cod3dig) <= 0 && "239".compareTo(cod3dig) >= 0) || "0422".equals(cod))
                    causeSpecific = 1;
            }
            else if ("1".equals(icd)) {
                //any cancer C00-D489 & AIDS & cancer B210-B219, coded as dead
                if (("C00".compareTo(cod3dig) <= 0 && "D489".compareTo(cod) >= 0) || "B21".equals(cod3dig))
                    causeSpecific = 1;
                //The last 4 rows (special cases) under miscellaneous, D36, D45-47 for histology 9950, 9960-9964, 9980-9989 are included in the above condition             
            }
        }
        else {
            if ("8".equals(icd)) {
                //unknown primary & secondary site 199, coded as dead;
                if ("199".equals(cod3dig))
                    causeSpecific = 1;
                    //Melanoma of any site (8720-8799) with a cause of death of 172, 216.9, or 232.2 is coded as 'dead' (footnote)
                else if (hist >= 8720 && hist <= 8799 && ("172".equals(cod3dig) || "2169".equals(cod) || "2322".equals(cod)))
                    causeSpecific = 1;
            }
            else if ("9".equals(icd)) {
                //unknown primary & secondary site 199, coded as dead;
                if ("199".equals(cod3dig))
                    causeSpecific = 1;
                    //Melanoma of any site (8720-8799) with a cause of death of 172, 216, or 232 is coded as 'dead' (footnote)
                else if (hist >= 8720 && hist <= 8799 && ("172".equals(cod3dig) || "216".equals(cod3dig) || "232".equals(cod3dig)))
                    causeSpecific = 1;
            }
            else if ("1".equals(icd)) {
                //Secondary other specified C798, unknown primary C80, multiple cancer C97, neoplasm nos D489, coded as dead;
                if ("C798".equals(cod) || "C80".equals(cod3dig) || "C97".equals(cod3dig) || "D489".equals(cod))
                    causeSpecific = 1;
                    //Melanoma of any site (8720-8799) with a cause of death of C43, D03 or D22 is coded as 'dead'. (footnote)
                else if (hist >= 8720 && hist <= 8799 && ("C43".equals(cod3dig) || "D03".equals(cod3dig) || "D22".equals(cod3dig)))
                    causeSpecific = 1;
                    //The last 4 rows (special cases) under miscellaneous, C77, C81-96,D36,D45-47 for histology 9950, 9960-9964, 9980-9989 and C00-D48, D619 for other
                else if (SeerSiteRecodeUtils.VERSION_2008.equals(seerSiteRecodeVersion)) {
                    if (hist == 9950 || (hist >= 9960 && hist <= 9964) || (hist >= 9980 && hist <= 9989)) {
                        if ("C77".equals(cod3dig) || ("C81".compareTo(cod3dig) <= 0 && "C96".compareTo(cod3dig) >= 0) || "D36".equals(cod3dig) || ("D45".compareTo(cod3dig) <= 0 && "D47".compareTo(
                                cod3dig)
                                >= 0))
                            causeSpecific = 1;
                    }
                    else if ("37000".equals(recode) && (("C00".compareTo(cod3dig) <= 0 && "D489".compareTo(cod) >= 0) || "D619".equals(cod)))
                        causeSpecific = 1;
                }
            }
        }

        //If we get a result, stop. otherwise continue to site specific
        if (causeSpecific == 1) {
            result.setCauseSpecificDeathClassification(DEAD);
            result.setCauseOtherDeathClassification(ALIVE_OR_DEAD_OF_OTHER_CAUSES);
            return result;
        }

        for (CauseSpecificDataDto obj : getData(seerSiteRecodeVersion)) {
            if (obj.doesMatchThisRow(input.getIcdRevisionNumber(), seq == 0 ? "0" : "1-59", recode, cod)) {
                result.setCauseSpecificDeathClassification(DEAD);
                result.setCauseOtherDeathClassification(ALIVE_OR_DEAD_OF_OTHER_CAUSES);
                return result;
            }
        }

        //If not found in the table cause-specific = '0' and cause- other = '1'
        result.setCauseSpecificDeathClassification(ALIVE_OR_DEAD_OF_OTHER_CAUSES);
        result.setCauseOtherDeathClassification(DEAD);
        return result;
    }

    private static synchronized List<CauseSpecificDataDto> getData(String version) {
        if (SeerSiteRecodeUtils.VERSION_2008.equals(version)) {
            if (_DATA_SITE_SPECIFIC_2008.isEmpty())
                loadData("data_2008.txt", _DATA_SITE_SPECIFIC_2008);
            return _DATA_SITE_SPECIFIC_2008;
        }
        else if (SeerSiteRecodeUtils.VERSION_2023.equals(version)) {
            if (_DATA_SITE_SPECIFIC_2023.isEmpty())
                loadData("data_2023.txt", _DATA_SITE_SPECIFIC_2023);
            return _DATA_SITE_SPECIFIC_2023;
        }
        else
            throw new IllegalArgumentException("Unsupported SEER Site Recode version: " + version);
    }

    private static void loadData(String filename, List<CauseSpecificDataDto> result) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("causespecific/" + filename)) {
            if (is == null)
                throw new IllegalStateException("Unable to find data file '" + filename + "'");

            try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
                String line = reader.readLine();

                //skip first line
                if (reader.getLineNumber() == 1)
                    line = reader.readLine();

                while (line != null) {
                    result.add(new CauseSpecificDataDto(StringUtils.split(line, ';')));
                    line = reader.readLine();
                }
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to load " + filename, e);
        }
    }
}