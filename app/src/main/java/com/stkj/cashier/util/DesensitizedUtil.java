package com.stkj.cashier.util;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/4/18
 */
public  class DesensitizedUtil {
    public static String desensitizeName(String fullName, int visibleChars) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }

        // 确保visibleChars不小于1，并且不大于姓名的长度
        visibleChars = Math.max(1, Math.min(visibleChars, fullName.length()));

        // 截取姓名的前visibleChars个字符
        String visiblePart = fullName.substring(0, visibleChars);

        // 计算需要添加的占位符数量
        int maskChars = fullName.length() - visibleChars;
        if (maskChars > 0) {
            // 创建占位符字符串
            StringBuilder mask = new StringBuilder();
            for (int i = 0; i < maskChars; i++) {
                mask.append('*');
            }
            // 将占位符添加到脱敏后的姓名中
            visiblePart += mask;
        }

        return visiblePart;
    }
    public static String desensitizePhoneNumber(String phoneNumber, int visibleDigits) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }

        // 确保visibleDigits不小于1，并且不大于手机号的长度
        visibleDigits = Math.max(1, Math.min(visibleDigits, phoneNumber.length()));

        // 根据visibleDigits确定脱敏的位置
        int maskStart = phoneNumber.length() - visibleDigits;
        if (maskStart < 0) {
            maskStart = 0; // 如果visibleDigits大于手机号长度，则从开头开始脱敏
        }

        // 创建脱敏后的手机号字符串
        StringBuilder desensitizedNumber = new StringBuilder(phoneNumber.substring(0, maskStart));

        // 添加占位符
        for (int i = maskStart; i < phoneNumber.length(); i++) {
            desensitizedNumber.append('*');
        }

        return desensitizedNumber.toString();
    }
}
