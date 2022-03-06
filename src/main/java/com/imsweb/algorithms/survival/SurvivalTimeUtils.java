/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.survival;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class is used to calculate the survival time in months for a give patient (a list of records).
 * It also calculates the vital status of the patient at the study cutoff date.
 * <br/><br/>
 * See <a href="http://seer.cancer.gov/survivaltime/">http://seer.cancer.gov/survivaltime/</a>.
 */
public class SurvivalTimeUtils {

    public static final String ALG_NAME = "SEER Survival Time in Months";
    public static final String VERSION = "version 2.2 released in September 2014";

    public static final String SURVIVAL_FLAG_COMPLETE_INFO_NO_SURVIVAL = "0";
    public static final String SURVIVAL_FLAG_COMPLETE_INFO_SOME_SURVIVAL = "1";
    public static final String SURVIVAL_FLAG_MISSING_INFO_NO_SURVIVAL_POSSIBLE = "2";
    public static final String SURVIVAL_FLAG_MISSING_INFO_SOME_SURVIVAL = "3";
    public static final String SURVIVAL_FLAG_DCO_AUTOPSY_ONLY = "8";
    public static final String SURVIVAL_FLAG_UNKNOWN = "9";

    private static final double _DAYS_IN_MONTH = 365.24 / (double)12;

    public static final String UNKNOWN_SURVIVAL = "9999";
    public static final String BLANK_YEAR = null;
    public static final String BLANK_MONTH = null;
    public static final String BLANK_DAY = null;

    /**
     * Calculates the survival time for the provided list of records representing a patient.
     * It also calculates the vital status of the patient at the study cutoff date.
     * <br/><br/>
     * See <a href="http://seer.cancer.gov/survivaltime/">http://seer.cancer.gov/survivaltime/</a>
     * @param input input object representing a patient
     * @param endPointYear end point year (also called end study year, or current reporting year)
     * @return SurvivalTimeOutputPatientDto
     */
    public static SurvivalTimeOutputPatientDto calculateSurvivalTime(SurvivalTimeInputPatientDto input, int endPointYear) {

        SurvivalTimeOutputPatientDto patientResultsDto = new SurvivalTimeOutputPatientDto();

        if (input == null || input.getSurvivalTimeInputPatientDtoList().isEmpty())
            return patientResultsDto;

        List<SurvivalTimeInputRecordDto> allRecords = input.getSurvivalTimeInputPatientDtoList();
        List<SurvivalTimeOutputRecordDto> patientResultsList = new ArrayList<>();

        try {
            // lets check if all records of a patient have same dolc and vs
            String dolcYearStr = allRecords.get(0).getDateOfLastContactYear();
            String dolcMonthStr = allRecords.get(0).getDateOfLastContactMonth();
            String dolcDayStr = allRecords.get(0).getDateOfLastContactDay();
            String vsStr = allRecords.get(0).getVitalStatus();
            patientResultsDto.setVitalStatusRecode(vsStr);
            for (SurvivalTimeInputRecordDto record : allRecords) {
                boolean sameYear = dolcYearStr == null ? record.getDateOfLastContactYear() == null : dolcYearStr.equals(record.getDateOfLastContactYear());
                boolean sameMonth = dolcMonthStr == null ? record.getDateOfLastContactMonth() == null : dolcMonthStr.equals(record.getDateOfLastContactMonth());
                boolean sameDay = dolcDayStr == null ? record.getDateOfLastContactDay() == null : dolcDayStr.equals(record.getDateOfLastContactDay());
                boolean sameVs = vsStr == null ? record.getVitalStatus() == null : vsStr.equals(record.getVitalStatus());
                if (!sameYear || !sameMonth || !sameDay || !sameVs) {
                    //We need to sort the records to calculated the sorted index
                    List<InternalRecDto> tempInternalRecords = new ArrayList<>();
                    for (SurvivalTimeInputRecordDto orgRecord : allRecords) {
                        SurvivalTimeOutputRecordDto recordResult = new SurvivalTimeOutputRecordDto();
                        recordResult.setSurvivalTimeDxYear(BLANK_YEAR);
                        recordResult.setSurvivalTimeDxMonth(BLANK_MONTH);
                        recordResult.setSurvivalTimeDxDay(BLANK_DAY);
                        recordResult.setSurvivalTimeDolcYear(BLANK_YEAR);
                        recordResult.setSurvivalTimeDolcMonth(BLANK_MONTH);
                        recordResult.setSurvivalTimeDolcDay(BLANK_DAY);
                        recordResult.setSurvivalTimeDolcYearPresumedAlive(BLANK_YEAR);
                        recordResult.setSurvivalTimeDolcMonthPresumedAlive(BLANK_MONTH);
                        recordResult.setSurvivalTimeDolcDayPresumedAlive(BLANK_DAY);
                        recordResult.setSurvivalMonths(UNKNOWN_SURVIVAL);
                        recordResult.setSurvivalMonthsFlag(SURVIVAL_FLAG_UNKNOWN);
                        recordResult.setSurvivalMonthsPresumedAlive(UNKNOWN_SURVIVAL);
                        recordResult.setSurvivalMonthsFlagPresumedAlive(SURVIVAL_FLAG_UNKNOWN);
                        patientResultsList.add(recordResult);
                        tempInternalRecords.add(new InternalRecDto(orgRecord, recordResult));
                    }
                    //Let's sort the temp records list and assign back the sorted index to the output
                    tempInternalRecords = sortTempRecords(tempInternalRecords);
                    for (int sortedIdx = 1; sortedIdx <= tempInternalRecords.size(); sortedIdx++) // output sort index should be 1-based
                        tempInternalRecords.get(sortedIdx - 1)._recordResult.setSortedIndex(sortedIdx);
                    patientResultsDto.setSurvivalTimeOutputPatientDtoList(patientResultsList);
                    if (!sameVs)
                        patientResultsDto.setVitalStatusRecode(null);
                    return patientResultsDto;
                }
            }

            // Lets continue if all records have same dolc and vs
            int dolcYear = NumberUtils.isDigits(dolcYearStr) ? Integer.parseInt(dolcYearStr) : 9999;
            int dolcMonth = NumberUtils.isDigits(dolcMonthStr) ? Integer.parseInt(dolcMonthStr) : 99;
            int dolcDay = NumberUtils.isDigits(dolcDayStr) ? Integer.parseInt(dolcDayStr) : 99;
            int vs = NumberUtils.isDigits(vsStr) ? Integer.parseInt(vsStr) : 9;
            //if dolc year is invalid or if dolc date is future date, set it as missing, and if Dolc year is missing set the month and date as missing.
            boolean isDolcValid = true;
            try {
                LocalDate.of(dolcYear, dolcMonth, dolcDay);
            }
            catch (DateTimeException e) {
                isDolcValid = false;
            }
            LocalDate now = LocalDate.now();
            if (dolcYear < 1900 || dolcYear > now.getYear() || (dolcYear == now.getYear() && ((dolcMonth <= 12 && dolcMonth > now.getMonthValue()) || (dolcMonth == now.getMonthValue() && isDolcValid
                    && dolcDay > now.getDayOfMonth()))))
                dolcYear = 9999;
            if (dolcYear == 9999)
                dolcMonth = dolcDay = 99;

            //Vital status recode is vital status at the study cutoff date.
            if (dolcYear != 9999 && dolcYear > endPointYear)
                patientResultsDto.setVitalStatusRecode("1");

            // we are also going to need the birth date for filling the missing diagnosis and dolc dates. (#192)
            String birthYearStr = allRecords.isEmpty() ? "" : allRecords.get(0).getBirthYear();
            int birthYear = NumberUtils.isDigits(birthYearStr) ? Integer.parseInt(birthYearStr) : 9999;
            String birthMonthStr = allRecords.isEmpty() ? "" : allRecords.get(0).getBirthMonth();
            int birthMonth = NumberUtils.isDigits(birthMonthStr) ? Integer.parseInt(birthMonthStr) : 99;
            String birthDayStr = allRecords.isEmpty() ? "" : allRecords.get(0).getBirthDay();
            int birthDay = NumberUtils.isDigits(birthDayStr) ? Integer.parseInt(birthDayStr) : 99;

            // we are going to use DTO objects to make it easier, let's also use a different list so we don't change the order of the original records
            List<InternalRecDto> validTempRecords = new ArrayList<>();
            List<InternalRecDto> allTempRecords = new ArrayList<>();
            for (SurvivalTimeInputRecordDto orgRecord : allRecords) {
                SurvivalTimeOutputRecordDto recordResult = new SurvivalTimeOutputRecordDto();
                recordResult.setSurvivalTimeDxYear(orgRecord.getDateOfDiagnosisYear());
                recordResult.setSurvivalTimeDxMonth(orgRecord.getDateOfDiagnosisMonth());
                recordResult.setSurvivalTimeDxDay(orgRecord.getDateOfDiagnosisDay());
                recordResult.setSurvivalTimeDolcYear(orgRecord.getDateOfLastContactYear());
                recordResult.setSurvivalTimeDolcMonth(orgRecord.getDateOfLastContactMonth());
                recordResult.setSurvivalTimeDolcDay(orgRecord.getDateOfLastContactDay());
                recordResult.setSurvivalTimeDolcYearPresumedAlive(orgRecord.getDateOfLastContactYear());
                recordResult.setSurvivalTimeDolcMonthPresumedAlive(orgRecord.getDateOfLastContactMonth());
                recordResult.setSurvivalTimeDolcDayPresumedAlive(orgRecord.getDateOfLastContactDay());
                patientResultsList.add(recordResult);
                InternalRecDto tempRec = new InternalRecDto(orgRecord, recordResult);
                allTempRecords.add(tempRec);

                // check validity of the date
                boolean valid = tempRec._year != 9999 && tempRec._year >= 1900 && tempRec._year <= endPointYear;
                if (valid) {
                    try {
                        LocalDate.of(tempRec._year, tempRec._month == 99 ? 1 : tempRec._month, tempRec._day == 99 ? 1 : tempRec._day);
                    }
                    catch (DateTimeException e) {
                        if (tempRec._month >= 1 && tempRec._month <= 12)
                            tempRec._daySafe = tempRec._day = 99;
                        else
                            tempRec._month = tempRec._monthSafe = tempRec._daySafe = tempRec._day = 99;
                    }
                }

                if (!valid) {  // any "bad" record is assigned unknown results
                    recordResult.setSurvivalMonths(UNKNOWN_SURVIVAL);
                    recordResult.setSurvivalMonthsFlag(SURVIVAL_FLAG_UNKNOWN);
                    recordResult.setSurvivalMonthsPresumedAlive(UNKNOWN_SURVIVAL);
                    recordResult.setSurvivalMonthsFlagPresumedAlive(SURVIVAL_FLAG_UNKNOWN);
                    recordResult.setSurvivalTimeDxYear(BLANK_YEAR);
                    recordResult.setSurvivalTimeDxMonth(BLANK_MONTH);
                    recordResult.setSurvivalTimeDxDay(BLANK_DAY);
                }
                else
                    validTempRecords.add(tempRec);
            }

            // STEP 1 - sort the records by DX date (and/or sequence number)
            validTempRecords = sortTempRecords(validTempRecords);

            // calculate the variables without presuming ALIVE (this one cannot handle an unknown DOLC)
            if (dolcYear != 9999)
                calculateSurvivalTime(validTempRecords, patientResultsList, dolcYear, dolcMonth, dolcDay, birthYear, birthMonth, birthDay, vs, endPointYear, 12, 31, false);
            else {
                for (InternalRecDto rec : validTempRecords) {
                    rec._recordResult.setSurvivalMonths(UNKNOWN_SURVIVAL);
                    rec._recordResult.setSurvivalMonthsFlag(SURVIVAL_FLAG_UNKNOWN);
                    rec._recordResult.setSurvivalTimeDolcYear(BLANK_YEAR);
                    rec._recordResult.setSurvivalTimeDolcMonth(BLANK_MONTH);
                    rec._recordResult.setSurvivalTimeDolcDay(BLANK_DAY);
                }
            }

            // reset the computed dates
            for (InternalRecDto rec : validTempRecords) {
                rec._yearSafe = rec._year;
                rec._monthSafe = rec._month;
                rec._daySafe = rec._day;
            }

            // calculate the variables presuming ALIVE (this one can handle the unknown DOLC only if vital status is ALIVE)
            if (dolcYear != 9999 || vs == 1)
                calculateSurvivalTime(validTempRecords, patientResultsList, dolcYear, dolcMonth, dolcDay, birthYear, birthMonth, birthDay, vs, endPointYear, 12, 31, true);
            else {
                for (InternalRecDto rec : validTempRecords) {
                    rec._recordResult.setSurvivalMonthsPresumedAlive(UNKNOWN_SURVIVAL);
                    rec._recordResult.setSurvivalMonthsFlagPresumedAlive(SURVIVAL_FLAG_UNKNOWN);
                    rec._recordResult.setSurvivalTimeDolcYearPresumedAlive(BLANK_YEAR);
                    rec._recordResult.setSurvivalTimeDolcMonthPresumedAlive(BLANK_MONTH);
                    rec._recordResult.setSurvivalTimeDolcDayPresumedAlive(BLANK_DAY);
                }
            }

            //Set the flags for DCO/Autopsy only cases (type of reporting source 6 and 7) to 8 and set the survival months fields to missing. (#192)
            for (InternalRecDto rec : validTempRecords) {
                if ("6".equals(rec._originalRecord.getTypeOfReportingSource()) || "7".equals(rec._originalRecord.getTypeOfReportingSource())) {
                    rec._recordResult.setSurvivalMonths(UNKNOWN_SURVIVAL);
                    rec._recordResult.setSurvivalMonthsFlag(SURVIVAL_FLAG_DCO_AUTOPSY_ONLY);
                    rec._recordResult.setSurvivalMonthsPresumedAlive(UNKNOWN_SURVIVAL);
                    rec._recordResult.setSurvivalMonthsFlagPresumedAlive(SURVIVAL_FLAG_DCO_AUTOPSY_ONLY);
                }
            }

            // assign the sorted index on every output record: based on sorted tmp records
            allTempRecords = sortTempRecords(allTempRecords);
            for (int sortedIdx = 1; sortedIdx <= allTempRecords.size(); sortedIdx++) // output sort index should be 1-based
                allTempRecords.get(sortedIdx - 1)._recordResult.setSortedIndex(sortedIdx);

        }
        catch (DateTimeException e) {
            // final safety net, if anything goes wrong, just assign 9's
            patientResultsList.clear();
            int sortedIdx = 1; // output sort index should be 1-based
            for (SurvivalTimeInputRecordDto orgRecord : allRecords) {
                SurvivalTimeOutputRecordDto recordResult = new SurvivalTimeOutputRecordDto();
                recordResult.setSurvivalMonths(UNKNOWN_SURVIVAL);
                recordResult.setSurvivalMonthsFlag(SURVIVAL_FLAG_UNKNOWN);
                recordResult.setSurvivalMonthsPresumedAlive(UNKNOWN_SURVIVAL);
                recordResult.setSurvivalMonthsFlagPresumedAlive(SURVIVAL_FLAG_UNKNOWN);
                recordResult.setSurvivalTimeDxYear(orgRecord.getDateOfDiagnosisYear());
                recordResult.setSurvivalTimeDxMonth(orgRecord.getDateOfDiagnosisMonth());
                recordResult.setSurvivalTimeDxDay(orgRecord.getDateOfDiagnosisDay());
                recordResult.setSurvivalTimeDolcYear(orgRecord.getDateOfLastContactYear());
                recordResult.setSurvivalTimeDolcMonth(orgRecord.getDateOfLastContactMonth());
                recordResult.setSurvivalTimeDolcDay(orgRecord.getDateOfLastContactDay());
                recordResult.setSurvivalTimeDolcYearPresumedAlive(orgRecord.getDateOfLastContactYear());
                recordResult.setSurvivalTimeDolcMonthPresumedAlive(orgRecord.getDateOfLastContactMonth());
                recordResult.setSurvivalTimeDolcDayPresumedAlive(orgRecord.getDateOfLastContactDay());
                recordResult.setSortedIndex(sortedIdx++);
                patientResultsList.add(recordResult);
            }
        }
        patientResultsDto.setSurvivalTimeOutputPatientDtoList(patientResultsList);

        return patientResultsDto;
    }

    @SuppressWarnings("SameParameterValue")
    private static void calculateSurvivalTime(List<InternalRecDto> records, List<SurvivalTimeOutputRecordDto> patientResultsList, int dolcYear, int dolcMonth, int dolcDay, int birthYear, int birthMonth, int birthDay, int vs, int endYear, int endMonth, int endDay, boolean presumeAlive) {

        //check validity of DOLC
        //if the month is invalid, set both day and month to 99, if the day is invalid set it as missing.
        try {
            LocalDate.of(dolcYear == 9999 ? 1900 : dolcYear, dolcMonth == 99 ? 1 : dolcMonth, dolcDay == 99 ? 1 : dolcDay);
        }
        catch (DateTimeException e) {
            if (dolcMonth >= 1 && dolcMonth <= 12)
                dolcDay = 99;
            else
                dolcMonth = dolcDay = 99;
        }
        // the DOLC also needs to be fixed using the same logic as the DX dates, so let's create a fake record at the end of the list
        InternalRecDto dolc = new InternalRecDto();
        dolc._year = dolc._yearSafe = dolcYear;
        dolc._month = dolc._monthSafe = dolcMonth;
        dolc._day = dolc._daySafe = dolcDay;
        records.add(dolc);

        // STEP 2 - fill in unknown parts
        //lets use the birthdate to fix the missing dx and dolc (#192)
        //check validity of month and day and set them as missing if they are invalid
        if (birthYear == 9999)
            birthMonth = birthDay = 99;
        if (birthMonth == 99)
            birthDay = 99;
        try {
            LocalDate.of(birthYear, birthMonth == 99 ? 1 : birthMonth, birthDay == 99 ? 1 : birthDay);
        }
        catch (DateTimeException e) {
            if (birthMonth >= 1 && birthMonth <= 12)
                birthDay = 99;
            else
                birthMonth = birthDay = 99;
        }

        //    step 2.1 - assign unknown day for dates that have a known month, skip the birthday
        for (int i = 0; i < records.size(); i++) {
            InternalRecDto rec = records.get(i);
            if (rec._monthSafe != 99 && rec._daySafe == 99) {
                // get earliest day possible for this date
                int earliestDayPossible = 0;
                //use birthday for the first record
                if (i == 0 && birthYear == rec._yearSafe && birthMonth == rec._monthSafe && birthDay != 99)
                    earliestDayPossible = birthDay;
                for (int j = i - 1; j >= 0; j--) {
                    InternalRecDto prevRec = records.get(j);
                    if (prevRec._daySafe != 99) {
                        if (prevRec._yearSafe == rec._yearSafe && prevRec._monthSafe == rec._monthSafe)
                            earliestDayPossible = prevRec._daySafe;
                        break;
                    }
                }
                if (earliestDayPossible == 0)
                    earliestDayPossible = 1;
                // get latest day possible for this date
                int latestDayPossible = 0;
                for (int j = i + 1; j < records.size(); j++) {
                    InternalRecDto nextRec = records.get(j);
                    if (nextRec._daySafe != 99) {
                        if (nextRec._yearSafe == rec._yearSafe && nextRec._monthSafe == rec._monthSafe)
                            latestDayPossible = nextRec._daySafe;
                        break;
                    }
                }
                if (latestDayPossible == 0)
                    latestDayPossible = YearMonth.of(rec._yearSafe, rec._monthSafe).lengthOfMonth();
                // take the middle point between earliest and latest
                rec._daySafe = (int)Math.floor((double)(earliestDayPossible + latestDayPossible) / (double)2);

                /* SP_CHANGE - SAS--Overwrite the dates with the new dates.
                 JAVA--Sewbesew and Fabian talked and decided not to modify the original record.
                We decided to return an output dto with 4 calculated values and modified dates
                Changes will be to dates that were originally unknown.  */
                rec._recordResult.setSurvivalTimeDxDay(StringUtils.leftPad(String.valueOf(rec._daySafe), 2, "0"));
            }
        }

        //    step 2.2 - assign unknown month and day for dates that also have unknown month, skip the birthday
        for (int i = 0; i < records.size(); i++) {
            InternalRecDto rec = records.get(i);
            if (rec._monthSafe == 99) {
                // get earliest day and month possible for this date
                int earliestMonthPossible = 0, earliestDayPossible = 0;
                //use birthday for the first record
                if (i == 0 && birthYear == rec._yearSafe && birthMonth != 99) {
                    earliestMonthPossible = birthMonth;
                    earliestDayPossible = birthDay != 99 ? birthDay : 1;
                }
                for (int j = i - 1; j >= 0; j--) {
                    InternalRecDto prevRec = records.get(j);
                    if (prevRec._monthSafe != 99) {
                        if (prevRec._yearSafe == rec._yearSafe) {
                            earliestMonthPossible = prevRec._monthSafe;
                            earliestDayPossible = prevRec._daySafe;
                        }
                        break;
                    }
                }
                if (earliestMonthPossible == 0) {
                    earliestMonthPossible = 1;
                    earliestDayPossible = 1;
                }
                // get latest day and month possible for this date
                int latestMonthPossible = 0, latestDayPossible = 0;
                for (int j = i + 1; j < records.size(); j++) {
                    InternalRecDto nextRec = records.get(j);
                    if (nextRec._monthSafe != 99) {
                        if (nextRec._yearSafe == rec._yearSafe) {
                            latestMonthPossible = nextRec._monthSafe;
                            latestDayPossible = nextRec._daySafe;
                        }
                        break;
                    }
                }
                if (latestMonthPossible == 0) {
                    latestMonthPossible = 12;
                    latestDayPossible = 31;
                }
                // take the middle point between earliest and latest
                LocalDate earliestDate = LocalDate.of(rec._yearSafe, earliestMonthPossible, earliestDayPossible);
                int diffInDays = (int)ChronoUnit.DAYS.between(earliestDate, LocalDate.of(rec._yearSafe, latestMonthPossible, latestDayPossible));
                earliestDate = earliestDate.plusDays((int)Math.floor(diffInDays / 2.0));
                rec._monthSafe = earliestDate.getMonthValue();
                rec._daySafe = earliestDate.getDayOfMonth();

                /* SP_CHANGE - SAS--Overwrite the dates with the new dates.
                 JAVA--Sewbesew and Fabian talked and decided not to modify the original record.
                We decided to return an output dto with 4 calculated values and modified dates.
                Changes will be to dates that were originally unknown.  */
                rec._recordResult.setSurvivalTimeDxMonth(StringUtils.leftPad(String.valueOf(rec._monthSafe), 2, "0"));
                rec._recordResult.setSurvivalTimeDxDay(StringUtils.leftPad(String.valueOf(rec._daySafe), 2, "0"));
            }
        }
        // we are done with the fake DOLC too , let's remove it
        records.remove(records.size() - 1);
        //and reset the filled day and month of DOLC if year is unknown
        if (dolcYear == 9999)
            dolc._monthSafe = dolc._daySafe = 99;

        /* if date of last contact is beyond study cut-off, set to study cut-off for both calculations */
        if (dolcYear > endYear && dolcYear != 9999) {
            dolc._yearSafe = endYear;
            dolc._monthSafe = endMonth;
            dolc._daySafe = endDay;
        }

        //set the modified DOLC to all record results
        for (SurvivalTimeOutputRecordDto output : patientResultsList) {
            if (dolc._yearSafe != 9999) {
                output.setSurvivalTimeDolcYear(StringUtils.leftPad(String.valueOf(dolc._yearSafe), 4, "0"));
                output.setSurvivalTimeDolcMonth(StringUtils.leftPad(String.valueOf(dolc._monthSafe), 2, "0"));
                output.setSurvivalTimeDolcDay(StringUtils.leftPad(String.valueOf(dolc._daySafe), 2, "0"));
            }
        }

        /* if patient alive and presumed alive, set to study cut-off for presumed alive */
        if (presumeAlive && vs == 1) {
            dolc._yearSafe = endYear;
            dolc._monthSafe = endMonth;
            dolc._daySafe = endDay;
        }

        //set the modified DOLC presumed alive  to all record results
        for (SurvivalTimeOutputRecordDto output : patientResultsList) {
            if (dolc._yearSafe != 9999) {
                output.setSurvivalTimeDolcYearPresumedAlive(StringUtils.leftPad(String.valueOf(dolc._yearSafe), 4, "0"));
                output.setSurvivalTimeDolcMonthPresumedAlive(StringUtils.leftPad(String.valueOf(dolc._monthSafe), 2, "0"));
                output.setSurvivalTimeDolcDayPresumedAlive(StringUtils.leftPad(String.valueOf(dolc._daySafe), 2, "0"));
            }
        }

        // STEP 3 - calculate variables for each record
        for (InternalRecDto rec : records) {
            int diffInDays = (int)ChronoUnit.DAYS.between(LocalDate.of(rec._yearSafe, rec._monthSafe, rec._daySafe), LocalDate.of(dolc._yearSafe, dolc._monthSafe, dolc._daySafe));
            if (presumeAlive)
                rec._diffInDaysPA = diffInDays;
            else
                rec._diffInDays = diffInDays;
            int diffInMonth = (int)Math.floor(diffInDays / _DAYS_IN_MONTH);

            //if we use end point dates as DOLC for calculation, use that date to calculate the flags too.
            if (dolc._yearSafe == endYear && dolc._monthSafe == endMonth && dolc._daySafe == endDay) {
                dolc._year = endYear;
                dolc._month = endMonth;
                dolc._day = endDay;
            }

            // safety net - do not allow negative values
            if (diffInMonth < 0)
                diffInMonth = 9999; // unknown

            // evaluate the flag
            String flag;
            if (diffInMonth == 9999)
                flag = SURVIVAL_FLAG_UNKNOWN; // flag=9
            else if (rec._month == 99 || rec._day == 99 || dolc._month == 99 || dolc._day == 99) {
                if (rec._year == dolc._year && (rec._month == dolc._month || rec._month == 99 || dolc._month == 99))
                    flag = SURVIVAL_FLAG_MISSING_INFO_NO_SURVIVAL_POSSIBLE; // flag=2
                else
                    flag = SURVIVAL_FLAG_MISSING_INFO_SOME_SURVIVAL; // flag=3
            }
            else if (diffInDays > 0)
                flag = SURVIVAL_FLAG_COMPLETE_INFO_SOME_SURVIVAL; // flag=1
            else
                flag = SURVIVAL_FLAG_COMPLETE_INFO_NO_SURVIVAL; // flag=0

            if (presumeAlive) {
                rec._recordResult.setSurvivalMonthsPresumedAlive(StringUtils.leftPad(String.valueOf(diffInMonth), 4, "0"));
                rec._recordResult.setSurvivalMonthsFlagPresumedAlive(flag);
            }
            else {
                rec._recordResult.setSurvivalMonths(StringUtils.leftPad(String.valueOf(diffInMonth), 4, "0"));
                rec._recordResult.setSurvivalMonthsFlag(flag);
            }
        }

        // STEP 4 - go through the records IN REVERSE ORDER and fix the issue where a person could have a DX with some missing codes
        // coded as "could be 0 days" followed by a tumor that could not be zero days (with or without some missing) - therefore the
        // earlier tumor can't be 0 days.
        boolean survivalGreaterThanZero = false, survivalGreaterThanZeroPA = false;
        for (int i = records.size() - 1; i >= 0; i--) {
            SurvivalTimeOutputRecordDto rec = records.get(i)._recordResult;
            String flag = rec.getSurvivalMonthsFlag();
            String flagPA = rec.getSurvivalMonthsFlagPresumedAlive();
            if (SURVIVAL_FLAG_COMPLETE_INFO_SOME_SURVIVAL.equals(flag) || SURVIVAL_FLAG_MISSING_INFO_SOME_SURVIVAL.equals(flag))
                survivalGreaterThanZero = true;
            if (SURVIVAL_FLAG_COMPLETE_INFO_SOME_SURVIVAL.equals(flagPA) || SURVIVAL_FLAG_MISSING_INFO_SOME_SURVIVAL.equals(flagPA))
                survivalGreaterThanZeroPA = true;

            if (SURVIVAL_FLAG_MISSING_INFO_NO_SURVIVAL_POSSIBLE.equals(flag) && survivalGreaterThanZero)
                rec.setSurvivalMonthsFlag(SURVIVAL_FLAG_MISSING_INFO_SOME_SURVIVAL);

            if (SURVIVAL_FLAG_MISSING_INFO_NO_SURVIVAL_POSSIBLE.equals(flagPA) && survivalGreaterThanZeroPA)
                rec.setSurvivalMonthsFlagPresumedAlive(SURVIVAL_FLAG_MISSING_INFO_SOME_SURVIVAL);
        }
    }

    private static class InternalRecDto implements Comparable<InternalRecDto> {

        SurvivalTimeInputRecordDto _originalRecord;
        SurvivalTimeOutputRecordDto _recordResult;
        int _year, _month, _day, _yearSafe, _monthSafe, _daySafe, _seqNum;
        int _diffInDays, _diffInDaysPA;

        public InternalRecDto() {
            _recordResult = new SurvivalTimeOutputRecordDto();
        }

        public InternalRecDto(SurvivalTimeInputRecordDto record, SurvivalTimeOutputRecordDto recordResult) {
            _originalRecord = record;
            _recordResult = recordResult;

            String yearStr = record.getDateOfDiagnosisYear();
            String monthStr = record.getDateOfDiagnosisMonth();
            String dayStr = record.getDateOfDiagnosisDay();
            _year = _yearSafe = NumberUtils.isDigits(yearStr) ? Integer.parseInt(yearStr) : 9999;
            _month = _monthSafe = NumberUtils.isDigits(monthStr) ? Integer.parseInt(monthStr) : 99;
            _day = _daySafe = NumberUtils.isDigits(dayStr) ? Integer.parseInt(dayStr) : 99;
            if (_month == 99)
                _day = _daySafe = 99;

            // sequence number
            //    the sequence numbers might be used to determine the order; there are two families of sequences: federal (00-59, 98, 99) and non-federal (60-97);
            //    sine the non-federal need to always be after the federal, let's add 100 to all the non-federal (making them 160-197)
            String seqNumStr = record.getSequenceNumberCentral();
            _seqNum = NumberUtils.isDigits(seqNumStr) ? Integer.parseInt(seqNumStr) : -1;
            if (_seqNum >= 60 && _seqNum <= 97)
                _seqNum = _seqNum + 100;
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

        public int compareDateOnly(InternalRecDto other) {
            if (_year == 9999 || other._year == 9999)
                return 0;
            else if (_year != other._year)
                return _year - other._year;
            else {
                if (_month == 99 || other._month == 99)
                    return 0;
                else if (_month != other._month)
                    return _month - other._month;
                else {
                    if (_day == 99 || other._day == 99 || this._day == other._day)
                        return 0;
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

    /**
     * This method is added to avoid the situation where a > b and b > c and c > a
     * Valid dates are always ordered first and then we add the invalid ones, we compare the invalid one starting from the last one
     * Example:
     * rec 1: 01: 2015/3/5
     * rec 2: 02: 2015/99/99
     * rec 3: 60: 2015/2/23
     * We want 3 and 1 to be compared first which 3 is first and 1 is second. (3, 1)
     * Then we compare the invalid one starting from the last, that is rec 1 in this case.
     * The order will become 3, 1, 2
     */
    private static List<InternalRecDto> sortTempRecords(List<InternalRecDto> list) {
        List<InternalRecDto> validRecords = new ArrayList<>();
        List<InternalRecDto> dayMissingRecords = new ArrayList<>();
        List<InternalRecDto> monthMissingRecords = new ArrayList<>();
        List<InternalRecDto> yearMissingRecords = new ArrayList<>();
        for (InternalRecDto dto : list) {
            if (dto._year == 9999)
                yearMissingRecords.add(dto);
            else if (dto._month == 99)
                monthMissingRecords.add(dto);
            else if (dto._day == 99)
                dayMissingRecords.add(dto);
            else
                validRecords.add(dto);
        }

        List<InternalRecDto> result = new ArrayList<>(validRecords);
        Collections.sort(result);

        //Handle day missing records
        sortTempRecords(result, dayMissingRecords);
        //Handle Month missing records
        sortTempRecords(result, monthMissingRecords);
        //Handle year missing record
        sortTempRecords(result, yearMissingRecords);
        return result;
    }

    private static void sortTempRecords(List<InternalRecDto> result, List<InternalRecDto> subList) {
        Collections.sort(subList);
        if (result.isEmpty())
            result.addAll(subList);
        else {
            for (InternalRecDto r : subList) {
                for (int i = result.size() - 1; i >= 0; i--) {
                    //if the subList record's date is later than the sorted result record, append it after sorted record
                    if (r.compareDateOnly(result.get(i)) > 0) {
                        result.add(i + 1, r);
                        break;
                    }
                    else if (r.compareDateOnly(result.get(i)) == 0) {
                        if ((r._seqNum < 100 && r._seqNum >= result.get(i)._seqNum) || (r._seqNum > 100 && result.get(i)._seqNum > 100 && r._seqNum >= result.get(i)._seqNum)) {
                            result.add(i + 1, r);
                            break;
                        }
                        else if (r._seqNum > 100) {
                            List<InternalRecDto> sortedRecordsWithPotentialLaterDate = new ArrayList<>();
                            for (int j = 0; j <= i; j++)
                                if (r.compareDateOnly(result.get(j)) == 0)
                                    sortedRecordsWithPotentialLaterDate.add(result.get(j));

                            if (!hasConflictedSequence(r._seqNum, sortedRecordsWithPotentialLaterDate)) {
                                result.add(i + 1, r);
                                break;
                            }
                        }
                    }
                    if (i == 0)
                        result.add(0, r);
                }
            }
        }
    }

    //This method is only for non federal, it checks if current non-federal should go before another non-federal
    private static boolean hasConflictedSequence(int seq, List<InternalRecDto> sortedResult) {
        //if all other records are federal, the non federal should go last
        if (sortedResult.stream().noneMatch(r -> r._seqNum > 100))
            return false;
        //if the other non federal's are not in the right order, don't bother fixing this
        for (int i = 0; i < sortedResult.size() - 1; i++)
            for (int j = i + 1; j <= sortedResult.size() - 1; j++)
                if (sortedResult.get(i)._seqNum > 100 && sortedResult.get(j)._seqNum > 100 && sortedResult.get(i)._seqNum > sortedResult.get(j)._seqNum)
                    return false;
        //If they are in order and if current sequence number is less than one of them, we need to move the current record
        for (int i = sortedResult.size() - 1; i >= 0; i--)
            if (sortedResult.get(i)._seqNum > 100)
                return seq <= sortedResult.get(i)._seqNum;

        return false;
    }
}
