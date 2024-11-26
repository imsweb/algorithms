package com.imsweb.algorithms.seersiterecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.internal.Utils;

/**
 * This class can be used to calculate the SEER Site Recode variable.
 * User: depryf
 * Date: 8/22/12
 */
public final class SeerSiteRecodeUtils {

    // algorithm name
    public static final String ALG_NAME = "SEER Site Recode ICD-O-3";

    // version for the 2023 data (https://seer.cancer.gov/siterecode/icdo3_2023/)
    public static final String VERSION_2023 = "2023 Revision";

    // version for the 2023 expanded data (https://seer.cancer.gov/siterecode/icdo3_2023_expanded/)
    public static final String VERSION_2023_EXPANDED = "2023 Revision Expanded";

    // version for the 2010+ data (https://seer.cancer.gov/siterecode/icdo3_dwhoheme/index.html)
    public static final String VERSION_2008 = "WHO 2008 Definition";

    // version for the 2003 data (https://seer.cancer.gov/siterecode/icdo3_d01272003/)
    public static final String VERSION_2003 = "2003 Definition";

    // version for the 2003 data without Mesothelioma (9050-9055) and Kaposi Sarcoma (9140) as separate groupings
    public static final String VERSION_2003_WITHOUT_KSM = "2003 Definition without Mesothelioma and Kaposi Sarcoma";

    // cached versions
    private static final Set<String> _VERSIONS = new HashSet<>();

    static {
        _VERSIONS.add(VERSION_2023);
        _VERSIONS.add(VERSION_2023_EXPANDED);
        _VERSIONS.add(VERSION_2008);
        _VERSIONS.add(VERSION_2003);
        _VERSIONS.add(VERSION_2003_WITHOUT_KSM);
    }

    // default version
    public static final String VERSION_DEFAULT = VERSION_2008;

    // unknown value
    public static final String UNKNOWN_RECODE_2008 = "99999";
    public static final String UNKNOWN_RECODE_2023 = "99";

    // unknown label
    public static final String UNKNOWN_LABEL = "Unknown";

    // nice data for the different versions, this is what is exposed to the outside world (lazy)
    private static final Map<String, List<SeerSiteGroupDto>> _DATA = new HashMap<>();

    // optimized data for the different versions, this is what is used for the calculation (lazy)
    private static final Map<String, List<SeerExecutableSiteGroupDto>> _INTERNAL_DATA = new HashMap<>();

    // cached site regex
    private static final Pattern _SITE_PATTERN = Pattern.compile("C\\d+");

    /**
     * Private constructor, no instantiation of this class!
     */
    private SeerSiteRecodeUtils() {}

    /**
     * Returns the available versions, ID is the version key, value is the version description.
     * @return the available versions
     */
    public static Set<String> getAvailableVersions() {
        return Collections.unmodifiableSet(_VERSIONS);
    }

    /**
     * Returns the calculated site recode for the provided parameters, unknown if it can't be calculated.
     * @param version data version
     * @param site site
     * @param histology histology
     * @return the calculated site recode for the provided parameters, unknown if it can't be calculated
     */
    public static String calculateSiteRecode(String version, String site, String histology) {
        return calculateSiteRecode(version, site, histology, null, null);
    }

    /**
     * Returns the calculated site recode for the provided parameters, unknown if it can't be calculated.
     * @param version data version
     * @param site site
     * @param histology histology
     * @param behavior behavior
     * @param dxYear DX Year
     * @return the calculated site recode for the provided parameters, unknown if it can't be calculated
     */
    public static String calculateSiteRecode(String version, String site, String histology, String behavior, String dxYear) {
        String result;
        if (VERSION_2008.equals(version) || VERSION_2003.equals(version) || VERSION_2003_WITHOUT_KSM.equals(version))
            result = UNKNOWN_RECODE_2008;
        else if (VERSION_2023.equals(version) || VERSION_2023_EXPANDED.equals(version))
            result = UNKNOWN_RECODE_2023;
        else
            throw new IllegalStateException("Unsupported version: " + version);

        // site/hist are required
        if (StringUtils.isBlank(site) || !_SITE_PATTERN.matcher(site).matches() || !NumberUtils.isDigits(histology))
            return result;

        // beh/dxYear are required for newer algorithms
        if ((VERSION_2023.equals(version) || VERSION_2023_EXPANDED.equals(version)) && (!NumberUtils.isDigits(behavior) || !NumberUtils.isDigits(dxYear)))
            return result;

        loadDataIfNeeded(version);

        Integer s = Integer.valueOf(site.substring(1));
        Integer h = Integer.valueOf(histology);
        Integer b = NumberUtils.isDigits(behavior) ? Integer.valueOf(behavior) : null;
        Integer y = NumberUtils.isDigits(dxYear) ? Integer.valueOf(dxYear) : null;

        for (SeerExecutableSiteGroupDto dto : _INTERNAL_DATA.get(version)) {
            if (dto.matches(s, h, b, y)) {
                result = dto.getRecode();
                break;
            }
        }

        return result;
    }

    /**
     * Returns the recode name for the provided recode and the default version of the data.
     * @param recode recode
     * @return the corresponding recode name, UNKNOWN_LABEL if it can't be found.
     */
    public static String getRecodeName(String recode) {
        return getRecodeName(recode, VERSION_DEFAULT);
    }

    /**
     * Returns the  recode name for the provided recode and data version.
     * @param recode recode
     * @param version data version
     * @return the corresponding recode name, UNKNOWN_LABEL if it can't be found.
     */
    public static String getRecodeName(String recode, String version) {
        String result = UNKNOWN_LABEL;

        if (!NumberUtils.isDigits(recode))
            return result;

        loadDataIfNeeded(version);

        for (SeerExecutableSiteGroupDto dto : _INTERNAL_DATA.get(version)) {
            if (recode.equals(dto.getRecode())) {
                result = dto.getName();
                break;
            }
        }

        return result;
    }

    public static List<SeerSiteGroupDto> getRawData(String version) {
        loadDataIfNeeded(version);

        return _DATA.get(version);
    }

    private static synchronized void loadDataIfNeeded(String version) {
        if (_DATA.containsKey(version))
            return;

        String file;
        if (VERSION_2023.equals(version))
            file = "seersiterecode/site-recode-data-2023.csv";
        else if (VERSION_2023_EXPANDED.equals(version))
            file = "seersiterecode/site-recode-data-2023-expanded.csv";
        else if (VERSION_2008.equals(version))
            file = "seersiterecode/site-recode-data-2008.csv";
        else if (VERSION_2003.equals(version))
            file = "seersiterecode/site-recode-data-2003.csv";
        else if (VERSION_2003_WITHOUT_KSM.equals(version))
            file = "seersiterecode/site-recode-data-2003-without-kms.csv";
        else
            throw new IllegalStateException("Unsupported version: " + version);

        List<SeerSiteGroupDto> groups = new ArrayList<>();
        _DATA.put(version, groups);

        List<SeerExecutableSiteGroupDto> executables = new ArrayList<>();
        _INTERNAL_DATA.put(version, executables);

        boolean newFormat = VERSION_2023.equals(version) || VERSION_2023_EXPANDED.equals(version);

        Set<String> names = new HashSet<>();
        Utils.processInternalFile(file, line -> {
            String id = line.getField(0).isEmpty() ? null : line.getField(0);
            String name = line.getField(1).isEmpty() ? null : line.getField(1);
            String level = line.getField(2).isEmpty() ? "0" : line.getField(2);
            String siteIn = line.getField(3).isEmpty() ? null : line.getField(3);

            String siteOut = null;
            String histIn;
            String histOut;
            String behIn = null;
            String yearMin = null;
            String yearMax = null;
            String recode;
            String children = null;
            if (newFormat) {
                histIn = line.getField(4).isEmpty() ? null : line.getField(4);
                histOut = line.getField(5).isEmpty() ? null : line.getField(5);
                behIn = line.getField(6).isEmpty() ? null : line.getField(6);
                yearMin = line.getField(7).isEmpty() ? null : line.getField(7);
                yearMax = line.getField(8).isEmpty() ? null : line.getField(8);
                recode = line.getField(9).isEmpty() ? null : line.getField(9);
            }
            else {
                siteOut = line.getField(4).isEmpty() ? null : line.getField(4);
                histIn = line.getField(5).isEmpty() ? null : line.getField(5);
                histOut = line.getField(6).isEmpty() ? null : line.getField(6);
                recode = line.getField(7).isEmpty() ? null : line.getField(7);
                children = line.getField(8).isEmpty() ? null : line.getField(8);
            }

            if (!names.contains(name) || newFormat) {
                SeerSiteGroupDto group = new SeerSiteGroupDto();
                group.setId(id);
                group.setName(name);
                group.setLevel(Integer.valueOf(level));
                group.setSiteInclusions(siteIn);
                group.setSiteExclusions(siteOut);
                group.setHistologyInclusions(histIn);
                group.setHistologyExclusions(histOut);
                group.setBehaviorInclusions(behIn);
                group.setMinDxYear(yearMin);
                group.setMaxDxYear(yearMax);
                group.setRecode(recode);
                if (children != null)
                    group.setChildrenRecodes(Arrays.asList(children.split(",")));
                groups.add(group);
                names.add(name);
            }

            if (recode != null) {
                SeerExecutableSiteGroupDto executable = new SeerExecutableSiteGroupDto();
                executable.setId(id);
                executable.setName(name);
                executable.setSiteInclusions(Utils.expandSitesAsIntegers(siteIn));
                executable.setSiteExclusions(Utils.expandSitesAsIntegers(siteOut));
                executable.setHistologyInclusions(Utils.expandHistologiesAsIntegers(histIn));
                executable.setHistologyExclusions(Utils.expandHistologiesAsIntegers(histOut));
                executable.setBehaviorInclusions(Utils.expandBehaviorsAsIntegers(behIn));
                if (NumberUtils.isDigits(yearMin))
                    executable.setMinDxYear(Integer.valueOf(yearMin));
                if (NumberUtils.isDigits(yearMax))
                    executable.setMaxDxYear(Integer.valueOf(yearMax));
                executable.setRecode(recode);
                executables.add(executable);
            }
        });
    }
}
