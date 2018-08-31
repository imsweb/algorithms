/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

public class RuralUrbanOutputDto {

    private String _urbanRuralIndicatorCode2000;
    private String _urbanRuralIndicatorCode2010;
    
    // null means no percentage available...
    private Float _urbanRuralIndicatorCode2000Percentage;
    private Float _urbanRuralIndicatorCode2010Percentage;

    private String _ruralUrbanCommutingArea2000;
    private String _ruralUrbanCommutingArea2010;

    private String _ruralUrbanContinuum1993;
    private String _ruralUrbanContinuum2003;
    private String _ruralUrbanContinuum2013;

    public RuralUrbanOutputDto() {
    }

    public String getUrbanRuralIndicatorCode2000() {
        return _urbanRuralIndicatorCode2000;
    }

    public Float getUrbanRuralIndicatorCode2000Percentage() {
        return _urbanRuralIndicatorCode2000Percentage;
    }

    public String getUrbanRuralIndicatorCode2010() {
        return _urbanRuralIndicatorCode2010;
    }

    public Float getUrbanRuralIndicatorCode2010Percentage() {
        return _urbanRuralIndicatorCode2010Percentage;
    }

    public String getRuralUrbanCommutingArea2000() {
        return _ruralUrbanCommutingArea2000;
    }

    public String getRuralUrbanCommutingArea2010() {
        return _ruralUrbanCommutingArea2010;
    }

    public String getRuralUrbanContinuum1993() {
        return _ruralUrbanContinuum1993;
    }

    public String getRuralUrbanContinuum2003() {
        return _ruralUrbanContinuum2003;
    }

    public String getRuralUrbanContinuum2013() {
        return _ruralUrbanContinuum2013;
    }

    public void setUrbanRuralIndicatorCode2000(String urbanRuralIndicatorCode2000) {
        _urbanRuralIndicatorCode2000 = urbanRuralIndicatorCode2000;
    }

    public void setUrbanRuralIndicatorCode2000Percentage(Float urbanRuralIndicatorCode2000Percentage) {
        _urbanRuralIndicatorCode2000Percentage = urbanRuralIndicatorCode2000Percentage;
    }

    public void setUrbanRuralIndicatorCode2010(String urbanRuralIndicatorCode2010) {
        _urbanRuralIndicatorCode2010 = urbanRuralIndicatorCode2010;
    }

    public void setUrbanRuralIndicatorCode2010Percentage(Float urbanRuralIndicatorCode2010Percentage) {
        _urbanRuralIndicatorCode2010Percentage = urbanRuralIndicatorCode2010Percentage;
    }

    public void setRuralUrbanCommutingArea2000(String ruralUrbanCommutingArea2000) {
        _ruralUrbanCommutingArea2000 = ruralUrbanCommutingArea2000;
    }

    public void setRuralUrbanCommutingArea2010(String ruralUrbanCommutingArea2010) {
        _ruralUrbanCommutingArea2010 = ruralUrbanCommutingArea2010;
    }

    public void setRuralUrbanContinuum1993(String ruralUrbanContinuum1993) {
        _ruralUrbanContinuum1993 = ruralUrbanContinuum1993;
    }

    public void setRuralUrbanContinuum2003(String ruralUrbanContinuum2003) {
        _ruralUrbanContinuum2003 = ruralUrbanContinuum2003;
    }

    public void setRuralUrbanContinuum2013(String ruralUrbanContinuum2013) {
        _ruralUrbanContinuum2013 = ruralUrbanContinuum2013;
    }

}
