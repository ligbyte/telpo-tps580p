package com.stkj.cashier.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/4/22
 */
public class WechatOrPayQRcodeUtil {
    public static void main(String[] strArr) {
    }

    public static int payCodeType(String str) {
        if (Tools.isEmpty(str)) {
            return 0;
        }
        if (AliPayRegexMatches(str)) {
            return 40;
        }
        return WechatRegexMatches2(str) ? 50 : 0;
    }

    public static boolean AliPayRegexMatches(String str) {
        Matcher matcher = Pattern.compile("^(25|26|27|28|29|30)\\d{14,22}$").matcher(str);
        System.out.println(matcher.matches());
        return matcher.matches();
    }

    public static boolean WechatRegexMatches2(String str) {
        Matcher matcher = Pattern.compile("^(10|11|12|13|14|15)\\d{16}$").matcher(str);
        System.out.println(matcher.matches());
        return matcher.matches();
    }
}