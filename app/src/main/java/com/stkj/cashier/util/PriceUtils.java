package com.stkj.cashier.util;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PriceUtils {

    /**
     * 格式化价格（保留两位小数，小数无0时补位）
     */
    public static String formatPrice(String price) {
        if (TextUtils.isEmpty(price)) {
            return "0.00";
        }
        String result = price;
        try {
            double parseDouble = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.HALF_UP);
            result = df.format(parseDouble);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化价格（保留两位小数，小数无0时补位）
     */
    public static String formatPrice(double price) {
        String result = String.valueOf(price);
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.HALF_UP);
            result = df.format(price);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化价格（保留两位小数，小数无0时不补位）
     */
    public static String formatPrice2(String price) {
        if (TextUtils.isEmpty(price)) {
            return "0.00";
        }
        String result = price;
        try {
            double parseDouble = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            result = df.format(parseDouble);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化价格（保留两位小数，小数无0时不补位）
     */
    public static String formatPrice2(Double price) {
        String result = String.valueOf(price);
        try {
            DecimalFormat df = new DecimalFormat("0.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            result = df.format(price);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String moJiaoPrice(String price) {
        String result = price;
        try {
            int indexOf = price.indexOf(".");
            if (indexOf != -1) {
                result = price.substring(0, indexOf) + ".00";
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String moFenPrice(String price) {
        String result = price;
        try {
            int indexOf = price.indexOf(".");
            if (indexOf != -1 && (indexOf + 2) < price.length()) {
                result = price.substring(0, indexOf + 2) + "0";
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double parsePrice(String price) {
        double result = 0;
        try {
            result = Double.parseDouble(price);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

}
