/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.icd;

public class IcdO2Entry {

    public enum ConversionResultType {
        CONVERSION_SUCCESSFUL,
        CONVERSION_SUCCESSFUL_NEEDS_HAND_REVIEW,
        CONVERSION_FAILED_INVALID_SITE,
        CONVERSION_FAILED_INVALID_HISTOLOGY,
        CONVERSION_FAILED_INVALID_BEHAVIOR
    }

    private String _site;
    private String _histology;
    private String _behavior;
    private ConversionResultType _conversionResult;

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

    public ConversionResultType getConversionResult() {
        return _conversionResult;
    }

    public void setConversionResult(ConversionResultType conversionResult) {
        _conversionResult = conversionResult;
    }

}
