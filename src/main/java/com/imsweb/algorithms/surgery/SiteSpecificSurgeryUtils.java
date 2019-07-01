/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.algorithms.surgery;

import com.imsweb.algorithms.surgery.xml.SurgeryTablesXmlDto;

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

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Utility class to return site-specific surgery information that can be used to build application-specific lookups.
 */
public final class SiteSpecificSurgeryUtils {

    /**
     * Unique instance of this class
     */
    private static final SiteSpecificSurgeryUtils _INSTANCE = new SiteSpecificSurgeryUtils();

    /**
     * Returns the unique instance of the site-specific surgery tables utility class.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @return the unique instance of the site-specific surgery tables utility class
     */
    public static SiteSpecificSurgeryUtils getInstance() {
        return _INSTANCE;
    }

    /**
     * Reads the site-specific surgery data from the provided URL, expects XML format.
     * <p/>
     * The provided stream will be closed when this method returns
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param url <code>URL</code> to the data file, cannot be null
     * @return a <code>SurgeryTablesXmlDto</code>, never null
     */
    public static SurgeryTablesXmlDto readTables(URL url) {
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

        return (SurgeryTablesXmlDto)xStream.fromXML(url);
    }

    // cached data
    private Map<Integer, SurgeryTablesDto> _data = new HashMap<>();

    // cached regex
    private Pattern _sitePattern = Pattern.compile("C\\d\\d\\d?"), _histPattern = Pattern.compile("\\d\\d\\d\\d");

    // internal lock to control concurrency (this is needed because we lazily initialize the data)
    private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();

    /**
     * No public constructor, use the instance.
     */
    private SiteSpecificSurgeryUtils() {
    }

    /**
     * Returns the data for the requested DX year.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param dxYear requested DX year, cannot be null
     * @return available data, null if no data correspond to the requested year
     */
    public SurgeryTablesDto getTables(int dxYear) {
        if (dxYear < 2003 || dxYear > LocalDate.now().getYear())
            return null;

        // optimization: some years share the same data, so let's adjust the DX year so its XML data actually exists
        int year = IntStream.of(2003, 2004, 2007, 2010, 2012, 2013, 2014, 2015, 2016, 2018).filter(y -> y <= dxYear).max().orElse(2018);

        _lock.readLock().lock();
        if (!_data.containsKey(year)) {
            _lock.readLock().unlock();
            _lock.writeLock().lock();
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource("surgery/site-specific-surgery-tables-" + year + ".xml");
                _data.put(year, new SurgeryTablesDto(readTables(url)));
                _lock.readLock().lock();
            }
            finally {
                _lock.writeLock().unlock();
            }
        }
        try {
            return _data.get(year);
        }
        finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Returns the site-specific surgery table corresponding to the requested year and title, null if not found.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param dxYear requested DX year, cannot be null
     * @param title requested table title
     * @return site-specific surgery table, maybe null
     */
    public SurgeryTableDto getTable(int dxYear, String title) {
        SurgeryTablesDto tables = getTables(dxYear);
        if (tables == null)
            return null;

        for (SurgeryTableDto table : tables.getTables())
            if (table.getTitle().equals(title))
                return table;

        return null;
    }

    /**
     * Returns the site-specific surgery table corresponding to the requested year and site/histology, null if not found.
     * <p/>
     * Created on Jul 18, 2012 by depryf
     * @param dxYear requested DX year, cannot be null
     * @param site primary site, cannot be null, format must be Cnnn or Cnn (in which case a trailing 9 is assumed)
     * @param histology histology, cannot be null, format must be nnnn
     * @return site-specific surgery table, maybe null
     */
    public SurgeryTableDto getTable(int dxYear, String site, String histology) {
        if (site == null || !_sitePattern.matcher(site).matches())
            return null;
        if (histology == null || !_histPattern.matcher(histology).matches())
            return null;

        SurgeryTablesDto tables = getTables(dxYear);
        if (tables == null)
            return null;

        // for site, assume trailing 9 if not provided
        if (site.length() == 3)
            site = site + "9";

        SurgeryTableDto result = null;
        for (SurgeryTableDto table : tables.getTables()) {

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
}
