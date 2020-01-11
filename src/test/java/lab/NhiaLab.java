/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package lab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.opencsv.CSVReaderBuilder;

import com.imsweb.algorithms.nhia.NhiaInputPatientDto;
import com.imsweb.algorithms.nhia.NhiaInputRecordDto;
import com.imsweb.algorithms.nhia.NhiaUtils;
import com.imsweb.layout.LayoutFactory;
import com.imsweb.layout.record.fixed.naaccr.NaaccrLayout;

// use this class to compare results from SAS and SEER*Utils...
@SuppressWarnings({"UnusedDeclaration", "ConstantConditions"})
public class NhiaLab {

    public static void main(String[] args) throws Exception {
        //createTestingData();
        //runSeerUtilsVersion();
        compareOutputsWithSas();
        //compareOutputsWithDms();
    }

    private static void createTestingData() throws Exception {

        List<Map<String, String>> opt0List = new ArrayList<>(), opt1List = new ArrayList<>(), opt2List = new ArrayList<>();
        for (String[] row : new CSVReaderBuilder(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("tools-test-data/testNHIA.csv"))).withSkipLines(1).build()
                .readAll()) {
            if (row.length < 2)
                continue;

            Map<String, String> rec = new HashMap<>();
            rec.put(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN, row[0]);
            rec.put(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY, row[1]);
            rec.put(NhiaUtils.PROP_RACE1, row[2]);
            rec.put(NhiaUtils.PROP_IHS, row[3]);
            rec.put(NhiaUtils.PROP_STATE_DX, row[4]);
            rec.put(NhiaUtils.PROP_COUNTY_DX_ANALYSIS, row[5]);
            rec.put(NhiaUtils.PROP_SEX, row[6]);
            rec.put(NhiaUtils.PROP_NAME_LAST, row[7]);
            rec.put(NhiaUtils.PROP_NAME_MAIDEN, row[8]);

            NhiaInputRecordDto input = new NhiaInputRecordDto();
            // TODO set rec values into the DTO

            String option = row[9];
            String nhia = row[10];

            // just a little verification...
            if (!nhia.equals(NhiaUtils.computeNhia(input, option).getNhia()))
                System.out.println(Arrays.asList(row));
            //assertEquals(nhia, NhiaUtils.computeNhia(rec, option));
            rec.put("nhia", nhia);

            if ("0".equals(option))
                opt0List.add(rec);
            else if ("1".equals(option))
                opt1List.add(rec);
            else if ("2".equals(option))
                opt2List.add(rec);
            else
                throw new RuntimeException("Unsupported option: " + option);

        }

        NaaccrLayout layout = (NaaccrLayout)LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
        layout.writeRecords(new File("E:\\Project Docs\\Nhappia docs\\nhia-seerutils-results-opt0.txt"), opt0List); // all cases (All Records)
        layout.writeRecords(new File("E:\\Project Docs\\Nhappia docs\\nhia-seerutils-results-opt1.txt"), opt1List); // 7's and 9's (OPTION1)
        layout.writeRecords(new File("E:\\Project Docs\\Nhappia docs\\nhia-seerutils-results-opt2.txt"), opt2List); // 7's only (OPTION2)

        // now re-do the files without the calculated NHIA values so we can run them through SAS...
        for (Map<String, String> rec : opt0List)
            rec.remove("nhia");
        for (Map<String, String> rec : opt1List)
            rec.remove("nhia");
        for (Map<String, String> rec : opt2List)
            rec.remove("nhia");

        layout.writeRecords(new File("H:\\NHAPIIA\\nhia-test-opt0.txt"), opt0List); // all cases (All Records)
        layout.writeRecords(new File("H:\\NHAPIIA\\nhia-test-opt1.txt"), opt1List); // 7's and 9's (OPTION1)
        layout.writeRecords(new File("H:\\NHAPIIA\\nhia-test-opt2.txt"), opt2List); // 7's only (OPTION2)
    }

    private static void runSeerUtilsVersion() throws Exception {
        File input = new File("E:\\Project Docs\\Nhappia docs\\sensitive\\new.mexico.full.case.abstract.items.txt");

        File output0 = new File("E:\\Project Docs\\Nhappia docs\\sensitive\\nhia-seerutils-results-opt0.txt");
        File output1 = new File("E:\\Project Docs\\Nhappia docs\\sensitive\\nhia-seerutils-results-opt1.txt");
        File output2 = new File("E:\\Project Docs\\Nhappia docs\\sensitive\\nhia-seerutils-results-opt2.txt");

        LineNumberReader reader = new LineNumberReader(new InputStreamReader(createInputStream(input, null)));
        BufferedWriter writer0 = new BufferedWriter(new OutputStreamWriter(createOutputStream(output0)));
        BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(createOutputStream(output1)));
        BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(createOutputStream(output2)));

        NaaccrLayout absLayout = (NaaccrLayout)LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);

        Map<String, String> rec = absLayout.readNextRecord(reader);
        while (rec != null) {
            NhiaInputRecordDto inputDto = new NhiaInputRecordDto();
            // TODO translate record into input objects

            rec.put("nhia", NhiaUtils.computeNhia(inputDto, NhiaUtils.NHIA_OPTION_ALL_CASES).getNhia());
            absLayout.writeRecord(writer0, rec);
            rec.put("nhia", NhiaUtils.computeNhia(inputDto, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia());
            absLayout.writeRecord(writer1, rec);
            rec.put("nhia", NhiaUtils.computeNhia(inputDto, NhiaUtils.NHIA_OPTION_SEVEN_ONLY).getNhia());
            absLayout.writeRecord(writer2, rec);
            rec = absLayout.readNextRecord(reader);
        }

        reader.close();
        writer0.close();
        writer1.close();
        writer2.close();

    }

    //compare sas and seerutils output
    private static void compareOutputsWithSas() throws Exception {

        File sasResults = new File("H:\\NHAPIIA\\nhia-sas-results-opt0.txt");
        File javaResults = new File("E:\\Project Docs\\Nhappia docs\\nhia-seerutils-results-opt0.txt");
        LineNumberReader sasReader = new LineNumberReader(new InputStreamReader(createInputStream(sasResults, null)));
        LineNumberReader javaReader = new LineNumberReader(new InputStreamReader(createInputStream(javaResults, null)));
        String sasLine = sasReader.readLine();
        String javaLine = javaReader.readLine();
        NaaccrLayout layout = (NaaccrLayout)LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
        long totalCases = 0;
        long diff = 0;
        while (sasLine != null && javaLine != null) {
            Map<String, String> sasRec = layout.createRecordFromLine(sasLine, 1, null);
            Map<String, String> javaRec = layout.createRecordFromLine(javaLine, 1, null);

            //this code is added because sometimes the test files have bad lines.
            /*
            if (javaRec.get("patientIdNumber") == null) {
                javaLine = javaReader.readLine();
                continue;
            }
            if (Integer.valueOf(javaRec.get("patientIdNumber")) > Integer.valueOf(sasRec.get("patientIdNumber"))) {
                while (!javaRec.get("patientIdNumber").equals(sasRec.get("patientIdNumber"))) {
                    sasLine = sasReader.readLine();
                    sasRec = layout.createRecordFromLine(sasLine);
                }
            }
            */

            totalCases++;
            //Put the output inside the if statement if there are differences, I put it here first to see everything is going well
            System.out.println("Patient Id        " + sasRec.get("patientIdNumber") + "\t" + javaRec.get("patientIdNumber"));
            System.out.println("Span/Hisp Orgn    " + sasRec.get(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN) + "\t" + javaRec.get(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN));
            System.out.println("Birth place       " + sasRec.get(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY) + "\t" + javaRec.get(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY));
            System.out.println("Last name         " + sasRec.get(NhiaUtils.PROP_NAME_LAST) + "\t" + javaRec.get(NhiaUtils.PROP_NAME_LAST));
            System.out.println("Maiden name       " + sasRec.get(NhiaUtils.PROP_NAME_MAIDEN) + "\t" + javaRec.get(NhiaUtils.PROP_NAME_MAIDEN));
            System.out.println("Race              " + sasRec.get(NhiaUtils.PROP_RACE1) + "\t" + javaRec.get(NhiaUtils.PROP_RACE1));
            System.out.println("Sex               " + sasRec.get(NhiaUtils.PROP_SEX) + "\t" + javaRec.get(NhiaUtils.PROP_SEX));
            System.out.println("State             " + sasRec.get(NhiaUtils.PROP_STATE_DX) + "\t" + javaRec.get(NhiaUtils.PROP_STATE_DX));
            System.out.println("County            " + sasRec.get(NhiaUtils.PROP_COUNTY_DX_ANALYSIS) + "\t" + javaRec.get(NhiaUtils.PROP_COUNTY_DX_ANALYSIS));
            System.out.println("IHS               " + sasRec.get(NhiaUtils.PROP_IHS) + "\t" + javaRec.get(NhiaUtils.PROP_IHS));
            System.out.println("Nhia              " + sasRec.get("nhia") + "\t" + javaRec.get("nhia"));
            System.out.println("...........................................");
            if (sasRec.get("nhia") == null ? javaRec.get("nhia") != null : !sasRec.get("nhia").equals(javaRec.get("nhia"))) {
                diff++;
            }
            sasLine = sasReader.readLine();
            javaLine = javaReader.readLine();
        }
        System.out.println("NHIA - option 0 : " + totalCases + " cases tested! and " + diff + " cases failed!");
    }

    //compare DMS and seerutils output
    private static void compareOutputsWithDms() throws Exception {

        File dmsOutput = new File("E:\\Project Docs\\Nhappia docs\\sensitive\\se.items.txt");
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(createInputStream(dmsOutput, null)));
        NaaccrLayout layout = (NaaccrLayout)LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_13_ABSTRACT);
        List<Map<String, String>> patient = new ArrayList<>();
        String currentPatIdNum = null;
        String line = reader.readLine();
        long totalCases = 0;
        while (line != null) {
            totalCases++;
            Map<String, String> rec = layout.createRecordFromLine(line, 1, null);
            String patIdNum = rec.get("patientIdNumber");
            if (patIdNum != null && !patIdNum.equals(currentPatIdNum)) {
                if (!patient.isEmpty())
                    handlePatient(patient, reader.getLineNumber() - 1);
                patient.clear();
                currentPatIdNum = patIdNum;
            }
            if (patIdNum != null)
                patient.add(rec);
            line = reader.readLine();
        }
        if (!patient.isEmpty())
            handlePatient(patient, reader.getLineNumber());
        reader.close();
        System.out.println("NHIA - option 1 : " + totalCases + " cases tested!");
    }

    private static void handlePatient(List<Map<String, String>> patient, long lineNumber) {

        NhiaInputPatientDto inputDto = new NhiaInputPatientDto();
        // TODO translate maps into proper input DTO

        //dont forget to change the option based on the registry
        String utilsNhia = NhiaUtils.computeNhia(inputDto, NhiaUtils.NHIA_OPTION_SEVEN_AND_NINE).getNhia();
        for (Map<String, String> record : patient) {
            if (record.get("nhia") == null ? utilsNhia != null : !record.get("nhia").equals(utilsNhia)) {
                System.out.println("Line Number       " + lineNumber);
                System.out.println("Patient Id        " + record.get("patientIdNumber"));
                System.out.println("Span/Hisp Orgn    " + record.get(NhiaUtils.PROP_SPANISH_HISPANIC_ORIGIN));
                System.out.println("Birth place       " + record.get(NhiaUtils.PROP_BIRTH_PLACE_COUNTRY));
                System.out.println("Last name         " + record.get(NhiaUtils.PROP_NAME_LAST));
                System.out.println("Maiden name       " + record.get(NhiaUtils.PROP_NAME_MAIDEN));
                System.out.println("Race              " + record.get(NhiaUtils.PROP_RACE1));
                System.out.println("Sex               " + record.get(NhiaUtils.PROP_SEX));
                System.out.println("State             " + record.get(NhiaUtils.PROP_STATE_DX));
                System.out.println("County            " + record.get(NhiaUtils.PROP_COUNTY_DX_ANALYSIS));
                System.out.println("IHS               " + record.get(NhiaUtils.PROP_IHS));
                System.out.println("Nhia              " + record.get("nhia") + "\t" + utilsNhia);
                System.out.println("...........................................");
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static InputStream createInputStream(File file, String zipEntryToUse) throws IOException {
        if (file == null || !file.exists())
            throw new IOException("File does not exist.");

        String name = file.getName().toLowerCase();

        InputStream is;
        if (name.endsWith(".gz") || name.endsWith(".gzip"))
            is = new GZIPInputStream(new FileInputStream(file));
        else if (name.endsWith(".zip")) {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            // count the number of entries
            List<String> list = new ArrayList<>();
            while (entries.hasMoreElements())
                list.add(entries.nextElement().getName());
            // can't be empty
            if (list.isEmpty())
                throw new IOException("Zip file is empty.");
            InputStream tmp;
            // if only one, just take that one...
            if (list.size() == 1)
                zipEntryToUse = list.get(0);

            if (list.contains(zipEntryToUse))
                tmp = zipFile.getInputStream(zipFile.getEntry(zipEntryToUse));
            else
                throw new IOException("Zip file contains more than one file.");

            // zip file could contain another compressed file; we are only supporting gzip or uncompressed!
            if ((zipEntryToUse.endsWith(".gz") || zipEntryToUse.endsWith(".gzip")))
                is = new GZIPInputStream(tmp);
            else if (zipEntryToUse.endsWith(".zip"))
                throw new IOException("Zip files inside zip files is not supported.");
            else
                is = tmp;
        }
        else
            is = new FileInputStream(file);

        return is;
    }

    private static OutputStream createOutputStream(File file) throws IOException {
        OutputStream os;

        String name = file.getName().toLowerCase();

        if (name.endsWith(".gz") || name.endsWith(".gzip"))
            os = new GZIPOutputStream(new FileOutputStream(file));
        else if (name.endsWith(".zip"))
            os = new ZipOutputStream(new FileOutputStream(file));
        else
            os = new FileOutputStream(file);

        return os;
    }
}
