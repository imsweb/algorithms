/*
 * Copyright (C) 2019 Information Management Services, Inc.
 */
package com.imsweb.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.imsweb.algorithms.ayasiterecode.AyaSiteRecodeAlgorithm2008;
import com.imsweb.algorithms.ayasiterecode.AyaSiteRecodeAlgorithm2020;
import com.imsweb.algorithms.behavrecode.SeerBehaviorRecodeAlgorithm;
import com.imsweb.algorithms.braincnsrecode.BrainCnsRecodeAlgorithm2020;
import com.imsweb.algorithms.cancerreportingzone.CancerReportingZoneAlgorithm;
import com.imsweb.algorithms.causespecific.DeathClassificationAlgorithm;
import com.imsweb.algorithms.censustractpovertyindicator.CensusTractPovertyIndicatorAlgorithm;
import com.imsweb.algorithms.countyatdiagnosisanalysis.CountyAtDxAnalysisAlgorithm;
import com.imsweb.algorithms.daystotreatment.DaysToTreatmentAlgorithm;
import com.imsweb.algorithms.derivedgrade.DerivedSummaryGradeAlgorithm2018;
import com.imsweb.algorithms.ephtsubcounty.EphtSubCountyAlgorithm;
import com.imsweb.algorithms.iarc.IarcAlgorithm;
import com.imsweb.algorithms.iccc.IcccAlgorithm;
import com.imsweb.algorithms.lymphoma.LymphoidNeoplasmRecodeAlgorithm2021;
import com.imsweb.algorithms.napiia.NapiiaAlgorithm;
import com.imsweb.algorithms.nhia.NhiaAlgorithm;
import com.imsweb.algorithms.persistentpoverty.PersistentPovertyAlgorithm;
import com.imsweb.algorithms.prcda.PrcdaAlgorithm;
import com.imsweb.algorithms.race.Race1RecodeAlgorithm;
import com.imsweb.algorithms.ruralurban.RuralUrbanAlgorithm;
import com.imsweb.algorithms.seersiterecode.SeerSiteRecode2023Algorithm;
import com.imsweb.algorithms.seersiterecode.SeerSiteRecodeAlgorithm;
import com.imsweb.algorithms.survival.SurvivalTimeAlgorithm;
import com.imsweb.algorithms.svi.SocialVulnerabilityAlgorithm;
import com.imsweb.algorithms.tractestcongressdist.TractEstCongressDistAlgorithm;
import com.imsweb.algorithms.tumorsizeovertime.TumorSizeOverTimeAlgorithm;
import com.imsweb.algorithms.uiho.UihoAlgorithm;
import com.imsweb.algorithms.yostacspoverty.YostAcsPovertyAlgorithm;

import static com.imsweb.algorithms.AlgorithmField.DATA_LEVEL_PATIENT;
import static com.imsweb.algorithms.AlgorithmField.DATA_LEVEL_TUMOR;

/**
 * Instructions for adding a new algorithm:
 * - add the constant for the algorithm ID (make sure to follow the naming convention, they all start with ALG_).
 * - add constants for any field that doesn't have a constant yet (there are two lists, standard fields, and non-standard fields).
 * - add the new fields (standard and non-standard) to the static fields cache (see _CACHED_FIELDS).
 * - if the new algorithm needs it, add constants for its options
 * - add a static method at the end of the class "createXxx" that returns an Algorithm, see how all the other ones are done.
 * - register the new algorithm (see initialize() method).
 */
public final class Algorithms {

    // algorithm IDs
    public static final String ALG_NHIA = "nhia";
    public static final String ALG_NAPIIA = "napiia";
    public static final String ALG_DEATH_CLASSIFICATION = "death-classification";
    public static final String ALG_CENSUS_POVERTY = "census-poverty";
    public static final String ALG_RURAL_URBAN = "rural-urban";
    public static final String ALG_SURVIVAL_TIME = "survival-time";
    public static final String ALG_SEER_SITE_RECODE = "seer-site-recode";
    public static final String ALG_SEER_SITE_RECODE_2023 = "seer-site-recode-2023";
    public static final String ALG_AYA_SITE_RECODE_2008 = "aya-site-recode"; // keep old name since it might be referenced in dictionaries
    public static final String ALG_AYA_SITE_RECODE_2020 = "aya-site-recode-2020";
    public static final String ALG_SEER_BEHAVIOR_RECODE = "seer-behavior-recode";
    public static final String ALG_ICCC = "iccc";
    public static final String ALG_IARC = "iarc-multiple-primary";
    public static final String ALG_COUNTY_AT_DIAGNOSIS_ANALYSIS = "county-at-diagnosis-analysis";
    public static final String ALG_PRCDA = "prcda";
    public static final String ALG_UIHO = "uiho";
    public static final String ALG_ACS_LINKAGE = "acs-linkage";
    public static final String ALG_EPHT_SUBCOUNTY = "epht-subcounty";
    public static final String ALG_TRACT_EST_CONGRESS_DIST = "tract-est-congress-dist";
    public static final String ALG_CANCER_REPORTING_ZONE = "cancer-reporting-zone";
    public static final String ALG_PERSISTENT_POVERTY = "persistent-poverty";
    public static final String ALG_SEER_BRAIN_CNS_RECODE = "seer-brain-cns-recode";
    public static final String ALG_SEER_LYMPH_NEO_RECODE_2021 = "seer-lymphoid-neoplasm-recode-2021";
    public static final String ALG_SEER_DERIVED_SUMMARY_STAGE_2018 = "seer-derived-summary-stage-2018";
    public static final String ALG_SVI = "svi";
    public static final String ALG_DAYS_TO_TREATMENT = "days-to-treatment";
    public static final String ALG_TUMOR_SIZE_OVER_TIME = "tumor-size-over-time";
    public static final String ALG_RACE_1_RECODE = "race-1-recode";

    // special properties
    public static final String FIELD_TUMORS = "tumors";

    // standard fields
    public static final String FIELD_PAT_ID_NUMBER = "patientIdNumber";
    public static final String FIELD_SPAN_HISP_OR = "spanishHispanicOrigin";
    public static final String FIELD_NAME_LAST = "nameLast";
    public static final String FIELD_NAME_FIRST = "nameFirst";
    public static final String FIELD_NAME_BIRTH_SURNAME = "nameBirthSurname";
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
    public static final String FIELD_CENSUS_CERTAINTY_2020 = "censusTractCertainty2020";
    public static final String FIELD_COUNTY_AT_DX_ANALYSIS = "countyAtDxAnalysis";
    public static final String FIELD_DATE_INITIAL_RX_SEER = "dateInitialRxSeer";
    public static final String FIELD_SCHEMA_ID = "schemaId";
    public static final String FIELD_GRADE_CLINICAL = "gradeClinical";
    public static final String FIELD_GRADE_PATHOLOGICAL = "gradePathological";
    public static final String FIELD_TUMOR_SIZE = "csTumorSize";
    public static final String FIELD_EOD_TUMOR_SIZE = "eodTumorSize";

    public static final String FIELD_TUMOR_SIZE_SUMMARY = "tumorSizeSummary";

    // non-standard fields
    public static final String FIELD_NAPIIA_NEEDS_REVIEW = "napiiaNeedsHumanReview";
    public static final String FIELD_NAPIIA_REVIEW_REASON = "napiiaReasonForReview";
    public static final String FIELD_SEER_SITE_RECODE = "seerSiteRecode";
    public static final String FIELD_SEER_SITE_RECODE_2023 = "seerSiteRecode2023";
    public static final String FIELD_SEER_SITE_RECODE_2023_EXPANDED = "seerSiteRecode2023Expanded";
    public static final String FIELD_SEER_BEHAV_RECODE = "seerBehaviorRecode";
    public static final String FIELD_AYA_SITE_RECODE_2008 = "ayaSiteRecode2008";
    public static final String FIELD_AYA_SITE_RECODE_2020 = "ayaSiteRecode2020";
    public static final String FIELD_ICCC = "iccc";
    public static final String FIELD_ICCC_EXT = "icccExtended";
    public static final String FIELD_IARC_MP_INDICATOR = "iarcMpIndicator";
    public static final String FIELD_COUNTY_AT_DX_ANALYSIS_FLAG = "countyAtDxAnalysisFlag";
    public static final String FIELD_IHS_PRCDA = "ihsPurchRefCareDeliveryArea";
    public static final String FIELD_IHS_PRCDA_2017 = "ihsPurchRefCareDeliveryArea2017";
    public static final String FIELD_UIHO = "urbanIndianHealthOrganization";
    public static final String FIELD_UIHO_CITY = "uihoFacility";
    public static final String FIELD_ACS_YOST_QUINTILE_US = "yostQuintileUS";
    public static final String FIELD_ACS_YOST_QUINTILE_STATE = "yostQuintileState";
    public static final String FIELD_ACS_POV_ALL_RACES = "acsPctPovAllRaces";
    public static final String FIELD_ACS_POV_WHITE = "acsPctPovWhite";
    public static final String FIELD_ACS_POV_BLACK = "acsPctPovBlack";
    public static final String FIELD_ACS_POV_AIAN = "acsPctPovAIAN";
    public static final String FIELD_ACS_POV_ASIAN_NHOPI = "acsPctPovAsianNHOPI";
    public static final String FIELD_ACS_POV_WHITE_NON_HISP = "acsPctPovWhiteNonHisp";
    public static final String FIELD_ACS_POV_HISP = "acsPctPovHispanic";
    public static final String FIELD_EPHT_2010_GEOID_5K = "epht2010GeoId5k";
    public static final String FIELD_EPHT_2010_GEOID_20K = "epht2010GeoId20k";
    public static final String FIELD_EPHT_2010_GEOID_50K = "epht2010GeoId50k";
    public static final String FIELD_TRACT_EST_CONGRESS_DIST = "tractEstCongressDist";
    public static final String FIELD_CANCER_REPORTING_ZONE = "cancerReportingZone";
    public static final String FIELD_CANCER_REPORTING_ZONE_TRACT_REQ = "cancerReportingZoneTractReq";
    public static final String FIELD_PERSISTENT_POVERTY = "persistentPoverty";
    public static final String FIELD_SEER_BRAIN_CSN_RECODE_2020 = "seerBrainCnsRecode2020";
    public static final String FIELD_SEER_LYMPH_NEO_RECODE_2021 = "seerLymphNeoplasmRecode2021";
    public static final String FIELD_DERIVED_SUMMARY_GRADE_2018 = "derivedSummaryGrade2018";
    public static final String FIELD_CDC_SVI_2018 = "cdcSVI2018";
    public static final String FIELD_DAYS_TO_TREATMENT = "daysToTreatment";
    public static final String FIELD_TUMOR_SIZE_OVER_TIME = "tumorSizeOverTime";
    public static final String FIELD_RACE1_RECODE = "race1Recode";

    // options
    public static final String PARAM_NHIA_OPTION = "nhiaOption";
    public static final String PARAM_SEER_COD_CLASS_CUTOFF_YEAR = "seerCodClassCutoffYear";
    public static final String PARAM_CENSUS_POVERTY_INC_RECENT_YEARS = "censusPovertyIncludeRecentYears";
    public static final String PARAM_SURV_CUTOFF_YEAR = "survivalCutoffYear";
    public static final String PARAM_IARC_REVIEW_MODE = "iarcReviewMode";

    // cached fields
    private static final Map<String, AlgorithmField> _CACHED_FIELDS = new HashMap<>();

    // cached algorithms
    private static final Map<String, Algorithm> _CACHED_ALGORITHMS = new HashMap<>();

    // lock to control safe access to the caches
    private static final ReentrantReadWriteLock _LOCK = new ReentrantReadWriteLock();

    /**
     * Constructor.
     */
    private Algorithms() {
        // no instances of this class allowed!
    }

    /**
     * Initializes the default fields and algorithms.
     */
    public static void initialize() {
        _LOCK.writeLock().lock();
        try {
            // standard fields
            addField(AlgorithmField.of(FIELD_PAT_ID_NUMBER, 20, 8, "Patient ID Number", "Pat ID #", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_NAME_LAST, 2230, 50, "Name--Last", "Last", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_NAME_FIRST, 2240, 50, "Name--First", "First", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_NAME_BIRTH_SURNAME, 2232, 40, "Name--Birth Surname", "Birth Sur", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_COUNTRY_BIRTH, 254, 3, "Birthplace--Country", "Birth Country", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_DATE_OF_BIRTH, 240, 8, "Date of Birth", "Birth Dt", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_SEX, 220, 1, "Sex", "Sex", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_RACE1, 160, 2, "Race 1", "Race 1", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_RACE2, 161, 2, "Race 2", "Race 2", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_RACE3, 162, 2, "Race 3", "Race 3", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_RACE4, 163, 2, "Race 4", "Race 4", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_RACE5, 164, 2, "Race 5", "Race 5", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_SPAN_HISP_OR, 190, 1, "Spanish/Hispanic Origin", "Hisp Orig", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_IHS, 192, 1, "IHS Link", "IHS", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_NHIA, 191, 1, "NHIA Derived Hisp Origin", "NHIA", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_NAPIIA, 193, 2, "Race--NAPIIA(derived API)", "NAPIIA", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_COUNTY_DX, 90, 3, "County at DX Reported", "DX Cty Code", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_STATE_DX, 80, 2, "Addr at DX--State", "DX State", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_DOLC, 1750, 8, "Date of Last Contact", "DOLC", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_VS, 1760, 1, "Vital Status", "VS", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_TYPE_RPT_SRC, 500, 1, "Type of Reporting Source", "Rpt Src", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_COD, 1910, 4, "Cause of Death", "COD", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_SEER_COD_CLASS, 1914, 1, "SEER Cause Specific COD", "COD Spec", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SEER_COD_OTHER, 1915, 1, "SEER Other COD", "COD Other", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_ICD_REV_NUM, 1920, 1, "ICD Revision Number", "COD Rev#", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_SEQ_NUM_CTRL, 380, 2, "Sequence Number--Central", "Ctrl Seq#", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_PRIMARY_SITE, 400, 4, "Primary Site", "Site", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_HIST_O3, 522, 4, "Histologic Type ICD-O-3", "Hist(O3)", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_BEHAV_O3, 523, 1, "Behavior Code ICD-O-3", "Behav(O3)", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_DX_DATE, 390, 8, "Date of Diagnosis", "DX Dt", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_CENSUS_2000, 130, 6, "Census Tract 2000", "Cens 2000", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_CENSUS_2010, 135, 6, "Census Tract 2010", "Cens 2010", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_CENSUS_POVERTY_INDICTR, 145, 1, "Census Tr Poverty Indictr", "Cens Pov", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_IHS_PRCDA, 194, 1, "IHS Purchased/Referred Care Delivery Area", "IHS PRCDA", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_UIHO, 284, 1, "Urban Indian Health Organization (UIHO)", "UIHO", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_UIHO_CITY, 285, 2, "UIHO City", "UIHO City", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_URIC_2000, 345, 1, "URIC 2000", "URIC 2000", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_URIC_2010, 346, 1, "URIC 2010", "URIC 2010", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_RUCA_2000, 339, 1, "RUCA 2000", "RUCA 2000", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_RUCA_2010, 341, 1, "RUCA 2010", "RUCA 2010", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_RURAL_CONT_1993, 3300, 2, "RuralUrban Continuum 1993", "Rur Urb Cont 93", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_RURAL_CONT_2003, 3310, 2, "RuralUrban Continuum 2003", "Rur Urb Cont 03", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_RURAL_CONT_2013, 3312, 2, "RuralUrban Continuum 2013", "Rur Urb Cont 13", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_VS_RECODE, 1762, 1, "Vital Status Recode", "VS Rec", DATA_LEVEL_PATIENT));
            addField(AlgorithmField.of(FIELD_SURV_DX_DATE_RECODE, 1788, 8, "Surv-Date DX Recode", "Surv DX", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_DATE_ACTIVE_FUP, 1782, 8, "Surv-Date Active Followup", "Surv Dt", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_DATE_PRESUMED_ALIVE, 1785, 8, "Surv-Date Presumed Alive", "Surv Dt PA", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_MONTH_ACTIVE_FUP, 1784, 4, "Surv-Mos Active Followup", "Surv", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_FLAG_ACTIVE_FUP, 1783, 1, "Surv-Flag Active Followup", "Surv Fg", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_MONTH_PRESUMED_ALIVE, 1787, 4, "Surv-Mos Presumed Alive", "Surv PA", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_FLAG_PRESUMED_ALIVE, 1786, 1, "Surv-Flag Presumed Alive", "Surv Fg PA", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SURV_REC_NUM_RECODE, 1775, 2, "Record Number Recode", "Rec Num", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_1990, 94, 3, "County at DX Geocode 1970/80/90", "Cty DX Geo708090", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_2000, 95, 3, "County at DX Geocode2000", "Cty DX Geo00", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_2010, 96, 3, "County at DX Geocode2010", "Cty DX Geo10", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_COUNTY_AT_DX_GEOCODE_2020, 97, 3, "County at DX Geocode2020", "Cty DX Geo20", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_19708090, 81, 2, "State at DX Geocode 1970/80/90", "State DX Geo708090", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_2000, 82, 2, "State at DX Geocode 2000", "State Dx Geo00", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_2010, 83, 2, "State at DX Geocode 2010", "State Dx Geo10", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_STATE_AT_DX_GEOCODE_2020, 84, 2, "State at DX Geocode 2020", "State Dx Geo20", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_CENSUS_CERTAINTY_708090, 364, 1, "Census Tr Cert 1970/80/90", "Cens Cert 70/80/90", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_CENSUS_CERTAINTY_2000, 365, 1, "Census Tr Certainty 2000", "Cens Cert 2000", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_CENSUS_CERTAINTY_2010, 367, 1, "Census Tr Certainty 2010", "Cens Cert 2010", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_CENSUS_CERTAINTY_2020, 369, 1, "Census Tract Certainty 2020", "Cens Cert 2020", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_COUNTY_AT_DX_ANALYSIS, 89, 3, "County at DX Analysis", "DX Anlys Cty", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_DATE_INITIAL_RX_SEER, 1260, 8, "Date Initial RX SEER", "Dt Init RX", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_SCHEMA_ID, 3800, 5, "Schema ID", "Schema Id", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_GRADE_CLINICAL, 3843, 1, "Grade Clinical", "Grade Clin", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_GRADE_PATHOLOGICAL, 3844, 1, "Grade Pathological", "Grade Path", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_TUMOR_SIZE, 2800, 3, "CS Tumor Size", "CS Size", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_EOD_TUMOR_SIZE, 780, 3, "EOD--Tumor Size", "EOD Size", DATA_LEVEL_TUMOR));
            addField(AlgorithmField.of(FIELD_TUMOR_SIZE_SUMMARY, 756, 3, "Tumor Size Summary", "Tumor Size Summary", DATA_LEVEL_TUMOR));

            // non-standard fields
            addField(AlgorithmField.of(FIELD_NAPIIA_NEEDS_REVIEW, 9430, 1, "NAPIIA Needs Review", "NAPIIA Rev", DATA_LEVEL_PATIENT, false));
            addField(AlgorithmField.of(FIELD_NAPIIA_REVIEW_REASON, 9431, 65, "NAPIIA Review Reason", "NAPIIA Rev Res", DATA_LEVEL_PATIENT, false));
            addField(AlgorithmField.of(FIELD_SEER_SITE_RECODE, 9410, 5, "SEER Site Recode ICD-O-3", "Site Recode", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_SEER_SITE_RECODE_2023, 9412, 2, "SEER Site Recode ICD-O-3 2023 Revision", "Site Recode 23", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_SEER_SITE_RECODE_2023_EXPANDED, 9413, 2, "SEER Site Recode ICD-O-3 2023 Revision Expanded", "Site Recode 23 Exp", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_SEER_BEHAV_RECODE, 9411, 1, "SEER Site Behavior Recode ICD-O-3", "SEER Behav", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_AYA_SITE_RECODE_2008, 9444, 2, "AYA Site Recode 2008", "AYA 08", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_AYA_SITE_RECODE_2020, 9445, 3, "AYA Site Recode 2020", "AYA 20", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ICCC, 9420, 3, "Int Class of Childhood Cancer (ICCC)", "ICCC", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ICCC_EXT, 9422, 3, "Int Class of Childhood Cancer (ICCC) Extended Code", "ICCC Ext", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_IARC_MP_INDICATOR, 9400, 1, "IARC Multiple Primary Indicator", "IARC MP", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_COUNTY_AT_DX_ANALYSIS_FLAG, 9440, 4, "County at DX Analysis Flag", "DX Anlys Cty Fg", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_IHS_PRCDA_2017, 9970, 1, "IHS PRCDA 2017", "IHS PRCDA 2017", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_YOST_QUINTILE_US, 9610, 1, "Yost Quintile (Across States)", "Yost US", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_YOST_QUINTILE_STATE, 9611, 1, "Yost Quintile (State Specific)", "Yost State", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_POV_ALL_RACES, 9612, 6, "ACS Pct Poverty (All Races)", "ACS Pov All", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_POV_WHITE, 9613, 6, "ACS Pct Poverty (2White)", "ACS Pov White", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_POV_BLACK, 9614, 6, "ACS Pct Poverty (Black)", "ACS Pov Black", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_POV_AIAN, 9615, 6, "ACS Pct Poverty (AIAN)", "ACS Pov AIAN", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_POV_ASIAN_NHOPI, 9616, 6, "ACS Pct Poverty (2Asian/NHOPI)", "ACS Pov Asian/NHOPI", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_POV_WHITE_NON_HISP, 9618, 6, "ACS Pct Poverty (White, Non-Hispanic)", "ACS Pov Non-Hispanic", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_ACS_POV_HISP, 9619, 6, "ACS Pct Poverty (Hispanic)", "ACS Pov Hispanic", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_EPHT_2010_GEOID_5K, 9993, 11, "EPHT 2010 GEO ID 5K", "EPHT 5K", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_EPHT_2010_GEOID_20K, 9994, 11, "EPHT 2010 GEO ID 20K", "EPHT 20K", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_EPHT_2010_GEOID_50K, 9995, 11, "EPHT 2010 GEO ID 50K", "EPHT 50K", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_TRACT_EST_CONGRESS_DIST, 9405, 4, "Tract-Estimated Congressional District", "Tract-Est Congress Dist", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_CANCER_REPORTING_ZONE, 9415, 10, "Cancer Reporting Zone", "Cancer Rpt Zone", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_CANCER_REPORTING_ZONE_TRACT_REQ, 9416, 1, "Census Tract Required for Cancer Reporting Zone", "Cancer Rpt Zone Tract Cert", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_PERSISTENT_POVERTY, 9625, 1, "Persistence Poverty", "Persist Pov", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_SEER_BRAIN_CSN_RECODE_2020, 9455, 2, "SEER Brain/CNS Recode 2020", "Brain/CNS", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_SEER_LYMPH_NEO_RECODE_2021, 9450, 2, "SEER Lymphoid Neoplasm Recode 2021", "Lymph 21", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_DERIVED_SUMMARY_GRADE_2018, 1975, 1, "Derived Summary Grade 2018", "Der Sum Grade 18", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_CDC_SVI_2018, 9600, 5, "CDC/ATSDR Social Vulnerability Index 2018", "CDC/ATSDR SVI 2018", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_DAYS_TO_TREATMENT, 9750, 3, "Days from Diagnosis to Treatment", "Days to TX", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_TUMOR_SIZE_OVER_TIME, 9650, 3, "Tumor Size Over Time", "Tumor Size Over Time", DATA_LEVEL_TUMOR, false));
            addField(AlgorithmField.of(FIELD_RACE1_RECODE, 9460, 2, "Race 1 Recode", "Race 1 Recode", DATA_LEVEL_PATIENT, false));

            // algorithms
            addAlgorithm(new NhiaAlgorithm());
            addAlgorithm(new NapiiaAlgorithm());
            addAlgorithm(new DeathClassificationAlgorithm());
            addAlgorithm(new CensusTractPovertyIndicatorAlgorithm());
            addAlgorithm(new SurvivalTimeAlgorithm());
            addAlgorithm(new RuralUrbanAlgorithm());
            addAlgorithm(new SeerSiteRecodeAlgorithm());
            addAlgorithm(new SeerSiteRecode2023Algorithm());
            addAlgorithm(new SeerBehaviorRecodeAlgorithm());
            addAlgorithm(new BrainCnsRecodeAlgorithm2020());
            addAlgorithm(new AyaSiteRecodeAlgorithm2008());
            addAlgorithm(new AyaSiteRecodeAlgorithm2020());
            addAlgorithm(new IcccAlgorithm());
            addAlgorithm(new IarcAlgorithm());
            addAlgorithm(new CountyAtDxAnalysisAlgorithm());
            addAlgorithm(new PrcdaAlgorithm());
            addAlgorithm(new UihoAlgorithm());
            addAlgorithm(new YostAcsPovertyAlgorithm());
            addAlgorithm(new EphtSubCountyAlgorithm());
            addAlgorithm(new TractEstCongressDistAlgorithm());
            addAlgorithm(new CancerReportingZoneAlgorithm());
            addAlgorithm(new PersistentPovertyAlgorithm());
            addAlgorithm(new LymphoidNeoplasmRecodeAlgorithm2021());
            addAlgorithm(new SocialVulnerabilityAlgorithm());
            addAlgorithm(new DaysToTreatmentAlgorithm());
            addAlgorithm(new DerivedSummaryGradeAlgorithm2018());
            addAlgorithm(new TumorSizeOverTimeAlgorithm());
            addAlgorithm(new Race1RecodeAlgorithm());
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    private static void addField(AlgorithmField field) {
        if (field.getId() == null)
            throw new IllegalStateException("Field ID is required!");
        if (Algorithms._CACHED_FIELDS.containsKey(field.getId()))
            throw new IllegalStateException("Field ID '" + field.getId() + "' has already been registered!");

        Algorithms._CACHED_FIELDS.put(field.getId(), field);
    }

    private static void addAlgorithm(Algorithm algorithm) {
        if (algorithm.getId() == null)
            throw new IllegalStateException("Algorithm ID is required!");
        if (Algorithms._CACHED_ALGORITHMS.containsKey(algorithm.getId()))
            throw new IllegalStateException("Algorithm ID '" + algorithm.getId() + "' has already been registered!");

        Algorithms._CACHED_ALGORITHMS.put(algorithm.getId(), algorithm);
    }

    /**
     * Returns true if the class has already been initializes, false otherwise.
     */
    public static boolean isInitialized() {
        return !_CACHED_ALGORITHMS.isEmpty();
    }

    /**
     * Registers the provided algorithm.
     */
    public static void registerAlgorithm(Algorithm algorithm) {
        if (algorithm.getId() == null)
            throw new IllegalStateException("Algorithm ID is required!");

        _LOCK.writeLock().lock();
        try {
            _CACHED_ALGORITHMS.put(algorithm.getId(), algorithm);
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    /**
     * Unregisters the provided algorithm.
     */
    public static void unregisterAlgorithm(Algorithm algorithm) {
        if (algorithm.getId() == null)
            throw new IllegalStateException("Algorithm ID is required!");

        _LOCK.writeLock().lock();
        try {
            _CACHED_ALGORITHMS.remove(algorithm.getId());
        }
        finally {
            _LOCK.writeLock().unlock();
        }
    }

    /**
     * Returns all the registered algorithms.
     */
    public static List<Algorithm> getAlgorithms() {
        _LOCK.readLock().lock();
        try {
            return new ArrayList<>(_CACHED_ALGORITHMS.values());
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    /**
     * Returns the algorithm for the requested ID; null if not found.
     */
    public static Algorithm getAlgorithm(String algorithmId) {
        _LOCK.readLock().lock();
        try {
            if (_CACHED_ALGORITHMS.isEmpty())
                throw new IllegalStateException("Algorithms have not been initialized!");
            return _CACHED_ALGORITHMS.get(algorithmId);
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    /**
     * Returns all the registered fields.
     */
    public static Collection<AlgorithmField> getAllFields() {
        return Collections.unmodifiableCollection(_CACHED_FIELDS.values());
    }

    /**
     * Returns the field for the requested ID, null if not found.
     */
    public static AlgorithmField getField(String field) {
        return _CACHED_FIELDS.get(field);
    }
}
