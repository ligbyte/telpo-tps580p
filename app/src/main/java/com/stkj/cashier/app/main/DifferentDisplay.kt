package com.stkj.cashier.app.main

import android.annotation.SuppressLint
import android.app.Presentation
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Pair
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stkj.cashier.util.util.*
import com.stkj.cashier.util.util.ThreadUtils.runOnUiThread
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.stkj.cashier.App
import com.stkj.cashier.App.instance.mFacePassHandler
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.adapter.ConsumeRefundListAdapter
import com.stkj.cashier.app.base.helper.CommonTipsHelper
import com.stkj.cashier.app.base.helper.SystemEventHelper
import com.stkj.cashier.app.weigh.commontips.CommonTipsView
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.bean.db.CompanyMemberdbEntity
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.glide.GlideApp
import com.stkj.cashier.greendao.biz.CompanyMemberBiz
import com.stkj.cashier.util.SettingVar
import com.stkj.cashier.util.camera.*
import com.stkj.cashier.util.rxjava.DefaultDisposeObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import mcv.facepass.FacePassException
import mcv.facepass.types.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import com.stkj.cashier.bean.ConsumeRefundListBean as ConsumeRefundListBean1

class DifferentDisplay : Presentation, CameraManager.CameraListener, View.OnClickListener {

    private var companyMember: CompanyMemberdbEntity? = null
    private lateinit var tvTime: TextView
    private lateinit var tvCompanyName: TextView
    private lateinit var tvFaceTips: TextView
    private lateinit var tvFaceTips2: TextView
    private lateinit var ivWifiState: ImageView
    private lateinit var pbBattery:ProgressBar
    private lateinit var ivBatteryBg:ImageView
//    private lateinit var tvWifiState: TextView

    private lateinit var tvRefundName: TextView
    private lateinit var tvNumber: TextView
    private lateinit var ivHeader2: ImageView
    private lateinit var rvRefund: RecyclerView
    private lateinit var tvRefundName2: TextView
    private lateinit var tvWindow: TextView

    private lateinit var llRefundList: LinearLayout
    private lateinit var llDefault: LinearLayout
    private lateinit var rlRefundConfirm: RelativeLayout

    private lateinit var flCameraAvatar:FrameLayout
    private lateinit var llBalance:LinearLayout
    private lateinit var tvBalance:TextView
    private lateinit var tvPayMoney:TextView

    private lateinit var llPayError:LinearLayout
    private lateinit var tvPayError:TextView

    private var layoutManager: LinearLayoutManager? = null
    private var mAdapter: ConsumeRefundListAdapter? = null
    private lateinit var cameraPreview: CameraPreview
    private var ivCameraOverLayer:ImageView? = null
    private var ivSuccessHeader:ImageView? = null

    /* 相机实例 */
    private var cameraManager: CameraManager? = null

    private val cameraRotation = SettingVar.cameraPreviewRotation

    private var realAmount = "0.0"

    companion object {
        var isStartFaceScan:AtomicBoolean = AtomicBoolean(false)
    }

    var mRecognizeDataQueue: ArrayBlockingQueue<RecognizeData> = ArrayBlockingQueue(5)
    var mFeedFrameQueue: ArrayBlockingQueue<CameraPreviewData> = ArrayBlockingQueue(1)

    /*recognize thread*/
    var mRecognizeThread: RecognizeThread? = null
    var mFeedFrameThread: FeedFrameThread? = null

    constructor(outerContext: Context, display: Display) : super(outerContext, display)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        LogUtils.e("副屏onCreate")
        setCancelable(false)
        setContentView(R.layout.layout_different_display_2)

        ivCameraOverLayer = findViewById(R.id.iv_camera_over_layer);
        ivSuccessHeader = findViewById(R.id.ivSuccessHeader);
        cameraPreview = findViewById(R.id.cameraPreview)
        cameraPreview.setAutoFitSurfaceListener { width, height ->
            if (height > 0 && width > height) {
                val offset = -(width - height) / 2
                cameraPreview.translationX = offset.toFloat()
                LogUtils.e("setAutoFitSurfaceListener---offset: $offset")
            }
        }
//        cameraPreview.setAspectRatio(600, 600)
        tvTime = findViewById<TextView>(R.id.tvTime)
        tvCompanyName = findViewById<TextView>(R.id.tvCompanyName)
        tvFaceTips = findViewById<TextView>(R.id.tvFaceTips)
        tvFaceTips2 = findViewById<TextView>(R.id.tvFaceTips2)
        ivWifiState = findViewById<ImageView>(R.id.ivWifiState)
        pbBattery = findViewById<ProgressBar>(R.id.pb_battery)
        ivBatteryBg = findViewById<ImageView>(R.id.iv_battery_bg)
//        tvWifiState = findViewById<TextView>(R.id.tvWifiState)

        llRefundList = findViewById<LinearLayout>(R.id.llRefundList)
        llDefault = findViewById<LinearLayout>(R.id.llDefault)
        rlRefundConfirm = findViewById<RelativeLayout>(R.id.rlRefundConfirm)

        flCameraAvatar = findViewById(R.id.fl_camera_avatar);
        llBalance = findViewById(R.id.ll_balance);
        tvBalance = findViewById(R.id.tvBalance);
        tvPayMoney = findViewById(R.id.tv_pay_money);

        llPayError = findViewById(R.id.ll_pay_error);
        tvPayError = findViewById(R.id.tv_pay_error);

        tvNumber = findViewById<TextView>(R.id.tvNumber)
        tvWindow = findViewById<TextView>(R.id.tvWindow)
        tvRefundName = findViewById<TextView>(R.id.tvRefundName)
        tvRefundName2 = findViewById<TextView>(R.id.tvRefundName2)
        ivHeader2 = findViewById<ImageView>(R.id.ivHeader2)
        rvRefund = findViewById<RecyclerView>(R.id.rvRefund)

        val ctvConsumer = findViewById<CommonTipsView>(R.id.ctv_consumer);
        CommonTipsHelper.INSTANCE.setConsumerTipsView(ctvConsumer)

        val switchFacePassPay = SPUtils.getInstance().getBoolean(Constants.SWITCH_FACE_PASS_PAY, false)

        if (switchFacePassPay) {
            openAndInitCamera()
            startFacePassDetect()
            ivCameraOverLayer?.visibility = View.GONE
        } else {
            closeAndReleaseCamera()
            stopFacePassDetect()
            ivCameraOverLayer?.visibility = View.VISIBLE
        }

        //系统事件监听
        SystemEventHelper.INSTANCE.addSystemEventListener(systemEventListener)
        //event事件
        EventBus.getDefault().register(this)
    }

    /**
     * 打开并初始化摄像头
     */
    private fun openAndInitCamera() {
        try {
            if (cameraManager != null) {
                closeAndReleaseCamera()
            }
            cameraManager = CameraManager()
            cameraManager!!.setPreviewDisplay(cameraPreview)
            cameraManager!!.setListener(this)
            cameraManager!!.open(window?.windowManager, false, 640, 360)
            LogUtils.e("CameraManager 打开并初始化摄像头")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭并释放摄像头
     */
    private fun closeAndReleaseCamera() {
        try {
            if (cameraManager != null) {
                cameraManager!!.release()
                cameraManager!!.finalRelease()
            }
            cameraManager = null
            LogUtils.e("CameraManager 关闭并释放摄像头")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 开始人脸识别
     */
    private fun startFacePassDetect() {
        try {
            clearFacePassQueueCache()
            mRecognizeThread = RecognizeThread()
            mRecognizeThread!!.start()
            mFeedFrameThread = FeedFrameThread()
            mFeedFrameThread!!.start()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 停止人脸识别
     */
    private fun stopFacePassDetect() {
        try {
            mRecognizeThread?.isInterrupt = true
            mFeedFrameThread?.isInterrupt = true
            mRecognizeThread = null
            mFeedFrameThread = null
            clearFacePassQueueCache()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 清理人脸识别队列缓存
     */
    private fun clearFacePassQueueCache() {
        mFeedFrameQueue.clear()
        mRecognizeDataQueue.clear();
    }

    override fun show() {
        try {
            super.show()
        } catch (e: Throwable) {
            e.printStackTrace()
            LogUtils.e("副屏初始化失败")
        }
    }

    override fun getResources(): Resources? {
        LogUtils.e("副屏getResources")

        val res = super.getResources()
        LogUtils.e("副屏getResources" + res.configuration.fontScale)
        //非默认值
        val newConfig = Configuration()
        newConfig.setToDefaults() //设置默认
        res.updateConfiguration(newConfig, res.displayMetrics)
        LogUtils.e("副屏getResources-res")
        return res
    }

    override fun onStart() {
        super.onStart()
        LogUtils.e("副屏初始化onStart")
    }

    override fun onDisplayRemoved() {
        super.onDisplayRemoved()
        LogUtils.e("副屏初始化onDisplayRemoved")
    }

    override fun onDisplayChanged() {
        super.onDisplayChanged()
        LogUtils.e("副屏初始化onDisplayChanged")
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (App.mMainActivity!=null){
            return App.mMainActivity!!.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    inner class FeedFrameThread : Thread() {
        var isInterrupt = false
        override fun run() {

            while (!isInterrupt) {

                /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                var detectionResult: FacePassTrackResult? = null
                try {
                    if (App.cameraType == FacePassCameraType.FACEPASS_DUALCAM) {
                        var framePair: Pair<CameraPreviewData, CameraPreviewData> = try {
                            ComplexFrameHelper.takeComplexFrame()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            continue
                        }
                        val imageRGB = FacePassImage(
                            framePair.first.nv21Data,
                            framePair.first.width,
                            framePair.first.height,
                            cameraRotation,
                            FacePassImageType.NV21
                        )
                        val imageIR = FacePassImage(
                            framePair.second.nv21Data,
                            framePair.second.width,
                            framePair.second.height,
                            cameraRotation,
                            FacePassImageType.NV21
                        )
                        detectionResult = App.mFacePassHandler?.feedFrameRGBIR(imageRGB, imageIR)
                    } else {
                        var cameraPreviewData: CameraPreviewData? = null
                        try {
                            cameraPreviewData = mFeedFrameQueue.take()
                            val imageRGB = FacePassImage(
                                cameraPreviewData.nv21Data,
                                cameraPreviewData.width,
                                cameraPreviewData.height,
                                cameraRotation,
                                FacePassImageType.NV21
                            )
                            detectionResult = App.mFacePassHandler?.feedFrame(imageRGB)
                            //LogUtils.e("识别到人脸1 "+detectionResult?.message)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // LogUtils.e("识别到人脸2 "+e.message)
                            continue
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    LogUtils.d("识别到人脸3 " + e.message)
                }

                /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                if (detectionResult != null && detectionResult.message.isNotEmpty()) {
                    LogUtils.d("识别到人脸" + detectionResult.images.size)
                    /*所有检测到的人脸框的属性信息*/

                    /*送识别的人脸框的属性信息*/
                    val trackOpts = arrayOfNulls<FacePassTrackOptions>(detectionResult.images.size)
                    for (i in detectionResult.images.indices) {
                        if (detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.NO_RESPIRATOR) {
                            val searchThreshold = 75f
                            val livenessThreshold = 80f // -1.0f will not change the liveness threshold
                            trackOpts[i] = FacePassTrackOptions(
                                detectionResult.images[i].trackId,
                                searchThreshold,
                                livenessThreshold
                            )
                        } else {
                            trackOpts[i] =
                                FacePassTrackOptions(detectionResult.images[i].trackId, -1f, -1f)
                        }
                    }
                    val mRecData = RecognizeData(detectionResult.message, trackOpts)
                    mRecognizeDataQueue.offer(mRecData)
                }

            }
        }

        override fun interrupt() {
            isInterrupt = true
            super.interrupt()
        }
    }

    /**
     * 识别线程
     * */
    inner class RecognizeThread : Thread() {
        var isInterrupt = false
        override fun run() {
            while (!isInterrupt) {

                try {
                    Log.d("DEBUG_TAG", "RecognizeData >>>>1")
                    val recognizeData: RecognizeData = mRecognizeDataQueue.take()
                    var recognizeResult: Array<FacePassRecognitionResult>? = null
                    if (isStartFaceScan.get()) {
                        Log.d("DEBUG_TAG", "RecognizeData >>>>2")

                        recognizeResult = App.mFacePassHandler?.recognize(
                            Constants.GROUP_NAME,
                            recognizeData.message,
                            1,
                            recognizeData.trackOpt[0].trackId,
                            FacePassRecogMode.FP_REG_MODE_DEFAULT,
                            recognizeData.trackOpt[0].livenessThreshold,
                            recognizeData.trackOpt[0].searchThreshold
                        )
                        //                        FacePassRecognitionResult[][] recognizeResultArray = mFacePassHandler.recognize(group_name, recognizeData.message, 1, recognizeData.trackOpt);
//                        if (recognizeResultArray != null && recognizeResultArray.length > 0) {
//                            for (FacePassRecognitionResult[] recognizeResult : recognizeResultArray) {
//                                if (recognizeResult != null && recognizeResult.length > 0) {
                        Log.d("DEBUG_TAG", "RecognizeData >>>>" + recognizeResult?.size)
                        if (recognizeResult != null && recognizeResult.isNotEmpty()) {
                            for (result in recognizeResult) {
                                //判断人脸相似程度 >75表示成功 否侧失败处理
                                if (result.detail != null && result.detail.searchScore < 75) {
                                    runOnUiThread {
                                        handleFacePassFailRetryDelay()
                                    }
                                    break
                                }
                                val faceToken = String(result.faceToken, charset("ISO-8859-1"))
                                LogUtils.e(
                                    "recognizeResult识别出来的人脸",
                                    faceToken + "/" + result.recognitionState
                                )
                                var faceTokens: Array<ByteArray>? = null
                                if (App.mFacePassHandler != null) {
                                    faceTokens =
                                        App.mFacePassHandler!!.getLocalGroupInfo(Constants.GROUP_NAME)
                                }
                                val faceTokenList: MutableList<kotlin.String> =
                                    java.util.ArrayList()
                                if (faceTokens?.isNotEmpty() == true) {
                                    for (j in faceTokens?.indices!!) {
                                        if (faceTokens[j].isNotEmpty()) {
                                            faceTokenList.add(kotlin.text.String(faceTokens[j]))
                                        }
                                    }
                                }
                                LogUtils.e("底库的人脸", "数量" + faceTokenList.size)
                               // CompanyMemberBiz.getCompanyMember2()

                                if (faceToken.isNotEmpty() && FacePassRecognitionState.RECOGNITION_PASS == result.recognitionState) {
                                    EventBus.getDefault().post(
                                        MessageEventBean(
                                            MessageEventType.MainResume
                                        )
                                    )
                                    //金额模式
                                    isStartFaceScan.set(false)
                                    companyMember =
                                        CompanyMemberBiz.getCompanyMember(faceToken)
                                    if (companyMember != null) {
                                        clearFacePassQueueCache()
                                        resetFacePassRetryDelay()
                                        ttsSpeak("识别成功")
                                        runOnUiThread {
                                            tvFaceTips2.visibility = View.VISIBLE
                                            tvFaceTips2.text = "识别成功"
                                            if (companyMember != null) {
                                                showSuccessFace(companyMember!!.imgData)
                                            }
                                        }

                                        EventBus.getDefault().post(
                                            MessageEventBean(
                                                MessageEventType.AmountToken,
                                                faceToken
                                            )
                                        )
                                    } else {

                                        try {
                                            val b = mFacePassHandler!!.deleteFace(
                                                faceToken.toByteArray(StandardCharsets.ISO_8859_1)
                                            )
                                            //重新识别
                                            App.mFacePassHandler?.reset()
                                            LogUtils.e("人脸匹配失败,删除地库人脸token成功" + faceToken + "==" + b)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            LogUtils.e("人脸匹配失败,删除地库人脸token失败" + faceToken + e.message)
                                        }
                                    }
                                } else if (FacePassRecognitionState.RECOGNITION_PASS != result.recognitionState) {
                                    //  ttsSpeak("本地暂无人脸信息")
                                    runOnUiThread {
                                        handleFacePassFailRetryDelay()
                                    }
                                }
//                                var resultGson =
//                                    FacePassRecognitionResultToGson(
//                                        result.trackId,
//                                        faceToken,
//                                        result.detail!!,
//                                        String(result.featureData),
//                                        result.recognitionState,
//                                        result.searchErrorCode,
//                                        result.livenessErrorCode,
//                                        result.smallSearchErrorCode
//                                    )
//                            val idx: Int = findidx(ageGenderResult, result.trackId)
//                                LogUtils.e("recognize ", Gson().toJson(resultGson))
//                            if (idx == -1) {
//                                showRecognizeResult(
//                                    result.trackId,
//                                    result.detail.searchScore,
//                                    result.detail.livenessScore,
//                                    !TextUtils.isEmpty(faceToken)
//                                )
//                            } else {
//                                showRecognizeResult(
//                                    result.trackId,
//                                    result.detail.searchScore,
//                                    result.detail.livenessScore,
//                                    !TextUtils.isEmpty(faceToken),
//                                    ageGenderResult!![idx].age,
//                                    ageGenderResult[idx].gender
//                                )
//                            }
                            }
                        }
//                        else{
//                            ttsSpeak("本地暂无人脸信息")
//                        }
                        //                                }
//                            }
//                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    LogUtils.e("recognize-Exception", e.message)
                } catch (e: FacePassException) {
                    e.printStackTrace()
                }
            }
        }

        override fun interrupt() {
            isInterrupt = true
            super.interrupt()
        }
    }

    private var canSpeakFacePassFail = true
    private var canSpeakFacePassFailObserver: DisposableObserver<Long>? = null

    /**
     * 重置人脸识别自动重试
     */
    private fun resetFacePassRetryDelay(){
        canSpeakFacePassFail = true
        canSpeakFacePassFailObserver?.dispose()
        canSpeakFacePassFailObserver = null
    }

    /**
     * 处理人脸识别失败自动重试
     */
    private fun handleFacePassFailRetryDelay() {
        if (canSpeakFacePassFail) {
            canSpeakFacePassFail = false
            canSpeakFacePassFailObserver = object : DefaultDisposeObserver<Long>() {
                override fun onSuccess(t: Long) {
                    canSpeakFacePassFail = true
                    canSpeakFacePassFailObserver = null
                }
            }
            //3秒之后重置识别失败语音提醒
            Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(canSpeakFacePassFailObserver)
            tvFaceTips2.visibility = View.VISIBLE
            tvFaceTips2.text = "识别失败，正在重试"
        }
    }

    override fun onPictureTaken(cameraPreviewData: CameraPreviewData?) {
        if (mFeedFrameThread != null && !mFeedFrameThread!!.isInterrupt) {
            if (App.cameraType == FacePassCameraType.FACEPASS_DUALCAM) {
                ComplexFrameHelper.addRgbFrame(cameraPreviewData)
            } else {
                mFeedFrameQueue.offer(cameraPreviewData)
            }
        }
    }

    private fun showSuccessFace(imageUrl:String){
        if (ivSuccessHeader != null) {
            ivSuccessHeader!!.visibility = View.VISIBLE
            GlideApp.with(App.applicationContext)
                .load(imageUrl)
                .placeholder(R.mipmap.icon_camera_over_layer)
                .into(ivSuccessHeader!!)
        }
    }

    private fun hideSuccessFace() {
        if (ivSuccessHeader != null) {
            ivSuccessHeader!!.visibility = View.GONE
        }
    }

    private fun hideBalance() {
        flCameraAvatar.visibility = View.VISIBLE
        tvFaceTips.visibility = View.VISIBLE
        llBalance.visibility = View.GONE
    }

    private fun showBalance(payMoney: String, balance: String) {
        flCameraAvatar.visibility = View.GONE
        tvFaceTips2.visibility = View.GONE
        tvFaceTips.visibility = View.GONE
        llBalance.visibility = View.VISIBLE
        tvPayMoney.text = "¥ $payMoney"
        SpanUtils.with(tvBalance)
            .append("账户余额：")
            .append("¥ $balance")
            .setFontSize(50, true)
            .create()
    }

    private fun hidePayError() {
        flCameraAvatar.visibility = View.VISIBLE
        tvFaceTips.visibility = View.VISIBLE
        llPayError.visibility = View.GONE
    }

    private fun showPayError(errorMsg: String) {
        flCameraAvatar.visibility = View.GONE
        tvFaceTips2.visibility = View.GONE
        tvFaceTips.visibility = View.GONE
        llPayError.visibility = View.VISIBLE;
        if (errorMsg.contains("余额不足")) {
            val split = errorMsg.split("|")
            if (split.size > 1) {
                SpanUtils.with(tvPayError)
                    .append("账户余额不足（可用 ¥ ")
                    .append(split[1])
                    .setForegroundColor(Color.parseColor("#FA5151"))
                    .append("）请充值")
                    .create()
            } else {
                tvPayError.text = errorMsg
            }
        } else {
            tvPayError.text = errorMsg
        }
    }

    //接收事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    open fun onEventReceiveMsg(message: MessageEventBean) {
        when (message.type) {
            MessageEventType.OpenFacePassPay -> {
                openAndInitCamera()
                //重新识别
                App.mFacePassHandler?.reset()
                startFacePassDetect()
                ivCameraOverLayer?.visibility = View.GONE
                hideSuccessFace()
            }
            MessageEventType.CloseFacePassPay -> {
                stopFacePassDetect()
                ivCameraOverLayer?.visibility  = View.VISIBLE
                hideSuccessFace()
                closeAndReleaseCamera()
            }
            MessageEventType.AmountNotice -> {
                hidePayError()
                hideBalance()
                //重新识别
                App.mFacePassHandler?.reset()

                //清除人脸缓存
                clearFacePassQueueCache()
                isStartFaceScan.set(true)
                message.content?.let {
                    realAmount = it
                    //tvAmount.visibility = View.VISIBLE
                    //tvAmount.text = "¥ $realAmount"

                    tvFaceTips.visibility = View.VISIBLE
                    tvFaceTips.text = "金额：$realAmount" + "元"
                    tvFaceTips2.visibility = View.VISIBLE
                    tvFaceTips2.text = "请支付"
                }
                hideSuccessFace()
                LogUtils.e("DiffAmountNotice取消操作" + isStartFaceScan)
            }
            MessageEventType.AmountNotice2 -> {
                hidePayError()
                hideBalance()
                realAmount = "0.0"
                isStartFaceScan.set(false)
                LogUtils.e("DiffAmountNotice2取消操作" + isStartFaceScan)

                tvFaceTips.visibility = View.VISIBLE
                tvFaceTips.text = "欢迎就餐"
                tvFaceTips2.visibility = View.GONE
                hideSuccessFace()
            }
            MessageEventType.AmountNotice3 -> {
                hidePayError()
                hideBalance()
                //重新识别
                App.mFacePassHandler?.reset()

                //清除人脸缓存
                clearFacePassQueueCache()
                isStartFaceScan.set(true)
                message.content?.let {
                    realAmount = it
                    //tvAmount.visibility = View.VISIBLE
                    //tvAmount.text = "¥ $realAmount"

                    tvFaceTips.visibility = View.VISIBLE
                    tvFaceTips.text = realAmount
                    tvFaceTips2.visibility = View.VISIBLE
                    tvFaceTips2.text = "请支付"
                }
                hideSuccessFace()
                LogUtils.e("DiffAmountNotice3取消操作" + isStartFaceScan)
            }
            MessageEventType.AmountPayingNotice -> {
                tvFaceTips2.visibility = View.VISIBLE
                tvFaceTips2.text = "支付中,请稍等"
            }
            MessageEventType.AmountSuccess -> {
                message.content?.let {
                    companyMember = CompanyMemberBiz.getCompanyMemberByCard(message.content)
                }

                if (message.realPayMoney != null) {
                    message.ext?.let { showBalance(message.realPayMoney!!, it) }
                } else {
                    message.ext?.let { showBalance(realAmount, it) }
                }

                realAmount = "0.0"

                Observable.timer(1500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io()) // 在IO调度器上订阅
                    .observeOn(AndroidSchedulers.mainThread()) // 在主线程上观察
                    .subscribe(
                        { aLong: Long? ->
                            // 这里的代码会在3秒后执行一次
                            hidePayError()
                            hideBalance()
                            hideSuccessFace()
                            tvFaceTips2.visibility = View.GONE

                            tvFaceTips.visibility = View.VISIBLE
                            tvFaceTips.text = "欢迎就餐"
                        }
                    ) { throwable: Throwable? ->
                        // 当发生错误时，这里的代码会被执行
                        Log.e("RxJava", "Error", throwable)
                    }

            }

            MessageEventType.AmountError -> {
                message.content?.let {
                    showPayError(it)
                }
            }

            MessageEventType.CompanyName -> {
                message.content?.let {
//                    tvCompanyName.text = it
                    tvWindow.text = message.ext
                }
            }

            MessageEventType.AmountCard -> {
                //根据卡号刷新人脸
                message.content?.let {
                    companyMember = CompanyMemberBiz.getCompanyMemberByCard(it)
                    if (companyMember != null) {
                       showSuccessFace(companyMember!!.imgData)
                    }
                }
            }

            MessageEventType.AmountRefund -> {
                hideSuccessFace()
                //点击退单按钮按键
                val switchFacePassPay =
                    SPUtils.getInstance().getBoolean(Constants.SWITCH_FACE_PASS_PAY)
                if (switchFacePassPay) {
                    tvFaceTips.visibility = View.VISIBLE
                    tvFaceTips.text = "请刷脸或刷卡"
                } else {
                    tvFaceTips.text = "请刷卡"
                }
                tvFaceTips2.visibility = View.GONE
                //清除人脸缓存
                clearFacePassQueueCache()
                isStartFaceScan.set(true)
            }
            MessageEventType.AmountRefundCancel -> {
                //点击退单按钮按键
                tvFaceTips.text = "欢迎就餐"
                tvFaceTips2.visibility = View.GONE
                llDefault.visibility = View.VISIBLE
                llRefundList.visibility = View.GONE
                rlRefundConfirm.visibility = View.GONE
                isStartFaceScan.set(false)
                hidePayError()
                hideBalance()
                hideSuccessFace()
            }
            MessageEventType.AmountRefundList -> {
                //退款列表
                message.content?.let {
                    val fromJson = Gson().fromJson(
                        it,
                        ConsumeRefundListBean1::class.java
                    )
                    if (fromJson.results != null && fromJson.results!!.size > 0) {
                        llRefundList.visibility = View.VISIBLE
                        rlRefundConfirm.visibility = View.GONE
                        llDefault.visibility = View.GONE
                        tvRefundName.text = "姓名：" + fromJson.customerName
                        tvNumber.text = "账号/卡号：" + fromJson.customerNo

                        Glide.with(App.applicationContext).load(fromJson.customerImg)
                            .placeholder(R.mipmap.icon_camerapreview_person_3) // 设置占位图
                            .into(ivHeader2)
                        layoutManager = LinearLayoutManager(App.applicationContext);//添加布局管理器
                        rvRefund.layoutManager = layoutManager//设置布局管理器

                        mAdapter = ConsumeRefundListAdapter(fromJson.results)
                        rvRefund.adapter = mAdapter
                        mAdapter?.setList(fromJson.results)
                        mAdapter?.setSelectedPosition(0)
                    }

                }

            }
            MessageEventType.AmountRefundSuccess -> {
                LogUtils.e("退款成功")
                //退款成功
                message.content?.let {
                    LogUtils.e("退款成功" + it)
                    tvRefundName2.text = it + ""
                    rlRefundConfirm.visibility = View.VISIBLE
                }
            }
            MessageEventType.AmountRefundListSelect -> {
                //退款成功
                message.obj?.let {

                    if (mAdapter != null && layoutManager != null) {
                        mAdapter?.setSelectedPosition(it as Int)
                        layoutManager?.scrollToPosition(it as Int)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LogUtils.e("副屏初始化onStop")
        resetFacePassRetryDelay()
        stopFacePassDetect()
        CommonTipsHelper.INSTANCE.setConsumerTipsView(null)
        SystemEventHelper.INSTANCE.removeSystemEventListener(systemEventListener)
        closeAndReleaseCamera()
        EventBus.getDefault().unregister(this)
    }

    fun ttsSpeak(value: String) {
        App.TTS.setSpeechRate(1f)
        App.TTS.speak(
            value,
            TextToSpeech.QUEUE_ADD, null
        )
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    /**
     * 刷新系统时间
     */
    private fun refreshSystemDate(formatDateStr: String) {
        try {
            val split = formatDateStr.split(" ")
            if (split.size > 2) {
                tvTime.text = "${split[1]} ${split[2]}"
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 刷新网络连接状态
     */
    private fun refreshNetworkStatus(netType: Int, isConnected: Boolean) {
        if (netType == SystemEventHelper.ETHERNET_NET_TYPE) {
            if (isConnected) {
                ivWifiState.setImageResource(R.mipmap.icon_ethernet)
            } else {
                ivWifiState.setImageResource(R.mipmap.icon_ethernetno)
            }
        } else if (netType == SystemEventHelper.WIFI_NET_TYPE) {
            if (isConnected) {
                ivWifiState.setImageResource(R.mipmap.icon_wifi4)
            } else {
                ivWifiState.setImageResource(R.mipmap.icon_wifi0)
            }
        } else if (netType == SystemEventHelper.MOBILE_NET_TYPE) {
            if (isConnected) {
                ivWifiState.setImageResource(R.mipmap.icon_level_4)
            } else {
                ivWifiState.setImageResource(R.mipmap.icon_levelno)
            }
        } else {
            ivWifiState.setImageResource(0)
        }
    }

    /**
     * 刷新网络信号状态
     */
    private fun refreshNetworkRssi(netType: Int, isConnect: Boolean, level: Int) {
        if (netType == SystemEventHelper.WIFI_NET_TYPE) {
            if (isConnect) {
                if (level == 0) {
                    ivWifiState.setImageResource(R.mipmap.icon_wifi0)
                } else if (level == 1) {
                    ivWifiState.setImageResource(R.mipmap.icon_wifi1)
                } else if (level == 2) {
                    ivWifiState.setImageResource(R.mipmap.icon_wifi2)
                } else if (level == 3) {
                    ivWifiState.setImageResource(R.mipmap.icon_wifi3)
                } else if (level == 4) {
                    ivWifiState.setImageResource(R.mipmap.icon_wifi4)
                }
            } else {
                ivWifiState.setImageResource(R.mipmap.icon_wifi0)
            }
        } else if (netType == SystemEventHelper.MOBILE_NET_TYPE) {
            if (isConnect) {
                if (level == 0) {
                    ivWifiState.setImageResource(R.mipmap.icon_levelno)
                } else if (level == 1) {
                    ivWifiState.setImageResource(R.mipmap.icon_level_1)
                } else if (level == 2) {
                    ivWifiState.setImageResource(R.mipmap.icon_level_2)
                } else if (level == 3) {
                    ivWifiState.setImageResource(R.mipmap.icon_level_3)
                } else if (level == 4) {
                    ivWifiState.setImageResource(R.mipmap.icon_level_4)
                }
            } else {
                ivWifiState.setImageResource(R.mipmap.icon_levelno)
            }
        }
    }

    private var batteryDefaultPro: Drawable? = null
    private var batteryChargingPro: Drawable? = null

    /**
     * 刷新电池相关
     */
    private fun refreshBatteryStatus(batteryLevel: Float, isCharging: Boolean) {
        if (batteryChargingPro == null) {
            batteryDefaultPro = if (batteryLevel > 20) {
                resources?.getDrawable(R.drawable.battery_pro_bar_default)
            } else {
                resources?.getDrawable(R.drawable.battery_pro_bar_low)
            }
            batteryChargingPro =
                resources?.getDrawable(R.drawable.battery_pro_bar_charging)
        }
        if (batteryChargingPro != null) {
            if (isCharging) {
                ivBatteryBg.setImageResource(R.mipmap.icon_battery_ischarging)
                pbBattery.progressDrawable = batteryChargingPro
            } else {
                ivBatteryBg.setImageResource(R.mipmap.icon_battery_percent);
                pbBattery.progressDrawable = batteryDefaultPro
            }
            pbBattery.progress = batteryLevel.toInt()
        }
    }

    //系统事件监听
    private val systemEventListener: SystemEventHelper.OnSystemEventListener =
        object : SystemEventHelper.OnSystemEventListener {
            override fun onDateTick(formatDate: String) {
                refreshSystemDate(formatDate)
            }

            override fun onDateChange(formatDate: String) {
                refreshSystemDate(formatDate)
            }

            override fun onNetworkChanged(netType: Int, isConnect: Boolean) {
                refreshNetworkStatus(netType, isConnect)
            }

            override fun onNetworkRssiChange(netType: Int, isConnect: Boolean, level: Int) {
                refreshNetworkRssi(netType, isConnect, level)
            }

            override fun onBatteryChange(batteryPercent: Float, isChanging: Boolean) {
                refreshBatteryStatus(batteryPercent, isChanging)
            }
        }

}

