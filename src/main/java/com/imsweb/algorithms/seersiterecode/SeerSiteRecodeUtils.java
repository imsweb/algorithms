package com.imsweb.algorithms.seersiterecode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import com.imsweb.algorithms.internal.Utils;

/**
 * This class can be used to calculate the SEER Site Recode variable.
 * User: depryf
 * Date: 8/22/12
 */
public final class SeerSiteRecodeUtils {

    // algorithm name
    public static final String ALG_NAME = "SEER Site Recode";

    // version for the 2010+ data (https://seer.cancer.gov/siterecode/icdo3_dwhoheme/index.html)
    public static final String VERSION_2010 = "2010+";
    public static final String VERSION_2010_INFO = "SEER Site Recode ICD-O-3 2010+ Cases WHO Heme Definition";

    // version for the 2003 data (https://seer.cancer.gov/siterecode/icdo3_d01272003/)
    public static final String VERSION_2003 = "2003-27-01";
    public static final String VERSION_2003_INFO = "SEER Site Recode ICD-O-3 (1/27/2003) Definition";

    // version for the 2003 data without Mesothelioma (9050-9055) and Kaposi Sarcoma (9140) as separate groupings
    public static final String VERSION_2003_WITHOUT_KSM = "2003-27-01 (no Meso and Kapo)";
    public static final String VERSION_2003_WITHOUT_KSM_INFO = "SEER Site Recode ICD-O-3 (1/27/2003) Definition without Mesothelioma (9050-9055) and Kaposi Sarcoma (9140)";

    // cached versions
    private static final Map<String, String> _VERSIONS = new HashMap<>();

    static {
        _VERSIONS.put(VERSION_2010, VERSION_2010_INFO);
        _VERSIONS.put(VERSION_2003, VERSION_2003_INFO);
        _VERSIONS.put(VERSION_2003_WITHOUT_KSM, VERSION_2003_WITHOUT_KSM_INFO);
    }

    // default version
    public static final String VERSION_DEFAULT = VERSION_2010;

    // unknown value
    public static final String UNKNOWN_RECODE = "99999";

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
    public static Map<String, String> getAvailableVersions() {
        return Collections.unmodifiableMap(_VERSIONS);
    }

    /**
     * Returns the calculated site recode for the provided parameters, unknown if it can't be calculated.
     * @param version data version
     * @param site site
     * @param histology histology
     * @return the calculated site recode for the provided parameters, unknown if it can't be calculated
     */
    public static String calculateSiteRecode(String version, String site, String histology) {
        String result = UNKNOWN_RECODE;

        if (StringUtils.isBlank(site) || !_SITE_PATTERN.matcher(site).matches() || StringUtils.isBlank(histology) || !NumberUtils.isDigits(histology))
            return result;

        ensureVersion(version);

        Integer s = Integer.valueOf(site.substring(1)), h = Integer.valueOf(histology);

        for (SeerExecutableSiteGroupDto dto : _INTERNAL_DATA.get(version)) {
            if (dto.matches(s, h)) {
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

        if (StringUtils.isBlank(recode) || !NumberUtils.isDigits(recode))
            return result;

        ensureVersion(version);

        for (SeerExecutableSiteGroupDto dto : _INTERNAL_DATA.get(version)) {
            if (recode.equals(dto.getRecode())) {
                result = dto.getName();
                break;
            }
        }

        return result;
    }

    public static List<SeerSiteGroupDto> getRawData(String version) {
        ensureVersion(version);

        return _DATA.get(version);
    }

    private static synchronized void ensureVersion(String version) {
        if (_DATA.containsKey(version))
            return;

        URL url;
        if (VERSION_2010.equals(version))
            url = Thread.currentThread().getContextClassLoader().getResource("seersiterecode/site-recode-data-2010.csv");
        else if (VERSION_2003.equals(version))
            url = Thread.currentThread().getContextClassLoader().getResource("seersiterecode/site-recode-data-2003.csv");
        else if (VERSION_2003_WITHOUT_KSM.equals(version))
            url = Thread.currentThread().getContextClassLoader().getResource("seersiterecode/site-recode-data-2003-without-kms.csv");
        else
            throw new RuntimeException("Unsupported version: " + version);

        if (url == null)
            throw new RuntimeException("Unable to find internal data file for version " + version);

        List<SeerSiteGroupDto> groups = new ArrayList<>();
        _DATA.put(version, groups);

        List<SeerExecutableSiteGroupDto> executables = new ArrayList<>();
        _INTERNAL_DATA.put(version, executables);

        try {
            Set<String> names = new HashSet<>();
            List<String[]> allData = new CSVReader(new InputStreamReader(url.openStream(), StandardCharsets.US_ASCII)).readAll();
            for (int i = 1; i < allData.size(); i++) {
                String[] data = allData.get(i);

                String id = data[0].isEmpty() ? null : data[0];
                String name = data[1].isEmpty() ? null : data[1];
                String level = data[2].isEmpty() ? "0" : data[2];
                String siteIn = data[3].isEmpty() ? null : data[3];
                String siteOut = data[4].isEmpty() ? null : data[4];
                String histIn = data[5].isEmpty() ? null : data[5];
                String histOut = data[6].isEmpty() ? null : data[6];
                String recode = data[7].isEmpty() ? null : data[7];
                String children = data[8].isEmpty() ? null : data[8];

                if (!names.contains(name)) {
                    SeerSiteGroupDto group = new SeerSiteGroupDto();
                    group.setId(id);
                    group.setName(name);
                    group.setLevel(Integer.valueOf(level));
                    group.setSiteInclusions(siteIn);
                    group.setSiteExclusions(siteOut);
                    group.setHistologyInclusions(histIn);
                    group.setHistologyExclusions(histOut);
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
                    executable.setRecode(recode);
                    executables.add(executable);
                }

            }
        }
        catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
