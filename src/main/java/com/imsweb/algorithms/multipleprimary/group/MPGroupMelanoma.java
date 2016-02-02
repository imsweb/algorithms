/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary.group;

import java.util.Arrays;

import com.imsweb.algorithms.multipleprimary.MPGroup;
import com.imsweb.algorithms.multipleprimary.MPInput;
import com.imsweb.algorithms.multipleprimary.MPRule;
import com.imsweb.algorithms.multipleprimary.MPRuleResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.RuleResult;

public class MPGroupMelanoma extends MPGroup {

    public MPGroupMelanoma() {
        super("melanoma", "Melanoma", "C440-C449", null, "8720-8780", null, Arrays.asList("2", "3", "6"));

        //M3- Melanomas in sites with ICD-O-3 topography codes that are different at the second (C?xx), third (Cx?x) or fourth (C44?) character are multiple primaries.
        MPRule rule = new MPRule("melanoma", "M3", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String site1 = i1.getPrimarySite(), site2 = i2.getPrimarySite();
                result.setResult(site1.equalsIgnoreCase(site2) ? RuleResult.FALSE : RuleResult.TRUE);
                return result;
            }
        };
        rule.setQuestion("Are there melanomas in sites withICD-O-3 topography codes that are different at the second (C?xx) , third (Cx?x) and/or fourth (C18?) character?");
        rule.setReason("Melanomas in sites with ICD-O-3 topography codes that are different at the second (C?xx), third (Cx?x) or fourth (C44?) character are multiple primaries.");
        _rules.add(rule);

        //M4- Melanomas with different laterality are multiple primaries. 
        rule = new MPRule("melanoma", "M4", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                // mid-line (5) is considered (look the example)
                if (!Arrays.asList("1", "2", "5").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Do the melanomas have different lateralities?");
        rule.setReason("Melanomas with different laterality are multiple primaries.");
        rule.getExamples().add("Melanoma of the right side of the chest and a melanoma at midline of the chest are different laterality, multiple primaries.");
        rule.getExamples().add("A melanoma of the right side of the chest and a melanoma of the left side of the chest are multiple primaries.");
        _rules.add(rule);

        //M5- Melanomas with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("melanoma", "M5");
        rule.setQuestion("Do the melanomas haveICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number?");
        rule.setReason("Melanomas with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.");
        _rules.add(rule);

        //M6- An invasive melanoma that occurs more than 60 days after an in situ melanoma is a multiple primary.
        rule = new MPRuleBehavior("melanoma", "M6");
        rule.setQuestion("Is there an invasive melanoma following an in situ tumor more than 60 days after diagnosis?");
        rule.setReason("An invasive melanoma that occurs more than 60 days after an in situ melanoma is a multiple primary.");
        _rules.add(rule);

        //M7- Melanomas diagnosed more than 60 days apart are multiple primaries. 
        rule = new MPRule("melanoma", "M7", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                int diff = verify60DaysApart(i1, i2, false);
                if (-1 == diff){
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
                }
                else
                    result.setResult(0 != diff ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there melanomas diagnosed more than 60 days apart?");
        rule.setReason("Melanomas diagnosed more than 60 days apart are multiple primaries.");
        _rules.add(rule);

        //M8- Melanomas that do not meet any of the above criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("melanoma", "M8");
        rule.setReason("Melanomas that do not meet any of the above criteria are abstracted as a single primary.");
        rule.getNotes().add("Use the data item \"Multiplicity Counter\" to record the number of melanomas abstracted as a single primary.");
        rule.getNotes().add("When an invasive melanoma follows an in situ melanoma within 60 days, abstract as a single primary.");
        rule.getNotes().add("All cases covered by this rule are the same site and histology.");
        _rules.add(rule);
    }
}
