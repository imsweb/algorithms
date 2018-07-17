/*
 * Copyright (C) 2018 Information Management Services, Inc.
 */
package com.imsweb.algorithms.iarc;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.imsweb.algorithms.AlgorithmsUtils;

/**
 * This class is used to calculate the International Primary indicator variable.
 * <br/><br/>
 * See <a href="http://www.iacr.com.fr/images/doc/MPrules_july2004.pdf">http://www.iacr.com.fr/images/doc/MPrules_july2004.pdf/</a>.
 */
public class IarcUtils {

    public static final String ALG_NAME = "IARC Multiple Primary Algorithm";
    public static final String VERSION = "ICD-O Third Edition";
    public static final String ALG_INFO = "International rules for multiple primary cancers released in 2004";

    public static final Integer DUPLICATE = 0;
    public static final Integer PRIMARY = 1;
    public static final Integer INSITU = 9;

    public static List<IarcInputRecordDto> calculateIarc(List<IarcInputRecordDto> records) {
        //No records
        if (records == null || records.isEmpty())
            return records;

        //If there is only one tumor
        if (records.size() == 1) {
            if (isInsitu(records.get(0)))
                records.get(0).setInternationalPrimaryIndicator(INSITU);
            else
                records.get(0).setInternationalPrimaryIndicator(PRIMARY);
            return records;
        }

        //Calculate the site group and hist group,
        for (IarcInputRecordDto record : records) {
            //Set iarc as primary by default
            record.setInternationalPrimaryIndicator(PRIMARY);
            if (record.getSite() != null && record.getSite().length() >= 3) {
                String site = record.getSite().toUpperCase().substring(0, 3);
                //By default only the first 3
                record.setSiteGroup(site);
                if (Arrays.asList("C01", "C02").contains(site))
                    record.setSiteGroup("C029");
                else if (Arrays.asList("C00", "C03", "C04", "C05", "C06").contains(site))
                    record.setSiteGroup("C069");
                else if (Arrays.asList("C09", "C10", "C12", "C13", "C14").contains(site))
                    record.setSiteGroup("C140");
                else if (Arrays.asList("C19", "C20").contains(site))
                    record.setSiteGroup("C209");
                else if (Arrays.asList("C23", "C24").contains(site))
                    record.setSiteGroup("C249");
                else if (Arrays.asList("C33", "C34").contains(site))
                    record.setSiteGroup("C349");
                else if (Arrays.asList("C40", "C41").contains(site))
                    record.setSiteGroup("C419");
                else if (Arrays.asList("C65", "C66", "C67", "C68").contains(site))
                    record.setSiteGroup("C689");
            }

            if (record.getHistology() != null && NumberUtils.isDigits(record.getHistology())) {
                int hist = NumberUtils.toInt(record.getHistology());
                record.setHistGroup(hist);
                if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8051-8084,8120-8131"), hist))
                    record.setHistGroup(1);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8090-8110"), hist))
                    record.setHistGroup(2);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8140-8149,8160-8162,8190-8221,8260-8337,8350-8551,8570-8576,8940-8941"), hist))
                    record.setHistGroup(3);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8030-8046,8150-8157,8170-8180,8230-8255,8340-8347,8560-8562,8580-8671"), hist))
                    record.setHistGroup(4);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8010-8015,8020-8022,8050"), hist))
                    record.setHistGroup(5);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8680-8713,8800-8921,8990-8991,9040-9044,9120-9125,9130-9136,9141-9252,9370-9373,9540-9582"), hist))
                    record.setHistGroup(6);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9050-9055"), hist))
                    record.setHistGroup(7);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9840,9861-9931,9945-9946,9950,9961-9964,9980-9987,9991-9992"), hist))
                    record.setHistGroup(8);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9597,9670-9699,9712,9728,9731-9738,9761-9767,9769,9811-9818,9823-9826,9833,9836,9940"), hist))
                    record.setHistGroup(9);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9700-9726,9729,9768,9827-9831,9834,9837,9948"), hist))
                    record.setHistGroup(10);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9650-9667"), hist))
                    record.setHistGroup(11);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9740-9742"), hist))
                    record.setHistGroup(12);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9750-9759"), hist))
                    record.setHistGroup(13);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("9590-9591,9596,9727,9760,9800-9809,9820,9832,9835,9860,9960,9965-9975,9989"), hist))
                    record.setHistGroup(14);
                else if (hist == 9140)
                    record.setHistGroup(15);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8720-8790,8930-8936,8950-8983,9000-9030,9060-9110,9260-9365,9380-9539"), hist))
                    record.setHistGroup(16);
                else if (AlgorithmsUtils.isContained(AlgorithmsUtils.expandHistologiesAsIntegers("8000-8005"), hist))
                    record.setHistGroup(17);
            }

        }

        //Calculate IARC
        for (int i = 0; i < records.size(); i++) {
            IarcInputRecordDto record1 = records.get(i);
            if (DUPLICATE.equals(record1.getInternationalPrimaryIndicator()) || INSITU.equals(record1.getInternationalPrimaryIndicator()))
                continue;
            //Insitu cases are ignored except bladder
            if (isInsitu(record1)) {
                record1.setInternationalPrimaryIndicator(INSITU);
                continue;
            }
            for (int j = i + 1; j < records.size(); j++) {
                IarcInputRecordDto record2 = records.get(j);
                //Insitu cases are ignored except bladder
                if (isInsitu(record2)) {
                    record2.setInternationalPrimaryIndicator(INSITU);
                    continue;
                }

                if (record1.getHistGroup() != null && record2.getHistGroup() != null && record1.getSiteGroup() != null && record2.getSiteGroup() != null) {

                    //Kaposi sarcoma or Hemato, no need to check the site
                    if ((record1.getHistGroup() == 15 && record2.getHistGroup() == 15) || (record1.getHistGroup() >= 8 && record1.getHistGroup() <= 14 && record2.getHistGroup() >= 8
                            && record2.getHistGroup() <= 14 && (record1.getHistGroup() == 14 || record2.getHistGroup() == 14 || record1.getHistGroup().equals(record2.getHistGroup())))) {
                        if (record1.getHistGroup() == 14 && record2.getHistGroup() != 14)
                            record1.setInternationalPrimaryIndicator(DUPLICATE);
                        else if (record1.getHistGroup() != 14 && record2.getHistGroup() == 14)
                            record2.setInternationalPrimaryIndicator(DUPLICATE);
                        else if (earlierDxDate(record1, record2) == 1)
                            record2.setInternationalPrimaryIndicator(DUPLICATE);
                        else
                            record1.setInternationalPrimaryIndicator(DUPLICATE);
                    }
                    else if (record1.getSiteGroup().equals(record2.getSiteGroup())) {
                        //NOS vs Specific
                        if (record1.getHistGroup() == 17 || record2.getHistGroup() == 17) {
                            if (record1.getHistGroup() == 17)
                                record1.setInternationalPrimaryIndicator(DUPLICATE);
                            else
                                record2.setInternationalPrimaryIndicator(DUPLICATE);
                        }
                        else if (record1.getHistGroup() == 14 && record2.getHistGroup() >= 8 && record2.getHistGroup() < 14)
                            record1.setInternationalPrimaryIndicator(DUPLICATE);
                        else if (record2.getHistGroup() == 14 && record1.getHistGroup() >= 8 && record1.getHistGroup() < 14)
                            record2.setInternationalPrimaryIndicator(DUPLICATE);
                        else if (record1.getHistGroup() == 5 && record2.getHistGroup() >= 1 && record2.getHistGroup() < 5)
                            record1.setInternationalPrimaryIndicator(DUPLICATE);
                        else if (record2.getHistGroup() == 5 && record1.getHistGroup() >= 1 && record1.getHistGroup() < 5)
                            record2.setInternationalPrimaryIndicator(DUPLICATE);
                            //Single tumours containing several different histologies which fall into one histological group in Table 2
                            // are registered as a single case, using the numerically highest ICD-O morphology code
                        else if (record1.getHistGroup().equals(record2.getHistGroup())) {
//                            if (NumberUtils.toInt(record1.getHistology()) > NumberUtils.toInt(record2.getHistology()))
//                                record2.setInternationalPrimaryIndicator(DUPLICATE);
//                            else if (NumberUtils.toInt(record1.getHistology()) < NumberUtils.toInt(record2.getHistology()))
//                                record1.setInternationalPrimaryIndicator(DUPLICATE);
                                //Case diagnosed first is primary
                            if (earlierDxDate(record1, record2) == 1)
                                record2.setInternationalPrimaryIndicator(DUPLICATE);
                            else
                                record1.setInternationalPrimaryIndicator(DUPLICATE);
                        }
                    }
                }
            }
        }

        return records;
    }

    //Returns 1 if the first is earlier, otherwise returns -1
    private static int earlierDxDate(IarcInputRecordDto rec1, IarcInputRecordDto rec2) {
        if (NumberUtils.toInt(rec1.getDateOfDiagnosisYear(), 9999) < NumberUtils.toInt(rec2.getDateOfDiagnosisYear(), 9999))
            return 1;
        if (NumberUtils.toInt(rec1.getDateOfDiagnosisYear(), 9999) > NumberUtils.toInt(rec2.getDateOfDiagnosisYear(), 9999))
            return -1;

        //if known same year
        if (NumberUtils.toInt(rec1.getDateOfDiagnosisMonth(), 9999) != 9999) {
            if (NumberUtils.toInt(rec1.getDateOfDiagnosisMonth(), 99) < NumberUtils.toInt(rec2.getDateOfDiagnosisMonth(), 99))
                return 1;
            if (NumberUtils.toInt(rec1.getDateOfDiagnosisMonth(), 99) > NumberUtils.toInt(rec2.getDateOfDiagnosisMonth(), 99))
                return -1;

            if (NumberUtils.toInt(rec1.getDateOfDiagnosisMonth(), 99) <= 12 && NumberUtils.toInt(rec1.getDateOfDiagnosisMonth(), 99) >= 1) {
                if (NumberUtils.toInt(rec1.getDateOfDiagnosisDay(), 99) < NumberUtils.toInt(rec2.getDateOfDiagnosisDay(), 99))
                    return 1;
                if (NumberUtils.toInt(rec1.getDateOfDiagnosisDay(), 99) > NumberUtils.toInt(rec2.getDateOfDiagnosisDay(), 99))
                    return -1;
            }
        }

        //Use sequence number
        int seq1 = rec1.getSequenceNumber();
        if (seq1 >= 60 && seq1 <= 97)
            seq1 += 1000;
        int seq2 = rec2.getSequenceNumber();
        if (seq2 >= 60 && seq2 <= 97)
            seq2 += 1000;

        return seq1 <= seq2 ? 1 : -1;
    }

    private static boolean isInsitu(IarcInputRecordDto record) {
        return !"3".equals(record.getBehavior()) && !(record.getSite() != null && record.getSite().toUpperCase().startsWith("C67"));
    }
}
