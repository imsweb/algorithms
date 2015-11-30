/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.ruralurban;

public class RuralUrbanOutputDto {

    private String _ruralUrbanCensus2000;
    private String _ruralUrbanCensus2010;
    
    // null means no percentage available...
    private Float _ruralUrbanCensus2000Percentage;
    private Float _ruralUrbanCensus2010Percentage;

    private String _ruralUrbanCommutingArea2000;
    private String _ruralUrbanCommutingArea2010;

    private String _ruralUrbanContinuum1993;
    private String _ruralUrbanContinuum2003;
    private String _ruralUrbanContinuum2013;

    public RuralUrbanOutputDto() {
    }

    public String getRuralUrbanCensus2000() {
        return _ruralUrbanCensus2000;
    }

    public Float getRuralUrbanCensus2000Percentage() {
        return _ruralUrbanCensus2000Percentage;
    }

    public String getRuralUrbanCensus2010() {
        return _ruralUrbanCensus2010;
    }

    public Float getRuralUrbanCensus2010Percentage() {
        return _ruralUrbanCensus2010Percentage;
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

    public void setRuralUrbanCensus2000(String ruralUrbanCensus2000) {
        _ruralUrbanCensus2000 = ruralUrbanCensus2000;
    }

    public void setRuralUrbanCensus2000Percentage(Float ruralUrbanCensus2000Percentage) {
        _ruralUrbanCensus2000Percentage = ruralUrbanCensus2000Percentage;
    }

    public void setRuralUrbanCensus2010(String ruralUrbanCensus2010) {
        _ruralUrbanCensus2010 = ruralUrbanCensus2010;
    }

    public void setRuralUrbanCensus2010Percentage(Float ruralUrbanCensus2010Percentage) {
        _ruralUrbanCensus2010Percentage = ruralUrbanCensus2010Percentage;
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
