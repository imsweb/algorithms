/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents the cause specific death classification tables.
 * Author: Sewbesew Bekele
 * Date: Feb 7, 2014
 */
public class CauseSpecificDataDto {

    private String _icdVersion;

    private String _seq;

    private String _recode;

    private List<Object> _deathCode3Dig;

    private List<Object> _deathCode4Dig;

    public CauseSpecificDataDto(String[] row) {
        _icdVersion = row[0];
        _seq = row[1];
        _recode = row[2];
        _deathCode3Dig = parse(row[3]);
        _deathCode4Dig = parse(row[4]);     
    }

    //The row in the text file represents a combination of icd revision number, sequence group, site recode and cause of death which is considered as dead ("1").
    //Anything not in the table is considered as alive or dead of other causes ("0").
    public boolean doesMatchThisRow(String icdVersion, String seq, String recode, String cod) {
        if (!(_icdVersion.equals(icdVersion) && _seq.equals(seq) && _recode.equals(recode)) || cod == null || cod.length() < 3)
            return false;     
       
        for (Object obj : _deathCode3Dig) {            
            if (obj instanceof CodRange) {
                CodRange range = (CodRange)obj;                
                if (cod.substring(0, 3).compareToIgnoreCase(range.getStart()) >= 0 && cod.substring(0, 3).compareToIgnoreCase(range.getEnd()) <= 0)
                    return true;
            }
            else {
                if (cod.substring(0, 3).equals(obj))
                    return true;
            }
        }
        
        for (Object obj : _deathCode4Dig) {
            if (obj instanceof CodRange) {
                CodRange range = (CodRange)obj;
                if (cod.compareToIgnoreCase(range.getStart()) >= 0 && cod.compareToIgnoreCase(range.getEnd()) <= 0)
                    return true;
            }
            else {
                if (cod.equals(obj))
                    return true;
            }
        }

        return false;
    }

    private List<Object> parse(String str) {
        List<Object> result = new ArrayList<>();
        if (str == null || str.trim().isEmpty())
            return result;

        for (String token : StringUtils.split(str, ',')) {
            token = token.trim();
            if (token.contains("-"))
                result.add(new CodRange(token.split("-")[0], token.split("-")[1]));
            else
                result.add(token);            
        }

        return result;
    }

    private static final class CodRange {

        String _start;

        String _end;

        private CodRange(String start, String end) {
            _start = start;
            _end = end;
        }

        public String getStart() {
            return _start;
        }

        public String getEnd() {
            return _end;
        }
    }
} 
