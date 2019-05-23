/*
 * Copyright (C) 2013 Information Management Services, Inc.
 * Created on April 29, 2013 by Sewbesew Bekele, IMS
 */
package com.imsweb.algorithms.napiia;

/**
 * Napiia results include calculated napiia value, a condition to decide whether a human review is needed or not and a reason if human review is required.
 */
public class NapiiaResultsDto {

    private String _napiiaValue;

    private Boolean _needsHumanReview;

    private String _reasonForReview;

    public NapiiaResultsDto() {
        _napiiaValue = null;
        _needsHumanReview = Boolean.FALSE;
        _reasonForReview = null;
    }

    public void setNapiiaValue(String napiia) {
        _napiiaValue = napiia;
    }

    public void setNeedsHumanReview(Boolean needsReview) {
        _needsHumanReview = needsReview;
    }

    public void setReasonForReview(String reason) {
        _reasonForReview = reason;
    }

    public String getNapiiaValue() {
        return _napiiaValue;
    }

    public Boolean getNeedsHumanReview() {
        return _needsHumanReview;
    }

    public String getReasonForReview() {
        return _reasonForReview;
    }
}
