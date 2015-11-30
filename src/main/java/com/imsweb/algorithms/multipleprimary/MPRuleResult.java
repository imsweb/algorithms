/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.multipleprimary;

import com.imsweb.algorithms.multipleprimary.MPUtils.RuleResult;

public class MPRuleResult {

    private RuleResult _result;

    private String _message;

    public MPRuleResult() {
    }

    public RuleResult getResult() {
        return _result;
    }

    public void setResult(RuleResult result) {
        _result = result;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }
}
