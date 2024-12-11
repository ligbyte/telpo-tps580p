package com.stkj.cashier.app.main

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.core.util.valueIterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.SPUtils
//import com.stkj.cashier.BuildConfig
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.mode.AmountFragment
import com.stkj.cashier.app.mode.NumberFragment
import com.stkj.cashier.app.mode.PickUpFragment
import com.stkj.cashier.app.setting.ConfigFragment
import com.stkj.cashier.app.setting.PasswordFragment
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.MeFragmentBinding
import com.stkj.cashier.databinding.SettingFragmentBinding
import com.stkj.cashier.dict.HomeMenu
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class SettingFragment : BaseFragment<SettingViewModel, SettingFragmentBinding>(),View.OnClickListener {

    companion object{
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    private val fragments by lazy {
        SparseArray<Fragment>()
    }
    var curPage = 1

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
//        var spPassWord = SPUtils.getInstance().getString(Constants.PASS_WORD)
//        if (spPassWord == Constants.DEFAULT_PASS_WORD){
//            showFragment(1)
//        }else{
//            showFragment(0)
//        }
        showFragment(0)

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            showFragment(0)
        }
    }
    override fun onResume() {
        super.onResume()
    }

    override fun getLayoutId(): Int {
        return R.layout.setting_fragment
    }
    private fun hideAllFragment(fragmentTransaction: FragmentTransaction) {
        fragments.valueIterator().forEach {
            fragmentTransaction.hide(it)
        }
    }

    private fun showFragment(@HomeMenu menu: Int) {

        val fragmentTransaction = childFragmentManager.beginTransaction()
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
        0 -> PasswordFragment.newInstance()
        1 -> ConfigFragment.newInstance()
        else -> throw NullPointerException()
    }
    override fun onEventReceiveMsg(message: MessageEventBean){
        super.onEventReceiveMsg(message)
        when(message.type){
            MessageEventType.SettingMessage -> showFragment(message.obj as Int)
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}