package com.stkj.cashier

import android.app.Application
import android.content.Context
import android.os.Environment
import android.speech.tts.TextToSpeech

import androidx.multidex.MultiDex
import com.stkj.cashier.util.util.CrashHandler
import com.king.base.baseurlmanager.BaseUrlManager
import com.king.base.baseurlmanager.bean.UrlInfo
import com.king.kvcache.KVCache
import com.stkj.cashier.app.Constants
import com.stkj.cashier.bean.User
import com.stkj.cashier.component.ComponentAppManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.stkj.cashier.app.main.MainActivity
import com.stkj.cashier.app.splash.SplashActivity
import com.stkj.cashier.bean.CurrentTimeInfoBean
import com.stkj.cashier.bean.IntervalCardTypeBean
import com.stkj.cashier.BuildConfig
import com.stkj.cashier.app.base.helper.SystemEventHelper
import com.stkj.cashier.glide.GlideAppHelper
import com.stkj.cashier.util.camera.FacePassCameraType
import dagger.hilt.android.HiltAndroidApp
//import dagger.hilt.android.qualifiers.ApplicationContext
import es.dmoral.toasty.Toasty
import mcv.facepass.FacePassHandler
import timber.log.Timber
import java.io.File
import java.util.Locale

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltAndroidApp
class App : Application() {

    val componentAppManager by lazy { ComponentAppManager() }
    var isVip = false
    var isAI = false
    companion object instance {
        var serialNumber: String = ""
        var BASE_URL: String = ""
        lateinit var TTS: TextToSpeech//tts语速
//        var volumeSpeech: Float = 1f//tts语速
        lateinit var applicationContext: Application
        var intervalCardType: MutableList<IntervalCardTypeBean> = mutableListOf()
         var currentTimeInfo: CurrentTimeInfoBean? = null
        var mFacePassHandler: FacePassHandler? = null
        var cameraType = FacePassCameraType.FACEPASS_SINGLECAM

        var mMainActivity: MainActivity? = null
        var mSplashActivity: SplashActivity? = null
        var mShowConsumeStat: Boolean = false
    }
    init {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorPrimary, R.color.white)
            MaterialHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(context)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
//        Beta.installTinker()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.envType == 0) {
            BASE_URL = Constants.BASE_OFFICIAL_URL
        } else {
            BASE_URL = Constants.BASE_TEST_URL
        }
//        BASE_URL = SPUtils.getInstance().getString(Constants.FACE_ADDRESS,"")
//        LogUtils.e("reStartApp",App.BASE_URL)
        initTTS()
        initLogger()
        KVCache.initialize(this)
//        Bugly.init(this, Constants.BUGLY_APP_ID, BuildConfig.DEBUG)

        Toasty.Config.getInstance().allowQueue(false).apply()
        instance.applicationContext = this
//        NeverCrash.init { t, e ->
//            CrashReport.postCatchedException(e)
//        }

        // 设置日志配置
//		LogUtils.Config config = LogUtils.getConfig();
//		config.setLog2FileSwitch(true);
//		config.setGlobalTag("Logger");
//		config.setSaveDays(2);
//		LogUtils.d("AppApplication 初始化完成");
        CrashHandler.getInstance(applicationContext).setCrashLogDir(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .absolutePath + File.separator + "Crash"
        )
        if(Constants.isDomain){// 提供动态切换环境
            if(BaseUrlManager.getInstance().count == 0){
                BaseUrlManager.getInstance().urlInfo = UrlInfo(BASE_URL)
            }
        }
        componentAppManager.initComponentApp(this)

        //天波
        //SDKUtil.getInstance(this).initSDK()
        GlideAppHelper.init(this)

        //初始化系统事件
        SystemEventHelper.INSTANCE.init()
    }

    private fun initTTS() {
        App.TTS = TextToSpeech(this, null)
        //设置语言
        App.TTS.setLanguage(Locale.CHINA)
        //设置音调
        App.TTS.setPitch(1.0f)
    }

    private fun initLogger(){
        //初始化打印日志
        var formatStrategy = PrettyFormatStrategy.newBuilder()
            .methodOffset(5)
            .tag(Constants.TAG)
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                if (BuildConfig.DEBUG) {
                    Logger.log(priority, tag, message, t)
                }
            }
        })

    }

    /**
     * 登录
     */
    fun login(token: String, user: User){
        // 缓存token
        KVCache.put(Constants.KEY_TOKEN, token)
        // TODO 是否需要缓存 user需根据需求决定
    }

    /**
     * 登出
     */
    fun logout(){
        // 移除 token
        KVCache.remove(Constants.KEY_TOKEN)
    }

}