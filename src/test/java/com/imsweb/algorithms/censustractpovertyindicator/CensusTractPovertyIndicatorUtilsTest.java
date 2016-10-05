/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.censustractpovertyindicator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CensusTractPovertyIndicatorUtilsTest {

    @Test
    public void testComputePovertyindicator() {

        Map<String, String> record = new HashMap<>();
        record.put(CensusTractPovertyIndicatorUtils.PROP_STATE_DX, "WY");
        record.put(CensusTractPovertyIndicatorUtils.PROP_COUNTY_DX, "039");
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2000, "997600");

        //test not enough information
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test non-integer diagnosis year
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "xxxx");
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test diagnosis year before 95 
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "1994");
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test diagnosis year after 2011
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2012");
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 1
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "1999");
        Assert.assertEquals("2", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 2
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2005");
        Assert.assertEquals("1", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 3
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2008");
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 3 with a combination of state, county and census 2010 not in the lookup
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "997600");
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 3 with a combination of state, county and census 2010  in the lookup
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "967702");
        Assert.assertEquals("3", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 4 with a combination of state, county and census 2010  in the lookup
        record.put(CensusTractPovertyIndicatorUtils.PROP_STATE_DX, "AL");
        record.put(CensusTractPovertyIndicatorUtils.PROP_COUNTY_DX, "001");
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "020900");
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2009");
        Assert.assertEquals("2", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 5 with a combination of state, county and census 2010  in the lookup
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "020900");
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2010");
        Assert.assertEquals("3", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 6 with a combination of state, county and census 2010  in the lookup
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "020900");
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2011");
        Assert.assertEquals("2", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
        //test year category 7 with a combination of state, county and census 2010  in the lookup
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "020900");
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2012");
        Assert.assertEquals("3", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());

        //test 2015+ years
        record.clear();
        record.put(CensusTractPovertyIndicatorUtils.PROP_STATE_DX, "WY");
        record.put(CensusTractPovertyIndicatorUtils.PROP_COUNTY_DX, "039");
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "967702");
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2015");
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record, false).getCensusTractPovertyIndicator());
        Assert.assertEquals("3", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record, true).getCensusTractPovertyIndicator());

        //test unknown year
        record.clear();
        record.put(CensusTractPovertyIndicatorUtils.PROP_STATE_DX, "WY");
        record.put(CensusTractPovertyIndicatorUtils.PROP_COUNTY_DX, "039");
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010, "967702");
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "999");
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record, false).getCensusTractPovertyIndicator());
        Assert.assertEquals("9", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record, true).getCensusTractPovertyIndicator());

        // test a real case from DMS (this case failed when we generated unique keys from the concatenated state/county/census)
        record.clear();
        record.put(CensusTractPovertyIndicatorUtils.PROP_STATE_DX, "HI");
        record.put(CensusTractPovertyIndicatorUtils.PROP_COUNTY_DX, "003");
        record.put(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2000, "003405");
        record.put(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR, "2007");
        Assert.assertEquals("3", CensusTractPovertyIndicatorUtils.computePovertyIndicator(record).getCensusTractPovertyIndicator());
    }

    //compare sas and seerutils output
    /*********************************************************************************************************
     public static void main(String[] args) throws Exception {
     File sasResults = new File("H:\\povertyIndicator\\poverty-sas-results.txt");
     File input = new File("E:\\Project Docs\\CensusTract\\poverty-test.txt");
     LineNumberReader sasReader = new LineNumberReader(new InputStreamReader(SeerUtils.createInputStream(sasResults)));
     LineNumberReader javaReader = new LineNumberReader(new InputStreamReader(SeerUtils.createInputStream(input)));
     String sasLine = sasReader.readLine();
     String javaLine = javaReader.readLine();
     Layout layout = LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
     long totalCases = 0;
     long diff = 0;
     while (sasLine != null && javaLine != null) {
     Map<String, String> sasRec = layout.createRecordFromLine(sasLine);
     Map<String, String> javaRec = layout.createRecordFromLine(javaLine);
     totalCases++;
     String utilsPovertyIndicator = CensusTractPovertyIndicatorUtils.computePovertyIndicator(javaRec).getCensusTractPovertyIndicator();

     totalCases++;
     if (sasRec.get("censusPovertyIndictr") == null ? utilsPovertyIndicator != null : !sasRec.get("censusPovertyIndictr").equals(utilsPovertyIndicator)) {
     diff++;
     //Put the output inside the if statement if there are differences, I put it here first to see everything is going well
     System.out.println("Patient Id        " + sasRec.get("patientIdNumber") + "...." + javaRec.get("patientIdNumber"));
     System.out.println("DX year           " + sasRec.get(CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR) + "...." + javaRec.get(
     CensusTractPovertyIndicatorUtils.PROP_DIAGNOSIS_YEAR));
     System.out.println("Dx State          " + sasRec.get(CensusTractPovertyIndicatorUtils.PROP_STATE_DX) + "...." + javaRec.get(CensusTractPovertyIndicatorUtils.PROP_STATE_DX));
     System.out.println("Dx County         " + sasRec.get(CensusTractPovertyIndicatorUtils.PROP_COUNTY_DX) + "...." + javaRec.get(CensusTractPovertyIndicatorUtils.PROP_COUNTY_DX));
     System.out.println("Census tract 2000 " + sasRec.get(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2000) + "...." + javaRec.get(
     CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2000));
     System.out.println("Census tract 2010 " + sasRec.get(CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010) + "...." + javaRec.get(
     CensusTractPovertyIndicatorUtils.PROP_CENSUS_TRACT_2010));
     System.out.println(sasRec.get("censusPovertyIndictr") + "--------" + utilsPovertyIndicator);
     System.out.println("..................................................");
     }
     sasLine = sasReader.readLine();
     javaLine = javaReader.readLine();
     }
     System.out.println("Poverty Indicator:  " + totalCases + " cases tested! and " + diff + " cases failed!");
     }
     *******************************************************************************************************************************************************/
}
