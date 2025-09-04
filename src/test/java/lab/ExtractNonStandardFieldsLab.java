/*
 * Copyright (C) 2025 Information Management Services, Inc.
 */
package lab;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.imsweb.algorithms.AlgorithmField;
import com.imsweb.algorithms.Algorithms;

public class ExtractNonStandardFieldsLab {

    public static void main(String[] args) {
        Algorithms.initialize();

        System.out.println("NAACCR Number,NAACCR ID,NAACCR Name,Length,Level");
        for (AlgorithmField field : Algorithms.getAllFields().stream().sorted(Comparator.comparing(AlgorithmField::getNumber)).collect(Collectors.toList())) {
            if (!field.isNaaccrStandard()) {
                StringBuilder buf = new StringBuilder();
                buf.append(field.getNumber());
                buf.append(",");
                buf.append(field.getId());
                buf.append(",");
                if (field.getName().contains(","))
                    buf.append("\"");
                buf.append(field.getName());
                if (field.getName().contains(","))
                    buf.append("\"");
                buf.append(",");
                buf.append(field.getLength());
                buf.append(",");
                buf.append(field.getDataLevel());
                System.out.println(buf);
            }
        }
    }

}
