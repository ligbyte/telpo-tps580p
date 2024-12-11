package com.stkj.cashier.temp

import android.os.Bundle
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.bean.Bean
import com.stkj.cashier.databinding.TempFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class TempFragment : BaseFragment<TempViewModel, TempFragmentBinding>() {

    companion object{
        fun newInstance(): TempFragment {
            return TempFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)

        observeData()
    }

    override fun getLayoutId(): Int {
        return R.layout.temp_fragment
    }

    private fun observeData(){
        viewModel.liveData.observe(viewLifecycleOwner){
            updateUI(it)
        }
    }

    private fun updateUI(data: Bean?){
        data?.let {
            binding.data = it
        }
    }
}