/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary.group;

import java.util.Arrays;
import java.util.List;

import com.imsweb.algorithms.multipleprimary.MPGroup;
import com.imsweb.algorithms.multipleprimary.MPInput;
import com.imsweb.algorithms.multipleprimary.MPRule;
import com.imsweb.algorithms.multipleprimary.MPRuleResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.RuleResult;

public class MPGroupColon extends MPGroup {

    public MPGroupColon() {
        super("colon", "Colon", "C180-C189", null, null, "9590-9989, 9140", Arrays.asList("2", "3", "6"));

        // M3 - Adenocarcinoma in adenomatous polyposis coli (familial polyposis) with one or more malignant polyps is a single primary. //TODO
        MPRule rule = new MPRule("colon", "M3", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there adenocarcinoma in adenomatous polyposis coli (familialpolyposis) with one or more malignant polyps?");
        rule.setReason("Adenocarcinoma in adenomatous polyposis coli (familial polyposis) with one or more malignant polyps is a single primary.");
        rule.getNotes().add("Tumors may be present in multiple segments of the colon or in a single segment of the colon.");
        _rules.add(rule);

        //M4- Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx), third (Cx?x) and/or fourth (C18?) character are multiple primaries.
        rule = new MPRule("colon", "M4", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String site1 = i1.getPrimarySite(), site2 = i2.getPrimarySite();
                result.setResult(site1.equalsIgnoreCase(site2) ? RuleResult.FALSE : RuleResult.TRUE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors in sites withICD-O-3 topography codes that are different at the second (C?xx) , third (Cx?x) and/or fourth (C18?) character?");
        rule.setReason("Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx), third (Cx?x) and/or fourth (C18?) character are multiple primaries.");
        _rules.add(rule);

        //M5- Tumors diagnosed more than one (1) year apart are multiple primaries. 
        rule = new MPRule("colon", "M5", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                int diff = verifyYearsApart(i1, i2, 1);
                if (-1 == diff) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
                }
                else
                    result.setResult(1 == diff ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there tumors diagnosed more than one (1) year apart?");
        rule.setReason("Tumors diagnosed more than one (1) year apart are multiple primaries.");
        _rules.add(rule);

        //M6- An invasive tumor following an insitu tumor more than 60 days after diagnosis is a multiple primary.
        rule = new MPRuleBehavior("colon", "M6");
        _rules.add(rule);

        //M7- A frank malignant or in situ adenocarcinoma and an insitu or malignant tumor in a polyp are a single primary.//TODO
        rule = new MPRule("colon", "M7", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there a frank malignant or in situ adenocarcinoma and an in situ ormalignant tumor in a polyp?");
        rule.setReason("A frank malignant or in situ adenocarcinoma and an in situ or malignant tumor in a polyp are a single primary.");
        _rules.add(rule);

        //M8 -
        rule = new MPRule("colon", "M8", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String hist1 = i1.getHistologIcdO3(), hist2 = i2.getHistologIcdO3();
                List<String> nosList = Arrays.asList("8000", "8010", "8140", "8800");
                if ((nosList.contains(hist1) && getNoSvsSpecificMap().containsKey(hist1) && getNoSvsSpecificMap().get(hist1).contains(hist2)) || (nosList.contains(hist2) && getNoSvsSpecificMap().containsKey(
                        hist2) && getNoSvsSpecificMap().get(hist2).contains(hist1)))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there cancer/malignant neoplasm, NOS (8000) and another is a specific histology? or\n" +
                "Is there carcinoma, NOS (8010) and another is a specific carcinoma? or\n" +
                "Is there adenocarcinoma, NOS (8140) and another is a specific adenocarcinoma? or\n" +
                "Is there sarcoma, NOS (8800) and another is a specific sarcoma?");
        rule.setReason("Abstract as a single primary* when one tumor is:\n" +
                "- Cancer/malignant neoplasm, NOS (8000) and another is a specific histology or\n" +
                "- Carcinoma, NOS (8010) and another is a specific carcinoma or\n" +
                "- Adenocarcinoma, NOS (8140) and another is a specific adenocarcinoma or\n" +
                "- Sarcoma, NOS (8800) and another is a specific sarcoma");
        _rules.add(rule);

        //M9- Multiple insitu and/or malignant polyps are a single primary.//TODO
        rule = new MPRule("colon", "M9", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there multiple in situ and /or malignant polyps?");
        rule.setReason("Multiple in situ and/or malignant polyps are a single primary.");
        rule.getNotes().add("Includes all combinations of adenomatous, tubular, villous, and tubulovillous adenomas or polyps.");
        _rules.add(rule);

        //M10- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("colon", "M10");
        _rules.add(rule);

        //M11- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("colon", "M11");
        rule.getNotes().add("When an invasive tumor follows an in situ tumor within 60 days, abstract as a single primary.");
        rule.getNotes().add("All cases covered by Rule M11 are in the same segment of the colon.");
        _rules.add(rule);
    }
}
