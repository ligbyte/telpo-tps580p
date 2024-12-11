package com.stkj.cashier.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.stkj.cashier.App;
import com.stkj.cashier.R;
import com.stkj.cashier.bean.MessageEventBean;
import com.stkj.cashier.config.MessageEventType;
//import com.common.api.system.SystemApiUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
public class UpdateService extends Service {
    public static final String TAG = "UpdateService";
    public static final String ACTION = "me.dave.UPDATE_APP";
    public static final String STATUS = "status";
    public static final String PROGRESS = "progress";
    public static boolean DEBUG = false;

    //下载大小通知频率
    public static final int UPDATE_NUMBER_SIZE = 1;
    public static final int DEFAULT_RES_ID = -1;

    public static final int UPDATE_PROGRESS_STATUS = 0;
    public static final int UPDATE_ERROR_STATUS = -1;
    public static final int UPDATE_SUCCESS_STATUS = 1;

    //params
    private static final String URL = "downloadUrl";
    private static final String ICO_RES_ID = "icoResId";
    private static final String ICO_SMALL_RES_ID = "icoSmallResId";
    private static final String UPDATE_PROGRESS = "updateProgress";
    private static final String STORE_DIR = "storeDir";
    private static final String DOWNLOAD_NOTIFICATION_FLAG = "downloadNotificationFlag";
    private static final String DOWNLOAD_SUCCESS_NOTIFICATION_FLAG = "downloadSuccessNotificationFlag";
    private static final String DOWNLOAD_ERROR_NOTIFICATION_FLAG = "downloadErrorNotificationFlag";
    private static final String IS_ON_BIND = "isOnBind";
    private static final String IS_SEND_BROADCAST = "isSendBroadcast";


    private String downloadUrl;
    private int icoResId;             //default app ico
    private int icoSmallResId;
    private int updateProgress;   //update notification progress when it add number
    private String storeDir;          //default sdcard/Android/package/update
    private int downloadNotificationFlag;
    private int downloadSuccessNotificationFlag;
    private int downloadErrorNotificationFlag;
    private boolean isSendBroadcast;

    private UpdateProgressListener updateProgressListener;
    private LocalBinder localBinder = new LocalBinder();

    /**
     * Class used for the client Binder.
     */
    public class LocalBinder extends Binder {
        /**
         * set update progress call back
         *
         * @param listener
         */
        public void setUpdateProgressListener(UpdateProgressListener listener) {
            UpdateService.this.setUpdateProgressListener(listener);
        }

        public UpdateService getService() {
            return UpdateService.this;
        }
    }


    private boolean startDownload;//开始下载
    private int lastProgressNumber;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private int notifyId;
    private String appName;
    private LocalBroadcastManager localBroadcastManager;
    private Intent localIntent;
    private DownloadApk downloadApkTask;

    /**
     * whether debug
     */
    public static void debug() {
        DEBUG = true;
    }

    private static Intent installIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(App.applicationContext, AppUtils.getAppPackageName() + ".fileprovider", new File(path));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            intent.putExtra("IMPLUS INSTALL", "SILENT INSTALL");//自动安装并在安装后自动执行
        } else {
            Uri uri = Uri.fromFile(new File(path));
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.putExtra("IMPLUS INSTALL", "SILENT INSTALL");//自动安装并在安装后自动执行
        }
        return intent;
    }

    private static Intent webLauncher(String downloadUrl) {
        Uri download = Uri.parse(downloadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, download);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private static String getSaveFileName(String downloadUrl) {
        if (downloadUrl == null || TextUtils.isEmpty(downloadUrl)) {
            return "noName.apk";
        }
        if (downloadUrl.lastIndexOf("?") > -1 && downloadUrl.lastIndexOf("/") < downloadUrl.lastIndexOf("?")) {
            return downloadUrl.substring(downloadUrl.lastIndexOf("/"), downloadUrl.lastIndexOf("?"));
        }
        return downloadUrl.substring(downloadUrl.lastIndexOf("/"));
    }

    private static File getDownloadDir(UpdateService service) {
        File downloadDir = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (service.storeDir != null) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                if (service.storeDir.contains(path)) {
                    downloadDir = new File(service.storeDir);
                } else {
                    downloadDir = new File(path, service.storeDir);
                }
            } else {
                downloadDir = new File(service.getExternalCacheDir(), "update");
            }
        } else {
            downloadDir = new File(service.getCacheDir(), "update");
        }
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        return downloadDir;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        appName = getApplicationName();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!startDownload && intent != null) {
            startDownload = true;
            downloadUrl = intent.getStringExtra(URL);
            icoResId = intent.getIntExtra(ICO_RES_ID, DEFAULT_RES_ID);
            icoSmallResId = intent.getIntExtra(ICO_SMALL_RES_ID, DEFAULT_RES_ID);
            storeDir = intent.getStringExtra(STORE_DIR);
            updateProgress = intent.getIntExtra(UPDATE_PROGRESS, UPDATE_NUMBER_SIZE);
            downloadNotificationFlag = intent.getIntExtra(DOWNLOAD_NOTIFICATION_FLAG, 0);
            downloadErrorNotificationFlag = intent.getIntExtra(DOWNLOAD_ERROR_NOTIFICATION_FLAG, 0);
            downloadSuccessNotificationFlag = intent.getIntExtra(DOWNLOAD_SUCCESS_NOTIFICATION_FLAG, 0);
            isSendBroadcast = intent.getBooleanExtra(IS_SEND_BROADCAST, false);
            boolean isOnBind = intent.getBooleanExtra(IS_ON_BIND, false);

            if (DEBUG) {
                Log.d(TAG, "downloadUrl: " + downloadUrl);
                Log.d(TAG, "icoResId: " + icoResId);
                Log.d(TAG, "icoSmallResId: " + icoSmallResId);
                Log.d(TAG, "storeDir: " + storeDir);
                Log.d(TAG, "updateProgress: " + updateProgress);
                Log.d(TAG, "downloadNotificationFlag: " + downloadNotificationFlag);
                Log.d(TAG, "downloadErrorNotificationFlag: " + downloadErrorNotificationFlag);
                Log.d(TAG, "downloadSuccessNotificationFlag: " + downloadSuccessNotificationFlag);
                Log.d(TAG, "isSendBroadcast: " + isSendBroadcast);
            }

            notifyId = startId;
            buildNotification();
            buildBroadcast();

            // 如果不是bindService模式，开启下载线程，否则在onBind()->onServiceConnected()中开启
            if (!isOnBind) {
                downloadApkTask = new DownloadApk(this);
                downloadApkTask.execute(downloadUrl);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void setUpdateProgressListener(UpdateProgressListener updateProgressListener) {
        this.updateProgressListener = updateProgressListener;
    }

    @Override
    public void onDestroy() {
        if (downloadApkTask != null) {
            downloadApkTask.cancel(true);
        }

        if (updateProgressListener != null) {
            updateProgressListener = null;
        }
        localIntent = null;
        builder = null;

        super.onDestroy();
    }

    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    private void buildBroadcast() {
        if (!isSendBroadcast) {
            return;
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localIntent = new Intent(ACTION);
    }

    private void sendLocalBroadcast(int status, int progress) {
        if (!isSendBroadcast || localIntent == null) {
            return;
        }
        localIntent.putExtra(STATUS, status);
        localIntent.putExtra(PROGRESS, progress);
        localBroadcastManager.sendBroadcast(localIntent);
    }

    private String channelId = "app_download_channel";
    private String channelName = "APP下载渠道";

    private void buildNotification() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);
            builder.setChannelId(channelId)
                    .setContentTitle(getString(R.string.update_app_model_prepare, appName))
                    .setSmallIcon(icoSmallResId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), icoResId));
        } else {
            builder.setContentTitle(getString(R.string.update_app_model_prepare, appName))
                    .setWhen(System.currentTimeMillis())
                    .setProgress(100, 1, false)
                    .setSmallIcon(icoSmallResId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), icoResId))
                    .setDefaults(downloadNotificationFlag);
        }
        manager.notify(notifyId, builder.build());
    }

    private void start() {
        builder.setContentTitle(appName);
        builder.setContentText(getString(R.string.update_app_model_start));
        manager.notify(notifyId, builder.build());
        sendLocalBroadcast(UPDATE_PROGRESS_STATUS, 1);
        if (updateProgressListener != null) {
            updateProgressListener.start();
        }
    }

    /**
     * @param progress download percent , max 100
     */
    private void update(int progress, int currentSize, int totalSize) {
        if (progress - lastProgressNumber > updateProgress) {
            lastProgressNumber = progress;
            builder.setProgress(100, progress, false);
            builder.setContentText(getString(R.string.update_app_model_progress, progress, "%"));
            manager.notify(notifyId, builder.build());
            sendLocalBroadcast(UPDATE_PROGRESS_STATUS, progress);
            if (updateProgressListener != null) {
                updateProgressListener.update(progress, currentSize, totalSize);
            }
        }
    }

    private void success(String path) {
        builder.setProgress(0, 0, false);
        builder.setContentText(getString(R.string.update_app_model_success));
        Intent i = installIntent(path);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        builder.setDefaults(downloadSuccessNotificationFlag);
        Notification n = builder.build();
        n.contentIntent = intent;
        manager.notify(notifyId, n);
        sendLocalBroadcast(UPDATE_SUCCESS_STATUS, 100);
        if (updateProgressListener != null) {
            updateProgressListener.success();
        }
        //静默安装指定路径下的apk
        LogUtils.e("下载地址" + path);
       /* YF_RK3399_API_Manager yfapi = new YF_RK3399_API_Manager(this);
        yfapi.yfslientinstallapk(path);


        AppUtils.installApp(path);*/

        // 静默安装APK
        RkSysTool.getInstance().installSlientApk(App.applicationContext, path,true);
//        RkSysTool.silenceInstallApk(App.applicationContext, path, null);
//        InstallUtils.install28(App.applicationContext, path, InstallResultReceiver.class);
//        new SystemApiUtil(this).installApp(path,"com.stkj.cashier");

        stopSelf();
    }

    private void error() {
        EventBus.getDefault().post(new MessageEventBean(MessageEventType.ProgressError));
        Intent i = webLauncher(downloadUrl);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentText(getString(R.string.update_app_model_error));
        builder.setContentIntent(intent);
        builder.setProgress(0, 0, false);
        builder.setDefaults(downloadErrorNotificationFlag);
        Notification n = builder.build();
        n.contentIntent = intent;
        manager.notify(notifyId, n);
        sendLocalBroadcast(UPDATE_ERROR_STATUS, -1);
        if (updateProgressListener != null) {
            updateProgressListener.error();
        }
        stopSelf();
    }

    private static class DownloadApk extends AsyncTask<String, Integer, String> {

        private WeakReference<UpdateService> updateServiceWeakReference;

        public DownloadApk(UpdateService service) {
            updateServiceWeakReference = new WeakReference<>(service);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UpdateService service = updateServiceWeakReference.get();
            if (service != null) {
                service.start();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            final String downloadUrl = params[0];

            final File file = new File(UpdateService.getDownloadDir(updateServiceWeakReference.get()),
                    UpdateService.getSaveFileName(downloadUrl));
            if (DEBUG) {
                Log.d(TAG, "download url is " + downloadUrl);
                Log.d(TAG, "download apk cache at " + file.getAbsolutePath());
            }
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            HttpURLConnection httpConnection = null;
            InputStream is = null;
            FileOutputStream fos = null;
            int updateTotalSize = 0;
            java.net.URL url;
            try {
                url = new URL(downloadUrl);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setConnectTimeout(20000);
                httpConnection.setReadTimeout(20000);

                if (DEBUG) {
                    Log.d(TAG, "download status code: " + httpConnection.getResponseCode());
                }

                if (httpConnection.getResponseCode() != 200) {
                    return null;
                }

                updateTotalSize = httpConnection.getContentLength();

                if (file.exists()) {
                    if (updateTotalSize == file.length()) {
                        // 下载完成
                        return file.getAbsolutePath();
                    } else {
                        file.delete();
                    }
                }
                file.createNewFile();
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(file, false);
                byte buffer[] = new byte[2048];

                int readSize = 0;
                int currentSize = 0;
                int progress =0;
                while ((readSize = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, readSize);
                    currentSize += readSize;
                    long bigCurrentSize = currentSize * 100L;
                    int i = (int) (bigCurrentSize / updateTotalSize);
                    publishProgress((int) (bigCurrentSize / updateTotalSize), currentSize, updateTotalSize);
                    if (DEBUG) {
                        Log.d(TAG, "doInBackground current progress is " + bigCurrentSize / updateTotalSize
                                + ",current currentSize is" + currentSize + ",current updateTotalSize is" + updateTotalSize
                                + ",current readSize is" + readSize);
                    }
                    LogUtils.e("0进度条"+ i +"/"+ progress);
                    if (i !=progress){
                        progress= i;
                        EventBus.getDefault().post(new MessageEventBean(MessageEventType.ProgressNumber, progress));
                        LogUtils.e("进度条"+ i +"/"+ progress);
                    }
                }
                // download success
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return file.getAbsolutePath();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (DEBUG) {
                Log.d(TAG, "current progress is " + values[0]);
            }
            UpdateService service = updateServiceWeakReference.get();
            if (service != null) {
                service.update(values[0], values[1], values[2]);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            UpdateService service = updateServiceWeakReference.get();
            if (service != null) {
                if (s != null) {
                    service.success(s);
                } else {
                    service.error();
                }
            }
        }
    }


    /**
     * a builder class helper use UpdateService
     */
    public static class Builder {

        private String downloadUrl;
        private int icoResId = DEFAULT_RES_ID;             //default app ico
        private int icoSmallResId = DEFAULT_RES_ID;
        private int updateProgress = UPDATE_NUMBER_SIZE;   //update notification progress when it add number
        private String storeDir;          //default sdcard/Android/package/update
        private int downloadNotificationFlag;
        private int downloadSuccessNotificationFlag;
        private int downloadErrorNotificationFlag;
        private boolean isSendBroadcast;

        private ServiceConnection serviceConnection;

        protected Builder(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public static Builder create(String downloadUrl) {
            if (downloadUrl == null) {
                throw new NullPointerException("downloadUrl == null");
            }
            return new Builder(downloadUrl);
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public int getIcoResId() {
            return icoResId;
        }

        public Builder setIcoResId(int icoResId) {
            this.icoResId = icoResId;
            return this;
        }

        public int getIcoSmallResId() {
            return icoSmallResId;
        }

        public Builder setIcoSmallResId(int icoSmallResId) {
            this.icoSmallResId = icoSmallResId;
            return this;
        }

        public int getUpdateProgress() {
            return updateProgress;
        }

        public Builder setUpdateProgress(int updateProgress) {
            if (updateProgress < 1) {
                throw new IllegalArgumentException("updateProgress < 1");
            }
            this.updateProgress = updateProgress;
            return this;
        }

        public String getStoreDir() {
            return storeDir;
        }

        public Builder setStoreDir(String storeDir) {
            this.storeDir = storeDir;
            return this;
        }

        public int getDownloadNotificationFlag() {
            return downloadNotificationFlag;
        }

        public Builder setDownloadNotificationFlag(int downloadNotificationFlag) {
            this.downloadNotificationFlag = downloadNotificationFlag;
            return this;
        }

        public int getDownloadSuccessNotificationFlag() {
            return downloadSuccessNotificationFlag;
        }

        public Builder setDownloadSuccessNotificationFlag(int downloadSuccessNotificationFlag) {
            this.downloadSuccessNotificationFlag = downloadSuccessNotificationFlag;
            return this;
        }

        public int getDownloadErrorNotificationFlag() {
            return downloadErrorNotificationFlag;
        }

        public Builder setDownloadErrorNotificationFlag(int downloadErrorNotificationFlag) {
            this.downloadErrorNotificationFlag = downloadErrorNotificationFlag;
            return this;
        }

        public boolean isSendBroadcast() {
            return isSendBroadcast;
        }

        public Builder setIsSendBroadcast(boolean isSendBroadcast) {
            this.isSendBroadcast = isSendBroadcast;
            return this;
        }

        public Builder build(Context context) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }
            Intent intent = new Intent();
            intent.setClass(context, UpdateService.class);
            intent.putExtra(URL, downloadUrl);

            if (icoResId == DEFAULT_RES_ID) {
                icoResId = getIcon(context);
            }

            if (icoSmallResId == DEFAULT_RES_ID) {
                icoSmallResId = icoResId;
            }
            intent.putExtra(ICO_RES_ID, icoResId);
            intent.putExtra(STORE_DIR, storeDir);
            intent.putExtra(ICO_SMALL_RES_ID, icoSmallResId);
            intent.putExtra(UPDATE_PROGRESS, updateProgress);
            intent.putExtra(DOWNLOAD_NOTIFICATION_FLAG, downloadNotificationFlag);
            intent.putExtra(DOWNLOAD_SUCCESS_NOTIFICATION_FLAG, downloadSuccessNotificationFlag);
            intent.putExtra(DOWNLOAD_ERROR_NOTIFICATION_FLAG, downloadErrorNotificationFlag);
            intent.putExtra(IS_SEND_BROADCAST, isSendBroadcast);
            intent.putExtra(IS_SEND_BROADCAST, isSendBroadcast);
            intent.putExtra(IS_ON_BIND, false);
            context.startService(intent);

            return this;
        }

        public Builder build(Context context, UpdateProgressListener listener) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }

            Intent intent = new Intent();
            intent.setClass(context, UpdateService.class);
            intent.putExtra(URL, downloadUrl);

            if (icoResId == DEFAULT_RES_ID) {
                icoResId = getIcon(context);
            }

            if (icoSmallResId == DEFAULT_RES_ID) {
                icoSmallResId = icoResId;
            }
            intent.putExtra(ICO_RES_ID, icoResId);
            intent.putExtra(STORE_DIR, storeDir);
            intent.putExtra(ICO_SMALL_RES_ID, icoSmallResId);
            intent.putExtra(UPDATE_PROGRESS, updateProgress);
            intent.putExtra(DOWNLOAD_NOTIFICATION_FLAG, downloadNotificationFlag);
            intent.putExtra(DOWNLOAD_SUCCESS_NOTIFICATION_FLAG, downloadSuccessNotificationFlag);
            intent.putExtra(DOWNLOAD_ERROR_NOTIFICATION_FLAG, downloadErrorNotificationFlag);
            intent.putExtra(IS_SEND_BROADCAST, isSendBroadcast);
            intent.putExtra(IS_ON_BIND, true);

            UpdateProgressListener updateProgressListener = new UpdateProgressListener() {
                @Override
                public void start() {
                    if (listener != null) {
                        listener.start();
                    }
                }

                @Override
                public void update(int progress, int currentSize, int totalSize) {
                    if (listener != null) {
                        listener.update(progress, currentSize, totalSize);
                    }
                }

                @Override
                public void success() {
                    try {
                        context.unbindService(serviceConnection);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "解绑失败" + e.getMessage());
                    }
                    if (listener != null) {
                        listener.success();
                    }
                }

                @Override
                public void error() {
                    try {
                        context.unbindService(serviceConnection);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "解绑失败" + e.getMessage());
                    }
                    if (listener != null) {
                        listener.error();
                    }
                }
            };

            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    LocalBinder binder = (LocalBinder) service;
                    binder.setUpdateProgressListener(updateProgressListener);
                    UpdateService updateService = binder.getService();
                    updateService.downloadApkTask = new DownloadApk(updateService);
                    updateService.downloadApkTask.execute(downloadUrl);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

            context.bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
            context.startService(intent);
            return this;
        }

        private int getIcon(Context context) {
            final PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appInfo = null;
            try {
                appInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (appInfo != null) {
                return appInfo.icon;
            }
            return 0;
        }
    }

}