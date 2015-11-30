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

public class MPGroupHeadAndNeck extends MPGroup {

    public MPGroupHeadAndNeck() {
        super("head-and-neck", "Head And Neck", "C000-C148, C300-C329", null, null, "9590-9989, 9140", Arrays.asList("2", "3", "6"));

        // M3 - Tumors on the right side and the left side of a paired site are multiple primaries.  
        MPRule rule = new MPRule("head-and-neck", "M3", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> pairedSites = Arrays.asList("C079", "C080,C081", "C090,C091,C098,C099", "C300", "C310,C312", "C301");
                boolean isPairedSite = false;
                for (String pairedSite : pairedSites) {
                    if (isContained(computeRange(pairedSite, true), Integer.parseInt(i1.getPrimarySite().substring(1))) &&  isContained(computeRange(pairedSite, true), Integer.parseInt(i2.getPrimarySite().substring(1)))) {
                        isPairedSite = true;
                        break;
                    }
                }
                if (!isPairedSite) {
                    result.setResult(RuleResult.FALSE);
                    return result;
                }
                else if (!Arrays.asList("1", "2").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality for paired sites of head and neck should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there tumors in both the left and right sides of a paired site?");
        rule.setReason("Tumors on the right side and the left side of a paired site are multiple primaries.");
        _rules.add(rule);

        //M4- Tumors on the upper lip (C000 or C003) and the lower lip (C001 or C004) are multiple primaries.
        rule = new MPRule("head-and-neck", "M4", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(differentCategory(i1.getPrimarySite(), i2.getPrimarySite(), Arrays.asList("C000", "C003"), Arrays.asList("C001",
                        "C004")) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors on the upper lip (C000 or C003) and the lower lip (C001 or C004)?");
        rule.setReason("Tumors on the upper lip (C000 or C003) and the lower lip (C001 or C004) are multiple primaries.");
        _rules.add(rule);

        //M5- Tumors on the upper gum (C030) and the lower gum (C031) are multiple primaries.
        rule = new MPRule("head-and-neck", "M5", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(differentCategory(i1.getPrimarySite(), i2.getPrimarySite(), Arrays.asList("C030"), Arrays.asList("C031")) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors on the upper gum (C030) and the lower gum (C031)?");
        rule.setReason("Tumors on the upper gum (C030) and the lower gum (C031) are multiple primaries.");
        _rules.add(rule);

        //M6- Tumors in the nasal cavity (C300) and the middle ear (C301) are multiple primaries.
        rule = new MPRule("head-and-neck", "M6", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult(differentCategory(i1.getPrimarySite(), i2.getPrimarySite(), Arrays.asList("C300"), Arrays.asList("C301")) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors in the nasal cavity (C300) and the middle ear (C301)?");
        rule.setReason("Tumors in the nasal cavity (C300) and the middle ear (C301) are multiple primaries.");
        _rules.add(rule);

        //M7- Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.
        rule = new MPRulePrimarySiteCode("head-and-neck", "M7");
        _rules.add(rule);

        //M8- An invasive tumor following an insitu tumor more than 60 days after diagnosis are multiple primaries.
        rule = new MPRuleBehavior("head-and-neck", "M8");
        _rules.add(rule);

        //M9- Tumors diagnosed more than five (5) years apart are multiple primaries.
        rule = new MPRuleDiagnosisDate("head-and-neck", "M9");
        _rules.add(rule);

        //M10 - 
        rule = new MPRule("head-and-neck", "M10", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String hist1 = i1.getHistologIcdO3(), hist2 = i2.getHistologIcdO3();
                List<String> nosList = Arrays.asList("8000", "8010", "8140", "8070", "8720", "8800");
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
                "Is there squamous cell carcinoma, NOS (8070) and another is a specific squamous cell carcinoma? or\n" +
                "Is there melanoma, NOS (8720) and another is a specific melanoma? or\n" +
                "Is there sarcoma, NOS (8800) and another is a specific sarcoma?");
        rule.setReason("Abstract as a single primary* when one tumor is:\n" +
                "- Cancer/malignant neoplasm, NOS (8000) and another is a specific histology or\n" +
                "- Carcinoma, NOS (8010) and another is a specific carcinoma or\n" +
                "- Adenocarcinoma, NOS (8140) and another is a specific adenocarcinoma or\n" +
                "- Squamous cell carcinoma, NOS (8070) and another is specific squamous cell carcinoma or\n" +
                "- Melanoma, NOS (8720) and another is a specific melanoma or\n" +
                "- Sarcoma, NOS (8800) and another is a specific sarcoma");
        _rules.add(rule);

        //M11- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("head-and-neck", "M11");
        _rules.add(rule);

        //M12- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("head-and-neck", "M12");
        rule.getNotes().add("When an invasive tumor follows an in situ tumor within 60 days, abstract as a single primary.");
        rule.getNotes().add("All cases covered by Rule M12 have the same first 3 numbers in ICD-O-3 histology code.");
        rule.getExamples().add("Multifocal tumors in floor of mouth.");
        rule.getExamples().add("An in situ and invasive tumor diagnosed within60 days.");
        rule.getExamples().add("In situ following an invasive tumor more than 60 days apart.");
        _rules.add(rule);
    }
}
