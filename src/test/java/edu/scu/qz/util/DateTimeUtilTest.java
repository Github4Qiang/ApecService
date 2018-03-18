package edu.scu.qz.util;

import org.junit.Test;

import java.util.Date;

public class DateTimeUtilTest {

    private static final String formatStr = "yyyy-MM-dd HH:mm:ss";
    private static final String dateTimeStr = "2011-01-01 11:11:11";

    @Test
    public void strToDate() {
        System.out.println(DateTimeUtil.dateToStr(new Date(), formatStr));
    }

    @Test
    public void dateToStr() {
        System.out.println(DateTimeUtil.strToDate(dateTimeStr, formatStr));
    }
}