/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.surgery;

import java.util.List;

public class SurgeryTableDto {

    private String _title;
    private String _siteInclusion;
    private String _histExclusion;
    private String _histInclusion;
    private String _preNote;
    private String _postNote;
    private List<SurgeryRowDto> _row;

    public String getSiteInclusion() {
        return _siteInclusion;
    }

    public void setSiteInclusion(String siteInclusion) {
        this._siteInclusion = siteInclusion;
    }

    public String getHistExclusion() {
        return _histExclusion;
    }

    public void setHistExclusion(String histExclusion) {
        this._histExclusion = histExclusion;
    }

    public String getHistInclusion() {
        return _histInclusion;
    }

    public void setHistInclusion(String histInclusion) {
        this._histInclusion = histInclusion;
    }

    public String getPreNote() {
        return _preNote;
    }

    public void setPreNote(String preNote) {
        this._preNote = preNote;
    }

    public List<SurgeryRowDto> getRow() {
        return _row;
    }

    public void setRow(List<SurgeryRowDto> row) {
        this._row = row;
    }

    public String getPostNote() {
        return _postNote;
    }

    public void setPostNote(String postNote) {
        this._postNote = postNote;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

}
