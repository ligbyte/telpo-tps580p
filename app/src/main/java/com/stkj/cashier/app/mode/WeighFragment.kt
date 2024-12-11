package com.stkj.cashier.app.mode

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.google.gson.Gson
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.adapter.ConsumeRecordListAdapter
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.DifferentDisplay
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.bean.TakeMealsListResult
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.AmountFragmentBinding
import com.stkj.cashier.databinding.WeighFragmentBinding
import com.stkj.cashier.greendao.biz.CompanyMemberBiz
import com.stkj.cashier.util.Tools
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
@description 称重收费$
@author: Administrator
@date: 2024/4/15
 */
@AndroidEntryPoint
class WeighFragment : BaseFragment<ModeViewModel, WeighFragmentBinding>(), View.OnClickListener {
    var operationType = 1

    //var cacheNumber = 0f
    var cacheOperation = ""
    var WeighNumber: Double = 0.0
    var WeighNumberNow: String = ""

    companion object {
        fun newInstance(): WeighFragment {
            return WeighFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.weigh_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        EventBus.getDefault().post(MessageEventBean(MessageEventType.AmountNotice2))
        binding.tvText0.setOnClickListener(this)
        binding.tvText1.setOnClickListener(this)
        binding.tvText2.setOnClickListener(this)
        binding.tvText3.setOnClickListener(this)
        binding.tvText4.setOnClickListener(this)
        binding.tvText5.setOnClickListener(this)
        binding.tvText6.setOnClickListener(this)
        binding.tvText7.setOnClickListener(this)
        binding.tvText8.setOnClickListener(this)
        binding.tvText9.setOnClickListener(this)
        binding.tvPoint.setOnClickListener(this)
        binding.llUnitPriceUpdate.setOnClickListener(this)
        binding.llDelete.setOnClickListener(this)
        binding.llConfirm.setOnClickListener(this)

        val memoryUnitPrice = SPUtils.getInstance().getString(Constants.MEMORY_UNIT_PRICE, "0")
        binding.tvMemoryUnitPrice.text = "¥ " + memoryUnitPrice + "/kg"

        val layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        binding.rvConsume.layoutManager = layoutManager//设置布局管理器
        var mAdapter = ConsumeRecordListAdapter(viewModel.consumeRecord.value?.results)
        binding.rvConsume.adapter = mAdapter

        var map = hashMapOf<String, Any>()
        map["mode"] = "ConsumeRecordList"
        map["machine_Number"] = App.serialNumber
//        map["cardNumber"] = "7202143305"
        map["pageIndex"] = 1
        map["pageSize"] = 5000
        var md5 = EncryptUtils.encryptMD5ToString16(App.serialNumber + "&1&5000")
        map["sign"] = md5
        viewModel.consumeRecordList(map)

        viewModel.consumeRecord.observe(this) {
            LogUtils.e("consumeRecord", Gson().toJson(it))
            mAdapter.setList(it.results)
        }

        viewModel.modifyBalance.observe(this) {
            LogUtils.e("modifyBalance", Gson().toJson(it))
            if (it.code == 10000) {
                viewModel.consumeRecordList(map)
                var voice = SPUtils.getInstance().getString(Constants.CONSUMPTION_SUCCESS, "消费成功")
                ttsSpeak(voice)
                EventBus.getDefault().post(
                    MessageEventBean(
                        MessageEventType.AmountSuccess, //WeighSuccess AmountSuccess
                        it.data?.customerNo,
                        it.data?.consumptionMone
                    )
                )
                setEnable(true)
            }else if (it.code == 10009) {
                //支付中
                LogUtils.e("支付中"+Gson().toJson(it.data))
                it.data?.payNo?.let { it1 -> getPayStatus(it1) }
            } else {
                if (!it.message.isNullOrEmpty()) {
                    ttsSpeak(it.message!!)
                    ToastUtils.showLong(it.message)
                }
                DifferentDisplay.isStartFaceScan.set(true)
                EventBus.getDefault().post(
                    MessageEventBean(
                        MessageEventType.AmountNotice,
                        binding.tvWeighAmount.text.toString()
                    )
                )
            }

        }
    }


    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tvText0 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "0"
                    } else {
                        binding.tvAmount.text = "0"
                        operationType = 1
                    }
                   // updateWeighNumber()
                }
                R.id.tvText1 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "1"
                    } else {
                        binding.tvAmount.text = "1"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }
                R.id.tvText2 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "2"
                    } else {
                        binding.tvAmount.text = "2"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }
                R.id.tvText3 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "3"
                    } else {
                        binding.tvAmount.text = "3"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }
                R.id.tvText4 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "4"
                    } else {
                        binding.tvAmount.text = "4"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }
                R.id.tvText5 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "5"
                    } else {
                        binding.tvAmount.text = "5"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }
                R.id.tvText6 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "6"

                    } else {
                        binding.tvAmount.text = "6"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }
                R.id.tvText7 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "7"
                    } else {
                        binding.tvAmount.text = "7"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }
                R.id.tvText8 -> {

                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "8"
                    } else {
                        binding.tvAmount.text = "8"
                        operationType = 1
                    }
                    //updateWeighNumber()
                }

                R.id.tvText9 -> {
                    if (operationType == 1) {
                        if (checkDouble()) {
                            return
                        }
                        if (binding.tvAmount.text.toString().contains(".")) {
                            if (binding.tvAmount.text.toString().length >= 9) {
                                return
                            }
                        } else {
                            if (binding.tvAmount.text.toString().length >= 6) {
                                return
                            }
                        }
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "9"
                    } else {
                        binding.tvAmount.text = "9"
                        operationType = 1
                    }
                   // updateWeighNumber()
                }
                R.id.tvPoint -> {
                    if (binding.tvAmount.text.toString().contains(".")) {
                        return
                    }
                    if (binding.tvAmount.text.toString().isEmpty()) {
                        binding.tvAmount.text = "0."
                        return
                    }
                    if (operationType == 1) {
                        binding.tvAmount.text = binding.tvAmount.text.toString() + "."
                    } else {
                        binding.tvAmount.text = "."
                        operationType = 1
                    }
                   // updateWeighNumber()
                }
                R.id.ivClearAmount -> {
                    binding.tvAmount.text = ""
                    operationType = 1

                }
                R.id.llDelete -> {
                    if (binding.tvAmount.text.isNotEmpty()) {
                        binding.tvAmount.text =
                            binding.tvAmount.text.substring(0, binding.tvAmount.text.length - 1);
                    }

                    //updateWeighNumber()
                }

                R.id.llConfirm -> {
                    if (binding.tvConfirm.text == "结算") {
                        if (binding.tvWeighAmount.text.toString().isEmpty()
                            || binding.tvWeighAmount.text.toString().toFloat() == 0f
                        ) {
                            ttsSpeak("请输入消费金额")
                            return
                        }
                        val decimalFormat = DecimalFormat("0.00")
                        setEnable(false)
                        modifyBalanceNotice()
                    } else {
                        //取消
                        EventBus.getDefault().post(MessageEventBean(MessageEventType.AmountNotice2))
                        setEnable(true)
                        modifyWeighNumber(WeighNumberNow)
                    }
                }
                R.id.llUnitPriceUpdate -> {
                    if (binding.tvAmount.text.isNotEmpty()) {
                        SPUtils.getInstance()
                            .put(Constants.MEMORY_UNIT_PRICE, binding.tvAmount.text.toString())
                        binding.tvMemoryUnitPrice.text =
                            "¥ " + binding.tvAmount.text.toString() + "/kg"

                        //更新后清空
                        binding.tvAmount.text = ""
                        updateWeighNumber()
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setEnable(enable: Boolean) {
        if (!enable) {
            binding.llConfirm.background =
                requireContext().getDrawable(R.drawable.shape_button_shadow_press)
            binding.tvConfirm.text = "取消"
        } else {
            binding.llConfirm.background =
                requireContext().getDrawable(R.drawable.selector_button_press_0087fa)
            binding.tvConfirm.text = "结算"

            //binding.tvWeighAmount.text = "0.00"
            //binding.tvWeigh.text = "0.00"
        }
        binding.tvText0.isEnabled = enable
        binding.tvText1.isEnabled = enable
        binding.tvText2.isEnabled = enable
        binding.tvText3.isEnabled = enable
        binding.tvText4.isEnabled = enable
        binding.tvText5.isEnabled = enable
        binding.tvText6.isEnabled = enable
        binding.tvText7.isEnabled = enable
        binding.tvText8.isEnabled = enable
        binding.tvText9.isEnabled = enable
        binding.tvPoint.isEnabled = enable
        binding.llDelete.isEnabled = enable


    }

    private fun checkDouble(): Boolean {
        var arr = binding.tvAmount.text.split(".")
        if (arr.size == 2) {
            if (arr[1].length == 2) {
                return true
            }
        }
        return false
    }


    private fun modifyWeighNumber(number: String) {
        if (binding.tvConfirm.text.equals("结算")) {


           // val toDouble = Tools.doubleAccurate(number)
            //WeighNumber = toDouble

            WeighNumber = number.toDouble()

            binding.tvWeigh.text = number
            val memoryUnitPrice = SPUtils.getInstance().getString(Constants.MEMORY_UNIT_PRICE, "0")
            val doubleAccurate = Tools.doubleAccurate(memoryUnitPrice)
            binding.tvWeighAmount.text = String.format("%.2f", WeighNumber * doubleAccurate)
        }

    }

    private fun updateWeighNumber() {
        if (binding.tvConfirm.text.equals("结算")) {
           // val doubleAccurate = Tools.doubleAccurate(binding.tvAmount.text.toString())
            val memoryUnitPrice = SPUtils.getInstance().getString(Constants.MEMORY_UNIT_PRICE, "0")
            val doubleAccurate = Tools.doubleAccurate(memoryUnitPrice)
            binding.tvWeighAmount.text = String.format("%.2f", WeighNumber * doubleAccurate)
        }
    }

    private fun modifyBalance(faceToken: String) {
        var companyMember = CompanyMemberBiz.getCompanyMember(faceToken)
        if (companyMember != null) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = companyMember.cardNumber
            map["consumption_type"] = 10
            map["deduction_Type"] = 60
            val randoms = (1000..9999).random().toString()
            var onlineOrderNumber ="ZGXF"+ TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = binding.tvWeighAmount.text.toString().trim()
//            map["online_Order_number"] = "202012211032"
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    companyMember.cardNumber + "&60&" + App.serialNumber + "&" + binding.tvWeighAmount.text.toString()
                        .trim() + "&" + onlineOrderNumber
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
            val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF"+TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = binding.tvWeighAmount.text.toString().trim()
//            map["online_Order_number"] = "202012211032"
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    companyMember.cardNumber + "&60&" + App.serialNumber + "&" + binding.tvWeighAmount.text.toString()
                        .trim() + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)

        } else {
            ttsSpeak("无信息")
            DifferentDisplay.isStartFaceScan.set(true)
        }


    }
    /**
     * 扫码
     * */
    private fun modifyBalanceByScanCode(card: String) {
        LogUtils.e("扫码" + card)

        if (!TextUtils.isEmpty(card)) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = card
            map["consumption_type"] = 30
            map["deduction_Type"] = 60
            val randoms = (1000..9999).random().toString()
            var onlineOrderNumber ="ZGXF"+ TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = binding.tvWeighAmount.text.toString().trim()
//            map["online_Order_number"] = "202012211032"
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    card + "&60&" + App.serialNumber + "&" + binding.tvWeighAmount.text.toString()
                        .trim() + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)

        } else {
            ttsSpeak("无信息")
        }


    }
    private fun getPayStatus(payNo : String) {
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
                        EncryptUtils.encryptMD5ToString16(card )
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
    private fun modifyBalanceNotice() {
        EventBus.getDefault()
            .post(MessageEventBean(MessageEventType.AmountNotice, binding.tvWeighAmount.text.toString()))
        ttsSpeak("请刷脸或刷卡")
    }


    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        LogUtils.e("重量onEventReceiveMsg" + Gson().toJson(message))
        when (message.type) {
            MessageEventType.WeighToken -> {
                // 称重 刷脸
                message.content?.let { modifyBalance(it) }
            }
            MessageEventType.WeighCard -> {
                //称重 刷卡
                message.content?.let { modifyBalanceByCard(it) }
            }
            MessageEventType.WeighScanCode -> {
                //扫码
                message.content?.let { modifyBalanceByScanCode(it) }
            }
            MessageEventType.WeighNumber -> {
                //称重
                message.content?.let {
                    LogUtils.e("重量" + Gson().toJson(it))
                    modifyWeighNumber(it)
                    WeighNumberNow=it
                }
            }

        }
    }
}