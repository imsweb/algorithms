/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iarc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.imsweb.algorithms.internal.Utils;

/**
 * This class is used to calculate the International Primary indicator variable.
 * <br/><br/>
 * See <a href="http://www.iacr.com.fr/images/doc/MPrules_july2004.pdf">http://www.iacr.com.fr/images/doc/MPrules_july2004.pdf/</a>.
 */
public final class IarcUtils {

    public static final String ALG_NAME = "IARC Multiple Primary Indicator";
    public static final String VERSION = "ICD-O Third Edition released in 2004";

    public static final Integer DUPLICATE = 0;
    public static final Integer PRIMARY = 1;
    public static final Integer INSITU = 9;

    private static final Set<String> _SITE_GROUP_C02 = new HashSet<>(Arrays.asList("C01", "C02"));
    private static final Set<String> _SITE_GROUP_C06 = new HashSet<>(Arrays.asList("C00", "C03", "C04", "C05", "C06"));
    private static final Set<String> _SITE_GROUP_C14 = new HashSet<>(Arrays.asList("C09", "C10", "C12", "C13", "C14"));
    private static final Set<String> _SITE_GROUP_C20 = new HashSet<>(Arrays.asList("C19", "C20"));
    private static final Set<String> _SITE_GROUP_C24 = new HashSet<>(Arrays.asList("C23", "C24"));
    private static final Set<String> _SITE_GROUP_C34 = new HashSet<>(Arrays.asList("C33", "C34"));
    private static final Set<String> _SITE_GROUP_C41 = new HashSet<>(Arrays.asList("C40", "C41"));
    private static final Set<String> _SITE_GROUP_C68 = new HashSet<>(Arrays.asList("C65", "C66", "C67", "C68"));

    private static final List<Pair<List<Object>, Integer>> _HISTOLOGY_RANGES = new ArrayList<>();

    static {
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8051-8084,8120-8131"), 1));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8090-8110"), 2));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8140-8149,8160-8162,8190-8221,8260-8337,8350-8551,8570-8576,8940-8941"), 3));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8030-8046,8150-8157,8170-8180,8230-8255,8340-8347,8560-8562,8580-8671"), 4));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8010-8015,8020-8022,8050"), 5));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8680-8713,8800-8921,8990-8991,9040-9044,9120-9125,9130-9136,9141-9252,9370-9373,9540-9582"), 6));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9050-9055"), 7));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9840,9861-9931,9945-9946,9950,9961-9964,9980-9987,9991-9992"), 8));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9597,9670-9699,9712,9728,9731-9738,9761-9767,9769,9811-9818,9823-9826,9833,9836,9940"), 9));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9700-9726,9729,9768,9827-9831,9834,9837,9948"), 10));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9650-9667"), 11));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9740-9742"), 12));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9750-9759"), 13));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9590-9591,9596,9727,9760,9800-9809,9820,9832,9835,9860,9960,9965-9975,9989"), 14));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("9140"), 15));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8720-8790,8930-8936,8950-8983,9000-9030,9060-9110,9260-9365,9380-9539"), 16));
        _HISTOLOGY_RANGES.add(Pair.of(Utils.expandHistologiesAsIntegers("8000-8005"), 17));
    }

    private IarcUtils() {
        // utility class
    }

    /**
     * Computes the IARC multiple primary flag on all the provided records belonging to the same patient.
     * @param records records to process
     * @return the computed IARC MP flag
     */
    public static List<IarcMpInputRecordDto> calculateIarcMp(List<IarcMpInputRecordDto> records) {
        return calculateIarcMp(records, false);
    }

    /**
     * Computes the IARC multiple primary flag on all the provided records belonging to the same patient.
     * @param records records to process
     * @param flagPrimaryWithDuplicate if set to false (the default), primary tumors will be set to 1=primary whether they have a duplicate or not. Otherwise, they will be set to 1=primary if they have no duplicate and 2=primary-with-duplicate if they have a duplicate.
     * @return the computed IARC MP flag
     */
    public static List<IarcMpInputRecordDto> calculateIarcMp(List<IarcMpInputRecordDto> records, boolean flagPrimaryWithDuplicate) {
        //No records
        if (records == null || records.isEmpty())
            return null;

        //If there is only one tumor
        if (records.size() == 1) {
            if (isInsitu(records.get(0)))
                records.get(0).setInternationalPrimaryIndicator(INSITU);
            else
                records.get(0).setInternationalPrimaryIndicator(PRIMARY);
            return records;
        }

        //Calculate the site group and hist group
        //let's also use a different list so we don't change the order of the original records
        List<InternalRecDto> internalRecords = new ArrayList<>();
        for (IarcMpInputRecordDto rec : records) {
            //Set IARC as primary by default
            rec.setInternationalPrimaryIndicator(PRIMARY);
            rec.setSiteGroup(calculateSiteGroup(rec.getSite()));
            rec.setHistGroup(calculateHistGroup(rec.getHistology()));
            internalRecords.add(new InternalRecDto(rec));
        }

        Collections.sort(internalRecords);

        //Calculate IARC MP variable
        for (int i = 0; i < internalRecords.size(); i++) {
            IarcMpInputRecordDto r1 = internalRecords.get(i).getOriginalRecord();
            if (DUPLICATE.equals(r1.getInternationalPrimaryIndicator()) || INSITU.equals(r1.getInternationalPrimaryIndicator()))
                continue;
            //Insitu cases are ignored except bladder
            if (isInsitu(r1)) {
                r1.setInternationalPrimaryIndicator(INSITU);
                continue;
            }
            for (int j = i + 1; j < internalRecords.size(); j++) {
                IarcMpInputRecordDto r2 = internalRecords.get(j).getOriginalRecord();
                //Insitu cases are ignored except bladder
                if (isInsitu(r2)) {
                    r2.setInternationalPrimaryIndicator(INSITU);
                    continue;
                }

                if (r1.getHistGroup() != null && r2.getHistGroup() != null && r1.getSiteGroup() != null && r2.getSiteGroup() != null) {
                    //Kaposi sarcoma or Hemato, no need to check the site
                    if (isKaposiSarcoma(r1, r2) || isHemato(r1, r2) || (isSameSiteGroup(r1, r2) && (isSameHistGroup(r1, r2) || isNosVsSpecific(r1, r2)))) {
                        r2.setInternationalPrimaryIndicator(DUPLICATE);
                        if (flagPrimaryWithDuplicate)
                            r1.setInternationalPrimaryIndicator(DUPLICATE);
                        if (needToUpdateHistology(r1, r2)) {
                            r1.setHistology(r2.getHistology());
                            r1.setHistGroup(calculateHistGroup(r1.getHistology()));
                        }
                    }
                }
            }
        }

        return records;
    }

    private static String calculateSiteGroup(String site) {
        String siteGroup = site != null && site.length() >= 3 ? site.toUpperCase().substring(0, 3) : null;

        if (siteGroup != null) {
            if (_SITE_GROUP_C02.contains(siteGroup))
                siteGroup = "C02";
            else if (_SITE_GROUP_C06.contains(siteGroup))
                siteGroup = "C06";
            else if (_SITE_GROUP_C14.contains(siteGroup))
                siteGroup = "C14";
            else if (_SITE_GROUP_C20.contains(siteGroup))
                siteGroup = "C20";
            else if (_SITE_GROUP_C24.contains(siteGroup))
                siteGroup = "C24";
            else if (_SITE_GROUP_C34.contains(siteGroup))
                siteGroup = "C34";
            else if (_SITE_GROUP_C41.contains(siteGroup))
                siteGroup = "C41";
            else if (_SITE_GROUP_C68.contains(siteGroup))
                siteGroup = "C68";
        }

        return siteGroup;
    }

    private static Integer calculateHistGroup(String histology) {
        Integer histologyGroup = null;

        if (NumberUtils.isDigits(histology)) {
            int hist = NumberUtils.toInt(histology);
            for (Pair<List<Object>, Integer> pair : _HISTOLOGY_RANGES) {
                if (Utils.isContained(pair.getLeft(), hist)) {
                    histologyGroup = pair.getRight();
                    break;
                }
            }
        }

        return histologyGroup;
    }

    private static boolean isInsitu(IarcMpInputRecordDto rec) {
        return !"3".equals(rec.getBehavior()) && !(rec.getSite() != null && rec.getSite().toUpperCase().startsWith("C67"));
    }

    private static boolean isKaposiSarcoma(IarcMpInputRecordDto rec1, IarcMpInputRecordDto rec2) {
        return rec1.getHistGroup() == 15 && rec2.getHistGroup() == 15;
    }

    private static boolean isHemato(IarcMpInputRecordDto rec1, IarcMpInputRecordDto rec2) {
        return rec1.getHistGroup() >= 8 && rec1.getHistGroup() <= 14 && rec2.getHistGroup() >= 8 && rec2.getHistGroup() <= 14 && (rec1.getHistGroup() == 14 || rec2.getHistGroup() == 14 || rec1
                .getHistGroup().equals(rec2.getHistGroup()));
    }

    private static boolean isSameSiteGroup(IarcMpInputRecordDto rec1, IarcMpInputRecordDto rec2) {
        return rec1.getSiteGroup().equals(rec2.getSiteGroup());
    }

    private static boolean isSameHistGroup(IarcMpInputRecordDto rec1, IarcMpInputRecordDto rec2) {
        return rec1.getHistGroup().equals(rec2.getHistGroup());
    }

    private static boolean isNosVsSpecific(IarcMpInputRecordDto rec1, IarcMpInputRecordDto rec2) {
        //17 is NOS for 1-7, 15 and 16
        //14 is NOS for 8-13
        //5 is NOS for 1-4
        return (rec1.getHistGroup() == 17 && ((rec2.getHistGroup() >= 1 && rec2.getHistGroup() <= 7) || rec2.getHistGroup() == 15 || rec2.getHistGroup() == 16)) ||
                (rec2.getHistGroup() == 17 && ((rec1.getHistGroup() >= 1 && rec1.getHistGroup() <= 7) || rec1.getHistGroup() == 15 || rec1.getHistGroup() == 16)) ||
                (rec1.getHistGroup() == 14 && rec2.getHistGroup() >= 8 && rec2.getHistGroup() <= 13) ||
                (rec2.getHistGroup() == 14 && rec1.getHistGroup() >= 8 && rec1.getHistGroup() <= 13) ||
                (rec1.getHistGroup() == 5 && rec2.getHistGroup() >= 1 && rec2.getHistGroup() <= 4) ||
                (rec2.getHistGroup() == 5 && rec1.getHistGroup() >= 1 && rec1.getHistGroup() <= 4);
    }

    private static boolean needToUpdateHistology(IarcMpInputRecordDto primaryRec, IarcMpInputRecordDto dupRecord) {
        return (primaryRec.getHistGroup() == 17 && ((dupRecord.getHistGroup() >= 1 && dupRecord.getHistGroup() <= 7) || dupRecord.getHistGroup() == 15 || dupRecord.getHistGroup() == 16)) ||
                (primaryRec.getHistGroup() == 14 && dupRecord.getHistGroup() >= 8 && dupRecord.getHistGroup() <= 13) ||
                (primaryRec.getHistGroup() == 5 && dupRecord.getHistGroup() >= 1 && dupRecord.getHistGroup() <= 4) ||
                (primaryRec.getHistGroup().equals(dupRecord.getHistGroup()) && NumberUtils.toInt(primaryRec.getHistology()) < NumberUtils.toInt(dupRecord.getHistology()));
    }

    private static class InternalRecDto implements Comparable<InternalRecDto> {

        IarcMpInputRecordDto _originalRecord;
        int _year;
        int _month;
        int _day;
        int _seqNum;

        public InternalRecDto(IarcMpInputRecordDto rec) {
            _originalRecord = rec;

            String yearStr = rec.getDateOfDiagnosisYear();
            String monthStr = rec.getDateOfDiagnosisMonth();
            String dayStr = rec.getDateOfDiagnosisDay();
            _year = NumberUtils.isDigits(yearStr) ? Integer.parseInt(yearStr) : 9999;
            _month = NumberUtils.isDigits(monthStr) ? Integer.parseInt(monthStr) : 99;
            _day = NumberUtils.isDigits(dayStr) ? Integer.parseInt(dayStr) : 99;
            if (_month == 99)
                _day = 99;
            _seqNum = rec.getSequenceNumber() == null ? -1 : rec.getSequenceNumber();
            //    the sequence numbers might be used to determine the order; there are two families of sequences: federal (00-59, 98, 99) and non-federal (60-97);
            //    sine the non-federal need to always be after the federal, let's add 100 to all the non-federal (making them 160-197)
            if (_seqNum >= 60 && _seqNum <= 97)
                _seqNum += 100;
        }

        public IarcMpInputRecordDto getOriginalRecord() {
            return _originalRecord;
        }

        @Override
        public int compareTo(InternalRecDto other) {
            if (_year == 9999 || other._year == 9999)
                return _seqNum - other._seqNum;
            else if (_year != other._year)
                return _year - other._year;
            else {
                if (_month == 99 || other._month == 99)
                    return _seqNum - other._seqNum;
                else if (_month != other._month)
                    return _month - other._month;
                else {
                    if (_day == 99 || other._day == 99 || this._day == other._day)
                        return _seqNum - other._seqNum;
                    else
                        return _day - other._day;
                }
            }
        }

        @Override
        @SuppressWarnings("SimplifiableIfStatement")
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            InternalRecDto other = (InternalRecDto)o;

            if (_year == 9999 || other._year == 9999)
                return _seqNum == other._seqNum;
            else if (_year != other._year)
                return false;
            else {
                if (_month == 99 || other._month == 99)
                    return _seqNum == other._seqNum;
                else if (_month != other._month)
                    return false;
                else {
                    if (_day == 99 || other._day == 99 || this._day == other._day)
                        return _seqNum == other._seqNum;
                    else
                        return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int result = _year;
            result = 31 * result + _month;
            result = 31 * result + _day;
            result = 31 * result + _seqNum;
            return result;
        }
    }
}
