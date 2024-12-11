package com.stkj.cashier.util;

/**
 * @description $ 卖宝乐 称重 串口数据解析
 * @author: Administrator
 * @date: 2024/4/15
 */
public class Hex2StringUtlis {
    public static String hexStringToString(String str) {
        String str2;
        if (str == null || str.equals("")) {
            return null;
        }
        String replace = str.replace(" ", "");
        byte[] bArr = new byte[replace.length() / 2];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i * 2;
            try {
                bArr[i] = (byte) (Integer.parseInt(replace.substring(i2, i2 + 2), 16) & 255);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            str2 = new String(bArr, "UTF-8");
            try {
                new String();
            } catch (Exception e2) {
                e2.printStackTrace();
                return str2;
            }
        } catch (Exception e3) {
            str2 = replace;
        }
        return str2;
    }
}