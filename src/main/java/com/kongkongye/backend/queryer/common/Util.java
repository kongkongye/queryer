package com.kongkongye.backend.queryer.common;

import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    /**
     * @param time 'yyyy-MM-dd'
     * @param end  是否结束时间
     * @return 毫秒
     */
    @SneakyThrows
    public static Long toMilli(String time, boolean end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long result = sdf.parse(time).getTime();
        return end ? result + 24L * 3600 * 1000 : result;
    }

    /**
     * @return 天,'yyyy-MM-dd'
     */
    @SneakyThrows
    public static String fromMilli(long milli) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(milli));
    }
}
