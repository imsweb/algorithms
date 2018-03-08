/*
 * Copyright (C) 2017 Information Management Services, Inc.
 */
package com.imsweb.algorithms.icd;

public class IcdO3Entry {

    private String _sourceCode;
    private String _site;
    private String _histology;
    private String _behavior;
    private String _grade;
    private String _laterality;
    private String _reportable;
    private String _sex;

    public String getSourceCode() {
        return _sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        _sourceCode = sourceCode;
    }

    public String getSite() {
        return _site;
    }

    public void setSite(String site) {
        _site = site;
    }

    public String getHistology() {
        return _histology;
    }

    public void setHistology(String histology) {
        _histology = histology;
    }

    public String getBehavior() {
        return _behavior;
    }

    public void setBehavior(String behavior) {
        _behavior = behavior;
    }

    public String getGrade() {
        return _grade;
    }

    public void setGrade(String grade) {
        _grade = grade;
    }

    public String getLaterality() {
        return _laterality;
    }

    public void setLaterality(String laterality) {
        _laterality = laterality;
    }

    public String getReportable() {
        return _reportable;
    }

    public void setReportable(String reportable) {
        _reportable = reportable;
    }

    public String getSex() {
        return _sex;
    }

    public void setSex(String sex) {
        _sex = sex;
    }
}
