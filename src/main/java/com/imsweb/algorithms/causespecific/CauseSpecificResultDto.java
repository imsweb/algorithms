/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

public class CauseSpecificResultDto {

    private String _causeSpecificDeathClassification;

    private String _causeOtherDeathClassification;

    public String getCauseSpecificDeathClassification() {
        return _causeSpecificDeathClassification;
    }

    public void setCauseSpecificDeathClassification(String causeSpecificDeathClassification) {
        _causeSpecificDeathClassification = causeSpecificDeathClassification;
    }

    public String getCauseOtherDeathClassification() {
        return _causeOtherDeathClassification;
    }

    public void setCauseOtherDeathClassification(String causeOtherDeathClassification) {
        _causeOtherDeathClassification = causeOtherDeathClassification;
    }
}
