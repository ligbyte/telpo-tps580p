package com.stkj.cashier.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;

public class TimeUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isCurrentTimeIsInRound(String start, String end){
        LocalTime beginTime = null;
        LocalTime endTime = null;

        try {
            beginTime = LocalTime.parse(start);
            endTime = LocalTime.parse(end);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (beginTime == null || endTime == null) {
            return false;
        }
        LocalTime nowTime = LocalTime.now();

        return nowTime.isAfter(beginTime) && nowTime.isBefore(endTime);
    }

}
