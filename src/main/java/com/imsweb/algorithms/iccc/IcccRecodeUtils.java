package com.imsweb.algorithms.iccc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;

import com.imsweb.algorithms.AlgorithmsUtils;

/**
 * This class can be used to calculate the ICCC Recode variable.
 * User: depryf
 * Date: 8/22/12
 */
public final class IcccRecodeUtils {

    // algorithm name
    public static final String ALG_NAME = "Main Classification from the International Classification of Childhood Cancer";

    // properties used by the algorithm
    public static final String PROP_PRIMARY_SITE = "primarySite";
    public static final String PROP_HISTOLOGY_3 = "histologyIcdO3";
    public static final String PROP_BEHAVIOR_3 = "behaviorIcdO3";

    // version for the 2010+ data (http://seer.cancer.gov/iccc/iccc3.html)
    public static final String VERSION_THIRD_EDITION = "Third Edition";
    public static final String VERSION_THIRD_EDITION_INFO = "Main Classification from the International Classification of Childhood Cancer, Third edition (ICCC-3) based on ICD-O-3";

    // version based on the WHO 2008 classification (http://seer.cancer.gov/iccc/iccc-who2008.html)
    public static final String VERSION_WHO_2008 = "ICD-O-3/WHO 2008";
    public static final String VERSION_WHO_2008_INFO = "Main Classification from the International Classification of Childhood Cancer based on ICD-O-3/WHO 2008";

    // cached versions
    private static final Map<String, String> _VERSIONS = new HashMap<>();

    // default version
    public static final String VERSION_DEFAULT = VERSION_WHO_2008;

    static {
        _VERSIONS.put(VERSION_THIRD_EDITION, VERSION_THIRD_EDITION_INFO);
        _VERSIONS.put(VERSION_WHO_2008, VERSION_WHO_2008_INFO);
    }

    // unknown value
    public static final String ICCC_UNKNOWN_RECODE = "999";

    // cached formatted data
    private static final Map<String, List<IcccSiteGroupDto>> _DATA = new HashMap<>();

    // cached runtime data
    private static final Map<String, List<IcccExecutableSiteGroupDto>> _INTERNAL_DATA = new HashMap<>();

    /**
     * Private constructor, no instanciation of this class!
     */
    private IcccRecodeUtils() {}

    /**
     * Returns the available versions, ID is the version key, value is the version description.
     * @return the available versions
     */
    public static Map<String, String> getAvailableVersions() {
        return Collections.unmodifiableMap(_VERSIONS);
    }

    /**
     * Returns the calculated site recode for the provided record, unknown if it can't be calculated.
     * @param record record
     * @return the calculated site recode for the provided record, unknown if it can't be calculated
     * @deprecated use the methods that takes the explicit site/hist/behav parameters
     */
    public static String calculateSiteRecode(Map<String, String> record) {
        return calculateSiteRecode(VERSION_DEFAULT, record.get(PROP_PRIMARY_SITE), record.get(PROP_HISTOLOGY_3), record.get(PROP_BEHAVIOR_3));
    }

    /**
     * Returns the calculated site recode for the provided version and record, unknown if it can't be calculated.
     * @param version data version
     * @param record record
     * @return the calculated site recode for the provided version and record, unknown if it can't be calculated
     * @deprecated use the methods that takes the explicit site/hist/behav parameters
     */
    public static String calculateSiteRecode(String version, Map<String, String> record) {
        return calculateSiteRecode(version, record.get(PROP_PRIMARY_SITE), record.get(PROP_HISTOLOGY_3), record.get(PROP_BEHAVIOR_3));
    }

    /**
     * Returns the calculated site recode for the provided parameters, unknown if it can't be calculated.
     * @param site site
     * @param histology histology
     * @return the calculated site recode for the provided parameters, unknown if it can't be calculated
     * @deprecated use the version that takes a version as first parameter along with the VERSION_DEFAULT constant.
     */
    public static String calculateSiteRecode(String site, String histology) {
        return calculateSiteRecode(VERSION_DEFAULT, site, histology, null);
    }

    /**
     * Returns the calculated site recode for the provided parameters, unknown if it can't be calculated.
     * @param version data version
     * @param site site
     * @param histology histology
     * @return the calculated site recode for the provided parameters, unknown if it can't be calculated
     * @deprecated use the method that takes the bhavior and pass null for that parameter
     */
    public static String calculateSiteRecode(String version, String site, String histology) {
        return calculateSiteRecode(version, site, histology, null);
    }

    /**
     * Returns the calculated site recode for the provided parameters, unknown if it can't be calculated.
     * @param version data version
     * @param site site
     * @param histology histology
     * @param behavior behavior
     * @return the calculated site recode for the provided parameters, unknown if it can't be calculated
     */
    public static String calculateSiteRecode(String version, String site, String histology, String behavior) {
        return calculateSiteRecode(version, site, histology, behavior, false);
    }

    /**
     * Returns the calculated site recode for the provided parameters, unknown if it can't be calculated.
     * @param version data version
     * @param site site
     * @param histology histology
     * @param behavior behavior
     * @param recodeExtended if true, extended recode value otherwise main recode classification will be calculated
     * @return the calculated site recode/ recode extended for the provided parameters, unknown if it can't be calculated
     */
    public static String calculateSiteRecode(String version, String site, String histology, String behavior, boolean recodeExtended) {
        String result = ICCC_UNKNOWN_RECODE;

        if (StringUtils.isBlank(site) || !site.matches("C\\d+") || StringUtils.isBlank(histology) || !histology.matches("\\d+"))
            return result;

        //Only the WHO 2008 version requires behavior
        if (VERSION_WHO_2008.equals(version) && (StringUtils.isBlank(behavior) || !behavior.matches("\\d+")))
            return result;

        ensureVersion(version);

        Integer s = Integer.valueOf(site.substring(1)), h = Integer.valueOf(histology), b = -1;

        if (!StringUtils.isBlank(behavior))
            b = Integer.valueOf(behavior);

        for (IcccExecutableSiteGroupDto dto : _INTERNAL_DATA.get(version)) {
            if (dto.matches(s, h, b)) {
                result = recodeExtended ? (StringUtils.isEmpty(dto.getRecodeExtended()) ? "999" : dto.getRecodeExtended()) : dto.getRecode();
                break;
            }
        }

        return result;
    }

    public static List<IcccSiteGroupDto> getRawData(String version) {
        ensureVersion(version);

        return _DATA.get(version);
    }

    private static synchronized void ensureVersion(String version) {
        if (_DATA.containsKey(version))
            return;

        URL url;
        if (VERSION_THIRD_EDITION.equals(version))
            url = Thread.currentThread().getContextClassLoader().getResource("iccc/iccc-data-third-edition.csv");
        else if (VERSION_WHO_2008.equals(version))
            url = Thread.currentThread().getContextClassLoader().getResource("iccc/iccc-data-who-2008.csv");
        else
            throw new RuntimeException("Unsupported version: " + version);

        if (url == null)
            throw new RuntimeException("Unable to find internal data file for version " + version);

        List<IcccSiteGroupDto> groups = new ArrayList<>();
        _DATA.put(version, groups);

        List<IcccExecutableSiteGroupDto> executables = new ArrayList<>();
        _INTERNAL_DATA.put(version, executables);

        try {
            List<String[]> allData = new CSVReader(new InputStreamReader(url.openStream(), StandardCharsets.US_ASCII)).readAll();
            for (int i = 1; i < allData.size(); i++) {
                String[] data = allData.get(i);

                String id = data[0];
                String name = data[1];
                String level = data[2];
                String siteIn = data[3];
                String siteOut = data[4];
                String histIn = data[5];
                String histOut = data[6];
                String recode = data[7];
                String children = data[8];
                String recodeExtended = "";
                String behaviorInclusions = "";
                if (VERSION_WHO_2008.equals(version)) {
                    recodeExtended = data[9];
                    behaviorInclusions = data[10];
                }

                IcccSiteGroupDto group = new IcccSiteGroupDto();
                group.setId(id);
                group.setName(name);
                group.setLevel(Integer.valueOf(level));
                group.setSiteInclusions(siteIn);
                group.setSiteExclusions(siteOut);
                group.setHistologyInclusions(histIn);
                group.setHistologyExclusions(histOut);
                group.setBehaviorInclusions(behaviorInclusions);
                group.setRecode(recode);
                group.setRecodeExtended(recodeExtended);
                if (!StringUtils.isBlank(children))
                    group.setChildrenRecodes(Arrays.asList(children.split(",")));

                if (!groups.contains(group))
                    groups.add(group);

                if (!StringUtils.isBlank(recode)) {
                    if (!recode.matches("\\d{3}"))
                        throw new RuntimeException("Invalid recode: " + recode + " for id " + id);
                    if (VERSION_WHO_2008.equals(version) && !recodeExtended.matches("\\d{3}"))
                        throw new RuntimeException("Invalid recode extended: " + recodeExtended + " for id " + id);

                    IcccExecutableSiteGroupDto executable = new IcccExecutableSiteGroupDto();
                    executable.setId(id);
                    executable.setSiteInclusions(AlgorithmsUtils.expandSitesAsIntegers(siteIn));
                    executable.setSiteExclusions(AlgorithmsUtils.expandSitesAsIntegers(siteOut));
                    executable.setHistologyInclusions(AlgorithmsUtils.expandHistologiesAsIntegers(histIn));
                    executable.setHistologyExclusions(AlgorithmsUtils.expandHistologiesAsIntegers(histOut));
                    executable.setBehaviorInclusions(AlgorithmsUtils.expandBehaviorsAsIntegers(behaviorInclusions));
                    executable.setRecode(recode);
                    executable.setRecodeExtended(recodeExtended);

                    executables.add(executable);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
