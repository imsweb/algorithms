/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary.group;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.imsweb.algorithms.multipleprimary.MPGroup;
import com.imsweb.algorithms.multipleprimary.MPInput;
import com.imsweb.algorithms.multipleprimary.MPRule;
import com.imsweb.algorithms.multipleprimary.MPRuleResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.RuleResult;

public class MPGroupLung extends MPGroup {

    public MPGroupLung() {
        super("lung", "Lung", "C340-C349", null, null, "9590-9989, 9140", Arrays.asList("2", "3", "6"));

        // M3- Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.
        MPRule rule = new MPRulePrimarySiteCode("lung", "M3");
        rule.getNotes().add("This is a change in rules; tumors in the trachea (C33) and in the lung (C34) were a single lung primary in the previous rules.");
        _rules.add(rule);

        // M4- At least one tumor that is non-small cell carcinoma (8046) and another tumor that is small cell carcinoma (8041-8045) are multiple primaries.
        rule = new MPRule("lung", "M4", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(differentCategory(i1.getHistologIcdO3(), i2.getHistologIcdO3(), Collections.singletonList("8046"), Arrays.asList("8041", "8042", "8043", "8044",
                        "8045")) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is at least one tumor non-small cell carcinoma (8046) and another tumor small cell carcinoma (8041-8045)?");
        rule.setReason("At least one tumor that is non-small cell carcinoma (8046) and another tumor that is small cell carcinoma (8041-8045) are multiple primaries.");
        _rules.add(rule);

        // M5- A tumor that is adenocarcinoma with mixed subtypes (8255) and another that is bronchioloalveolar (8250-8254) are multiple primaries.
        rule = new MPRule("lung", "M5", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(differentCategory(i1.getHistologIcdO3(), i2.getHistologIcdO3(), Collections.singletonList("8255"), Arrays.asList("8250", "8251", "8252", "8253",
                        "8254")) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there a tumor that is adenocarcinoma with mixed subtypes (8255) and another that is bronchioalveolar (8250-8254)?");
        rule.setReason("A tumor that is adenocarcinoma with mixed subtypes (8255) and another that is bronchioloalveolar (8250-8254) are multiple primaries.");
        _rules.add(rule);

        // M6- A single tumor in each lung is multiple primaries.
        rule = new MPRule("lung", "M6", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if (!Arrays.asList("1", "2", "4").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality for lung cancer should be provided.");
                }
                else
                    result.setResult(("1".equals(i1.getLaterality()) && "2".equals(i2.getLaterality())) || ("2".equals(i1.getLaterality()) && "1".equals(i2.getLaterality())) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Is there a single tumor in each lung?");
        rule.setReason("A single tumor in each lung is multiple primaries.");
        rule.getNotes().add("When there is a single tumor in each lung abstract as multiple primaries unless stated or proven to be metastatic.");
        _rules.add(rule);

        // M7- Multiple tumors in both lungs with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.
        rule = new MPRule("lung", "M7", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                //if they are on the same lung, don't apply this
                if (i1.getLaterality().equals(i2.getLaterality()) && !"4".equals(i1.getLaterality()))
                    result.setResult(RuleResult.FALSE);
                else {
                    String hist1 = i1.getHistologIcdO3(), hist2 = i2.getHistologIcdO3();
                    result.setResult(((hist1.charAt(0) != hist2.charAt(0)) || (hist1.charAt(1) != hist2.charAt(1)) || (hist1.charAt(2) != hist2.charAt(
                            2))) ? RuleResult.TRUE : RuleResult.FALSE);
                }
                return result;
            }
        };
        rule.setQuestion("Are there multiple tumors in both lungs with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number?");
        rule.setReason("Multiple tumors in both lungs with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (x?xx) number are multiple primaries.");
        _rules.add(rule);

        // M8- Tumors diagnosed more than three (3) years apart are multiple primaries.
        rule = new MPRule("lung", "M8", MPResult.MULTIPLE_PRIMARIES) {
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

        // M9- An invasive tumor following an in situ tumor more than 60 days after diagnosis are multiple primaries.
        rule = new MPRuleBehavior("lung", "M9");
        _rules.add(rule);

        // M10- Tumors with non-small cell carcinoma, NOS (8046) and a more specific non-small cell carcinoma type (chart 1) are a single primary.
        rule = new MPRule("lung", "M10", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> specificNonSmall = Arrays.asList("8033", "8980", "8031", "8022", "8972", "8032", "8012", "8140", "8200", "8430", "8560", "8070", "8550", "8255", "8251", "8250", "8252",
                        "8253", "8254", "8310", "8470", "8480", "8481", "8260", "8490", "8230", "8333", "8013", "8014", "8082", "8123", "8310", "8083", "8052", "8084", "8071", "8072", "8073");
                result.setResult(differentCategory(i1.getHistologIcdO3(), i2.getHistologIcdO3(), Collections.singletonList("8046"), specificNonSmall) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors with non-small cell carcinoma (8046) and a more specific non-small cell carcinoma type (chart 1)?");
        rule.setReason("Tumors with non-small cell carcinoma, NOS (8046) and a more specific non-small cell carcinoma type (chart 1) are a single primary.");
        _rules.add(rule);

        // M11- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.
        rule = new MPRuleHistologyCode("lung", "M11");
        rule.getNotes().add("Adenocarcinoma in one tumor and squamous cell carcinoma in another tumor are multiple primaries.");
        _rules.add(rule);

        // M12- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("lung", "M12");
        rule.getNotes().add("When an invasive tumor follows an in situ tumor within 60 days, abstract as a single primary.");
        rule.getNotes().add("All cases covered by this rule are the same histology.");
        _rules.add(rule);
    }
}
