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

public class MPGroupBreast extends MPGroup {

    private static final List<String> _INTRADUCTALORDUCT = Arrays.asList("8201/2", "8230/2", "8401/2", "8500/2", "8501/2", "8503/2", "8504/2", "8507/2", "8022/3", "8035/3", "8500/3", "8501/3", "8502/3", "8503/3", "8508/3");

    public MPGroupBreast() {
        super("breast", "Breast", "C500-C509", null, null, "9590-9989,9140", Arrays.asList("2", "3", "6"));

        // M4- Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.
        MPRule rule = new MPRulePrimarySiteCode("breast", "M4");
        _rules.add(rule);

        //M5- Tumors diagnosed more than five (5) years apart are multiple primaries.
        rule = new MPRuleDiagnosisDate("breast", "M5");
        _rules.add(rule);

        //M6- Inflammatory carcinoma in one or both breasts is a single primary. (8530/3)
        rule = new MPRule("breast", "M6", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if ("3".equals(i1.getBehaviorIcdO3()) && "3".equals(i2.getBehaviorIcdO3()) && "8530".equals(i1.getHistologIcdO3()) && "8530".equals(i2.getHistologIcdO3()))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there inflammatory carcinoma in one or both breasts?");
        rule.setReason("Inflammatory carcinoma in one or both breasts is a single primary.");
        _rules.add(rule);

        //M7- Tumors on both sides (right and left breast) are multiple primaries.
        rule = new MPRule("breast", "M7", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if (!Arrays.asList("1", "2").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Is there a tumor(s) in each breast?");
        rule.setReason("Tumors on both sides (right and left breast) are multiple primaries.");
        rule.getNotes().add("Lobular carcinoma in both breasts (\"mirror image\") is a multiple primary.");
        _rules.add(rule);

        //M8- An invasive tumor following an in situ tumor more than 60 days after diagnosis are multiple primaries.
        rule = new MPRuleBehavior("breast", "M8");
        _rules.add(rule);

        //M9- Tumors that are intraductal or duct and Paget Disease are a single primary.
        rule = new MPRule("breast", "M9", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                List<String> paget = Arrays.asList("8540/2", "8541/2", "8542/2", "8543/2", "8540/3", "8541/3", "8542/3", "8543/3");
                String icd1 = i1.getHistologIcdO3() + "/" + i1.getBehaviorIcdO3(), icd2 = i2.getHistologIcdO3() + "/" + i2.getBehaviorIcdO3();
                MPRuleResult result = new MPRuleResult();
                result.setResult(differentCategory(icd1, icd2, paget, _INTRADUCTALORDUCT) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are the tumors intraductal or duct and Paget Disease?");
        rule.setReason("Tumors that are intraductal or duct and Paget Disease are a single primary.");
        rule.getNotes().add("Use Table 1 and Table 2 to identify intraductal and duct carcinomas.");
        _rules.add(rule);

        //M10- Tumors that are lobular (8520) and intraductal or duct are a single primary.
        rule = new MPRule("breast", "M10", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                List<String> lobular = Arrays.asList("8520/2", "8520/3");
                String icd1 = i1.getHistologIcdO3() + "/" + i1.getBehaviorIcdO3(), icd2 = i2.getHistologIcdO3() + "/" + i2.getBehaviorIcdO3();
                MPRuleResult result = new MPRuleResult();
                result.setResult(differentCategory(icd1, icd2, lobular, _INTRADUCTALORDUCT) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are the tumors lobular (8520) and intraductal or duct?");
        rule.setReason("Tumors that are lobular (8520) and intraductal or duct are a single primary.");
        rule.getNotes().add("Use Table 1 and Table 2 to identify intraductal and duct carcinomas.");
        _rules.add(rule);

        //M11- Multiple intraductal and/or duct carcinomas are a single primary.
        rule = new MPRule("breast", "M11", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                String icd1 = i1.getHistologIcdO3() + "/" + i1.getBehaviorIcdO3(), icd2 = i2.getHistologIcdO3() + "/" + i2.getBehaviorIcdO3();
                MPRuleResult result = new MPRuleResult();
                if (!icd1.equals(icd2) && _INTRADUCTALORDUCT.contains(icd1) && _INTRADUCTALORDUCT.contains(icd2))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there multiple intraductal and/or duct carcinomas?");
        rule.setReason("Multiple intraductal and/or duct carcinomas are a single primary.");
        rule.getNotes().add("Use Table 1 and Table 2 to identify intraductal and duct carcinomas.");
        _rules.add(rule);

        //M12- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("breast", "M12");
        _rules.add(rule);

        //M13- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("breast", "M13");
        rule.getNotes().add("When an invasive tumor follows an in situ tumor within 60 days, abstract as a single primary.");
        rule.getNotes().add("All cases covered by Rule M13 have the same first 3 numbers in ICD-O-3 histology code.");
        rule.getExamples().add("Invasive duct and intraductal carcinoma in the same breast.");
        rule.getExamples().add("Multi-centric lobular carcinoma, left breast.");
        _rules.add(rule);
    }
}
