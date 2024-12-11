package com.stkj.cashier.app.base

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.ViewDataBinding
import com.stkj.cashier.util.util.LogUtils
import com.stkj.cashier.util.util.SPUtils
import com.king.base.util.StringUtils
import com.king.frame.mvvmframe.base.BaseFragment
import com.king.frame.mvvmframe.base.BaseModel
import com.king.frame.mvvmframe.base.BaseViewModel
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.home.HomeActivity
import com.stkj.cashier.bean.MessageEventBean
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
abstract class BaseFragment<VM : BaseViewModel<out BaseModel>,VDB : ViewDataBinding> : BaseFragment<VM,VDB>(){

    fun getApp() = requireActivity().application as App

    override fun initData(savedInstanceState: Bundle?) {
        registerMessageEvent {
            showToast(it)
        }

    }


    //-----------------------------------

    fun showToast(@StringRes resId: Int){
        Toasty.normal(requireContext(),resId).show()
    }

    fun showToast(text: CharSequence){
        Toasty.normal(getApp().applicationContext,text).show()
    }

    //-----------------------------------

    fun checkInput(tv: TextView): Boolean {
        return !TextUtils.isEmpty(tv.text)
    }

    fun checkInput(tv: TextView,msgId: Int): Boolean {
        if (TextUtils.isEmpty(tv.text)) {
            if (msgId != 0) {
                showToast(msgId)
            }
            return false
        }
        return true
    }

    fun checkInput(tv: TextView, msg: CharSequence? = null): Boolean {
        if (TextUtils.isEmpty(tv.text)) {
            if (StringUtils.isNotBlank(msg)) {
                showToast(msg!!)
            }
            return false
        }
        return true
    }
    //-----------------------------------

    fun startActivity(clazz: Class<*>,username: String? = null){
        var intent = newIntent(clazz)
        intent.putExtra(Constants.KEY_USERNAME,username)
        startActivity(intent)
    }

//    fun startLoginActivity(username: String? = null,isCode: Boolean = false,isAlphaAnim: Boolean = false,isClearTask: Boolean = false) {
//        val intent = Intent(context, if (isCode) CodeLoginActivity::class.java else LoginActivity::class.java)
//        intent.putExtra(Constants.KEY_USERNAME, username)
//        intent.putExtra(Constants.KEY_CLEAR_TASK, isClearTask)
//        if (isClearTask) {
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        if(isAlphaAnim){
//            val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(requireContext(), R.anim.alpha_in_anim, R.anim.app_dialog_scale_out)
//            startActivity(intent, optionsCompat.toBundle())
//        }else{
//            startActivity(intent)
//        }
//    }

    fun startHomeActivity(){
        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(requireContext(), R.anim.alpha_in_anim, R.anim.alpha_out_anim)
        startActivity(intent, optionsCompat.toBundle())
    }


    fun startWebActivity(url: String,title: String? = null){
        var intent = Intent(context, WebActivity::class.java)
        title?.let {
            intent.putExtra(Constants.KEY_TITLE,it)
        }
        intent.putExtra(Constants.KEY_URL,url)
        startActivity(intent)
    }

    //-----------------------------------

    override fun onStart() {
        super.onStart()
        // 设置事件总线监听
        // 设置事件总线监听
        LogUtils.e("EventBus_onStart"+this)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
            LogUtils.e("EventBus_onStart注册"+this)
        }else{
            //EventBus.getDefault().unregister(this)
            //EventBus.getDefault().register(this)
            LogUtils.e("EventBus_onStart已注册"+this)
        }
    }

    override fun onStop() {
        LogUtils.e("EventBus_onStop"+this)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            LogUtils.e("EventBus_onStop unregister"+this)
        }
        super.onStop()

    }
    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        LogUtils.e("onDestroy"+this)
    }
    //接收事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    open fun onEventReceiveMsg(message:MessageEventBean ){
//        Log.e("EventBus_Subscriber", "onReceiveMsg_MAIN: " + message.toString());
    }

    fun ttsSpeak(value:String){
        App.TTS.setSpeechRate(1f)
        App.TTS.speak(
            value,
            TextToSpeech.QUEUE_FLUSH, null
        )
    }
    interface OnKeyEventListener {
        fun onKeyEvent(event: KeyEvent?): Boolean
    }

    /**
     * 创建一个随机订单号
     */
    fun createOrderNumber(): String {
        val randoms = Random().nextInt(9000) + 1000
        val machineNumber: String = App.serialNumber
        var machineEndTag = ""
        if (!TextUtils.isEmpty(machineNumber) && machineNumber.length >= 5) {
            machineEndTag = machineNumber.substring(machineNumber.length - 5)
        }
        val orderNumber = "ZGXF" + machineEndTag + System.currentTimeMillis() / 1000 + randoms
        //LogHelper.print("--createOrderNumber--orderNumber: $orderNumber")
        return orderNumber
    }
}