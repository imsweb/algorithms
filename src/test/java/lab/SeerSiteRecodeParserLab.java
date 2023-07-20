/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package lab;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class SeerSiteRecodeParserLab {

    public static void main(String[] args) throws IOException, CsvException {
        File input = new File("<original-csv-path>");

        List<String> newLines = new ArrayList<>();
        newLines.add("ID,Group Name,Indentation,Sites IN,Sites OUT,Hist IN,Hist OUT,Beh IN,DX Year Min,DX Year Max,Recode");

        int count = 0;
        try (CSVReader reader = new CSVReader(new InputStreamReader(Files.newInputStream(input.toPath()), StandardCharsets.US_ASCII))) {
            String[] line = reader.readNext();
            while (line != null) {
                if (line.length != 6)
                    throw new IllegalStateException("!!! " + Arrays.toString(line));

                if ("Site Group".equals(line[0])) {
                    line = reader.readNext();
                    continue;
                }

                String name = line[0].trim();
                String site = line[1].trim();
                String hist = line[2].trim();
                String beh = line[3].trim();
                String recode = line[4].trim();

                StringBuilder buf = new StringBuilder();

                buf.append("\"").append(++count).append("\"");
                buf.append(",");

                buf.append("\"").append(name).append("\"");
                buf.append(",");

                buf.append("\"").append(StringUtils.isEmpty(site) ? "0" : "1").append("\"");
                buf.append(",");

                buf.append("\"").append(site.replace(" " , "")).append("\"");
                buf.append(",\"\",");

                if (hist.toLowerCase().startsWith("excluding "))
                    buf.append("\"\"").append(",").append("\"").append(hist.toLowerCase().replace("excluding", "").replace(" " , ""));
                else if ("all".equalsIgnoreCase(hist))
                    buf.append("\"\"").append(",").append("\"");
                else
                    buf.append("\"").append(hist.replace(" " , "")).append("\"").append(",").append("\"");
                buf.append("\"").append(",");

                buf.append("\"").append(beh.replace("-", ",")).append("\"");
                buf.append(",");

                buf.append("\"{MIN}\"");
                buf.append(",");

                buf.append("\"{MAX}\"");
                buf.append(",");

                buf.append("\"").append(recode).append("\"");

                String newLine = buf.toString();
                String newExtraLine = null;
                if (hist.contains("9727 (<2010)")) {
                    newLine = buf.toString().replace("9727(<2010)", "9727").replace("{MAX}", "2009");
                    newExtraLine = buf.toString().replace(String.valueOf(count), String.valueOf(++count)).replace("9727(<2010),", "").replace("{MIN}", "2010");
                }
                else if (hist.contains("9727 (2010+)")) {
                    newLine = buf.toString().replace("9727(2010+)", "9727").replace("{MIN}", "2010");
                    newExtraLine = buf.toString().replace(String.valueOf(count), String.valueOf(++count)).replace("9727(2010+),", "").replace("{MAX}", "2009");
                }

                newLine = newLine.replace("{MAX}", "").replace("{MIN}", "").replace("\"\",", ",");
                if (newExtraLine != null)
                    newExtraLine = newExtraLine.replace("{MAX}", "").replace("{MIN}", "").replace("\"\",", ",");

                newLines.add(newLine);
                if (newExtraLine != null)
                    newLines.add(newExtraLine);

                line = reader.readNext();
            }
        }

        newLines.forEach(System.out::println);
    }

}
