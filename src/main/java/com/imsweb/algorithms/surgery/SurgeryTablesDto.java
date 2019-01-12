
package com.imsweb.algorithms.surgery;

import com.imsweb.algorithms.surgery.xml.SurgeryRowXmlDto;
import com.imsweb.algorithms.surgery.xml.SurgeryTableXmlDto;
import com.imsweb.algorithms.surgery.xml.SurgeryTablesXmlDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for a set of site-specific surgery tables (those are usually tied to a given year, but this
 * specific class doesn't know about that).
 */
public class SurgeryTablesDto {

    // the version for this set of tables
    private String _version;

    // the version name for this set of tables
    private String _versionName;

    // the list of surgery tables
    private List<SurgeryTableDto> _tables;

    /**
     * Default constructor.
     */
    public SurgeryTablesDto() {
    }

    /**
     * Constructor.
     * <br/><br/>
     * It's a bit silly to maintain both the XML and regular classes since they are identical, but
     * since that's how all the other modules are setup, I am going to keep that here as well...
     * @param surgeryTablesXmlDto XML data
     */
    public SurgeryTablesDto(SurgeryTablesXmlDto surgeryTablesXmlDto) {
        _version = surgeryTablesXmlDto.getVersion();
        _versionName = surgeryTablesXmlDto.getVersionName();
        _tables = new ArrayList<>();
        for (SurgeryTableXmlDto table : surgeryTablesXmlDto.getSurgeryTable()) {
            SurgeryTableDto dto = new SurgeryTableDto();
            dto.setTitle(table.getTitle());
            dto.setSiteInclusion(table.getSiteInclusion());
            dto.setHistInclusion(table.getHistInclusion());
            dto.setHistExclusion(table.getHistExclusion());
            dto.setPreNote(table.getPreNote());
            dto.setPostNote(table.getPostNote());
            List<SurgeryRowDto> rows = new ArrayList<>();
            for (SurgeryRowXmlDto row : table.getRow()) {
                SurgeryRowDto rowDto = new SurgeryRowDto();
                rowDto.setCode(row.getCode());
                rowDto.setDescription(row.getDescription());
                rowDto.setLevel(row.getLevel() == null ? Integer.valueOf(0) : row.getLevel());
                rowDto.setLineBreak(row.isBreak() == null ? Boolean.FALSE : row.isBreak());
                rows.add(rowDto);
            }
            dto.setRow(rows);
            _tables.add(dto);
        }
    }

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

    public List<SurgeryTableDto> getTables() {
        return _tables;
    }

    public void setTables(List<SurgeryTableDto> tables) {
        _tables = tables;
    }
}
