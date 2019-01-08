/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.surgery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.ByteConverter;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.basic.ShortConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
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
    private static final List<YearSpecificSiteSpecificSurgeryUtils> _INSTANCES = new ArrayList<>();

    static {
        _INSTANCES.add(new YearSpecificSiteSpecificSurgeryUtils(2014));
    }

    /**
     * Returns the unique instance of the site-specific surgery tables utility class.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return the unique instance of the site-specific surgery tables utility class
     */
    public static SiteSpecificSurgeryUtils getInstance() {
        return getInstance(LocalDate.now().getYear());
    }

    /**
     * Returns the unique instance of the site-specific surgery tables utility class.
     * <p/>
     * @param dxYear DX year
     * Created on Jul 18, 2012 by depryf
     * @return the unique instance of the site-specific surgery tables utility class
     */
    public static SiteSpecificSurgeryUtils getInstance(int dxYear) {
        try {
            for (YearSpecificSiteSpecificSurgeryUtils instance : _INSTANCES) {
                if (instance.matches(dxYear)) {
                    if (instance.getData() == null) {
                        URL url = getInternalSiteSpecificSurgeryDataUrl(dxYear);
                        if (url != null) // TODO deal with no URL available
                            instance.setData(new SiteSpecificSurgeryUtils(readSiteSpecificSurgeryData(url.openStream())));
                    }
                }
            }

            return null; // TODO should return default empty instance
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to read internal data", e);
        }
    }

    /**
     * Returns the URL to the internal site-specific surgery tables XML file.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param dxYear DX year
     * @return the URL to the internal site-specific surgery tables XML file
     */
    public static URL getInternalSiteSpecificSurgeryDataUrl(int dxYear) {
        if (dxYear == 2014)
            return Thread.currentThread().getContextClassLoader().getResource("surgery/site-specific-surgery-tables-2014.xml");
        return null;
    }

    /**
     * Reads the site-specific surgery data from the provided URL, expects XML format.
     * <p/>
     * The provided stream will be closed when this method returns
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param stream <code>InputStream</code> to the data file, cannot be null
     * @return a <code>SurgeryTablesXmlDto</code>, never null
     */
    public static SurgeryTablesXmlDto readSiteSpecificSurgeryData(InputStream stream) throws IOException {
        try {
            return (SurgeryTablesXmlDto)createSiteSpecificSurgeryDataXStream().fromXML(stream);
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
     */
    public static void writeSiteSpecificSurgeryData(OutputStream stream, SurgeryTablesXmlDto data) throws IOException {
        try {
            createSiteSpecificSurgeryDataXStream().toXML(data, stream);
        }
        finally {
            stream.close();
        }
    }

    private static XStream createSiteSpecificSurgeryDataXStream() {
        XStream xStream = new XStream(new XppDriver()) {
            // only register the converters we need; other converters generate a private access warning in the console on Java9+...
            @Override
            protected void setupConverters() {
                registerConverter(new NullConverter(), PRIORITY_VERY_HIGH);
                registerConverter(new IntConverter(), PRIORITY_NORMAL);
                registerConverter(new FloatConverter(), PRIORITY_NORMAL);
                registerConverter(new DoubleConverter(), PRIORITY_NORMAL);
                registerConverter(new LongConverter(), PRIORITY_NORMAL);
                registerConverter(new ShortConverter(), PRIORITY_NORMAL);
                registerConverter(new BooleanConverter(), PRIORITY_NORMAL);
                registerConverter(new ByteConverter(), PRIORITY_NORMAL);
                registerConverter(new StringConverter(), PRIORITY_NORMAL);
                registerConverter(new DateConverter(), PRIORITY_NORMAL);
                registerConverter(new CollectionConverter(getMapper()), PRIORITY_NORMAL);
                registerConverter(new ReflectionConverter(getMapper(), getReflectionProvider()), PRIORITY_VERY_LOW);
            }
        };
        xStream.autodetectAnnotations(true);
        xStream.alias("surgery-tables", SurgeryTablesXmlDto.class);

        // setup proper security by limiting what classes can be loaded by XStream (#79515)
        xStream.addPermission(NoTypePermission.NONE);
        xStream.addPermission(new WildcardTypePermission(new String[] {"com.imsweb.algorithms.surgery.xml.**"}));

        return xStream;
    }

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
     * Constructor.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param data a <code>SurgeryTablesXmlDto</code>, cannot be null
     */
    public SiteSpecificSurgeryUtils(SurgeryTablesXmlDto data) {
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
                for (String s : StringUtils.split(histInc, ',')) {
                    s = s.trim();
                    if (s.contains("-")) {
                        String[] parts = StringUtils.split(s, '-');
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
                for (String s : StringUtils.split(table.getSiteInclusion(), ',')) {
                    s = s.trim();
                    if (s.contains("-")) {
                        String[] parts = StringUtils.split(s, '-');
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
                    for (String s : StringUtils.split(histExc, ',')) {
                        s = s.trim();
                        if (s.contains("-")) {
                            String[] parts = StringUtils.split(s, '-');
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

    private static final class YearSpecificSiteSpecificSurgeryUtils {

        private int _startYear;
        private int _endYear;
        private SiteSpecificSurgeryUtils _data;

        public YearSpecificSiteSpecificSurgeryUtils(int start, int end) {
            _startYear = start;
            _endYear = end;
        }

        public YearSpecificSiteSpecificSurgeryUtils(int year) {
            _startYear = year;
            _endYear = year;
        }

        public boolean matches(int year) {
            return year >= _startYear && year <= _endYear;
        }

        public SiteSpecificSurgeryUtils getData() {
            return _data;
        }

        public void setData(SiteSpecificSurgeryUtils data) {
            _data = data;
        }
    }
}
