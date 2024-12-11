package com.stkj.cashier.app.main

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.valueIterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.king.base.adapter.divider.DividerItemDecoration
import com.king.image.imageviewer.ImageViewer
import com.king.image.imageviewer.loader.GlideImageLoader
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.adapter.BannerImageAdapter
import com.stkj.cashier.app.adapter.BaseBindingAdapter
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.mode.AmountFragment
import com.stkj.cashier.app.mode.NumberFragment
import com.stkj.cashier.app.mode.PickUpFragment
import com.stkj.cashier.app.mode.WeighFragment
import com.stkj.cashier.bean.BannerBean
import com.stkj.cashier.bean.Bean
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.MainFragmentBinding
import com.stkj.cashier.dict.HomeMenu
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class MainFragment : BaseFragment<MainViewModel, MainFragmentBinding>() {

    public val fragments by lazy {
        SparseArray<Fragment>()
    }
    var curPage = 1

    companion object{
        fun newInstance(): MainFragment {
            LogUtils.e("MainFragment_newInstance"+this)
            return MainFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        var menu = SPUtils.getInstance().getInt(Constants.MODE_VALUE,0)
        if (menu == -1){
            showFragment(0)
        }else{
            showFragment(menu)
        }


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
        return R.layout.main_fragment
    }
    private fun hideAllFragment(fragmentTransaction: FragmentTransaction) {
        fragments.valueIterator().forEach {
            fragmentTransaction.hide(it)
        }
    }

    private fun showFragment(@HomeMenu menu: Int) {
        LogUtils.e("MainFragment_showFragment"+this)
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
            LogUtils.e("MainFragment_createFragment"+this)
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
        //LogUtils.e("AmountFragment_newInstance第一次创建"+this)
        // TODO 只需修改此处，改为对应的 Fragment
        0 -> AmountFragment().newInstance()
        1 -> NumberFragment.newInstance()
        2 -> PickUpFragment.newInstance()
        3 -> PickUpFragment.newInstance()
        4 -> WeighFragment.newInstance()
        else -> throw NullPointerException()
    }
    override fun onEventReceiveMsg(message: MessageEventBean){
        super.onEventReceiveMsg(message)
        when(message.type){
            MessageEventType.ModeMessage -> showFragment(message.obj as Int)
        }
    }

}