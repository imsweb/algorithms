/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Use this class to convert poverty_format_lib.fmt file to csv
 */
public class CensusTractPovertyIndicatorFormatsLab {

    public static void main(String[] args) {

        // input: readable file containing all the SAS formats...
        File fmtFile = new File(System.getProperty("user.dir") + "\\config\\censustractpoverty\\2013Oct\\poverty_format_lib.fmt");

        // output: the CSV files that are used to popuplate the in-memory lookups        
        File pov9504 = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\censustractpovertyindicator\\poverty-indicator-1995-2004.csv");
        File pov0507 = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\censustractpovertyindicator\\poverty-indicator-2005-2007.csv");
        File pov08 = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\censustractpovertyindicator\\poverty-indicator-2008.csv");
        File pov0911 = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\censustractpovertyindicator\\poverty-indicator-2009-2011.csv");

        // BufferedReader (reads line per line) or LineNumberReader (same thing with available line number)
        BufferedReader reader;
        List<String[]> pov9504Data = new ArrayList<>();
        List<String[]> pov0507Data = new ArrayList<>();
        List<String[]> pov08Data = new ArrayList<>();
        List<String[]> pov0911Data = new ArrayList<>();
        pov9504Data.add(new String[] {"State", "County", "Census2000", "PovertyIndicator"});
        pov0507Data.add(new String[] {"State", "County", "Census2000", "PovertyIndicator"});
        pov08Data.add(new String[] {"State", "County", "Census2010", "PovertyIndicator"});
        pov0911Data.add(new String[] {"State", "County", "Census2010", "PovertyIndicator"});
        try {
            reader = new BufferedReader(new FileReader(fmtFile));
            boolean year1 = false, year2 = false, year3 = false, year4 = false;
            String text = reader.readLine();
            while (text != null) {
                //For 95-04
                if (text.contains("invalue pov9504f")) {
                    year1 = true;
                    year2 = false;
                    year3 = false;
                    year4 = false;
                }
                //For 05-07
                else if (text.contains("invalue pov0509f")) {
                    year1 = false;
                    year2 = true;
                    year3 = false;
                    year4 = false;
                }
                //For 08
                else if (text.contains("invalue pov0610f")) {
                    year1 = false;
                    year2 = false;
                    year3 = true;
                    year4 = false;
                }
                //For 09-11
                else if (text.contains("invalue pov0711f")) {
                    year1 = false;
                    year2 = false;
                    year3 = false;
                    year4 = true;
                }

                if (text.startsWith("'")) {
                    String[] parts = text.split("=");
                    String input = parts[0].trim().replaceAll("'", "");
                    if (parts.length != 2 || input.length() != 11 || parts[1].trim().length() != 1)
                        throw new RuntimeException("Not reading the correct line. Something went wrong!");
                    if (year1)
                        pov9504Data.add(new String[] {input.substring(0, 2), input.substring(2, 5), input.substring(5, 11), parts[1].trim()});
                    else if (year2)
                        pov0507Data.add(new String[] {input.substring(0, 2), input.substring(2, 5), input.substring(5, 11), parts[1].trim()});
                    else if (year3)
                        pov08Data.add(new String[] {input.substring(0, 2), input.substring(2, 5), input.substring(5, 11), parts[1].trim()});
                    else if (year4)
                        pov0911Data.add(new String[] {input.substring(0, 2), input.substring(2, 5), input.substring(5, 11), parts[1].trim()});

                }
                text = reader.readLine();
            }

            reader.close();
            CSVWriter writer9504 = new CSVWriter(new FileWriter(pov9504));
            writer9504.writeAll(pov9504Data);
            writer9504.close();
            CSVWriter writer0507 = new CSVWriter(new FileWriter(pov0507));
            writer0507.writeAll(pov0507Data);
            writer0507.close();
            CSVWriter writer08 = new CSVWriter(new FileWriter(pov08));
            writer08.writeAll(pov08Data);
            writer08.close();
            CSVWriter writer0911 = new CSVWriter(new FileWriter(pov0911));
            writer0911.writeAll(pov0911Data);
            writer0911.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        // display the counters to check against SAS
        System.out.println(pov9504Data.size());
        System.out.println(pov0507Data.size());
        System.out.println(pov08Data.size());
        System.out.println(pov0911Data.size());
    }
}
