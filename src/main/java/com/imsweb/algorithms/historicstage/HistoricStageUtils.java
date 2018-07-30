/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.opencsv.CSVReaderBuilder;

import com.imsweb.algorithms.historicstage.internal.HistStageDataCsExtDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataCsMetsDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataCsNodeDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataCsStageDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod0StageDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod10ExtDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod10NodeDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod10StageDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod13digBladderDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod13digExtDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod13digGeneralStageDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod13digLungDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod13digMelanomaDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod13digNodeDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod2digDirectStageDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod2digExtDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod2digExtNodeStageDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod2digNodeDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEod4digStageDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataEodPatchDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataLeukemiaDto;
import com.imsweb.algorithms.historicstage.internal.HistStageDataSchemaDto;

/**
 * This class is used to calculate Historic Stage value.
 * Created on May 20, 2013
 * @author Sewbesew Bekele
 */

public final class HistoricStageUtils {

    public static final String PROP_DATE_OF_DIAGNOSIS_YEAR = "dateOfDiagnosisYear";
    public static final String PROP_PRIMARY_SITE = "primarySite";
    public static final String PROP_HISTOLOGY_3 = "histologyIcdO3";
    public static final String PROP_BEHAVIOR_3 = "behaviorIcdO3";
    public static final String PROP_TYPE_OF_REPORTING_SOURCE = "typeOfReportingSource";
    public static final String PROP_REGISTRY_ID = "registryId";
    public static final String PROP_CS_EXTENSION = "csExtension";
    public static final String PROP_CS_LYMPH_NODES = "csLymphNodes";
    public static final String PROP_CS_METS_AT_DX = "csMetsAtDx";
    public static final String PROP_CS_SITE_SPECIFIC_FACTOR1 = "csSiteSpecificFactor1";
    public static final String PROP_CS_SITE_SPECIFIC_FACTOR2 = "csSiteSpecificFactor2";
    public static final String PROP_EOD_CODING_SYSTEM = "eodCodingSys";
    public static final String PROP_EOD10_LYMPH_NODES = "eodLymphNodeInvolv";
    public static final String PROP_EOD10_EXTENSION = "eodExtension";
    public static final String PROP_EOD4_DIGIT_EXTENSION = "eodOld4DigitExtent";
    public static final String PROP_EOD4_DIGIT_NODES = "eodOld4DigitNodes";
    public static final String PROP_EOD4_DIGIT_SIZE = "eodOld4DigitSize";
    public static final String PROP_EOD13_DIGIT = "eodOld13Digit";
    public static final String PROP_EOD2_DIGIT = "eodOld2Digit";

    public static final String IN_SITU = "0";
    public static final String LOCALIZED = "1";
    public static final String REGIONAL = "2";
    public static final String DISTANT = "4";
    public static final String UNSTAGED = "9";
    public static final String NEED_REVIEW = "6";
    public static final String NOT_APPLICABLE = "7";

    //lookups for tables used in calculation of historic stage
    private static List<HistStageDataEod10ExtDto> _DATA_EOD10_EXT = new ArrayList<>();
    private static List<HistStageDataEod10NodeDto> _DATA_EOD10_NODE = new ArrayList<>();
    private static List<HistStageDataEod10StageDto> _DATA_EOD10_STAGE = new ArrayList<>();
    private static List<HistStageDataSchemaDto> _DATA_HISTORIC_STAGE_SCHEMA = new ArrayList<>();
    private static List<HistStageDataCsExtDto> _DATA_CS_EXT = new ArrayList<>();
    private static List<HistStageDataCsNodeDto> _DATA_CS_NODE = new ArrayList<>();
    private static List<HistStageDataCsMetsDto> _DATA_CS_METS = new ArrayList<>();
    private static List<HistStageDataCsStageDto> _DATA_CS_STAGE = new ArrayList<>();
    private static List<HistStageDataLeukemiaDto> _DATA_LEUKEMIA = new ArrayList<>();
    private static List<HistStageDataEodPatchDto> _DATA_EOD_PATCH = new ArrayList<>();
    private static List<HistStageDataEod4digStageDto> _DATA_EOD_4DIG_STAGE = new ArrayList<>();
    private static List<HistStageDataEod13digExtDto> _DATA_EOD_13DIG_EXT = new ArrayList<>();
    private static List<HistStageDataEod13digNodeDto> _DATA_EOD_13DIG_NODE = new ArrayList<>();
    private static List<HistStageDataEod13digLungDto> _DATA_EOD_13DIG_LUNG = new ArrayList<>();
    private static List<HistStageDataEod13digMelanomaDto> _DATA_EOD_13DIG_MELANOMA = new ArrayList<>();
    private static List<HistStageDataEod13digBladderDto> _DATA_EOD_13DIG_BLADDER = new ArrayList<>();
    private static List<HistStageDataEod13digGeneralStageDto> _DATA_EOD_13DIG_GENERAL_STAGE = new ArrayList<>();
    private static List<HistStageDataEod2digExtDto> _DATA_EOD_2DIG_EXT = new ArrayList<>();
    private static List<HistStageDataEod2digNodeDto> _DATA_EOD_2DIG_NODE = new ArrayList<>();
    private static List<HistStageDataEod2digExtNodeStageDto> _DATA_EOD_2DIG_EXTENSION_NODE_STAGE = new ArrayList<>();
    private static List<HistStageDataEod2digDirectStageDto> _DATA_EOD_2DIG_DIRECT_STAGE = new ArrayList<>();
    private static List<HistStageDataEod0StageDto> _DATA_EOD0_STAGE = new ArrayList<>();

    /**
     * Private constructor, no instantiation...
     */
    private HistoricStageUtils() {
    }

    /**
     * Calculates the Historic Stage for the provided record.
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm wil use the following ones:
     * <ul>
     * <li>primarySite (#400)</li>
     * <li>histologyIcdO3 (#522)</li>
     * <li>behaviorIcdO3 (#523)</li>
     * <li>csExtension (#2810)</li>
     * <li>cSLymphNodes (#2830)</li>
     * <li>csMetsAtDx (#2850)</li>
     * <li>dateOfDiagnosisYear (#390)</li>
     * <li>eodLymphNodeInvolv (#810)</li>
     * <li>eodExtension (#790)</li>
     * <li>typeOfReportingSource (#500)</li>
     * <li>eodOld4DigitExtent (#860)</li>
     * <li>eodOld4DigitNodes (#860)</li>
     * <li>eodOld4DigitSize (#860)</li>
     * <li>registryId (#40)</li>
     * <li>eodCodingSys (#870)</li>
     * <li>eodOld13Digit (#840)</li>
     * <li>eodOld2Digit (#850)</li>
     * <li>csSiteSpecificFactor1 (#2880)</li>
     * <li>csSiteSpecificFactor2 (#2890)</li>
     * </ul>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * @param record a map of properties representing a NAACCR line
     * @return the computed historic stage. The out put values are:
     * <ul>
     * <li>IN_SITU = "0"</li>
     * <li>LOCALIZED = "1"</li>
     * <li>REGIONAL = "2"</li>
     * <li>DISTANT = "4"</li>
     * <li>UNSTAGED = "9"</li>
     * <li>NEED_REVIEW = "6"</li>
     * <li>NOT_APPLICABLE = "7"</li>
     * </ul>
     */
    public static HistoricStageResultsDto computeHistoricStage(Map<String, String> record) {
        HistoricStageInputDto input = new HistoricStageInputDto();
        input.setDateOfDiagnosisYear(record.get(PROP_DATE_OF_DIAGNOSIS_YEAR));
        input.setPrimarysite(record.get(PROP_PRIMARY_SITE));
        input.setHistologyIcdO3(record.get(PROP_HISTOLOGY_3));
        input.setBehaviorIcdO3(record.get(PROP_BEHAVIOR_3));
        input.setTypeOfReportingSource(record.get(PROP_TYPE_OF_REPORTING_SOURCE));
        input.setRegistryId(record.get(PROP_REGISTRY_ID));
        input.setCsExtension(record.get(PROP_CS_EXTENSION));
        input.setCsLymphNodes(record.get(PROP_CS_LYMPH_NODES));
        input.setCsMetsAtDx(record.get(PROP_CS_METS_AT_DX));
        input.setCsSiteSpecificFactor1(record.get(PROP_CS_SITE_SPECIFIC_FACTOR1));
        input.setCsSiteSpecificFactor2(record.get(PROP_CS_SITE_SPECIFIC_FACTOR2));
        input.setEodCodingSys(record.get(PROP_EOD_CODING_SYSTEM));
        input.setEodExtension(record.get(PROP_EOD10_EXTENSION));
        input.setEodLymphNodeInvolv(record.get(PROP_EOD10_LYMPH_NODES));
        input.setEodOld4DigitExtent(record.get(PROP_EOD4_DIGIT_EXTENSION));
        input.setEodOld4DigitNodes(record.get(PROP_EOD4_DIGIT_NODES));
        input.setEodOld4DigitSize(record.get(PROP_EOD4_DIGIT_SIZE));
        input.setEodOld13Digit(record.get(PROP_EOD13_DIGIT));
        input.setEodOld2Digit(record.get(PROP_EOD2_DIGIT));
        return computeHistoricStage(input);

    }

    //This method uses special object of input which contains only the necessary properties to calculate historic stage
    public static HistoricStageResultsDto computeHistoricStage(HistoricStageInputDto input) {
        HistoricStageResultsDto historicStage = new HistoricStageResultsDto(UNSTAGED);
        String diagnosisYear = input.getDateOfDiagnosisYear();
        int currentYear = LocalDate.now().getYear();

        //check for valid values of diagnosis year
        if (NumberUtils.isDigits(diagnosisYear) && Integer.parseInt(diagnosisYear) <= currentYear && Integer.parseInt(diagnosisYear) > 1900) {
            //2004+ coding
            if (Integer.parseInt(diagnosisYear) >= 2004) {
                String primarySite = input.getPrimarySite();
                String histology3 = input.getHistologyIcdO3();
                historicStage.setResult(NEED_REVIEW);
                //check for NPE primaries and histologies.
                if (primarySite != null && histology3 != null) {
                    primarySite = formattedSite(primarySite);
                    //check for non-integer primaries and histologies. ("C" for primary sites is already removed, it should be an integer now. )
                    if (NumberUtils.isDigits(primarySite) && NumberUtils.isDigits(histology3)) {
                        int iSite = Integer.parseInt(primarySite);
                        int iHist = Integer.parseInt(histology3);
                        //check roughly for the validity of primary site and histology
                        if (iSite >= 0 && iSite <= 999 && iHist >= 8000 && iHist <= 9999) {
                            //Non leukemia DCO  cases, stage = 9
                            if ("7".equals(input.getTypeOfReportingSource()) && !isLeukemia(primarySite, histology3))
                                historicStage.setResult(UNSTAGED);
                            else {
                                // Calculate Schema from given primary site and histology
                                String historicStageSchema = calculateHistoricStageSchema(primarySite, histology3);
                                //if we have valid necessary inputs apply cs tables.
                                if (historicStageSchema != null && NumberUtils.isDigits(input.getCsExtension()) &&
                                        NumberUtils.isDigits(input.getCsLymphNodes()) && NumberUtils.isDigits(input.getCsMetsAtDx()))
                                    historicStage.setResult(runCsTables(input, historicStageSchema));
                            }
                        }
                    }
                }
            }

            //2004 Earlier
            else {
                String primarySite = input.getPrimarySite();
                String histology3 = input.getHistologyIcdO3();
                //check for NPE primaries and histologies.
                if (primarySite != null && histology3 != null) {
                    primarySite = formattedSite(primarySite);
                    //check for non-integer primaries and histologies. ("C" for primary sites is already removed, it should be an integer now. )
                    if (NumberUtils.isDigits(primarySite) && NumberUtils.isDigits(histology3)) {
                        int iSite = Integer.parseInt(primarySite);
                        int iHist = Integer.parseInt(histology3);
                        //check roughly for the validity of primary site and histology
                        if (iSite >= 0 && iSite <= 999 && iHist >= 8000 && iHist <= 9999) {
                            //If year < 2004 & eod coding system is blank then no stage (changed to not applicable "7" based on meeting on June 4, 2013)
                            if (input.getEodCodingSys() == null || input.getEodCodingSys().trim().isEmpty())
                                historicStage.setResult(NOT_APPLICABLE);
                                //else apply the corresponding eod coding system
                            else
                                historicStage = apply2004Earlier(input);
                        }
                    }
                }
            }
        }

        return historicStage;
    }

    protected static HistoricStageResultsDto apply2004Earlier(HistoricStageInputDto input) {
        HistoricStageResultsDto historicStage = new HistoricStageResultsDto();
        String primarySite = formattedSite(input.getPrimarySite());
        String histology3 = input.getHistologyIcdO3();
        String codingSystem = input.getEodCodingSys();
        String eod13dig = input.getEodOld13Digit();
        int iHist = Integer.parseInt(histology3);
        int iSite = Integer.parseInt(primarySite);

        //EOD Coding System = 4 Coding
        if ("4".equals(codingSystem)) {

            //Los-angeles cases 1988-1991 – no stage (changed to not applicable ("7") based on meeting on June 4, 2013)
            int diagnosisYear = Integer.parseInt(input.getDateOfDiagnosisYear());
            if (NumberUtils.isDigits(input.getRegistryId()) && Integer.parseInt(input.getRegistryId()) == 1535 && diagnosisYear >= 1988 && diagnosisYear <= 1991)
                historicStage.setResult(NOT_APPLICABLE);

                //Non leukemia DCO cases, stage = 9, leukemia DCO cases, stage = 4
            else if ("7".equals(input.getTypeOfReportingSource())) {
                if (isLeukemia(primarySite, histology3))
                    historicStage.setResult(DISTANT);
                else
                    historicStage.setResult(UNSTAGED);
            }

            else {
                //Otherwise run EOD10 tables for valid eod10 extension and eod 10 lymph node values
                String eod10Ext = input.getEodExtension();
                String eod10Node = input.getEodLymphNodeInvolv();
                if (NumberUtils.isDigits(eod10Ext) && NumberUtils.isDigits(eod10Node))
                    historicStage.setResult(runEod10Tables(input));

                //- Bladder cases (primary site: 670-679) staged to IS (0) get changed to Local (1)
                if (IN_SITU.equals(historicStage.getResult()) && iSite >= 670 && iSite <= 679)
                    historicStage.setResult(LOCALIZED);

                //- Certain brain cases (primary site: 710-719 , hist icd-o-3: 8000-9529, 9540-9589) get set to unstaged (9)
                if (((iHist >= 8000 && iHist <= 9529) || (iHist >= 9540 && iHist <= 9589)) && (iSite >= 710 && iSite <= 719))
                    historicStage.setResult(UNSTAGED);

                //If anything is not handled in above tables, historic stage = 9
                if (historicStage.getResult() == null)
                    historicStage.setResult(UNSTAGED);
            }
        }

        else if ("3".equals(codingSystem) || "2".equals(codingSystem) || "1".equals(codingSystem) || "0".equals(codingSystem)) {
            //Run EOD Patch Table
            String reportingSource = input.getTypeOfReportingSource();
            if (reportingSource != null)
                historicStage.setResult(runEodPatchTable(reportingSource, primarySite, histology3));

            //If stage not found in Patch table, process according to EOD Coding System per below
            //EOD Coding System = 3 Coding
            if (historicStage.getResult() == null && "3".equals(codingSystem)) {
                //If EOD 4-digit Extension is blank or invalid, set to 99, If EOD 4-digit Nodes is blank or invalid, set to 99
                String eod4Ext = input.getEodOld4DigitExtent() == null || input.getEodOld4DigitExtent().trim().isEmpty() ? "99" : input.getEodOld4DigitExtent();
                String eod4Node = input.getEodOld4DigitNodes() == null || input.getEodOld4DigitNodes().trim().isEmpty() ? "99" : input.getEodOld4DigitNodes();
                //Run EOD 4dig Stage table
                historicStage.setResult(runEod4DigStageTable(histology3, primarySite, eod4Ext, eod4Node));
                //If anything is not handled in above tables, historic stage = 9
                if (historicStage.getResult() == null)
                    historicStage.setResult(UNSTAGED);
                //If site = 569 (ovary) and EOD 4dig Size = ‘02’ & EOD 4dig Ext > 0, set stage to distant (4)
                if ("569".equals(formattedSite(input.getPrimarySite())) && "02".equals(input.getEodOld4DigitSize()) && Integer.parseInt(eod4Ext) > 0)
                    historicStage.setResult(DISTANT);
            }

            //EOD Coding System = 2 coding
            else if (historicStage.getResult() == null && "2".equals(codingSystem) && eod13dig != null && eod13dig.length() == 13) {
                //For Histologies 8000-9589:
                if (iHist >= 8000 && iHist <= 9589) {
                    //Lung cases (Site 340-349): Run EOD 13dig Lung Table (inputs: EOD 13dig Digits 4-13, Output: Historic Stage). If anything not handled, stage = 9.
                    if (iSite >= 340 && iSite <= 349)
                        historicStage.setResult(runEod13LungTable(eod13dig));
                        //Melanoma skin/vulva/penis cases (Sites 440-447, 510-519, 600-609; Histologies 8720 – 8799):
                    else if (((iSite >= 440 && iSite <= 447) || (iSite >= 510 && iSite <= 519) || (iSite >= 600 && iSite <= 609)) && (iHist >= 8720 && iHist <= 8799))
                        historicStage.setResult(runEod13MelanomaTable(primarySite, eod13dig));

                        //Bladder cases (Site 670-679): Run EOD 13dig Bladder table (Inputs: EOD 13dig Digits 5-7 & 9-13, Output: Historic Stage).  If anything not handled, stage = 9.
                    else if (iSite >= 670 && iSite <= 679)
                        historicStage.setResult(runEod13BladderTable(eod13dig));

                        //All other sites: Run EOD 13dig General Stage table (Inputs: Primary Site, EOD 13dig Ext Recode, EOD 13dig Node Recode; Output: Historic Stage).
                    else
                        historicStage.setResult(runEod13GeneralStageTable(primarySite, eod13dig));

                    //If anything not handled, stage = 9.
                    if (historicStage.getResult() == null)
                        historicStage.setResult(UNSTAGED);
                }
                //Other Histologies: Stage = 9
                else
                    historicStage.setResult(UNSTAGED);
            }

            //EOD Coding System = 1 coding            
            else if (historicStage.getResult() == null && "1".equals(codingSystem) && input.getEodOld2Digit() != null)
                historicStage.setResult(runEod2DigTables(primarySite, histology3, input.getEodOld2Digit()));

                //EOD Coding System = 0 Coding
            else if (historicStage.getResult() == null && "0".equals(codingSystem) && input.getEodOld2Digit() != null)
                historicStage.setResult(runEod0StageTable(primarySite, histology3, input.getEodOld2Digit()));
        }

        //Final ‘fix’: Cases before 1983 with icd-o-3 histology 9140 are unstaged (9)
        if (iHist == 9140 && Integer.parseInt(input.getDateOfDiagnosisYear()) <= 1983)
            historicStage.setResult(UNSTAGED);

        return historicStage;
    }

    protected static boolean isLeukemia(String primarySite, String histology3) {
        String isLeukemia = null;
        initializeLeukemiaTable();

        for (HistStageDataLeukemiaDto obj : _DATA_LEUKEMIA) {
            isLeukemia = obj.computeResult(primarySite, histology3);
            if (isLeukemia != null)
                break;
        }

        return "1".equals(isLeukemia);

    }

    protected static String calculateHistoricStageSchema(String primarySite, String histology3) {
        String historicStageSchema = null;
        initializeShemaTable();
        for (HistStageDataSchemaDto obj : _DATA_HISTORIC_STAGE_SCHEMA) {
            historicStageSchema = obj.computeResult(primarySite, histology3);
            if (historicStageSchema != null)
                break;
        }

        return historicStageSchema;
    }

    protected static String runCsTables(HistoricStageInputDto input, String historicStageSchema) {
        String historicStage = null;
        initializeCsTables();
        //Run CS Ext Table (Inputs:   CS Schema v1 , CS Extension; Output: CS Ext Recode)
        String extRecode = null;
        for (HistStageDataCsExtDto obj : _DATA_CS_EXT) {
            extRecode = obj.computeResult(historicStageSchema, input.getCsExtension());
            if (extRecode != null)
                break;
        }
        //Make CS Ext Recode exceptions – see sample code section CS Ext Recode Exceptions
        extRecode = makeCsExtRecodeExceptions(input, extRecode, historicStageSchema);
        if (extRecode != null) {
            //- Run CS Node Table (Inputs:  CS Schema v1, CS Lymph Nodes;  Output: CS Node Recode)
            String nodeRecode = null;
            for (HistStageDataCsNodeDto obj : _DATA_CS_NODE) {
                nodeRecode = obj.computeResult(historicStageSchema, input.getCsLymphNodes());
                if (nodeRecode != null)
                    break;
            }
            //- Run CS Mets Table (Inputs:  CS Schema v1, CS Mets at DX; Output: CS Mets Recode)
            String metsRecode = null;
            for (HistStageDataCsMetsDto obj : _DATA_CS_METS) {
                metsRecode = obj.computeResult(historicStageSchema, input.getCsMetsAtDx());
                if (metsRecode != null)
                    break;
            }
            //- Run CS Stage Table (Inputs:  CS Ext Recode, CS Node Recode, CS Mets Recode;  Output:  Historic Stage)
            for (HistStageDataCsStageDto obj : _DATA_CS_STAGE) {
                historicStage = obj.computeResult(extRecode, nodeRecode, metsRecode);
                if (historicStage != null)
                    break;
            }
        }

        //If anything is not handled in above tables, historic stage = 6 (for review)
        if (historicStage == null)
            historicStage = NEED_REVIEW;
        return historicStage;
    }

    protected static String runEod10Tables(HistoricStageInputDto input) {
        String historicStage = null;
        String primarySite = formattedSite(input.getPrimarySite());
        String histology3 = input.getHistologyIcdO3();
        int iSite = Integer.parseInt(primarySite);
        int iHist = Integer.parseInt(histology3);
        initializeEod10Tables();
        //- Run EOD 10 Node Table (Inputs: Primary Site, Histology 3, EOD 10 Lymph odes; Output: EOD 10 Node Recode)
        String eod10NodeRecode = null;
        for (HistStageDataEod10NodeDto obj : _DATA_EOD10_NODE) {
            eod10NodeRecode = obj.computeResult(primarySite, histology3, input.getEodLymphNodeInvolv());
            if (eod10NodeRecode != null)
                break;
        }
        //Run EOD 10 Ext Table (Inputs: Year DX, Primary Site, Histology, EOD 10 Extension; Output: EOD 10 Ext Recode)
        String eod10ExtRecode = null;
        for (HistStageDataEod10ExtDto obj : _DATA_EOD10_EXT) {
            eod10ExtRecode = obj.computeResult(input.getDateOfDiagnosisYear(), primarySite, histology3, input.getEodExtension());
            if (eod10ExtRecode != null)
                break;
        }
        //Make EOD10 Ext Recode exceptions
        // Breast cases sometimes depend on Behavior ICD-O-3
        if (iSite >= 500 && iSite <= 509 && Integer.parseInt(input.getEodExtension()) == 5) {
            if ("2".equals(input.getBehaviorIcdO3()))
                eod10ExtRecode = "1";
            else if ("3".equals(input.getBehaviorIcdO3()))
                eod10ExtRecode = "2";
        }

        // This is the special case for melanoma
        if (((440 <= iSite && iSite <= 449) || (510 <= iSite && iSite <= 519) ||
                (600 <= iSite && iSite <= 602) || (608 <= iSite && iSite <= 609) ||
                iSite == 632) && (8720 <= iHist && iHist <= 8790) &&
                Integer.parseInt(input.getEodLymphNodeInvolv()) == 3 && Integer.parseInt(input.getEodExtension()) == 99)
            eod10ExtRecode = "3";

        //Run EOD10 Stage Table (Inputs: Ext Recode, Node Recode; Output: Historic Stage)
        for (HistStageDataEod10StageDto obj : _DATA_EOD10_STAGE) {
            historicStage = obj.computeResult(eod10ExtRecode, eod10NodeRecode);
            if (historicStage != null)
                break;
        }
        return historicStage;
    }

    protected static String runEodPatchTable(String reportingSource, String primarySite, String histology3) {
        String historicStage = null;
        initializeEodPatchTable();
        for (HistStageDataEodPatchDto obj : _DATA_EOD_PATCH) {
            historicStage = obj.computeResult(reportingSource, primarySite, histology3);
            if (historicStage != null)
                break;
        }
        return historicStage;
    }

    protected static String runEod13LungTable(String eod13dig) {
        String historicStage = null;
        String dig4 = Character.toString(eod13dig.charAt(3)), dig5 = Character.toString(eod13dig.charAt(4)), dig6 = Character.toString(eod13dig.charAt(5)),
                dig7 = Character.toString(eod13dig.charAt(6)), dig8 = Character.toString(eod13dig.charAt(7)), dig9 = Character.toString(eod13dig.charAt(8)),
                dig10 = Character.toString(eod13dig.charAt(9)), dig11 = Character.toString(eod13dig.charAt(10)), dig12 = Character.toString(eod13dig.charAt(11)),
                dig13 = Character.toString(eod13dig.charAt(12));

        initializeEod13Tables();
        for (HistStageDataEod13digLungDto obj : _DATA_EOD_13DIG_LUNG) {
            historicStage = obj.computeResult(dig4, dig5, dig6, dig7, dig8, dig9, dig10, dig11, dig12, dig13);
            if (historicStage != null)
                break;
        }
        return historicStage;
    }

    protected static String runEod13MelanomaTable(String primarySite, String eod13dig) {
        String historicStage = null;
        String dig5 = Character.toString(eod13dig.charAt(4)), dig6 = Character.toString(eod13dig.charAt(5)), dig9 = Character.toString(eod13dig.charAt(8)),
                dig10 = Character.toString(eod13dig.charAt(9)), dig11 = Character.toString(eod13dig.charAt(10)), dig12 = Character.toString(eod13dig.charAt(11)),
                dig13 = Character.toString(eod13dig.charAt(12));

        initializeEod13Tables();
        for (HistStageDataEod13digMelanomaDto obj : _DATA_EOD_13DIG_MELANOMA) {
            historicStage = obj.computeResult(primarySite, dig5, dig6, dig9, dig10, dig11, dig12, dig13);
            if (historicStage != null)
                break;
        }
        return historicStage;
    }

    protected static String runEod13BladderTable(String eod13dig) {
        String historicStage = null;
        String dig5 = Character.toString(eod13dig.charAt(4)), dig6 = Character.toString(eod13dig.charAt(5)), dig7 = Character.toString(eod13dig.charAt(6)),
                dig9 = Character.toString(eod13dig.charAt(8)), dig10 = Character.toString(eod13dig.charAt(9)), dig11 = Character.toString(eod13dig.charAt(10)),
                dig12 = Character.toString(eod13dig.charAt(11)), dig13 = Character.toString(eod13dig.charAt(12));

        initializeEod13Tables();
        for (HistStageDataEod13digBladderDto obj : _DATA_EOD_13DIG_BLADDER) {
            historicStage = obj.computeResult(dig5, dig6, dig7, dig9, dig10, dig11, dig12, dig13);
            if (historicStage != null)
                break;
        }
        return historicStage;
    }

    protected static String runEod13GeneralStageTable(String primarySite, String eod13dig) {
        String historicStage = null;
        String dig4 = Character.toString(eod13dig.charAt(3)), dig5 = Character.toString(eod13dig.charAt(4)), dig6 = Character.toString(eod13dig.charAt(5)),
                dig7 = Character.toString(eod13dig.charAt(6)), dig8 = Character.toString(eod13dig.charAt(7)), dig9 = Character.toString(eod13dig.charAt(8)),
                dig10 = Character.toString(eod13dig.charAt(9)), dig11 = Character.toString(eod13dig.charAt(10)), dig12 = Character.toString(eod13dig.charAt(11)),
                dig13 = Character.toString(eod13dig.charAt(12));

        initializeEod13Tables();
        //Run EOD 13dig Ext table (inputs: primary site, EOD 13dig Digits 4-8 & 13: Output: EOD 13dig Ext Recode)
        String extRecode = null;
        for (HistStageDataEod13digExtDto obj : _DATA_EOD_13DIG_EXT) {
            extRecode = obj.computeResult(primarySite, dig4, dig5, dig6, dig7, dig8, dig13);
            if (extRecode != null)
                break;
        }
        //If anything is not handled in node table, set Ext Recode to 99
        if (extRecode == null)
            extRecode = "99";
        //Run EOD 13dig Nodes table (inputs: primary site, EOD 13dig Digits 7-12: Output: EOD 13dig Node Recode)
        String nodeRecode = null;
        for (HistStageDataEod13digNodeDto obj : _DATA_EOD_13DIG_NODE) {
            nodeRecode = obj.computeResult(primarySite, dig7, dig8, dig9, dig10, dig11, dig12);
            if (nodeRecode != null)
                break;
        }
        //If anything is not handled in node table, set Node Recode to 99
        if (nodeRecode == null)
            nodeRecode = "99";

        //: Run EOD 13dig General Stage table (Inputs: Primary Site, EOD 13dig Ext Recode, EOD 13dig Node Recode; Output: Historic Stage).
        for (HistStageDataEod13digGeneralStageDto obj : _DATA_EOD_13DIG_GENERAL_STAGE) {
            historicStage = obj.computeResult(primarySite, extRecode, nodeRecode);
            if (historicStage != null)
                break;
        }

        return historicStage;
    }

    protected static String runEod4DigStageTable(String histology3, String primarySite, String eod4Ext, String eod4Node) {
        String historicStage = null;
        initializeEod4DigTable();
        for (HistStageDataEod4digStageDto obj : _DATA_EOD_4DIG_STAGE) {
            historicStage = obj.computeResult(histology3, primarySite, eod4Ext, eod4Node);
            if (historicStage != null)
                break;
        }

        return historicStage;
    }

    protected static String runEod2DigTables(String primarySite, String histology3, String eod2dig) {
        String historicStage = null;
        if (eod2dig != null && eod2dig.length() == 2) {
            String dig1 = Character.toString(eod2dig.charAt(0));
            String dig2 = Character.toString(eod2dig.charAt(1));
            initializeEod2Tables();
            //Run EOD 2dig Direct Stage
            for (HistStageDataEod2digDirectStageDto obj : _DATA_EOD_2DIG_DIRECT_STAGE) {
                historicStage = obj.computeResult(primarySite, histology3, dig1, dig2);
                if (historicStage != null)
                    break;
            }
            //If anything is not handled by Direct Stage table, run EOD 2dig Ext Node Stage table
            if (historicStage == null) {
                String extRecode = null;
                for (HistStageDataEod2digExtDto obj : _DATA_EOD_2DIG_EXT) {
                    extRecode = obj.computeResult(histology3, primarySite, dig1, dig2);
                    if (extRecode != null)
                        break;
                }
                //If anything not handled, set extension recode = 99.
                if (extRecode == null)
                    extRecode = "99";

                String nodeRecode = null;
                //For Histologies 8000-9589, run EOD 2dig Nodes Table
                if (Integer.parseInt(histology3) >= 8000 && Integer.parseInt(histology3) <= 9589) {
                    for (HistStageDataEod2digNodeDto obj : _DATA_EOD_2DIG_NODE) {
                        nodeRecode = obj.computeResult(primarySite, dig1, dig2);
                        if (nodeRecode != null)
                            break;
                    }
                }

                //If anything not handled, set node recode = 99.
                if (nodeRecode == null)
                    nodeRecode = "99";

                for (HistStageDataEod2digExtNodeStageDto obj : _DATA_EOD_2DIG_EXTENSION_NODE_STAGE) {
                    historicStage = obj.computeResult(primarySite, histology3, extRecode, nodeRecode);
                    if (historicStage != null)
                        break;
                }

            }

        }

        //If anything is not handled in above table, historic stage = 9
        if (historicStage == null)
            historicStage = UNSTAGED;

        return historicStage;
    }

    protected static String runEod0StageTable(String primarySite, String histology3, String eod2dig) {
        String historicStage = null;
        if (eod2dig != null && eod2dig.length() == 2) {
            initializeEod0StageTable();
            for (HistStageDataEod0StageDto obj : _DATA_EOD0_STAGE) {
                historicStage = obj.computeResult(primarySite, histology3, Character.toString(eod2dig.charAt(0)), Character.toString(eod2dig.charAt(1)));
                if (historicStage != null)
                    break;
            }
        }

        //If anything is not handled in above table, historic stage = 9
        if (historicStage == null)
            historicStage = UNSTAGED;

        return historicStage;
    }

    protected static String makeCsExtRecodeExceptions(HistoricStageInputDto input, String extRecode, String historicStageSchema) {
        int iSite = Integer.parseInt(formattedSite(input.getPrimarySite()));
        int iHist = Integer.parseInt(input.getHistologyIcdO3());
        int iCsExt = Integer.parseInt(input.getCsExtension());

        //Special case for longevity consistency
        if (iCsExt == 800 && (iHist == 9823 || iHist == 9827) && ((0 <= iSite && iSite <= 419) || (422 <= iSite && iSite <= 423) ||
                (425 <= iSite && iSite <= 809)))
            extRecode = "9";

        //Special case for corpus schemas per Lynn Ries/Jennifer Ruhl starting Nov11 sub
        if ("62".equals(historicStageSchema) && "1".equals(extRecode) && ("010".equals(input.getCsSiteSpecificFactor2()) || "10".equals(input.getCsSiteSpecificFactor2())))
            extRecode = "2";

        //Special Breast Processing
        if ("58".equals(historicStageSchema) && (iCsExt == 50 || iCsExt == 70)) {
            //behavior is INSITU
            if ("2".equals(input.getBehaviorIcdO3()))
                extRecode = "0"; //IS
                //behavior is MALIG
            else if ("3".equals(input.getBehaviorIcdO3()))
                extRecode = "1"; //L
                //Bad Breast Behavior
            else
                extRecode = null;
        }

        //Special Pleura Processing
        if ("49".equals(historicStageSchema)) {

            if (!NumberUtils.isDigits(input.getCsSiteSpecificFactor1()))
                extRecode = null;
            else {
                int iCSSSF1 = Integer.parseInt(input.getCsSiteSpecificFactor1());
                if (100 <= iCsExt && iCsExt <= 305) {
                    if (iCSSSF1 == 0 || iCSSSF1 == 10 || iCSSSF1 == 999)
                        extRecode = "1";
                    else if (iCSSSF1 == 20 || iCSSSF1 == 30)
                        extRecode = "7";
                        //Bad Pleura Extension
                    else
                        extRecode = null;
                }
                else if (420 <= iCsExt && iCsExt <= 650) {
                    if (iCSSSF1 == 0 || iCSSSF1 == 10 || iCSSSF1 == 999)
                        extRecode = "2";
                    else if (iCSSSF1 == 20 || iCSSSF1 == 30)
                        extRecode = "7";
                        //Bad Pleura Extension
                    else
                        extRecode = null;
                }
                else if (690 <= iCsExt && iCsExt <= 850) {
                    if (iCSSSF1 == 0 || iCSSSF1 == 10 || iCSSSF1 == 20 || iCSSSF1 == 30 || iCSSSF1 == 999)
                        extRecode = "7";
                        //Bad Pleura Extension
                    else
                        extRecode = null;
                }
                else if (950 <= iCsExt && iCsExt <= 999) {
                    if (iCSSSF1 == 0 || iCSSSF1 == 10 || iCSSSF1 == 999)
                        extRecode = "9";
                    else if (iCSSSF1 == 20 || iCSSSF1 == 30)
                        extRecode = "7";
                        //Bad Pleura Extension
                    else
                        extRecode = null;
                }
                //Bad Pleura Extension
                else
                    extRecode = null;
            }

        }

        return extRecode;
    }

    protected static String formattedSite(String site) {
        return site.startsWith("C") || site.startsWith("c") ? site.substring(1) : site;
    }


    /* *********************************************************************************************************************
     * ***************************   INITIALIZE LOOKUPS FROM TABLES      *****************************************************
     * *********************************************************************************************************************/

    protected static synchronized void initializeLeukemiaTable() {
        if (_DATA_LEUKEMIA.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Leuk.csv"), "US-ASCII");
                for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll())
                    _DATA_LEUKEMIA.add(new HistStageDataLeukemiaDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeShemaTable() {
        if (_DATA_HISTORIC_STAGE_SCHEMA.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-SchemaFor2004+.csv"), "US-ASCII");
                List<String[]> rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_HISTORIC_STAGE_SCHEMA.add(new HistStageDataSchemaDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeCsTables() {
        if (_DATA_CS_EXT.isEmpty() || _DATA_CS_NODE.isEmpty() || _DATA_CS_METS.isEmpty() || _DATA_CS_STAGE.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-csExt.csv"), "US-ASCII");
                List<String[]> rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_CS_EXT.add(new HistStageDataCsExtDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-csNode.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_CS_NODE.add(new HistStageDataCsNodeDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-csMets.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_CS_METS.add(new HistStageDataCsMetsDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-csStage.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_CS_STAGE.add(new HistStageDataCsStageDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeEod10Tables() {
        if (_DATA_EOD10_EXT.isEmpty() || _DATA_EOD10_NODE.isEmpty() || _DATA_EOD10_STAGE.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod10Ext.csv"), "US-ASCII");
                List<String[]> rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD10_EXT.add(new HistStageDataEod10ExtDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod10Node.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD10_NODE.add(new HistStageDataEod10NodeDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod10Stage.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD10_STAGE.add(new HistStageDataEod10StageDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeEodPatchTable() {
        if (_DATA_EOD_PATCH.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-EodPatch.csv"), "US-ASCII");
                for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll())
                    _DATA_EOD_PATCH.add(new HistStageDataEodPatchDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeEod4DigTable() {
        if (_DATA_EOD_4DIG_STAGE.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod4digStage.csv"), "US-ASCII");
                for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll())
                    _DATA_EOD_4DIG_STAGE.add(new HistStageDataEod4digStageDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeEod13Tables() {
        if (_DATA_EOD_13DIG_EXT.isEmpty() || _DATA_EOD_13DIG_NODE.isEmpty() || _DATA_EOD_13DIG_BLADDER.isEmpty() ||
                _DATA_EOD_13DIG_LUNG.isEmpty() || _DATA_EOD_13DIG_MELANOMA.isEmpty() || _DATA_EOD_13DIG_GENERAL_STAGE.isEmpty()) {

            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod13digNodes.csv"), "US-ASCII");
                List<String[]> rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_13DIG_NODE.add(new HistStageDataEod13digNodeDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod13digExt.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_13DIG_EXT.add(new HistStageDataEod13digExtDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod13digLung.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_13DIG_LUNG.add(new HistStageDataEod13digLungDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod13digMelanoma.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_13DIG_MELANOMA.add(new HistStageDataEod13digMelanomaDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod13digBladder.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_13DIG_BLADDER.add(new HistStageDataEod13digBladderDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod13digGeneralStage.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_13DIG_GENERAL_STAGE.add(new HistStageDataEod13digGeneralStageDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeEod2Tables() {
        if (_DATA_EOD_2DIG_EXT.isEmpty() || _DATA_EOD_2DIG_NODE.isEmpty() || _DATA_EOD_2DIG_DIRECT_STAGE.isEmpty() || _DATA_EOD_2DIG_EXTENSION_NODE_STAGE.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod2digExt.csv"), "US-ASCII");
                List<String[]> rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_2DIG_EXT.add(new HistStageDataEod2digExtDto(row));

                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod2digNode.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_2DIG_NODE.add(new HistStageDataEod2digNodeDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod2digDirectStage.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_2DIG_DIRECT_STAGE.add(new HistStageDataEod2digDirectStageDto(row));
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod2digExtNodeStage.csv"), "US-ASCII");
                rows = new CSVReaderBuilder(reader).withSkipLines(1).build().readAll();
                for (String[] row : rows)
                    _DATA_EOD_2DIG_EXTENSION_NODE_STAGE.add(new HistStageDataEod2digExtNodeStageDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static synchronized void initializeEod0StageTable() {
        if (_DATA_EOD0_STAGE.isEmpty()) {
            try {
                Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("historicstage/Historic-stage-Eod0Stage.csv"), "US-ASCII");
                for (String[] row : new CSVReaderBuilder(reader).withSkipLines(1).build().readAll())
                    _DATA_EOD0_STAGE.add(new HistStageDataEod0StageDto(row));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
