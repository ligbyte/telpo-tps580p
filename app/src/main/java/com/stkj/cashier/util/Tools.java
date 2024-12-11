package com.stkj.cashier.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stkj.cashier.util.util.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.params.HttpParams;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/4/15
 */
public class Tools {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private static int NO_OF_EMOTICONS = 54;
    public static final int exitToLognTwoAcivity = 0;
    private static long lastClickTime;
    private static long lastClickTime2;
    private static String mDay;
    private static String mMonth;
    private static String mWay;
    private static String mYear;
    private static final int[] dayArr = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private static final String[] constellationArr = {"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};

    public static int getMaxInt() {
        return Integer.MAX_VALUE;
    }

    public static boolean isNullObject(Object obj) {
        return obj == null;
    }

    public static void shortToast(Context context, String str) {
    }

    public static boolean activityIsRunging(Activity activity) {
        if (activity == null) {
            return true;
        }
        if (activity.isFinishing()) {
            LogUtils.e("activityIsRungingtrue");
            return true;
        }
        LogUtils.e("activityIsRungingfalse");
        return false;
    }

    public static long stringToLongDate(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String stringDecimalFormat(String str, String str2) {
        if (str.equals("") || str == null) {
            str = "0";
        }
        return new DecimalFormat(str2).format(Double.valueOf(str).doubleValue());
    }

    public static double doubleAccurate(String str) {
        try {
            if (isEmpty(str)) {
                str = "0";

            }
            return Double.parseDouble(String.format("%.2f", Double.valueOf(Double.valueOf(str).doubleValue())));
        } catch (Exception e) {
            LogUtils.e(e.toString());
            return 0.0d;
        }
    }
    public static double threeAccurate(String str) {
        try {
            if (isEmpty(str)) {
                str = "0";

            }
            return Double.parseDouble(String.format("%.2f", Double.valueOf(Double.valueOf(str).doubleValue())));
        } catch (Exception e) {
            LogUtils.e(e.toString());
            return 0.0d;
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("null") || str.trim().equals("") || TextUtils.isEmpty(str);
    }


    public static boolean isIntenetConnected(Context context) {
        if (context != null) {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(9);
            return !isNullObject(networkInfo) && networkInfo.isConnected() && networkInfo.isAvailable();
        }
        return false;
    }

    public static String getWeekOfDate() {
        String[] strArr = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int i = calendar.get(7) - 1;
        if (i < 0) {
            i = 0;
        }
        return strArr[i];
    }

    public static String getOrderNum() {
        String format = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        return format + ((int) ((Math.random() * 9000.0d) + 1000.0d));
    }

    public static String getOrderNumOnlineTurnOffline() {
        String format = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        return format + ((int) ((Math.random() * 9000.0d) + 1000.0d)) + "ZX";
    }

    public static String getDateThisTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static List<String> mockData() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("充值模式");
        arrayList.add("录入设备号");
        arrayList.add("接口地址修改");
        return arrayList;
    }

    public static String getEQId(Activity activity) {
        return ((TelephonyManager) activity.getSystemService("phone")).getDeviceId();
    }

    public static void prohibitInput(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }


}