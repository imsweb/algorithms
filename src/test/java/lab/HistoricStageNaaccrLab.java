package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import de.siegmar.fastcsv.writer.CsvWriter;

import com.imsweb.algorithms.historicstage.HistoricStageInputDto;
import com.imsweb.algorithms.historicstage.HistoricStageUtils;
import com.imsweb.layout.LayoutFactory;
import com.imsweb.layout.record.fixed.naaccr.NaaccrLayout;

/**
 * Read in a NAACCR 14 gz file and output a csv with registry, pat id, seq num, yeardx, stage
 * Created by keelg on 6/29/2015.
 */
public class HistoricStageNaaccrLab {

    public static void main(String[] args) throws IOException {

        NaaccrLayout layout = (NaaccrLayout)LayoutFactory.getLayout(LayoutFactory.LAYOUT_ID_NAACCR_16_INCIDENCE);
        CsvWriter writer = CsvWriter.builder().build(new FileWriter("C:\\Users\\flynng\\Desktop\\tmpTest\\algorithms.histstage.csv"));
        File dataDir = new File("C:\\Users\\flynng\\Desktop\\tmpTest\\");

        File[] files = dataDir.listFiles();
        if (files != null) {
            for (File dataFile : files) {
                if (!dataFile.getName().endsWith(".txd.gz")) continue;

                LineNumberReader reader = new LineNumberReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(dataFile))));
                Map<String, String> rec = layout.readNextRecord(reader);
                while (rec != null) {
                    // process record...
                    String[] line = new String[5];
                    line[0] = rec.get(layout.getFieldByNaaccrItemNumber(40).getName()); //Regsitry
                    line[1] = rec.get(layout.getFieldByNaaccrItemNumber(20).getName()); //Pat ID
                    line[2] = rec.get(layout.getFieldByNaaccrItemNumber(380).getName()); //Sequence number
                    line[3] = rec.get(layout.getFieldByNaaccrItemNumber(390).getName()); //Yeardx

                    HistoricStageInputDto inputDto = new HistoricStageInputDto();

                    // translate record into input DTO (method taking a record was deprecated)

                    line[4] = HistoricStageUtils.computeHistoricStage(inputDto).getResult();
                    writer.writeRecord(line);
                    rec = layout.readNextRecord(reader);
                }
                reader.close();
            }
        }
        writer.close();
    }
}