/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package lab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

/**
 * Lab class was used to parse data from these locations:
 * <a href="https://seer.cancer.gov/siterecode/icdo3_2023/">...</a>
 * <a href="https://seer.cancer.gov/siterecode/icdo3_2023_expanded/">...</a>
 * Downloaded Excel file, saved as CSV, replaced non-ASCII dashes by ASCII ones and ran this class...
 */
public class SeerSiteRecodeParserLab {

    public static void main(String[] args) throws IOException {
        File input = new File("<original-csv-path>");

        List<List<String>> newLines = new ArrayList<>();
        newLines.add(Arrays.asList("ID", "Name", "Indentation", "Sites IN", "Hist IN", "Hist OUT", "Beh IN", "DX Year Min", "DX Year Max", "Recode"));

        AtomicInteger count = new AtomicInteger();
        try (CsvReader<NamedCsvRecord> reader = CsvReader.builder().ofNamedCsvRecord(input.toPath())) {
            reader.stream().forEach(line -> {
                if (line.getFieldCount() != 6)
                    throw new IllegalStateException("!!! " + Arrays.toString(line.getFields().toArray(new String[0])));

                if ("Site Group".equals(line.getField(0))) {
                    return;
                }

                String name = line.getField(0).trim();
                String site = line.getField(1).trim();
                String hist = line.getField(2).trim();
                String beh = line.getField(3).trim();
                String recode = line.getField(4).trim();

                List<String> newLine = new ArrayList<>();

                newLine.add(String.valueOf(count.incrementAndGet()));
                newLine.add(name);
                newLine.add(StringUtils.isEmpty(site) ? "0" : "1");
                newLine.add(site.replace(" ", ""));
                if (hist.toLowerCase().startsWith("excluding ")) {
                    newLine.add(null);
                    newLine.add(hist.toLowerCase().replace("excluding", "").replace(" ", ""));
                }
                else if ("all".equalsIgnoreCase(hist)) {
                    newLine.add(null);
                    newLine.add(null);
                }
                else {
                    newLine.add(hist.replace(" ", ""));
                    newLine.add(null);
                }
                newLine.add(beh.replace("-", ","));
                newLine.add(null);
                newLine.add(null);
                newLine.add(recode);

                int siteIdx = 3;
                int histInIdx = 4;
                int histOutIdx = 5;
                int behInIdx = 6;
                int minYearIdx = 7;
                int maxYearIdx = 8;

                // do we need to copy previous line (for "merged" cells)?
                if (StringUtils.isNotBlank(newLine.get(siteIdx))) {
                    if (StringUtils.isBlank(newLine.get(histInIdx)) && StringUtils.isBlank(newLine.get(histOutIdx))) {
                        List<String> previousLine = newLines.get(newLines.size() - 1);
                        if (StringUtils.isNotBlank(previousLine.get(histInIdx)))
                            newLine.set(histInIdx, previousLine.get(histInIdx));
                        else
                            newLine.set(histOutIdx, previousLine.get(histOutIdx));
                    }
                    if (StringUtils.isBlank(newLine.get(histInIdx)) && StringUtils.isBlank(newLine.get(histOutIdx)))
                        throw new IllegalStateException(String.join(",", newLine));

                    if (StringUtils.isBlank(newLine.get(behInIdx)))
                        newLine.set(behInIdx, newLines.get(newLines.size() - 1).get(6));
                    if (StringUtils.isBlank(newLine.get(behInIdx)))
                        throw new IllegalStateException(String.join(",", newLine));
                }

                // fix the only place that uses the DX year
                List<String> newExtraLine = null;
                if (StringUtils.isNotBlank(newLine.get(histInIdx)) && newLine.get(histInIdx).contains("9727(<2010)")) {
                    newExtraLine = new ArrayList<>(newLine);
                    newLine.set(histInIdx, newLine.get(histInIdx).replace("9727(<2010)", "9727"));
                    newLine.set(maxYearIdx, "2009");
                    newExtraLine.set(histInIdx, newExtraLine.get(histInIdx).replace("9727(<2010)", ""));
                    newExtraLine.set(minYearIdx, "2010");
                }
                else if (StringUtils.isNotBlank(newLine.get(histInIdx)) && newLine.get(histInIdx).contains("9727(2010+)")) {
                    newExtraLine = new ArrayList<>(newLine);
                    newLine.set(histInIdx, newLine.get(histInIdx).replace("9727(2010+)", "9727"));
                    newLine.set(minYearIdx, "2010");
                    newExtraLine.set(histInIdx, newExtraLine.get(histInIdx).replace("9727(2010+)", ""));
                    newExtraLine.set(maxYearIdx, "2009");
                }

                newLines.add(newLine);
                if (newExtraLine != null)
                    newLines.add(newExtraLine);

            });
        }

        List<String> lines = new ArrayList<>();
        for (List<String> list : newLines) {
            List<String> cleanValues = new ArrayList<>();
            for (String value : list)
                cleanValues.add(StringUtils.isBlank(value) ? "" : ("\"" + value + "\""));
            lines.add(String.join(",", cleanValues));
        }

        lines.forEach(System.out::println);
    }

}
