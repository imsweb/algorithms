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

public class MPGroupMalignantBrain extends MPGroup {

    private static final Map<String, String> _CHART1_MAP = new HashMap<>();

    static {
        _CHART1_MAP.put("9503", "Neuroepithelial"); //This is included in all branches
        _CHART1_MAP.put("9508", "Embryonal tumors"); //Atypical tetratoid/rhabdoid tumor
        _CHART1_MAP.put("9392", "Embryonal tumors"); //Ependymoblastoma
        _CHART1_MAP.put("9501", "Embryonal tumors"); //Medulloepithelioma
        _CHART1_MAP.put("9502", "Embryonal tumors"); //Teratoid medulloepthelioma
        _CHART1_MAP.put("9470", "Embryonal tumors"); //Medulloblastoma
        _CHART1_MAP.put("9471", "Embryonal tumors"); //Demoplastic
        _CHART1_MAP.put("9474", "Embryonal tumors"); //Large cell
        _CHART1_MAP.put("9472", "Embryonal tumors"); //Medullomyoblastoma
        _CHART1_MAP.put("9473", "Embryonal tumors"); //Supratentorial primitive neuroectodermal tumor (PNET)
        _CHART1_MAP.put("9500", "Embryonal tumors"); //Neuroblastoma
        _CHART1_MAP.put("9490", "Embryonal tumors"); //Ganglioneuroblastoma
        _CHART1_MAP.put("9391", "Ependymal tumors"); //Ependymoma, NOS
        _CHART1_MAP.put("9392", "Ependymal tumors"); //Anasplastic ependymoma
        _CHART1_MAP.put("9393", "Ependymal tumors"); //Papillary ependymoma
        _CHART1_MAP.put("9362", "Pineal tumors"); //Pineoblastoma
        _CHART1_MAP.put("9390", "Choroid plexus tumors"); //Choroid plexus carcinoma
        _CHART1_MAP.put("9505", "Neuronal and mixed neuronal-glial tumors"); //Ganglioglioma, anaplastic  // Ganglioglioma, malignant
        _CHART1_MAP.put("9522", "Neuroblastic tumors"); //Olfactory neuroblastoma
        _CHART1_MAP.put("9521", "Neuroblastic tumors"); //Olfactory neurocytoma
        _CHART1_MAP.put("9523", "Neuroblastic tumors"); //Olfactory neuroepithlioma
        _CHART1_MAP.put("9380", "Glial tumors"); //Glioma, NOS
        _CHART1_MAP.put("9430", "Glial tumors"); //Astroblastoma
        _CHART1_MAP.put("9381", "Glial tumors"); //Gliomatosis cerebri
        _CHART1_MAP.put("9423", "Glial tumors"); //Polar spongioblastoma
        _CHART1_MAP.put("9382", "Glial tumors"); //Mixed glioma
        _CHART1_MAP.put("9400", "Glial tumors"); //Astrocytoma, NOS
        _CHART1_MAP.put("9401", "Glial tumors"); //Anaplastic astrocytoma
        _CHART1_MAP.put("9420", "Glial tumors"); //Fibrillary astrocytoma
        _CHART1_MAP.put("9411", "Glial tumors"); //Gemistocytic astrocytoma
        _CHART1_MAP.put("9410", "Glial tumors"); //Protoplasmic astromytoma
        _CHART1_MAP.put("9421", "Glial tumors"); //Pilocytic astrocytoma
        _CHART1_MAP.put("9424", "Glial tumors"); //Pleomorphic xanthoastrocytoma
        _CHART1_MAP.put("9440", "Glial tumors"); //Glioblastoma, NOS and Glioblastoma multiforme
        _CHART1_MAP.put("9441", "Glial tumors"); //Giant cell glioblastoma
        _CHART1_MAP.put("9442", "Glial tumors"); //Gliosarcoma
        _CHART1_MAP.put("9450", "Oligodendroglial tumors"); //Oligodendroglioma NOS
        _CHART1_MAP.put("9451", "Oligodendroglial tumors"); //Oligodendroglioma anaplastic
        _CHART1_MAP.put("9460", "Oligodendroglial tumors"); //Oligodendroblastoma

    }

    private static final Map<String, String> _CHART2_MAP = new HashMap<>();
    static {
        _CHART2_MAP.put("9540", "Periphera Nerve"); //Malignant peripheral nerve sheath tumor
        _CHART2_MAP.put("9561", "Periphera Nerve"); //Malignant peripheral nerve sheath tumor with rhabdomyoblastic differentiation (MPNST)
        _CHART2_MAP.put("9560", "Periphera Nerve"); //Neurilemoma, malignant
        _CHART2_MAP.put("9571", "Periphera Nerve"); //Perineurioma, malignant
        _CHART2_MAP.put("9100", "Germ Cell Tumors"); //Choriocarcinoma
        _CHART2_MAP.put("9070", "Germ Cell Tumors"); //Embryonal carcionoma
        _CHART2_MAP.put("9064", "Germ Cell Tumors"); //Germinoma
        _CHART2_MAP.put("9080", "Germ Cell Tumors"); //Immature teratoma
        _CHART2_MAP.put("9085", "Germ Cell Tumors"); //Mixed germ cell tumor
        _CHART2_MAP.put("9084", "Germ Cell Tumors"); //Teratoma with malignant transformation
        _CHART2_MAP.put("9071", "Germ Cell Tumors"); //Yolk sac tumor
        _CHART2_MAP.put("9539", "Meningioma, malignant"); //Meningeal sarcomatosis
        _CHART2_MAP.put("9538", "Meningioma, malignant"); //Papillary meningioma, rhadboid meningioma
    }

    public MPGroupMalignantBrain() {
        super("malignant-brain", "Malignant Brain", "C700-C701,C709-C725,C728-C729,C751-C753", null, null, "9590-9989,9140", Arrays.asList("3"));

        // M4 - An invasive brain tumor (/3) and either a benign brain tumor (/0) or an uncertain/borderline brain tumor (/1) are always multiple primaries.
        MPRule rule = new MPRule("malignant-brain", "M4", MPResult.MULTIPLE_PRIMARIES) {
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

        // M5- Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.
        rule = new MPRulePrimarySiteCode("malignant-brain", "M5");
        _rules.add(rule);

        // M6 - A glioblastoma or glioblastoma multiforme (9440) following a glial tumor is a single primary.
        rule = new MPRule("malignant-brain", "M6", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                List<String> glial = Arrays.asList("9380", "9430", "9381", "9423", "9382", "9400", "9401", "9420", "9411", "9410", "9421", "9424", "9440", "9441", "9442");
                int laterDiagnosedTumor = MPGroup.compareDxDate(i1, i2);
                if (-1 == laterDiagnosedTumor) { //If impossible to decide which tumor is diagnosed later
                    result.setResult(RuleResult.UNKNOWN);
                    result.setMessage("Unable to apply Rule" + this.getStep() + " of " + this.getGroupId() + ". Known diagnosis date should be provided.");
                }
                else if (1 == laterDiagnosedTumor && "9440".equals(i1.getHistologyIcdO3()) && glial.contains(i2.getHistologyIcdO3()))
                    result.setResult(RuleResult.TRUE);
                else if (2 == laterDiagnosedTumor && "9440".equals(i2.getHistologyIcdO3()) && glial.contains(i1.getHistologyIcdO3()))
                    result.setResult(RuleResult.TRUE);
                else
                    result.setResult(RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Is there a glioblastoma or glioblastoma multiforme (9440) following a glial tumor (See Chart 1) ?");
        rule.setReason("A glioblastoma or glioblastoma multiforme (9440) following a glial tumor is a single primary.");
        _rules.add(rule);

        // M7 - Tumors with ICD-O-3 histology codes on the same branch in Chart 1 or Chart 2 are a single primary.
        rule = new MPRule("malignant-brain", "M7", MPResult.SINGLE_PRIMARY) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String branch1 = _CHART1_MAP.get(i1.getHistologyIcdO3()), branch2 = _CHART1_MAP.get(i2.getHistologyIcdO3());
                if (branch1 != null && branch2 != null && (branch1.equals(branch2) || "Neuroepithelial".equals(branch1) || "Neuroepithelial".equals(branch2))) {
                    result.setResult(RuleResult.TRUE);
                    return result;
                }
                branch1 = _CHART2_MAP.get(i1.getHistologyIcdO3());
                branch2 = _CHART2_MAP.get(i2.getHistologyIcdO3());
                result.setResult((branch1 != null && branch1.equals(branch2)) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Do the tumors have ICD-O-3 histology on the same branch in Chart 1 or Chart 2?");
        rule.setReason("Tumors with ICD-O-3 histology codes on the same branch in Chart 1 or Chart 2 are a single primary.");
        rule.getNotes().add("Recurrence, progression, or any reappearance of histologies on the same branch in Chart 1 or Chart 2 is always the same disease process.");
        rule.getExamples().add("Patient has an astrocytoma. Ten years later the patient is diagnosed with glioblastoma multiforme. This is a progression or recurrence of the earlier astrocytoma.");
        _rules.add(rule);

        // M8 - Tumors with ICD-O-3 histology codes on different branches in Chart 1 or Chart 2 are multiple primaries.
        rule = new MPRule("malignant-brain", "M8", MPResult.MULTIPLE_PRIMARIES) {
            @Override
            public MPRuleResult apply(MPInput i1, MPInput i2) {
                MPRuleResult result = new MPRuleResult();
                String branch1 = _CHART1_MAP.get(i1.getHistologyIcdO3()), branch2 = _CHART1_MAP.get(i2.getHistologyIcdO3());
                if (branch1 != null && branch2 != null && !branch1.equals(branch2) && !"Neuroepithelial".equals(branch1) && !"Neuroepithelial".equals(branch2)) {
                    result.setResult(RuleResult.TRUE);
                    return result;
                }
                branch1 = _CHART2_MAP.get(i1.getHistologyIcdO3());
                branch2 = _CHART2_MAP.get(i2.getHistologyIcdO3());
                result.setResult((branch1 != null && branch2 != null && !branch1.equals(branch2)) ? RuleResult.TRUE : RuleResult.FALSE);
                return result;
            }
        };
        rule.setQuestion("Do the tumors have ICD-O-3 histology codes on different branches in Chart 1 or Chart 2?");
        rule.setReason("Tumors with ICD-O-3 histology codes on different branches in Chart 1 or Chart 2 are multiple primaries.");
        _rules.add(rule);

        // M9- Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.        
        rule = new MPRuleHistologyCode("malignant-brain", "M9");
        _rules.add(rule);

        // M10- Tumors that do not meet any of the criteria are abstracted as a single primary.
        rule = new MPRuleNoCriteriaSatisfied("malignant-brain", "M10");
        rule.getNotes().add("Multicentric brain tumors which involve different lobes of the brain that do not meet any of the above criteria are the same disease process.");
        rule.getNotes().add("Neither timing nor laterality is used to determine multiple primaries for malignant intracranial and CNS tumors.");
        rule.getExamples().add(
                "The patient is treated for an anaplastic astrocytoma (9401) in the right parietal lobe. Three months later the patient is diagnosed with a separate anaplastic astrocytoma in the left parietal lobe. This is one primary because laterality is not used to determine multiple primary status.");
        _rules.add(rule);
    }
}
