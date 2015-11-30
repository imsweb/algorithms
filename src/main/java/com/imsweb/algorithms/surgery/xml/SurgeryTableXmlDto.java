
package com.imsweb.algorithms.surgery.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("surgery-table")
public class SurgeryTableXmlDto {

    @XStreamAsAttribute
    @XStreamAlias("title")
    private String _title;

    @XStreamAlias("site-inclusion")
    private String _siteInclusion;

    @XStreamAlias("hist-exclusion")
    private String _histExclusion;

    @XStreamAlias("hist-inclusion")
    private String _histInclusion;

    @XStreamAlias("pre-note")
    private String _preNote;

    @XStreamImplicit
    private List<SurgeryRowXmlDto> _row;

    @XStreamAlias("post-note")
    private String _postNote;

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public String getSiteInclusion() {
        return _siteInclusion;
    }

    public void setSiteInclusion(String siteInclusion) {
        _siteInclusion = siteInclusion;
    }

    public String getHistExclusion() {
        return _histExclusion;
    }

    public void setHistExclusion(String histExclusion) {
        _histExclusion = histExclusion;
    }

    public String getHistInclusion() {
        return _histInclusion;
    }

    public void setHistInclusion(String histInclusion) {
        _histInclusion = histInclusion;
    }

    public String getPreNote() {
        return _preNote;
    }

    public void setPreNote(String preNote) {
        _preNote = preNote;
    }

    public List<SurgeryRowXmlDto> getRow() {
        return _row;
    }

    public void setRow(List<SurgeryRowXmlDto> row) {
        _row = row;
    }

    public String getPostNote() {
        return _postNote;
    }

    public void setPostNote(String postNote) {
        _postNote = postNote;
    }
}
