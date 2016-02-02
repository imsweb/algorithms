/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.multipleprimary.group.MPGroupBenignBrain;
import com.imsweb.algorithms.multipleprimary.group.MPGroupBreast;
import com.imsweb.algorithms.multipleprimary.group.MPGroupColon;
import com.imsweb.algorithms.multipleprimary.group.MPGroupHeadAndNeck;
import com.imsweb.algorithms.multipleprimary.group.MPGroupKidney;
import com.imsweb.algorithms.multipleprimary.group.MPGroupLung;
import com.imsweb.algorithms.multipleprimary.group.MPGroupMalignantBrain;
import com.imsweb.algorithms.multipleprimary.group.MPGroupMelanoma;
import com.imsweb.algorithms.multipleprimary.group.MPGroupOtherSites;
import com.imsweb.algorithms.multipleprimary.group.MPGroupUrinary;

/**
 * This class is used to determine whether two tumors are single or multiple primaries. More information can be found here:
 * <a href="http://www.seer.cancer.gov/tools/mphrules">http://www.seer.cancer.gov/tools/mphrules</a>
 * <br/><br/>
 * This Java implementation is based on the the PDF documentation provided on the above website.
 * Created in December 2013 by Sewbesew Bekele
 */
public class MPUtils {

    public static final String PROP_PRIMARY_SITE = "primarySite";
    public static final String PROP_HISTOLOGY_ICDO3 = "histologyIcdO3";
    public static final String PROP_BEHAVIOR_ICDO3 = "behaviorIcdO3";
    public static final String PROP_LATERALITY = "laterality";
    public static final String PROP_DX_YEAR = "dateOfDiagnosisYear";
    public static final String PROP_DX_MONTH = "dateOfDiagnosisMonth";
    public static final String PROP_DX_DAY = "dateOfDiagnosisDay";

    //when we apply the rule, it might be true, false or unknown if we don't have enough information.
    public enum RuleResult {
        TRUE, FALSE, UNKNOWN
    }

    //Based on the applied rule results, we would say two tumors are single or multiple primary or questionable if we don't have enough information.
    public enum MPResult {
        SINGLE_PRIMARY, MULTIPLE_PRIMARIES, QUESTIONABLE, NOT_APPLICABLE
    }

    private static List<MPGroup> _GROUPS = new ArrayList<>();

    static {
        _GROUPS.add(new MPGroupHeadAndNeck());
        _GROUPS.add(new MPGroupColon());
        _GROUPS.add(new MPGroupLung());
        _GROUPS.add(new MPGroupMelanoma());
        _GROUPS.add(new MPGroupBreast());
        _GROUPS.add(new MPGroupKidney());
        _GROUPS.add(new MPGroupUrinary());
        _GROUPS.add(new MPGroupBenignBrain());
        _GROUPS.add(new MPGroupMalignantBrain());
        _GROUPS.add(new MPGroupOtherSites());
    }

    /**
     * Determines whether two records of solid tumors are single or multiple primary. It returns "questionable" if there is no enough information to decide.
     * <br/><br/>
     * The provided record doesn't need to contain all the input variables, but the algorithm will use the following ones:
     * <ul>
     * <li>primarySite (#400)</li>
     * <li>histologyIcdO3 (#522)</li>
     * <li>behaviorIcdO3 (#523)</li>
     * <li>laterality (#410)</li>
     * <li>dateOfDiagnosisYear (#390)</li>
     * <li>dateOfDiagnosisMonth (#390)</li>
     * <li>dateOfDiagnosisDay (#390)</li>
     * </ul>
     * <br/><br/>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * @param record1 a map of properties representing a NAACCR line
     * @param record2 a map of properties representing a NAACCR line
     * @return the computed output which is an object which has result (Single Primary, Multiple Primaries or Questionable), reason and rules applied to make a decision.
     */
    public static MPOutput computePrimaries(Map<String, String> record1, Map<String, String> record2) {
        MPInput input1 = new MPInput();
        input1.setPrimarySite(record1.get(PROP_PRIMARY_SITE));
        input1.setHistologyIcdO3(record1.get(PROP_HISTOLOGY_ICDO3));
        input1.setBehaviorIcdO3(record1.get(PROP_BEHAVIOR_ICDO3));
        input1.setLaterality(record1.get(PROP_LATERALITY));
        input1.setDateOfDiagnosisYear(record1.get(PROP_DX_YEAR));
        input1.setDateOfDiagnosisMonth(record1.get(PROP_DX_MONTH));
        input1.setDateOfDiagnosisDay(record1.get(PROP_DX_DAY));

        MPInput input2 = new MPInput();
        input2.setPrimarySite(record2.get(PROP_PRIMARY_SITE));
        input2.setHistologyIcdO3(record2.get(PROP_HISTOLOGY_ICDO3));
        input2.setBehaviorIcdO3(record2.get(PROP_BEHAVIOR_ICDO3));
        input2.setLaterality(record2.get(PROP_LATERALITY));
        input2.setDateOfDiagnosisYear(record2.get(PROP_DX_YEAR));
        input2.setDateOfDiagnosisMonth(record2.get(PROP_DX_MONTH));
        input2.setDateOfDiagnosisDay(record2.get(PROP_DX_DAY));

        return computePrimaries(input1, input2);
    }

    /**
     * Determines whether two input objects of solid tumors are single or multiple primary. It returns "questionable" if there is no enough information to decide.
     * <br/><br/>
     * <br/><br/>
     * The provided record dto has the following parameters:
     * <ul>
     * <li>_primarySite</li>
     * <li>_histologyIcdO3</li>
     * <li>_behaviorIcdO3</li>
     * <li>_laterality/li>
     * <li>_dateOfDiagnosisYear</li>
     * <li>_dateOfDiagnosisMonth</li>
     * <li>_dateOfDiagnosisDay</li>
     * </ul>
     * <br/><br/>
     * All those properties are defined as constants in this class.
     * <br/><br/>
     * @param input1 an input dto which has a list of parameters used in the calculation.
     * @param input2 an input dto which has a list of parameters used in the calculation.
     * @return the computed output which is an object which has result (Single Primary, Multiple Primaries or Questionable), reason and rules applied to make a decision.
     */
    public static MPOutput computePrimaries(MPInput input1, MPInput input2) {
        MPOutput output = new MPOutput();

        //The rules are effective for cases diagnosed January 1, 2007 and after. Do not use these rules to abstract cases diagnosed prior to January 1, 2007.
        String y1 = input1.getDateOfDiagnosisYear(), y2 = input2.getDateOfDiagnosisYear();
        if (!NumberUtils.isDigits(y1) || Integer.parseInt(y1) < 2007 || Integer.parseInt(y1) == 9999 || !NumberUtils.isDigits(y2) || Integer.parseInt(y2) < 2007 || Integer.parseInt(y2) == 9999) {
            output.setResult(MPResult.NOT_APPLICABLE);
            output.setReason("The multiple primary rules are ONLY effective for cases diagnosed January 1, 2007 and after.");
            return output;
        }

        MPGroup group1 = findCancerGroup(input1.getPrimarySite(), input1.getHistologyIcdO3(), input1.getBehaviorIcdO3());
        MPGroup group2 = findCancerGroup(input2.getPrimarySite(), input2.getHistologyIcdO3(), input2.getBehaviorIcdO3());

        if (!validateProperties(input1.getPrimarySite(), input1.getHistologyIcdO3(), input1.getBehaviorIcdO3())) {
            output.setResult(MPResult.QUESTIONABLE);
            output.setReason("Unable to identify cancer group for first set of parameters. Valid primary site (C000-C999 excluding C809), histology (8000-9999) and behavior (0-3, 6) are required.");
        }
        else if (!validateProperties(input2.getPrimarySite(), input2.getHistologyIcdO3(), input2.getBehaviorIcdO3())) {
            output.setResult(MPResult.QUESTIONABLE);
            output.setReason("Unable to identify cancer group for second set of parameters. Valid primary site (C000-C999 excluding C809), histology (8000-9999) and behavior (0-3, 6) are required.");
        }
        else if (group1 == null) {
            output.setResult(MPResult.QUESTIONABLE);
            output.setReason("The first tumor provided does not belong to any of the cancer groups.");
        }
        else if (group2 == null) {
            output.setResult(MPResult.QUESTIONABLE);
            output.setReason("The second tumor provided does not belong to any of the cancer groups.");
        }
        else if (!group1.getId().equals(group2.getId())) {
            output.setResult(MPResult.MULTIPLE_PRIMARIES);
            output.setReason("The two sets of parameters belong to two different cancer groups.");
        }
        else {
            for (MPRule rule : group1.getRules()) {
                output.getAppliedRules().add(rule);
                MPRuleResult result = rule.apply(input1, input2);
                if (RuleResult.TRUE.equals(result.getResult())) {
                    output.setResult(rule.getResult());
                    output.setReason(rule.getReason());
                    break;
                }
                else if (RuleResult.UNKNOWN.equals(result.getResult())) {
                    output.setResult(MPResult.QUESTIONABLE);
                    output.setReason(result.getMessage());
                    break;
                }
            }
        }

        return output;
    }

    /**
     * Calculates the cancer group for the provided naaccr properties.
     * @param primarySite
     * @param histology
     * @param behavior
     * @return the computed cancer group
     */
    public static MPGroup findCancerGroup(String primarySite, String histology, String behavior) {
        if (!validateProperties(primarySite, histology, behavior))
            return null;
        for (MPGroup group : _GROUPS) {
            if (group.isApplicable(primarySite, histology, behavior))
                return group;
        }
        return null;
    }

    /**
     * @return the list of cancer groups.
     */
    public static List<MPGroup> getAllGroups() {
        return Collections.unmodifiableList(_GROUPS);
    }

    /**
     * Validates the provided input's primary site, histology and behavior. These properties are required to determine the cancer group and used at least in one of the rules in each group.
     */
    public static boolean validateProperties(String primarySite, String histology, String behavior) {
        if (primarySite == null || primarySite.length() != 4 || !primarySite.startsWith("C") || !NumberUtils.isDigits(primarySite.substring(1)) || "C809".equalsIgnoreCase(primarySite)
                || histology == null || histology.length() != 4 || !NumberUtils.isDigits(histology) || Integer.parseInt(histology) < 8000)
            return false;
        return !(behavior == null || !Arrays.asList("0", "1", "2", "3", "6").contains(behavior));
    }
}
