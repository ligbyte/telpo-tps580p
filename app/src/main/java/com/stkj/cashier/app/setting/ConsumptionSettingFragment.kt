package com.stkj.cashier.app.setting

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioGroup
import androidx.core.view.isVisible
import com.stkj.cashier.util.util.LogUtils
import com.stkj.cashier.util.util.SPUtils
import com.google.gson.Gson
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.MainActivity
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.*
import com.stkj.cashier.dict.HomeMenu
import com.stkj.cashier.util.PopupWindowUtil
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class ConsumptionSettingFragment :
    BaseFragment<SettingViewModel, ConsumptionSettingFragmentBinding>(), View.OnClickListener {


    companion object {
        fun newInstance(): ConsumptionSettingFragment {
            return ConsumptionSettingFragment()
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

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        var index = SPUtils.getInstance().getInt(Constants.MODE_VALUE, 0)
        when (index) {
            -1 -> {
                binding.tvSelectConsumption.text = "金额模式"
            }
            0 -> {
                binding.tvSelectConsumption.text = "金额模式"
            }
            1 -> {
                binding.tvSelectConsumption.text = "按次模式"
            }
            2 -> {
                binding.tvSelectConsumption.text = "取餐模式"
            }
            3 -> {
                binding.tvSelectConsumption.text = "送餐模式"
            }
            4 -> {
                binding.tvSelectConsumption.text = "称重模式"
            }
        }
        var switchConsumptionDialog =
            SPUtils.getInstance().getInt(Constants.SWITCH_CONSUMPTION_DIALOG, 1)
        binding.switchConsumptionDialog.isChecked = switchConsumptionDialog != 0
        var switchStatics = SPUtils.getInstance().getInt(Constants.SWITCH_STATICS, 1)
        binding.switchStatics.isChecked = switchStatics != 0

        binding.tvSelectConsumption.setOnClickListener(this)
        binding.tvSelectDevice.setOnClickListener(this)
        binding.switchStatics.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPUtils.getInstance().put(Constants.SWITCH_STATICS, 1)
                EventBus.getDefault()
                    .post(MessageEventBean(MessageEventType.SwitchStatics, 1))

            } else {
                SPUtils.getInstance().put(Constants.SWITCH_STATICS, 0)
                EventBus.getDefault()
                    .post(MessageEventBean(MessageEventType.SwitchStatics, 0))
            }
        }
        var switchPay = SPUtils.getInstance().getInt(Constants.SWITCH_PAY, 0)
        binding.swithPay.isChecked = switchPay != 0
        binding.swithPay.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPUtils.getInstance().put(Constants.SWITCH_PAY, 1)

            } else {
                SPUtils.getInstance().put(Constants.SWITCH_PAY, 0)
            }
        }


        binding.switchConsumptionDialog.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPUtils.getInstance().put(Constants.SWITCH_CONSUMPTION_DIALOG, 1)
            } else {
                SPUtils.getInstance().put(Constants.SWITCH_CONSUMPTION_DIALOG, 0)
            }
        }
        var successTime = SPUtils.getInstance().getInt(Constants.FACE_SUCCESS_TIME, 3)
        binding.etSuccessTime.setText(successTime.toString())
        binding.etSuccessTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etSuccessTime.text.isNotEmpty()) {
                    SPUtils.getInstance().put(
                        Constants.FACE_SUCCESS_TIME,
                        binding.etSuccessTime.text.toString().toInt()
                    )
                }
            }

        })
    }

    override fun getLayoutId(): Int {
        return R.layout.consumption_setting_fragment
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tvSelectConsumption -> {
                    //创建对象
                    val popupWindow = PopupWindow(requireActivity())
                    val inflate: View = LayoutInflater.from(requireActivity())
                        .inflate(R.layout.layout_select_consumption, null)
                    var rgSelectPopup = inflate.findViewById<RadioGroup>(R.id.rgSelectPopup)
                    var index = SPUtils.getInstance().getInt(Constants.MODE_VALUE, 0)
                    when (index) {
                        -1 -> {
                            rgSelectPopup.check(R.id.rbAmount)
                        }
                        0 -> {
                            rgSelectPopup.check(R.id.rbAmount)
                        }
                        1 -> {
                            rgSelectPopup.check(R.id.rbNumber)
                        }
                        2 -> {
                            rgSelectPopup.check(R.id.rbPickMeal)
                        }
                        3 -> {
                            rgSelectPopup.check(R.id.rbSendMeal)
                        }
                    }
                    //设置view布局
                    popupWindow.contentView = inflate
                    popupWindow.width = binding.tvSelectConsumption.width
                    //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
                    popupWindow.isTouchable = true
                    //设置背景透明
                    popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
                    //点击空白处的时候让PopupWindow消失
                    popupWindow.isOutsideTouchable = true
                    // true时，点击返回键先消失 PopupWindow
                    // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
                    // false时PopupWindow不处理返回键，默认是false
                    popupWindow.isFocusable = false
                    //设置dismiss事件
                    rgSelectPopup.setOnCheckedChangeListener { group, checkedId ->
                        when (checkedId) {
                            R.id.rbAmount -> {
                                binding.tvSelectConsumption.text = "金额模式"
                                SPUtils.getInstance().put(Constants.MODE_VALUE, 0)
                                EventBus.getDefault()
                                    .post(MessageEventBean(MessageEventType.ModeMessage, 0))
                            }
                            R.id.rbNumber -> {
                                binding.tvSelectConsumption.text = "按次模式"
                                SPUtils.getInstance().put(Constants.MODE_VALUE, 1)
                                EventBus.getDefault()
                                    .post(MessageEventBean(MessageEventType.ModeMessage, 1))
                            }
                            R.id.rbPickMeal -> {
                                binding.tvSelectConsumption.text = "取餐模式"
                                SPUtils.getInstance().put(Constants.MODE_VALUE, 2)
                                EventBus.getDefault()
                                    .post(MessageEventBean(MessageEventType.ModeMessage, 2))
                            }
                            R.id.rbSendMeal -> {
                                binding.tvSelectConsumption.text = "送餐模式"
                                SPUtils.getInstance().put(Constants.MODE_VALUE, 3)
                                EventBus.getDefault()
                                    .post(MessageEventBean(MessageEventType.ModeMessage, 3))
                            }
                            R.id.rbWeighMeal -> {
                                binding.tvSelectConsumption.text = "称重模式"
                                SPUtils.getInstance().put(Constants.MODE_VALUE, 4)
                                EventBus.getDefault()
                                    .post(MessageEventBean(MessageEventType.ModeMessage, 4))
                            }
                        }
                        popupWindow.dismiss()
                    }
                    popupWindow.setOnDismissListener(PopupWindow.OnDismissListener { })
                    val showing: Boolean = popupWindow.isShowing
                    if (!showing) {
                        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                        val measuredHeight: Int = inflate.measuredHeight
                        //show，并且可以设置位置
                        popupWindow.showAsDropDown(binding.tvSelectConsumption, 0, 20)
                    }
                }
                R.id.tvSelectDevice -> {
                    //创建对象
                    val popupWindow = PopupWindow(requireActivity())
                    val inflate: View = LayoutInflater.from(requireActivity())
                        .inflate(R.layout.layout_select_device, null)
                    var rgSelectPopup = inflate.findViewById<RadioGroup>(R.id.rgSelectPopup)

                    //设置view布局
                    popupWindow.contentView = inflate
                    popupWindow.width = binding.tvSelectDevice.width
                    //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
                    popupWindow.isTouchable = true
                    //设置背景透明
                    popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
                    //点击空白处的时候让PopupWindow消失
                    popupWindow.isOutsideTouchable = true
                    // true时，点击返回键先消失 PopupWindow
                    // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
                    // false时PopupWindow不处理返回键，默认是false
                    popupWindow.isFocusable = false
                    //设置dismiss事件
                    rgSelectPopup.setOnCheckedChangeListener { group, checkedId ->
                        when (checkedId) {
                            R.id.rbAmount -> {
                                binding.tvSelectDevice.text = "无"
                            }
                            R.id.rbNumber -> {
                                binding.tvSelectDevice.text = "送餐打印票机"
                            }
                        }
                        popupWindow.dismiss()
                    }
                    popupWindow.setOnDismissListener(PopupWindow.OnDismissListener { })
                    val showing: Boolean = popupWindow.isShowing
                    if (!showing) {
                        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                        val measuredHeight: Int = inflate.measuredHeight
                        //show，并且可以设置位置
                        popupWindow.showAsDropDown(binding.tvSelectDevice, 0, 20)
                    }
                }
            }
        }
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        LogUtils.e("消费模式" + Gson().toJson(message)+SPUtils.getInstance().getBoolean(Constants.FRAGMENT_SET, false))
        when (message.type) {
            MessageEventType.KeyEventNumber -> {
                //LogUtils.e("金额模式 按键")
                if (SPUtils.getInstance().getBoolean(Constants.FRAGMENT_SET, false)) {
                    message.content?.let {
                        LogUtils.e("消费模式 按键" + it)
                        when (it) {
                            "功能" -> {

                            }
                            "向上" -> {
                                binding.swithPay.isChecked= !binding.swithPay.isChecked
                            }
                            "向下" -> {
                                binding.swithPay.isChecked= !binding.swithPay.isChecked
                            }
                            "确认" -> {
                                var mainActivity = activity as MainActivity
                                 mainActivity.showFragment(HomeMenu.MENU1)
                            }

                        }

                    }
                }

            }
        }
    }

}