package com.stkj.cashier.app.setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
//import com.huayi.hgt.hyznjar.CustomAPI
import com.king.android.ktx.fragment.argument
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ForwardToSettingsCallback
import com.permissionx.guolindev.request.ForwardScope
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.MenuFragmentBinding
import com.stkj.cashier.databinding.PasswordFragmentBinding
import com.stkj.cashier.databinding.StatisticsFragmentBinding
import com.stkj.cashier.databinding.WifiFragmentBinding
//import com.tencent.bugly.Bugly.applicationContext
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class WifiFragment : BaseFragment<SettingViewModel, WifiFragmentBinding>() {


    private val REQUEST_LOCATION_PERMISSION: Int = 1003

    companion object {
        fun newInstance(): WifiFragment {
            return WifiFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        requestPermission()
        binding.tvWifiSetting.setOnClickListener {

            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));//进入无线网络配置界面
        }
        binding.tvWifiSetting2.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));//进入无线网络配置界面
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    private fun requestPermission() {

        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "请同意该权限才能继续使用该功能！",
                    "同意",
                    "取消"
                )
            }

            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    //call()
                    getWifiInfo()
                } else {
                    Toast.makeText(requireContext(), "您拒绝了位置权限", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getWifiInfo() {
        // 获取 WifiManager 实例
        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

// 获取当前连接的 WiFi 信息
        val wifiInfo = wifiManager.connectionInfo
        if (wifiInfo != null) {
            if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                // 当前已经连接到 WiFi 网络
                val ssid = wifiInfo.ssid // 获取 WiFi 名称
                val bssid = wifiInfo.bssid // 获取 WiFi MAC 地址
                binding.tvWifiName.text = ssid.replace("\"", "").replace("\"", "")
                binding.llWifiConnect.visibility = View.VISIBLE
                binding.llNoWifiConnect.visibility = View.GONE
                // 其他信息
            } else {
                // 当前正在尝试连接 WiFi 网络
            }
        } else {
            // 当前未连接到 WiFi 网络
            binding.llWifiConnect.visibility = View.GONE
            binding.llNoWifiConnect.visibility = View.VISIBLE
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.wifi_fragment
    }

    override fun onResume() {
        super.onResume()
//        CustomAPI(requireContext()).switchNavBar(0)
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        when (message.type) {
            MessageEventType.WifiMessage -> {
                var wifiState = message.obj as Int
                if (wifiState == 1) {
                    // 当前未连接到 WiFi 网络
                    binding.llWifiConnect.visibility = View.GONE
                    binding.llNoWifiConnect.visibility = View.VISIBLE
                } else {
                    getWifiInfo()
                }
//                when (wifiState) {
//                    WifiManager.WIFI_STATE_DISABLING -> {
//                    }
//                    WifiManager.WIFI_STATE_DISABLED -> {
//
//                    }
//                    WifiManager.WIFI_STATE_ENABLING -> {
//
//                    }
//                    WifiManager.WIFI_STATE_ENABLED -> {
//
//                    }
//                    WifiManager.WIFI_STATE_UNKNOWN -> {
////                    wifi_image.setImageResource(R.drawable.wifi_sel)
////                    wifi_image.setImageLevel(level)
//                    }
//
//                }
            }
        }
    }
}