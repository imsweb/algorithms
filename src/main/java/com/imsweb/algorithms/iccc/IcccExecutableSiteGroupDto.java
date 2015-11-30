package com.imsweb.algorithms.iccc;

import java.util.List;

import org.apache.commons.lang3.Range;

/**
 * Internal site group DTO used to calculate the recode, this class should not be used outside of SEER*Utils...
 * User: depryf
 * Date: 8/22/12
 */
public class IcccExecutableSiteGroupDto {

    /**
     * Unique identifier
     */
    private String _id;

    /**
     * Name of the group
     */
    private String _name;

    /**
     * Site inclusions (single integer values or ranges)
     */
    private List<Object> _siteInclusions;

    /**
     * Site exclusions (single integer values or ranges)
     */
    private List<Object> _siteExclusions;

    /**
     * Histology inclusions (single integer values or ranges)
     */
    private List<Object> _histologyInclusions;

    /**
     * Histology exclusions (single integer values or ranges)
     */
    private List<Object> _histologyExclusions;

    /**
     * Behavior inclusions (single integer values
     */
    private List<Object> _behaviorInclusions;

    /**
     * Recode
     */
    private String _recode;

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public List<Object> getSiteInclusions() {
        return _siteInclusions;
    }

    public List<Object> getSiteExclusions() {
        return _siteExclusions;
    }

    public List<Object> getHistologyInclusions() {
        return _histologyInclusions;
    }

    public List<Object> getHistologyExclusions() {
        return _histologyExclusions;
    }

    public List<Object> getBehaviorInclusions() {
        return _behaviorInclusions;
    }

    public String getRecode() {
        return _recode;
    }

    public void setId(String id) {
        _id = id;
    }

    public void setSiteInclusions(List<Object> siteInclusions) {
        _siteInclusions = siteInclusions;
    }

    public void setSiteExclusions(List<Object> siteExclusions) {
        _siteExclusions = siteExclusions;
    }

    public void setHistologyInclusions(List<Object> histologyInclusions) {
        _histologyInclusions = histologyInclusions;
    }

    public void setHistologyExclusions(List<Object> histologyExclusions) {
        _histologyExclusions = histologyExclusions;
    }

    public void setBehaviorInclusions(List<Object> behaviorInclusions) {
        _behaviorInclusions = behaviorInclusions;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setRecode(String recode) {
        _recode = recode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IcccExecutableSiteGroupDto that = (IcccExecutableSiteGroupDto)o;

        return !(_id != null ? !_id.equals(that._id) : that._id != null);

    }

    @Override
    public int hashCode() {
        return _id != null ? _id.hashCode() : 0;
    }

    public boolean matches(Integer site, Integer histology, Integer behavior) {
        boolean siteOk, histOk = false, behavOk = false;

        // check site
        if (_siteInclusions != null)
            siteOk = isContained(_siteInclusions, site);
        else if (_siteExclusions != null)
            siteOk = !isContained(_siteExclusions, site);
        else
            siteOk = true;

        // check histology (only if site matched)
        if (siteOk) {
            if (_histologyInclusions != null)
                histOk = isContained(_histologyInclusions, histology);
            else if (_histologyExclusions != null)
                histOk = !isContained(_histologyExclusions, histology);
            else
                histOk = true;
        }
        
        //Check the behavior
        if (siteOk && histOk) {
            if (_behaviorInclusions != null)
                behavOk = isContained(_behaviorInclusions, behavior);
            else
                behavOk = true;
        }

        return siteOk && histOk && behavOk;
    }

    @SuppressWarnings("unchecked")
    private boolean isContained(List<Object> list, Integer value) {
        if (list == null)
            return false;
        for (Object obj : list)
            if ((obj instanceof Range && ((Range)obj).contains(value)) || (obj.equals(value)))
                return true;
        return false;
    }
}
