package com.stkj.cashier.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

//import com.telpo.tps550.api.util.ShellUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/7/10
 */
public class SystemApiUtil {
    private Context mContext;
    private WakeUpAppReceiver wakeUpAppReceiver;

    public SystemApiUtil(Context context) {
        this.mContext = context;
    }

    public synchronized void registerWakeUpAppBroadcast() {
        if (this.wakeUpAppReceiver == null && this.mContext != null) {
            this.wakeUpAppReceiver = new WakeUpAppReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PACKAGE_REMOVED");
            filter.addAction("android.intent.action.PACKAGE_REPLACED");
            filter.addAction("android.intent.action.PACKAGE_ADDED");
            filter.addAction("android.intent.action.REBOOT");
            filter.addDataScheme("package");
            this.mContext.registerReceiver(this.wakeUpAppReceiver, filter);
        }

    }

    public synchronized void unRegisterWakeUpAppBroadcast() {
        if (this.wakeUpAppReceiver != null && this.mContext != null) {
            this.mContext.unregisterReceiver(this.wakeUpAppReceiver);
        }

    }

    public synchronized void showStatusBar() {
        Intent intent = this.createIntent();
        intent.setAction("android.intent.action.STATUSBAR");
        intent.putExtra("status", 1);
        this.mContext.sendOrderedBroadcast(intent, (String)null);
        intent = this.createIntent();
        intent.setAction("android.intent.action.systemui");
        intent.putExtra("status_bar", "show");
        this.mContext.sendOrderedBroadcast(intent, (String)null);
    }

    public synchronized void hideStatusBar() {
        Intent intent = this.createIntent();
        intent.setAction("android.intent.action.STATUSBAR");
        intent.putExtra("status", 0);
        this.mContext.sendOrderedBroadcast(intent, (String)null);
        intent = this.createIntent();
        intent.setAction("android.intent.action.systemui");
        intent.putExtra("status_bar", "dismiss");
        this.mContext.sendOrderedBroadcast(intent, (String)null);
    }

    public synchronized void showNavigationBar() {
        Intent intent = this.createIntent();
        intent.setAction("com.android.internal.policy.impl.showNavigationBar");
        this.mContext.sendOrderedBroadcast(intent, (String)null);
        intent = this.createIntent();
        intent.setAction("android.intent.action.systemui");
        intent.putExtra("navigation_bar", "show");
        this.mContext.sendOrderedBroadcast(intent, (String)null);
    }

    public synchronized void hideNavigationBar() {
        Intent intent = this.createIntent();
        intent.setAction("com.android.internal.policy.impl.hideNavigationBar");
        this.mContext.sendOrderedBroadcast(intent, (String)null);
        intent = this.createIntent();
        intent.setAction("android.intent.action.systemui");
        intent.putExtra("navigation_bar", "dismiss");
        this.mContext.sendOrderedBroadcast(intent, (String)null);
    }

    @RequiresApi(
            api = 17
    )
    public synchronized void setSystemTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeInMillis);
        String str = sdf.format(date);
        int year = Integer.valueOf(str.substring(0, 4));
        int month = Integer.valueOf(str.substring(5, 7));
        int day = Integer.valueOf(str.substring(8, 10));
        int hourOfDay = Integer.valueOf(str.substring(11, 13));
        int minute = Integer.valueOf(str.substring(14, 16));
        int second = Integer.valueOf(str.substring(17, 19));
        Intent intent = this.createIntent();
        intent.setAction("android.intent.action.SET_SYSTEM_TIME");
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        intent.putExtra("hour", hourOfDay);
        intent.putExtra("minute", minute);
        intent.putExtra("second", second);
        this.mContext.sendOrderedBroadcast(intent, (String)null);
    }

    public synchronized void installApp(String appPath, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            WakeUpAppReceiver.packName = packageName;
            WakeUpAppReceiver.wakeUpControlFlag = true;
        }

        Intent intent = this.createIntent();
        intent.setAction("android.intent.action.appinstall");
        intent.putExtra("quiet_install", appPath);
        this.mContext.sendOrderedBroadcast(intent, (String)null);
        intent = this.createIntent();
        intent.setAction("android.intent.action.application");
        intent.putExtra("quiet_install", appPath);
        this.mContext.sendOrderedBroadcast(intent, (String)null);
    }

    public synchronized void uninstallApp(String packageName) {
        Intent intent = this.createIntent();
        intent.setAction("android.intent.action.uninstall");
        intent.putExtra("quiet_uninstall", packageName);
        this.mContext.sendOrderedBroadcast(intent, (String)null);
        intent = this.createIntent();
        intent.setAction("android.intent.action.application");
        intent.putExtra("uninstall", packageName);
        this.mContext.sendOrderedBroadcast(intent, (String)null);
    }

    public synchronized void setStaticIpConfig() {
    }

    public synchronized void rebootDevice() {
//        ShellUtils.execCommand("reboot", false);
    }

    public synchronized void shutdown() {
//        ShellUtils.execCommand("reboot -p", false);
    }

    public synchronized void installPackage() {
        try {
            Intent intent = this.createIntent();
            intent.setAction("android.intent.action.TELPO_SYSTEM_UPDATE");
            this.mContext.sendBroadcast(intent);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    @SuppressLint({"WrongConstant"})
    private Intent createIntent() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            intent.setFlags(16777216);
        }

        return intent;
    }
}

