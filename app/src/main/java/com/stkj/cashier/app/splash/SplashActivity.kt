package com.stkj.cashier.app.splash

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.*
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseActivity
import com.stkj.cashier.app.main.MainActivity
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.SplashActivityBinding
import com.stkj.cashier.util.FileCertUtil
import com.stkj.cashier.util.camera.FacePassCameraType
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
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
import java.util.concurrent.TimeUnit


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>() {

    private val CERT_PATH = "Download/CBG_Android_Face_Reco---36500-Formal-two-stage.cert"
    private val CODE_PATH = "Download/CBG_Android_Face_Reco---36500-Formal--1-active.txt"

    companion object {
        private const val CERT_PATH_STATIC =
            "Download/CBG_Android_Face_Reco---36500-Formal-two-stage.cert"
        private const val CODE_PATH_STATIC =
            "Download/CBG_Android_Face_Reco---36500-Formal--1-active.txt"

        @JvmStatic
        fun initFaceHandlerStatic() {
            object : Thread() {
                override fun run() {
                    while (true) {
                        while (FacePassHandler.isAvailable()) {
                            Log.i("FacePassHandler", "start to build FacePassHandler")
                            val config: FacePassConfig
                            try {
                                /* 填入所需要的配置 */
                                config = FacePassConfig()
                                config.rgbIrLivenessModel =
                                    FacePassModel.initModel(App.applicationContext.assets, App.applicationContext.getString(R.string.mcv_liveness_A))
                                config.LivenessModel = FacePassModel.initModel(
                                    App.applicationContext.assets,
                                    App.applicationContext.getString(R.string.mcv_livenessrgb_A)
                                )
                                config.rgbIrLivenessModel =
                                    FacePassModel.initModel(App.applicationContext.assets, App.applicationContext.getString(R.string.mcv_liveness_A))
                                config.searchModel =
                                    FacePassModel.initModel(App.applicationContext.assets, App.applicationContext.getString(R.string.mcv_feature_Ari))
                                config.poseBlurModel =
                                    FacePassModel.initModel(App.applicationContext.assets, App.applicationContext.getString(R.string.mcv_poseblur_A))
                                config.postFilterModel = FacePassModel.initModel(
                                    App.applicationContext.assets,
                                    App.applicationContext.getString(R.string.mcv_postfilter_A)
                                )
                                config.rcAttributeModel = FacePassModel.initModel(
                                    App.applicationContext.assets,
                                    App.applicationContext.getString(R.string.mcv_rc_attribute_A)
                                )
                                config.detectModel = FacePassModel.initModel(
                                    App.applicationContext.assets,
                                    App.applicationContext.getString(R.string.mcv_rk3568_det_A_det)
                                )
                                config.occlusionFilterModel =
                                    FacePassModel.initModel(App.applicationContext.assets, App.applicationContext.getString(R.string.mcv_occlusion_B))
                                /* 送识别阈值参数 */
                                config.searchThreshold = 75f
                                config.livenessThreshold = 80f //单目推荐80
                                config.faceMinThreshold = SPUtils.getInstance().getInt(Constants.FACE_MIN_THRESHOLD, 100)
                                config.poseThreshold = FacePassPose(20f, 20f, 20f)
                                config.blurThreshold = 0.8f
                                config.lowBrightnessThreshold = 30f
                                config.highBrightnessThreshold = 210f
                                config.brightnessSTDThreshold = 80f
                                config.edgefacecompThreshold = 0.9f
                                //双目识别
                                if (App.cameraType == FacePassCameraType.FACEPASS_DUALCAM) {
                                    config.LivenessEnabled = false
                                    config.rgbIrLivenessEnabled = true // 启用双目活体功能(默认CPU)
                                } else {
                                    config.LivenessEnabled = true
                                    config.rgbIrLivenessEnabled = false
                                }
                                config.rcAttributeEnabled = true
                                config.occlusionFilterEnabled = true

                                /* 其他设置 */
                                config.maxFaceEnabled = false
                                config.retryCount = 10
                                config.fileRootPath =
                                    App.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                        ?.getAbsolutePath()

                                //                          getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

                                /* 创建SDK实例 */
                                App.mFacePassHandler = FacePassHandler()
                                val code = FacePassHandler.initHandle(config)

                                /*设置双目图片配准参数, 偏移量系数需由客户根据自己的设备测试得到.*/
//                            App.mFacePassHandler!!.setIRConfig(1.0, 0.0, 1.0, 0.0, 0.3)

                                /* 入库阈值参数 */
                                val addFaceConfig: FacePassConfig = App.mFacePassHandler!!.addFaceConfig
                                addFaceConfig.poseThreshold.pitch = 35f
                                addFaceConfig.poseThreshold.roll = 35f
                                addFaceConfig.poseThreshold.yaw = 35f
                                addFaceConfig.blurThreshold = 0.7f
                                addFaceConfig.lowBrightnessThreshold = 70f
                                addFaceConfig.highBrightnessThreshold = 220f
                                addFaceConfig.brightnessSTDThreshold = 63.25f
                                addFaceConfig.faceMinThreshold = 100
                                addFaceConfig.edgefacecompThreshold = 0.9f
                                App.mFacePassHandler!!.setAddFaceConfig(addFaceConfig)
                                createGroupStatic()
                            } catch (e: Throwable) {
                                e.printStackTrace()
                                Log.i("FacePassHandler", "FacePassHandler is null")
                                return
                            }
                            return
                        }
                        try {
                            /* 如果SDK初始化未完成则需等待 */
                            sleep(500)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }.start()
        }

        private fun createGroupStatic() {
            if (App.mFacePassHandler == null) {
                return
            }
            try {
                val localGroups = App.mFacePassHandler?.localGroups
//            isLocalGroupExist = false
                if (localGroups == null || localGroups.isEmpty()) {
                    App.mFacePassHandler?.createLocalGroup(Constants.GROUP_NAME)
                }

            } catch (e: FacePassException) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun initFacePassSDKStatic() {
            val mContext: Context = Utils.getApp().applicationContext
            FacePassHandler.initSDK(mContext)
            // 金雅拓授权接口
            var auth_status: Boolean = FacePassHandler.authCheck()
            if (!auth_status) {
                singleCertification(mContext)
                auth_status = FacePassHandler.authCheck()
            }
            if (!auth_status) {
                Log.d("mcvsafe", "Authentication result : failed.")
                // 授权不成功，根据业务需求处理
                // ...
                EventBus.getDefault().post(MessageEventBean(MessageEventType.InitFaceSDKFail))

                return
            }
        }

        private fun singleCertification(mContext: Context) {
            var cert = FileCertUtil.readExternal(CERT_PATH_STATIC).trim();
            var code = FileCertUtil.readExternal(CODE_PATH_STATIC).trim();
//        val cert =
//            "\"{\"\"serial\"\":\"\"m01165fc49cc8de407a352bc749c410f7b090\"\",\"\"key\"\":\"\"12e166dbeba1fbd5bb46e74c01f96d2f75dd69552451709a5186b51ebae8839e18f4d720b511ae83bf60d2bfa04d7e786f0a6d62b6c15e3486d5ccf73dfcd1d52555dbd31a50aa6db4cb086f6d393d2c75fb144c3510d8cda7f856ec1b429480fb5c5e4350f452abd64c0a1ffdfbeac171d34c96319e997e26df31ee3b580efedfd97103088cfabf42bfdf1479e6b5ffc973f41a49cbe9112db73d77c42b4e4dfe2c6a0d5ee1af3b9a8e0d593c3f27f48b0ada8497f1665abd5051519e2fe074\"\"}\""
            if (TextUtils.isEmpty(cert)) {
                LogUtils.e("singleCertification", "cert is null")
                ToastUtils.showLong("认证文件不存在")
                return
            }
            val resp = arrayOf(AuthApplyResponse())
            FacePassHandler.authDevice(Utils.getApp().applicationContext, cert, code) { result ->
                resp[0] = result
                if (resp[0].errorCode == ErrorCodeConfig.AUTH_SUCCESS) {
                    LogUtils.e("authDevice", "Apply update: OK")

                } else {
                    ToastUtils.showLong("人脸认证失败")

                    LogUtils.e(
                        "authDevice",
                        "Apply update: error. error code is: " + resp[0].errorCode + " error message: " + resp[0].errorMessage
                    )
                }
            }
        }
    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.e("重启"+isTaskRoot+"/"+Gson().toJson(intent))
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            LogUtils.e("重启finish")
            finish()
            return
        }
        super.onCreate(savedInstanceState)
        LogUtils.e("重启"+isTaskRoot+"/"+Gson().toJson(intent))
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        // 检查Activity是否存在
        App.mSplashActivity=this
        //识别距离
        SPUtils.getInstance().put(Constants.FACE_MIN_THRESHOLD, 135)
        //baseurl
//        SPUtils.getInstance().put(Constants.FACE_ADDRESS, "http://101.43.252.67:9003")
//        SPUtils.getInstance().put(Constants.FACE_ADDRESS, "http://101.42.54.44:9003")
        super.onCreate(savedInstanceState)
    }
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

       /* if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            LogUtils.e("重启finish")
            finish()
            return
        }*/
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        // 判断当前activity是不是所在任务栈的根
        val intent: Intent? = intent
        if (intent != null) {
            val action: String = intent.getAction().toString()
            //1.避免从桌面启动程序后，会重新实例化入口类的activity , 判断当前activity是不是所在任务栈的根
            LogUtils.e("重启"+isTaskRoot+"/"+Gson().toJson(intent))
            if (!isTaskRoot) {
                LogUtils.e("重启"+ Intent.ACTION_MAIN.equals(action)+"/"+intent.hasCategory(Intent.CATEGORY_LAUNCHER))
                LogUtils.e("重启"+Gson().toJson(intent.getCategories()))
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    LogUtils.e("重启finish")
                    finish()
                    return
                }
                finish()
                return
            }
            //2.经过路由跳转的，判断当前应用是否已经初始化过，首页是否存在并且未销毁
            if (Intent.ACTION_VIEW.equals(action)) {
                val isActivityRunning = App.mMainActivity?.let { isActivityRunning(it, MainActivity::class.java.name) }
                LogUtils.e("重启isActivityRunning"+isActivityRunning)
                if (isActivityRunning == true) {
                    LogUtils.e("重启经过路由跳finish")
                    finish()
                    return
                }
                /*val homeActivity: Activity = AppManager.INSTANCE.getMainActivity()
                if (!ActivityUtils.isActivityFinished(homeActivity)) {
                    finish()
                    return
                }*/
            }
        }

        val map: MutableMap<String, Any> = HashMap()
        var deviceId = App.serialNumber
        map["mode"] = "GetIntervalCardType"
        map["machine_Number"] = deviceId
        var md5 = EncryptUtils.encryptMD5ToString16(deviceId)
        map["sign"] = md5
//        viewModel.getIntervalCardType(map);
        viewModel.intervalCardType.observe(this) {
            LogUtils.e("intervalCardType", Gson().toJson(it))
//            SPStaticUtils.put(SPKey.KEY_CONFIG,it.getAiStatus())
            App.intervalCardType = it
        }
//        requestConsumptionType()
        startAnimation(viewDataBinding.rootView)

    }

    override fun getLayoutId(): Int {
        return R.layout.splash_activity
    }

    private fun requestConsumptionType() {
        var deviceId = App.serialNumber
        var timeMap = hashMapOf<String, Any>()
        timeMap["mode"] = "Current_Time_Info"
        timeMap["machine_Number"] = deviceId
        var timeMd5 = EncryptUtils.encryptMD5ToString16(deviceId)
        timeMap["sign"] = timeMd5
        viewModel.currentTimeInfo(timeMap)
        viewModel.currentTimeInfo.observe(this) {
            LogUtils.e("currentTimeInfo", Gson().toJson(it))
            App.currentTimeInfo = it
        }
    }

    private fun startAnimation(view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.splash_anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                if (App.mFacePassHandler != null) {
                    startActivity()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        view.startAnimation(anim)
        Observable.timer(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aLong ->
                requestPermission()
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startActivity() {
//        if (SPStaticUtils.getString(SPKey.KEY_TOKEN).isNullOrBlank()){
//            startActivity(LoginActivity::class.java);
//        }else{
        startMainActivity()
//        }

        finish()
    }

    private fun initFaceHandler() {
        object : Thread() {
            override fun run() {
                while (true && !isFinishing) {
                    while (FacePassHandler.isAvailable()) {
                        Log.i("FacePassHandler", "start to build FacePassHandler")
                        val config: FacePassConfig
                        try {
                            /* 填入所需要的配置 */
                            config = FacePassConfig()
                            config.rgbIrLivenessModel =
                                FacePassModel.initModel(assets, getString(R.string.mcv_liveness_A))
                            config.LivenessModel = FacePassModel.initModel(
                                assets,
                                getString(R.string.mcv_livenessrgb_A)
                            )
                            config.rgbIrLivenessModel =
                                FacePassModel.initModel(assets, getString(R.string.mcv_liveness_A))
                            config.searchModel =
                                FacePassModel.initModel(assets, getString(R.string.mcv_feature_Ari))
                            config.poseBlurModel =
                                FacePassModel.initModel(assets, getString(R.string.mcv_poseblur_A))
                            config.postFilterModel = FacePassModel.initModel(
                                assets,
                                getString(R.string.mcv_postfilter_A)
                            )
                            config.rcAttributeModel = FacePassModel.initModel(
                                assets,
                                getString(R.string.mcv_rc_attribute_A)
                            )
                            config.detectModel = FacePassModel.initModel(
                                assets,
                                getString(R.string.mcv_rk3568_det_A_det)
                            )
                            config.occlusionFilterModel =
                                FacePassModel.initModel(assets, getString(R.string.mcv_occlusion_B))
                            /* 送识别阈值参数 */
                            config.searchThreshold = 75f
                            config.livenessThreshold = 80f //单目推荐80
                            config.faceMinThreshold = SPUtils.getInstance().getInt(Constants.FACE_MIN_THRESHOLD, 100)
                            config.poseThreshold = FacePassPose(20f, 20f, 20f)
                            config.blurThreshold = 0.8f
                            config.lowBrightnessThreshold = 30f
                            config.highBrightnessThreshold = 210f
                            config.brightnessSTDThreshold = 80f
                            config.edgefacecompThreshold = 0.9f
                            //双目识别
                            if (App.cameraType == FacePassCameraType.FACEPASS_DUALCAM) {
                                config.LivenessEnabled = false
                                config.rgbIrLivenessEnabled = true // 启用双目活体功能(默认CPU)
                            } else {
                                config.LivenessEnabled = true
                                config.rgbIrLivenessEnabled = false
                            }
                            config.rcAttributeEnabled = true
                            config.occlusionFilterEnabled = true

                            /* 其他设置 */
                            config.maxFaceEnabled = false
                            config.retryCount = 10
                            config.fileRootPath =
                                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                    ?.getAbsolutePath()

                            //                          getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

                            /* 创建SDK实例 */
                            App.mFacePassHandler = FacePassHandler()
                            val code = FacePassHandler.initHandle(config)

                            /*设置双目图片配准参数, 偏移量系数需由客户根据自己的设备测试得到.*/
//                            App.mFacePassHandler!!.setIRConfig(1.0, 0.0, 1.0, 0.0, 0.3)

                            /* 入库阈值参数 */
                            val addFaceConfig: FacePassConfig = App.mFacePassHandler!!.addFaceConfig
                            addFaceConfig.poseThreshold.pitch = 35f
                            addFaceConfig.poseThreshold.roll = 35f
                            addFaceConfig.poseThreshold.yaw = 35f
                            addFaceConfig.blurThreshold = 0.7f
                            addFaceConfig.lowBrightnessThreshold = 70f
                            addFaceConfig.highBrightnessThreshold = 220f
                            addFaceConfig.brightnessSTDThreshold = 63.25f
                            addFaceConfig.faceMinThreshold = 100
                            addFaceConfig.edgefacecompThreshold = 0.9f
                            App.mFacePassHandler!!.setAddFaceConfig(addFaceConfig)
                            createGroup()
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            Log.i("FacePassHandler", "FacePassHandler is null")
                            return
                        }
                        return
                    }
                    try {
                        /* 如果SDK初始化未完成则需等待 */
                        sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    private fun createGroup() {
        if (App.mFacePassHandler == null) {
            return
        }
        try {
            val localGroups = App.mFacePassHandler?.localGroups
//            isLocalGroupExist = false
            if (localGroups == null || localGroups.isEmpty()) {
                App.mFacePassHandler?.createLocalGroup(Constants.GROUP_NAME)
            }
            LogUtils.e("localGroups",Gson().toJson(localGroups))
            MainScope().launch {
                startActivity()
            }
        } catch (e: FacePassException) {
            e.printStackTrace()
        }
    }

    private fun requestPermission() {

        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                    if (App.mFacePassHandler == null) {
                        initFacePassSDK()
                        initFaceHandler()
                    }
                } else {
                    Toast.makeText(this, "您拒绝了权限", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initFacePassSDK() {
        val mContext: Context = context.applicationContext
        //FacePassHandler.initSDK(mContext)
        // 金雅拓授权接口
        var auth_status: Boolean = FacePassHandler.authCheck()
        if (!auth_status) {
            singleCertification(mContext)
            auth_status = FacePassHandler.authCheck()
        }
        if (!auth_status) {
            Log.d("mcvsafe", "Authentication result : failed.")
            // 授权不成功，根据业务需求处理
            // ...
            startActivity()
            return
        }
    }

    private fun singleCertification(mContext: Context) {
        var cert = FileCertUtil.readExternal(CERT_PATH).trim();
        var code = FileCertUtil.readExternal(CODE_PATH).trim();
//        val cert =
//            "\"{\"\"serial\"\":\"\"m01165fc49cc8de407a352bc749c410f7b090\"\",\"\"key\"\":\"\"12e166dbeba1fbd5bb46e74c01f96d2f75dd69552451709a5186b51ebae8839e18f4d720b511ae83bf60d2bfa04d7e786f0a6d62b6c15e3486d5ccf73dfcd1d52555dbd31a50aa6db4cb086f6d393d2c75fb144c3510d8cda7f856ec1b429480fb5c5e4350f452abd64c0a1ffdfbeac171d34c96319e997e26df31ee3b580efedfd97103088cfabf42bfdf1479e6b5ffc973f41a49cbe9112db73d77c42b4e4dfe2c6a0d5ee1af3b9a8e0d593c3f27f48b0ada8497f1665abd5051519e2fe074\"\"}\""
        if (TextUtils.isEmpty(cert)) {
            LogUtils.e("singleCertification", "cert is null")
            ToastUtils.showLong("认证文件不存在")
            return
        }
        val resp = arrayOf(AuthApplyResponse())
        FacePassHandler.authDevice(context.applicationContext, cert, code) { result ->
            resp[0] = result
            if (resp[0].errorCode == ErrorCodeConfig.AUTH_SUCCESS) {
                LogUtils.e("authDevice", "Apply update: OK")
            } else {
                ToastUtils.showLong("人脸认证失败")
                LogUtils.e(
                    "authDevice",
                    "Apply update: error. error code is: " + resp[0].errorCode + " error message: " + resp[0].errorMessage
                )
            }
        }
    }

    private fun isActivityRunning(context: Context, activityName: String): Boolean {
        val am: ActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (am != null) {
            // 获取当前运行的任务信息
            for (taskInfo in am.getRunningTasks(Int.MAX_VALUE)) {
                if (taskInfo.topActivity?.getClassName().equals(activityName)) {
                    // 如果找到对应的Activity，且它是顶部活动，则认为它存在且未销毁
                    return true
                }
            }
        }
        return false
    }
    override fun onDestroy() {
        super.onDestroy()
    }

}