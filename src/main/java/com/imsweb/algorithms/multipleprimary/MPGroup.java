/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;
import com.imsweb.algorithms.multipleprimary.MPUtils.RuleResult;

public abstract class MPGroup {

    // List of specific histologies for a given NOS, this list is used in few of the groups
    private static final Map<String, String> _NOSVSSPECIFIC_MAP = new HashMap<>();
    static {
        _NOSVSSPECIFIC_MAP.put("8000", "8001, 8002, 8003, 8004, 8005"); //Cancer/malignant neoplasm, NOS
        _NOSVSSPECIFIC_MAP.put("8010", "8011, 8012, 8013, 8014, 8015"); //Carcinoma, NOS
        _NOSVSSPECIFIC_MAP.put("8140 ", "8141, 8142, 8143, 8144, 8145, 8147, 8148"); //Adenocarcinoma, NOS
        _NOSVSSPECIFIC_MAP.put("8070", "8071, 8072, 8073, 8074, 8075, 8076, 8077, 8078, 8080, 8081, 8082, 8083, 8084, 8094, 8323"); //Squamous cell carcinoma, NOS
        _NOSVSSPECIFIC_MAP.put("8720", "8721, 8722, 8723, 8726, 8728, 8730, 8740, 8741, 8742, 8743, 8744, 9745, 8746, 8761, 8770, 8771, 8772, 8773, 8774, 8780"); //Melanoma, NOS
        _NOSVSSPECIFIC_MAP.put("8800", "8801. 8802, 8803, 8804, 8805, 8806"); //Sarcoma, NOS
        _NOSVSSPECIFIC_MAP.put("8312", "8313, 8314, 8315, 8316, 8317, 8318, 8319, 8320"); //Renal cell carcinoma, NOS
    }

    protected String _id;

    protected String _name;

    protected String _siteInclusions;

    protected String _siteExclusions;

    protected String _histInclusions;

    protected String _histExclusions;

    protected List<String> _behavInclusions;

    protected List<MPRule> _rules;

    private List<Range<Integer>> _siteIncRanges;

    private List<Range<Integer>> _siteExcRanges;

    private List<Range<Integer>> _histIncRanges;

    private List<Range<Integer>> _histExcRanges;

    public MPGroup(String id, String name, String siteInclusions, String siteExclusions, String histInclusions, String histExclusions, List<String> behavInclusions) {
        _id = id;
        _name = name;
        _siteInclusions = siteInclusions;
        _siteExclusions = siteExclusions;
        _histInclusions = histInclusions;
        _histExclusions = histExclusions;
        _behavInclusions = behavInclusions;
        _rules = new ArrayList<>();

        // compute the raw inclusions/exclusions into ranges
        _siteIncRanges = computeRange(siteInclusions, true);
        _siteExcRanges = computeRange(siteExclusions, true);
        _histIncRanges = computeRange(histInclusions, false);
        _histExcRanges = computeRange(histExclusions, false);
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getSiteInclusions() {
        return _siteInclusions;
    }

    public String getSiteExclusions() {
        return _siteExclusions;
    }

    public String getHistInclusions() {
        return _histInclusions;
    }

    public String getHistExclusions() {
        return _histExclusions;
    }

    public List<String> getBehavInclusions() {
        return _behavInclusions;
    }

    public List<MPRule> getRules() {
        return _rules;
    }

    public boolean isApplicable(String primarySite, String histology, String behavior) {
        if (!MPUtils.validateProperties(primarySite, histology, behavior) || !_behavInclusions.contains(behavior))
            return false;

        boolean siteOk, histOk = false;

        Integer site = Integer.parseInt(primarySite.substring(1)), hist = Integer.parseInt(histology);

        // check site
        if (_siteIncRanges != null)
            siteOk = isContained(_siteIncRanges, site);
        else
            siteOk = _siteExcRanges == null || !isContained(_siteExcRanges, site);

        // check histology (only if site matched)
        if (siteOk) {
            if (_histIncRanges != null)
                histOk = isContained(_histIncRanges, hist);
            else
                histOk = _histExcRanges == null || !isContained(_histExcRanges, hist);
        }

        return siteOk && histOk;
    }

    protected boolean isContained(List<Range<Integer>> list, Integer value) {
        for (Range<Integer> range : list)
            if (range.contains(value))
                return true;
        return false;
    }

    protected List<Range<Integer>> computeRange(String rawValue, boolean isSite) {
        if (rawValue == null)
            return null;

        List<Range<Integer>> result = new ArrayList<>();

        for (String item : StringUtils.split(rawValue, ',')) {
            String[] parts = StringUtils.split(item.trim(), '-');
            if (parts.length == 1) {
                if (isSite)
                    result.add(Range.is(Integer.parseInt(parts[0].substring(1))));
                else
                    result.add(Range.is(Integer.parseInt(parts[0])));
            }
            else {
                if (isSite)
                    result.add(Range.between(Integer.parseInt(parts[0].substring(1)), Integer.parseInt(parts[1].substring(1))));
                else
                    result.add(Range.between(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
            }
        }

        return result;
    }

    protected Map<String, String> getNoSvsSpecificMap() {
        return Collections.unmodifiableMap(_NOSVSSPECIFIC_MAP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MPGroup mpGroup = (MPGroup)o;

        return _id.equals(mpGroup._id);

    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    /* *********************************************************************************************************************
     * ***************************   COMMON RULES USED IN MOST OF THE GROUPS       ****************************************
     * *********************************************************************************************************************/
    public static class MPRuleHistologyCode extends MPRule {

        public MPRuleHistologyCode(String groupId, String step) {
            super(groupId, step, MPResult.MULTIPLE_PRIMARIES);
            setQuestion("Do the tumors haveICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number?");
            setReason("Tumors with ICD-O-3 histology codes that are different at the first (?xxx), second (x?xx) or third (xx?x) number are multiple primaries.");
        }

        @Override
        public MPRuleResult apply(MPInput i1, MPInput i2) {
            MPRuleResult result = new MPRuleResult();
            String hist1 = i1.getHistologIcdO3(), hist2 = i2.getHistologIcdO3();
            result.setResult(((hist1.charAt(0) != hist2.charAt(0)) || (hist1.charAt(1) != hist2.charAt(1)) || (hist1.charAt(2) != hist2.charAt(
                    2))) ? RuleResult.TRUE : RuleResult.FALSE);
            return result;
        }

    }

    public static class MPRulePrimarySiteCode extends MPRule {

        public MPRulePrimarySiteCode(String groupId, String step) {
            super(groupId, step, MPResult.MULTIPLE_PRIMARIES);
            setQuestion("Are there tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third character (Cx?x)?");
            setReason("Tumors in sites with ICD-O-3 topography codes that are different at the second (C?xx) and/or third (Cx?x) character are multiple primaries.");
        }

        @Override
        public MPRuleResult apply(MPInput i1, MPInput i2) {
            MPRuleResult result = new MPRuleResult();
            result.setResult(((i1.getPrimarySite().charAt(1) != i2.getPrimarySite().charAt(1)) || (i1.getPrimarySite().charAt(2) != i2.getPrimarySite().charAt(
                    2))) ? RuleResult.TRUE : RuleResult.FALSE);
            return result;
        }
    }

    public static class MPRuleBehavior extends MPRule {

        public MPRuleBehavior(String groupId, String step) {
            super(groupId, step, MPResult.MULTIPLE_PRIMARIES);
            setQuestion("Is there an invasive tumor following an in situ tumor more than 60 days after diagnosis?");
            setReason("An invasive tumor following an in situ tumor more than 60 days after diagnosis are multiple primaries.");
            getNotes().add("The purpose of this rule is to ensure that the case is counted as an incident (invasive) case when incidence data are analyzed.");
            getNotes().add("Abstract as multiple primaries even if the medical record/physician states it is recurrence or progression of disease.");
        }

        @Override
        public MPRuleResult apply(MPInput i1, MPInput i2) {
            MPRuleResult result = new MPRuleResult();
            String beh1 = i1.getBehaviorIcdO3(), beh2 = i2.getBehaviorIcdO3();
            if (!differentCategory(beh1, beh2, Arrays.asList("2"), Arrays.asList("3"))) {
                result.setResult(RuleResult.FALSE);
                return result;
            }
            int diff = verify60DaysApart(i1, i2, true);
            if (-1 == diff) {
                result.setResult(RuleResult.UNKNOWN);
                result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
            }
            else if ((1 == diff && "3".equals(beh1) && "2".equals(beh2)) || (2 == diff && "3".equals(beh2) && "2".equals(beh1)))
                result.setResult(RuleResult.TRUE);
            else
                result.setResult(RuleResult.FALSE);

            return result;
        }
    }

    public static class MPRuleDiagnosisDate extends MPRule {

        public MPRuleDiagnosisDate(String groupId, String step) {
            super(groupId, step, MPResult.MULTIPLE_PRIMARIES);
            setQuestion("Are there tumors diagnosed more than five (5) years apart?");
            setReason("Tumors diagnosed more than five (5) years apart are multiple primaries.");

        }

        @Override
        public MPRuleResult apply(MPInput i1, MPInput i2) {
            MPRuleResult result = new MPRuleResult();
            int diff = verifyYearsApart(i1, i2, 5);
            if (-1 == diff) {
                result.setResult(RuleResult.UNKNOWN);
                result.setMessage("Unable to apply Rule " + this.getStep() + " of " + this.getGroupId() + ". There is no enough diagnosis date information.");
            }
            else
                result.setResult(1 == diff ? RuleResult.TRUE : RuleResult.FALSE);

            return result;
        }
    }

    public static class MPRuleNoCriteriaSatisfied extends MPRule {

        public MPRuleNoCriteriaSatisfied(String groupId, String step) {
            super(groupId, step, MPResult.SINGLE_PRIMARY);
            setQuestion("Does not meet any of the criteria?");
            setReason("Tumors that do not meet any of the criteria are abstracted as a single primary.");
        }

        @Override
        public MPRuleResult apply(MPInput i1, MPInput i2) {
            MPRuleResult result = new MPRuleResult();
            result.setResult(RuleResult.TRUE);
            return result;
        }
    }
    
    /* *********************************************************************************************************************
     * ***************************   HELPER METHODS USED IN MOST OF THE GROUPS      ****************************************
     * *********************************************************************************************************************/

    //helper method, checks if one property belongs to some category and the other to different category
    public static boolean  differentCategory(String prop1, String prop2, List<String> cat1, List<String> cat2) {
        return !(cat1 == null || cat2 == null || cat1.isEmpty() || cat2.isEmpty()) && ((cat1.contains(prop1) && cat2.contains(prop2)) || (cat1.contains(prop2) && cat2.contains(prop1)));
    }

    //checks if the two tumors are diagnosed "x" years apart. It returns Yes (1), No (0) or Unknown (-1) (If there is no enough information)
    public static int verifyYearsApart(MPInput input1, MPInput input2, int yearsApart) {
        int yes = 1, no = 0, unknown = -1;
        int year1 = NumberUtils.isDigits(input1.getDateOfDiagnosisYear()) ? Integer.parseInt(input1.getDateOfDiagnosisYear()) : 9999;
        int year2 = NumberUtils.isDigits(input2.getDateOfDiagnosisYear()) ? Integer.parseInt(input2.getDateOfDiagnosisYear()) : 9999;
        int month1 = NumberUtils.isDigits(input1.getDateOfDiagnosisMonth()) ? Integer.parseInt(input1.getDateOfDiagnosisMonth()) : 99;
        int month2 = NumberUtils.isDigits(input2.getDateOfDiagnosisMonth()) ? Integer.parseInt(input2.getDateOfDiagnosisMonth()) : 99;
        int day1 = NumberUtils.isDigits(input1.getDateOfDiagnosisDay()) ? Integer.parseInt(input1.getDateOfDiagnosisDay()) : 99;
        int day2 = NumberUtils.isDigits(input2.getDateOfDiagnosisDay()) ? Integer.parseInt(input2.getDateOfDiagnosisDay()) : 99;
        //If year is missing or in the future, return unknown
        int currYear = LocalDate.now().getYear();
        if (year1 == 9999 || year2 == 9999 || year1 > currYear || year2 > currYear)
            return unknown;
        else if (Math.abs(year1 - year2) > yearsApart)
            return yes;
        else if (Math.abs(year1 - year2) < yearsApart)
            return no;
        else {
            //if month is missing, set day to 99
            if (month1 == 99)
                day1 = 99;
            if (month2 == 99)
                day2 = 99;
            //if month and day are invalid set them to 99 (Example: if month is 13 or day is 35)
            try {
                new LocalDate(year1, month1 == 99 ? 1 : month1, day1 == 99 ? 1 : day1);
            }
            catch (Exception e) {
                day1 = 99;
                if (month1 < 1 || month1 > 12)
                    month1 = 99;
            }

            try {
                new LocalDate(year2, month2 == 99 ? 1 : month2, day2 == 99 ? 1 : day2);
            }
            catch (Exception e) {
                day2 = 99;
                if (month2 < 1 || month2 > 12)
                    month2 = 99;
            }

            if (month1 == 99 || month2 == 99)
                return unknown;
            else if ((year1 > year2 && month1 > month2) || (year2 > year1 && month2 > month1))
                return yes;
            else if ((year1 > year2 && month1 < month2) || (year2 > year1 && month2 < month1))
                return no;
            else if (day1 == 99 || day2 == 99)
                return unknown;
            else
                return Math.abs(Years.yearsBetween(new LocalDate(year1, month1, day1), new LocalDate(year2, month2, day2)).getYears()) >= yearsApart ? yes : no;
        }
    }

    //helper method, checks whether there are 60 *days* between diagnosis date of the two tumors. It returns 1 (if tumor 1 is diagnosed after 60 days of tumor 2), 
    //2 (if tumor 2 is diagnosed 60 days after tumor 1), 0 (if the days between two diagnosis is less than 60 days) or -1 (if there is insufficient information);
    // If checkBehavior is true, it also checks if invasive is after in situ tumor

    public static int verify60DaysApart(MPInput input1, MPInput input2, boolean checkBehavior) {
        int yes1 = 1, yes2 = 2, no = 0, unknown = -1;
        int year1 = NumberUtils.isDigits(input1.getDateOfDiagnosisYear()) ? Integer.parseInt(input1.getDateOfDiagnosisYear()) : 9999;
        int year2 = NumberUtils.isDigits(input2.getDateOfDiagnosisYear()) ? Integer.parseInt(input2.getDateOfDiagnosisYear()) : 9999;
        int month1 = NumberUtils.isDigits(input1.getDateOfDiagnosisMonth()) ? Integer.parseInt(input1.getDateOfDiagnosisMonth()) : 99;
        int month2 = NumberUtils.isDigits(input2.getDateOfDiagnosisMonth()) ? Integer.parseInt(input2.getDateOfDiagnosisMonth()) : 99;
        int day1 = NumberUtils.isDigits(input1.getDateOfDiagnosisDay()) ? Integer.parseInt(input1.getDateOfDiagnosisDay()) : 99;
        int day2 = NumberUtils.isDigits(input2.getDateOfDiagnosisDay()) ? Integer.parseInt(input2.getDateOfDiagnosisDay()) : 99;
        //If year is missing or in the future, return unknown
        int currYear = LocalDate.now().getYear();
        if (year1 == 9999 || year2 == 9999 || year1 > currYear || year2 > currYear)
            return unknown;
        //if month is missing, set day to 99
        if (month1 == 99)
            day1 = 99;
        if (month2 == 99)
            day2 = 99;
        //if month and day are invalid set them to 99 (Example: if month is 13 or day is 35)
        try {
            new LocalDate(year1, month1 == 99 ? 1 : month1, day1 == 99 ? 1 : day1);
        }
        catch (Exception e) {
            day1 = 99;
            if (month1 < 1 || month1 > 12)
                month1 = 99;
        }

        try {
            new LocalDate(year2, month2 == 99 ? 1 : month2, day2 == 99 ? 1 : day2);
        }
        catch (Exception e) {
            day2 = 99;
            if (month2 < 1 || month2 > 12)
                month2 = 99;
        }

        if (month1 != 99 && month2 != 99 && day1 != 99 && day2 != 99) {
            if (Days.daysBetween(new LocalDate(year2, month2, day2), new LocalDate(year1, month1, day1)).getDays() > 60)
                return yes1;
            else if (Days.daysBetween(new LocalDate(year1, month1, day1), new LocalDate(year2, month2, day2)).getDays() > 60)
                return yes2;
            else
                return no;
        }
        else if (year1 - year2 >= 2)
            return yes1;
        else if (year2 - year1 >= 2)
            return yes2;
        else if (year1 > year2) {
            // If invasive is diagnosed before in situ
            if (checkBehavior && "2".equals(input1.getBehaviorIcdO3()) && "3".equals(input2.getBehaviorIcdO3()))
                return no;
            return verify60DaysApart(year2, month2, day2, year1, month1, day1);
        }
        else if (year2 > year1) {
            // If invasive is diagnosed before in situ
            if (checkBehavior && "3".equals(input1.getBehaviorIcdO3()) && "2".equals(input2.getBehaviorIcdO3()))
                return no;
            return 1 == verify60DaysApart(year1, month1, day1, year2, month2, day2) ? yes2 : verify60DaysApart(year1, month1, day1, year2, month2, day2);
        }
        else {
            if (month1 == 99 || month2 == 99)
                return unknown;
            else if (month1 > month2) {
                // If invasive is diagnosed before in situ
                if (checkBehavior && "2".equals(input1.getBehaviorIcdO3()) && "3".equals(input2.getBehaviorIcdO3()))
                    return no;
                return verify60DaysApart(year2, month2, day2, year1, month1, day1);
            }
            else if (month2 > month1) {
                // If invasive is diagnosed before in situ
                if (checkBehavior && "3".equals(input1.getBehaviorIcdO3()) && "2".equals(input2.getBehaviorIcdO3()))
                    return no;
                return 1 == verify60DaysApart(year1, month1, day1, year2, month2, day2) ? yes2 : verify60DaysApart(year1, month1, day1, year2, month2, day2);
            }
            else
                return no;
        }
    }

    //This method is called with valid years 
    private static int verify60DaysApart(int startYr, int startMon, int startDay, int endYr, int endMon, int endDay) {

        LocalDate startDateMin, startDateMax, endDateMin, endDateMax;
        if (startMon == 99 && endMon == 99)
            return -1;
        else if (startMon != 99 && endMon != 99) {
            startDateMin = new LocalDate(startYr, startMon, 1);
            startDateMax = startDateMin.dayOfMonth().withMaximumValue();
            endDateMin = new LocalDate(endYr, endMon, 1);
            endDateMax = endDateMin.dayOfMonth().withMaximumValue();
            if (startDay != 99)
                startDateMin = startDateMax = new LocalDate(startYr, startMon, startDay);
            if (endDay != 99)
                endDateMin = endDateMax = new LocalDate(endYr, endMon, endDay);
        }
        else if (endMon == 99) {
            endDateMin = new LocalDate(endYr, 1, 1);
            endDateMax = new LocalDate(endYr, 12, 31);
            if (startDay != 99)
                startDateMin = startDateMax = new LocalDate(startYr, startMon, startDay);
            else {
                startDateMin = new LocalDate(startYr, startMon, 1);
                startDateMax = startDateMin.dayOfMonth().withMaximumValue();
            }
        }
        else {
            startDateMin = new LocalDate(startYr, 1, 1);
            startDateMax = new LocalDate(startYr, 12, 31);
            if (endDay != 99)
                endDateMin = endDateMax = new LocalDate(endYr, endMon, endDay);
            else {
                endDateMin = new LocalDate(endYr, endMon, 1);
                endDateMax = endDateMin.dayOfMonth().withMaximumValue();
            }
        }
        int minDaysBetween = Days.daysBetween(startDateMax, endDateMin).getDays();
        int maxDaysBetween = Days.daysBetween(startDateMin, endDateMax).getDays();
        if (minDaysBetween > 60)
            return 1;
        else if (maxDaysBetween <= 60)
            return 0;
        else
            return -1;
    }

}
