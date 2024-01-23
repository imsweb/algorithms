/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tumorsizeovertime;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class TumorSizeOverTimeUtils {

    public static final String ALG_NAME = "Tumor Size Over Time";
    public static final String ALG_VERSION = "version 1.0 released in January 2024";

    public static final String TUMOR_SIZE_NOT_AVAILABLE = "XX1";
    public static final int INVALID_INPUT_VALUE = -1;

    private TumorSizeOverTimeUtils() {
        // no instances of this class allowed!
    }

    public static String computeTumorSizeOverTime(TumorSizeOverTimeInputDto input) {
        if (input == null || StringUtils.isBlank(input.getTumorSize()))
            return null;
        int hist = NumberUtils.toInt(input.getHist(), INVALID_INPUT_VALUE);
        int site = input.getSite() == null || input.getSite().length() < 4 ? INVALID_INPUT_VALUE : NumberUtils.toInt(input.getSite().substring(1), INVALID_INPUT_VALUE);

        //Updated Logic.Apply First tab
        if (tumorSizeNotAvailable(hist, site))
            return TUMOR_SIZE_NOT_AVAILABLE;

        //Applying Tumor Size Definitions.Updated sheet
        String size = input.getTumorSize();
        int sizeInt = NumberUtils.toInt(input.getTumorSize(), INVALID_INPUT_VALUE);
        int dxYear = input.getDxYear();
        String result = size;
        //All years all sites 401-989 -> 999
        if (sizeInt >= 401 && sizeInt <= 989)
            result = "999";
        else if (dxYear >= 1988 && dxYear <= 2003) {
            if ("990".equals(size))
                result = "989";
            else if ("001".equals(size))
                result = "990";
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
                else if (sizeInt == 997 || sizeInt == 998)
                    result = "999";
            }
        }

        //998 special code
        if ("998".equals(result) && !valid998(hist, site, input.getBehavior()))
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
}
