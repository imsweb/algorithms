/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tumorsizeovertime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.internal.Utils;

public final class TumorSizeOverTimeUtils {

    public static final String ALG_NAME = "SEER Tumor Size Over Time";
    public static final String ALG_VERSION = "version 1.1 released in March 2024";

    public static final String TUMOR_SIZE_NOT_AVAILABLE = "XX1";

    public static final int INVALID_INPUT_VALUE = -1;

    // internal lock to control concurrency to the data
    private static final ReentrantReadWriteLock _LOCK = new ReentrantReadWriteLock();

    private static Map<String, List<Object>> _SITE_SIZE_MAP;

    private TumorSizeOverTimeUtils() {
        // no instances of this class allowed!
    }

    @SuppressWarnings({"IfCanBeSwitch", "ConstantValue"})
    public static String computeTumorSizeOverTime(TumorSizeOverTimeInputDto input) {
        if (input == null)
            return null;
        int hist = NumberUtils.toInt(input.getHist(), INVALID_INPUT_VALUE);
        int site = input.getSite() == null || input.getSite().length() < 4 ? INVALID_INPUT_VALUE : NumberUtils.toInt(input.getSite().substring(1), INVALID_INPUT_VALUE);

        //Updated Logic.Apply First tab
        if (tumorSizeNotAvailable(hist, site))
            return TUMOR_SIZE_NOT_AVAILABLE;

        int dxYear = NumberUtils.toInt(input.getDxYear(), INVALID_INPUT_VALUE);
        String size = null;
        if (dxYear >= 1988 && dxYear <= 2003)
            size = input.getEodTumorSize();
        else if (dxYear >= 2004 && dxYear <= 2015)
            size = input.getCsTumorSize();
        else if (dxYear > 2015)
            size = input.getTumorSizeSummary();

        if (StringUtils.isBlank(size))
            return null;
        //Applying Tumor Size Definitions.Updated sheet
        int sizeInt = NumberUtils.toInt(size, INVALID_INPUT_VALUE);
        String result = size;
        //All years all sites 401-989 -> 999
        if (sizeInt >= 401 && sizeInt <= 989)
            result = "999";
        else if (dxYear >= 1988 && dxYear <= 2003) {
            if ("990".equals(size))
                result = "989";
            else if ("001".equals(size))
                result = "990";
            else if ("002".equals(size) && ((site >= 340 && site <= 349) || (site >= 500 && site <= 509)))
                result = "999";
            else if ("997".equals(size) && site >= 500 && site <= 509)
                result = "999";
        }
        else if (dxYear >= 2004 && dxYear <= 2015) {
            //Shared across all definitions
            if ("991".equals(size))
                result = "005";
            else if ("992".equals(size))
                result = "015";
            else if ("993".equals(size))
                result = "025";
            else if ("994".equals(size))
                result = "035";
            else if ("995".equals(size))
                result = "045";

            //Tumor definition 4, /*Row 4 Column E*/
            if (site >= 340 && site <= 349) {
                if ("996".equals(size))
                    result = "999";
                else if ("997".equals(size))
                    result = "998";
            }
            //Tumor definition 5, /*Row 5 Column E*/
            else if ((site >= 400 && site <= 403) || (site >= 408 && site <= 409) || (site >= 410 && site <= 414) || (site >= 418 && site <= 419)) {
                if ("996".equals(size))
                    result = "065";
                else if ("997".equals(size))
                    result = "085";
            }
            //Tumor definition 6, /*Row 6 Column E*/
            else if (dxYear >= 2010 && ((site >= 150 && site <= 209) || site == 480 || site == 482 || site == 488) && (hist >= 8935 && hist <= 8936)) {
                if ("996".equals(size))
                    result = "075";
                else if ("997".equals(size))
                    result = "105";
            }
            //Tumor definition 7, /*Row 7 Column E*/
            else if (site >= 500 && site <= 509) {
                if (sizeInt == 996 || sizeInt == 997)
                    result = "999";
            }
            //Tumor definition 8, /*Row 8 Column E*/
            else if (site == 649) {
                if ("996".equals(size))
                    result = "065";
                else if ("997".equals(size))
                    result = "075";
                else if ("998".equals(size))
                    result = "105";
            }
            //Tumor definition 1, /*Row 2 Column E*/
            else {
                if ("996".equals(size))
                    result = "055";
                else if ("997".equals(size))
                    result = "999";
            }
        }

        //998 special code
        if ("998".equals(result) && !valid998(hist, site, input.getBehavior()))
            result = "999";

        if (!"998".equals(result) && !"999".equals(result) && !isValidTumorSize(input.getSite(), NumberUtils.toInt(result, INVALID_INPUT_VALUE)))
            result = "999";
        return result;
    }

    public static boolean tumorSizeNotAvailable(int histology, int site) {
        return (histology >= 8000 && histology <= 9993 && ((site >= 420 && site <= 424) || site == 690 || (site >= 760 && site <= 779) || site == 809)) ||
               (histology >= 8720 && histology <= 8790) || (histology >= 9590 && histology <= 9993) || histology == 9140;
    }

    public static boolean valid998(int hist, int site, String behavior) {
        return (((site >= 180 && site <= 189) || site == 199 || site == 209) && (hist == 8220 || hist == 8221) && "3".equals(behavior)) ||
               (((site >= 150 && site <= 169) || (site >= 340 && site <= 349) || (site >= 500 && site <= 509)) && !((hist >= 8720 && hist <= 8790) || hist == 9140 || (hist >= 9590 && hist <= 9993)));

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean isValidTumorSize(String site, Integer size) {
        if (size == null || StringUtils.isBlank(site))
            return false;

        _LOCK.readLock().lock();
        try {
            if (_SITE_SIZE_MAP == null) {
                _LOCK.readLock().unlock();
                _LOCK.writeLock().lock();
                try {
                    _SITE_SIZE_MAP = readData();
                }
                finally {
                    _LOCK.writeLock().unlock();
                    _LOCK.readLock().lock();
                }
            }
            List<Object> validSizes = _SITE_SIZE_MAP.getOrDefault(site, Collections.emptyList());
            for (Object validSize : validSizes) {
                if (validSize instanceof Integer && size.equals(validSize))
                    return true;
                if (validSize instanceof Range && ((Range)validSize).contains(size))
                    return true;
            }
            //if site is not defined in the override list, return true
            return validSizes.isEmpty();
        }
        finally {
            _LOCK.readLock().unlock();
        }
    }

    private static Map<String, List<Object>> readData() {
        Map<String, List<Object>> map = new HashMap<>();

        Utils.processInternalFileNoHeaders("tumorsizeovertime/edits.csv", line -> {
            String site = line.getField(0).substring(0, 4);
            List<Object> sizes = getSizeList(line.getField(1));
            map.put(site, sizes);
        });

        return map;
    }

    private static List<Object> getSizeList(String size) {
        List<Object> sizes = new ArrayList<>();
        if ("Not enough cases".equals(size))
            sizes.add(Range.of(0, 999));
        else if ("Tumor size always 999".equals(size))
            sizes.add(999);
        else {
            for (String token : StringUtils.split(size, ',')) {
                token = token.trim();
                if (token.contains("-")) {
                    int start = NumberUtils.toInt(StringUtils.split(token, '-')[0], INVALID_INPUT_VALUE);
                    int end = NumberUtils.toInt(StringUtils.split(token, '-')[1], INVALID_INPUT_VALUE);
                    sizes.add(Range.of(start, end));
                }
                else
                    sizes.add(NumberUtils.toInt(token, INVALID_INPUT_VALUE));
            }
        }
        return sizes;

    }
}
