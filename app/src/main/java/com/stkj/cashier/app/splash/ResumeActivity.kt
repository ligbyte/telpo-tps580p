package com.stkj.cashier.app.splash

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.king.base.baseurlmanager.BaseUrlManager
import com.king.base.baseurlmanager.bean.UrlInfo
import com.permissionx.guolindev.PermissionX
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseActivity
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.AppConfigModule
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.config.SPKey
import com.stkj.cashier.databinding.ResumeActivityBinding
import com.stkj.cashier.databinding.SplashActivityBinding
import com.stkj.cashier.util.camera.FacePassCameraType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mcv.facepass.FacePassException
import mcv.facepass.FacePassHandler
import mcv.facepass.auth.AuthApi.AuthApplyResponse
import mcv.facepass.auth.AuthApi.ErrorCodeConfig
import mcv.facepass.types.FacePassConfig
import mcv.facepass.types.FacePassModel
import mcv.facepass.types.FacePassPose
import org.greenrobot.eventbus.EventBus
import java.util.HashMap

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class ResumeActivity : BaseActivity<SplashViewModel, ResumeActivityBinding>() {


    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        binding.rootView.setOnClickListener {
            finish()
            EventBus.getDefault().post(MessageEventBean(MessageEventType.ScreenOffTimeout))
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.resume_activity
    }


    private fun startActivity() {
//        if (SPStaticUtils.getString(SPKey.KEY_TOKEN).isNullOrBlank()){
//            startActivity(LoginActivity::class.java);
//        }else{
        startMainActivity()
//        }

        finish()
    }


}