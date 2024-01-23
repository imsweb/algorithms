/*
 * Copyright (C) 2024 Information Management Services, Inc.
 */
package com.imsweb.algorithms.tumorsizeovertime;

public class TumorSizeOverTimeInputDto {
    private int _dxYear;
    private String _site;
    private String _hist;
    private String _behavior;
    private String _tumorSize;

    public int getDxYear() {
        return _dxYear;
    }

    public void setDxYear(int dxYear) {
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

    public String getTumorSize() {
        return _tumorSize;
    }

    public void setTumorSize(String tumorSize) {
        _tumorSize = tumorSize;
    }
}
