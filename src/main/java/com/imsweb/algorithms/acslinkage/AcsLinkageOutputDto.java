package com.imsweb.algorithms.acslinkage;

import org.apache.commons.lang3.StringUtils;

public class AcsLinkageOutputDto {

    private String
            _yostQuintile0610US, _yostQuintile0610State, _acsPctPov0610AllRaces, _acsPctPov0610White, _acsPctPov0610Black,
            _yostQuintile1014US, _yostQuintile1014State, _acsPctPov1014AllRaces, _acsPctPov1014White, _acsPctPov1014Black,
            _yostQuintile1317US, _yostQuintile1317State, _acsPctPov1317AllRaces, _acsPctPov1317White, _acsPctPov1317Black,
            _acsPctPov0610AIAN, _acsPctPov0610AsianNHOPI, _acsPctPov0610OtherMulti, _acsPctPov0610WhiteNonHisp, _acsPctPov0610Hispanic,
            _acsPctPov1014AIAN, _acsPctPov1014AsianNHOPI, _acsPctPov1014OtherMulti, _acsPctPov1014WhiteNonHisp, _acsPctPov1014Hispanic,
            _acsPctPov1317AIAN, _acsPctPov1317AsianNHOPI, _acsPctPov1317OtherMulti, _acsPctPov1317WhiteNonHisp, _acsPctPov1317Hispanic;

    public AcsLinkageOutputDto() {
        _yostQuintile0610US = "";
        _yostQuintile0610State = "";
        _acsPctPov0610AllRaces = "";
        _acsPctPov0610White = "";
        _acsPctPov0610Black = "";
        _acsPctPov0610AIAN = "";
        _acsPctPov0610AsianNHOPI = "";
        _acsPctPov0610OtherMulti = "";
        _acsPctPov0610WhiteNonHisp = "";
        _acsPctPov0610Hispanic = "";

        _yostQuintile1014US = "";
        _yostQuintile1014State = "";
        _acsPctPov1014AllRaces = "";
        _acsPctPov1014White = "";
        _acsPctPov1014Black = "";
        _acsPctPov1014AIAN = "";
        _acsPctPov1014AsianNHOPI = "";
        _acsPctPov1014OtherMulti = "";
        _acsPctPov1014WhiteNonHisp = "";
        _acsPctPov1014Hispanic = "";

        _yostQuintile1317US = "";
        _yostQuintile1317State = "";
        _acsPctPov1317AllRaces = "";
        _acsPctPov1317White = "";
        _acsPctPov1317Black = "";
        _acsPctPov1317AIAN = "";
        _acsPctPov1317AsianNHOPI = "";
        _acsPctPov1317OtherMulti = "";
        _acsPctPov1317WhiteNonHisp = "";
        _acsPctPov1317Hispanic = "";
    }

    public String getYostQuintile0610US() { return _yostQuintile0610US; }

    public String getYostQuintile0610State() { return _yostQuintile0610State; }

    public String getAcsPctPov0610AllRaces() { return _acsPctPov0610AllRaces; }

    public String getAcsPctPov0610White() { return _acsPctPov0610White; }

    public String getAcsPctPov0610Black() { return _acsPctPov0610Black; }

    public String getAcsPctPov0610AIAN() { return _acsPctPov0610AIAN; }

    public String getAcsPctPov0610AsianNHOPI() { return _acsPctPov0610AsianNHOPI; }

    public String getAcsPctPov0610OtherMulti() { return _acsPctPov0610OtherMulti; }

    public String getAcsPctPov0610WhiteNonHisp() { return _acsPctPov0610WhiteNonHisp; }

    public String getAcsPctPov0610Hispanic() { return _acsPctPov0610Hispanic; }

    public String getYostQuintile1014US() { return _yostQuintile1014US; }

    public String getYostQuintile1014State() { return _yostQuintile1014State; }

    public String getAcsPctPov1014AllRaces() { return _acsPctPov1014AllRaces; }

    public String getAcsPctPov1014White() { return _acsPctPov1014White; }

    public String getAcsPctPov1014Black() { return _acsPctPov1014Black; }

    public String getAcsPctPov1014AIAN() { return _acsPctPov1014AIAN; }

    public String getAcsPctPov1014AsianNHOPI() { return _acsPctPov1014AsianNHOPI; }

    public String getAcsPctPov1014OtherMulti() { return _acsPctPov1014OtherMulti; }

    public String getAcsPctPov1014WhiteNonHisp() { return _acsPctPov1014WhiteNonHisp; }

    public String getAcsPctPov1014Hispanic() { return _acsPctPov1014Hispanic; }

    public String getYostQuintile1317US() { return _yostQuintile1317US; }

    public String getYostQuintile1317State() { return _yostQuintile1317State; }

    public String getAcsPctPov1317AllRaces() { return _acsPctPov1317AllRaces; }

    public String getAcsPctPov1317White() { return _acsPctPov1317White; }

    public String getAcsPctPov1317Black() { return _acsPctPov1317Black; }

    public String getAcsPctPov1317AIAN() { return _acsPctPov1317AIAN; }

    public String getAcsPctPov1317AsianNHOPI() { return _acsPctPov1317AsianNHOPI; }

    public String getAcsPctPov1317OtherMulti() { return _acsPctPov1317OtherMulti; }

    public String getAcsPctPov1317WhiteNonHisp() { return _acsPctPov1317WhiteNonHisp; }

    public String getAcsPctPov1317Hispanic() { return _acsPctPov1317Hispanic; }

    public void setYostQuintile0610US(String str) { _yostQuintile0610US = str; }

    public void setYostQuintile0610State(String str) { _yostQuintile0610State = str; }

    public void setAcsPctPov0610AllRaces(String str) { _acsPctPov0610AllRaces = cleanup(str); }

    public void setAcsPctPov0610White(String str) { _acsPctPov0610White = cleanup(str); }

    public void setAcsPctPov0610Black(String str) { _acsPctPov0610Black = cleanup(str); }

    public void setAcsPctPov0610AIAN(String str) { _acsPctPov0610AIAN = cleanup(str); }

    public void setAcsPctPov0610AsianNHOPI(String str) { _acsPctPov0610AsianNHOPI = cleanup(str); }

    public void setAcsPctPov0610OtherMulti(String str) { _acsPctPov0610OtherMulti = cleanup(str); }

    public void setAcsPctPov0610WhiteNonHisp(String str) { _acsPctPov0610WhiteNonHisp = cleanup(str); }

    public void setAcsPctPov0610Hispanic(String str) { _acsPctPov0610Hispanic = cleanup(str); }

    public void setYostQuintile1014US(String str) { _yostQuintile1014US = str; }

    public void setYostQuintile1014State(String str) { _yostQuintile1014State = str; }

    public void setAcsPctPov1014AllRaces(String str) { _acsPctPov1014AllRaces = cleanup(str); }

    public void setAcsPctPov1014White(String str) { _acsPctPov1014White = cleanup(str); }

    public void setAcsPctPov1014Black(String str) { _acsPctPov1014Black = cleanup(str); }

    public void setAcsPctPov1014AIAN(String str) { _acsPctPov1014AIAN = cleanup(str); }

    public void setAcsPctPov1014AsianNHOPI(String str) { _acsPctPov1014AsianNHOPI = cleanup(str); }

    public void setAcsPctPov1014OtherMulti(String str) { _acsPctPov1014OtherMulti = cleanup(str); }

    public void setAcsPctPov1014WhiteNonHisp(String str) { _acsPctPov1014WhiteNonHisp = cleanup(str); }

    public void setAcsPctPov1014Hispanic(String str) { _acsPctPov1014Hispanic = cleanup(str); }

    public void setYostQuintile1317US(String str) { _yostQuintile1317US = str; }

    public void setYostQuintile1317State(String str) { _yostQuintile1317State = str; }

    public void setAcsPctPov1317AllRaces(String str) { _acsPctPov1317AllRaces = cleanup(str); }

    public void setAcsPctPov1317White(String str) { _acsPctPov1317White = cleanup(str); }

    public void setAcsPctPov1317Black(String str) { _acsPctPov1317Black = cleanup(str); }

    public void setAcsPctPov1317AIAN(String str) { _acsPctPov1317AIAN = cleanup(str); }

    public void setAcsPctPov1317AsianNHOPI(String str) { _acsPctPov1317AsianNHOPI = cleanup(str); }

    public void setAcsPctPov1317OtherMulti(String str) { _acsPctPov1317OtherMulti = cleanup(str); }

    public void setAcsPctPov1317WhiteNonHisp(String str) { _acsPctPov1317WhiteNonHisp = cleanup(str); }

    public void setAcsPctPov1317Hispanic(String str) { _acsPctPov1317Hispanic = cleanup(str); }

    private String cleanup(String s) {
        return StringUtils.isBlank(s) ? s : StringUtils.leftPad(StringUtils.replace(s, ".", ""), 6, "0");
    }
}

