/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.behavrecode.BehaviorRecodeUtils;
import com.imsweb.algorithms.causespecific.CauseSpecificInputDto;
import com.imsweb.algorithms.causespecific.CauseSpecificResultDto;
import com.imsweb.algorithms.causespecific.CauseSpecificUtils;
import com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorInputDto;
import com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorOutputDto;
import com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorUtils;
import com.imsweb.algorithms.countyatdiagnosisanalysis.CountyAtDxAnalysisInputDto;
import com.imsweb.algorithms.countyatdiagnosisanalysis.CountyAtDxAnalysisOutputDto;
import com.imsweb.algorithms.countyatdiagnosisanalysis.CountyAtDxAnalysisUtils;
import com.imsweb.algorithms.iarc.IarcMpInputRecordDto;
import com.imsweb.algorithms.iarc.IarcUtils;
import com.imsweb.algorithms.iccc.IcccRecodeUtils;
import com.imsweb.algorithms.internal.Utils;
import com.imsweb.algorithms.napiia.NapiiaInputPatientDto;
import com.imsweb.algorithms.napiia.NapiiaInputRecordDto;
import com.imsweb.algorithms.napiia.NapiiaResultsDto;
import com.imsweb.algorithms.napiia.NapiiaUtils;
import com.imsweb.algorithms.nhia.NhiaInputPatientDto;
import com.imsweb.algorithms.nhia.NhiaInputRecordDto;
import com.imsweb.algorithms.nhia.NhiaResultsDto;
import com.imsweb.algorithms.nhia.NhiaUtils;
import com.imsweb.algorithms.prcdauiho.PrcdaUihoInputDto;
import com.imsweb.algorithms.prcdauiho.PrcdaUihoOutputDto;
import com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils;
import com.imsweb.algorithms.ruralurban.RuralUrbanInputDto;
import com.imsweb.algorithms.ruralurban.RuralUrbanOutputDto;
import com.imsweb.algorithms.ruralurban.RuralUrbanUtils;
import com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils;
import com.imsweb.algorithms.survival.SurvivalTimeInputPatientDto;
import com.imsweb.algorithms.survival.SurvivalTimeInputRecordDto;
import com.imsweb.algorithms.survival.SurvivalTimeOutputPatientDto;
import com.imsweb.algorithms.survival.SurvivalTimeOutputRecordDto;
import com.imsweb.algorithms.survival.SurvivalTimeUtils;

import static com.imsweb.algorithms.AlgorithmField.DATA_LEVEL_PATIENT;
import static com.imsweb.algorithms.AlgorithmField.DATA_LEVEL_TUMOR;
import static com.imsweb.algorithms.iccc.IcccRecodeUtils.VERSION_WHO_2008;
import static com.imsweb.algorithms.iccc.IcccRecodeUtils.VERSION_WHO_2008_INFO;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_ALL_CASES;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE;
import static com.imsweb.algorithms.nhia.NhiaUtils.NHIA_OPTION_SEVEN_ONLY;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.PRCDA_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_FACILITY_UNKNOWN;
import static com.imsweb.algorithms.prcdauiho.PrcdaUihoUtils.UIHO_UNKNOWN;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_96;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_97;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_98;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.CONTINUUM_UNK_99;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RUCA_VAL_UNK_A;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.RUCA_VAL_UNK_D;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URIC_VAL_UNK_A;
import static com.imsweb.algorithms.ruralurban.RuralUrbanUtils.URIC_VAL_UNK_D;
import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2010;
import static com.imsweb.algorithms.seersiterecode.SeerSiteRecodeUtils.VERSION_2010_INFO;

/**
 * Instructions for adding a new algorithm:
 * - add the constant for the algorithm ID (make sure to follow the naming convention, they all start with ALG_).
 * - add constants for any field that doesn't have a constant yet (there are two lists, standard fields, and non-standard fields).
 * - add the new fields (standard and non-standard) to the static fields cache (see _CACHED_FIELDS).
 * - if the new algorithm needs it, add constants for its options
 * - add a static method at the end of the class "createXxx" that returns an Algorithm, see how all the other ones are done.
 * - register the new algorithm (see initialize() method).
 */
public class Algorithms {

    // algorithm IDs
    public static final String ALG_NHIA = "nhia";
    public static final String ALG_NAPIIA = "napiia";
    public static final String ALG_DEATH_CLASSIFICATION = "death-classification";
    public static final String ALG_CENSUS_POVERTY = "census-poverty";
    public static final String ALG_URIC = "uric";
    public static final String ALG_RUCA = "ruca";
    public static final String ALG_URBAN_CONTINUUM = "urban-continuum";
    public static final String ALG_SURVIVAL_TIME = "survival-time";
    public static final String ALG_SEER_SITE_RECODE = "seer-site-recode";
    public static final String ALG_SEER_BEHAVIOR_RECODE = "seer-behavior-recode";
    public static final String ALG_ICCC = "iccc";
    public static final String ALG_IARC = "iarc-multiple-primary";
    public static final String ALG_COUNTY_AT_DIAGNOSIS_ANALYSIS = "county-at-diagnosis-analysis";
    public static final String ALG_PRCDA_UIHO = "prcda-uiho";

    // special properties
    public static final String FIELD_TUMORS = "tumors";

    // standard fields
    public static final String FIELD_PAT_ID_NUMBER = "patientIdNumber";
    public static final String FIELD_SPAN_HISP_OR = "spanishHispanicOrigin";
    public static final String FIELD_NAME_LAST = "nameLast";
    public static final String FIELD_NAME_FIRST = "nameFirst";
    public static final String FIELD_NAME_MAIDEN = "nameMaiden";
    public static final String FIELD_COUNTRY_BIRTH = "birthplaceCountry";
    public static final String FIELD_DATE_OF_BIRTH = "dateOfBirth";
    public static final String FIELD_RACE1 = "race1";
    public static final String FIELD_RACE2 = "race2";
    public static final String FIELD_RACE3 = "race3";
    public static final String FIELD_RACE4 = "race4";
    public static final String FIELD_RACE5 = "race5";
    public static final String FIELD_SEX = "sex";
    public static final String FIELD_IHS = "ihsLink";
    public static final String FIELD_COUNTY_DX = "countyAtDx";
    public static final String FIELD_STATE_DX = "addrAtDxState";
    public static final String FIELD_NHIA = "nhiaDerivedHispOrigin";
    public static final String FIELD_NAPIIA = "raceNapiia";
    public static final String FIELD_DOLC = "dateOfLastContact";
    public static final String FIELD_VS = "vitalStatus";
    public static final String FIELD_TYPE_RPT_SRC = "typeOfReportingSource";
    public static final String FIELD_COD = "causeOfDeath";
    public static final String FIELD_SEER_COD_CLASS = "seerCauseSpecificCod";
    public static final String FIELD_SEER_COD_OTHER = "seerOtherCod";
    public static final String FIELD_ICD_REV_NUM = "icdRevisionNumber";
    public static final String FIELD_SEQ_NUM_CTRL = "sequenceNumberCentral";
    public static final String FIELD_PRIMARY_SITE = "primarySite";
    public static final String FIELD_HIST_O3 = "histologicTypeIcdO3";
    public static final String FIELD_BEHAV_O3 = "behaviorCodeIcdO3";
    public static final String FIELD_DX_DATE = "dateOfDiagnosis";
    public static final String FIELD_CENSUS_2000 = "censusTract2000";
    public static final String FIELD_CENSUS_2010 = "censusTract2010";
    public static final String FIELD_CENSUS_POVERTY_INDICTR = "censusTrPovertyIndictr";
    public static final String FIELD_URIC_2000 = "uric2000";
    public static final String FIELD_URIC_2010 = "uric2010";
    public static final String FIELD_RUCA_2000 = "ruca2000";
    public static final String FIELD_RUCA_2010 = "ruca2010";
    public static final String FIELD_RURAL_CONT_1993 = "ruralurbanContinuum1993";
    public static final String FIELD_RURAL_CONT_2003 = "ruralurbanContinuum2003";
    public static final String FIELD_RURAL_CONT_2013 = "ruralurbanContinuum2013";
    public static final String FIELD_SURV_VS_RECODE = "vitalStatusRecode";
    public static final String FIELD_SURV_DX_DATE_RECODE = "survDateDxRecode";
    public static final String FIELD_SURV_DATE_ACTIVE_FUP = "survDateActiveFollowup";
    public static final String FIELD_SURV_DATE_PRESUMED_ALIVE = "survDatePresumedAlive";
    public static final String FIELD_SURV_MONTH_ACTIVE_FUP = "survMosActiveFollowup";
    public static final String FIELD_SURV_FLAG_ACTIVE_FUP = "survFlagActiveFollowup";
    public static final String FIELD_SURV_MONTH_PRESUMED_ALIVE = "survMosPresumedAlive";
    public static final String FIELD_SURV_FLAG_PRESUMED_ALIVE = "survFlagPresumedAlive";
    public static final String FIELD_SURV_REC_NUM_RECODE = "recordNumberRecode";
    public static final String FIELD_COUNTY_AT_DX_GEOCODE_1990 = "countyAtDxGeocode1990";
    public static final String FIELD_COUNTY_AT_DX_GEOCODE_2000 = "countyAtDxGeocode2000";
    public static final String FIELD_COUNTY_AT_DX_GEOCODE_2010 = "countyAtDxGeocode2010";
    public static final String FIELD_COUNTY_AT_DX_GEOCODE_2020 = "countyAtDxGeocode2020";
    public static final String FIELD_STATE_AT_DX_GEOCODE_19708090 = "stateAtDxGeocode19708090";
    public static final String FIELD_STATE_AT_DX_GEOCODE_2000 = "stateAtDxGeocode2000";
    public static final String FIELD_STATE_AT_DX_GEOCODE_2010 = "stateAtDxGeocode2010";
    public static final String FIELD_STATE_AT_DX_GEOCODE_2020 = "stateAtDxGeocode2020";
    public static final String FIELD_CENSUS_CERTAINTY_708090 = "censusTrCert19708090";
    public static final String FIELD_CENSUS_CERTAINTY_2000 = "censusTrCertainty2000";
    public static final String FIELD_CENSUS_CERTAINTY_2010 = "censusTrCertainty2010";
    public static final String FIELD_CENSUS_CERTAINTY_2020 = "censusTractCertainty2020"; // really, not the same convention as the 3 other fields??? Someone should get fired!
    public static final String FIELD_COUNTY_AT_DX_ANALYSIS = "countyAtDxAnalysis";

    // non-standard fields
    public static final String FIELD_NAPIIA_NEEDS_REVIEW = "napiiaNeedsHumanReview";
    public static final String FIELD_NAPIIA_REVIEW_REASON = "napiiaReasonForReview";
    public static final String FIELD_SEER_SITE_RECODE = "seerSiteRecode";
    public static final String FIELD_SEER_BEHAV_RECODE = "seerBehaviorRecode";
    public static final String FIELD_ICCC = "iccc";
    public static final String FIELD_ICCC_MAJOR_CATEGORY = "icccMajorCategory";
    public static final String FIELD_IARC_MP_INDICATOR = "iarcMpIndicator";
    public static final String FIELD_IARC_MP_SITE_GROUP = "iarcMpSiteGroup";
    public static final String FIELD_IARC_MP_HIST_GROUP = "iarcMpHistGroup";
    public static final String FIELD_IARC_MP_HISTOLOGY = "iarcMpHistologicTypeIcdO3";
    public static final String FIELD_COUNTY_AT_DX_ANALYSIS_FLAG = "countyAtDxAnalysisFlag";
    public static final String FIELD_PRCDA_COUNTY_2017 = "prcdaCounty2017";
    public static final String FIELD_UIHO_COUNTY_2017 = "uihoCounty2017";
    public static final String FIELD_UIHO_FACILITY_2017 = "uihoFacility2017";

    // options
    public static final String PARAM_NHIA_OPTION = "nhiaOption";
    public static final String PARAM_SEER_COD_CLASS_CUTOFF_YEAR = "seerCodClassCutoffYear";
    public static final String PARAM_CENSUS_POVERTY_INC_RECENT_YEARS = "censusPovertyIncludeRecentYears";
    public static final String PARAM_SURV_CUTOFF_YEAR = "survivalCutoffYear";

    // cached fields
    private static Map<String, AlgorithmField> _CACHED_FIELDS = new HashMap<>();

    static {
        // standard fields
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_PAT_ID_NUMBER, 20, 8, "Patient ID Number", "Pat ID #", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAME_LAST, 2230, 50, "Name--Last", "Last", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAME_FIRST, 2240, 50, "Name--First", "First", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAME_MAIDEN, 2390, 50, "Name--Maiden", "Maiden", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTRY_BIRTH, 254, 3, "Birthplace--Country", "Birth Country", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_DATE_OF_BIRTH, 240, 8, "Date of Birth", "Birth Dt", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SEX, 220, 1, "Sex", "Sex", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE1, 160, 2, "Race 1", "Race 1", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE2, 161, 2, "Race 2", "Race 2", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE3, 162, 2, "Race 3", "Race 3", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE4, 163, 2, "Race 4", "Race 4", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RACE5, 164, 2, "Race 5", "Race 5", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SPAN_HISP_OR, 190, 1, "Spanish/Hispanic Origin", "Hisp Orig", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_IHS, 192, 1, "IHS Link", "IHS", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NHIA, 191, 1, "NHIA Derived Hisp Origin", "NHIA", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAPIIA, 193, 2, "Race--NAPIIA(derived API)", "NAPIIA", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_DX, 90, 3, "County at DX Reported", "DX Cty Code", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_STATE_DX, 80, 2, "Addr at DX--State", "DX State", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_DOLC, 1750, 8, "Date of Last Contact", "DOLC", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_VS, 1760, 1, "Vital Status", "VS", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_TYPE_RPT_SRC, 500, 1, "Type of Reporting Source", "Rpt Src", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COD, 1910, 4, "Cause of Death", "COD", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SEER_COD_CLASS, 1914, 1, "SEER Cause Specific COD", "COD Spec", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SEER_COD_OTHER, 1915, 1, "SEER Other COD", "COD Other", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_ICD_REV_NUM, 1920, 1, "ICD Revision Number", "COD Rev#", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SEQ_NUM_CTRL, 380, 2, "Sequence Number--Central", "Ctrl Seq#", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_PRIMARY_SITE, 400, 4, "Primary Site", "Site", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_HIST_O3, 522, 4, "Histologic Type ICD-O-3", "Hist(O3)", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_BEHAV_O3, 523, 1, "Behavior Code ICD-O-3", "Behav(O3)", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_DX_DATE, 390, 8, "Date of Diagnosis", "DX Dt", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_CENSUS_2000, 130, 6, "Census Tract 2000", "Cens 2000", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_CENSUS_2010, 135, 6, "Census Tract 2010", "Cens 2010", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_CENSUS_POVERTY_INDICTR, 145, 1, "Census Tr Poverty Indictr", "Cens Pov", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_URIC_2000, 345, 1, "URIC 2000", "URIC 2000", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_URIC_2010, 346, 1, "URIC 2010", "URIC 2010", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RUCA_2000, 339, 1, "RUCA 2000", "RUCA 2000", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RUCA_2010, 341, 1, "RUCA 2010", "RUCA 2010", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RURAL_CONT_1993, 3300, 2, "RuralUrban Continuum 1993", "Rur Urb Cont 93", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RURAL_CONT_2003, 3310, 2, "RuralUrban Continuum 2003", "Rur Urb Cont 03", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_RURAL_CONT_2013, 3312, 2, "RuralUrban Continuum 2013", "Rur Urb Cont 13", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_VS_RECODE, 1762, 1, "Vital Status Recode", "VS Rec", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_DX_DATE_RECODE, 1788, 8, "Surv-Date DX Recode", "Surv DX", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_DATE_ACTIVE_FUP, 1782, 8, "Surv-Date Active Followup", "Surv Dt", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_DATE_PRESUMED_ALIVE, 1785, 8, "Surv-Date Presumed Alive", "Surv Dt PA", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_MONTH_ACTIVE_FUP, 1784, 4, "Surv-Mos Active Followup", "Surv", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_FLAG_ACTIVE_FUP, 1783, 1, "Surv-Flag Active Followup", "Surv Fg", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_MONTH_PRESUMED_ALIVE, 1787, 4, "Surv-Mos Presumed Alive", "Surv PA", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_FLAG_PRESUMED_ALIVE, 1786, 1, "Surv-Flag Presumed Alive", "Surv Fg PA", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SURV_REC_NUM_RECODE, 1775, 2, "Record Number Recode", "Rec Num", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_1990, 94, 3, "County at DX Geocode 1970/80/90", "Cty DX Geo708090", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_2000, 95, 3, "County at DX Geocode2000", "Cty DX Geo00", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_2010, 96, 3, "County at DX Geocode2010", "Cty DX Geo10", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_2020, 97, 3, "County at DX Geocode2020", "Cty DX Geo20", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_19708090, 81, 2, "State at DX Geocode 1970/80/90", "State DX Geo708090", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_2000, 82, 2, "State at DX Geocode 2000", "State Dx Geo00", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_2010, 83, 2, "State at DX Geocode 2010", "State Dx Geo10", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_2020, 84, 2, "State at DX Geocode 2020", "State Dx Geo20", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_CENSUS_CERTAINTY_708090, 364, 1, "Census Tr Cert 1970/80/90", "Cens Cert 70/80/90", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_CENSUS_CERTAINTY_2000, 365, 1, "Census Tr Certainty 2000", "Cens Cert 2000", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_CENSUS_CERTAINTY_2010, 367, 1, "Census Tr Certainty 2010", "Cens Cert 2010", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_CENSUS_CERTAINTY_2020, 369, 1, "Census Tract Certainty 2020", "Cens Cert 2020", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_AT_DX_ANALYSIS, 89, 3, "County at DX Analysis", "DX Anlys Cty", DATA_LEVEL_TUMOR));

        // non-standard fields
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAPIIA_NEEDS_REVIEW, null, 1, "NAPIIA Needs Review", "NAPIIA Rev", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_NAPIIA_REVIEW_REASON, null, 256, "NAPIIA Review Reason", "NAPIIA Rev Res", DATA_LEVEL_PATIENT));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SEER_SITE_RECODE, null, 5, "SEER Site Recode ICD-O-3", "Site Recode", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_SEER_BEHAV_RECODE, null, 1, "SEER Site Behavior Recode ICD-O-3", "SEER Behav", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_ICCC, null, 3, "International Classification of Childhood Cancer (ICCC)", "ICCC", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_ICCC_MAJOR_CATEGORY, null, 2, "ICCC Major Category", "ICCC Cat", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_IARC_MP_INDICATOR, null, 1, "IARC Multiple Primary Indicator", "IARC MP", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_IARC_MP_SITE_GROUP, null, 4, "IARC Multiple Primary Site Group", "IARC MP Site Gr", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_IARC_MP_HIST_GROUP, null, 2, "IARC Multiple Primary Histology Group", "IARC MP Hist Grp", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_IARC_MP_HISTOLOGY, null, 4, "IARC Multiple Primary Histology", "IARC MP Hist", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_COUNTY_AT_DX_ANALYSIS_FLAG, null, 4, "County at DX Analysis Flag", "DX Anlys Cty Fg", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_PRCDA_COUNTY_2017, null, 1, "PRCDA County 2017", "PRCDA Cty 17", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_UIHO_COUNTY_2017, null, 1, "UIHO County 2017", "UIHO Cty 17", DATA_LEVEL_TUMOR));
        addField(_CACHED_FIELDS, AlgorithmField.of(FIELD_UIHO_FACILITY_2017, null, 2, "UIHO Facility 2017", "UIHO Fac 17", DATA_LEVEL_TUMOR));
    }

    private static void addField(Map<String, AlgorithmField> cache, AlgorithmField field) {
        cache.put(field.getId(), field);
    }

    private static Map<String, Algorithm> _CACHED_ALGORITHMS = new HashMap<>();

    private static ReentrantReadWriteLock _LOCK = new ReentrantReadWriteLock();

    public static void initialize() {
        _LOCK.writeLock().lock();
        try {
            addAlgorithm(_CACHED_ALGORITHMS, createNhia());
            addAlgorithm(_CACHED_ALGORITHMS, createNapiia());
            addAlgorithm(_CACHED_ALGORITHMS, createDeathClassification());
            addAlgorithm(_CACHED_ALGORITHMS, createCensusTractPoverty());
            addAlgorithm(_CACHED_ALGORITHMS, createSurvivalTime());
            addAlgorithm(_CACHED_ALGORITHMS, createUric());
            addAlgorithm(_CACHED_ALGORITHMS, createRuca());
            addAlgorithm(_CACHED_ALGORITHMS, createUrbanContinuum());
            addAlgorithm(_CACHED_ALGORITHMS, createSeerSiteRecode());
            addAlgorithm(_CACHED_ALGORITHMS, createSeerBehaviorRecode());
            addAlgorithm(_CACHED_ALGORITHMS, createIccc());
            addAlgorithm(_CACHED_ALGORITHMS, createIarc());
            addAlgorithm(_CACHED_ALGORITHMS, createCountyAtDiagnosisAnalysis());
            addAlgorithm(_CACHED_ALGORITHMS, createPrcdaUiho());
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    private static void addAlgorithm(Map<String, Algorithm> cache, Algorithm algorithm) {
        if (algorithm.getId() == null)
            throw new RuntimeException("Algorithm ID is required!");
        if (cache.containsKey(algorithm.getId()))
            throw new RuntimeException("Algorithm ID '" + algorithm.getId() + "' has already been registered!");

        cache.put(algorithm.getId(), algorithm);
    }

    public static void registerAlgorithm(Algorithm algorithm) {
        if (algorithm.getId() == null)
            throw new RuntimeException("Algorithm ID is required!");

        _LOCK.writeLock().lock();
        try {
            _CACHED_ALGORITHMS.put(algorithm.getId(), algorithm);
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    public static void unregisterAlgorithm(Algorithm algorithm) {
        if (algorithm.getId() == null)
            throw new RuntimeException("Algorithm ID is required!");

        _LOCK.writeLock().lock();
        try {
            _CACHED_ALGORITHMS.remove(algorithm.getId());
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    public static List<Algorithm> getAlgorithms() {
        _LOCK.readLock().lock();
        try {
            return new ArrayList<>(_CACHED_ALGORITHMS.values());
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    public static Algorithm getAlgorithm(String algorithmId) {
        _LOCK.readLock().lock();
        try {
            if (_CACHED_ALGORITHMS.isEmpty())
                throw new RuntimeException("Algorithms have not been initialized!");
            Algorithm algorithm = _CACHED_ALGORITHMS.get(algorithmId);
            if (algorithm == null)
                throw new RuntimeException("Unable to get algorithm " + algorithmId);
            return algorithm;
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    public static Collection<AlgorithmField> getAllFields() {
        return Collections.unmodifiableCollection(_CACHED_FIELDS.values());
    }

    private static Algorithm createNhia() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_NHIA;
            }

            @Override
            public String getName() {
                return NhiaUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return NhiaUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return NhiaUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "https://www.naaccr.org/analysis-and-data-improvement-tools/#NHAPIIA";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                List<AlgorithmParam<?>> params = new ArrayList<>();
                params.add(AlgorithmParam.of(PARAM_NHIA_OPTION, "NHIA Option", String.class, Arrays.asList(NHIA_OPTION_ALL_CASES, NHIA_OPTION_SEVEN_AND_NINE, NHIA_OPTION_SEVEN_ONLY)));
                return params;
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SPAN_HISP_OR));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_LAST));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_MAIDEN));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTRY_BIRTH));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE1));
                fields.add(_CACHED_FIELDS.get(FIELD_SEX));
                fields.add(_CACHED_FIELDS.get(FIELD_IHS));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_NHIA));
                return fields;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {
                NhiaInputPatientDto inputPatient = new NhiaInputPatientDto();
                inputPatient.setNhiaInputPatientDtoList(new ArrayList<>());
                Map<String, Object> patientMap = Utils.extractPatient(input);
                for (Map<String, Object> tumorMap : Utils.extractTumors(patientMap, true)) {
                    NhiaInputRecordDto dto = new NhiaInputRecordDto();
                    dto.setSpanishHispanicOrigin((String)patientMap.get(FIELD_SPAN_HISP_OR));
                    dto.setBirthplaceCountry((String)patientMap.get(FIELD_COUNTRY_BIRTH));
                    dto.setSex((String)patientMap.get(FIELD_SEX));
                    dto.setRace1((String)patientMap.get(FIELD_RACE1));
                    dto.setIhs((String)patientMap.get(FIELD_IHS));
                    dto.setNameLast((String)patientMap.get(FIELD_NAME_LAST));
                    dto.setNameMaiden((String)patientMap.get(FIELD_NAME_MAIDEN));
                    dto.setCountyAtDxAnalysis((String)tumorMap.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                    dto.setStateAtDx((String)tumorMap.get(FIELD_STATE_DX));
                    inputPatient.getNhiaInputPatientDtoList().add(dto);
                }

                NhiaResultsDto result = NhiaUtils.computeNhia(inputPatient, (String)input.getParameter(PARAM_NHIA_OPTION));

                Map<String, Object> outputPatient = new HashMap<>();
                outputPatient.put(FIELD_NHIA, result.getNhia());

                return AlgorithmOutput.of(outputPatient);
            }
        };
    }

    private static Algorithm createNapiia() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_NAPIIA;
            }

            @Override
            public String getName() {
                return NapiiaUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return NapiiaUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return NapiiaUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "https://www.naaccr.org/analysis-and-data-improvement-tools/#NHAPIIA";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SPAN_HISP_OR));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_LAST));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_MAIDEN));
                fields.add(_CACHED_FIELDS.get(FIELD_NAME_FIRST));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTRY_BIRTH));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE1));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE2));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE3));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE4));
                fields.add(_CACHED_FIELDS.get(FIELD_RACE5));
                fields.add(_CACHED_FIELDS.get(FIELD_SEX));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_NAPIIA));
                fields.add(_CACHED_FIELDS.get(FIELD_NAPIIA_NEEDS_REVIEW));
                fields.add(_CACHED_FIELDS.get(FIELD_NAPIIA_REVIEW_REASON));
                return fields;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {
                NapiiaInputPatientDto inputDto = new NapiiaInputPatientDto();
                inputDto.setNapiiaInputPatientDtoList(new ArrayList<>());

                Map<String, Object> patientMap = Utils.extractPatient(input);
                for (Map<String, Object> ignored : Utils.extractTumors(patientMap, true)) {
                    NapiiaInputRecordDto dto = new NapiiaInputRecordDto();
                    dto.setSpanishHispanicOrigin((String)patientMap.get(FIELD_SPAN_HISP_OR));
                    dto.setBirthplaceCountry((String)patientMap.get(FIELD_COUNTRY_BIRTH));
                    dto.setSex((String)patientMap.get(FIELD_SEX));
                    dto.setRace1((String)patientMap.get(FIELD_RACE1));
                    dto.setRace2((String)patientMap.get(FIELD_RACE2));
                    dto.setRace3((String)patientMap.get(FIELD_RACE3));
                    dto.setRace4((String)patientMap.get(FIELD_RACE4));
                    dto.setRace5((String)patientMap.get(FIELD_RACE5));
                    dto.setNameLast((String)patientMap.get(FIELD_NAME_LAST));
                    dto.setNameMaiden((String)patientMap.get(FIELD_NAME_MAIDEN));
                    dto.setNameFirst((String)patientMap.get(FIELD_NAME_FIRST));
                    inputDto.getNapiiaInputPatientDtoList().add(dto);
                }

                NapiiaResultsDto result = NapiiaUtils.computeNapiia(inputDto);

                Map<String, Object> outputPatient = new HashMap<>();
                outputPatient.put(FIELD_NAPIIA, result.getNapiiaValue());
                outputPatient.put(FIELD_NAPIIA_NEEDS_REVIEW, Boolean.TRUE.equals(result.getNeedsHumanReview()) ? "1" : "0");
                outputPatient.put(FIELD_NAPIIA_REVIEW_REASON, result.getReasonForReview());

                return AlgorithmOutput.of(outputPatient);
            }
        };
    }

    private static Algorithm createDeathClassification() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_DEATH_CLASSIFICATION;
            }

            @Override
            public String getName() {
                return "SEER Cause-specific Death Classification";
            }

            @Override
            public String getVersion() {
                return "N/A";
            }

            @Override
            public String getInfo() {
                return getName();
            }

            @Override
            public String getDocumentationUrl() {
                return "http://seer.cancer.gov/causespecific/";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                List<AlgorithmParam<?>> params = new ArrayList<>();
                params.add(AlgorithmParam.of(PARAM_SEER_COD_CLASS_CUTOFF_YEAR, "Cutoff Year", Integer.class));
                return params;
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_PRIMARY_SITE));
                fields.add(_CACHED_FIELDS.get(FIELD_HIST_O3));
                fields.add(_CACHED_FIELDS.get(FIELD_SEQ_NUM_CTRL));
                fields.add(_CACHED_FIELDS.get(FIELD_ICD_REV_NUM));
                fields.add(_CACHED_FIELDS.get(FIELD_COD));
                fields.add(_CACHED_FIELDS.get(FIELD_DOLC));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SEER_COD_CLASS));
                fields.add(_CACHED_FIELDS.get(FIELD_SEER_COD_OTHER));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                Map<String, List<String>> result = new HashMap<>();
                result.put(FIELD_SEER_COD_CLASS, Arrays.asList(CauseSpecificUtils.MISSING_UNKNOWN_DEATH_OF_CODE, CauseSpecificUtils.NA_NOT_FIRST_TUMOR));
                result.put(FIELD_SEER_COD_OTHER, Arrays.asList(CauseSpecificUtils.MISSING_UNKNOWN_DEATH_OF_CODE, CauseSpecificUtils.NA_NOT_FIRST_TUMOR));
                return result;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {
                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                Integer cutoffYear = (Integer)input.getParameter(PARAM_SEER_COD_CLASS_CUTOFF_YEAR);
                if (cutoffYear == null)
                    cutoffYear = Calendar.getInstance().get(Calendar.YEAR);

                Map<String, Object> inputPatient = Utils.extractPatient(input);
                for (Map<String, Object> inputTumor : Utils.extractTumors(inputPatient)) {
                    CauseSpecificInputDto inputDto = new CauseSpecificInputDto();
                    inputDto.setPrimarySite((String)inputTumor.get(FIELD_PRIMARY_SITE));
                    inputDto.setHistologyIcdO3((String)inputTumor.get(FIELD_HIST_O3));
                    inputDto.setSequenceNumberCentral((String)inputTumor.get(FIELD_SEQ_NUM_CTRL));
                    inputDto.setIcdRevisionNumber((String)inputPatient.get(FIELD_ICD_REV_NUM));
                    inputDto.setCauseOfDeath((String)inputPatient.get(FIELD_COD));
                    inputDto.setDateOfLastContactYear(Utils.extractYear((String)inputPatient.get(FIELD_DOLC)));

                    CauseSpecificResultDto resultDto = CauseSpecificUtils.computeCauseSpecific(inputDto, cutoffYear);

                    Map<String, Object> outputTumor = new HashMap<>();
                    outputTumor.put(FIELD_SEER_COD_CLASS, resultDto.getCauseSpecificDeathClassification());
                    outputTumor.put(FIELD_SEER_COD_OTHER, resultDto.getCauseOtherDeathClassification());
                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(outputPatient);
            }
        };
    }

    private static Algorithm createCensusTractPoverty() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_CENSUS_POVERTY;
            }

            @Override
            public String getName() {
                return CensusTractPovertyIndicatorUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return CensusTractPovertyIndicatorUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return CensusTractPovertyIndicatorUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "https://www.naaccr.org/analysis-and-data-improvement-tools/#POVERTY";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                List<AlgorithmParam<?>> params = new ArrayList<>();
                params.add(AlgorithmParam.of(PARAM_CENSUS_POVERTY_INC_RECENT_YEARS, "Include Recent Years", Boolean.class));
                return params;
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                fields.add(_CACHED_FIELDS.get(FIELD_DX_DATE));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_2000));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_2010));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_POVERTY_INDICTR));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                return Collections.singletonMap(FIELD_CENSUS_POVERTY_INDICTR, Collections.singletonList(CensusTractPovertyIndicatorUtils.POVERTY_INDICATOR_UNKNOWN));
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Boolean includeRecentYears = (Boolean)input.getParameter(PARAM_CENSUS_POVERTY_INC_RECENT_YEARS);
                if (includeRecentYears == null)
                    includeRecentYears = Boolean.TRUE;

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    CensusTractPovertyIndicatorInputDto inputDto = new CensusTractPovertyIndicatorInputDto();
                    inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
                    inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                    inputDto.setDateOfDiagnosisYear(Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE)));
                    inputDto.setCensusTract2000((String)inputTumor.get(FIELD_CENSUS_2000));
                    inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));

                    CensusTractPovertyIndicatorOutputDto outputDto = CensusTractPovertyIndicatorUtils.computePovertyIndicator(inputDto, includeRecentYears);
                    outputTumors.add(Collections.singletonMap(FIELD_CENSUS_POVERTY_INDICTR, outputDto.getCensusTractPovertyIndicator()));
                }

                return AlgorithmOutput.of(outputPatient);

            }
        };
    }

    private static Algorithm createSurvivalTime() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_SURVIVAL_TIME;
            }

            @Override
            public String getName() {
                return SurvivalTimeUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return SurvivalTimeUtils.VERSION;
            }

            @Override
            public String getInfo() {
                return SurvivalTimeUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "http://seer.cancer.gov/survivaltime/";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                List<AlgorithmParam<?>> params = new ArrayList<>();
                params.add(AlgorithmParam.of(PARAM_SURV_CUTOFF_YEAR, "Cutoff Year", Integer.class));
                return params;
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_PAT_ID_NUMBER));
                fields.add(_CACHED_FIELDS.get(FIELD_DATE_OF_BIRTH));
                fields.add(_CACHED_FIELDS.get(FIELD_DOLC));
                fields.add(_CACHED_FIELDS.get(FIELD_VS));
                fields.add(_CACHED_FIELDS.get(FIELD_DX_DATE));
                fields.add(_CACHED_FIELDS.get(FIELD_SEQ_NUM_CTRL));
                fields.add(_CACHED_FIELDS.get(FIELD_TYPE_RPT_SRC));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_MONTH_ACTIVE_FUP));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_FLAG_ACTIVE_FUP));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_DATE_ACTIVE_FUP));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_MONTH_PRESUMED_ALIVE));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_FLAG_PRESUMED_ALIVE));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_DATE_PRESUMED_ALIVE));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_DX_DATE_RECODE));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_VS_RECODE));
                fields.add(_CACHED_FIELDS.get(FIELD_SURV_REC_NUM_RECODE));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                Map<String, List<String>> result = new HashMap<>();
                result.put(FIELD_SURV_MONTH_ACTIVE_FUP, Collections.singletonList(SurvivalTimeUtils.UNKNOWN_SURVIVAL));
                result.put(FIELD_SURV_FLAG_ACTIVE_FUP, Collections.singletonList(SurvivalTimeUtils.SURVIVAL_FLAG_UNKNOWN));
                result.put(FIELD_SURV_MONTH_PRESUMED_ALIVE, Collections.singletonList(SurvivalTimeUtils.UNKNOWN_SURVIVAL));
                result.put(FIELD_SURV_FLAG_PRESUMED_ALIVE, Collections.singletonList(SurvivalTimeUtils.SURVIVAL_FLAG_UNKNOWN));
                return result;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Integer cutoffYear = (Integer)input.getParameter(PARAM_SURV_CUTOFF_YEAR);
                if (cutoffYear == null)
                    throw new RuntimeException("This algorithm requires a cutoff year!");

                Map<String, Object> inputPatient = Utils.extractPatient(input);

                List<SurvivalTimeInputRecordDto> recDtoList = new ArrayList<>();
                for (Map<String, Object> inputTumor : Utils.extractTumors(inputPatient)) {
                    SurvivalTimeInputRecordDto recDto = new SurvivalTimeInputRecordDto();
                    recDto.setPatientIdNumber((String)inputPatient.get(FIELD_PAT_ID_NUMBER));
                    recDto.setDateOfDiagnosisYear(Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE)));
                    recDto.setDateOfDiagnosisMonth(Utils.extractMonth((String)inputTumor.get(FIELD_DX_DATE)));
                    recDto.setDateOfDiagnosisDay(Utils.extractDay((String)inputTumor.get(FIELD_DX_DATE)));
                    recDto.setDateOfLastContactYear(Utils.extractYear((String)inputPatient.get(FIELD_DOLC)));
                    recDto.setDateOfLastContactMonth(Utils.extractMonth((String)inputPatient.get(FIELD_DOLC)));
                    recDto.setDateOfLastContactDay(Utils.extractDay((String)inputPatient.get(FIELD_DOLC)));
                    recDto.setBirthYear(Utils.extractYear((String)inputPatient.get(FIELD_DATE_OF_BIRTH)));
                    recDto.setBirthMonth(Utils.extractMonth((String)inputPatient.get(FIELD_DATE_OF_BIRTH)));
                    recDto.setBirthDay(Utils.extractDay((String)inputPatient.get(FIELD_DATE_OF_BIRTH)));
                    recDto.setVitalStatus((String)inputPatient.get(FIELD_VS));
                    recDto.setSequenceNumberCentral((String)inputTumor.get(FIELD_SEQ_NUM_CTRL));
                    recDto.setTypeOfReportingSource((String)inputTumor.get(FIELD_TYPE_RPT_SRC));
                    recDtoList.add(recDto);
                }

                SurvivalTimeInputPatientDto patDto = new SurvivalTimeInputPatientDto();
                patDto.setSurvivalTimeInputPatientDtoList(recDtoList);

                SurvivalTimeOutputPatientDto patResultDto = SurvivalTimeUtils.calculateSurvivalTime(patDto, cutoffYear);

                Map<String, Object> outputPatient = new HashMap<>();
                outputPatient.put(FIELD_SURV_VS_RECODE, patResultDto.getVitalStatusRecode());

                List<Map<String, Object>> outputTumorList = new ArrayList<>();
                for (SurvivalTimeOutputRecordDto dto : patResultDto.getSurvivalTimeOutputPatientDtoList()) {
                    Map<String, Object> outputTumor = new HashMap<>();

                    outputTumor.put(FIELD_SURV_MONTH_ACTIVE_FUP, dto.getSurvivalMonths());
                    outputTumor.put(FIELD_SURV_FLAG_ACTIVE_FUP, dto.getSurvivalMonthsFlag());
                    outputTumor.put(FIELD_SURV_DATE_ACTIVE_FUP, Utils.combineDate(dto.getSurvivalTimeDolcYear(), dto.getSurvivalTimeDolcMonth(), dto.getSurvivalTimeDolcDay()));
                    outputTumor.put(FIELD_SURV_MONTH_PRESUMED_ALIVE, dto.getSurvivalMonthsPresumedAlive());
                    outputTumor.put(FIELD_SURV_FLAG_PRESUMED_ALIVE, dto.getSurvivalMonthsFlagPresumedAlive());
                    outputTumor.put(FIELD_SURV_DATE_PRESUMED_ALIVE,
                            Utils.combineDate(dto.getSurvivalTimeDolcYearPresumedAlive(), dto.getSurvivalTimeDolcMonthPresumedAlive(), dto.getSurvivalTimeDolcDayPresumedAlive()));
                    outputTumor.put(FIELD_SURV_DX_DATE_RECODE, Utils.combineDate(dto.getSurvivalTimeDxYear(), dto.getSurvivalTimeDxMonth(), dto.getSurvivalTimeDxDay()));
                    outputTumor.put(FIELD_SURV_REC_NUM_RECODE, dto.getSortedIndex() == null ? null : StringUtils.leftPad(dto.getSortedIndex().toString(), 2, '0'));

                    outputTumorList.add(outputTumor);
                }
                outputPatient.put(FIELD_TUMORS, outputTumorList);

                return AlgorithmOutput.of(outputPatient);
            }
        };
    }

    private static Algorithm createUric() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_URIC;
            }

            @Override
            public String getName() {
                return RuralUrbanUtils.ALG_NAME + " - URIC";
            }

            @Override
            public String getVersion() {
                return RuralUrbanUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return RuralUrbanUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "https://www.naaccr.org/analysis-and-data-improvement-tools/#RURAL";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_2000));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_2010));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_URIC_2000));
                fields.add(_CACHED_FIELDS.get(FIELD_URIC_2010));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                Map<String, List<String>> result = new HashMap<>();
                result.put(FIELD_URIC_2000, Arrays.asList(URIC_VAL_UNK_A, URIC_VAL_UNK_D));
                result.put(FIELD_URIC_2010, Arrays.asList(URIC_VAL_UNK_A, URIC_VAL_UNK_D));
                return result;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    RuralUrbanInputDto inputDto = new RuralUrbanInputDto();
                    inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
                    inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                    inputDto.setCensusTract2000((String)inputTumor.get(FIELD_CENSUS_2000));
                    inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));

                    RuralUrbanOutputDto outputDto = RuralUrbanUtils.computeUrbanRuralIndicatorCode(inputDto);

                    Map<String, Object> outputTumor = new HashMap<>();
                    outputTumor.put(FIELD_URIC_2000, outputDto.getUrbanRuralIndicatorCode2000());
                    outputTumor.put(FIELD_URIC_2010, outputDto.getUrbanRuralIndicatorCode2010());

                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(outputPatient);

            }
        };
    }

    private static Algorithm createRuca() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_RUCA;
            }

            @Override
            public String getName() {
                return RuralUrbanUtils.ALG_NAME + " - RUCA";
            }

            @Override
            public String getVersion() {
                return RuralUrbanUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return RuralUrbanUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "https://www.naaccr.org/analysis-and-data-improvement-tools/#RURAL";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_2000));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_2010));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_RUCA_2000));
                fields.add(_CACHED_FIELDS.get(FIELD_RUCA_2010));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                Map<String, List<String>> result = new HashMap<>();
                result.put(FIELD_RUCA_2000, Arrays.asList(RUCA_VAL_UNK_A, RUCA_VAL_UNK_D));
                result.put(FIELD_RUCA_2010, Arrays.asList(RUCA_VAL_UNK_A, RUCA_VAL_UNK_D));
                return result;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    RuralUrbanInputDto inputDto = new RuralUrbanInputDto();
                    inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
                    inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                    inputDto.setCensusTract2000((String)inputTumor.get(FIELD_CENSUS_2000));
                    inputDto.setCensusTract2010((String)inputTumor.get(FIELD_CENSUS_2010));

                    RuralUrbanOutputDto outputDto = RuralUrbanUtils.computeRuralUrbanCommutingArea(inputDto);

                    Map<String, Object> outputTumor = new HashMap<>();
                    outputTumor.put(FIELD_RUCA_2000, outputDto.getRuralUrbanCommutingArea2000());
                    outputTumor.put(FIELD_RUCA_2010, outputDto.getRuralUrbanCommutingArea2010());

                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(outputPatient);

            }
        };
    }

    private static Algorithm createUrbanContinuum() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_URBAN_CONTINUUM;
            }

            @Override
            public String getName() {
                return RuralUrbanUtils.ALG_NAME + " - Urban Continuum";
            }

            @Override
            public String getVersion() {
                return RuralUrbanUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return RuralUrbanUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "https://www.naaccr.org/analysis-and-data-improvement-tools/#RURAL";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_RURAL_CONT_1993));
                fields.add(_CACHED_FIELDS.get(FIELD_RURAL_CONT_2003));
                fields.add(_CACHED_FIELDS.get(FIELD_RURAL_CONT_2013));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                Map<String, List<String>> result = new HashMap<>();
                result.put(FIELD_RURAL_CONT_1993, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
                result.put(FIELD_RURAL_CONT_2003, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
                result.put(FIELD_RURAL_CONT_2013, Arrays.asList(CONTINUUM_UNK_96, CONTINUUM_UNK_97, CONTINUUM_UNK_98, CONTINUUM_UNK_99));
                return result;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    RuralUrbanInputDto inputDto = new RuralUrbanInputDto();
                    inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
                    inputDto.setCountyAtDxAnalysis((String)inputTumor.get(FIELD_COUNTY_AT_DX_ANALYSIS));

                    RuralUrbanOutputDto outputDto = RuralUrbanUtils.computeRuralUrbanContinuum(inputDto);

                    Map<String, Object> outputTumor = new HashMap<>();
                    outputTumor.put(FIELD_RURAL_CONT_1993, outputDto.getRuralUrbanContinuum1993());
                    outputTumor.put(FIELD_RURAL_CONT_2003, outputDto.getRuralUrbanContinuum2003());
                    outputTumor.put(FIELD_RURAL_CONT_2013, outputDto.getRuralUrbanContinuum2013());

                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(outputPatient);

            }
        };
    }

    private static Algorithm createSeerSiteRecode() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_SEER_SITE_RECODE;
            }

            @Override
            public String getName() {
                return SeerSiteRecodeUtils.ALG_NAME + " " + VERSION_2010;
            }

            @Override
            public String getVersion() {
                return VERSION_2010;
            }

            @Override
            public String getInfo() {
                return VERSION_2010_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "http://seer.cancer.gov/siterecode";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_PRIMARY_SITE));
                fields.add(_CACHED_FIELDS.get(FIELD_HIST_O3));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SEER_SITE_RECODE));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                return Collections.singletonMap(FIELD_SEER_SITE_RECODE, Collections.singletonList(SeerSiteRecodeUtils.UNKNOWN_RECODE));
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
                    String hist = (String)inputTumor.get(FIELD_HIST_O3);
                    outputTumors.add(Collections.singletonMap(FIELD_SEER_SITE_RECODE, SeerSiteRecodeUtils.calculateSiteRecode(getVersion(), site, hist)));
                }

                return AlgorithmOutput.of(outputPatient);

            }
        };
    }

    private static Algorithm createSeerBehaviorRecode() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_SEER_BEHAVIOR_RECODE;
            }

            @Override
            public String getName() {
                return BehaviorRecodeUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return BehaviorRecodeUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return BehaviorRecodeUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "https://seer.cancer.gov/behavrecode/";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_PRIMARY_SITE));
                fields.add(_CACHED_FIELDS.get(FIELD_HIST_O3));
                fields.add(_CACHED_FIELDS.get(FIELD_BEHAV_O3));
                fields.add(_CACHED_FIELDS.get(FIELD_DX_DATE));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_SEER_BEHAV_RECODE));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                return Collections.singletonMap(FIELD_SEER_BEHAV_RECODE, Collections.singletonList(BehaviorRecodeUtils.UNKNOWN));
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
                    String hist = (String)inputTumor.get(FIELD_HIST_O3);
                    String beh = (String)inputTumor.get(FIELD_BEHAV_O3);
                    String dxYear = Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE));
                    outputTumors.add(Collections.singletonMap(FIELD_SEER_BEHAV_RECODE, BehaviorRecodeUtils.computeBehaviorRecode(site, hist, beh, dxYear)));
                }

                return AlgorithmOutput.of(outputPatient);

            }
        };
    }

    private static Algorithm createIccc() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_ICCC;
            }

            @Override
            public String getName() {
                return IcccRecodeUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return VERSION_WHO_2008;
            }

            @Override
            public String getInfo() {
                return VERSION_WHO_2008_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "http://seer.cancer.gov/iccc";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_PRIMARY_SITE));
                fields.add(_CACHED_FIELDS.get(FIELD_HIST_O3));
                fields.add(_CACHED_FIELDS.get(FIELD_BEHAV_O3));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_ICCC));
                fields.add(_CACHED_FIELDS.get(FIELD_ICCC_MAJOR_CATEGORY));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                Map<String, List<String>> unknownValues = new HashMap<>();
                unknownValues.put(FIELD_ICCC, Collections.singletonList(IcccRecodeUtils.ICCC_UNKNOWN_RECODE));
                unknownValues.put(FIELD_ICCC_MAJOR_CATEGORY, Collections.singletonList(IcccRecodeUtils.ICCC_UNKNOWN_MAJOR_CATEGORY));
                return unknownValues;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    String site = (String)inputTumor.get(FIELD_PRIMARY_SITE);
                    String hist = (String)inputTumor.get(FIELD_HIST_O3);
                    String beh = (String)inputTumor.get(FIELD_BEHAV_O3);

                    String icccCode = IcccRecodeUtils.calculateSiteRecode(getVersion(), site, hist, beh, false);
                    String icccMajorCategory = IcccRecodeUtils.calculateIcccMajorCategory(icccCode);

                    Map<String, Object> outputTumor = new HashMap<>();
                    outputTumor.put(FIELD_ICCC, icccCode);
                    outputTumor.put(FIELD_ICCC_MAJOR_CATEGORY, icccMajorCategory);
                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(outputPatient);
            }
        };
    }

    private static Algorithm createIarc() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_IARC;
            }

            @Override
            public String getName() {
                return IarcUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return IarcUtils.VERSION;
            }

            @Override
            public String getInfo() {
                return IarcUtils.ALG_INFO;
            }

            @Override
            public String getDocumentationUrl() {
                return "http://www.iacr.com.fr/images/doc/MPrules_july2004.pdf";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_PRIMARY_SITE));
                fields.add(_CACHED_FIELDS.get(FIELD_HIST_O3));
                fields.add(_CACHED_FIELDS.get(FIELD_BEHAV_O3));
                fields.add(_CACHED_FIELDS.get(FIELD_DX_DATE));
                fields.add(_CACHED_FIELDS.get(FIELD_SEQ_NUM_CTRL));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_IARC_MP_INDICATOR));
                fields.add(_CACHED_FIELDS.get(FIELD_IARC_MP_SITE_GROUP));
                fields.add(_CACHED_FIELDS.get(FIELD_IARC_MP_HIST_GROUP));
                fields.add(_CACHED_FIELDS.get(FIELD_IARC_MP_HISTOLOGY));
                return fields;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                List<IarcMpInputRecordDto> inputRecordDtoList = new ArrayList<>();
                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    IarcMpInputRecordDto inputRecordDto = new IarcMpInputRecordDto();

                    inputRecordDto.setSite((String)inputTumor.get(FIELD_PRIMARY_SITE));
                    inputRecordDto.setHistology((String)inputTumor.get(FIELD_HIST_O3));
                    inputRecordDto.setBehavior((String)inputTumor.get(FIELD_BEHAV_O3));
                    inputRecordDto.setDateOfDiagnosisYear(Utils.extractYear((String)inputTumor.get(FIELD_DX_DATE)));
                    inputRecordDto.setDateOfDiagnosisMonth(Utils.extractMonth((String)inputTumor.get(FIELD_DX_DATE)));
                    inputRecordDto.setDateOfDiagnosisDay(Utils.extractDay((String)inputTumor.get(FIELD_DX_DATE)));
                    String seqNum = (String)inputTumor.get(FIELD_SEQ_NUM_CTRL);
                    inputRecordDto.setSequenceNumber(NumberUtils.isDigits(seqNum) ? Integer.valueOf(seqNum) : null);

                    inputRecordDtoList.add(inputRecordDto);
                }

                IarcUtils.calculateIarcMp(inputRecordDtoList);

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);
                for (IarcMpInputRecordDto dto : inputRecordDtoList) {
                    Map<String, Object> outputTumor = new HashMap<>();

                    outputTumor.put(FIELD_IARC_MP_INDICATOR, Objects.toString(dto.getInternationalPrimaryIndicator(), null));
                    outputTumor.put(FIELD_IARC_MP_SITE_GROUP, dto.getSiteGroup());
                    outputTumor.put(FIELD_IARC_MP_HIST_GROUP, Objects.toString(dto.getHistGroup(), null));
                    outputTumor.put(FIELD_IARC_MP_HISTOLOGY, dto.getHistology()); // this is weird, but the algorithm can actually update the histology as a side effect!

                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(outputPatient);
            }
        };
    }

    private static Algorithm createCountyAtDiagnosisAnalysis() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_COUNTY_AT_DIAGNOSIS_ANALYSIS;
            }

            @Override
            public String getName() {
                return CountyAtDxAnalysisUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return CountyAtDxAnalysisUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return getName();
            }

            @Override
            public String getDocumentationUrl() {
                return "https://www.naaccr.org/analysis-and-data-improvement-tools/#1571164427018-84262475-4421";
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_DX_DATE));
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_GEOCODE_1990));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_GEOCODE_2000));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_GEOCODE_2010));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_GEOCODE_2020));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_CERTAINTY_708090));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_CERTAINTY_2000));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_CERTAINTY_2010));
                fields.add(_CACHED_FIELDS.get(FIELD_CENSUS_CERTAINTY_2020));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_ANALYSIS));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_AT_DX_ANALYSIS_FLAG));
                return fields;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {
                Map<String, Object> patient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                patient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    CountyAtDxAnalysisInputDto inputDto = new CountyAtDxAnalysisInputDto();
                    inputDto.setDateOfDiagnosis((String)inputTumor.get(FIELD_DX_DATE));
                    inputDto.setAddrAtDxState((String)inputTumor.get(FIELD_STATE_DX));
                    inputDto.setCountyAtDx((String)inputTumor.get(FIELD_COUNTY_DX));
                    inputDto.setCountyAtDxGeocode1990((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_1990));
                    inputDto.setCountyAtDxGeocode2000((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_2000));
                    inputDto.setCountyAtDxGeocode2010((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_2010));
                    inputDto.setCountyAtDxGeocode2020((String)inputTumor.get(FIELD_COUNTY_AT_DX_GEOCODE_2020));
                    inputDto.setCensusTrCert19708090((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_708090));
                    inputDto.setCensusTrCertainty2000((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_2000));
                    inputDto.setCensusTrCertainty2010((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_2010));
                    inputDto.setCensusTrCertainty2020((String)inputTumor.get(FIELD_CENSUS_CERTAINTY_2020));

                    CountyAtDxAnalysisOutputDto output = CountyAtDxAnalysisUtils.computeCountyAtDiagnosis(inputDto);

                    Map<String, Object> outputTumor = new HashMap<>();
                    outputTumor.put(FIELD_COUNTY_AT_DX_ANALYSIS, output.getCountyAtDxAnalysis());
                    outputTumor.put(FIELD_COUNTY_AT_DX_ANALYSIS_FLAG, output.getCountyAtDxAnalysisFlag());
                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(patient);
            }
        };
    }

    private static Algorithm createPrcdaUiho() {
        return new Algorithm() {

            @Override
            public String getId() {
                return ALG_PRCDA_UIHO;
            }

            @Override
            public String getName() {
                return PrcdaUihoUtils.ALG_NAME;
            }

            @Override
            public String getVersion() {
                return PrcdaUihoUtils.ALG_VERSION;
            }

            @Override
            public String getInfo() {
                return PrcdaUihoUtils.ALG_INFO;
            }

            @Override
            public List<AlgorithmParam<?>> getParameters() {
                return Collections.emptyList();
            }

            @Override
            public List<AlgorithmField> getInputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_STATE_DX));
                fields.add(_CACHED_FIELDS.get(FIELD_COUNTY_DX));
                return fields;
            }

            @Override
            public List<AlgorithmField> getOutputFields() {
                List<AlgorithmField> fields = new ArrayList<>();
                fields.add(_CACHED_FIELDS.get(FIELD_PRCDA_COUNTY_2017));
                fields.add(_CACHED_FIELDS.get(FIELD_UIHO_COUNTY_2017));
                fields.add(_CACHED_FIELDS.get(FIELD_UIHO_FACILITY_2017));
                return fields;
            }

            @Override
            public Map<String, List<String>> getUnknownValues() {
                Map<String, List<String>> result = new HashMap<>();
                result.put(FIELD_PRCDA_COUNTY_2017, Collections.singletonList(PRCDA_UNKNOWN));
                result.put(FIELD_UIHO_COUNTY_2017, Collections.singletonList(UIHO_UNKNOWN));
                result.put(FIELD_UIHO_FACILITY_2017, Collections.singletonList(UIHO_FACILITY_UNKNOWN));
                return result;
            }

            @Override
            public AlgorithmOutput execute(AlgorithmInput input) {

                Map<String, Object> outputPatient = new HashMap<>();
                List<Map<String, Object>> outputTumors = new ArrayList<>();
                outputPatient.put(FIELD_TUMORS, outputTumors);

                for (Map<String, Object> inputTumor : Utils.extractTumors(Utils.extractPatient(input))) {
                    PrcdaUihoInputDto inputDto = new PrcdaUihoInputDto();
                    inputDto.setAddressAtDxState((String)inputTumor.get(FIELD_STATE_DX));
                    inputDto.setAddressAtDxCounty((String)inputTumor.get(FIELD_COUNTY_DX));

                    PrcdaUihoOutputDto outputDto = PrcdaUihoUtils.computePrcdaUiho(inputDto);

                    Map<String, Object> outputTumor = new HashMap<>();
                    outputTumor.put(FIELD_PRCDA_COUNTY_2017, outputDto.getPRCDA());
                    outputTumor.put(FIELD_UIHO_COUNTY_2017, outputDto.getUIHO());
                    outputTumor.put(FIELD_UIHO_FACILITY_2017, outputDto.getUIHOFacility());

                    outputTumors.add(outputTumor);
                }

                return AlgorithmOutput.of(outputPatient);

            }
        };
    }
}
