package com.stkj.cashier.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/7/10
 */
public class WakeUpAppReceiver extends BroadcastReceiver {
    public static String packName = null;
    public static boolean wakeUpControlFlag = false;

    public WakeUpAppReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        if (wakeUpControlFlag) {
            if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
                PackageManager pm = context.getPackageManager();
                if (packName != null) {
                    Intent launchIntent = pm.getLaunchIntentForPackage(packName);
                    if (launchIntent != null) {
                        launchIntent.setFlags(268435456);
                        context.startActivity(launchIntent);
                    }
                }

                wakeUpControlFlag = false;
                packName = null;
            }

        }
    }
}