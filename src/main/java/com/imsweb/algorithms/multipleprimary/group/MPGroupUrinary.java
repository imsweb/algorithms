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

public class MPGroupUrinary extends MPGroup {

    public MPGroupUrinary() {
        super("urinary", "Urinary", "C659, C669, C670-C679, C680-C689", null, null, "9590-9989, 9140", Arrays.asList("2", "3", "6"));

        // M3 - When no other urinary sites are involved, tumor(s) in the right renal pelvis AND tumor(s) in the left renal pelvis are multiple primaries. (C659) 
        MPRule rule = new MPRule("urinary", "M3", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if (!"C659".equalsIgnoreCase(i1.getPrimarySite()) || !"C659".equalsIgnoreCase(i2.getPrimarySite())) {
                    result.setResult(RuleResult.FALSE);
                }
                else if (!Arrays.asList("1", "2").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality for renal pelvis tumors should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there tumors in both the right renal pelvis and the left renal pelvis and no other urinary sites are involved?");
        rule.setReason("When no other urinary sites are involved, tumor(s) in the right renal pelvis AND tumor(s) in the left renal pelvis are multiple primaries.");
        rule.getNotes().add("Use this rule and abstract as a multiple primary unless documented to be metastatic.");
        _rules.add(rule);

        // M4 - When no other urinary sites are involved, tumor(s) in both the right ureter AND tumor(s) in the left ureter are multiple primaries. (C669) 
        rule = new MPRule("urinary", "M4", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if (!"C669".equalsIgnoreCase(i1.getPrimarySite()) || !"C669".equalsIgnoreCase(i2.getPrimarySite())) {
                    result.setResult(RuleResult.FALSE);
                }
                else if (!Arrays.asList("1", "2").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality for ureter tumors should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there tumors in both the right ureter and the left ureter and no other urinary sites are involved?");
        rule.setReason("When no other urinary sites are involved, tumor(s) in both the right ureter AND tumor(s) in the left ureter are multiple primaries.");
        rule.getNotes().add("Use this rule and abstract as a multiple primary unless documented to be metastatic.");
        _rules.add(rule);

        // M5- An invasive tumor following an in situ tumor more than 60 days after diagnosis are multiple primaries.
        rule = new MPRuleBehavior("urinary", "M5");
        _rules.add(rule);

        // M6 - Bladder tumors with any combination of the following histologies: papillary carcinoma (8050), transitional cell carcinoma (8120-8124), 
        // or papillary transitional cell carcinoma (8130-8131), are a single primary.       
        rule = new MPRule("urinary", "M6", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> carcinomaHist = Arrays.asList("8050", "8120", "8121", "8122", "8123", "8124", "8130", "8131");
                if (!i1.getPrimarySite().toLowerCase().startsWith("c67") || !i2.getPrimarySite().toLowerCase().startsWith("c67")) {
                    result.setResult(RuleResult.FALSE);
                }
                else
                    result.setResult(carcinomaHist.containsAll(Arrays.asList(i1.getHistologyIcdO3(), i2.getHistologyIcdO3())) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there bladder tumors with any combination of the following histologies:\n" +
                "- papillary carcinoma (8050)\n" +
                "- transitional cell carcinoma (8120-8124)\n" +
                "- papillary tansitional cell carcinoma (8130-8131)?");
        rule.setReason(
                "Bladder tumors with any combination of the following histologies: papillary carcinoma (8050), transitional cell carcinoma (8120-8124), or papillary transitional cell carcinoma (8130-8131), are a single primary.");
        _rules.add(rule);

        // M7 - Tumors diagnosed more than three (3) years apart are multiple primaries.
        rule = new MPRule("urinary", "M7", MPResult.MULTIPLE_PRIMARIES) {
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

        // M8 - Urothelial tumors in two or more of the following sites are a single primary* (See Table 1 of pdf)
        // Renal pelvis (C659), Ureter(C669), Bladder (C670-C679), Urethra /prostatic urethra (C680)
        rule = new MPRule("urinary", "M8", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> urothelialTumors = Arrays.asList("8120", "8130", "8131", "8082", "8122", "8031", "8020");
                //The only sites not included are those from C681-C689
                if (i1.getPrimarySite().toLowerCase().startsWith("c68") && !"c680".equals(i1.getPrimarySite().toLowerCase()) || i2.getPrimarySite().toLowerCase().startsWith("c68") && !i2
                        .getPrimarySite().toLowerCase().equals("c680"))
                    result.setResult(RuleResult.FALSE);
                else
                    result.setResult(urothelialTumors.containsAll(Arrays.asList(i1.getHistologyIcdO3(), i2.getHistologyIcdO3())) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there urothelial tumors in two or more of the following sites:\n" +
                "- Renal pelvis (C659)\n" +
                "- Ureter(C669)\n" +
                "- Bladder (C670-C679)\n" +
                "- Urethra /prostatic urethra (C680)");
        rule.setReason("Urothelial tumors in two or more of the following sites are a single primary.\n" +
                "- Renal pelvis (C659)\n" +
                "- Ureter(C669)\n" +
                "- Bladder (C670-C679)\n" +
                "- Urethra /prostatic urethra (C680)");
        _rules.add(rule);

        // M9- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("urinary", "M9");
        _rules.add(rule);

        // M10- Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.
        rule = new MPRulePrimarySiteCode("urinary", "M10");
        _rules.add(rule);

        // M11- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("urinary", "M11");
        rule.getNotes().add("When an invasive tumor follows an in situ tumor within 60 days, abstract as a single primary.");
        _rules.add(rule);
    }
}
