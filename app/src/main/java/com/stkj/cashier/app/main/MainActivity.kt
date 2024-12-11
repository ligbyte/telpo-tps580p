package com.stkj.cashier.app.main

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Notification
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.hardware.display.DisplayManager
import android.nfc.*
import android.os.*
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.util.valueIterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.king.base.util.SystemUtils
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseActivity
import com.stkj.cashier.app.base.helper.SystemEventHelper
import com.stkj.cashier.app.base.helper.SystemEventHelper.OnSystemEventListener
import com.stkj.cashier.app.mode.AmountFragment
import com.stkj.cashier.app.setting.Consumption1SettingFragment
import com.stkj.cashier.bean.CheckAppVersionBean
import com.stkj.cashier.bean.CompanyMemberBean
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.bean.Result
import com.stkj.cashier.bean.db.CompanyMemberdbEntity
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.MainActivityBinding
import com.stkj.cashier.dict.HomeMenu
import com.stkj.cashier.glide.GlideApp
import com.stkj.cashier.greendao.CompanyMemberdbEntityDao
import com.stkj.cashier.greendao.biz.CompanyMemberBiz
import com.stkj.cashier.greendao.tool.DBManager
import com.stkj.cashier.util.*
import com.stkj.cashier.util.TimeUtils
import com.stkj.cashier.util.keyevent.KeyEventResolver
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import mcv.facepass.types.FacePassAddFaceResult
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.HttpException
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, MainActivityBinding>(), View.OnClickListener {

    private var mPresentation: DifferentDisplay? = null
    private lateinit var reportDeviceStatusDisposable: Disposable
    private val fragments by lazy {
        SparseArray<Fragment>()
    }
    var pageIndex = 1
    var pageSize = 5000
    private lateinit var mDisplayManager: DisplayManager//屏幕管理类

    private var currentTimeDisposable: Disposable? = null
    var queueManager = QueueManager()

    var callBack = false

    var face_0_Count = 0;
    var face_1_Count = 0;
    var face_2_Count = 0;
    var face_def_Count = 0;
    var totalFaceCount = 0;

    var latch: CountDownLatch? = null

    lateinit var progressDialog: AlertDialog //更新进度弹窗
    lateinit var allFaceDownDialog: AlertDialog //全量人脸
    lateinit var tvProgress: TextView
    lateinit var sbProgress: ProgressBar

    var allFaceDown: Boolean = false
    private var netStatusDisposable: Disposable? = null
    private var keyEventResolver: KeyEventResolver? = null

    private var isNetworkLost: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // 检查首页Activity是否存在
        App.mMainActivity = this
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        LogUtils.e("主页initData")
        RkSysTool.getInstance().initContext(this)
        RkSysTool.getInstance().setStatusBar(false)
        RkSysTool.getInstance().setNavitionBar(false)

        SPUtils.getInstance().put(Constants.SWITCH_CONSUMPTION_DIALOG, 0) //不显示消费确认框
        showFragment(HomeMenu.MENU1)
        SPUtils.getInstance().put(Constants.FRAGMENT_SET, false)

        ScreenUtils.setFullScreen(this)
        BarUtils.setNavBarVisibility(this, false)
        EventBus.getDefault().register(this)
        initUsbCard()
//        viewDataBinding.ivWifiState.setOnClickListener(this)
        App.serialNumber = SerialNumber.getMachineNumber()
        LogUtils.e("serialNumber", App.serialNumber)
        //binding.tvSerialNumber.text = App.serialNumber
        clickVersionUpdate()
        showDifferentDisplay()

        initCheckStatus()
        getIntervalCardType()
        company()
        viewModel.deviceStatus.observe(this) {
            LogUtils.e("deviceStatus observe")
            var indexs = it.data?.updateUserInfo?.split("&")
            if (indexs != null) {
                for (item in indexs) {
                    LogUtils.e("心跳item" + item + "/" + callBack + "/" + latch?.count + "/" + allFaceDown)
                    if (item == "1") {
                        /* if (queueManager == null) {
                             LogUtils.e("queueManager=null")
                             queueManager = QueueManager()
                         }
                         if (!callBack&&(latch?.count==0L||latch==null)&&!allFaceDown) {
                             queueManager.addTask(Runnable {
                                 LogUtils.e("队列新增")
                                 companyMember(1)
                             })
                             LogUtils.e("item下发" + item)
                         }*/
                        if ((latch?.count == 0L || latch == null) && !allFaceDown) {
                            companyMember(1)
                            LogUtils.e("item下发" + item)
                        }
//                        isBreak=true
//
                    } else if (item == "2") {
                        getIntervalCardType()
                    } else if (item == "3") {
                        offlineSet()
                    } else if (item == "4") {
                        company()
                    }
                }
            }

        }

        viewModel.offlineSet.observe(this) {
            LogUtils.e("offlineSet observe")
        }
        viewModel.company.observe(this) {
            LogUtils.e("company observe")
            //{"Code":10000,"company":"测试服","Data":{"deviceName":"键盘设备"},"Message":"成功"}

//            binding.tvCompanyName.text = it.company
            EventBus.getDefault().post(
                MessageEventBean(
                    MessageEventType.CompanyName,
                    it.company,
                    it.data?.deviceName
                )
            )
        }
        viewModel.companyMember.observe(this) { items ->
            LogUtils.e("lime== 人脸录入后台返回的人数" + (items.data?.results?.size))
            if (items.code == 10000) {
                LogUtils.e("companyMember", "==" + items.data?.results?.size)
//            checkFace(it.results)
                CompanyMemberBiz.addCompanyMembers(items.data?.results)
//            CompanyMemberBiz.getCompanyMemberList {
//                if (it.size == items.totalCount){
//                callBack = true

                //latch = items.data?.results?.size?.let { CountDownLatch(it) }

                checkFace(items.data?.results)
                EventBus.getDefault().post(MessageEventBean(MessageEventType.FaceDBChange))

                //latch?.await(); // 等待所有请求完成
                //LogUtils.e("人脸录入调用companyMember方法2")
                //companyMember(1)

                /*Observable.timer(3, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { aLong ->
                        LogUtils.e("人脸录入调用companyMember方法2")
                        companyMember(1)
                    }*/
            } else {
                if (items.code == 10024) {
                    callBack = false
                    allFaceDown = false
                    if (allFaceDownDialog != null) {
                        allFaceDownDialog.dismiss()
                    }
                    queueManager.clearTasks()
                    EventBus.getDefault().post(MessageEventBean(MessageEventType.FaceDBChangeEnd))


                } else {
                    items.message?.let { it1 -> showToast(it1) }
                }
            }
//                }
//            }
        }
        viewModel.setCallback(object : MainViewModel.MyCompanyMemberCallback {
            override fun onDataReceived(items: Result<CompanyMemberBean>) {
                LogUtils.e("lime== 人脸录入后台返回的人数Callback" + (items.data?.results?.size))
                LogUtils.e("lime== items.code " + (items.code))
                // 10000 人脸数据下发成功
                if (items.code == 10000) {
                    LogUtils.e("lime== companyMember", "==" + items.data?.results?.size)
                    CompanyMemberBiz.addCompanyMembers(items.data?.results)

                    checkFace(items.data?.results)
                    EventBus.getDefault().post(MessageEventBean(MessageEventType.FaceDBChange))
                    //latch?.await(); // 等待所有请求完成
                    //LogUtils.e("人脸录入调用companyMember方法2")
                    //companyMember(1)
                    /*Observable.timer(3, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { aLong ->
                            LogUtils.e("人脸录入调用companyMember方法2")
                            companyMember(1)
                        }*/
                } else {
                    //10024 人脸数据下发结束
                    if (items.code == 10024) {
                        callBack = false
                        queueManager.clearTasks()
                        EventBus.getDefault()
                            .post(MessageEventBean(MessageEventType.FaceDBChangeEnd))
                        allFaceDown = false
                        //isBreak=false
                        //isDownFace=false
                        if (allFaceDownDialog != null) {
                            allFaceDownDialog.dismiss()
                        }

                        //显示人脸数
//                        var faceTokens: Array<ByteArray>? = null
//                        if (App.mFacePassHandler != null) {
//                            faceTokens =
//                                App.mFacePassHandler!!.getLocalGroupInfo(Constants.GROUP_NAME)
//                        }
//                        CompanyMemberBiz.getCompanyMemberList {
//                            LogUtils.e("getCompanyMemberList", "==" + it.size)
//                            binding.tvCheckMember.text =
//                                "已入库(" + faceTokens?.size + "/" + it.size + ")"
//                        }

                    } else {
                        items.message?.let { it1 -> showToast(it1) }
                    }
                }
            }

            override fun onError(error: Throwable) {
                cancelAllFaceDownDialog()
            }

        })
        viewModel.companyMemberStatus.observe(this) {
            // 处理响应
            latch?.countDown(); // 每个请求完成后递减计数器
            LogUtils.e("callBack计数器", "==$callBack" + latch?.count)
            LogUtils.e("companyMemberStatus observe")
//            SPStaticUtils.put(SPKey.KEY_CONFIG,it.getAiStatus())
        }
        viewModel.intervalCardType.observe(this) {
            LogUtils.e("intervalCardType observe")
//            SPStaticUtils.put(SPKey.KEY_CONFIG,it.getAiStatus())
            App.intervalCardType = it
            requestConsumptionType()
            EventBus.getDefault()
                .post(MessageEventBean(MessageEventType.IntervalCardType))
        }
        viewModel.checkAppVersion.observe(this) {
            LogUtils.e("checkAppVersion observe")
            if (it.code == 10000) {
                if (it.data?.version?.toInt()!! > AppUtils.getAppVersionCode()) {
                    // showUpdataDialog(it.data)
//                    mPresentation?.dismiss()
//                    showUpdateDisplay()
                    showUpdataProgressDialog(it.data)


                }
            } else {
                it.message?.let { it1 -> showToast(it1) }
            }
        }
        viewModel.equUpgCallback.observe(this) {
            LogUtils.e("equUpgCallback observe")
        }
        viewModel.currentTimeInfo.observe(this) {
            LogUtils.e("currentTimeInfo observe")
            if (it.code == 10000) {
                App.currentTimeInfo = it.data
                EventBus.getDefault().post(MessageEventBean(MessageEventType.CurrentTimeInfo))
            } else {
                EventBus.getDefault().post(MessageEventBean(MessageEventType.CurrentTimeInfoFail))
            }
        }
//        var faceTokens: Array<ByteArray>? = null
//        if (App.mFacePassHandler != null) {
//            faceTokens = App.mFacePassHandler!!.getLocalGroupInfo(Constants.GROUP_NAME)
//        }
//        val faceTokenList: MutableList<String> = ArrayList()
//        if (faceTokens?.isNotEmpty() == true) {
//            for (j in faceTokens?.indices!!) {
//                if (faceTokens!![j].isNotEmpty()) {
//                    faceTokenList.add(String(faceTokens!![j]))
//                }
//            }
//            CompanyMemberBiz.getCompanyMemberList {
//                LogUtils.e("getCompanyMemberList", "==" + it.size)
//                binding.tvCheckMember.text = "已入库(" + faceTokens!!.size + "/" + it.size + ")"
//            }
//        }
//
        initMemberThread()

        //网络状态
        requestNetStatus()
        viewModel.netStatus.observe(this) {
            // LogUtils.d("网络状态" + it)
            if (viewModel.isSuccess(it)) {
                if (isNetworkLost) {
                    Log.i("viewModel.netStatus", "net connect success")
                    isNetworkLost = false
//                    ttsSpeak("网络连接成功")
                    //请求餐厅时段信息
                    getIntervalCardType()
                }
            } else {
                if (!isNetworkLost) {
                    Log.i("viewModel.netStatus", "isNetworkLost")
                    isNetworkLost = true
//                    ttsSpeak("网络已断开")
                }
            }
        }
        setCallback(object : MyCallback {
            override fun onDataReceived(data: String) {
                LogUtils.e("网络消息回调2" + data)
                // ToastUtils.showShort("断网")
                when (data) {
                    getString(R.string.result_network_unavailable_error),
                    getString(R.string.result_connect_timeout_error),
                    getString(R.string.result_connect_failed_error) -> {

                        cancelAllFaceDownDialog()

                    }

                    else -> LogUtils.e("网络消息回调" + data)
                }
            }

            override fun onError(error: Exception) {
            }
        })

        val builder = AlertDialog.Builder(this, R.style.app_dialog)
        allFaceDownDialog = builder.create()
        allFaceDownDialog.setCancelable(false)

        keyEventResolver = KeyEventResolver(object : KeyEventResolver.OnScanSuccessListener {
            override fun onScanSuccess(barcode: String?) {
                LogUtils.e("扫码: 键盘" + barcode)

                EventBus.getDefault().post(
                    MessageEventBean(
                        MessageEventType.AmountScanCode,
                        barcode
                    )
                )
            }
        })

        //系统事件监听
        SystemEventHelper.INSTANCE.addSystemEventListener(systemEventListener)
    }

    private fun cancelAllFaceDownDialog() {
        if (allFaceDownDialog.isShowing || allFaceDown) {
            allFaceDownDialog.dismiss()
            EventBus.getDefault()
                .post(MessageEventBean(MessageEventType.FaceDBChangeEnd))
            allFaceDown = false

            latch = CountDownLatch(1) // 新的latch，初始计数为5
            latch?.countDown()
        }
    }

    private fun initMemberThread() {
        Thread(Runnable {
            while (true) {
                if (queueManager.hasMoreTasks() && !callBack) {
                    callBack = true
                    queueManager.nextTask?.run()
                    LogUtils.e("队列执行")
                }
            }
        }).start()

    }

    private fun initCheckStatus() {
        var time = SPUtils.getInstance().getInt(Constants.FACE_HEAD_BEAT, 30).toLong()
        reportDeviceStatusDisposable = Observable.interval(0, time, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aLong ->
                // 点击回复按钮
                var map = hashMapOf<String, Any>()
                map["mode"] = "ReportDeviceStatus"
                map["machine_Number"] = App.serialNumber
                var md5 = EncryptUtils.encryptMD5ToString16(App.serialNumber)
                map["sign"] = md5
                viewModel.reportDeviceStatus(map)


            }
    }

    private fun requestConsumptionType() {
        currentTimeDisposable?.dispose()
        currentTimeDisposable = Observable.interval(0, 60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aLong ->
                //一分钟检查金额模式 固定模式
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    checkCurrentAmountMode()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkCurrentAmountMode() {
        LogUtils.e("checkCurrentAmountMode start")
        val switchFixAmount = SPUtils.getInstance().getBoolean(Constants.SWITCH_FIX_AMOUNT, false);
        if (switchFixAmount) {
            val currentFixAmountTime =
                SPUtils.getInstance().getString(Constants.CURRENT_FIX_AMOUNT_TIME, "")
            if (!TextUtils.isEmpty(currentFixAmountTime)) {
                val split = currentFixAmountTime.split("-")
                if (split.size >= 2) {
                    LogUtils.e("checkCurrentAmountMode RefreshFixAmountMode")
                    if (!TimeUtils.isCurrentTimeIsInRound(split[0], split[1])) {
                        EventBus.getDefault()
                            .post(MessageEventBean(MessageEventType.RefreshFixAmountMode))
                    }
                }
            } else {
                LogUtils.e("checkCurrentAmountMode RefreshFixAmountMode empty currentFixAmountTime")
                EventBus.getDefault().post(MessageEventBean(MessageEventType.RefreshFixAmountMode))
            }
        }
        LogUtils.e("checkCurrentAmountMode end")
    }

    private fun requestNetStatus() {
        netStatusDisposable?.dispose()
        netStatusDisposable = Observable.interval(0, 10, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aLong ->
                var timeMap = hashMapOf<String, Any>()
                timeMap["mode"] = "HealthCheck"
                timeMap["machine_Number"] = App.serialNumber
                var timeMd5 = EncryptUtils.encryptMD5ToString16(App.serialNumber)
                timeMap["sign"] = timeMd5
                viewModel.networkStatus(timeMap, errorCallback = {
                    when (it) {
                        is SocketTimeoutException -> if (!isNetworkLost) {
                            isNetworkLost = true
//                            ttsSpeak("网络已断开")
                            Log.i("viewModel.netStatus", "isNetworkLost")
                        }

                        is ConnectException -> if (!isNetworkLost) {
                            isNetworkLost = true
//                            ttsSpeak("网络已断开")
                            Log.i("viewModel.netStatus", "isNetworkLost")
                        }

                        is HttpException -> if (!isNetworkLost) {
                            isNetworkLost = true
//                            ttsSpeak("网络异常")
                            Log.i("viewModel.netStatus", "isNetworkLost")
                        }
                    }
                })
            }
    }


    private fun initUsbCard() {
        try {
            ///dev/ttyS5 读卡 /dev/ttyS1 读卡  //ttyS3 称重 115200
            val serialHelper: SerialHelper = object : SerialHelper("/dev/ttyS4", 115200) {
                override fun onDataReceived(comBean: ComBean) {
                    try {
                        LogUtils.i("读卡", comBean.bRec)

                        val data = ConvertUtils.bytes2HexString(comBean.bRec)

                        val card: String =
                            ParseData.decodeHexStringIdcard2Int(data.substring(6, 14))
                        LogUtils.i("读卡", card)

                        if (DifferentDisplay.isStartFaceScan.get()) {
                            DifferentDisplay.isStartFaceScan.set(false)
                            EventBus.getDefault().post(
                                MessageEventBean(
                                    MessageEventType.AmountCard,
                                    card
                                )
                            )
                        }
                        LogUtils.e("cardData", card)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        LogUtils.e("读取卡数据失败", e.localizedMessage)
                        //读取卡数据失败
                    }
                }
            }
            serialHelper.open()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private fun checkFace(entities: List<CompanyMemberdbEntity>?) {
        Thread(Runnable {
//            CompanyMemberBiz.getCompanyMemberList {
            face_0_Count = 0;
            face_1_Count = 0;
            face_2_Count = 0;
            face_def_Count = 0;
            totalFaceCount = 0;

            latch = entities?.size?.let { CountDownLatch(it) }

            for ((index, item) in entities?.withIndex()!!) {
                LogUtils.e("lime*** 下发人脸中 589")
                val unique =
                    DBManager.getInstance().daoSession.companyMemberdbEntityDao.queryBuilder()
                        .where(CompanyMemberdbEntityDao.Properties.UniqueNumber.eq(entities[index].uniqueNumber))
                        .unique()
                LogUtils.e("lime*** 下发人脸中 594")
                //LogUtils.e("lime== 人脸add" + unique.cardState + "//"+unique?.faceToken )
                LogUtils.e("lime*** 下发人脸中 596")
                if (unique != null && unique.cardState != 64 && unique?.faceToken == null) {
//                    if (entities[index].cardState != 64) {
//                        entities[i].id = unique.id
//                        DBManager.getInstance().daoSession.companyMemberdbEntityDao.update(
//                            entities[i]
//                        )
//                    }
                    LogUtils.e("lime*** 下发人脸中 603")
                    var base64ToBitmap: Bitmap? = null
                    try {
                        LogUtils.e("lime*** 下发人脸中 606")
                        val futureTarget: FutureTarget<Bitmap> =
                            GlideApp.with(App.applicationContext)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .load(item.imgData)
                                .submit()
                        base64ToBitmap = futureTarget.get()
                    } catch (e: Throwable) {
                        LogUtils.e(
                            "-facePassHelper-addFacePassToLocal--cardNumber: " + item.cardNumber
                                    + " load imageData error " + item.imgData
                        )
                        e.printStackTrace()
                    }
                    // var base64ToBitmap = ImageUtils.base64ToBitmap(item.imgData)
                    try {
                        if (base64ToBitmap == null) {
                            item.result = -99
                            LogUtils.e("lime== 人脸token生成失败" + item.fullName + "///下载图片失败")
                            CompanyMemberBiz.updateCompanyMember(item)
                        } else {
                            val result: FacePassAddFaceResult? =
                                App.mFacePassHandler?.addFace(base64ToBitmap)
                            totalFaceCount += 1;
                            if (result?.result == 0) {
                                //0：成功
                                item.result = 20
                                face_0_Count += 1;

                            } else if (result?.result == 1) {
                                //1：没有检测到人脸
                                face_1_Count += 1;
                                item.result = 3
                                LogUtils.e("lime== 入库失败--没有检测到人脸" + item.fullName)
                            } else if (result?.result == 2) {
                                //2：检测到人脸，但是没有通过质量判断
                                face_2_Count += 1;
                                item.result = 4
                                LogUtils.e("lime== 入库失败--检测到人脸，但是没有通过质量判断" + item.fullName)
                            } else {
                                //其他值：未知错误
                                face_def_Count += 1;
                                item.result = 5
                                LogUtils.e("lime== 入库失败--其他值：未知错误" + item.fullName)
                            }
                            if (result?.result == 0) {
                                item.faceToken = String(result.faceToken, charset("ISO-8859-1"))
//                            unique.faceToken = String(result.faceToken)
                                LogUtils.e("lime== 人脸token生成成功" + item.fullName + item.phone + index + "///" + item.faceToken)
                                CompanyMemberBiz.updateCompanyMember(item)
                                try {
                                    val b: Boolean = App.mFacePassHandler!!.bindGroup(
                                        Constants.GROUP_NAME,
                                        result.faceToken
                                    )
                                    val result = if (b) "success " else "failed"
//                                toast("bind  $result")
                                    LogUtils.e("addFace", "bind  $result")
                                } catch (e: Exception) {
                                    e.printStackTrace()
//                                toast(e.message)
                                }
                            } else {
                                LogUtils.e("lime== 人脸token生成失败" + item.fullName + "///" + item.faceToken)
                                CompanyMemberBiz.updateCompanyMember(item)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LogUtils.e("lime== addFace", item.fullName + e.message)
                        // toast(e.message)
                    } finally {
                        //base64ToBitmap?.recycle()


                    }

                }

                LogUtils.e("lime*** 下发人脸中 682")

                downFaceFail(item, 0)
                LogUtils.e(
                    "人脸",
                    "lime== ==$callBack" + callBack + item.fullName + item.phone + "imgData " + item.imgData + "index" + index + "/" + (entities.size - 1)
                )
                // 处理响应
                //latch?.countDown(); // 每个请求完成后递减计数器

                if (index == entities.size - 1) {
                    // Thread.sleep(1000)
                    callBack = false
//                    isDownFace = false

                    LogUtils.e("lime== callBack", "==$callBack" + latch?.count)

                }
            }

            LogUtils.w("lime============ face_0_Count: " + face_0_Count)
            LogUtils.w("lime============ face_1_Count: " + face_1_Count)
            LogUtils.w("lime============ face_2_Count: " + face_2_Count)
            LogUtils.w("lime============ face_def_Count: " + face_def_Count)
            LogUtils.w("lime============ totalFaceCount: " + totalFaceCount)


            LogUtils.w(
                "lime============  sdk Face Count face_pass_1: " + App?.mFacePassHandler?.getLocalDyGroupFaceNum(
                    "face_pass_1"
                )
            )


            //   companyMember()
            latch?.await(); // 等待所有请求完成
            //LogUtils.e("人脸录入调用companyMember方法2")
            companyMember(1)
//            }
        }).start()


    }

    private fun clickVersionUpdate() {
        // TODO 处理点击“版本更新”逻辑

        var map = hashMapOf<String, Any>()
        map["mode"] = "CheckAppVersion"
        map["machine_Number"] = App.serialNumber
//        map["deviceType"] = "1"
        map["deviceType"] = getString(R.string.deviceType)//设备厂商+产品+设备型号
        map["version_No"] = AppUtils.getAppVersionCode()
        // var md5 =EncryptUtils.encryptMD5ToString16("1&" + App.serialNumber + "&" + AppUtils.getAppVersionCode())
        var md5 =
            EncryptUtils.encryptMD5ToString16(getString(R.string.deviceType) + "&" + App.serialNumber + "&" + AppUtils.getAppVersionCode())
        map["sign"] = md5
        viewModel.checkAppVersion(map)
    }

    fun ttsSpeak(value: String) {
        App.TTS.setSpeechRate(1f)
        App.TTS.speak(
            value,
            TextToSpeech.QUEUE_FLUSH, null
        )
    }

    private fun company() {
        var companyMap = hashMapOf<String, Any>()
        companyMap["mode"] = "company_setup"
        companyMap["machine_Number"] = App.serialNumber
        var companyMd5 = EncryptUtils.encryptMD5ToString16(App.serialNumber)
        companyMap["sign"] = companyMd5
        viewModel.companySetup(companyMap)
    }

    private fun offlineSet() {
        var map = hashMapOf<String, Any>()
        map["mode"] = "OfflineSet"
        map["machine_Number"] = App.serialNumber
        var md5 = EncryptUtils.encryptMD5ToString16(App.serialNumber)
        map["sign"] = md5
        viewModel.offlineSet(map)
    }

    private fun getIntervalCardType() {
        val map: MutableMap<String, Any> = HashMap()
        var deviceId = App.serialNumber
        map["mode"] = "GetIntervalCardType"
        map["machine_Number"] = deviceId
        var md5 = EncryptUtils.encryptMD5ToString16(deviceId)
        map["sign"] = md5
        viewModel.getIntervalCardType(map);

    }

    fun companyMember(inferior_type: Int) {
        LogUtils.e("lime== 人脸录入companyMember" + inferior_type)
        if (inferior_type == 0) {
            if (SystemUtils.isNetWorkActive(getApp())) {
                runOnUiThread {
                    showAllFsceProgressDialog()
                }
            } else {
                //ttsSpeak(getString(R.string.result_network_unavailable_error))
//                ttsSpeak("网络已断开，请检查网络。")
                EventBus.getDefault().post(MessageEventBean(MessageEventType.FaceDBChangeEnd))
                allFaceDown = false
                return
            }
        }
        allFaceDown = true
        /*  Thread(Runnable {
              try {
                  // 在子线程中执行的代码
                  // 输出日志信息
                  LogUtils.d("MyThread", "子线程开始执行isBreak" + isBreak + "isDownFace" + isDownFace)

                  while (isBreak) {
                      if (!isDownFace) {
                          isDownFace = true
                      }
                  }
                  // 再次输出日志信息
                  LogUtils.d("MyThread", "子线程执行完毕")
              } catch (e: InterruptedException) {
                  // 处理中断异常
                  e.printStackTrace()
                  LogUtils.e("MyThread", "子线程被中断", e)
              }
              //  LogUtils.e("人脸录入companyMember" + gett)
          }).start()*/

        var companyMap = hashMapOf<kotlin.String, Any>()
        //companyMap["mode"] = "CompanyMember"
        companyMap["mode"] = "KeyBoardCompanyMember" //键盘设备录入人员

        companyMap["inferior_type"] = inferior_type
        companyMap["machine_Number"] = App.serialNumber
        companyMap["pageIndex"] = pageIndex
        companyMap["pageSize"] = pageSize
        var companyMd5 =
            EncryptUtils.encryptMD5ToString16("$inferior_type&" + App.serialNumber + "&$pageIndex&$pageSize")
        companyMap["sign"] = companyMd5
        viewModel.companyMember(companyMap)
    }

    private fun downFaceFail(item: CompanyMemberdbEntity, isFinish: Int) {
        var companyMap = hashMapOf<String, Any>()
        companyMap["mode"] = "DownFaceFail"
//        companyMap["cardNumber"] = item.cardNumber
        companyMap["customerId"] = item.uniqueNumber
        if (item.result == null) {
            item.result = 1
            companyMap["errorType"] = 1
        } else {
            companyMap["errorType"] = item.result
        }

        companyMap["isFinish"] = isFinish
        companyMap["machine_Number"] = App.serialNumber

        // var companyMd5 =EncryptUtils.encryptMD5ToString16(item.cardNumber + "&" + item.result + "&$isFinish&" + App.serialNumber)
        var companyMd5 =
            EncryptUtils.encryptMD5ToString16(item.uniqueNumber + "&" + item.result + "&$isFinish&" + App.serialNumber)
        companyMap["sign"] = companyMd5
        viewModel.downFaceFail(companyMap)

    }

    override fun getLayoutId(): Int {
        return R.layout.main_activity
    }

    //双屏显示
    private fun showDifferentDisplay() {
        mDisplayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager;
        //得到显示器数组
        var displays = mDisplayManager.displays
        LogUtils.e("副屏数量" + displays.size)

        if (displays.size > 1) {
            try {
                if (mPresentation == null) {
                    LogUtils.e("副屏1")
                    mPresentation = DifferentDisplay(this, displays[0])
                    mPresentation?.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    mPresentation?.show()

                } else {
                    LogUtils.e("副屏2")
                    mPresentation = DifferentDisplay(this, displays[0])
                    mPresentation?.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    mPresentation?.show()

                }
            } catch (e: Exception) {
                LogUtils.e("副屏" + e.message)
            }
        } else {
            mDisplayManager.registerDisplayListener(object : DisplayManager.DisplayListener {
                override fun onDisplayAdded(displayId: Int) {
                    if (displayId != Display.DEFAULT_DISPLAY) {
                        val managerDisplay = mDisplayManager.getDisplay(displayId)
                        //add Presentation display
                        if (managerDisplay != null) {
                            //todo

                        }
                    }
                }

                override fun onDisplayRemoved(displayId: Int) {}
                override fun onDisplayChanged(displayId: Int) {}
            }, Handler(this.getMainLooper()))

            if (mPresentation != null) {
                try {
                    mPresentation?.show()
                } catch (e: Exception) {
                    LogUtils.e("副屏" + e.message)
                }
                LogUtils.e("副屏3")
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //天波二维码
        if (requestCode == 0x124) {
            if (resultCode == 0) {
                if (data != null) {
                    try {
                        //  mBeepManager.playBeepSoundAndVibrate()
                    } catch (e: java.lang.Exception) {
                        // TODO: handle exception
                        e.printStackTrace()
                    }
                    val qrcode = data.getStringExtra("qrCode")
                    //change(qrcode);
                    Toast.makeText(this@MainActivity, "Scan result:$qrcode", Toast.LENGTH_LONG)
                        .show()
                    return
                }
            } else {
                Toast.makeText(this@MainActivity, "Scan Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtils.e("主页onResume")
    }


    override fun onPause() {
        super.onPause()
        LogUtils.e("主页onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.e("主页onStop")
    }

    override fun onBackPressed() {
//        if (lastTime < System.currentTimeMillis() - Constants.DOUBLE_CLICK_EXIT_TIME) {
//            lastTime = System.currentTimeMillis()
//            showToast(R.string.tips_double_click_exit)
//            return
//        }
//        super.onBackPressed()
    }


    private fun hideAllFragment(fragmentTransaction: FragmentTransaction) {
        fragments.valueIterator().forEach {
            fragmentTransaction.hide(it)
        }
    }

    fun showPlaceHolderFragment(placeHolderFragment: Fragment) {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.place_holder_content, placeHolderFragment)
                .commitNowAllowingStateLoss()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun hidePlaceHolderFragment(placeHolderFragment: Fragment) {
        try {
            supportFragmentManager.beginTransaction()
                .remove(placeHolderFragment)
                .commitNowAllowingStateLoss()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun showFragment(@HomeMenu menu: Int) {
        LogUtils.e("主页showFragment" + this)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        hideAllFragment(fragmentTransaction)
        fragmentTransaction.show(getFragment(fragmentTransaction, menu))
        fragmentTransaction.commit()
    }

    private fun getFragment(
        fragmentTransaction: FragmentTransaction,
        @HomeMenu menu: Int
    ): Fragment {
        var fragment: Fragment? = fragments[menu]
        if (fragment == null) {
            LogUtils.e("主页createFragment" + this)
            fragment = createFragment(menu)
            fragment.let {
                fragmentTransaction.add(R.id.fragmentContent, it)
                fragments.put(menu, it)
            }
        }
        return fragment
    }

    /**
     * 创建 Fragment
     */
    private fun createFragment(@HomeMenu menu: Int): Fragment = when (menu) {
        // TODO 只需修改此处，改为对应的 Fragment
        HomeMenu.MENU1 -> MainFragment.newInstance()
        HomeMenu.MENU2 -> StatisticsFragment.newInstance()
        HomeMenu.MENU3 -> SettingFragment.newInstance()
        //键盘设备 消费设置接口
        HomeMenu.MENU4 -> Consumption1SettingFragment.newInstance()
        else -> throw NullPointerException()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivWifiState -> {
                if (EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().unregister(this);
                    LogUtils.e("EventBus unregister_打开wifi界面" + this)
                }
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
//            R.id.tvChangeMode -> {
//                EventBus.getDefault().post(MessageEventBean(MessageEventType.ModeMessage, 2))
//            }
        }
    }

    /**
     * 显示更新进度条
     * */
    fun showUpdataProgressDialog(data: CheckAppVersionBean?) {
        LogUtils.e("显示更新进度条")
        val builder = AlertDialog.Builder(this, R.style.app_dialog)
        progressDialog = builder.create()
        progressDialog.setCancelable(false)
        val view = View.inflate(this, R.layout.dialog_updata_version_progress, null)
        tvProgress = view.findViewById<TextView>(R.id.tvProgress)
        sbProgress = view.findViewById<ProgressBar>(R.id.sbProgress)
        sbProgress.isEnabled = false
        /*if ("1" == data?.versionForce) {
            tvCancel.visibility = View.GONE
            progressDialog.setCancelable(false)
        }*/
        // 处理文件名
        // 处理文件名
        progressDialog.show()
        LogUtils.e("显示更新进度条show")
        progressDialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Objects.requireNonNull(progressDialog.window)!!.setContentView(view)

        val path: String = makeDownloadPath()
        UpdateService.Builder.create(data?.url)
            .setStoreDir(path)
            .setIcoResId(R.mipmap.ic_main_logo)
            .setIcoSmallResId(R.mipmap.ic_main_logo)
            .setDownloadSuccessNotificationFlag(Notification.DEFAULT_ALL)
            .setDownloadErrorNotificationFlag(Notification.DEFAULT_ALL)
            .build(this, null)
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

    //接收事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    open fun onEventReceiveMsg(message: MessageEventBean) {
//        Log.e("EventBus_Subscriber", "onReceiveMsg_MAIN: " + message.toString());
        when (message.type) {
            MessageEventType.HeadBeat -> {
                reportDeviceStatusDisposable.dispose()
                initCheckStatus()
            }

            MessageEventType.ProgressNumber -> {
                LogUtils.e("进度条" + message.obj)
//                binding.mainView.visibility = View.VISIBLE
                if (progressDialog != null && progressDialog.isShowing) {
                    tvProgress.text = "" + message.obj
                    sbProgress.progress = message.obj as Int
                }
            }

            MessageEventType.ProgressError -> {
                LogUtils.e("下载失败")
                if (progressDialog != null && progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        reportDeviceStatusDisposable.dispose()
        currentTimeDisposable?.dispose()
        netStatusDisposable?.dispose()
        mPresentation?.dismiss()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    /**
     * 全量下发蒙层
     * */
    fun showAllFsceProgressDialog() {
        LogUtils.e("全量下发蒙层")


        val view = View.inflate(this, R.layout.dialog_all_face_progress, null)

        allFaceDownDialog.show()
        LogUtils.e("显示全量下发蒙层show")
        allFaceDownDialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Objects.requireNonNull(allFaceDownDialog.window)!!.setContentView(view)
    }

    /**
     * 截获按键事件.发给ScanGunKeyEventHelper
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
//        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
//            LogUtils.e("按键MainActivity back")
//            return true
//        }
        LogUtils.e("按键MainActivity keyCode: " + event.keyCode + "  device: " + event.device.name)
        //LogUtils.e("按键MainActivity device vendorId: " + event.device.vendorId + " productId: " + event.device.productId)

        // 判断当前如果是支付状态,处理USB 扫码枪键盘事件
        if (AmountFragment.mIsPaying && (event.keyCode != KeyEvent.KEYCODE_DEL)) {
            keyEventResolver?.analysisKeyEvent(event)
            return true
        }

        if (event.keyCode == KeyEvent.KEYCODE_ENTER || event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            if (event.action == KeyEvent.ACTION_UP) {
                val content = keyEventResolver?.getInputCode(event)
                EventBus.getDefault()
                    .post(MessageEventBean(MessageEventType.KeyEventNumber, content))
            }
            return true
        }
        if (event.keyCode == KeyEvent.KEYCODE_DPAD_UP || event.keyCode == KeyEvent.KEYCODE_DPAD_DOWN || event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT || event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (event.action == KeyEvent.ACTION_UP) {
                val content = keyEventResolver?.getInputCode(event)
                EventBus.getDefault()
                    .post(MessageEventBean(MessageEventType.KeyEventNumber, content))
            }
            return true
        }

        if (event.action == KeyEvent.ACTION_UP) {
            val content = keyEventResolver?.getInputCode(event)
            EventBus.getDefault().post(MessageEventBean(MessageEventType.KeyEventNumber, content))
            LogUtils.e("按键" + content)
        }
        return superDispatchKeyEvent(event)
    }

    fun finishAll() {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        am.restartPackage(packageName)
    }

    /**
     * 刷新系统时间
     */
    private fun refreshSystemDate(formatDateStr: String) {
        try {
            val split = formatDateStr.split(" ")
            if (split.size > 2) {
                binding.tvTime.text = "${split[1]} ${split[2]}"
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
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_ethernet)
            } else {
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_ethernetno)
            }
        } else if (netType == SystemEventHelper.WIFI_NET_TYPE) {
            if (isConnected) {
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi4)
            } else {
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi0)
            }
        } else if (netType == SystemEventHelper.MOBILE_NET_TYPE) {
            if (isConnected) {
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_level_4)
            } else {
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_levelno)
            }
        } else {
            viewDataBinding.ivWifiState.setImageResource(0)
        }
    }

    /**
     * 刷新网络信号状态
     */
    private fun refreshNetworkRssi(netType: Int, isConnect: Boolean, level: Int) {
        if (netType == SystemEventHelper.WIFI_NET_TYPE) {
            if (isConnect) {
                if (level == 0) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi0)
                } else if (level == 1) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi1)
                } else if (level == 2) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi2)
                } else if (level == 3) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi3)
                } else if (level == 4) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi4)
                }
            } else {
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_wifi0)
            }
        } else if (netType == SystemEventHelper.MOBILE_NET_TYPE) {
            if (isConnect) {
                if (level == 0) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_levelno)
                } else if (level == 1) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_level_1)
                } else if (level == 2) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_level_2)
                } else if (level == 3) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_level_3)
                } else if (level == 4) {
                    viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_level_4)
                }
            } else {
                viewDataBinding.ivWifiState.setImageResource(R.mipmap.icon_levelno)
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
                resources.getDrawable(R.drawable.battery_pro_bar_default)
            } else {
                resources.getDrawable(R.drawable.battery_pro_bar_low)
            }
            batteryChargingPro =
                resources.getDrawable(R.drawable.battery_pro_bar_charging)
        }
        if (isCharging) {
            binding.ivBatteryBg.setImageResource(R.mipmap.icon_battery_ischarging);
            binding.pbBattery.progressDrawable = batteryChargingPro
        } else {
            binding.ivBatteryBg.setImageResource(R.mipmap.icon_battery_percent);
            binding.pbBattery.progressDrawable = batteryDefaultPro
        }
        binding.pbBattery.progress = batteryLevel.toInt()
    }

    //系统事件监听
    private val systemEventListener: SystemEventHelper.OnSystemEventListener =
        object : OnSystemEventListener {
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