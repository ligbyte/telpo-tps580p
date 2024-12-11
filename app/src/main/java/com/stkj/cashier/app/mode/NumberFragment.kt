package com.stkj.cashier.app.mode

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.stkj.cashier.util.util.*
import com.google.gson.Gson
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.adapter.ConsumeRecordListAdapter
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.DifferentDisplay
import com.stkj.cashier.bean.ConsumeRecordListBean
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.bean.db.CompanyMemberdbEntity
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.NumberFragmentBinding
import com.stkj.cashier.greendao.biz.CompanyMemberBiz
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import mcv.facepass.FacePassException
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class NumberFragment : BaseFragment<ModeViewModel, NumberFragmentBinding>() {


    private lateinit var reportDeviceStatusDisposable: Disposable
    private var companyMember: CompanyMemberdbEntity? = null
    var curPage = 1

    companion object {
        fun newInstance(): NumberFragment {
            return NumberFragment()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.e("按次消费onHiddenChanged"+hidden)
        if (hidden) {
            EventBus.getDefault().post(MessageEventBean(MessageEventType.NumberNotice2))
        } else {
            EventBus.getDefault().post(MessageEventBean(MessageEventType.NumberNotice))
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        EventBus.getDefault().post(MessageEventBean(MessageEventType.NumberNotice))
        val layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        binding.rvConsume.layoutManager = layoutManager//设置布局管理器
        var mAdapter = ConsumeRecordListAdapter(viewModel.consumeRecord.value?.results)
        binding.rvConsume.adapter = mAdapter
        consumeRecord()
        viewModel.consumeRecord.observe(this) {
            LogUtils.d("consumeRecord", Gson().toJson(it))
            mAdapter.setList(it.results)
        }
        viewModel.modifyBalance.observe(this) {
            LogUtils.e("按次消费modifyBalance", Gson().toJson(it))
            if (it.code == 10000) {
                var voice = SPUtils.getInstance().getString(Constants.CONSUMPTION_SUCCESS, "消费成功")
                ttsSpeak(voice+ it.data?.consumptionMone+"元")
                it.data?.customerNo?.let {
                    companyMember = CompanyMemberBiz.getCompanyMemberByCard(it)
                }
                binding.llNoPerson.visibility = View.GONE
                binding.llPerson.visibility = View.VISIBLE

                binding.tvNumber.text = it.data?.billCount
                if (!TextUtils.isEmpty(it.data?.customerNo)) {
                    //binding.ivHeader.setImageBitmap(StringUtils.stringToBitmap(companyMember?.imgData))
                    if (companyMember == null) {
                        binding.ivHeader.setImageDrawable(App.applicationContext.getDrawable(R.mipmap.ic_no_person))
                    } else {
                        binding.ivHeader.setImageBitmap(ImageUtils.base64ToBitmap(companyMember?.imgData))
                    }
                } else {
                    binding.ivHeader.setImageDrawable(App.applicationContext.getDrawable(R.mipmap.ic_no_person))
                }
                LogUtils.e("modifyBalance", it.data?.consumptionMone.toString())
                binding.tvAmount.text = "¥" + it.data?.consumptionMone.toString()
                delayToScan()
                consumeRecord()
                EventBus.getDefault().post(
                    MessageEventBean(
                        MessageEventType.NumberSuccess,
                        it.data?.customerNo,
                        it.data?.consumptionMone
                    )
                )
            } else if (it.code == 10009) {
                //支付中
                LogUtils.e("支付中" + Gson().toJson(it.data))
                it.data?.payNo?.let { it1 -> getPayStatus(it1) }
            } else {
                if (!it.message.isNullOrEmpty()) {
                    ttsSpeak(it.message!!)
                    ToastUtils.showLong(it.message)
                }
                DifferentDisplay.isStartFaceScan.set(true)
                EventBus.getDefault().post(MessageEventBean(MessageEventType.NumberNotice))
            }

        }
    }

    private fun delayToScan() {

        EventBus.getDefault().post(MessageEventBean(MessageEventType.NumberNotice))
        reportDeviceStatusDisposable = Observable.timer(6000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aLong ->
                binding.llPerson.visibility = View.GONE
                binding.llNoPerson.visibility = View.VISIBLE
            }

    }

    private fun consumeRecord() {
        var map = hashMapOf<String, Any>()
        map["mode"] = "ConsumeRecordList"
        map["machine_Number"] = App.serialNumber
        map["pageIndex"] = 1
        map["pageSize"] = 5000
        var md5 = EncryptUtils.encryptMD5ToString16(App.serialNumber + "&1&5000")
        map["sign"] = md5
        viewModel.consumeRecordList(map)
    }

    private fun modifyBalance(faceToken: String) {
        companyMember = CompanyMemberBiz.getCompanyMember(faceToken)
        if (companyMember != null) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = companyMember?.cardNumber.toString()
            map["consumption_type"] = 10
            map["deduction_Type"] = 50
            val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = 0
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    companyMember?.cardNumber + "&50&" + App.serialNumber + "&0" + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            LogUtils.e("按次modifyBalance消费" + Gson().toJson(map))
            viewModel.modifyBalance(map)
        }


    }

    private fun modifyBalanceByCard(card: String) {
        companyMember = CompanyMemberBiz.getCompanyMemberByCard(card)
        if (companyMember != null && !TextUtils.isEmpty(companyMember?.cardNumber)) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = companyMember?.cardNumber.toString()
            map["consumption_type"] = 20
            map["deduction_Type"] = 50
            val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = 0
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    companyMember?.cardNumber + "&50&" + App.serialNumber + "&0" + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)
        } else if (card != null) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = card
            map["consumption_type"] = 20
            map["deduction_Type"] = 50
            val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = 0
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    card + "&50&" + App.serialNumber + "&0" + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)
        } else {
            ttsSpeak("无信息")
            DifferentDisplay.isStartFaceScan.set(true)
        }


    }

    private fun modifyBalanceByScanCode(card: String) {
        if (!TextUtils.isEmpty(card)) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "ModifyBalance"
            map["cardNumber"] = card
            map["consumption_type"] = 30
            map["deduction_Type"] = 50
            val randoms = (1000..9999).random().toString()
            var onlineOrderNumber = "ZGXF" + TimeUtils.millis2String(
                System.currentTimeMillis(),
                "yyyyMMddHHmmss"
            ) + randoms
            map["online_Order_number"] = onlineOrderNumber
            map["machine_Number"] = App.serialNumber
            map["money"] = 0
            var md5 =
                EncryptUtils.encryptMD5ToString16(
                    card + "&50&" + App.serialNumber + "&0" + "&" + onlineOrderNumber
                )
            map["sign"] = md5
            viewModel.modifyBalance(map)
        } else {
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

    override fun onStart() {
        super.onStart()

    }
    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onStop() {
        super.onStop()
    }


    override fun hideLoading() {
        super.hideLoading()

    }

    override fun getLayoutId(): Int {
        return R.layout.number_fragment
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        LogUtils.e("按次onEventReceiveMsg" + Gson().toJson(message))
        when (message.type) {
            MessageEventType.NumberToken -> {
                message.content?.let {
                    LogUtils.e("按次modifyBalance" + Gson().toJson(message))
                    modifyBalance(it)
                }
            }
            MessageEventType.NumberCard -> {
                message.content?.let { modifyBalanceByCard(it) }
            }
            MessageEventType.NumberScanCode -> {
                message.content?.let { modifyBalanceByScanCode(it) }
            }
        }
    }

}