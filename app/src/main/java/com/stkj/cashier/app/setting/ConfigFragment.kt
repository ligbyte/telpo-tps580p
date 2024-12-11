package com.stkj.cashier.app.setting

import android.os.Bundle
import android.util.SparseArray
import android.widget.RadioGroup
import androidx.core.util.valueIterator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.app.mode.AmountFragment
import com.stkj.cashier.app.mode.NumberFragment
import com.stkj.cashier.app.mode.PickUpFragment
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.ConfigFragmentBinding
import com.stkj.cashier.databinding.MenuFragmentBinding
import com.stkj.cashier.databinding.PasswordFragmentBinding
import com.stkj.cashier.databinding.StatisticsFragmentBinding
import com.stkj.cashier.dict.HomeMenu
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class ConfigFragment : BaseFragment<SettingViewModel, ConfigFragmentBinding>() {


    private val fragments by lazy {
        SparseArray<Fragment>()
    }

    companion object {
        fun newInstance(): ConfigFragment {
            return ConfigFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        showFragment(0)
        binding.rgSetting.check(R.id.rbServerAddress)
        binding.rgSetting.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                //
                R.id.rbServerAddress -> {
                    showFragment(0)
                }
                R.id.rbDeviceConfig -> {
                    showFragment(1)
                }
                R.id.tvWifi -> {
                    showFragment(2)
                }
                R.id.rbConsumptionSetting -> {
                    showFragment(3)
                }
                R.id.rbVoiceSetting -> {
                    showFragment(4)
                }
                R.id.rbFaceRecognition -> {
                    showFragment(5)
                }
                R.id.rbRestart -> {
                    showFragment(6)
                }

            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.config_fragment
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
        0 -> ServerAddressFragment.newInstance()
        1 -> DeviceConfigFragment.newInstance()
        2 -> WifiFragment.newInstance()
        3 -> ConsumptionSettingFragment.newInstance()
        4 -> VoiceSettingFragment.newInstance()
        5 -> FaceRecognitionFragment.newInstance()
        6 -> RestartFragment.newInstance()
        else -> throw NullPointerException()
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        when (message.type) {
          //  MessageEventType.ModeMessage -> showFragment(message.obj as Int)
        }
    }
}