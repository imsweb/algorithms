package com.imsweb.algorithms.seersiterecode;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Range;

/**
 * Internal site group DTO used to calculate the recode, this class should not be used outside of SEER*Utils...
 * User: depryf
 * Date: 8/22/12
 */
@SuppressWarnings("unused")
public class SeerExecutableSiteGroupDto {

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
        SeerExecutableSiteGroupDto that = (SeerExecutableSiteGroupDto)o;
        return Objects.equals(_id, that._id);

    }

    @Override
    public int hashCode() {
        return _id != null ? _id.hashCode() : 0;
    }

    public boolean matches(Integer site, Integer histology) {
        boolean siteOk;
        boolean histOk = false;

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

        return siteOk && histOk;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean isContained(List<Object> list, Integer value) {
        if (list == null)
            return false;
        for (Object obj : list)
            if ((obj instanceof Range && ((Range)obj).contains(value)) || (obj.equals(value)))
                return true;
        return false;
    }
}
