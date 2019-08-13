/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

public class PrcdaUihoOutputDto {

    private String _prcda, _uiho, _uihoFacility;

    public PrcdaUihoOutputDto() {  }

    public String getPRCDA() {
        return _prcda;
    }

    public String getUIHO() { return _uiho; }

    public String getUIHOFacility() { return _uihoFacility; }

    public void setPRCDA(String str) {
        _prcda = str;
    }

    public void setUIHO(String str) {
        _uiho = str;
    }

    public void setUIHOFacility(String str) {
        _uihoFacility = str;
    }
}
