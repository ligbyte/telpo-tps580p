package com.stkj.cashier.app.setting

import android.R.interpolator
import android.os.Bundle
import androidx.core.view.isVisible
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.databinding.DeviceConfigFragmentBinding
import com.stkj.cashier.databinding.MenuFragmentBinding
import com.stkj.cashier.databinding.PasswordFragmentBinding
import com.stkj.cashier.databinding.StatisticsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

import android.graphics.drawable.ColorDrawable

import android.view.LayoutInflater
import android.view.View
import android.view.Gravity

import android.R.interpolator.linear
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Notification
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.blankj.utilcode.util.SizeUtils.getMeasuredHeight


//import com.tencent.bugly.proguard.v
import android.provider.Settings.System.SCREEN_OFF_TIMEOUT
import android.widget.*
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.LogUtils.E
import com.google.gson.Gson
import com.king.app.dialog.AppDialog
import com.king.app.dialog.AppDialogConfig
import com.king.app.updater.AppUpdater
import com.king.app.updater.callback.UpdateCallback
import com.stkj.cashier.App
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.splash.SplashActivity
import com.stkj.cashier.bean.CheckAppVersionBean
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.util.UpdateService
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.Exception
import java.util.*


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class DeviceConfigFragment : BaseFragment<SettingViewModel, DeviceConfigFragmentBinding>(),
    View.OnClickListener {

//    var index: Int = -1

    companion object {
        fun newInstance(): DeviceConfigFragment {
            return DeviceConfigFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        company()
        binding.tvCheckUpdate.setOnClickListener(this)
        binding.tvSerialNumber.text = App.serialNumber
        var index = SPUtils.getInstance().getInt(Constants.SCREEN_OFF_TIMEOUT,0)
        when (index) {
            0 ->  binding.tvSelectScreenSaver.text = "永不"
            1 -> binding.tvSelectScreenSaver.text = "3分钟"
            2 -> binding.tvSelectScreenSaver.text = "5分钟"
            3 -> binding.tvSelectScreenSaver.text = "10分钟"
        }
        binding.tvVersion.text = AppUtils.getAppVersionName()
        binding.tvSelectScreenSaver.setOnClickListener { view ->
            //创建对象
            val popupWindow = PopupWindow(requireActivity())
            val inflate: View =
                LayoutInflater.from(requireActivity()).inflate(R.layout.layout_screen_saver, null)
            //设置view布局
            popupWindow.contentView = inflate
            popupWindow.width = binding.tvSelectScreenSaver.width
            //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
            popupWindow.isTouchable = true
            //设置背景透明
            popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
            //点击空白处的时候让PopupWindow消失
            popupWindow.isOutsideTouchable = true
            // true时，点击返回键先消失 PopupWindow
            // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
            // false时PopupWindow不处理返回键，默认是false
            popupWindow.isFocusable = false
            //设置dismiss事件

            popupWindow.setOnDismissListener(PopupWindow.OnDismissListener { })
            val showing: Boolean = popupWindow.isShowing
            if (!showing) {
                inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val measuredHeight: Int = inflate.measuredHeight
                //show，并且可以设置位置
                popupWindow.showAsDropDown(
                    binding.tvSelectScreenSaver,
                    0,
                    -(binding.tvSelectScreenSaver.height + measuredHeight + 24)
                )
            }
            var rgSelectScreenSaver = inflate.findViewById<RadioGroup>(R.id.rgSelectScreenSaver)
            var index = SPUtils.getInstance().getInt(Constants.SCREEN_OFF_TIMEOUT,0)
            when (index) {
                0 -> rgSelectScreenSaver.check(R.id.rgSelect0)
                1 -> rgSelectScreenSaver.check(R.id.rgSelect1)
                2 -> rgSelectScreenSaver.check(R.id.rgSelect2)
                3 -> rgSelectScreenSaver.check(R.id.rgSelect3)
            }
            rgSelectScreenSaver.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rgSelect0 -> {
                        binding.tvSelectScreenSaver.text = "永不"
//                        Settings.System.putInt(
//                            requireContext().contentResolver,
//                            SCREEN_OFF_TIMEOUT,
//                            Int.MAX_VALUE
//                        ) //永不休眠
                        SPUtils.getInstance().put(Constants.SCREEN_OFF_TIMEOUT,0)
                    }
                    R.id.rgSelect1 -> {
                        binding.tvSelectScreenSaver.text = "3分钟"
//                        Settings.System.putInt(
//                            requireContext().contentResolver,
//                            SCREEN_OFF_TIMEOUT,
//                            1000 * 60 * 3
//                        ) //3分钟
                        SPUtils.getInstance().put(Constants.SCREEN_OFF_TIMEOUT,1)
                    }
                    R.id.rgSelect2 -> {
                        binding.tvSelectScreenSaver.text = "5分钟"
//                        Settings.System.putInt(
//                            requireContext().contentResolver,
//                            SCREEN_OFF_TIMEOUT,
//                            1000 * 60 * 5
//                        ) //5分钟
                        SPUtils.getInstance().put(Constants.SCREEN_OFF_TIMEOUT,2)
                    }
                    R.id.rgSelect3 -> {
                        binding.tvSelectScreenSaver.text = "10分钟"
//                        Settings.System.putInt(
//                            requireContext().contentResolver,
//                            SCREEN_OFF_TIMEOUT,
//                            1000 * 60 * 10
//                        ) //10分钟
                        SPUtils.getInstance().put(Constants.SCREEN_OFF_TIMEOUT,3)
                    }
                }
                popupWindow.dismiss()
                EventBus.getDefault().post(MessageEventBean(MessageEventType.ScreenOffTimeout))
            }
        }
//        setScreenSaverEnable()
        viewModel.checkAppVersion.observe(this) {
            LogUtils.e("checkAppVersion", Gson().toJson(it))
            if (it.code == 10000) {
                if(it.data?.version?.toInt()!! > AppUtils.getAppVersionCode()) {
                    showUpdataDialog(it.data)
                }else{
                    showToast("当前已是最新版本")
                }
            } else {
                it.message?.let { it1 -> showToast(it1) }
            }
        }
        viewModel.company.observe(this) {
            LogUtils.e("company", Gson().toJson(it))
            binding.tvDeviceName.text = it.deviceName
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.device_config_fragment
    }

    /**
     * 默认无操作后15s屏幕进入休眠
     *
     * @param isChecked 常亮开关
     */
    private fun setScreenSaverEnable() {

        if (!Settings.System.canWrite(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:" + requireContext().packageName)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(intent, 1000)
        } else {
            var int = Settings.System.getInt(
                requireContext().contentResolver,
                SCREEN_OFF_TIMEOUT,
                0
            )
            LogUtils.e("SCREEN_OFF_TIMEOUT", "==$int")
//                PrfUtils.setScreenState(isChecked)

            Settings.System.putInt(
                requireContext().contentResolver,
                SCREEN_OFF_TIMEOUT,
                Int.MAX_VALUE
            ) //永不休眠

        }

    }
    private fun company() {
        var companyMap = hashMapOf<String, Any>()
        companyMap["mode"] = "company_setup"
        companyMap["machine_Number"] = App.serialNumber
        var companyMd5 = EncryptUtils.encryptMD5ToString16(App.serialNumber)
        companyMap["sign"] = companyMd5
        viewModel.companySetup(companyMap)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (!Settings.System.canWrite(requireContext())) {
                ToastUtils.showLong("请允许修改系统设置")
            } else {
                var int = Settings.System.getInt(
                    requireContext().contentResolver,
                    SCREEN_OFF_TIMEOUT,
                    0
                )
//                if (int <= 1000 * 60 * 3) {
//                    index = 1
//                    binding.tvSelectScreenSaver.text = "3分钟"
//                } else if (int <= 1000 * 60 * 5) {
//                    index = 2
//                    binding.tvSelectScreenSaver.text = "5分钟"
//                } else if (int <= 1000 * 60 * 10) {
//                    index = 3
//                    binding.tvSelectScreenSaver.text = "10分钟"
//                } else {
//                    index = 0
//                    binding.tvSelectScreenSaver.text = "永不"
//                }

            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tvCheckUpdate -> {
                    clickVersionUpdate()
                }
            }
        }

    }

    private fun clickVersionUpdate() {
        // TODO 处理点击“版本更新”逻辑

        var map = hashMapOf<String, Any>()
        map["mode"] = "CheckAppVersion"
        map["machine_Number"] = App.serialNumber
        //map["deviceType"] = "1"
        map["deviceType"] = getString(R.string.deviceType)//设备厂商+产品+设备型号
        map["version_No"] = AppUtils.getAppVersionCode()
       // var md5 = EncryptUtils.encryptMD5ToString16("1&"+App.serialNumber + "&" + AppUtils.getAppVersionCode())
        var md5 =EncryptUtils.encryptMD5ToString16(getString(R.string.deviceType) + "&" + App.serialNumber + "&" + AppUtils.getAppVersionCode())
        map["sign"] = md5
        viewModel.checkAppVersion(map)
    }

    private fun showUpdataDialog(data:CheckAppVersionBean?) {
        val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
        val dialog = builder.create()
        dialog.setCancelable(true)
        val view = View.inflate(requireContext(), R.layout.dialog_updata_version, null)
        val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        tvContent.text = data?.content
        if("1" == data?.versionForce){
            tvCancel.visibility = View.GONE
            dialog.setCancelable(false)
        }
        tvCancel.setOnClickListener { view1: View? -> dialog.cancel() }
        tvConfirm.setOnClickListener { view1: View? ->
            dialog.cancel()
            // 处理文件名
            // 处理文件名
            val path: String = makeDownloadPath()
            UpdateService.Builder.create(data?.url)
                .setStoreDir(path)
                .setIcoResId(R.mipmap.ic_main_logo)
                .setIcoSmallResId(R.mipmap.ic_main_logo)
                .setDownloadSuccessNotificationFlag(Notification.DEFAULT_ALL)
                .setDownloadErrorNotificationFlag(Notification.DEFAULT_ALL)
                .build(requireContext(), null)
        }
        dialog.show()
        dialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() *0.32).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Objects.requireNonNull(dialog.window)!!.setContentView(view)
    }
    private fun makeDownloadPath(): String {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/Download/APK"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        // 清理目录中历史apk
        if (file.listFiles() != null && file.listFiles().size > 0) {
            for (f in file.listFiles()) {
                f.delete()
            }
        }
        return path
    }
}