/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary.group;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imsweb.algorithms.multipleprimary.MPGroup;
import com.imsweb.algorithms.multipleprimary.MPInput;
import com.imsweb.algorithms.multipleprimary.MPRule;
import com.imsweb.algorithms.multipleprimary.MPRuleResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.RuleResult;

public class MPGroupBenignBrain extends MPGroup {

    private static final Map<String, String> _CHART1_MAP = new HashMap<>();
    static {
        _CHART1_MAP.put("9383/1", "Ependymomas"); //Subependymoma
        _CHART1_MAP.put("9394/1", "Ependymomas"); //Myxopapillary Ependymoma
        _CHART1_MAP.put("9444/1", "Ependymomas"); //Choroid glioma
        _CHART1_MAP.put("9384/1", "Neuronal and neuronal-glial neoplasms"); //Subependymal giant cell astrocytoma
        _CHART1_MAP.put("9412/1", "Neuronal and neuronal-glial neoplasms"); //Desmoplastic infantile astrocytoma
        _CHART1_MAP.put("9413/0", "Neuronal and neuronal-glial neoplasms"); //Dysembryoplastic neuroepithelial tumor
        _CHART1_MAP.put("9442/1", "Neuronal and neuronal-glial neoplasms"); //Gliofibroma
        _CHART1_MAP.put("9505/1", "Neuronal and neuronal-glial neoplasms"); //Ganglioglioma
        _CHART1_MAP.put("9506/1", "Neuronal and neuronal-glial neoplasms"); //Central neurocytoma
        _CHART1_MAP.put("9540/0", "Neurofibromas"); //Neurofibroma, NOS
        _CHART1_MAP.put("9540/1", "Neurofibromas"); //Neurofibromatosis, NOS
        _CHART1_MAP.put("9541/0", "Neurofibromas"); //Melanotic neurofibroma
        _CHART1_MAP.put("9550/0", "Neurofibromas"); //Plexiform neurofibroma
        _CHART1_MAP.put("9560/0", "Neurofibromas"); //Neurilemoma, NOS
        _CHART1_MAP.put("9560/1", "Neurinomatosis"); //Neurinomatosis
        _CHART1_MAP.put("9562", "Neurothekeoma"); //Neurothekeoma
        _CHART1_MAP.put("9570", "Neuroma"); //Neuroma
        _CHART1_MAP.put("9571/0", "Perineurioma, NOS"); //Perineurioma, NOS
    }

    public MPGroupBenignBrain() {
        super("benign-brain", "Benign Brain", "C700-C701, C709-C725, C728-C729, C751-C753", null, null, "9590-9989,9140", Arrays.asList("0", "1"));

        // M3 - An invasive brain tumor (/3) and either a benign brain tumor (/0) or an uncertain/borderline brain tumor (/1) are always multiple primaries.        
        MPRule rule = new MPRule("benign-brain", "M3", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                //This will never happen, since the two conditions belong to different cancer group.
                MPRuleResult result = new MPRuleResult();
                result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there an invasive tumor (/3) and either a benign brain tumor (/0) or an uncertain/borderline brain tumor (/1)?");
        rule.setReason("An invasive brain tumor (/3) and either a benign brain tumor (/0) or an uncertain/borderline brain tumor (/1) are always multiple primaries.");
        _rules.add(rule);

        // M4 - Tumors with ICD-O-3 topography codes that are different at the second (C?xx) and/or third characters (Cx?x), or fourth (Cxx?) are multiple primaries.
        rule = new MPRule("benign-brain", "M4", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String site1 = i1.getPrimarySite(), site2 = i2.getPrimarySite();
                result.setResult((site1.equalsIgnoreCase(site2)) ? RuleResult.FALSE : RuleResult.TRUE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx), third character (Cx?x) and/or fourth character (Cxx?)?");
        rule.setReason("Tumors with ICD-O-3 topography codes that are different at the second (C?xx) and/or third characters (Cx?x), or fourth (Cxx?) are multiple primaries.");
        _rules.add(rule);

        // M5 - Tumors on both sides (left and right) of a paired site (Table 1) are multiple primaries.
        rule = new MPRule("benign-brain", "M5", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> pairedSites = Arrays.asList("C700", "C710", "C711", "C712", "C713", "C714", "C722", "C723", "C724", "C725");
                boolean isPairedSite = false;
                for (String pairedSite : pairedSites) {
                    if (isContained(computeRange(pairedSite, true), Integer.parseInt(i1.getPrimarySite().substring(1))) &&  isContained(computeRange(pairedSite, true), Integer.parseInt(i2.getPrimarySite().substring(1)))) {
                        isPairedSite = true;
                        break;
                    }
                }
                if (!isPairedSite)
                    result.setResult(RuleResult.FALSE);
                else if (!Arrays.asList("1", "2").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule" + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality for paired sites of " + this.getGroupId() + " should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);
                    
                return result;
            }
        };
        rule.setQuestion("Are there tumors on both sides (left and right) of a paired site?");
        rule.setReason("Tumors on both sides (left and right) of a paired site are multiple primaries.");
        _rules.add(rule);

        // M6 - An atypical choroid plexus papilloma (9390/1) following a choroid plexus papilloma, NOS (9390/0) is a single primary.
        rule = new MPRule("benign-brain", "M6", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if (!"9390".equals(i1.getHistologIcdO3()) || !"9390".equals(i2.getHistologIcdO3()))
                    result.setResult(RuleResult.FALSE);
                else
                   result.setResult(differentCategory(i1.getBehaviorIcdO3(), i2.getBehaviorIcdO3(), Arrays.asList("0"), Arrays.asList("1"))? RuleResult.TRUE : RuleResult.FALSE);
                
                return result;
            }
        };
        rule.setQuestion("Is there an atypicalchoroid plexuspapilloma (9390/1) following achoroid plexus papilloma,NOS (9390/0)?");
        rule.setReason("An atypical choroid plexus papilloma (9390/1) following a choroid plexus papilloma, NOS (9390/0) is a single primary.");
        rule.getNotes().add("Do not code progression of disease as multiple primaries.");
        _rules.add(rule);

        // M7 - A neurofibromatosis, NOS (9540/1) following a neurofibroma, NOS (9540/0) is a single primary.
        rule = new MPRule("benign-brain", "M7", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if (!"9540".equals(i1.getHistologIcdO3()) || !"9540".equals(i2.getHistologIcdO3()))
                    result.setResult(RuleResult.FALSE);
                else
                    result.setResult(differentCategory(i1.getBehaviorIcdO3(), i2.getBehaviorIcdO3(), Arrays.asList("0"), Arrays.asList("1")) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Is there a neurofibromatosis, NOS (9540/1) following a neurofibroma, NOS (9540/0)?");
        rule.setReason("A neurofibromatosis, NOS (9540/1) following a neurofibroma, NOS (9540/0) is a single primary.");
        rule.getNotes().add("Do not code progression of disease as multiple primaries.");
        _rules.add(rule);

        // M8 - Tumors with two or more histologic types on the same branch in Chart 1 are a single primary.
        rule = new MPRule("benign-brain", "M8", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                //two or more histologic types
                if (i1.getHistologIcdO3().equals(i2.getHistologIcdO3())){
                    result.setResult(RuleResult.FALSE);
                    return result;
                }
                String icd1 = i1.getHistologIcdO3() + "/" + i1.getBehaviorIcdO3(), icd2 = i2.getHistologIcdO3() + "/" + i2.getBehaviorIcdO3();
                String branch1 = _CHART1_MAP.get(icd1) != null ? _CHART1_MAP.get(icd1) : _CHART1_MAP.get(i1.getHistologIcdO3());
                String branch2 = _CHART1_MAP.get(icd2) != null ? _CHART1_MAP.get(icd2) : _CHART1_MAP.get(i2.getHistologIcdO3());
                if (branch1 == null || branch2 == null)
                    result.setResult(RuleResult.FALSE);
                else
                    result.setResult(branch1.equals(branch2) ? RuleResult.TRUE : RuleResult.FALSE);
                
                return result;
            }
        };
        rule.setQuestion("Do the tumors have two or more histologic types on the same branch in Chart 1?");
        rule.setReason("Tumors with two or more histologic types on the same branch in Chart 1 are a single primary.");
        _rules.add(rule);

        // M9 - Tumors with multiple histologic types on different branches in Chart 1 are multiple primaries.
        rule = new MPRule("benign-brain", "M9", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                //two or more histologic types
                if (i1.getHistologIcdO3().equals(i2.getHistologIcdO3())){
                    result.setResult(RuleResult.FALSE);
                    return result;
                }
                String icd1 = i1.getHistologIcdO3() + "/" + i1.getBehaviorIcdO3(), icd2 = i2.getHistologIcdO3() + "/" + i2.getBehaviorIcdO3();
                String branch1 = _CHART1_MAP.get(icd1) != null ? _CHART1_MAP.get(icd1) : _CHART1_MAP.get(i1.getHistologIcdO3());
                String branch2 = _CHART1_MAP.get(icd2) != null ? _CHART1_MAP.get(icd2) : _CHART1_MAP.get(i2.getHistologIcdO3());
                if (branch1 == null || branch2 == null)
                    result.setResult(RuleResult.FALSE);
                else
                    result.setResult(!branch1.equals(branch2) ? RuleResult.TRUE : RuleResult.FALSE);
                
                return result;
            }
        };
        rule.setQuestion("Do the tumors have multiple histologic types on different branches in Chart 1?");
        rule.setReason("Tumors with multiple histologic types on different branches in Chart 1 are multiple primaries.");
        _rules.add(rule);

        // M10 - Tumors with two or more histologic types and at least one of the histologies is not listed in Chart 1 are multiple primaries.
        rule = new MPRule("benign-brain", "M10", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                //two or more histologic types
                if (i1.getHistologIcdO3().equals(i2.getHistologIcdO3())){
                    result.setResult(RuleResult.FALSE);
                    return result;
                }
                String icd1 = i1.getHistologIcdO3() + "/" + i1.getBehaviorIcdO3(), icd2 = i2.getHistologIcdO3() + "/" + i2.getBehaviorIcdO3();
                String branch1 = _CHART1_MAP.get(icd1) != null ? _CHART1_MAP.get(icd1) : _CHART1_MAP.get(i1.getHistologIcdO3());
                String branch2 = _CHART1_MAP.get(icd2) != null ? _CHART1_MAP.get(icd2) : _CHART1_MAP.get(i2.getHistologIcdO3());
                //This rule is used only when one histology code is listed in chart and the other not, see note for M11
                if (branch1 == null && branch2 == null)
                    result.setResult(RuleResult.FALSE);
                else
                    result.setResult((branch1 == null || branch2 == null) ? RuleResult.TRUE : RuleResult.FALSE);
                
                return result;
            }
        };
        rule.setQuestion("Do the tumors have two or more histologic types and at least one of the histologies is not listed in Chart 1?");
        rule.setReason("Tumors with two or more histologic types and at least one of the histologies is not listed in Chart 1 are multiple primaries.");
        _rules.add(rule);

        //M11- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("benign-brain", "M11");
        rule.getNotes().add("Use this rule when none of the histology codes are listed in Chart 1.");
        _rules.add(rule);

        //M12- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("benign-brain", "M12");
        rule.getNotes().add("Timing is not used to determine multiple primaries for benign and borderline intracranial and CNS tumors.");
        rule.getExamples().add("Tumors in the same site with the same histology (Chart 1) and the same laterality as the original tumor are a single primary.");
        rule.getExamples().add("Tumors in the same site with the same histology (Chart 1) and it is unknown if laterality is the same as the original tumor are a single primary.");
        rule.getExamples().add("Tumors in the same site and same laterality with histology codes not listed in Chart 1 that have the same first three numbers are a single primary.");
        _rules.add(rule);
    }
}
