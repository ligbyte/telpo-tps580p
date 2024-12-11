package com.stkj.cashier.app.setting

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.stkj.cashier.util.util.ConvertUtils
import com.stkj.cashier.util.util.ScreenUtils
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.databinding.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import androidx.core.content.ContextCompat.getSystemService

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Color
import android.os.Process
import android.provider.Settings
import com.stkj.cashier.util.util.BarUtils
import com.stkj.cashier.util.util.LogUtils
//import com.common.api.system.SystemApiUtil
//import com.huayi.hgt.hyznjar.CustomAPI
import com.jakewharton.processphoenix.ProcessPhoenix
import com.stkj.cashier.App
import com.stkj.cashier.app.splash.SplashActivity
//import com.stkj.cashier.util.SystemApiUtil
//import com.telpo.tps550.api.util.LEDUtil


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class RestartFragment : BaseFragment<SettingViewModel, RestartFragmentBinding>(),
    View.OnClickListener {


    companion object {
        fun newInstance(): RestartFragment {
            return RestartFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        binding.tvAppClose.setOnClickListener(this)
        binding.tvStatusShow.setOnClickListener(this)
        binding.tvStatusCancel.setOnClickListener(this)
        binding.tvNavbarShow.setOnClickListener(this)
        binding.tvNavbarCancel.setOnClickListener(this)
        binding.tvDeviceClose.setOnClickListener(this)
        binding.tvRestart.setOnClickListener(this)

        binding.tvAppClose.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
            val dialog = builder.create()
            dialog.setCancelable(true)
            val view = View.inflate(requireContext(), R.layout.dialog_restart, null)
            val ivClose = view.findViewById<ImageView>(R.id.ivClose)
            val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)
            val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
            ivClose.setOnClickListener { view1: View? -> dialog.cancel() }
            tvCancel.setOnClickListener { view1: View? -> dialog.cancel() }
            tvConfirm.setOnClickListener { view1: View? ->
//                val intent = Intent(context, SplashActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                requireContext().startActivity(intent)
//                Process.killProcess(Process.myPid())
//                val intent = Intent(requireContext(), SplashActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                }
//                requireContext().startActivity(intent)
               // restartAppDelay(1)
                ProcessPhoenix.triggerRebirth(App.instance.applicationContext)
            }
            dialog.show()
            dialog.window!!.setLayout(
                (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            Objects.requireNonNull(dialog.window)!!.setContentView(view)
        }
    }

    fun restartAppDelay(second: Long) {
        LogUtils.e("重启应用restartAppDelay")
        //val context: Application = AppManager.INSTANCE.getApplication()
        val launchIntent: Intent =
            requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName())!!
        val restartIntent: PendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            launchIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val mgr: AlarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(
            AlarmManager.RTC,
            System.currentTimeMillis() + second * 1000,
            restartIntent
        ) // {second}秒钟后重启应用
        Process.killProcess(Process.myPid())
    }
    override fun getLayoutId(): Int {
        return R.layout.restart_fragment
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tvDeviceClose -> {
                    deviceClose()
                }
                R.id.tvRestart -> {
                    deviceReStart()

                }
                R.id.tvStatusShow -> {
//                    var value = Settings.System.getInt(
//                        requireContext().contentResolver,
//                        "switch_navigation_bar")
//                    ScreenUtils.setNonFullScreen(requireActivity())
//                    BarUtils.setNavBarVisibility(requireActivity(), true)
////                    CustomAPI(requireContext()).switchNavBar(value)
////                    CustomAPI(requireContext()).switchStatusBar(1)
//                    Settings.System.putInt(
//                        requireContext().contentResolver,
//                        "switch_navigation_bar",
//                        value
//                    )

//                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

//                    val yfapi = YF_RK3399_API_Manager(requireContext())
//                    yfapi.yfsetStatusBarDisplay(true);
//                    SystemApiUtil(requireContext()).showStatusBar()
                }
                R.id.tvStatusCancel -> {
//                    var value = Settings.System.getInt(
//                        requireContext().contentResolver,
//                        "switch_navigation_bar")
//                    ScreenUtils.setFullScreen(requireActivity())
//                    BarUtils.setNavBarVisibility(requireActivity(), false)
////                    CustomAPI(requireContext()).switchNavBar(value)
////                    CustomAPI(requireContext()).switchStatusBar(0)
//                    Settings.System.putInt(
//                        requireContext().contentResolver,
//                        "switch_navigation_bar",
//                        value
//                    )

//                    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    val yfapi = YF_RK3399_API_Manager(requireContext())
//                    yfapi.yfsetStatusBarDisplay(false);

//                    SystemApiUtil(requireContext()).hideStatusBar()
                }
                R.id.tvNavbarShow -> {
                    //BarUtils.setNavBarVisibility(requireActivity(), true)
                    // com.lztek.toolkit.Lztek.create(requireContext()).hideNavigationBar()
                    //导航栏
                    //val yfapi = YF_RK3399_API_Manager(requireContext())
                   // yfapi.yfsetNavigationBarVisibility(true)

//                    SystemApiUtil(requireContext()).showNavigationBar()
                }
                R.id.tvNavbarCancel -> {
                    //BarUtils.setNavBarVisibility(requireActivity(), false)

                    // Lztek.create(requireContext()).showNavigationBar()
//                    val yfapi = YF_RK3399_API_Manager(requireContext())
//                    yfapi.yfsetNavigationBarVisibility(false)

//                    SystemApiUtil(requireContext()).hideNavigationBar()
                }
            }
        }
    }

    private fun deviceClose() {
        val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
        val dialog = builder.create()
        dialog.setCancelable(true)
        val view = View.inflate(requireContext(), R.layout.dialog_restart, null)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        val tvContentTip = view.findViewById<TextView>(R.id.tvContentTip)
        ivClose.setOnClickListener { view1: View? -> dialog.cancel() }
        tvCancel.setOnClickListener { view1: View? -> dialog.cancel() }
        tvContentTip.text = "确定要关闭当前设备吗"
        tvConfirm.setOnClickListener { view1: View? ->
//             YNHAPI.getInstance().shutdown()

            //3399
            //Lztek.create(requireContext()).hardShutdown()
//            val yfapi = YF_RK3399_API_Manager(requireContext())
//            yfapi.yfShutDown()

            //天波
//            SystemApiUtil(requireContext()).shutdown()
        }
        dialog.show()
        dialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Objects.requireNonNull(dialog.window)!!.setContentView(view)
    }

    private fun deviceReStart() {
        val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
        val dialog = builder.create()
        dialog.setCancelable(true)
        val view = View.inflate(requireContext(), R.layout.dialog_restart, null)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        val tvContentTip = view.findViewById<TextView>(R.id.tvContentTip)
        ivClose.setOnClickListener { view1: View? -> dialog.cancel() }
        tvCancel.setOnClickListener { view1: View? -> dialog.cancel() }
        tvContentTip.text = "确定要重启当前设备吗"
        tvConfirm.setOnClickListener { view1: View? ->
//            CustomAPI(requireContext()).reboot( "reboot")
//            dialog.dismiss()
            //YNHAPI.getInstance().reboot()  //卖宝乐 3568

            // 卖宝乐 3399
            // Lztek.create(requireContext()).softReboot()
//            val yfapi = YF_RK3399_API_Manager(requireContext())
//            yfapi.yfReboot()
            //天波
//            SystemApiUtil(requireContext()).rebootDevice()
        }
        dialog.show()
        dialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Objects.requireNonNull(dialog.window)!!.setContentView(view)
    }

}