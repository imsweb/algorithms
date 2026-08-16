package com.imsweb.algorithms.yostacspoverty;

import com.imsweb.algorithms.StateCountyTractInputDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import static com.imsweb.algorithms.yostacspoverty.YostAcsPovertyUtils.YOST_ACS_CENSUS_TRACT_PIVOT_YEAR;

public class YostAcsPovertyInputDto extends StateCountyTractInputDto {

    private static final String MISSING_YEAR = "";

    private String _dateOfDiagnosis;
    private String _yearOfDiagnosis;

    @Override
    public void applyRecodes() {
        super.applyRecodes();
        _dateOfDiagnosis = _dateOfDiagnosis == null ? "" : _dateOfDiagnosis.trim();
        _yearOfDiagnosis = _yearOfDiagnosis == null ? "" : _yearOfDiagnosis.trim();
    }

    public int computeYearOfDiagnosis() {
        if (isValidYear(_yearOfDiagnosis)) {
            return Integer.parseInt(_yearOfDiagnosis);
        }
        return -1;
    }

    public String getDateOfDiagnosis() {
        return _dateOfDiagnosis;
    }

    public void setDateOfDiagnosis(String dateOfDiagnosis) {
        _dateOfDiagnosis = dateOfDiagnosis;
        _yearOfDiagnosis = dateOfDiagnosis == null ? null : StringUtils.rightPad(dateOfDiagnosis, 4).substring(0, 4).trim();
    }

    public boolean hasInvalidStateCountyCensusTractOrYear() {
        if (isInvalidState(getAddressAtDxState()) || isInvalidCounty(getCountyAtDxAnalysis()) || isInvalidYear(_yearOfDiagnosis))
            return true;
        // if year is missing we don't know which census tract is the right one to use, so just check both
        if (isUnknownYear(_yearOfDiagnosis))
            return isInvalidCensusTract(getCensusTract2010()) || isInvalidCensusTract(getCensusTract2020());
        // year isn't missing, so check the census tract we'll end up using for the lookup
        int dxYear = Integer.parseInt(_yearOfDiagnosis);
        CensusTract censusTract = dxYear <= YOST_ACS_CENSUS_TRACT_PIVOT_YEAR ? CensusTract.CENSUS_2010 : CensusTract.CENSUS_2020;
        return isInvalidCensusTract(getCensusTractVariable(censusTract));
    }

    public boolean hasUnknownStateCountyCensusTractOrYear() {
        if (isUnknownState(getAddressAtDxState()) || isUnknownCounty(getCountyAtDxAnalysis()) || isUnknownYear(_yearOfDiagnosis))
            return true;
        // if year is invalid we don't know which census tract is the right one to use, so just check both
        if (isInvalidYear(_yearOfDiagnosis))
            return isUnknownCensusTract(getCensusTract2010()) || isUnknownCensusTract(getCensusTract2020());
        // year isn't invalid, so check the census tract we'll end up using for the lookup
        int dxYear = Integer.parseInt(_yearOfDiagnosis);
        CensusTract censusTract = dxYear <= YOST_ACS_CENSUS_TRACT_PIVOT_YEAR ? CensusTract.CENSUS_2010 : CensusTract.CENSUS_2020;
        return isUnknownCensusTract(getCensusTractVariable(censusTract));
    }

    public static boolean isInvalidYear(String year) {
        return !(isValidYear(year) || isUnknownYear(year));
    }

    public static boolean isUnknownYear(String year) {
        return MISSING_YEAR.equals(year);
    }

    // this is private because it's meant to be more of a helper function
    // I want users of this class to use the "Invalid" and "Unknown" functions
    private static boolean isValidYear(String year) {
        return year != null && year.length() == 4 && NumberUtils.isDigits(year);
    }

}
