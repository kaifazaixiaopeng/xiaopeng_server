package com.xiaopeng.server.app.bean.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auto:BUGPeng
 * @Date:2022/6/17 9:55
 * @ClassName:SimpleDate
 * @Remark:
 */
public class SimpleDate {
    /**
     *    大写HH 24小时制
     *    小写hh 12小时制
     */
    private static final String DEFAULT_DATE="yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat format;
    public static String getDefaultDate(Date date){
        format = new SimpleDateFormat(DEFAULT_DATE);
        return format.format(date);
    }
}
