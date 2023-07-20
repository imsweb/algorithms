package com.imsweb.algorithms.seersiterecode;

import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: depryf
 * Date: 8/22/12
 */
@SuppressWarnings("unused")
public class SeerSiteGroupDto {

    private String _id;

    private String _name;

    private Integer _level;

    private String _siteInclusions;

    private String _siteExclusions;

    private String _histologyInclusions;

    private String _histologyExclusions;

    private String _behaviorInclusions;

    private String _minDxYear;

    private String _maxDxYear;

    private String _recode;

    private List<String> _childrenRecodes;

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public Integer getLevel() {
        return _level;
    }

    public String getSiteInclusions() {
        return _siteInclusions;
    }

    public String getSiteExclusions() {
        return _siteExclusions;
    }

    public String getHistologyInclusions() {
        return _histologyInclusions;
    }

    public String getHistologyExclusions() {
        return _histologyExclusions;
    }

    public String getRecode() {
        return _recode;
    }

    public List<String> getChildrenRecodes() {
        return _childrenRecodes;
    }

    public void setId(String id) {
        _id = id;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setLevel(Integer level) {
        _level = level;
    }

    public void setSiteInclusions(String siteInclusions) {
        _siteInclusions = siteInclusions;
    }

    public void setSiteExclusions(String siteExclusions) {
        _siteExclusions = siteExclusions;
    }

    public void setHistologyInclusions(String histologyInclusions) {
        _histologyInclusions = histologyInclusions;
    }

    public void setHistologyExclusions(String histologyExclusions) {
        _histologyExclusions = histologyExclusions;
    }

    public String getBehaviorInclusions() {
        return _behaviorInclusions;
    }

    public void setBehaviorInclusions(String behaviorInclusions) {
        _behaviorInclusions = behaviorInclusions;
    }

    public String getMinDxYear() {
        return _minDxYear;
    }

    public void setMinDxYear(String minDxYear) {
        _minDxYear = minDxYear;
    }

    public String getMaxDxYear() {
        return _maxDxYear;
    }

    public void setMaxDxYear(String maxDxYear) {
        _maxDxYear = maxDxYear;
    }

    public void setRecode(String recode) {
        _recode = recode;
    }

    public void setChildrenRecodes(List<String> childrenRecodes) {
        _childrenRecodes = childrenRecodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        return Objects.equals(_id, ((SeerSiteGroupDto)o)._id);

    }

    @Override
    public int hashCode() {
        return _id != null ? _id.hashCode() : 0;
    }
}
