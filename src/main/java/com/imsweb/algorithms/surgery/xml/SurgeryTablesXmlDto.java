
package com.imsweb.algorithms.surgery.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("surgery-tables")
public class SurgeryTablesXmlDto {

    @XStreamAsAttribute
    @XStreamAlias("version")
    private String _version;

    @XStreamAsAttribute
    @XStreamAlias("version-name")
    private String _versionName;

    @XStreamImplicit
    private List<SurgeryTableXmlDto> _surgeryTable;

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public String getVersionName() {
        return _versionName;
    }

    public void setVersionName(String versionName) {
        _versionName = versionName;
    }

    public List<SurgeryTableXmlDto> getSurgeryTable() {
        return _surgeryTable;
    }

    public void setSurgeryTable(List<SurgeryTableXmlDto> surgeryTable) {
        _surgeryTable = surgeryTable;
    }
}
