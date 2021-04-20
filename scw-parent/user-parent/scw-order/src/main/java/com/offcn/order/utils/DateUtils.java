package com.offcn.order.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class DateUtils {
    /*当前时间格式化*/
    public static String getFormatNow(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        return  format;
    }

    public static String getFormatNow(Date data){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(data);
        return  format;
    }

    public static String getFormatNow(String patten){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        String format = simpleDateFormat.format(new Date());
        return  format;
    }

    public static String getFormatNow(Date date,String patten){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        String format = simpleDateFormat.format(date);
        return  format;
    }
}
