/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary;

import java.util.ArrayList;
import java.util.List;

import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;

public abstract class MPRule {

    private String _groupId;

    private String _step;

    private String _question;

    private String _reason;

    private MPResult _result;

    private List<String> _notes;

    private List<String> _examples;

    public MPRule(String groupId, String step, MPResult result) {
        _groupId = groupId;
        _step = step;
        _result = result;
        _notes = new ArrayList<>();
        _examples = new ArrayList<>();
    }

    public String getGroupId() {
        return _groupId;
    }

    public String getStep() {
        return _step;
    }

    public String getQuestion() {
        return _question;
    }

    public void setQuestion(String question) {
        _question = question;
    }

    public String getReason() {
        return _reason;
    }

    public void setReason(String reason) {
        _reason = reason;
    }

    public MPResult getResult() {
        return _result;
    }

    public List<String> getNotes() {
        return _notes;
    }

    public List<String> getExamples() {
        return _examples;
    }

    public abstract MPRuleResult apply(MPInput i1, MPInput i2);
}
