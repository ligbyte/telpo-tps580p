package com.stkj.cashier.app.setting

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM
import androidx.core.view.isVisible
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.MenuFragmentBinding
import com.stkj.cashier.databinding.PasswordFragmentBinding
import com.stkj.cashier.databinding.StatisticsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class PasswordFragment : BaseFragment<SettingViewModel, PasswordFragmentBinding>() ,View.OnClickListener{

    var floatView: View? = null

    companion object {
        fun newInstance(): PasswordFragment {
            return PasswordFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        binding.tvConfirm.setOnClickListener(this)
        binding.tvChangePassword.setOnClickListener(this)
        binding.tvConfirmPassword.setOnClickListener(this)
        binding.tvCancel.setOnClickListener(this)
//        binding.etPassword.getViewTreeObserver()
//            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                //当键盘弹出隐藏的时候会 调用此方法。
//                override fun onGlobalLayout() {
//                    val r = Rect()
//                    //获取当前界面可视部分
//                    requireActivity().getWindow().getDecorView()
//                        .getWindowVisibleDisplayFrame(r)
//                    //获取屏幕的高度
//                    val screenHeight: Int =
//                        requireActivity().getWindow().getDecorView().getRootView().getHeight()
//                    //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
//                    val heightDifference: Int = screenHeight - r.bottom
//                    Log.d("Keyboard Size", "Size: $heightDifference")
//                    showAViewOverKeyBoard(heightDifference)
//                }
//            })

        binding.etPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                /*隐藏软键盘*/
                KeyboardUtils.hideSoftInput( binding.etPassword)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.password_fragment
    }

    private fun showAViewOverKeyBoard(heightDifference: Int) {
        if (heightDifference > 0) { //显示
            if (floatView == null) { //第一次显示的时候创建  只创建一次
                floatView = View.inflate(requireContext(), R.layout.layout_float_keyboard, null)
                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(-1, -2)
                params.bottomMargin = heightDifference
                binding.llPasswordContainer.addView(floatView, params)
            }
            floatView!!.visibility = View.VISIBLE
        } else { //隐藏
            if (view != null) {
                floatView!!.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        if (v!=null){
            when(v.id){
                R.id.tvConfirm->{
                    if (binding.etPassword.text.isNotEmpty()) {
                        var spPassWord = SPUtils.getInstance().getString(Constants.PASS_WORD,Constants.DEFAULT_PASS_WORD)
                        var inputPassWord = binding.etPassword.text.toString()
                        if (inputPassWord == spPassWord) {
                            EventBus.getDefault().post(MessageEventBean(MessageEventType.SettingMessage,1))
                            KeyboardUtils.hideSoftInput( binding.etPassword)
                            binding.etPassword.setText("")
                        } else {
                            ToastUtils.showLong("密码不正确")
                        }
                    }else{
                        ToastUtils.showLong("请输入密码")
                    }
                }
                R.id.tvChangePassword->{
                    binding.llInputPassword.visibility = View.GONE
                    binding.llChanePassword.visibility = View.VISIBLE
                    var spPassWord = SPUtils.getInstance().getString(Constants.PASS_WORD,Constants.DEFAULT_PASS_WORD)
                    binding.etOldPassword.setText(spPassWord)

                }
                R.id.tvConfirmPassword ->{
                    changePassword()
                }
                R.id.tvCancel ->{
                    binding.llInputPassword.visibility = View.VISIBLE
                    binding.llChanePassword.visibility = View.GONE
                }
            }
        }
    }

    private fun changePassword() {
        if (binding.etNewPassword.text.toString().isNullOrEmpty()){
            ToastUtils.showLong("请输入新密码")
            return
        }
        if (binding.etNewPassword.text.toString().length<6||binding.etNewPassword.text.toString().length>12){
            ToastUtils.showLong("请输入6~12位数字或字母")
            return
        }
        if (binding.etNewPasswordNext.text.toString().isNullOrEmpty()){
            ToastUtils.showLong("请再次输入新密码")
            return
        }
        if (binding.etNewPasswordNext.text.toString().length<6||binding.etNewPasswordNext.text.toString().length>12){
            ToastUtils.showLong("请输入6~12位数字或字母")
            return
        }
        if (binding.etNewPassword.text.toString() != binding.etNewPasswordNext.text.toString()){
            ToastUtils.showLong("密码不一致")
            return
        }
        SPUtils.getInstance().put(Constants.PASS_WORD,binding.etNewPassword.text.toString())
        binding.llInputPassword.visibility = View.VISIBLE
        binding.llChanePassword.visibility = View.GONE
    }


}