package com.stkj.cashier.app.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.jakewharton.processphoenix.ProcessPhoenix
import com.stkj.cashier.App
import com.stkj.cashier.BuildConfig
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.MainActivity
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.Consumption1SettingFragmentBinding
import com.stkj.cashier.dict.HomeMenu
import com.stkj.cashier.util.ShellUtils
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class Consumption1SettingFragment :
    BaseFragment<SettingViewModel, Consumption1SettingFragmentBinding>(), View.OnClickListener {


    companion object {
        fun newInstance(): Consumption1SettingFragment {
            return Consumption1SettingFragment()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.e("消费模式onHiddenChanged" + hidden)
        if (hidden) {
            SPUtils.getInstance().put(Constants.FRAGMENT_SET, false)
        } else {
            SPUtils.getInstance().put(Constants.FRAGMENT_SET, true)
        }
    }

    //当前页面选中状态索引
    private var currentSelectIndex = -1

    //当前页面索引
    private var mPageIndex = 0

    //第一层页面总item数量
    private val firstPageSelectItemCount = 5

    //第二层页面总item数量
    private val secondPageSelectItemCount = 8
    private val fixAmountDataMap: MutableMap<String, String> = mutableMapOf()

    /**
     * 刷新时段信息
     */
    private fun refreshIntervalCardType() {
        //早餐时间
        val intervalCardType = App.intervalCardType
        if (intervalCardType.isNotEmpty()) {
            val cardTypeBean = intervalCardType[0]
            val oneTime = cardTypeBean.oneTime
            if (!TextUtils.isEmpty(oneTime)) {
                binding.llSwitchBreakfast.visibility = View.VISIBLE
                binding.tvBreakfastTime.text = oneTime
                val breakfastSwitch =
                    SPUtils.getInstance().getBoolean(Constants.BREAKFAST_SWITCH, true)
                if (breakfastSwitch) {
                    binding.llBreakfastSetting.visibility = View.VISIBLE
                    binding.ivSwitchBreakfast.setImageResource(R.mipmap.icon_check_selected)
                } else {
                    binding.llBreakfastSetting.visibility = View.GONE
                    binding.ivSwitchBreakfast.setImageResource(0)
                }
            } else {
                binding.llSwitchBreakfast.visibility = View.GONE
                binding.llBreakfastSetting.visibility = View.GONE
            }
            val twoTime = cardTypeBean.twoTime
            if (!TextUtils.isEmpty(twoTime)) {
                binding.llSwitchLunch.visibility = View.VISIBLE
                binding.tvLunchTime.text = twoTime
                val lunchSwitch =
                    SPUtils.getInstance().getBoolean(Constants.LUNCH_SWITCH, true)
                if (lunchSwitch) {
                    binding.llLunchSetting.visibility = View.VISIBLE
                    binding.ivSwitchLunch.setImageResource(R.mipmap.icon_check_selected)
                } else {
                    binding.llLunchSetting.visibility = View.GONE
                    binding.ivSwitchLunch.setImageResource(0)
                }
            } else {
                binding.llSwitchLunch.visibility = View.GONE
                binding.llLunchSetting.visibility = View.GONE
            }
            val threeTime = cardTypeBean.threeTime
            if (!TextUtils.isEmpty(threeTime)) {
                binding.llSwitchDinner.visibility = View.VISIBLE
                binding.tvDinnerTime.text = threeTime
                val dinnerSwitch =
                    SPUtils.getInstance().getBoolean(Constants.DINNER_SWITCH, true)
                if (dinnerSwitch) {
                    binding.llDinnerSetting.visibility = View.VISIBLE
                    binding.ivSwitchDinner.setImageResource(R.mipmap.icon_check_selected)
                } else {
                    binding.llDinnerSetting.visibility = View.GONE
                    binding.ivSwitchDinner.setImageResource(0)
                }
            } else {
                binding.llSwitchDinner.visibility = View.GONE
                binding.llDinnerSetting.visibility = View.GONE
            }
        } else {
            binding.llSwitchBreakfast.visibility = View.GONE
            binding.llBreakfastSetting.visibility = View.GONE
            binding.llSwitchLunch.visibility = View.GONE
            binding.llLunchSetting.visibility = View.GONE
            binding.llSwitchDinner.visibility = View.GONE
            binding.llDinnerSetting.visibility = View.GONE
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        showFirstPage()
    }

    private fun scrollPreItem() {
        currentSelectIndex--;
        if (mPageIndex == 0) {
            if (currentSelectIndex <= -1) {
                currentSelectIndex = firstPageSelectItemCount - 1
            }
        } else if (mPageIndex == 1) {
            Log.e("selectScrollItem", "-scrollPreItem-mPageIndex- = $currentSelectIndex")
            if (currentSelectIndex <= -1) {
                currentSelectIndex = secondPageSelectItemCount - 1
                Log.e("selectScrollItem", "-scrollPreItem--resetIndex- = $currentSelectIndex")
            }
            if (binding.llDinnerSetting.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollPreItem-llDinnerSetting-GONE")
                if (currentSelectIndex == 5) {
                    currentSelectIndex -= 1;
                }
            }
            if (binding.llLunchSetting.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollPreItem-llLunchSetting-GONE")
                if (currentSelectIndex == 4) {
                    currentSelectIndex -= 1;
                }
            }
            if (binding.llBreakfastSetting.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollPreItem-llBreakfastSetting-GONE")
                if (currentSelectIndex == 3) {
                    currentSelectIndex -= 1;
                }
            }
            if (binding.llSwitchDinner.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollPreItem-llSwitchDinner-GONE")
                if (currentSelectIndex == 2) {
                    currentSelectIndex -= 1;
                }
            }
            if (binding.llSwitchLunch.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollPreItem-llSwitchLunch-GONE")
                if (currentSelectIndex == 1) {
                    currentSelectIndex -= 1;
                }
            }
            if (binding.llSwitchBreakfast.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollPreItem-llSwitchBreakfast-GONE")
                if (currentSelectIndex == 0) {
                    currentSelectIndex -= 1;
                }
            }
            if (currentSelectIndex <= -1) {
                currentSelectIndex = secondPageSelectItemCount - 1
                Log.e("selectScrollItem", "-scrollPreItem--resetIndex- = $currentSelectIndex")
            }
        }
        Log.e("selectScrollItem", "-scrollPreItem-currentSelectIndex-- = $currentSelectIndex")
        selectScrollItem(currentSelectIndex)
    }

    private fun scrollNextItem() {
        currentSelectIndex++
        if (mPageIndex == 0) {
            if (currentSelectIndex >= firstPageSelectItemCount) {
                currentSelectIndex = 0
            }
        } else if (mPageIndex == 1) {
            Log.e("selectScrollItem", "-scrollNextItem-mPageIndex-$currentSelectIndex")
            if (currentSelectIndex >= secondPageSelectItemCount) {
                currentSelectIndex = 0
                Log.e("selectScrollItem", "-scrollNextItem--resetIndex- = $currentSelectIndex")
            }
            if (binding.llSwitchBreakfast.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollNextItem-llSwitchBreakfast-GONE")
                if (currentSelectIndex == 0) {
                    currentSelectIndex += 1;
                }
            }
            if (binding.llSwitchLunch.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollNextItem-llSwitchLunch-GONE")
                if (currentSelectIndex == 1) {
                    currentSelectIndex += 1;
                }
            }
            if (binding.llSwitchDinner.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollNextItem-llSwitchDinner-GONE")
                if (currentSelectIndex == 2) {
                    currentSelectIndex += 1;
                }
            }
            if (binding.llBreakfastSetting.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollNextItem-llBreakfastSetting-GONE")
                if (currentSelectIndex == 3) {
                    currentSelectIndex += 1;
                }
            }
            if (binding.llLunchSetting.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollNextItem-llLunchSetting-GONE")
                if (currentSelectIndex == 4) {
                    currentSelectIndex += 1;
                }
            }
            if (binding.llDinnerSetting.visibility == View.GONE) {
                Log.e("selectScrollItem", "-scrollNextItem-llDinnerSetting-GONE")
                if (currentSelectIndex == 5) {
                    currentSelectIndex += 1;
                }
            }
            if (currentSelectIndex >= secondPageSelectItemCount) {
                currentSelectIndex = 0
                Log.e("selectScrollItem", "-scrollNextItem--resetIndex- = $currentSelectIndex")
            }
        }
        Log.e("selectScrollItem", "-scrollNextItem-currentSelectIndex-- = $currentSelectIndex")
        selectScrollItem(currentSelectIndex)
    }

    private fun selectScrollItem(itemIndex: Int) {
        if (mPageIndex == 0) {
            binding.flFixAmountMode.background = null
            binding.flSwitchFacePass.background = null
            binding.flRestartApp.background = null
            binding.flShutdownDevice.background = null
            binding.flRebootDevice.background = null
            var focusView: View? = null
            when (itemIndex) {
                0 -> {
                    binding.flFixAmountMode.background = ColorDrawable(0x12ffffff)
                    focusView = binding.flFixAmountMode
                }

                1 -> {
                    binding.flSwitchFacePass.background = ColorDrawable(0x12ffffff)
                    focusView = binding.flSwitchFacePass
                }

                2 -> {
                    binding.flRestartApp.background = ColorDrawable(0x12ffffff)
                    focusView = binding.flRestartApp
                }

                3 -> {
                    binding.flShutdownDevice.background = ColorDrawable(0x12ffffff)
                    focusView = binding.flShutdownDevice
                }

                4 -> {
                    binding.flRebootDevice.background = ColorDrawable(0x12ffffff)
                    focusView = binding.flRebootDevice
                }
            }
            if (focusView != null) {
                binding.svContent.smoothScrollTo(0, focusView.top)
            }
        } else if (mPageIndex == 1) {
            binding.flCanteenTimeSetting.background = null
            binding.llSwitchBreakfast.isSelected = false
            binding.ivSwitchBreakfast.isSelected = false
            binding.tvSwitchBreakfast.isSelected = false
            binding.llSwitchLunch.isSelected = false
            binding.ivSwitchLunch.isSelected = false
            binding.tvSwitchLunch.isSelected = false
            binding.llSwitchDinner.isSelected = false
            binding.ivSwitchDinner.isSelected = false
            binding.tvSwitchDinner.isSelected = false
            //早餐
            binding.llBreakfastSetting.background = null
            binding.tvBreakfastAmount.isSelected = false
            binding.tvBreakfastAmount.setTextColor(Color.parseColor("#ffffffff"))
            //午餐
            binding.llLunchSetting.background = null
            binding.tvLunchAmount.isSelected = false
            binding.tvLunchAmount.setTextColor(Color.parseColor("#ffffffff"))
            //晚餐
            binding.llDinnerSetting.background = null
            binding.tvDinnerAmount.isSelected = false
            binding.tvDinnerAmount.setTextColor(Color.parseColor("#ffffffff"))
            //开关
            binding.llSwitchFixAmount.background = null
            binding.tvOpenFixAmount.isSelected = false
            binding.tvCloseFixAmount.isSelected = false
            var focusView: View? = null
            when (itemIndex) {
                //早餐开关
                0 -> {
                    binding.flCanteenTimeSetting.background = ColorDrawable(0x12ffffff)
                    binding.llSwitchBreakfast.isSelected = true
                    binding.ivSwitchBreakfast.isSelected = true
                    binding.tvSwitchBreakfast.isSelected = true
                    focusView = binding.flCanteenTimeSetting
                }
                //午餐开关
                1 -> {
                    binding.flCanteenTimeSetting.background = ColorDrawable(0x12ffffff)
                    binding.llSwitchLunch.isSelected = true
                    binding.ivSwitchLunch.isSelected = true
                    binding.tvSwitchLunch.isSelected = true
                    focusView = binding.flCanteenTimeSetting
                }
                //晚餐开关
                2 -> {
                    binding.flCanteenTimeSetting.background = ColorDrawable(0x12ffffff)
                    binding.llSwitchDinner.isSelected = true
                    binding.ivSwitchDinner.isSelected = true
                    binding.tvSwitchDinner.isSelected = true
                    focusView = binding.flCanteenTimeSetting
                }

                //早餐金额
                3 -> {
                    binding.llBreakfastSetting.background = ColorDrawable(0x12ffffff)
                    binding.tvBreakfastAmount.isSelected = true
                    binding.tvBreakfastAmount.setTextColor(Color.parseColor("#ff00dc82"))
                    focusView = binding.llBreakfastSetting
                }

                //午餐金额
                4 -> {
                    binding.llLunchSetting.background = ColorDrawable(0x12ffffff)
                    binding.tvLunchAmount.isSelected = true
                    binding.tvLunchAmount.setTextColor(Color.parseColor("#ff00dc82"))
                    focusView = binding.llLunchSetting
                }

                //晚餐金额
                5 -> {
                    binding.llDinnerSetting.background = ColorDrawable(0x12ffffff)
                    binding.tvDinnerAmount.isSelected = true
                    binding.tvDinnerAmount.setTextColor(Color.parseColor("#ff00dc82"))
                    focusView = binding.llDinnerSetting
                }

                //开启
                6 -> {
                    binding.llSwitchFixAmount.background = ColorDrawable(0x12ffffff)
                    binding.tvOpenFixAmount.isSelected = true
                    focusView = binding.llSwitchFixAmount
                }

                //关闭
                7 -> {
                    binding.llSwitchFixAmount.background = ColorDrawable(0x12ffffff)
                    binding.tvCloseFixAmount.isSelected = true
                    focusView = binding.llSwitchFixAmount
                }
            }
            if (focusView != null) {
                binding.svContent.smoothScrollTo(0, focusView.top)
            }
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.consumption1_setting_fragment
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        when (message.type) {
            MessageEventType.KeyEventNumber -> {
                //LogUtils.e("金额模式 按键")
                if (SPUtils.getInstance().getBoolean(Constants.FRAGMENT_SET, false)) {
                    message.content?.let {
                        LogUtils.e("消费模式 按键" + it)
                        when (it) {
                            "向左",
                            "向上" -> {
                                if (mPageIndex == 1) {
                                    if (binding.flTips.visibility == View.VISIBLE) {
                                        hidTips()
                                        return
                                    }
                                }
                                scrollPreItem()
                            }

                            "向右",
                            "向下" -> {
                                if (mPageIndex == 1) {
                                    if (binding.flTips.visibility == View.VISIBLE) {
                                        hidTips()
                                        return
                                    }
                                }
                                scrollNextItem()
                            }

                            "删除" -> {
                                if (mPageIndex == 1) {
                                    if (currentSelectIndex == 3 || currentSelectIndex == 4 || currentSelectIndex == 5) {
                                        if (!processDelNumber()) {
                                            // TODO:
                                        } else {

                                        }
                                    } else {
                                        backPress()
                                    }
                                } else {
                                    backPress()
                                }
                            }

                            "取消" -> {
                                backPress()
                            }

                            "确认" -> {
                                if (mPageIndex == 0) {
                                    if (currentSelectIndex == 1) {
                                        val switchFacePassPay = binding.ivSwitchFacePass.isSelected
                                        binding.ivSwitchFacePass.isSelected = !switchFacePassPay
                                        if (binding.ivSwitchFacePass.isSelected) {
                                            SPUtils.getInstance()
                                                .put(Constants.SWITCH_FACE_PASS_PAY, true)
                                            EventBus.getDefault()
                                                .post(MessageEventBean(MessageEventType.OpenFacePassPay))
                                        } else {
                                            SPUtils.getInstance()
                                                .put(Constants.SWITCH_FACE_PASS_PAY, false)
                                            EventBus.getDefault()
                                                .post(MessageEventBean(MessageEventType.CloseFacePassPay))
                                        }
                                    } else if (currentSelectIndex == 0) {
                                        showSecondPage()
                                    } else if (currentSelectIndex == 2) {
                                        ProcessPhoenix.triggerRebirth(App.applicationContext)
                                    } else if (currentSelectIndex == 3){
                                        ShellUtils.execCommand("reboot -p",false)
                                    } else if (currentSelectIndex == 4){
                                        ShellUtils.execCommand("reboot",false)
                                    } else {

                                    }
                                } else if (mPageIndex == 1) {
                                    if (binding.flTips.visibility == View.VISIBLE) {
                                        hidTips()
                                    }
                                    if (currentSelectIndex == 0 || currentSelectIndex == 1 || currentSelectIndex == 2) {
                                        handleCanteenTimeSetting()
                                    } else if (currentSelectIndex == 3 || currentSelectIndex == 4 || currentSelectIndex == 5){
                                        scrollNextItem()
                                    } else if (currentSelectIndex == 6) {

                                        if (binding.llBreakfastSetting.visibility == View.VISIBLE) {
                                            SPUtils.getInstance()
                                                .put(Constants.BREAKFAST_SWITCH, true)
                                            //早餐金额判断
                                            var breakfastAmountStr =
                                                binding.tvBreakfastAmount.text.toString()
                                            val breakfastAmount =
                                                breakfastAmountStr.toDoubleOrNull()
                                            if (breakfastAmount == null || breakfastAmount <= 0) {
                                                showTips("早餐金额未设置，不能开启定额模式")
                                                return
                                            }
                                            //保存本地金额
                                            SPUtils.getInstance()
                                                .put(Constants.BREAKFAST_AMOUNT, breakfastAmountStr)
                                        } else {
                                            SPUtils.getInstance()
                                                .put(Constants.BREAKFAST_SWITCH, false)
                                        }

                                        if (binding.llLunchSetting.visibility == View.VISIBLE) {
                                            SPUtils.getInstance().put(Constants.LUNCH_SWITCH, true)
                                            //午餐金额判断
                                            var lunchAmountStr =
                                                binding.tvLunchAmount.text.toString()
                                            val lunchAmount =
                                                lunchAmountStr.toDoubleOrNull()
                                            if (lunchAmount == null || lunchAmount <= 0) {
                                                showTips("午餐金额未设置，不能开启定额模式")
                                                return
                                            }
                                            //保存本地金额
                                            SPUtils.getInstance()
                                                .put(Constants.LUNCH_AMOUNT, lunchAmountStr)
                                        } else {
                                            SPUtils.getInstance().put(Constants.LUNCH_SWITCH, false)
                                        }

                                        if (binding.llDinnerSetting.visibility == View.VISIBLE) {
                                            SPUtils.getInstance().put(Constants.DINNER_SWITCH, true)
                                            //晚餐金额判断
                                            var dinnerAmountStr =
                                                binding.tvDinnerAmount.text.toString()
                                            val dinnerAmount =
                                                dinnerAmountStr.toDoubleOrNull()
                                            if (dinnerAmount == null || dinnerAmount <= 0) {
                                                showTips("晚餐金额未设置，不能开启定额模式")
                                                return
                                            }
                                            //保存本地金额
                                            SPUtils.getInstance()
                                                .put(Constants.DINNER_AMOUNT, dinnerAmountStr)
                                        } else {
                                            SPUtils.getInstance()
                                                .put(Constants.DINNER_SWITCH, false)
                                        }

                                        SPUtils.getInstance().put(Constants.SWITCH_FIX_AMOUNT, true)
                                        EventBus.getDefault()
                                            .post(MessageEventBean(MessageEventType.OpenFixAmountMode))
                                        showFirstPage()
                                        returnMainPage()
                                    } else if (currentSelectIndex == 7) {
                                        //关闭定额
                                        SPUtils.getInstance()
                                            .put(Constants.SWITCH_FIX_AMOUNT, false)
                                        EventBus.getDefault()
                                            .post(MessageEventBean(MessageEventType.CloseFixAmountMode))
                                        showFirstPage()
                                        returnMainPage()
                                    } else {

                                    }
                                } else {

                                }
                            }

                            "0",
                            "1",
                            "2",
                            "3",
                            "4",
                            "5",
                            "6",
                            "7",
                            "8",
                            "9",
                            ".",
                            -> {
                                if (mPageIndex == 1) {
                                    if (binding.flTips.visibility == View.VISIBLE) {
                                        hidTips()
                                        return
                                    }
                                    try {
                                        handleInputNumber(it)
                                    } catch (e: Throwable) {
                                        e.printStackTrace()
                                        showTips("输入数字异常,请重新输入")
                                    }
                                } else {

                                }
                            }

                            else -> {}
                        }

                    }
                }

            }

            MessageEventType.IntervalCardType -> {
                refreshIntervalCardType()
            }
        }
    }

    private fun handleDelAmountNumber(
        amountConst: String,
        amountTextView: TextView
    ): Boolean {
        try {
            val amountTxt = amountTextView.text.toString()
            if (amountTxt.length == 0) {
                fixAmountDataMap[amountConst] = "-"
                amountTextView.text = ""
                return false
            } else if (amountTxt.length == 1) {
                fixAmountDataMap[amountConst] = "-"
                amountTextView.text = ""
            } else {
                amountTextView.text = amountTxt.substring(0, amountTxt.length - 1)
            }
            return true
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

    private fun processDelNumber(): Boolean {
        if (mPageIndex == 1) {
            when (currentSelectIndex) {

                //早餐金额
                3 -> {
                    return handleDelAmountNumber(
                        Constants.BREAKFAST_AMOUNT,
                        binding.tvBreakfastAmount
                    )
                }

                //午餐金额
                4 -> {
                    return handleDelAmountNumber(
                        Constants.LUNCH_AMOUNT,
                        binding.tvLunchAmount
                    )
                }

                //晚餐金额
                5 -> {
                    return handleDelAmountNumber(
                        Constants.DINNER_AMOUNT,
                        binding.tvDinnerAmount
                    )
                }
            }
        }
        return false
    }

    private var hidTipsTask = {
        binding.flTips.visibility = View.GONE
    }

    private fun backPress() {
        if (mPageIndex == 0) {
            returnMainPage()
        } else if (mPageIndex == 1) {
            if (binding.flTips.visibility == View.VISIBLE) {
                hidTips()
                return
            }
            showFirstPage()
        }
    }

    private fun returnMainPage() {
        val mainActivity = activity as MainActivity
        mainActivity.showFragment(HomeMenu.MENU1)
        SPUtils.getInstance().put(Constants.FRAGMENT_SET, false)
    }

    private fun showFirstPage() {
        mPageIndex = 0
        currentSelectIndex = -1
        binding.llPageFirst.visibility = View.VISIBLE
        binding.llPageSecond.visibility = View.GONE
        refreshFirstPageData()
        Choreographer.getInstance().postFrameCallbackDelayed( {
            scrollNextItem()
        },50)
    }

    private fun refreshFirstPageData() {
        binding.tvDeviceSerialNumber.text = App.serialNumber + "/" + BuildConfig.VERSION_NAME
        val facePassPaySwitch =
            SPUtils.getInstance().getBoolean(Constants.SWITCH_FACE_PASS_PAY, false)
        binding.ivSwitchFacePass.isSelected = facePassPaySwitch
    }

    private fun showSecondPage() {
        mPageIndex = 1
        currentSelectIndex = -1
        binding.llPageFirst.visibility = View.GONE
        binding.llPageSecond.visibility = View.VISIBLE
        refreshSecondPageData()
        Choreographer.getInstance().postFrameCallbackDelayed( {
            scrollNextItem()
        },50)
    }

    private fun refreshSecondPageData() {
        //时段信息
        refreshIntervalCardType()

        //早餐
        binding.tvBreakfastAmount.text =
            SPUtils.getInstance().getString(Constants.BREAKFAST_AMOUNT, "") //早餐金额

        //午餐
        binding.tvLunchAmount.text =
            SPUtils.getInstance().getString(Constants.LUNCH_AMOUNT, "") //午餐金额

        //晚餐
        binding.tvDinnerAmount.text =
            SPUtils.getInstance().getString(Constants.DINNER_AMOUNT, "") //晚餐金额
    }

    private fun showTips(tips: String) {
        binding.flTips.removeCallbacks(hidTipsTask)
        binding.flTips.visibility = View.VISIBLE
        binding.tvTips.text = tips
        binding.flTips.postDelayed(hidTipsTask, 1500)
    }

    private fun hidTips() {
        binding.flTips.removeCallbacks(hidTipsTask)
        binding.flTips.visibility = View.GONE
    }

    private fun handleInputAmountNumber(
        insetNumber: String,
        amountTextView: TextView
    ) {
        val text = amountTextView.text.toString()
        if (TextUtils.isEmpty(text)) {
            if (insetNumber == ".") {
                amountTextView.text = "0."
            } else {
                amountTextView.text = insetNumber
            }
        } else {
            //判断数字长度不能超多
            val pointIndex: Int = text.indexOf(".")
            if (pointIndex != -1) {
                //获取小数点位数 最多两位小数
                if (text.length - 1 >= pointIndex + 2) {
                    showTips("最多两位小数")
                    return
                }
            } else {
                if (text.length >= 2 && insetNumber != ".") {
                    showTips("超过最大限值")
                    return
                }
            }
            if (insetNumber == ".") {
                if (!text.contains(".")) {
                    amountTextView.text = "$text."
                }
            } else {
                amountTextView.text = "$text$insetNumber"
            }
        }
    }

    private fun handleInputNumber(insetNumber: String) {
        when (currentSelectIndex) {

            //早餐金额
            3 -> {
                handleInputAmountNumber(
                    insetNumber,
                    binding.tvBreakfastAmount
                )
            }

            //午餐金额
            4 -> {
                handleInputAmountNumber(
                    insetNumber,
                    binding.tvLunchAmount
                )
            }

            //晚餐金额
            5 -> {
                handleInputAmountNumber(
                    insetNumber,
                    binding.tvDinnerAmount
                )
            }
        }
    }

    private fun handleCanteenTimeSetting() {
        when (currentSelectIndex) {
            //早餐开关
            0 -> {
                if (binding.llBreakfastSetting.visibility == View.VISIBLE) {
                    binding.llBreakfastSetting.visibility = View.GONE
                    binding.ivSwitchBreakfast.setImageResource(0)
                } else {
                    binding.llBreakfastSetting.visibility = View.VISIBLE
                    binding.ivSwitchBreakfast.setImageResource(R.mipmap.icon_check_selected)
                }
            }

            //午餐开关
            1 -> {
                if (binding.llLunchSetting.visibility == View.VISIBLE) {
                    binding.llLunchSetting.visibility = View.GONE
                    binding.ivSwitchLunch.setImageResource(0)
                } else {
                    binding.llLunchSetting.visibility = View.VISIBLE
                    binding.ivSwitchLunch.setImageResource(R.mipmap.icon_check_selected)
                }
            }

            //晚餐开关
            2 -> {
                if (binding.llDinnerSetting.visibility == View.VISIBLE) {
                    binding.llDinnerSetting.visibility = View.GONE
                    binding.ivSwitchDinner.setImageResource(0)
                } else {
                    binding.llDinnerSetting.visibility = View.VISIBLE
                    binding.ivSwitchDinner.setImageResource(R.mipmap.icon_check_selected)
                }
            }
        }
    }

    override fun onClick(v: View?) {

    }

}