/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tumorsizeovertime;

public class TumorSizeOverTimeInputDto {
    private String _dxYear;
    private String _site;
    private String _hist;
    private String _behavior;
    private String _eodTumorSize;
    private String _csTumorSize;
    private String _tumorSizeSummary;

    public String getDxYear() {
        return _dxYear;
    }

    public void setDxYear(String dxYear) {
        _dxYear = dxYear;
    }

    public String getSite() {
        return _site;
    }

    public void setSite(String site) {
        _site = site;
    }

    public String getHist() {
        return _hist;
    }

    public void setHist(String hist) {
        _hist = hist;
    }

    public String getBehavior() {
        return _behavior;
    }

    public void setBehavior(String behavior) {
        _behavior = behavior;
    }

    public String getEodTumorSize() {
        return _eodTumorSize;
    }

    public void setEodTumorSize(String eodTumorSize) {
        _eodTumorSize = eodTumorSize;
    }

    public String getCsTumorSize() {
        return _csTumorSize;
    }

    public void setCsTumorSize(String csTumorSize) {
        _csTumorSize = csTumorSize;
    }

    public String getTumorSizeSummary() {
        return _tumorSizeSummary;
    }

    public void setTumorSizeSummary(String tumorSizeSummary) {
        _tumorSizeSummary = tumorSizeSummary;
    }
}
