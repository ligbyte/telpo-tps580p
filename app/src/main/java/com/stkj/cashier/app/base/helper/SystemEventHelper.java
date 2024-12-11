package com.stkj.cashier.app.base.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.stkj.cashier.App;

import java.util.HashSet;
import java.util.Set;

/**
 * 系统事件广播监听
 */
public enum SystemEventHelper {
    INSTANCE;

    private final Set<OnSystemEventListener> dateListenerSet = new HashSet<>();

    //网络连接状态监听
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {

        private int mChangedNetType = -99;
        private boolean mChangedNetConnect;

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            boolean isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            int networkTypeInt = getTypeFromNetwork(network);
            Log.d("SystemEventHelper", "NetworkCallback onCapabilitiesChanged isConnected: " + isConnected + " networkTypeInt: " + networkTypeInt);
            if (mChangedNetType == networkTypeInt && isConnected == mChangedNetConnect) {
                //网络未发生变化,不做处理;可能是信号发生变化,信号已经单独监听,无需处理
                Log.d("SystemEventHelper", "NetworkCallback onCapabilitiesChanged network not change");
                return;
            }
            mChangedNetType = networkTypeInt;
            mChangedNetConnect = isConnected;
            MainThreadHolder.post(new Runnable() {
                @Override
                public void run() {
                    for (OnSystemEventListener listener : CollectUtils.copySet(dateListenerSet)) {
                        listener.onNetworkChanged(networkTypeInt, isConnected);
                    }
                    Log.d("SystemEventHelper", "NetworkCallback onCapabilitiesChanged");
                }
            });
        }
    };

    /**
     * 系统事件监听（时间、电池、wifi强度）
     */
    private final BroadcastReceiver mSysEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (action == null || action.isEmpty()) {
                return;
            }

            switch (action) {
                case Intent.ACTION_TIME_TICK:
                    String dateFormatTick = DateFormat.format("yyyy MM-dd HH:mm", System.currentTimeMillis()).toString();
                    for (OnSystemEventListener listener : CollectUtils.copySet(dateListenerSet)) {
                        listener.onDateTick(dateFormatTick);
                    }
                    Log.d("SystemEventHelper", "ACTION_TIME_TICK");
                    break;
                case Intent.ACTION_TIME_CHANGED:
                    String dateFormatChange = DateFormat.format("yyyy MM-dd HH:mm", System.currentTimeMillis()).toString();
                    for (OnSystemEventListener listener : CollectUtils.copySet(dateListenerSet)) {
                        listener.onDateChange(dateFormatChange);
                    }
                    Log.d("SystemEventHelper", "ACTION_TIME_CHANGED");
                    break;
                case Intent.ACTION_BATTERY_CHANGED:
                    int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);///电池剩余电量

                    int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);///获取电池满电量数值

                    float batteryPercent = 100;
                    if (batteryLevel != 0 && batteryScale != 0) {
                        batteryPercent = batteryLevel * 100 / (float) batteryScale;
                    }
                    int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);///获取电池状态

                    boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING;

                    Log.d("SystemEventHelper", "batteryPercent: " + batteryPercent);

                    for (OnSystemEventListener listener : CollectUtils.copySet(dateListenerSet)) {
                        listener.onBatteryChange(batteryPercent, isCharging);
                    }
                    Log.d("SystemEventHelper", "ACTION_BATTERY_CHANGED");
                    break;
                case WifiManager.RSSI_CHANGED_ACTION:
                    NetworkCapabilities networkCapabilities = getActiveNetworkCapabilities();
                    if (networkCapabilities != null) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            boolean isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                            int rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -1);
                            //这函数可以计算出信号的等级
                            int level = WifiManager.calculateSignalLevel(rssi, 4);
                            for (OnSystemEventListener listener : CollectUtils.copySet(dateListenerSet)) {
                                listener.onNetworkRssiChange(WIFI_NET_TYPE, isConnected, level);
                            }
                        }
                    }
                    Log.d("SystemEventHelper", "RSSI_CHANGED_ACTION");
                    break;
            }
        }
    };

    /**
     * 移动网络信号
     */
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int level = signalStrength.getLevel();
            MainThreadHolder.post(new Runnable() {
                @Override
                public void run() {
                    NetworkCapabilities networkCapabilities = getActiveNetworkCapabilities();
                    if (networkCapabilities != null) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            boolean isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                            for (OnSystemEventListener listener : CollectUtils.copySet(dateListenerSet)) {
                                listener.onNetworkRssiChange(MOBILE_NET_TYPE, isConnected, level);
                            }
                        }
                    }
                    Log.d("SystemEventHelper", "PhoneStateListener");
                }
            });
        }
    };

    /**
     * 初始化
     */
    public void init() {
        IntentFilter intentFilter = new IntentFilter();
        //系统时间变化
        //system every 1 min send broadcast
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        //system hand change time send broadcast
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        //电池电量变化
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //wifi信号强度监听
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        App.applicationContext.registerReceiver(mSysEventReceiver, intentFilter);
        //移动网络信号监听
        TelephonyManager mTelephonyManager = (TelephonyManager) App.applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //网络连接状况监听
        ConnectivityManager connectivityManager = App.applicationContext.getSystemService(ConnectivityManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        }
    }

    public void addSystemEventListener(OnSystemEventListener systemDateListener) {
        dateListenerSet.add(systemDateListener);
        initSystemEventListener(systemDateListener);
    }

    /**
     * 第一次初始化
     */
    private void initSystemEventListener(OnSystemEventListener listener) {
        if (listener != null) {
            //获取时间
            String dateFormatTick = DateFormat.format("yyyy MM-dd HH:mm", System.currentTimeMillis()).toString();
            listener.onDateTick(dateFormatTick);
            //获取当前链接状态
            NetworkCapabilities networkCapabilities = getActiveNetworkCapabilities();
            if (networkCapabilities != null) {
                boolean isConnect = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    listener.onNetworkChanged(ETHERNET_NET_TYPE, isConnect);
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    listener.onNetworkChanged(WIFI_NET_TYPE, isConnect);
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    listener.onNetworkChanged(MOBILE_NET_TYPE, isConnect);
                } else {
                    listener.onNetworkChanged(0, isConnect);
                }
            } else {
                listener.onNetworkChanged(0, false);
            }
            //获取电池电量/充电状态
            BatteryManager batteryManager = (BatteryManager) App.applicationContext.getSystemService(Context.BATTERY_SERVICE);
            int batteryPercent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);///当前电量百分比
            int batteryStatus = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);///充电状态
            boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING;
            listener.onBatteryChange(batteryPercent, isCharging);
        }
    }

    public void removeSystemEventListener(OnSystemEventListener systemDateListener) {
        dateListenerSet.remove(systemDateListener);
    }

    public void clear() {
        dateListenerSet.clear();
        App.applicationContext.unregisterReceiver(mSysEventReceiver);
    }

    public interface OnSystemEventListener {

        default void onDateTick(String formatDate) {
        }

        default void onDateChange(String formatDate) {
        }

        default void onNetworkChanged(int netType, boolean isConnect) {

        }

        /**
         * 信号强度(0-4)
         */
        default void onNetworkRssiChange(int netType, boolean isConnect, int level) {

        }

        default void onBatteryChange(float batteryPercent, boolean isCharging) {

        }
    }

    public static final int UNKNOWN_NET_TYPE = 0;
    public static final int ETHERNET_NET_TYPE = 1;
    public static final int WIFI_NET_TYPE = 2;
    public static final int MOBILE_NET_TYPE = 3;

    /**
     * 获取网络类型
     *
     * @return int (0：未知 1.网线 2.wifi 3.移动)
     */
    public int getTypeFromNetwork(Network network) {
        if (network == null) {
            return -1;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) App.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        if (networkCapabilities == null) {
            return -1;
        } else {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return ETHERNET_NET_TYPE;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return WIFI_NET_TYPE;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return MOBILE_NET_TYPE;
            }
        }
        return UNKNOWN_NET_TYPE;
    }

    /**
     * 获取当前正在连接的网络
     */
    private NetworkCapabilities getActiveNetworkCapabilities() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) App.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            return connectivityManager.getNetworkCapabilities(activeNetwork);
        }
        return null;
    }

    /**
     * 获取wifi网络信号强度(0-4)
     * 0 <-88
     * 1 [-88, -77）
     * 2 [-77, -66）
     * 3 [-66, -55）
     * 4 >= -55
     */
    public static int getWifiNetworkRSSILevel(int level) {
        WifiManager wifiManager = (WifiManager) App.applicationContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接速度
            // int speed = info.getLinkSpeed();
            // // 链接速度单位
            // String units = WifiInfo.LINK_SPEED_UNITS;
            // // Wifi源名称
            // String ssid = info.getSSID();
            return WifiManager.calculateSignalLevel(info.getRssi(), level);

        }
        return 0;
    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard() {
        TelephonyManager telMgr = (TelephonyManager) App.applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false; // 未知状态
                break;
        }
        return result;
    }

}
