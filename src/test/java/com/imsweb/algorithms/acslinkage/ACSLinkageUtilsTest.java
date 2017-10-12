/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.acslinkage;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ACSLinkageUtilsTest {

    @Test
    public void testComputeACSData() {
        ACSLinkageDataProvider.Range range1 = ACSLinkageDataProvider.Range.ACS_2007_2011;
        ACSLinkageDataProvider.Range range2 = ACSLinkageDataProvider.Range.ACS_2011_2015;

        Map<String, String> record = new HashMap<>();
        record.put(ACSLinkageUtils.PROP_STATE_DX, "AL");
        record.put(ACSLinkageUtils.PROP_COUNTY_DX, "001");
        record.put(ACSLinkageUtils.PROP_CENSUS_TRACT_2010, "020100");
        record.put(ACSLinkageUtils.PROP_DIAGNOSIS_YEAR, "2010");

        //test the ranges
        Assert.assertEquals("     180    1768     155 10.181 17.760 13.781  0.000  5.034 17.760 13.781  1.075  1.694    1284     125 83.200  5.900 16.800  5.900 19.900  6.000     666      73  0.000  4.8001731.00142.000  1.098  5.425    1702     147 10.200  4.000 14.689  6.2433    1398     141  2.900  2.700    1768     155 90.102 11.105  3.620  3.038  2.262  2.934  4.016  4.511", ACSLinkageUtils.computeACSData(record, range1).getACSData());
        Assert.assertEquals("     251    1948     203 12.885 11.704 15.281  0.873  1.074 12.577 15.655  2.310  1.781    1243     135 85.197  8.869 14.803  8.869 27.514  6.361     697      54  2.439  3.3961872.00193.000  0.000  0.588    1948     203  8.111  5.271 17.454  7.8542     997     141  5.416  3.013    1938     199 84.469  4.570 10.320  7.094  3.560  2.710  1.651  1.746 36.927  7.950 48.270  7.583 60.127  9.123   61838   11900     719     126  149100   29354", ACSLinkageUtils.computeACSData(record, range2).getACSData());

        //test the ranges with a combination of state, county and census 2010 not in the lookup
        record.put(ACSLinkageUtils.PROP_CENSUS_TRACT_2010, "999999");
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range1), ACSLinkageUtils.computeACSData(record, range1).getACSData());
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range2), ACSLinkageUtils.computeACSData(record, range2).getACSData());
        record.put(ACSLinkageUtils.PROP_CENSUS_TRACT_2010, "020100");

        //test non-integer diagnosis year
        record.put(ACSLinkageUtils.PROP_DIAGNOSIS_YEAR, "xxxx");
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range1), ACSLinkageUtils.computeACSData(record, range1).getACSData());
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range2), ACSLinkageUtils.computeACSData(record, range2).getACSData());

        //test diagnosis year before 2005
        record.put(ACSLinkageUtils.PROP_DIAGNOSIS_YEAR, "2004");
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range1), ACSLinkageUtils.computeACSData(record, range1).getACSData());
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range2), ACSLinkageUtils.computeACSData(record, range2).getACSData());

        //test diagnosis years after 2015 excluding recent years
        record.put(ACSLinkageUtils.PROP_DIAGNOSIS_YEAR, "2016");
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range1), ACSLinkageUtils.computeACSData(record, range1, false).getACSData());
        Assert.assertEquals(ACSLinkageDataProvider.getUnknownValueForRange(range2), ACSLinkageUtils.computeACSData(record, range2, false).getACSData());

        //test diagnosis years after 2015 including recent years
        record.put(ACSLinkageUtils.PROP_DIAGNOSIS_YEAR, "2016");
        Assert.assertEquals("     180    1768     155 10.181 17.760 13.781  0.000  5.034 17.760 13.781  1.075  1.694    1284     125 83.200  5.900 16.800  5.900 19.900  6.000     666      73  0.000  4.8001731.00142.000  1.098  5.425    1702     147 10.200  4.000 14.689  6.2433    1398     141  2.900  2.700    1768     155 90.102 11.105  3.620  3.038  2.262  2.934  4.016  4.511", ACSLinkageUtils.computeACSData(record, range1, true).getACSData());
        Assert.assertEquals("     251    1948     203 12.885 11.704 15.281  0.873  1.074 12.577 15.655  2.310  1.781    1243     135 85.197  8.869 14.803  8.869 27.514  6.361     697      54  2.439  3.3961872.00193.000  0.000  0.588    1948     203  8.111  5.271 17.454  7.8542     997     141  5.416  3.013    1938     199 84.469  4.570 10.320  7.094  3.560  2.710  1.651  1.746 36.927  7.950 48.270  7.583 60.127  9.123   61838   11900     719     126  149100   29354", ACSLinkageUtils.computeACSData(record, range2, true).getACSData());
    }
}
