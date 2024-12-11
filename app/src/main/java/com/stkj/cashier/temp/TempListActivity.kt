package com.stkj.cashier.temp

import androidx.recyclerview.widget.RecyclerView
import com.stkj.cashier.R
import com.stkj.cashier.app.adapter.BaseBindingAdapter
import com.stkj.cashier.app.base.ListActivity
import com.stkj.cashier.bean.Bean
import com.stkj.cashier.databinding.ListActivityBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class TempListActivity : ListActivity<Bean, TempListViewModel, ListActivityBinding>() {

    override fun requestData(curPage: Int) {
        super.requestData(curPage)
        viewModel.requestData(curPage, pageSize)
    }

    override fun createAdapter(): BaseBindingAdapter<Bean> {
        return BaseBindingAdapter(R.layout.rv_item)
    }

    override fun smartRefreshLayout(): SmartRefreshLayout {
        return binding.srl
    }

    override fun recyclerView(): RecyclerView {
        return binding.rv
    }
}