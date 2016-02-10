
package com.imsweb.algorithms.surgery.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("row")
public class SurgeryRowXmlDto {

    @XStreamAsAttribute
    @XStreamAlias("level")
    private Integer _level;

    @XStreamAsAttribute
    @XStreamAlias("break")
    private Boolean _break;

    @XStreamAlias("code")
    private String _code;

    @XStreamAlias("description")
    private String _description;

    public Integer getLevel() {
        return _level;
    }

    public void setLevel(Integer level) {
        _level = level;
    }

    public Boolean isBreak() {
        return _break;
    }

    public void setBreak(Boolean aBreak) {
        _break = aBreak;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        _code = code;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }
}
