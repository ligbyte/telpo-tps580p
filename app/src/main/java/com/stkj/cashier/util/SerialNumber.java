package com.stkj.cashier.util;

import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;

public class SerialNumber {
    public static String getMachineNumber() {
//        //通过反射获取sn号
        String serial = "";
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = String.valueOf(get.invoke(c, "ro.serialno"));
            if (!serial.equals("") && !serial.equals("unknown")) {
                return serial;
            }
            //9.0及以上无法获取到sn，此方法为补充，能够获取到多数高版本手机 sn
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = Build.getSerial();
                if (!TextUtils.isEmpty(serial)) {
                    return serial;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return Build.SERIAL;
    }

}
