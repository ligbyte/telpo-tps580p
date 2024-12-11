package com.stkj.cashier.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.stkj.cashier.util.util.LogUtils;
import com.stkj.cashier.App;
import com.stkj.cashier.app.main.MainActivity;
import com.stkj.cashier.app.splash.SplashActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
        LogUtils.e("开机自启==", "我接收到广播啦");

//        }
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED: {

                //1000
                Observable.timer(10, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            MainActivity mMainActivity = App.instance.getMMainActivity();
                            SplashActivity mSplashActivity = App.instance.getMSplashActivity();
                            if (mMainActivity!=null||mSplashActivity!=null){
                                if (mSplashActivity!=null){
                                    LogUtils.e("开机自启mSplashActivity"+mSplashActivity.isTaskRoot());
                                    boolean mSplashRunning = isActivityRunning(mSplashActivity, SplashActivity.class.getName());
                                    LogUtils.e("开机自启mSplashActivity"+mSplashRunning);
                                    // ToastUtils.showShort("开机自启mSplashActivity"+mSplashRunning);
                                    if (!mSplashRunning){
                                        Intent it = new Intent(context, SplashActivity.class);
                                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(it);
                                        LogUtils.e("开机自启1==", "手机开机了");
                                    }
                                }else {
                                    LogUtils.e("开机自启mMainActivity"+mMainActivity.isTaskRoot());
                                    boolean activityRunning = isActivityRunning(mMainActivity, MainActivity.class.getName());
                                    LogUtils.e("开机自启mMainActivity"+activityRunning);
                                    // ToastUtils.showShort("开机自启mMainActivity"+activityRunning);
                                    if (!activityRunning){
                                        Intent it = new Intent(context, SplashActivity.class);
                                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(it);
                                        LogUtils.e("开机自启2==", "手机开机了");
                                    }
                                }
                            }else {
                                Intent it = new Intent(context, SplashActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(it);
                                LogUtils.e("开机自启2==", "手机开机了");
                            }

                        });
            }
            break;
        }


//            case  Intent.ACTION_SHUTDOWN:
//                Log.e("开机自启==","手机关机了");
//                break;
//            case Intent.ACTION_SCREEN_ON:
//                Log.e("开机自启==","亮屏");
//                break;
//            case Intent.ACTION_SCREEN_OFF:
//                Log.e("开机自启==","息屏");
//                break;
//            case Intent.ACTION_USER_PRESENT:
//                Log.e("开机自启==","手机解锁");
//                break;
    }
    private boolean isActivityRunning(Context context, String activityName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            // 获取当前运行的任务信息
            for (ActivityManager.RunningTaskInfo taskInfo : am.getRunningTasks(Integer.MAX_VALUE)) {
                if (taskInfo.topActivity.getClassName().equals(activityName)) {
                    // 如果找到对应的Activity，且它是顶部活动，则认为它存在且未销毁
                    return true;
                }
            }
        }
        return false;
    }
}


