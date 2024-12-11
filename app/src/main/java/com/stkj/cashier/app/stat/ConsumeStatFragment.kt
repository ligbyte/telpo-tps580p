package com.stkj.cashier.app.stat

import android.os.Bundle
import android.util.Log
import com.stkj.cashier.util.util.EncryptUtils
import com.stkj.cashier.util.util.LogUtils
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.MainActivity
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.ConsumeStatFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 消费统计页面
 */
@AndroidEntryPoint
class ConsumeStatFragment :
    BaseFragment<ConsumeStatViewModel, ConsumeStatFragmentBinding>() {

    companion object {
        fun newInstance(): ConsumeStatFragment {
            return ConsumeStatFragment()
        }
    }

    //当前页面选中状态索引
    private var currentSelectIndex = -1

    //页面总item数量
    private val pageSelectItemCount = 4

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        viewModel.consumeStatResult.observe(this) {
            isRequestingPageData = false
            if (it.code == 10000 && !it.data.isNullOrEmpty()) {
                setConsumeStatData(it)
            } else {
                var errorMsg = "获取数据失败,请点击确认键重试"
                if (!it.message.isNullOrEmpty()) {
                    errorMsg = it.message
                }
                binding.ctvStat.setTips(errorMsg)
            }
        }
        currentSelectIndex = -1
        scrollNextItem()
    }

    /**
     * 设置消费统计数据
     */
    private fun setConsumeStatData(statData: ConsumeStatBean) {
        binding.ctvStat.hideTipsView()
        binding.tvSumConsume.text = statData.sumConsume
        binding.tvSumRefund.text = statData.sumRefund
        binding.tvSumIncome.text = statData.sumIncome
        val statDataList = statData.data
        for (statItem in statDataList) {
            when (statItem.feeType) {
                "1" -> {
                    binding.tvBreakfastConsume.text = statItem.consume
                    binding.tvBreakfastRefund.text = "-" + statItem.refund
                    binding.tvBreakfastIncome.text = statItem.income
                }

                "2" -> {
                    binding.tvLunchConsume.text = statItem.consume
                    binding.tvLunchRefund.text = "-" + statItem.refund
                    binding.tvLunchIncome.text = statItem.income
                }

                "3" -> {
                    binding.tvDinnerConsume.text = statItem.consume
                    binding.tvDinnerRefund.text = "-" + statItem.refund
                    binding.tvDinnerIncome.text = statItem.income
                }
            }
        }
    }

    private var isRequestingPageData: Boolean = false

    /**
     * 刷新页面数据信息
     */
    private fun requestConsumeStatDate(itemIndex: Int) {
        binding.ctvStat.setLoading("加载中")
        isRequestingPageData = true
        val totalTime = (itemIndex + 1).toString()
        val map = hashMapOf<String, Any>()
        map["mode"] = "ConsumeFeeTypeTotal"
        map["machine_Number"] = App.serialNumber
        map["totalTime"] = totalTime
        val md5 = EncryptUtils.encryptMD5ToString16(App.serialNumber + "&" + totalTime)
        map["sign"] = md5
        viewModel.requestConsumeStat(map, errorCallback = {
            isRequestingPageData = false
            binding.ctvStat.setTips("获取数据失败,请点击确认键重试")
        })
    }

    private fun scrollPreItem() {
        currentSelectIndex--;
        if (currentSelectIndex <= -1) {
            currentSelectIndex = pageSelectItemCount - 1
        }
        Log.e("selectScrollItem", "-scrollPreItem-currentSelectIndex-- = $currentSelectIndex")
        selectScrollItem(currentSelectIndex)
    }

    private fun scrollNextItem() {
        currentSelectIndex++
        if (currentSelectIndex >= pageSelectItemCount) {
            currentSelectIndex = 0
        }
        Log.e("selectScrollItem", "-scrollNextItem-currentSelectIndex-- = $currentSelectIndex")
        selectScrollItem(currentSelectIndex)
    }

    private fun selectScrollItem(itemIndex: Int) {
        binding.tvTabToday.isSelected = false
        binding.tvTabWeek.isSelected = false
        binding.tvTabThisMonth.isSelected = false
        binding.tvTabLastMonth.isSelected = false
        binding.tvSumConsume.text = "—"
        binding.tvSumRefund.text = "—"
        binding.tvSumIncome.text = "—"
        binding.tvBreakfastConsume.text = "—"
        binding.tvBreakfastRefund.text = "—"
        binding.tvBreakfastIncome.text = "—"
        binding.tvLunchConsume.text = "—"
        binding.tvLunchRefund.text = "—"
        binding.tvLunchIncome.text = "—"
        binding.tvDinnerConsume.text = "—"
        binding.tvDinnerRefund.text = "—"
        binding.tvDinnerIncome.text = "—"
        when (itemIndex) {
            0 -> {
                binding.tvTabToday.isSelected = true
            }

            1 -> {
                binding.tvTabWeek.isSelected = true
            }

            2 -> {
                binding.tvTabThisMonth.isSelected = true
            }

            3 -> {
                binding.tvTabLastMonth.isSelected = true
            }
        }
        requestConsumeStatDate(itemIndex)
    }

    override fun getLayoutId(): Int {
        return R.layout.consume_stat_fragment
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        when (message.type) {
            MessageEventType.KeyEventNumber -> {
                //LogUtils.e("金额模式 按键")
                if (App.mShowConsumeStat) {
                    message.content?.let {
                        LogUtils.e("消费模式 按键" + it)
                        when (it) {
                            "向左",
                            "向上" -> {
                                if (isRequestingPageData) {
                                    return
                                }
                                binding.ctvStat.hideTipsView()
                                scrollPreItem()
                            }

                            "向右",
                            "向下" -> {
                                if (isRequestingPageData) {
                                    return
                                }
                                binding.ctvStat.hideTipsView()
                                scrollNextItem()
                            }

                            "删除",
                            "取消" -> {
                                backPress()
                            }

                            "确认" -> {
                                if (isRequestingPageData) {
                                    return
                                }
                                requestConsumeStatDate(currentSelectIndex)
                            }
                        }

                    }
                }

            }
        }
    }

    private fun backPress() {
        val mainActivity = activity as MainActivity
        mainActivity.hidePlaceHolderFragment(this)
        App.mShowConsumeStat = false
    }

}