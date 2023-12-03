/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.historicstage;

import java.util.List;

import org.apache.commons.lang3.Range;
import org.junit.Assert;
import org.junit.Test;

import com.imsweb.algorithms.historicstage.internal.HistStageDataDto;

public class HistStageDataDtoTest {

    private static class HistStageDataTestDto extends HistStageDataDto {

        @Override
        protected List<Object> parse(String str) {
            return super.parse(str);
        }

        @Override
        protected boolean find(List<Object> list, String value) {
            return super.find(list, value);
        }
    }

    @Test
    public void testParse() {
        HistStageDataTestDto dto = new HistStageDataTestDto();
        List<Object> result = dto.parse("8000,9540-9589,9990-9999,&-");
        Range<Integer> range = Range.of(9540, 9589);
        Assert.assertTrue(result.contains(8000));
        Assert.assertTrue(result.contains("&-"));
        Assert.assertTrue(result.contains(range));
        range = Range.of(9990, 9999);
        Assert.assertTrue(result.contains(range));
        Assert.assertFalse(result.contains(9540));
        Assert.assertFalse(result.contains("8000"));
        range = Range.of(8000, 9540);
        Assert.assertFalse(result.contains(range));
        result = dto.parse("345A-346B,7-8-9,-2,-8000-2000,0001-9999,932-999a,000-075,-&90, 34,22");
        Assert.assertTrue(result.contains("345A-346B"));
        Assert.assertTrue(result.contains("7-8-9"));
        Assert.assertTrue(result.contains("-2"));
        Assert.assertTrue(result.contains("-8000-2000"));
        Assert.assertTrue(result.contains("932-999a"));
        Assert.assertTrue(result.contains("-&90"));
        Assert.assertTrue(result.contains(" 34"));
        Assert.assertTrue(result.contains(22));
        range = Range.of(1, 9999);
        Assert.assertTrue(result.contains(range));
        range = Range.of(0, 75);
        Assert.assertTrue(result.contains(range));
        range = Range.of(345, 346);
        Assert.assertFalse(result.contains(range));
        range = Range.of(7, 8);
        Assert.assertFalse(result.contains(range));
        range = Range.of(8, 9);
        Assert.assertFalse(result.contains(range));
        range = Range.of(7, 9);
        Assert.assertFalse(result.contains(range));
        range = Range.of(-8000, 2000);
        Assert.assertFalse(result.contains(range));
        range = Range.of(932, 999);
        Assert.assertFalse(result.contains(range));
        Assert.assertFalse(result.contains(34));
    }

    @Test
    public void testFind() {
        HistStageDataTestDto dto = new HistStageDataTestDto();
        List<Object> result = dto.parse("9121,-&,1-9,8156&,000-419,422-423,425-809");
        Assert.assertTrue(dto.find(result, "003"));
        Assert.assertTrue(dto.find(result, "8"));
        Assert.assertTrue(dto.find(result, "808"));
        Assert.assertTrue(dto.find(result, "423"));
        Assert.assertTrue(dto.find(result, "-&"));
        Assert.assertTrue(dto.find(result, "05"));
        Assert.assertTrue(dto.find(result, "9121"));
        Assert.assertTrue(dto.find(result, "8156&"));
        Assert.assertFalse(dto.find(result, "8156"));
        Assert.assertFalse(dto.find(result, "424"));
        Assert.assertFalse(dto.find(result, "1-9"));
        Assert.assertFalse(dto.find(result, "810"));
    }
}
