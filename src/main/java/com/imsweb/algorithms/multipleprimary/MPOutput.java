/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary;

import java.util.ArrayList;
import java.util.List;

import com.imsweb.algorithms.multipleprimary.MPUtils.MPResult;

public class MPOutput {

    private MPResult _result;

    private String _reason;
    
    private List<MPRule> _appliedRules;

    public MPOutput() {
        _appliedRules = new ArrayList<>();
    }

    public MPResult getResult() {
        return _result;
    }

    public void setResult(MPResult result) {
        _result = result;
    }

    public String getReason() {
        return _reason;
    }

    public void setReason(String reason) {
        _reason = reason;
    }

    public List<MPRule> getAppliedRules() {
        return _appliedRules;
    }
}
