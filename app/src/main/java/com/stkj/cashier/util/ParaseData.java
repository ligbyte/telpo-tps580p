package com.stkj.cashier.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/4/22
 */
public class ParaseData {
    public static String decodeHexString2(String str) {
        String bigInteger = new BigInteger(HighLowHex(spaceHex(str)), 16).toString();
        int length = bigInteger.length();
        for (int i = 0; i < 4 - length; i++) {
            bigInteger = "0" + bigInteger;
        }
        return bigInteger;
    }

    public static String decodeHexString(String str) {
        String bigInteger = new BigInteger(HighLowHex(spaceHex(str)), 16).toString();
        int length = bigInteger.length();
        for (int i = 0; i < 10 - length; i++) {
            bigInteger = "0" + bigInteger;
        }
        return bigInteger;
    }

    public static String spaceHex(String str) {
        char[] charArray = str.toCharArray();
        if (str.length() <= 2) {
            return str;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        while (i < charArray.length) {
            int i2 = i + 1;
            if (i2 % 2 == 0) {
                stringBuffer.append(charArray[i]);
                stringBuffer.append(" ");
            } else {
                stringBuffer.append(charArray[i]);
            }
            i = i2;
        }
        return stringBuffer.toString();
    }

    public static String HighLowHex(String str) {
        if (str.trim().length() <= 2) {
            return str;
        }
        List<String> asList = Arrays.asList(str.split(" "));
        Collections.reverse(asList);
        StringBuffer stringBuffer = new StringBuffer();
        for (String str2 : asList) {
            stringBuffer.append(str2);
        }
        return stringBuffer.toString();
    }

    public static void main(String[] strArr) {
        System.out.println(decodeHexString("AE21A4CE5DF3"));
    }
}