package com.stkj.cashier.temp

import android.os.Bundle
import android.view.View
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseActivity
import com.stkj.cashier.bean.Bean
import com.stkj.cashier.databinding.TempActivityBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class TempActivity : BaseActivity<TempViewModel, TempActivityBinding>(){

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        observeData()
    }

    override fun getLayoutId(): Int {
        return R.layout.temp_activity
    }

    private fun observeData(){
        viewModel.liveData.observe(this){
            updateUI(it)
        }
    }

    private fun updateUI(data: Bean?){
        data?.let {
            binding.data = it
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
    }
}