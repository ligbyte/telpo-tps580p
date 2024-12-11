package com.stkj.cashier.util;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.kh.KaihuangManager;
import android.kh.PowerTime;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/7/16
 */
public class RkSysTool {
    private static  RkSysTool instance=null;
    private KaihuangManager kaihuangManager=null;
    private  boolean isinit=false;
    private static final   String TAG="RkSysTool";
    private  RkSysTool(){}
    public static  RkSysTool getInstance(){
        if(instance==null)
        {
            instance=new RkSysTool();
        }
        return instance;
    }
    public void initContext(Context context)
    {
        kaihuangManager = (KaihuangManager) context.getSystemService("kaihuang");
        if (kaihuangManager != null) {

            isinit=true;
        }
    }


    /**
     * 设置输出gpio 电平
     * @param path
     * @param islowLevel
     * @return
     */
    public int setGpioLevel(String path, boolean islowLevel)
    {
        if(kaihuangManager!=null)
            return kaihuangManager.setGpioOutput(path,islowLevel);
        return -1;
    }

    /**
     * 静默升级
     * @param appfilepath
     */
    public void installSlientApk(Context context,String appfilepath,boolean isluanc)
    {
//        if (kaihuangManager != null) {
//            kaihuangManager.installSlientApk(appfilepath);
//            LogUtils.e("下载地址 kaihuangManager.installSlientApk(appfilepath);");
//        }
        File appFile = new File(appfilepath);
        if (!appFile.exists()) {
            Log.i(TAG,"请确认 " +appfilepath+ "是否存在");
            return;
        }
        String authority = context.getPackageName() + ".fileprovider";
        Log.i(TAG,"packe:"+authority );
        Uri uri = FileProvider.getUriForFile(context, authority, appFile);
//        CTKManager.getInstance(InstallActivity.this).getApi().silenceInstall(InstallActivity.this, uri, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent installApkIntent = new Intent();
            installApkIntent.setAction(Intent.ACTION_VIEW);
            installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
            installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installApkIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //设置静默安装标识
            installApkIntent.putExtra("silence_install", true);
            //设置安装完成是否启动标识
            installApkIntent.putExtra("is_launch", isluanc);
            //设置后台中启动activity标识
            installApkIntent.putExtra("allowed_Background", true);
            if (context.getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
                context.startActivity(installApkIntent);
            }
            LogUtils.e(TAG,"下载地址 silence_install 8.0");
        }
    }

    /**
     * 静默安装,一般需要root权限或者是厂商自己的系统应用。
     */
    public static void silenceInstallApk(Context context, String apkPath, ComponentName componentName) {
        PackageInstaller.Session session = null;
        OutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            File file = new File(apkPath);
            String apkName = apkPath.substring(apkPath.lastIndexOf(File.separator) + 1, apkPath.lastIndexOf(".apk"));
            //Get PackageInstaller
            PackageInstaller packageInstaller = context.getPackageManager()
                    .getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller
                    .SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            //Create Session
            int sessionId = packageInstaller.createSession(params);
            //Open Session
            session = packageInstaller.openSession(sessionId);
            //Get the output stream to write apk to session
            outputStream = session.openWrite(apkName, 0, -1);
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int n;
            //Read apk file and write session
            while ((n = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, n);
            }
            //You need to close the flow after writing, otherwise the exception "files still open" will be thrown
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
            //Configure the intent initiated after the installation is completed, usually to open the activity
            Intent intent = new Intent();
            intent.setAction("APK_INSTALL_ACTION");
            if (componentName != null) {
                intent.setComponent(componentName);
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            IntentSender intentSender = pendingIntent.getIntentSender();
            //Submit to start installation
            session.commit(intentSender);
            LogUtils.e("--silenceInstallApk---success--");
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.e("--silenceInstallApk---error--" + e.getMessage());
        } finally {
            try {
                if (null != session) {
                    session.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                LogUtils.e("--silenceInstallApk---error--" + e.getMessage());
            }
        }

    }

    /**
     * 设置系统时间同步服务器
     * @param nameserver
     * @param index
     */
    public void setNptServer(String nameserver, int index)
    {
        if(kaihuangManager!=null)
            kaihuangManager.setNptServer(nameserver,index);
    }

    /**
     * h获取ccid
     * @return
     */
    public String getccid() {
        if(kaihuangManager!=null)
            return  kaihuangManager.getccid();
        return null;
    }

    /**
     * 设置状态栏显示
     * @param ishow 是否显示
     */
    public void setStatusBar(boolean ishow) {
        if(kaihuangManager!=null)
            kaihuangManager.setStatusBar(ishow);

    }

    /**
     * 设置导航栏显示
     * @param ishow
     */
    public void setNavitionBar(boolean ishow) {
        if(kaihuangManager!=null)
            kaihuangManager.setNavitionBar(ishow);

    }

    /**
     * 开启系统root 权限
     * @param isopenroot 是否显示
     */
    public void setRootPermission(boolean isopenroot) {
        if(kaihuangManager!=null)
            kaihuangManager.setRootPermission(isopenroot);

    }

    /**
     * 设置rtc时间
     * @param datestr yyyyMMDDHHmmss
     */
    public void setDateTime(String datestr) {
        if(kaihuangManager!=null)
            kaihuangManager.setDateTime(datestr);

    }

    /**
     * 设置app为桌面 开机启动,不进入桌面直接进入app
     * @param packagename
     */
    public void setHomeApp(String packagename) {
        if(kaihuangManager!=null)
            kaihuangManager.setHomeApp(packagename);
    }


    public void setBackLight0(Context context, String val) {
        FileOutputStream fileOutputStream=null;
        File file =new File("/sys/bus/platform/drivers/pwm-backlight/backlight/backlight/backlight/brightness");
        if(file.exists())
        {
            try {
                fileOutputStream=new FileOutputStream(file);

                fileOutputStream.write(val.getBytes());
                fileOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(fileOutputStream!=null)
                {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public void setBackLight1(Context context, String val) {
        FileOutputStream fileOutputStream=null;
        File file =new File("/sys/bus/platform/drivers/pwm-backlight/backlight5/backlight/backlight5/brightness");
        if(file.exists())
        {
            try {
                fileOutputStream=new FileOutputStream(file);

                fileOutputStream.write(val.getBytes());
                fileOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(fileOutputStream!=null)
                {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 获取全部开关机时间配置
     * @return
     */
    public List<PowerTime> getPowerTimes(){
        if(kaihuangManager!=null)
            return kaihuangManager.getPowerTimes();
        return null;
    }

    /**
     * 设置开机时间配置
     * @param ptime
     * @return
     */
    public boolean setPowerTime(PowerTime ptime) {
        if(kaihuangManager!=null)
            return kaihuangManager.setPowerTime(ptime);
        return  false;
    }

    /**
     * 单次设置当前开机时间
     * @param sec   距离当前时间 sec秒 后发出开机信号,小于10秒视为10秒
     * @return
     */
    public boolean setNextPowerOnTime(long sec) {
        if(kaihuangManager!=null)
            return kaihuangManager.setNextPowerOnTime(sec);
        return  false;
    }

    /**
     * 单次设置当前关机时间
     * @param sec  距离当前时间 sec秒 后发出关机信号 ,小于10秒视为10秒
     * @return
     */
    public boolean setNextPowerDownTime(long sec) {
        if(kaihuangManager!=null)
            return kaihuangManager.setNextPowerDownTime(sec);
        return  false;
    }
}
