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

public class MPGroupKidney extends MPGroup {

    public MPGroupKidney() {
        super("kidney", "Kidney", "C649", null, null, "9590-9989, 9140", Arrays.asList("2", "3", "6"));

        // M3 - Wilms tumors are a single primary. (8960/3)
        MPRule rule = new MPRule("kidney", "M3", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if ("3".equals(i1.getBehaviorIcdO3()) && "3".equals(i2.getBehaviorIcdO3()) && "8960".equals(i1.getHistologIcdO3()) && "8960".equals(i2.getHistologIcdO3()))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is the diagnosisWilms tumor?");
        rule.setReason("Wilms tumors are a single primary.");
        _rules.add(rule);

        // M4 - Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.
        rule = new MPRulePrimarySiteCode("kidney", "M4");
        _rules.add(rule);

        // M5 - Tumors in both the right kidney and in the left kidney are multiple primaries.
        rule = new MPRule("kidney", "M5", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if (!Arrays.asList("1", "2").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep()+ " of " + this.getGroupId()+ ". Valid and known laterality should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there tumors in both the left and right kidney?");
        rule.setReason("Tumors in both the right kidney and in the left kidney are multiple primaries.");
        rule.getNotes().add("Abstract as a single primary when the tumors in one kidney are documented to be metastatic from the other kidney.");
        _rules.add(rule);

        // M6 - Tumors diagnosed more than three (3) years apart are multiple primaries. 
        rule = new MPRule("kidney", "M6", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                int diff = verifyYearsApart(i1, i2, 3);
                if (-1 == diff) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
                }
                else
                    result.setResult(1 == diff ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there tumors diagnosed more than three (3) years apart?");
        rule.setReason("Tumors diagnosed more than three (3) years apart are multiple primaries.");
        _rules.add(rule);

        // M7 - An invasive tumor following an in situ tumor more than 60 days after diagnosis are multiple primaries.
        rule = new MPRuleBehavior("kidney", "M7");
        _rules.add(rule);

        // M8 - One tumor with a specific renal cell type and another tumor with a different specific renal cell type are multiple primaries (table 1 in pdf).
        rule = new MPRule("kidney", "M8", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> specificRenalCellType = Arrays.asList("8260", "8310", "8316", "8317", "8318", "8319", "8320", "8510", "8959");
                String hist1 = i1.getHistologIcdO3(), hist2 = i2.getHistologIcdO3();
                result.setResult((specificRenalCellType.containsAll(Arrays.asList(hist1, hist2)) && !hist1.equals(hist2)) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there one tumor with a specific renal cell type and another tumor with a different specific renal cell type?");
        rule.setReason("One tumor with a specific renal cell type and another tumor with a different specific renal cell type are multiple primaries.");
        _rules.add(rule);

        // M9 -
        rule = new MPRule("kidney", "M9", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String hist1 = i1.getHistologIcdO3(), hist2 = i2.getHistologIcdO3();
                List<String> nosList = Arrays.asList("8000", "8010", "8140", "8312");
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
                "Is there renal cell carcinoma, NOS (8312) and the other is a single renal cell type?");
        rule.setReason("Abstract as a single primary* when one tumor is:\n" +
                "- Cancer/malignant neoplasm, NOS (8000) and another is a specific histology or\n" +
                "- Carcinoma, NOS (8010) and another is a specific carcinoma or\n" +
                "- Adenocarcinoma, NOS (8140) and another is a specific adenocarcinoma or\n" +
                "- Renal cell carcinoma, NOS (8312) and the other is a single renal cell type");
        rule.getNotes().add("The specific histology for in situ tumors may be identified as pattern, architecture, type, subtype, predominantly, with features of, major, or with ____differentiation");
        rule.getNotes().add("The specific histology for invasive tumors may be identified as type, subtype, predominantly, with features of, major, or with ____differentiation.");
        _rules.add(rule);

        // M10- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.  
        rule = new MPRuleHistologyCode("kidney", "M10");
        _rules.add(rule);

        //M11- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("kidney", "M11");
        rule.getNotes().add("When an invasive tumor follows an in situ tumor within 60 days, abstract as a single primary.");
        _rules.add(rule);
    }
}
