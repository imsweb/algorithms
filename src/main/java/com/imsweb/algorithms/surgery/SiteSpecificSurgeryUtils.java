/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.surgery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;

import com.imsweb.algorithms.surgery.xml.SurgeryRowXmlDto;
import com.imsweb.algorithms.surgery.xml.SurgeryTableXmlDto;
import com.imsweb.algorithms.surgery.xml.SurgeryTablesXmlDto;

/**
 * Utility class to return site-specific surgery information that can be used to build application-specific lookups.
 */
public final class SiteSpecificSurgeryUtils {

    /**
     * Unique instance of this class
     */
    private static SiteSpecificSurgeryUtils _INSTANCE;

    /**
     * Version for the surgery tables
     */
    private String _version;

    /**
     * Version name for the surgery tables
     */
    private String _versionName;

    /**
     * Cached surgery tables
     */
    private List<SurgeryTableDto> _tables;

    /**
     * Returns the unique instance of the site-specific surgery tables utility class.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return the unique instance of the site-specific surgery tables utility class
     */
    public static synchronized SiteSpecificSurgeryUtils getInstance() {
        try {
            if (_INSTANCE == null)
                _INSTANCE = new SiteSpecificSurgeryUtils(readSiteSpecificSurgeryData(getInternalSiteSpecificSurgeryDataUrl().openStream()));

            return _INSTANCE;
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to read internal data", e);
        }
    }

    /**
     * Registers an instance of the data.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param data the data to initialize from
     */
    public static void registerInstance(SurgeryTablesXmlDto data) {
        _INSTANCE = new SiteSpecificSurgeryUtils(data);
    }

    /**
     * Returns the URL to the internal site-specific surgery tables XML file.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return the URL to the internal site-specific surgery tables XML file
     */
    public static URL getInternalSiteSpecificSurgeryDataUrl() {
        return Thread.currentThread().getContextClassLoader().getResource("surgery/site-specific-surgery-tables.xml");
    }

    /**
     * Reads the site-specific surgery data from the provided URL, expects XML format.
     * <p/>
     * The provided stream will be closed when this method returns
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param stream <code>InputStream</code> to the data file, cannot be null
     * @return a <code>SurgeryTablesXmlDto</code>, never null
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static SurgeryTablesXmlDto readSiteSpecificSurgeryData(InputStream stream) throws IOException {
        try {
            return (SurgeryTablesXmlDto)createSiteSpecificSurgeryDataXstream().fromXML(stream);
        }
        finally {
            stream.close();
        }
    }

    /**
     * Writes the site-specific surgery data to the provided URL, using XML format.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param stream <code>OutputStream</code> to the data file, cannot be null
     * @param data the <code>SurgeryTablesXmlDto</code> to write, cannot be null
     * @throws IOException
     */
    public static void writeSiteSpecificSurgeryData(OutputStream stream, SurgeryTablesXmlDto data) throws IOException {
        try {
            createSiteSpecificSurgeryDataXstream().toXML(data, stream);
        }
        finally {
            stream.close();
        }
    }

    private static XStream createSiteSpecificSurgeryDataXstream() {
        XStream xStream = new XStream(new XppDriver());
        xStream.autodetectAnnotations(true);
        xStream.alias("surgery-tables", SurgeryTablesXmlDto.class);

        // setup proper security by limiting what classes can be loaded by XStream (#79515)
        xStream.addPermission(NoTypePermission.NONE);
        xStream.addPermission(new WildcardTypePermission(new String[] {"com.imsweb.algorithms.surgery.xml.**"}));

        return xStream;
    }

    /**
     * Constructor.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param data a <code>SurgeryTablesXmlDto</code>, cannot be null
     */
    SiteSpecificSurgeryUtils(SurgeryTablesXmlDto data) {
        _version = data.getVersion();
        _versionName = data.getVersionName();
        _tables = new ArrayList<>();

        for (SurgeryTableXmlDto table : data.getSurgeryTable()) {
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

    /**
     * Returns the data version.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return the data version
     */
    public String getVersion() {
        return _version;
    }

    /**
     * Returns the data version name.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return the data version name
     */
    public String getVersionName() {
        return _versionName;
    }

    /**
     * Returns all the available site-specific surgery tables.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return all the available site-specific surgery table
     */
    public Collection<SurgeryTableDto> getAllTables() {
        return Collections.unmodifiableList(_tables);
    }

    /**
     * Returns all the available site-specific surgery table titles in alphabetical order.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return all the available site-specific surgery table titles in alphabetical order
     */
    public List<String> getAllTableTitles() {
        List<String> result = new ArrayList<>();

        for (SurgeryTableDto table : _tables)
            result.add(table.getTitle());

        // just for convenience...
        Collections.sort(result);

        return result;
    }

    /**
     * Returns the site-specific surgery table corresponding to the requested title, null if not found.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param title requested table title
     * @return the site-specific surgery table corresponding to the requested title, null if not found
     */
    public SurgeryTableDto getTable(String title) {
        for (SurgeryTableDto table : _tables)
            if (table.getTitle().equals(title))
                return table;
        return null;
    }

    /**
     * Returns the site-specific surgery table corresponding to the requested site/histology, null if the table is not found.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param site primary site, cannot be null, format must be Cxxx
     * @param histology histology, cannot be null, format must be xxxx
     * @return the site-specific surgery table corresponding to the requested site/histology, null if the table is not found
     */
    public SurgeryTableDto getTable(String site, String histology) {
        if (site == null || !site.matches("C\\d\\d\\d") || histology == null || !histology.matches("\\d\\d\\d\\d"))
            return null;

        SurgeryTableDto result = null;

        for (SurgeryTableDto table : _tables) {

            // if the table has an histology inclusion, check it first
            boolean includeHist = false;
            String histInc = table.getHistInclusion();
            if (histInc != null) {
                for (String s : histInc.split(",")) {
                    s = s.trim();
                    if (s.contains("-")) {
                        String[] parts = s.split("\\-");
                        if (parts[0].compareTo(histology) <= 0 && parts[1].compareTo(histology) >= 0) {
                            includeHist = true;
                            break;
                        }
                    }
                    else {
                        if (s.equals(histology)) {
                            includeHist = true;
                            break;
                        }
                    }
                }
            }
            if (includeHist) {
                result = table;
                break;
            }

            // check the site inclusion
            boolean includeSite = false;
            if (table.getSiteInclusion() != null) {
                for (String s : table.getSiteInclusion().split(",")) {
                    s = s.trim();
                    if (s.contains("-")) {
                        String[] parts = s.split("\\-");
                        if (parts[0].compareTo(site) <= 0 && parts[1].compareTo(site) >= 0) {
                            includeSite = true;
                            break;
                        }
                    }
                    else {
                        if (s.equals(site)) {
                            includeSite = true;
                            break;
                        }
                    }
                }
            }

            if (includeSite) {
                // check the histology exclusion
                boolean excludeHist = false;
                String histExc = table.getHistExclusion();
                if (histExc != null) {
                    for (String s : histExc.split(",")) {
                        s = s.trim();
                        if (s.contains("-")) {
                            String[] parts = s.split("\\-");
                            if (parts[0].compareTo(histology) <= 0 && parts[1].compareTo(histology) >= 0) {
                                excludeHist = true;
                                break;
                            }
                        }
                        else {
                            if (s.equals(histology)) {
                                excludeHist = true;
                                break;
                            }
                        }
                    }
                }
                if (!excludeHist) {
                    result = table;
                    break;
                }
            }
        }

        return result;
    }
}
