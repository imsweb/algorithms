/*
 * Copyright (C) 2014 Information Management Services, Inc.
 */
package com.imsweb.algorithms.causespecific;

import org.junit.Assert;
import org.junit.Test;

public class CauseSpecificDataDtoTest {    
    
    @Test
    public void testMatchThisRow(){
        CauseSpecificDataDto dto = new CauseSpecificDataDto(new String[] {"1", "0", "27700", "B12-B14,C69,D42-E15", "C723-C924,B823,D471-D499", "test"});   
        
        Assert.assertFalse(dto.doesMatchThisRow("8", "0", "27700", "C698"));
        Assert.assertFalse(dto.doesMatchThisRow("1", "1", "27700", "C698"));
        Assert.assertFalse(dto.doesMatchThisRow("1", "0", "27710", "C698"));
        Assert.assertFalse(dto.doesMatchThisRow("1", "0", "27700", "B173"));
        Assert.assertFalse(dto.doesMatchThisRow("1", "0", "27700", "C710"));        
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "B138"));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "C698"));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "C69 "));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "D435"));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "E149 "));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "C885"));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "B823"));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "D471"));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "D485"));
        Assert.assertTrue(dto.doesMatchThisRow("1", "0", "27700", "D499"));

        dto = new CauseSpecificDataDto(new String[] {"8", "1", "27700", "612-614,620,622,624-625", "5960,6077,6150,6152-6159,6160-6161,6169,6230,6233-6239,6266,6299", "test"});

        Assert.assertFalse(dto.doesMatchThisRow("9", "1", "27700", "6135"));
        Assert.assertFalse(dto.doesMatchThisRow("8", "0", "27700", "6135"));
        Assert.assertFalse(dto.doesMatchThisRow("8", "1", "27710", "6135"));
        Assert.assertFalse(dto.doesMatchThisRow("8", "1", "27700", "611 "));
        Assert.assertFalse(dto.doesMatchThisRow("1", "0", "27700", "6151"));

        Assert.assertTrue(dto.doesMatchThisRow("8", "1", "27700", "613 "));
        Assert.assertTrue(dto.doesMatchThisRow("8", "1", "27700", "6138"));
        Assert.assertTrue(dto.doesMatchThisRow("8", "1", "27700", "622"));
        Assert.assertTrue(dto.doesMatchThisRow("8", "1", "27700", "5960"));
        Assert.assertTrue(dto.doesMatchThisRow("8", "1", "27700", "6152"));
        Assert.assertTrue(dto.doesMatchThisRow("8", "1", "27700", "6161"));
        Assert.assertTrue(dto.doesMatchThisRow("8", "1", "27700", "6238"));        
    }    
}
