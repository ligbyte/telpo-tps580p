package com.stkj.cashier.app.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.os.Process.killProcess
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.app.splash.SplashActivity
import com.stkj.cashier.databinding.MenuFragmentBinding
import com.stkj.cashier.databinding.PasswordFragmentBinding
import com.stkj.cashier.databinding.ServerAddressFragmentBinding
import com.stkj.cashier.databinding.StatisticsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import com.stkj.cashier.app.main.MainActivity

import androidx.core.content.ContextCompat.getSystemService
import com.permissionx.guolindev.PermissionX
import android.R.attr.process
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.blankj.utilcode.util.*
import com.jakewharton.processphoenix.ProcessPhoenix
import com.stkj.cashier.App
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class ServerAddressFragment : BaseFragment<SettingViewModel, ServerAddressFragmentBinding>(),
    TextView.OnEditorActionListener {

    companion object {
        fun newInstance(): ServerAddressFragment {
            return ServerAddressFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        var address = SPUtils.getInstance().getString(Constants.FACE_ADDRESS, "")

        binding.etAddress.setText(address)
        binding.etAddress.setOnEditorActionListener(this)
        binding.etAddress.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                /*隐藏软键盘*/
                var imm: InputMethodManager =
                    v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    imm.hideSoftInputFromWindow(v.applicationWindowToken, 0);
                }
                reStartApp()
            }
        }
        var headBeat = SPUtils.getInstance().getInt(Constants.FACE_HEAD_BEAT, 30)
        binding.etHeartBeat.setText(headBeat.toString())
        binding.etHeartBeat.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                EventBus.getDefault().post(MessageEventBean(MessageEventType.HeadBeat))
                /*隐藏软键盘*/
                var imm: InputMethodManager =
                    v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    imm.hideSoftInputFromWindow(v.applicationWindowToken, 0);
                }
            }
        }
        binding.etHeartBeat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etHeartBeat.text.isNotEmpty()) {
                    SPUtils.getInstance()
                        .put(Constants.FACE_HEAD_BEAT, binding.etHeartBeat.text.toString().toInt())
                }
            }

        })
    }

    override fun getLayoutId(): Int {
        return R.layout.server_address_fragment
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        /*判断是否是“NEXT”键*/
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            /*隐藏软键盘*/
            var imm: InputMethodManager =
                v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(v.applicationWindowToken, 0);
            }
            reStartApp()
            return true;
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    fun reStartApp() {
        var address = SPUtils.getInstance().getString(Constants.FACE_ADDRESS, "")
        if (address != binding.etAddress.text.toString().trim()) {
            SPUtils.getInstance()
                .put(Constants.FACE_ADDRESS, binding.etAddress.text.toString().trim())
            LogUtils.e("reStartApp", binding.etAddress.text.toString().trim())
            val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
            val dialog = builder.create()
            dialog.setCancelable(true)
            val view = View.inflate(requireContext(), R.layout.dialog_restart_tip, null)

            val tvRestartTip = view.findViewById<TextView>(R.id.tvRestartTip)

            dialog.show()
            dialog.window!!.setLayout(
                (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            Objects.requireNonNull(dialog.window)!!.setContentView(view)
            var time = 3;
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { aLong ->
                    tvRestartTip.text = "$time s后重新启动软件"
                    if (time == 0) {
                        //这里做重新启动app
                      /*  val intent = Intent(context, SplashActivity::class.java)
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                        requireContext().startActivity(intent)
//                        killProcess(Process.myPid())
                        requireActivity().finish()
                        Runtime.getRuntime().exit(0)*/
                        EventBus.getDefault().unregister(this)
                       // restartAppDelay(1)
                        ProcessPhoenix.triggerRebirth(App.instance.applicationContext)
                        //ProcessPhoenix.triggerRebirth(MyApplication.getInstance())
                    }
                    time--
                }
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

}
