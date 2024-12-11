package com.stkj.cashier.app.mode

//import com.telpo.tps550.api.idcard.TPS900IDCard
//import com.telpo.tps550.api.idcard.TPS900IDCard.number
//import com.common.face.api.FaceUtil
//import com.telpo.tps550.api.led.Led900

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.king.base.util.SystemUtils
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.adapter.ConsumeRefundListAdapter
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.DifferentDisplay
import com.stkj.cashier.app.main.MainActivity
import com.stkj.cashier.app.base.helper.CommonTipsHelper
import com.stkj.cashier.app.main.DifferentDisplay.Companion.isStartFaceScan
import com.stkj.cashier.app.stat.ConsumeStatFragment
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.AmountFragment580Binding
import com.stkj.cashier.dict.HomeMenu
import com.stkj.cashier.greendao.biz.CompanyMemberBiz
import com.stkj.cashier.util.PriceUtils
import com.stkj.cashier.util.RkSysTool
import com.stkj.cashier.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import mcv.facepass.FacePassException
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class AmountFragment : BaseFragment<ModeViewModel, AmountFragment580Binding>(),
    View.OnClickListener {

    var scanningCode = ""

    private val SPLITE_STRING = " "
    private val PLUS_OPERATE = "+"
    private val MINUS_OPERATE = "-"
    private val MAX_VALUE = BigDecimal(99999999)
    private val MIN_VALUE = BigDecimal(0)
    private val OPERATE_ALL = arrayOf(PLUS_OPERATE, MINUS_OPERATE)

    private var fragment: AmountFragment? = null
    private var mAdapter: ConsumeRefundListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var mIsRefund = false
    private var mPayErrorRetry: Disposable? = null

    private var mRefundName = ""
    private var mCustomerNo = ""
    var mRefundNumber = 0.0// 退款金额
    public fun newInstance(): AmountFragment {

        if (fragment == null) {
            fragment = AmountFragment()
            LogUtils.e("AmountFragment_newInstance第一次创建" + this)
        }
        LogUtils.e("AmountFragment_newInstance" + this)
        return fragment!!
        //return AmountFragment()
    }

    companion object {
        @Volatile
        var mIsPaying = false
    }
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        EventBus.getDefault().post(MessageEventBean(MessageEventType.AmountNotice2))
        LogUtils.e("MessageEventType.AmountNotice2 initData")
        layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        binding.rvRefund.layoutManager = layoutManager//设置布局管理器

        mAdapter = ConsumeRefundListAdapter(viewModel.consumeRefundList.value?.data?.results)
        binding.rvRefund.adapter = mAdapter
        viewModel.consumeRefundList.observe(this) {
            LogUtils.d("consumeRecord observe")
            if (mIsRefund) {
                if (it.code == 10000) {
                    val refundListBean = it.data
                    if (refundListBean != null && !refundListBean.results.isNullOrEmpty()) {
                        val results = refundListBean.results
                        binding.llDefault.visibility = View.GONE
                        binding.llRefundList.visibility = View.VISIBLE
                        mAdapter?.setList(results)
                        val position = mAdapter?.getSelectedPosition()
                        if (position != null && position != -1) {
                            mAdapter?.setSelectedPosition(position)
                        } else {
                            mAdapter?.setSelectedPosition(0)
                        }
                        LogUtils.d("退款订单类别" + results!!.size)

                        binding.tvName.text = "姓名：" + refundListBean.customerName
                        binding.tvNumber.text = "账号/卡号：" + refundListBean.customerNo

                        if (TextUtils.isEmpty(refundListBean.customerImg)) {
                            LogUtils.d("头像空" + refundListBean.customerImg)
                            Glide.with(this).load(R.mipmap.icon_camerapreview_person_3)
                                .into(binding.ivHeader)
                        } else {
                            LogUtils.d("头像" + refundListBean.customerImg)
                            Glide.with(this).load(refundListBean.customerImg)
                                .placeholder(R.mipmap.icon_camerapreview_person_3) // 设置占位图
                                .into(binding.ivHeader)
                        }


                        mRefundName = refundListBean.customerName.toString()

                        LogUtils.d("退款订单类别" + mCustomerNo + "/" + refundListBean.customerNo)
                        if (!TextUtils.isEmpty(refundListBean.customerNo)) {
                            //微信的是空的
                            mCustomerNo = refundListBean.customerNo.toString()
                            LogUtils.d("退款订单类别" + mCustomerNo)
                        }


                        EventBus.getDefault().post(
                            MessageEventBean(
                                MessageEventType.AmountRefundList,
                                Gson().toJson(refundListBean)
                            )
                        )

                        //  AmountRefundList
                    } else {

                        ttsSpeak("没有消费订单")
                        CommonTipsHelper.INSTANCE.setTipsDelayHide("没有消费订单")
                        EventBus.getDefault()
                            .post(MessageEventBean(MessageEventType.AmountRefund))
                    }
                } else {
                    var errorTips = "没有消费订单"
                    if (!it.message.isNullOrEmpty()) {
                        errorTips = it.message!!
                        ttsSpeak(it.message!!)
                    } else {
                        ttsSpeak("没有消费订单")
                    }
                    CommonTipsHelper.INSTANCE.setTipsDelayHide(errorTips)
                    EventBus.getDefault()
                        .post(MessageEventBean(MessageEventType.AmountRefund))
                }
            }
        }

        viewModel.modifyBalance.observe(this) {
            LogUtils.e("modifyBalance observe")

            if (it.code == 10000) {
//                FaceUtil.GPIOSet("rgb_led_en", 0);

                //viewModel.consumeRecordList(map)
//                var voice =
//                    SPUtils.getInstance().getString(Constants.CONSUMPTION_SUCCESS, "消费成功")
                ttsSpeak("支付成功")
                val message = MessageEventBean(
                    MessageEventType.AmountSuccess,
                    it.data?.customerNo,
                    it.data?.consumptionMone
                )
                message.ext = it.data?.balance
                message.realPayMoney = it.data?.consumptionMone.toString()
                EventBus.getDefault().post(message)
                scanningCode = ""


                binding.tvFixAmountModeStatus.text = "支付成功"
                binding.tvFixAmountModeStatus.background = null
                binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#00DC82"))
                binding.tvStatus.text = "支付成功"
                binding.tvStatus.setTextColor(Color.parseColor("#00DC82"))


                Observable.timer(1500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io()) // 在IO调度器上订阅
                    .observeOn(AndroidSchedulers.mainThread()) // 在主线程上观察
                    .subscribe(
                        { aLong: Long? ->
                            // 这里的代码会在3秒后执行一次

                            mIsPaying = false

                            binding.tvFixAmountModeStatus.text = "定额模式"
                            binding.tvFixAmountModeStatus.setBackgroundResource(R.drawable.bg_select_checkbox_selected)
                            binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#00DC82"))
                            binding.tvStatus.text = "-"
                            binding.tvStatus.setTextColor(Color.parseColor("#ffffff"))

                            //定额模式自定去支付
                            if (isCurrentFixAmountMode) {
                                goToFixAmountPay()
                            } else {
                                binding.tvAmount.text = ""
                            }

                        }
                    ) { throwable: Throwable? ->
                        // 当发生错误时，这里的代码会被执行
                        Log.e("RxJava", "Error", throwable)
                    }
            } else if (it.code == 10009) {
                //支付中
                LogUtils.e("支付中")
                it.data?.payNo?.let { it1 -> getPayStatus(it1) }
            } else {
                var errorMsg = "请重新支付"
                if (!it.message.isNullOrEmpty()) {
                    ttsSpeak(it.message!!)
                    errorMsg = it.message!!
                } else {
                    ttsSpeak("支付失败")
                }
                if (it.code == 10010) {
                    errorMsg = "余额不足|" + it.data?.balance
                }
                val message = MessageEventBean(
                    MessageEventType.AmountError,
                    errorMsg
                )
                EventBus.getDefault().post(message)

                if (it.code == 10010) {
                    binding.tvFixAmountModeStatus.text = "余额不足"
                    binding.tvFixAmountModeStatus.background = null
                    binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#FA5151"))
                    binding.tvStatus.text = "余额不足"
                    binding.tvStatus.setTextColor(Color.parseColor("#FA5151"))
                } else {
                    binding.tvFixAmountModeStatus.text = "支付失败"
                    binding.tvFixAmountModeStatus.background = null
                    binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#FA5151"))
                    binding.tvStatus.text = "支付失败"
                    binding.tvStatus.setTextColor(Color.parseColor("#FA5151"))
                }

                mPayErrorRetry = Observable.timer(1500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io()) // 在IO调度器上订阅
                    .observeOn(AndroidSchedulers.mainThread()) // 在主线程上观察
                    .subscribe(
                        { aLong: Long? ->

                            mPayErrorRetry = null
                            scanningCode = ""

                            //当前非定额模式并且金额为零时，直接取消支付，否则了可以继续支付
                            if (isInvalidAmountMoney()) {
                                cancelAmountPay()
                            } else {
                                binding.tvFixAmountModeStatus.text = "定额模式"
                                binding.tvFixAmountModeStatus.setBackgroundResource(R.drawable.bg_select_checkbox_selected)
                                binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#00DC82"))
                                binding.tvStatus.text = "支付中"
                                binding.tvStatus.setTextColor(Color.parseColor("#FF8F1F"))

                                sendConsumerModifyBalanceNotice()
                            }
                        }
                    ) { throwable: Throwable? ->
                        // 当发生错误时，这里的代码会被执行
                        Log.e("RxJava", "Error", throwable)
                    }
            }

        }

        viewModel.consumeRefundResult.observe(this) {
            LogUtils.d("consumeRefundResult observe")
            if (it.code == 10000) {
                if (!it.message.isNullOrEmpty()) {
                    ttsSpeak(it.message!!)
                    //ToastUtils.showLong(it.message)
                }
                if (mIsRefund) {
                    binding.rlRefundConfirm.visibility = View.VISIBLE
                    //已退回至张志诚的账户
                    binding.tvRefundName1.text = "已退回至"
                    binding.tvRefundName.text = mRefundName
                    binding.tvRefundName2.text = "的账户"
                    binding.llSureCancel.visibility = View.GONE

                    LogUtils.e("退款成功" + mRefundNumber)
                    EventBus.getDefault().post(
                        MessageEventBean(
                            MessageEventType.AmountRefundSuccess,
                            "" + mRefundNumber
                        )
                    )


                    Observable.timer(3, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io()) // 在IO调度器上订阅
                        .observeOn(AndroidSchedulers.mainThread()) // 在主线程上观察
                        .subscribe(
                            { aLong: Long? ->
                                // 这里的代码会在3秒后执行一次
                                //是否为张志诚进行该笔退款？
                                binding.tvRefundName1.text = "是否为"
                                binding.tvRefundName2.text = "进行该笔退款？"
                                binding.llSureCancel.visibility = View.VISIBLE
                                binding.rlRefundConfirm.visibility = View.GONE
                                LogUtils.e("退款列表" + mCustomerNo)
                                searchConsumeRefundList(mCustomerNo)

                                mRefundNumber = 0.0
                            }
                        ) { throwable: Throwable? ->
                            // 当发生错误时，这里的代码会被执行
                            Log.e("RxJava", "Error", throwable)
                        }

                    if (!it.message.isNullOrEmpty() && it.message.equals("退款中")) {
                        //微信退款二次查询
                        Observable.timer(20, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io()) // 在IO调度器上订阅
                            .observeOn(AndroidSchedulers.mainThread()) // 在主线程上观察
                            .subscribe(
                                { aLong: Long? ->
                                    // 这里的代码会在3秒后执行一次
                                    //是否为张志诚进行该笔退款？
                                    binding.tvRefundName1.text = "是否为"
                                    binding.tvRefundName2.text = "进行该笔退款？"
                                    binding.llSureCancel.visibility = View.VISIBLE
                                    binding.rlRefundConfirm.visibility = View.GONE
                                    LogUtils.e("退款列表" + mCustomerNo)
                                    searchConsumeRefundList(mCustomerNo)

                                    mRefundNumber = 0.0
                                }
                            ) { throwable: Throwable? ->
                                // 当发生错误时，这里的代码会被执行
                                Log.e("RxJava", "Error", throwable)
                            }
                    }
                }
            } else {
                if (!it.message.isNullOrEmpty()) {
                    ttsSpeak(it.message!!)
                } else {
                    ttsSpeak("退款失败")
                }
            }

        }

        //金额文字监听
        binding.tvAmount.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                val svAmountHeight: Int = binding.svAmount.getHeight()
                val tvAmountHeight = bottom - top
                LogUtils.e("高" + top + "/" + bottom)
                if (svAmountHeight > 0 && tvAmountHeight > svAmountHeight) {
                    Choreographer.getInstance()
                        .postFrameCallback(object : Choreographer.FrameCallback {
                            override fun doFrame(frameTimeNanos: Long) {
                                binding.svAmount.fullScroll(FOCUS_DOWN)
                            }
                        })
                }
            }
        })

        CommonTipsHelper.INSTANCE.setMainTipsView(binding.ctvAmount)
    }


    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        CommonTipsHelper.INSTANCE.setMainTipsView(null)
    }

    override fun onStop() {
        super.onStop()
    }


    override fun hideLoading() {
        super.hideLoading()

    }

    override fun getLayoutId(): Int {
        return R.layout.amount_fragment_580
    }

    private fun modifyBalanceByFaceToken(faceToken: String) {
        var companyMember = CompanyMemberBiz.getCompanyMember(faceToken)
        if (companyMember != null) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = companyMember.cardNumber
            map["consumption_type"] = 10
            map["deduction_Type"] = 60
            /* val randoms = (1000..9999).random().toString()
             var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                 System.currentTimeMillis(),
                 "yyyyMMddHHmmss"
             ) + randoms*/
            var onlineOrderNumber = createOrderNumber()
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = getRealPayMoney()
//            map["online_Order_number"] = "202012211032"
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    companyMember.cardNumber + "&60&" + App.serialNumber + "&" + getRealPayMoney()
                            + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)

        }


    }

    private fun modifyBalanceByCard(card: String) {
        var companyMember = CompanyMemberBiz.getCompanyMemberByCard(card)
        if (companyMember != null && !TextUtils.isEmpty(companyMember.cardNumber)) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = companyMember.cardNumber
            map["consumption_type"] = 20
            map["deduction_Type"] = 60

            /* val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms*/
            var onlineOrderNumber = createOrderNumber()
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = getRealPayMoney()
//            map["online_Order_number"] = "202012211032"
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    companyMember.cardNumber + "&60&" + App.serialNumber + "&" + getRealPayMoney()
                            + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)

        } else if (card != null) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = card
            map["consumption_type"] = 20
            map["deduction_Type"] = 60
            /* val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms*/
            var onlineOrderNumber = createOrderNumber()
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = getRealPayMoney()
//            map["online_Order_number"] = "202012211032"
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    card + "&60&" + App.serialNumber + "&" + getRealPayMoney()
                            + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)
        } else {
            ttsSpeak("无信息")
            isStartFaceScan.set(true)
        }


    }

    public fun modifyBalanceByScanCode(card: String) {
        if (!TextUtils.isEmpty(card)) {
            var card = card.replace("\r", "")
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = card
            map["consumption_type"] = 30
            map["deduction_Type"] = 60
            /* val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms*/
            var onlineOrderNumber = createOrderNumber()
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = getRealPayMoney()
//            map["online_Order_number"] = "202012211032"

            LogUtils.e(
                card + "&60&" + App.serialNumber + "&" + getRealPayMoney()
                    .trim() + "&" + onlineOrderNumber
            )
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    card + "&60&" + App.serialNumber + "&" + getRealPayMoney()
                        .trim() + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)
            scanningCode = card
        } else if (TextUtils.isEmpty(card)) {
            ttsSpeak("无信息")
        }


    }

    private fun getPayStatus(payNo: String) {
        Observable.timer(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aLong ->
                if (!TextUtils.isEmpty(payNo)) {
                    var card = payNo.replace("\r", "")
                    var map = hashMapOf<String, Any>()
                    map["mode"] = "PayStatus"
                    map["payNo"] = card
                    var md5 =
                        EncryptUtils.encryptMD5ToString16(card)
                    map["sign"] = md5
                    viewModel.payStatus(map)
                }
            }
        /*timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

            }
        }, 1000, 1000)*/
    }

    /**
     * 查询退款订单列表(通过人脸token)
     * */
    private fun handelRefundByFaceToken(faceToken: String) {
        var companyMember = CompanyMemberBiz.getCompanyMember(faceToken)
        //LogUtils.e("查询退款订单列表" + companyMember.cardNumber)
        if (companyMember != null && !TextUtils.isEmpty(companyMember.cardNumber)) {
            searchConsumeRefundList(companyMember.cardNumber)
        } else {
            ttsSpeak("用户不存在")
        }
    }

    /**
     * 查询退款订单列表
     * */
    private fun searchConsumeRefundList(card: String) {
        LogUtils.e("查询退款订单列表 卡号: " + card)
        var map = hashMapOf<String, Any>()
        map["mode"] = "RefundOrderList"
        map["machine_Number"] = App.serialNumber
        map["cardNumber"] = card
        map["pageIndex"] = 1
        map["pageSize"] = 5000
        var md5 =
            EncryptUtils.encryptMD5ToString16(card + "&" + App.serialNumber + "&1&5000")
        map["sign"] = md5
        viewModel.consumeRefundList(map)
    }

    /**
     * 订单退款
     * */
    private fun RefundOrder(payNo: String, billType: Int) {
        LogUtils.e("订单退款")
        var map = hashMapOf<String, Any>()
        map["mode"] = "RefundOrder"
        map["machine_Number"] = App.serialNumber
        map["billId"] = payNo
        map["consumption_type"] = billType
        var md5 = EncryptUtils.encryptMD5ToString16(payNo + "&" + App.serialNumber)
        map["sign"] = md5
        viewModel.consumeRefundResult(map)
    }

    private fun modifyBalanceNotice() {
        sendConsumerModifyBalanceNotice()
//        ttsSpeak("请支付")
    }

    /**
     * 判断金额模式 支付金额是否不合法
     */
    private fun isInvalidAmountMoney(): Boolean {
        return !isCurrentFixAmountMode && (binding.tvAmount.text.toString()
            .isEmpty()
                || binding.tvAmount.text.toString().toFloat() == 0f)
    }

    /**
     * 获取支付金额
     */
    private fun getRealPayMoney(): String {
        if (isCurrentFixAmountMode) {
            return currentFixAmountMoney
        } else {
            val price = binding.tvAmount.text.toString();
            return PriceUtils.formatPrice2(price)
        }
    }

    /**
     * 副屏金额提示
     */
    private fun sendConsumerModifyBalanceNotice(){
        if (isCurrentFixAmountMode) {
            val canteenType = binding.tvTitle.text.toString()
            val canteenMoney = binding.tvFixAmountModeMoney.text.toString()
            EventBus.getDefault()
                .post(MessageEventBean(MessageEventType.AmountNotice3,
                    "$canteenType: $canteenMoney"
                ))
        } else {
            EventBus.getDefault()
                .post(MessageEventBean(MessageEventType.AmountNotice, getRealPayMoney()))
        }
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        when (message.type) {
            MessageEventType.AmountToken -> {
                message.content?.let {
                    if (mIsRefund) {
                        handelRefundByFaceToken(it)
                    } else {
                        if (isInvalidAmountMoney()) {
                            binding.tvStatus.text = "-"
                            ttsSpeak("请重新输入消费金额")
                            EventBus.getDefault()
                                .post(MessageEventBean(MessageEventType.AmountNotice2))
                            return
                        }
                        refreshPayingStatus()
                        modifyBalanceByFaceToken(it)
                    }
                }
            }

            MessageEventType.AmountCard -> {
                message.content?.let {
                    if (mIsRefund) {
                        searchConsumeRefundList(it)
                    } else {
                        if (isInvalidAmountMoney()) {
                            binding.tvStatus.text = "-"
                            ttsSpeak("请重新输入消费金额")
                            EventBus.getDefault()
                                .post(MessageEventBean(MessageEventType.AmountNotice2))
                            return
                        }
                        refreshPayingStatus()
                        modifyBalanceByCard(it)
                    }
                }
            }

            MessageEventType.AmountScanCode -> {
                LogUtils.e("金额模式扫码scanningCode" + scanningCode)
                message.content?.let {
                    if (mIsRefund) {
                        searchConsumeRefundList(it)
                        //微信用户
                        mCustomerNo = it
                    } else {
                        if (isInvalidAmountMoney()) {
                            binding.tvStatus.text = "-"
                            ttsSpeak("请重新输入消费金额")
                            EventBus.getDefault()
                                .post(MessageEventBean(MessageEventType.AmountNotice2))
                            return
                        }
                        if (TextUtils.isEmpty(scanningCode) || !TextUtils.equals(scanningCode,it)) {
                            refreshPayingStatus()
                            modifyBalanceByScanCode(it)
                        }
                    }
                }
            }

            MessageEventType.AmountCancel -> {
                LogUtils.e("金额模式 取消支付")
                // ToastUtils.showShort("金额模式")
                LogUtils.e("MessageEventType.AmountNotice2 AmountCancel")
                //定额模式不取消支付
                if (isCurrentFixAmountMode) {
                    goToFixAmountPay()
                } else {
                    cancelAmountPay()
                }
            }

            MessageEventType.KeyEventNumber -> {
                //LogUtils.e("金额模式 按键")
                if (!SPUtils.getInstance().getBoolean(Constants.FRAGMENT_SET, false)&&!App.mShowConsumeStat) {
                    message.content?.let {
                        LogUtils.e("金额模式 按键" + it)
                        onKeyEvent3(it)
                    }
                }
            }

            MessageEventType.RefreshFixAmountMode -> {
                LogUtils.e("刷新定额模式")
                //刷新定额模式
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        refreshCurrentFixAmount()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            MessageEventType.OpenFixAmountMode -> {
                LogUtils.e("打开定额模式")
                //打开定额模式
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        refreshCurrentFixAmount()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            MessageEventType.CloseFixAmountMode -> {
                LogUtils.e("关闭定额模式")
                //关闭定额模式
                closeCurrentFixAmount()
            }

            MessageEventType.IntervalCardType -> {
                LogUtils.e("刷新定额模式--获取时间段信息")
                val switchFixAmount = SPUtils.getInstance().getBoolean(Constants.SWITCH_FIX_AMOUNT, false);
                if (switchFixAmount) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            refreshCurrentFixAmount()
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //当前是定额模式
    private var isCurrentFixAmountMode:Boolean = false
    private var currentFixAmountMoney = ""

    private fun closeCurrentFixAmount() {
        //如果正在退款,取消退款 start
        if (mIsRefund) {
            hideRefundList()
            //setEnable(true)
        }
        //如果正在退款,取消退款 end

        //清理当前支付状态 start
        cancelAmountPay()
        isCurrentFixAmountMode = false
        currentFixAmountMoney = ""
        binding.tvFixAmountModeStatus.text = "定额模式"
        binding.tvFixAmountModeStatus.setBackgroundResource(R.drawable.bg_select_checkbox_selected)
        binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#00DC82"))
        //清理当前支付状态 end
        //显示默认金额状态
        binding.tvTitle.text = "当前订单"
        binding.svAmount.visibility = VISIBLE
        binding.flAmount.visibility = VISIBLE
        binding.tvStatus.visibility = VISIBLE
        binding.tvFixAmountModeMoney.visibility = GONE
        binding.tvFixAmountModeStatus.visibility = GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshCurrentFixAmount() {
        val intervalCardType = App.intervalCardType
        var currentFixAmount = ""
        var currentCanteenType = ""
        var currentCanteenTime = ""
        if (intervalCardType.isNotEmpty()) {
            val cardTypeBean = intervalCardType[0]
            val oneTime = cardTypeBean.oneTime
            if (!TextUtils.isEmpty(oneTime)) {
                val split = oneTime!!.split("-")
                if (split.size >= 2) {
                    if (TimeUtils.isCurrentTimeIsInRound(split[0], split[1])) {
                        val breakfastSwitch =
                            SPUtils.getInstance().getBoolean(Constants.BREAKFAST_SWITCH, true)
                        if (breakfastSwitch) {
                            currentCanteenTime = oneTime
                            currentCanteenType = "早餐"
                            currentFixAmount =
                                SPUtils.getInstance().getString(Constants.BREAKFAST_AMOUNT, "")
                        }
                    }
                }
            }
            val twoTime = cardTypeBean.twoTime
            if (!TextUtils.isEmpty(twoTime)) {
                val split = twoTime!!.split("-")
                if (split.size >= 2) {
                    if (TimeUtils.isCurrentTimeIsInRound(split[0], split[1])) {
                        val lunchSwitch =
                            SPUtils.getInstance().getBoolean(Constants.LUNCH_SWITCH, true)
                        if (lunchSwitch) {
                            currentCanteenTime = twoTime
                            currentCanteenType = "午餐"
                            currentFixAmount =
                                SPUtils.getInstance().getString(Constants.LUNCH_AMOUNT, "")
                        }
                    }
                }
            }
            val threeTime = cardTypeBean.threeTime
            if (!TextUtils.isEmpty(threeTime)) {
                val split = threeTime!!.split("-")
                if (split.size >= 2) {
                    if (TimeUtils.isCurrentTimeIsInRound(split[0], split[1])) {
                        val dinnerSwitch =
                            SPUtils.getInstance().getBoolean(Constants.DINNER_SWITCH, true)
                        if (dinnerSwitch) {
                            currentCanteenTime = threeTime
                            currentCanteenType = "晚餐"
                            currentFixAmount =
                                SPUtils.getInstance().getString(Constants.DINNER_AMOUNT, "")
                        }
                    }
                }
            }
        }
        //判断定额模式金额
        val realPayMoney = currentFixAmount.toDoubleOrNull()
        LogUtils.e("定额模式 realPayMoney = $realPayMoney")
        //清理之前的金额数据
        closeCurrentFixAmount()
        if (realPayMoney != null && realPayMoney > 0) {
            //隐藏默认金额状态
            binding.svAmount.visibility = GONE
            binding.tvStatus.visibility = GONE
            binding.tvFixAmountModeMoney.visibility = VISIBLE
            binding.tvFixAmountModeStatus.visibility = VISIBLE

            //设置当前为定额模式
            isCurrentFixAmountMode = true
            //设置当前定额模式时间
            SPUtils.getInstance().put(Constants.CURRENT_FIX_AMOUNT_TIME, currentCanteenTime)
            //输入定额模式金额
            binding.tvTitle.text = currentCanteenType
            val realPayMoneyStr = realPayMoney.toString();
            currentFixAmountMoney = realPayMoneyStr
            SpanUtils.with(binding.tvFixAmountModeMoney)
                .append(realPayMoneyStr)
                .append("元/份")
                .setFontSize(40, true)
                .create()
            binding.tvFixAmountModeMoney.visibility = VISIBLE
            binding.tvAmount.text = realPayMoneyStr
            goToFixAmountPay()
        } else {
            //设置当前定额模式时间
            SPUtils.getInstance().put(Constants.CURRENT_FIX_AMOUNT_TIME, "")
        }
    }

    /**
     * 刷新支付中状态显示
     */
    private fun refreshPayingStatus() {
        binding.tvFixAmountModeStatus.text = "支付中"
        binding.tvFixAmountModeStatus.background = null
        binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#FF8F1F"))
        binding.tvStatus.text = "支付中"
        binding.tvStatus.setTextColor(Color.parseColor("#FF8F1F"))
        EventBus.getDefault()
            .post(MessageEventBean(MessageEventType.AmountPayingNotice))
    }

    /**
     * 定额模式支付
     */
    private fun goToFixAmountPay() {
        mIsPaying = true
        modifyBalanceNotice()

        binding.tvStatus.text = "支付中"
        binding.tvStatus.setTextColor(Color.parseColor("#FF8F1F"))
    }

    /**
     * 取消支付
     */
    private fun cancelAmountPay() {
        if (mPayErrorRetry != null) {
            mPayErrorRetry!!.dispose()
            mPayErrorRetry = null
        }
        isStartFaceScan.set(false)
        EventBus.getDefault()
            .post(MessageEventBean(MessageEventType.AmountNotice2))
        LogUtils.e("MessageEventType.AmountNotice2 cancelAmountPay")
        binding.tvAmount.text = ""
        binding.tvStatus.text = "-"
        binding.tvStatus.setTextColor(Color.parseColor("#ffffff"))
        binding.tvFixAmountModeStatus.text = "定额模式"
        binding.tvFixAmountModeStatus.setBackgroundResource(R.drawable.bg_select_checkbox_selected)
        binding.tvFixAmountModeStatus.setTextColor(Color.parseColor("#00DC82"))
        mIsPaying = false
    }

    /**
     * 显示退款提示列表
     */
    private fun showRefundList() {
        val switchFacePassPay =
            SPUtils.getInstance().getBoolean(Constants.SWITCH_FACE_PASS_PAY)
        if (switchFacePassPay) {
            ttsSpeak("请用户刷脸或刷卡，以确认可退款订单")
            binding.tvTitle.text = "请刷脸或刷卡，以确认可退款订单"
        } else {
            ttsSpeak("请用户刷卡，以确认可退款订单")
            binding.tvTitle.text = "请刷卡，以确认可退款订单"
        }

        binding.llDefault.visibility = View.VISIBLE
        binding.tvAmount.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE
        binding.tvFixAmountModeStatus.visibility = View.GONE
        binding.svAmount.visibility = View.GONE
        binding.flAmount.visibility = View.GONE

        mIsRefund = true
        EventBus.getDefault()
            .post(MessageEventBean(MessageEventType.AmountRefund))
    }

    /**
     * 隐藏退款提示列表
     */
    private fun hideRefundList() {
        mIsRefund = false
        binding.rvRefund.scrollToPosition(0)
        binding.llRefundList.visibility = View.GONE
        binding.rlRefundConfirm.visibility = View.GONE
        mAdapter?.setList(null)
        mAdapter?.setSelectedPosition(-1)
        binding.tvTitle.text = "当前订单"
        binding.llDefault.visibility = View.VISIBLE
        binding.tvAmount.visibility = View.VISIBLE
        binding.svAmount.visibility = View.VISIBLE
        binding.flAmount.visibility = View.VISIBLE
        binding.tvStatus.visibility = View.VISIBLE
        EventBus.getDefault()
            .post(MessageEventBean(MessageEventType.AmountRefundCancel))
    }

    fun onKeyEvent3(inputCode: String?): Boolean {
        if (!TextUtils.isEmpty(inputCode)) {
            when (inputCode) {
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9"
                -> {
                    //判断定额模式
                    if (isCurrentFixAmountMode) {
                        ttsSpeak("定额模式下不可操作")
                        return false
                    }
                    if (!mIsRefund && !mIsPaying) {
                        inputNumber(inputCode)
                        binding.tvStatus.text = "输入中"
                        binding.tvStatus.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }

                "+"
                -> {
                    //判断定额模式
                    if (isCurrentFixAmountMode) {
                        ttsSpeak("定额模式下不可操作")
                        return false
                    }
                    if (!mIsRefund && !mIsPaying) {
                        inputOperate(PLUS_OPERATE)
                        binding.tvStatus.setTextColor(Color.parseColor("#FFFFFF"))
                        binding.tvStatus.text = "输入中"
                    }
                }

                "-"
                -> {
                    //判断定额模式
                    if (isCurrentFixAmountMode) {
                        ttsSpeak("定额模式下不可操作")
                        return false
                    }
                    if (!mIsRefund && !mIsPaying) {
                        inputOperate(MINUS_OPERATE)
                        binding.tvStatus.setTextColor(Color.parseColor("#FFFFFF"))
                        binding.tvStatus.text = "输入中"
                    }
                }

                "." -> {
                    //判断定额模式
                    if (isCurrentFixAmountMode) {
                        ttsSpeak("定额模式下不可操作")
                        return false
                    }
                    if (!mIsRefund && !mIsPaying) {
                        inputSpot();
                        binding.tvStatus.setTextColor(Color.parseColor("#FFFFFF"))
                        binding.tvStatus.text = "输入中"
                    }
                }


                "确认" -> {
                    if (mIsRefund) {
                        if (mAdapter?.data?.size!! > 0) {
                            val selectedItem = mAdapter?.getSelectedItem()
                            LogUtils.e("退款项")
                            if (selectedItem != null) {
                                if (binding.rlRefundConfirm.visibility == View.VISIBLE) {
                                    selectedItem.billId?.let {
                                        selectedItem.billType?.let { it1 ->
                                            mRefundNumber = selectedItem.billFee
                                            ttsSpeak("确认")
                                            RefundOrder(
                                                it,
                                                it1
                                            )
                                            binding.rlRefundConfirm.visibility = View.GONE


                                        }
                                    }
                                } else {
                                    binding.rlRefundConfirm.visibility = View.VISIBLE
                                    binding.tvRefundName.text = mRefundName
                                }

                            }
                        }
                    } else {
                        //判断定额模式
                        if (isCurrentFixAmountMode) {
                            ttsSpeak("定额模式下不可操作")
                            return false
                        }
                        //判断网络
                        val connected = NetworkUtils.isConnected()
                        LogUtils.e("判断网络" + connected + "/" + SystemUtils.isNetWorkActive(getApp()))
                        // ToastUtils.showShort("判断网络"+connected+"/"+SystemUtils.isNetWorkActive(getApp()))
                        if (binding.tvAmount.text.toString().equals("88888888")) {
                            //activity?.finishAffinity() // 结束当前任务中的所有活动
                            RkSysTool.getInstance().setNavitionBar(true)
//                            var mainActivity = activity as MainActivity
//                            mainActivity.finishAll()
//                            //
//                            Process.killProcess(Process.myPid())
                            System.exit(0);
                        } else if (!SystemUtils.isNetWorkActive(getApp())) {
                            //ttsSpeak(getString(R.string.result_network_unavailable_error))
                            ttsSpeak("网络已断开，请检查网络。")
                        } else {
                            if (binding.tvAmount.text.toString().equals("99999999")) {
                                binding.tvAmount.text = ""
                                binding.tvStatus.text = "-"

                                // 下载人脸
                                var mainActivity = activity as MainActivity
                                Thread(Runnable {
                                    App.mFacePassHandler?.clearAllGroupsAndFaces()
                                    createGroup()
                                    CompanyMemberBiz.deleteRelationAll()
                                    dismissProgressDialog()
                                    // 下载人脸
                                    mainActivity.callBack = true
                                    LogUtils.e("lime== 全量更新弹窗")
                                    mainActivity.companyMember(0)

                                }).start()

                            } else if (!mIsPaying) {
                                //Led900(activity)?.on(100)
//                                FaceUtil.GPIOSet("rgb_led_en", 1);
                                ttsSpeak("确认")
                                confirmOperateNumberResult()
                                LogUtils.e("金额模式 按键" + binding.tvAmount.text)

                                if (binding.tvAmount.text.toString().isEmpty()
                                    || binding.tvAmount.text.toString().toFloat() == 0f
                                ) {
                                    ttsSpeak("请输入消费金额")
                                    return false
                                }
                                //binding.tvAmount.text = decimalFormat.format(binding.tvAmount.text).toString()

                                mIsPaying = true
                                modifyBalanceNotice()

                                binding.tvStatus.text = "支付中"
                                binding.tvStatus.setTextColor(Color.parseColor("#FF8F1F"))

                            } else {
                                ttsSpeak("支付中,请稍等")
//                        EventBus.getDefault().post(MessageEventBean(MessageEventType.AmountNotice2))
//                        setEnable(true)
                            }
                        }
                    }

                }

                "删除" -> {
                    if (mIsRefund) {
                        hideRefundList()
                        //退款后自动切换到定额模式
                        if (isCurrentFixAmountMode){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                refreshCurrentFixAmount()
                            }
                        }
                        //setEnable(true)
                    } else {
                        //判断定额模式
                        if (isCurrentFixAmountMode) {
                            ttsSpeak("定额模式下不可操作")
                            return false
                        }
                        if (!mIsPaying) {
                            if (binding.tvAmount.text.isNotEmpty()) {
                                deleteInput();
                                ttsSpeak("删除")
                                /*  binding.tvAmount.text = input.substring(0, input.length - 1)
                                  EventBus.getDefault().post(MessageEventBean(MessageEventType.AmountNotice2))
                                  setEnable(true)*/
                            }
                        } else {
                            //                        FaceUtil.GPIOSet("rgb_led_en", 0);
                            cancelAmountPay()
                        }
                    }
                }

//                "取消" -> {
//                    ttsSpeak("取消")
//                    if (mIsRefund) {
//                        hideRefundList()
//                        //退款后自动切换到定额模式
//                        if (isCurrentFixAmountMode){
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                refreshCurrentFixAmount()
//                            }
//                        }
//                        //setEnable(true)
//                    } else {
//                        //判断定额模式
//                        if (isCurrentFixAmountMode) {
//                            ttsSpeak("定额模式下不可操作")
//                            return false
//                        }
////                        FaceUtil.GPIOSet("rgb_led_en", 0);
//                        cancelAmountPay()
//                    }
//
//                }

                "取消" -> {
                    LogUtils.e("取消" + DifferentDisplay.isStartFaceScan + "/" + mIsRefund)
                    if (NetworkUtils.isConnected()) {
                        if (isCurrentFixAmountMode) {
                            //退款中
                            if (mIsRefund) {
                                ttsSpeak("请先点删除键，再点取消键")
                                return false
                            }
                            if (mIsPaying) {
                                cancelAmountPay()
                            }
                            showRefundList()
                        } else {
                            if (!mIsRefund && !mIsPaying) {
                                showRefundList()
                            } else {
                                ttsSpeak("请先点取清除键，再点功能键")
                            }
                        }

                    } else {
                        ttsSpeak("网络已断开，请检查网络。")
                    }

                }

                "向上" -> {
                    //ttsSpeak("向上")
                    if (!mIsRefund) {
                        //上下键操作
                        val offset = binding.tvAmount.getPaint().getTextSize()
                        LogUtils.e("向上" + (-offset).toInt())
                        binding.svAmount.scrollBy(0, (-offset).toInt())

                        LogUtils.e(
                            "向下getScrollY" + binding.svAmount.getScrollY() + "/" + binding.svAmount.getChildAt(
                                0
                            ).getHeight() + "/" + binding.svAmount.getHeight()
                        )
                        // binding.svAmount.scrollBy(0, (-(offset+50)).toInt())


                        //binding.svAmount.fullScroll(ScrollView.FOCUS_UP);

                        /*val currentScrollY: Int = binding.svAmount.getScrollY()
                        LogUtils.e("向上currentScrollY" + currentScrollY)
                        if (currentScrollY > 0) {
                            val scrollDistance = Math.min(100, currentScrollY)
                            binding.svAmount.scrollBy(0, -scrollDistance)
                        } else {
                            binding.svAmount.scrollTo(0, 0)
                            binding.svAmount.fullScroll(ScrollView.FOCUS_UP)
                        }
                        binding.svAmount.smoothScrollTo(0, 0)*/
                        //binding.svAmount.fullScroll(ScrollView.FOCUS_UP)
                    } else {
                        if (mAdapter != null && mAdapter?.data?.size!! > 0) {
                            LogUtils.e("向上" + mAdapter?.getSelectedPosition())
                            if (mAdapter?.getSelectedPosition()!! > 0) {
                                val position = mAdapter?.getSelectedPosition()!! - 1
                                mAdapter?.setSelectedPosition(position)

                                layoutManager?.scrollToPosition(position)
                                EventBus.getDefault().post(
                                    MessageEventBean(
                                        MessageEventType.AmountRefundListSelect,
                                        position
                                    )
                                )
                            }
                        }
                    }

                }

                "向下" -> {
                    //ttsSpeak("向下")
                    if (!mIsRefund) {
                        //上下键操作
                        // 获取ScrollView可以滚动的总距离，这通常是子视图的完整高度

                        // 获取ScrollView可以滚动的总距离，这通常是子视图的完整高度
                        // val scrollRange: Int =  binding.svAmount.computeVerticalScrollRange()
                        // LogUtils.e("向下" + scrollRange)


                        val offset = binding.tvAmount.getPaint().getTextSize()
                        LogUtils.e("向下" + offset)
                        binding.svAmount.scrollBy(0, offset.toInt())

                        /* val currentScrollY: Int = binding.svAmount.getScrollY()
                         val scrollRange: Int =
                             binding.svAmount.getChildAt(0)
                                 .getHeight() - binding.svAmount.getHeight()
                         LogUtils.e(
                             "向下currentScrollY" + currentScrollY + "/" + binding.svAmount.getChildAt(
                                 0
                             ).getHeight() + "/" + binding.svAmount.getHeight()
                         )
                         if (currentScrollY < scrollRange) {
                             val scrollDistance = Math.min(100, scrollRange - currentScrollY)
                             binding.svAmount.scrollBy(0, scrollDistance)
                         } else {
                             binding.svAmount.fullScroll(ScrollView.FOCUS_DOWN)
                         }*/
                    } else {
                        if (mAdapter != null && mAdapter?.data?.size!! > 0) {
                            LogUtils.e("向下" + mAdapter?.getSelectedPosition())

                            mAdapter?.getSelectedPosition()?.plus(1)
                                ?.let {
                                    if (it < mAdapter?.itemCount!!) {
                                        mAdapter?.setSelectedPosition(it)
                                        layoutManager?.scrollToPosition(it)
                                        EventBus.getDefault().post(
                                            MessageEventBean(
                                                MessageEventType.AmountRefundListSelect,
                                                it
                                            )
                                        )
                                    }
                                }
                        }
                    }
                }

                "设置",
                -> {
                    if (mIsRefund) {
                        return false
                    }
                    if (!SPUtils.getInstance().getBoolean(Constants.FRAGMENT_SET, false)) {
                        App.mMainActivity?.showFragment(HomeMenu.MENU4)
                        SPUtils.getInstance().put(Constants.FRAGMENT_SET, true)
                    }
                }
                "统计",
                -> {
                    //退款中
                    if (mIsRefund) {
                        ttsSpeak("请先点取清除键，再点查询键")
                        return false
                    }
                    if (!App.mShowConsumeStat) {
                        App.mShowConsumeStat = true
                        App.mMainActivity?.showPlaceHolderFragment(ConsumeStatFragment.newInstance())
                    }
                }
            }
        }

        return true

    }

    /**
     * 获取当前输入的数值
     */
    private fun getCurrentInputText(): String {
        return binding.tvAmount.getText().toString()
    }

    private fun confirmOperateNumberResult() {
        val currentInputText = getCurrentInputText()
        if (TextUtils.isEmpty(currentInputText)) {
            return
        }
        //分割最后的输入的数据
        val splitInput: Array<String> = currentInputText.split(SPLITE_STRING).toTypedArray()
        if (splitInput.size > 2) {
            //获取最后一个公式
            val lastOperateNumber = splitInput[splitInput.size - 1]
            //如果最后是操作符则不进行运算
            var needOperateLength = 0
            needOperateLength = if (isOperate(lastOperateNumber)) {
                splitInput.size - 1
            } else {
                splitInput.size
            }
            val lastFormulaBuilder = StringBuilder()
            var tempLastResult: String? = ""
            var tempLastOperate: String? = ""
            for (i in 0 until needOperateLength) {
                val s = splitInput[i]
                val pointIndex = s.indexOf(".")
                if (pointIndex != -1 && pointIndex + 1 == s.length) {
                    lastFormulaBuilder.append(s.substring(0, pointIndex)).append(SPLITE_STRING)
                } else {
                    lastFormulaBuilder.append(s).append(SPLITE_STRING)
                }
                if (!isOperate(s)) {
                    if (TextUtils.isEmpty(tempLastResult)) {
                        //第一次赋值result
                        tempLastResult = s
                        continue
                    }
                    if (!TextUtils.isEmpty(tempLastOperate)) {
                        //有操作符去计算结果
                        try {
                            var numberResult = 0.0
                            val lastNumberValue = BigDecimal(tempLastResult)
                            val currentNumberValue = BigDecimal(s)
                            when (tempLastOperate) {
                                PLUS_OPERATE -> numberResult =
                                    lastNumberValue.add(currentNumberValue).toDouble()

                                MINUS_OPERATE -> numberResult =
                                    lastNumberValue.subtract(currentNumberValue).toDouble()
                            }
                            LogUtils.e("---SimpleCalculator---lastNumberValue: $lastNumberValue--currentNumberValue: $currentNumberValue--numberResult: $numberResult")
                            tempLastResult = numberResult.toString()
                            tempLastOperate = ""
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            tempLastResult = "0"
                            tempLastOperate = ""
                            ToastUtils.showShort("计算出错了:" + e.message)
                        }
                    }
                } else {
                    tempLastOperate = s
                }
            }
            val result = BigDecimal(tempLastResult)
            val max: BigDecimal = result.max(MAX_VALUE)
            if (max == result) {
                ToastUtils.showShort("超出最大数99999999")
                return
            }
            val min: BigDecimal = result.min(MIN_VALUE)
            if (min == result) {
                ToastUtils.showShort("低于最小数0")
                return
            }
            val mLastResult: String = result.stripTrailingZeros().toPlainString()
            binding.tvAmount.setText(mLastResult)
            /* tvOperateNumber.setText(lastFormulaBuilder.append("=").toString())
             if (onCalculateListener != null) {
                 onCalculateListener.onConfirmMoney(mLastResult)
             }*/
        } else {
            if (splitInput.size >= 1) {
                val mLastResult = splitInput[0]
                binding.tvAmount.setText(mLastResult)
                /*if (onCalculateListener != null) {
                    onCalculateListener.onConfirmMoney(mLastResult)
                }*/
            }
        }
    }

    /**
     * 获取最后的操作符或者数字
     */
    private fun getLastOperateOrNumber(): String {
        val currentInputText = getCurrentInputText()
        if (TextUtils.isEmpty(currentInputText)) {
            LogUtils.e("---SimpleCalculator-getLastOperateOrNumber currentInputText is null")
            return ""
        }
        val split: Array<String> = currentInputText.split(SPLITE_STRING).toTypedArray()
        if (split.size > 0) {
            val endOperateOrNumber = split[split.size - 1]
            LogUtils.e("---SimpleCalculator-getLastOperateOrNumber split endOperateOrNumber = $endOperateOrNumber")
            return endOperateOrNumber
        }
        LogUtils.e("---SimpleCalculator-getLastOperateOrNumber split is null")
        return ""
    }

    /**
     * 是否是操作度
     */
    private fun isOperate(operateOrNumber: String): Boolean {
        for (operate in OPERATE_ALL) {
            if (TextUtils.equals(operateOrNumber, operate)) {
                return true
            }
        }
        return false
    }

    /**
     * 输入小数点
     */
    private fun inputSpot() {
        try {
            val lastOperateOrNumber = getLastOperateOrNumber()
            if (!TextUtils.isEmpty(lastOperateOrNumber) && !isOperate(lastOperateOrNumber)) {
                if (!lastOperateOrNumber.contains(".")) {
                    inputFormula(".")
                }
            } else {
                //当前没有任何输入直接插入0.
                val currentInputText = getCurrentInputText()
                if (TextUtils.isEmpty(currentInputText)) {
                    inputFormula("0.")
                } else {
                    inputFormula(SPLITE_STRING + "0.")
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            LogUtils.e("输入小数点出错了:" + e.message)
        }
    }

    /**
     * 删除输入
     */
    private fun deleteInput() {
        try {
            val inputBuilder = StringBuilder(getCurrentInputText())
            if (inputBuilder.length > 0) {
                //删除最后的一个字符
                val length = inputBuilder.length
                inputBuilder.delete(length - 1, inputBuilder.length)
                //删除最后字符,判断最后一个字符为空格则直接删除
                val afterLength = inputBuilder.length
                if (afterLength > 0) {
                    val lastString = inputBuilder.substring(afterLength - 1)
                    if (SPLITE_STRING.equals(lastString)) {
                        inputBuilder.delete(afterLength - 1, inputBuilder.length)
                    }
                }
                binding.tvAmount.setText(inputBuilder.toString())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            LogUtils.e("删除出错了:" + e.message)
        }
    }

    private fun inputFormula(operateOrNumber: String) {
        if (operateOrNumber.equals(".")) {
            ttsSpeak("点")
        } else if (operateOrNumber.equals("0.")) {
            ttsSpeak("零点")
        } else {
            ttsSpeak(operateOrNumber)
        }

        val currentInputText = getCurrentInputText()
        if (!TextUtils.isEmpty(currentInputText)) {
            binding.tvAmount.setText(currentInputText + operateOrNumber)
        } else {
            binding.tvAmount.setText(operateOrNumber)
        }
    }

    /**
     * 输入操作符
     */
    private fun inputOperate(operate: String) {
        try {
            val lastOperateOrNumber = getLastOperateOrNumber()
            if (!TextUtils.isEmpty(lastOperateOrNumber)) {
                //当前输入框没有数字则不能数字
                //当前输入的是操作符，先插入分隔符再插入数字
                if (isOperate(lastOperateOrNumber)) {
                    //判断如果最后是操作符 则替换成当前输入的操作符
                    //删除最后的操作符
                    deleteInput()
                    inputFormula(SPLITE_STRING.toString() + operate)
                } else {
                    //如果结尾是小数点 则先删除小数点
                    if (lastOperateOrNumber.endsWith(".")) {
                        deleteInput();
                        inputFormula(SPLITE_STRING + operate);
                    } else {
                        inputFormula(SPLITE_STRING + operate);
                    }

                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            LogUtils.e("输入操作符出错了:" + e.message)
        }
    }

    /**
     * 输入数字
     */
    private fun inputNumber(number: String) {
        try {
            val lastOperateOrNumber = getLastOperateOrNumber()
            if (!TextUtils.isEmpty(lastOperateOrNumber)) {
                //如果最后是操作符,先插入分隔符再插入数字
                if (isOperate(lastOperateOrNumber)) {
                    inputFormula(SPLITE_STRING.toString() + number)
                } else {
                    //判断数字长度不能超多
                    val pointIndex = lastOperateOrNumber.indexOf(".")
                    if (pointIndex != -1) {
                        //获取小数点位数 最多两位小数
                        if (lastOperateOrNumber.length - 1 >= pointIndex + 2) {
                            return
                        }
                        //小数可以输入最大11位
                        if (lastOperateOrNumber.length >= 11) {
                            return
                        }
                    } else {
                        //整数可以输入最大8位
                        if (lastOperateOrNumber.length >= 8) {
                            return
                        }
                        //007类似不可以输入
                        if (lastOperateOrNumber == "0") {
                            //当前如果还是0 则不可以输入
                            if (number != "0") {
                                //删除一个值 插入新数字
                                deleteInput()
                                //当前没有任何输入直接插入数字
                                val currentInputText = getCurrentInputText()
                                if (TextUtils.isEmpty(currentInputText)) {
                                    //插入数字
                                    inputFormula(number)
                                } else {
                                    inputFormula(SPLITE_STRING + number)
                                }
                            }
                            return
                        }
                    }
                    //插入数字
                    inputFormula(number)
                }
            } else {
                inputFormula(number)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            LogUtils.e("输入数字出错了:" + e.message)
        }
    }

    /**
     * 清空重置数据
     */
    fun clearCalcData() {
        binding.tvAmount.setText("")
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
        } catch (e: FacePassException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {

    }


}