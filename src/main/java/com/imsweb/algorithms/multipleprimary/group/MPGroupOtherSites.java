/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.imsweb.algorithms.multipleprimary.MPGroup;
import com.imsweb.algorithms.multipleprimary.MPInput;
import com.imsweb.algorithms.multipleprimary.MPRule;
import com.imsweb.algorithms.multipleprimary.MPRuleResult;
import com.imsweb.algorithms.multipleprimary.MPUtils;
import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.RuleResult;

public class MPGroupOtherSites extends MPGroup {

    private static final List<String> _POLYP = MPGroup.expandList(Collections.singletonList("8210-8211,8213,8220-8221,8261-8263"));

    //Excludes Head and Neck, Colon, Lung, Melanoma of Skin, Breast, Kidney, Renal Pelvis, Ureter, Bladder, Brain, Lymphoma and Leukemia
    public MPGroupOtherSites() {
        super("other-sites", "Other Sites", null, null, null, "9590-9989", Arrays.asList("2", "3", "6"));

        //M3- Adenocarcinoma of the prostate is always a single primary. (C619, 8140)
        MPRule rule = new MPRule("other-sites", "M3", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                if ("C619".equalsIgnoreCase(i1.getPrimarySite()) && "C619".equalsIgnoreCase(i2.getPrimarySite()) && "8140".equals(i1.getHistologyIcdO3()) && "8140".equals(i2.getHistologyIcdO3()))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is the diagnosis adenocarcinoma of the prostate?");
        rule.setReason("Adenocarcinoma of the prostate is always a single primary.");
        rule.getNotes().add("Report only one adenocarcinoma of the prostate per patient per lifetime.");
        rule.getNotes().add("95% of prostate malignancies are the common (acinar) adenocarcinoma histology (8140). See Equivalent Terms, Definitions and Tables for more information.");
        rule.getNotes().add("If patient has a previous acinar adenocarcinoma of the prostate in the database and is diagnosed with adenocarcinoma in 2007 it is a single primary.");
        _rules.add(rule);

        //M4- Retinoblastoma is always a single primary (unilateral or bilateral). (9510, 9511, 9512, 9513)
        rule = new MPRule("other-sites", "M4", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                List<String> retinoBlastoma = Arrays.asList("9510", "9511", "9512", "9513");
                MPRuleResult result = new MPRuleResult();
                if (retinoBlastoma.containsAll(Arrays.asList(i1.getHistologyIcdO3(), i2.getHistologyIcdO3())))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is the diagnosis retinoblastoma (unilateral or bilateral)?");
        rule.setReason("Retinoblastoma is always a single primary (unilateral or bilateral).");
        _rules.add(rule);

        //M5- Kaposi sarcoma (any site or sites) is always a single primary.
        rule = new MPRule("other-sites", "M5", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                result.setResult((i1.getHistologyIcdO3().equals("9140") && i2.getHistologyIcdO3().equals("9140")) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is the diagnosis Kaposi sarcoma (any site or sites)?");
        rule.setReason("Kaposi sarcoma (any site or sites) is always a single primary.");
        _rules.add(rule);

        //M6- Follicular and papillary tumors in the thyroid within 60 days of diagnosis are a single primary. (C739, 8340)
        rule = new MPRule("other-sites", "M6", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                //If not thyroid follicular and papillary
                String site1 = i1.getPrimarySite().toUpperCase(), site2 = i2.getPrimarySite().toUpperCase(), hist1 = i1.getHistologyIcdO3(), hist2 = i2.getHistologyIcdO3();
                if (!("C739".equals(site1) && "C739".equals(site2) && "8340".equals(hist1) && "8340".equals(hist2))) {
                    result.setResult(RuleResult.FALSE);
                    return result;
                }
                int diff = verify60DaysApart(i1, i2, false);
                if (-1 == diff) {
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
                    result.setResult(RuleResult.UNKNOWN);
                }
                else
                    result.setResult(0 == diff ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there follicular and papillary tumors of the thyroid within 60 days of diagnosis?");
        rule.setReason("Follicular and papillary tumors in the thyroid within 60 days of diagnosis are a single primary.");
        _rules.add(rule);

        //M7- Bilateral epithelial tumors (8000-8799) of the ovary within 60 days are a single primary. Ovary = C569
        rule = new MPRule("other-sites", "M7", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String site1 = i1.getPrimarySite().toUpperCase(), site2 = i2.getPrimarySite().toUpperCase(), hist1 = i1.getHistologyIcdO3(), hist2 = i2.getHistologyIcdO3();
                if (!("C569".equals(site1) && "C569".equals(site2) && Integer.parseInt(hist1) <= 8799 && Integer.parseInt(hist2) <= 8799)) {
                    result.setResult(RuleResult.FALSE);
                    return result;
                }
                int diff = verify60DaysApart(i1, i2, false);
                if (-1 == diff) {
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
                    result.setResult(RuleResult.UNKNOWN);
                }
                else
                    result.setResult(0 == diff ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there bilateral epithelial tumors (8000-8799) of the ovary within 60 days of diagnosis?");
        rule.setReason("Bilateral epithelial tumors (8000-8799) of the ovary within 60 days are a single primary.");
        _rules.add(rule);

        // M8 - Tumors on both sides (right and left) of a site listed in Table 1 are multiple primaries.
        rule = new MPRule("other-sites", "M8", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> pairedSites = Arrays.asList("C384", "C400", "C401", "C402", "C403", "C413", "C414", "C441", "C442", "C443", "C445", "C446", "C447", "C471", "C472", "C491", "C492", "C569",
                        "C570", "C620-C629", "C630", "C631", "C690-C699", "C740-C749", "C754");
                boolean isPairedSite = false;
                for (String pairedSite : pairedSites) {
                    if (isContained(computeRange(pairedSite, true), Integer.parseInt(i1.getPrimarySite().substring(1))) && isContained(computeRange(pairedSite, true),
                            Integer.parseInt(i2.getPrimarySite().substring(1)))) {
                        isPairedSite = true;
                        break;
                    }
                }
                if (!isPairedSite)
                    result.setResult(RuleResult.FALSE);
                else if (!Arrays.asList("1", "2").containsAll(Arrays.asList(i1.getLaterality(), i2.getLaterality()))) {
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". Valid and known laterality for paired sites of other-sites should be provided.");
                }
                else
                    result.setResult(!i1.getLaterality().equals(i2.getLaterality()) ? RuleResult.TRUE : RuleResult.FALSE);

                return result;
            }
        };
        rule.setQuestion("Are there tumors in both the left and right sides of a paired site (Table 1)?");
        rule.setReason("Tumors on both sides (right and left) of a site listed in Table 1 are multiple primaries.");
        rule.getNotes().add("Table 1 – Paired Organs and Sites with Laterality.");
        _rules.add(rule);

        //M9 - Adenocarcinoma in adenomatous polyposis coli (familial polyposis) with one or more in situ or malignant polyps is a single primary.
        rule = new MPRule("other-sites", "M9", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                List<String> sites = Arrays.asList("C180", "C181", "C182", "C183", "C184", "C185", "C186", "C187", "C188", "C189", "C199", "C209");
                List<String> adenocarcinomaInAdenomatous = Collections.singletonList("8220");
                MPRuleResult result = new MPRuleResult();
                if (sites.containsAll(Arrays.asList(i1.getPrimarySite(), i2.getPrimarySite())) && ("3".equals(i1.getBehaviorIcdO3()) || "3".equals(i2.getBehaviorIcdO3())) &&
                        MPGroup.differentCategory(i1.getHistologyIcdO3(), i2.getHistologyIcdO3(), adenocarcinomaInAdenomatous, _POLYP))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is the diagnosis adenocarcinoma in adenomatous polyposis coli (familialpolyposis ) with one or more malignant polyps?");
        rule.setReason("Adenocarcinoma in adenomatous polyposis coli (familial polyposis) with one or more in situ or malignant polyps is a single primary.");
        _rules.add(rule);

        //M10 - Tumors diagnosed more than one (1) year apart are multiple primaries.
        rule = new MPRule("other-sites", "M10", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                int diff = verifyYearsApart(i1, i2, 1);
                if (-1 == diff) {
                    result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
                    result.setResult(RuleResult.UNKNOWN);
                }
                else
                    result.setResult(1 == diff ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors diagnosed more than one (1) year apart?");
        rule.setReason("Tumors diagnosed more than one (1) year apart are multiple primaries.");
        _rules.add(rule);

        //M11 - Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.
        rule = new MPRulePrimarySiteCode("other-sites", "M11");
        rule.getExamples().add("A tumor in the penis C609 and a tumor in the rectum C209 have different second characters in their ICD-O-3 topography codes, so they are multiple primaries.");
        rule.getExamples().add("A tumor in the cervix C539 and a tumor in the vulva C519 have different third characters in their ICD-O-3 topography codes, so they are multiple primaries.");
        _rules.add(rule);

        //M12 - Tumors with ICD-O-3 topography codes that differ only at the fourth character (Cxx?) and are in any one of the following primary sites are multiple primaries. ** Anus and anal canal (C21_) Bones, joints, and articular cartilage (C40_- C41_) Peripheral nerves and autonomic nervous system (C47_) Connective subcutaneous and other soft tissues (C49_) Skin (C44_)
        rule = new MPRule("other-sites", "M12", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> list = Arrays.asList("C21", "C40", "C41", "C47", "C49", "C44");
                //primary sites should be the same at their 2nd and 3rd digit to pass M11, so if site 1 is in the list site 2 also is.
                result.setResult((list.contains(i1.getPrimarySite().substring(0, 3).toUpperCase()) && i1.getPrimarySite().charAt(3) != i2.getPrimarySite().charAt(
                        3)) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there tumors in sites with ICD-O-3 topography codes that differ at only the fourth character (Cxx?) and are in any one of the following primary sites:\n" +
                "Anus and anal canal (C21_)\n" +
                "Bones, joints, and articular cartilage (C40_- C41_)\n" +
                "Peripheral nerves and autonomic nervous system (C47_)\n" +
                "Connective subcutaneous and other soft tissues (C49_)\n" +
                "Skin (C44_)");
        rule.setReason("Tumors with ICD-O-3 topography codes that differ only at the fourth character (Cxx?) and are in any one of the following primary sites are multiple primaries.\n" +
                "Anus and anal canal (C21_)\n" +
                "Bones, joints, and articular cartilage (C40_- C41_)\n" +
                "Peripheral nerves and autonomic nervous system (C47_)\n" +
                "Connective subcutaneous and other soft tissues (C49_)\n" +
                "Skin (C44_)");
        _rules.add(rule);

        //M13 - A frank in situ or malignant adenocarcinoma and an in situ or malignant tumor in a polyp are a single primary.
        rule = new MPRule("other-sites", "M13", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> insituOrMalignant = Arrays.asList("2", "3");
                List<String> adenocarcinoma = MPGroup.expandList(Collections.singletonList(
                        "8140,8000-8005,8010-8011,8020-8022,8046,8141-8148,8154,8160-8162,8190,8200-8201,8210-8211,8214-8215,8220-8221,8230-8231,8244-8245,8250-8255,8260-8263,8270-8272,8280-8281,8290,8300,8310,8312-8320,8322-8323,8330-8333,8335,8337,8350,8370,8380-8384,8390,8400-8403,8407-8409,8410,8413,8420,8440-8442,8450-8453,8460-8462,8470-8473,8480-8482,8490,8500-8504,8507-8508,8510,8512-8514,8520-8525,8530,8540-8543,8550-8551,8561-8562,8570-8576"));
                if (insituOrMalignant.containsAll(Arrays.asList(i1.getBehaviorIcdO3(), i2.getBehaviorIcdO3())) &&
                        !_POLYP.containsAll(Arrays.asList(i1.getHistologyIcdO3(), i2.getHistologyIcdO3()))
                        && MPGroup.differentCategory(i1.getHistologyIcdO3(), i2.getHistologyIcdO3(), adenocarcinoma, _POLYP))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there a frank in situ or malignant adenocarcinoma and an in situ or malignant tumor in a polyp?");
        rule.setReason("A frank in situ or malignant adenocarcinoma and an in situ or malignant tumor in a polyp are a single primary.");
        _rules.add(rule);

        //M14 - Multiple in situ and/or malignant polyps are a single primary.
        rule = new MPRule("other-sites", "M14", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> insituOrMalignant = Arrays.asList("2", "3");
                if (insituOrMalignant.containsAll(Arrays.asList(i1.getBehaviorIcdO3(), i2.getBehaviorIcdO3())) && _POLYP.containsAll(Arrays.asList(i1.getHistologyIcdO3(), i2.getHistologyIcdO3())))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Are there multiple in situ and/or malignant polyps?");
        rule.setReason("Multiple in situ and/or malignant polyps are a single primary.");
        rule.getNotes().add("Includes all combinations of adenomatous, tubular, villous, and tubulovillous adenomas or polyps.");
        _rules.add(rule);

        //M15 - An invasive tumor following an in situ tumor more than 60 days after diagnosis is a multiple primary.
        rule = new MPRuleBehavior("other-sites", "M15");
        _rules.add(rule);

        //M16 -
        rule = new MPRule("other-sites", "M16", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String hist1 = i1.getHistologyIcdO3(), hist2 = i2.getHistologyIcdO3();
                List<String> nosList = Arrays.asList("8000", "8010", "8070", "8140", "8720", "8800");
                if ((nosList.contains(hist1) && getNosVsSpecificMap().containsKey(hist1) && getNosVsSpecificMap().get(hist1).contains(hist2)) || (nosList.contains(hist2) && getNosVsSpecificMap()
                        .containsKey(hist2) && getNosVsSpecificMap().get(hist2).contains(hist1)))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there cancer/malignant neoplasm, NOS (8000) and another is a specific histology? or\n" +
                "Is there carcinoma, NOS (8010) and another is a specific carcinoma? or\n" +
                "Is there squamous cell carcinoma, NOS (8070) and another is a specific squamous cell carcinoma? or\n" +
                "Is there adenocarcinoma, NOS (8140) and another is a specific adenocarcinoma? or\n" +
                "Is there melanoma, NOS (8720) and another is a specific melanoma? or\n" +
                "Is there sarcoma, NOS (8800) and another is a specific sarcoma?");
        rule.setReason("Abstract as a single primary* when one tumor is:\n" +
                "- Cancer/malignant neoplasm, NOS (8000) and another is a specific histology or\n" +
                "- Carcinoma, NOS (8010) and another is a specific carcinoma or\n" +
                "- Squamous cell carcinoma, NOS (8070) and another is specific squamous cell carcinoma or\n" +
                "- Adenocarcinoma, NOS (8140) and another is a specific adenocarcinoma or\n" +
                "- Melanoma, NOS (8720) and another is a specific melanoma or\n" +
                "- Sarcoma, NOS (8800) and another is a specific sarcoma");
        _rules.add(rule);

        //M17- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("other-sites", "M17");
        _rules.add(rule);

        //M18- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("other-sites", "M18");
        rule.getNotes().add("When an invasive tumor follows an in situ tumor within 60 days, abstract as a single primary.");
        _rules.add(rule);
    }

    @Override
    public boolean isApplicable(String primarySite, String histology, String behavior) {
        if (isContained(computeRange(_histExclusions, false), Integer.parseInt(histology)) || !_behavInclusions.contains(behavior) || !MPUtils.validateProperties(primarySite, histology, behavior))
            return false;

        List<MPGroup> specificGroups = new ArrayList<>();
        specificGroups.add(new MPGroupHeadAndNeck());
        specificGroups.add(new MPGroupColon());
        specificGroups.add(new MPGroupLung());
        specificGroups.add(new MPGroupMelanoma());
        specificGroups.add(new MPGroupBreast());
        specificGroups.add(new MPGroupKidney());
        specificGroups.add(new MPGroupUrinary());
        specificGroups.add(new MPGroupBenignBrain());
        specificGroups.add(new MPGroupMalignantBrain());
        for (MPGroup group : specificGroups) {
            if (group.isApplicable(primarySite, histology, behavior))
                return false;
        }
        return true;
    }
}
