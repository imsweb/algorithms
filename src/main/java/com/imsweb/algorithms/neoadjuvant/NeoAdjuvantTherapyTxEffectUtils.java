/*
 * Copyright (C) 2021 Information Management Services, Inc.
 */
package com.imsweb.algorithms.neoadjuvant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public final class NeoAdjuvantTherapyTxEffectUtils {

    private static final Set<String> _A_SCHEMA = new HashSet<>(Arrays.asList("00350", "00360", "00381", "00382", "00383", "00400", "00410", "00421", "00422", "00430", "00440", "00450"));
    private static final Set<String> _B_SCHEMA = new HashSet<>(Collections.singletonList("00480"));
    private static final Set<String> _C_SCHEMA = new HashSet<>(Arrays.asList("00161", "00169", "00170", "00200", "00210", "00280"));
    private static final Set<String> _D_SCHEMA = new HashSet<>(Arrays.asList("00551", "00552", "00553"));
    private static final Set<String> _E_SCHEMA = new HashSet<>(Collections.singletonList("00580"));
    private static final Set<String> _G_SCHEMA = new HashSet<>(Arrays.asList("00790", "00795", "00811", "00812", "00821", "00822", "00830", "99999"));

    private static final Map<String, String> _A = new LinkedHashMap<>();
    private static final Map<String, String> _B = new LinkedHashMap<>();
    private static final Map<String, String> _C = new LinkedHashMap<>();
    private static final Map<String, String> _D = new LinkedHashMap<>();
    private static final Map<String, String> _E = new LinkedHashMap<>();
    private static final Map<String, String> _F = new LinkedHashMap<>();
    private static final Map<String, String> _G = new LinkedHashMap<>();

    // Commonly used entries - fixing SonarQube warning
    private static final String ENTRY_0 = "Neoadjuvant therapy not given/no known presurgical therapy";
    private static final String ENTRY_6 = "Neoadjuvant therapy completed and surgical resection performed, response not documented or unknown;\nCannot be determined";
    private static final String ENTRY_7 = "Neoadjuvant therapy completed and planned surgical resection not performed";
    private static final String ENTRY_9
            = "Unknown if neoadjuvant therapy performed;\nUnknown if planned surgical procedure performed after completion of neoadjuvant therapy;\nDeath certificate only (DCO)";

    static {
        _A.put("0", ENTRY_0);
        _A.put("1", "No residual invasive carcinoma identified\nResidual in situ carcinoma only\nStated as Complete response (CR)");
        _A.put("2", "Less than or equal to 10% residual viable tumor");
        _A.put("3", "Greater than 10% of residual viable tumor");
        _A.put("4", "Residual viable tumor, percentage not stated\nStated as partial response");
        _A.put("6", ENTRY_6);
        _A.put("7", ENTRY_7);
        _A.put("9", ENTRY_9);

        _B.put("0", ENTRY_0);
        _B.put("1", "No residual invasive carcinoma present in the breast after presurgical therapy\nResidual in situ carcinoma only\nStated as Complete response (CR)");
        _B.put("3", "Probable or definite response to presurgical therapy in the invasive carcinoma\nStated as Partial response (PR)\nStated as minimal or near complete response");
        _B.put("4", "No definite response to presurgical therapy in the invasive carcinoma\nStated as No response (NR)\nStated as poor response");
        _B.put("6", ENTRY_6);
        _B.put("7", ENTRY_7);
        _B.put("9", ENTRY_9);

        _C.put("0", ENTRY_0);
        _C.put("1", "Present: No viable cancer cells\nComplete response\nScore 0");
        _C.put("2", "Present: Single cells or rare small groups of cancer cells\nNear complete response\nScore 1");
        _C.put("3", "Present: Residual cancer with evident tumor regression, but more than single cells or rare small groups of cancer cells\nPartial response\nMinimal response\nScore 2");
        _C.put("4", "Absent: Extensive residual cancer with no evident tumor regression\nPoor or no response\nScore 3");
        _C.put("6", ENTRY_6);
        _C.put("7", ENTRY_7);
        _C.put("9", ENTRY_9);

        _D.put("0", ENTRY_0);
        _D.put("1", "No definite or minimal response identified\nStated as Chemotherapy response score 1 [CRS1]");
        _D.put("2", "Moderate response identified\nStated as Chemotherapy response score 2 [CRS2]");
        _D.put("3", "Marked response with no or minimal residual cancer\nStated as Chemotherapy response score 3 [CRS3]");
        _D.put("4", "Residual tumor, not specified as minimal, moderate or marked\nNot documented as CRS1, CRS2 or CRS3\nResponse, NOS");
        _D.put("6", ENTRY_6);
        _D.put("7", ENTRY_7);
        _D.put("9", ENTRY_9);

        _E.put("0", ENTRY_0);
        _E.put("1", "No residual invasive carcinoma identified\nResidual in situ carcinoma only\nStated as Complete response (CR)");
        _E.put("2", "Radiation therapy effect present");
        _E.put("3", "Hormonal therapy effect present");
        _E.put("4", "Other therapy effect(s) present");
        _E.put("6", ENTRY_6);
        _E.put("7", ENTRY_7);
        _E.put("9", ENTRY_9);

        _F.put("0", ENTRY_0);
        _F.put("1", "Complete pathological response\nPresent: No viable cancer cells/no residual invasive carcinoma identified\nResidual in situ carcinoma only");
        _F.put("2", "Near complete pathological response Present: Single cells or rare small groups of invasive cancer cells");
        _F.put("3", "Partial or minimal pathological response Present: Residual invasive cancer with evident tumor regression, but more than single cells or rare small groups of cancer cells");
        _F.put("4", "Poor or no pathological response\nAbsent: Extensive residual cancer with no evident tumor regression");
        _F.put("6", ENTRY_6);
        _F.put("7", ENTRY_7);
        _F.put("9", ENTRY_9);

        _G.put("0", "No Neoadjuvant therapy (not applicable)");
        _G.put("9", "Death certificate only (DCO)");
    }

    private static final Set<String> _ALL_ALLOWED_VALUES = new HashSet<>();

    static {
        _ALL_ALLOWED_VALUES.addAll(_A.keySet());
        _ALL_ALLOWED_VALUES.addAll(_B.keySet());
        _ALL_ALLOWED_VALUES.addAll(_C.keySet());
        _ALL_ALLOWED_VALUES.addAll(_D.keySet());
        _ALL_ALLOWED_VALUES.addAll(_E.keySet());
        _ALL_ALLOWED_VALUES.addAll(_F.keySet());
        _ALL_ALLOWED_VALUES.addAll(_G.keySet());
    }

    private NeoAdjuvantTherapyTxEffectUtils() {
        // utility class
    }

    /**
     * Returns the lookup for neoadjuvTherapyTreatmentEffect (#1634) corresponding to the provided schemaId (#3800).
     */
    public static Map<String, String> getLookup(String schemaId) {
        if (StringUtils.isBlank(schemaId))
            return Collections.emptyMap();

        Map<String, String> result;
        if (_A_SCHEMA.contains(schemaId))
            result = _A;
        else if (_B_SCHEMA.contains(schemaId))
            result = _B;
        else if (_C_SCHEMA.contains(schemaId))
            result = _C;
        else if (_D_SCHEMA.contains(schemaId))
            result = _D;
        else if (_E_SCHEMA.contains(schemaId))
            result = _E;
        else if (_G_SCHEMA.contains(schemaId))
            result = _G;
        else
            result = _F;

        return Collections.unmodifiableMap(result);
    }

    /**
     * Returns all the possible values for neoadjuvTherapyTreatmentEffect (#1634) in the different lookups.
     */
    public static Set<String> getAllAllowedValues() {
        return Collections.unmodifiableSet(_ALL_ALLOWED_VALUES);
    }
}