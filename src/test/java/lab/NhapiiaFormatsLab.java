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

import com.opencsv.CSVWriter;

// use this class to translate an xpt SAS format file into an lst file, which is also SAS but is a human-readable file (ASCII)...
public class NhapiiaFormatsLab {

    @SuppressWarnings({"UnusedDeclaration", "MismatchedQueryAndUpdateOfCollection"})
    public static void main(String[] args) {

        // input: readable file containing all the SAS formats
        File readableFormats = new File(System.getProperty("user.dir") + "\\config\\nhapiia\\20130912\\NHAPIIA_Formats_2013_09_12.lst");

        // output: the CSV files that are used to popuplate the in-memory lookups
        //For Napiia lookups
        File napiiaCensusAsianFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\napiia-census-asian.csv");
        File napiiaCensusPiFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\napiia-census-pi.csv");
        File napiiaLaudSurnameFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\napiia-laud-surname.csv");
        File napiiaLaudGivenMaleFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\napiia-laud-given-male.csv");
        File napiiaLaudGivenFemaleFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\napiia-laud-given-female.csv");
        File napiiaNaaccrSurnameFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\napiia-naaccr-surname.csv");
        File napiiaNaaccrGivenFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\napiia-naaccr-given.csv");
        //For Nhia Lookups
        File nhiaRarelyHispNamesFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\nhia-rarely-hisp-names.csv");
        File nhiaHeavilyHispanicNamesFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\nhia-heavily-hisp-names.csv");
        File nhiaHighHispEthnCountiesFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\nhia-high-hisp-ethn-counties.csv");
        File nhiaLowEthnCountiesFile = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\tools\\nhia-low-hisp-ethn-counties.csv");
        //Counters to check the numbers against SAS file
        int censusAsianCounter = 0;
        int censusPiCounter = 0;
        int laudSurnameCounter = 0;
        int laudGivenMaleCounter = 0;
        int laudGivenFemaleCounter = 0;
        int naaccrSurnameCounter = 0;
        int naaccrGivenCounter = 0;
        int rarelyHispNamesCounter = 0;
        int heavilyHispnamesCounter = 0;
        int highHispEthnCountiesCounter = 0;
        int lowHispEthnCountiesCounter = 0;

        // BufferedReader (reads line per line) or LineNumberReader (same thing with available line number)
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(readableFormats));
            List<String[]> censusAsianData = new ArrayList<>();
            List<String[]> censusPiData = new ArrayList<>();
            List<String[]> laudSurnameData = new ArrayList<>();
            List<String[]> laudGivenMaleData = new ArrayList<>();
            List<String[]> laudGivenFemaleData = new ArrayList<>();
            List<String[]> naaccrSurnameData = new ArrayList<>();
            List<String[]> naaccrGivenData = new ArrayList<>();
            List<String[]> rarelyHispNamesData = new ArrayList<>();
            List<String[]> heavilyHispNamesData = new ArrayList<>();
            List<String[]> highHispEthnCountiesData = new ArrayList<>();
            List<String[]> lowHispEthnCountiesData = new ArrayList<>();
            String text = reader.readLine();
            while (text != null) {
                //For Napiia Census Asian Names
                if (text.contains("FORMAT NAME: $CENS_AS")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        censusAsianData.add(new String[] {parts[1].trim(), parts[3].trim()});
                        censusAsianCounter++;
                        text = reader.readLine();
                    }
                }
                //For Napiia Census PI Names
                if (text.contains("FORMAT NAME: $CENS_PI")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        censusPiData.add(new String[] {parts[1].trim(), parts[3].trim()});
                        censusPiCounter++;
                        text = reader.readLine();
                    }
                }
                //For Napiia Laud. surnames
                if (text.contains("FORMAT NAME: $LAUD_S")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        laudSurnameCounter++;
                        laudSurnameData.add(new String[] {parts[1].trim(), parts[3].trim()});
                        text = reader.readLine();
                    }
                }
                //For Napiia Laud. Male given names (this is represented in 2 tables because of names "LOW" and "HIGH" could not ba handled by sas xpt code)
                if (text.contains("FORMAT NAME: $LPRE_GM") || text.contains("FORMAT NAME: $LAUD_GM")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        laudGivenMaleData.add(new String[] {parts[1].trim(), parts[3].trim()});
                        laudGivenMaleCounter++;
                        text = reader.readLine();
                    }
                }
                //For Napiia Laud. Female given names (this is represented in 2 tables because of names "LOW" and "HIGH" could not ba handled by sas xpt code)
                if (text.contains("FORMAT NAME: $LPRE_GF") || text.contains("FORMAT NAME: $LAUD_GF")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        laudGivenFemaleData.add(new String[] {parts[1].trim(), parts[3].trim()});
                        laudGivenFemaleCounter++;
                        text = reader.readLine();
                    }
                }
                //For Napiia Naaccr surnames (this is represented in 2 tables because of names "LOW" and "HIGH" could not ba handled by sas xpt code)
                if (text.contains("FORMAT NAME: $NPRE_HS") || text.contains("FORMAT NAME: $NCR_HS")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        naaccrSurnameData.add(new String[] {parts[1].trim(), parts[3].trim()});
                        naaccrSurnameCounter++;
                        text = reader.readLine();
                    }
                }
                //For Napiia Naaccr given names
                if (text.contains("FORMAT NAME: $NCR_HG")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        naaccrGivenData.add(new String[] {parts[1].trim(), parts[3].trim()});
                        naaccrGivenCounter++;
                        text = reader.readLine();
                    }
                }
                //For Nhia rarely hispanic names (this is represented in 2 tables because of names "LOW" and "HIGH" could not ba handled by sas xpt code)
                if (text.contains("FORMAT NAME: $RSPANX") || text.contains("FORMAT NAME: $RSPAN ")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        if (parts[3].trim().startsWith("onlist")) {
                            rarelyHispNamesData.add(new String[] {parts[1].trim()});
                            rarelyHispNamesCounter++;
                        }
                        text = reader.readLine();
                    }
                }
                //For Nhia heavily hispanic names
                if (text.contains("FORMAT NAME: $HSPAN")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        if (parts[3].trim().startsWith("onlist")) {
                            heavilyHispNamesData.add(new String[] {parts[1].trim()});
                            heavilyHispnamesCounter++;
                        }
                        text = reader.readLine();
                    }
                }
                //For Nhia hispanic ethnic counties
                if (text.contains("FORMAT NAME: $PCT10_HSP")) {
                    for (int i = 1; i <= 4; i++)
                        reader.readLine();
                    text = reader.readLine();
                    while (text != null && text.startsWith("|") && !text.contains("The SAS System") && !text.contains("|**OTHER**")) {
                        String[] parts = text.split("\\|");
                        if (!parts[1].trim().equals(parts[2].trim()))
                            System.out.println("There is an error");
                        if (parts[3].trim().startsWith(">= 5%")) {
                            highHispEthnCountiesData.add(new String[] {parts[1].trim()});
                            highHispEthnCountiesCounter++;
                        }
                        else if (parts[3].trim().startsWith("< 5%")) {
                            lowHispEthnCountiesData.add(new String[] {parts[1].trim()});
                            lowHispEthnCountiesCounter++;
                        }
                        else
                            System.out.println("ERROR for hisp ethnicity: " + parts[3].trim());
                        text = reader.readLine();
                    }
                }

                text = reader.readLine();
            }

            CSVWriter censusAsian = new CSVWriter(new FileWriter(napiiaCensusAsianFile));
            censusAsian.writeAll(censusAsianData);
            censusAsian.close();
            CSVWriter censusPi = new CSVWriter(new FileWriter(napiiaCensusPiFile));
            censusPi.writeAll(censusPiData);
            censusPi.close();
            CSVWriter laudSurname = new CSVWriter(new FileWriter(napiiaLaudSurnameFile));
            laudSurname.writeAll(laudSurnameData);
            laudSurname.close();
            CSVWriter laudGivenMale = new CSVWriter(new FileWriter(napiiaLaudGivenMaleFile));
            laudGivenMale.writeAll(laudGivenMaleData);
            laudGivenMale.close();
            CSVWriter laudGivenFemale = new CSVWriter(new FileWriter(napiiaLaudGivenFemaleFile));
            laudGivenFemale.writeAll(laudGivenFemaleData);
            laudGivenFemale.close();
            CSVWriter ncrSurname = new CSVWriter(new FileWriter(napiiaNaaccrSurnameFile));
            ncrSurname.writeAll(naaccrSurnameData);
            ncrSurname.close();
            CSVWriter naaccrGiven = new CSVWriter(new FileWriter(napiiaNaaccrGivenFile));
            naaccrGiven.writeAll(naaccrGivenData);
            naaccrGiven.close();
            CSVWriter rarelyHispNames = new CSVWriter(new FileWriter(nhiaRarelyHispNamesFile));
            rarelyHispNames.writeAll(rarelyHispNamesData);
            rarelyHispNames.close();
            CSVWriter heavilyHispNames = new CSVWriter(new FileWriter(nhiaHeavilyHispanicNamesFile));
            heavilyHispNames.writeAll(heavilyHispNamesData);
            heavilyHispNames.close();
            //CSVWriter highHispEthnCounties = new CSVWriter(new FileWriter(nhiaHighHispEthnCountiesFile));
            //highHispEthnCounties.writeAll(highHispEthnCountiesData);
            //highHispEthnCounties.close();
            CSVWriter lowHispEthnCounties = new CSVWriter(new FileWriter(nhiaLowEthnCountiesFile));
            lowHispEthnCounties.writeAll(lowHispEthnCountiesData);
            lowHispEthnCounties.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        // display the counters to check against SAS
        System.out.println(censusAsianCounter);
        System.out.println(censusPiCounter);
        System.out.println(laudSurnameCounter);
        System.out.println(laudGivenMaleCounter);
        System.out.println(laudGivenFemaleCounter);
        System.out.println(naaccrSurnameCounter);
        System.out.println(naaccrGivenCounter);
        System.out.println(rarelyHispNamesCounter);
        System.out.println(heavilyHispnamesCounter);
        //System.out.println(highHispEthnCountiesCounter);
        System.out.println(lowHispEthnCountiesCounter);
    }

}
