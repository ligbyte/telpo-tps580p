package com.stkj.cashier.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.stkj.cashier.BR

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
open class BaseBindingAdapter<T>(layoutResId: Int, data: MutableList<T>? = null) : BaseQuickAdapter<T, BindingViewHolder<*>>(layoutResId, data) {

    override fun convert(helper: BindingViewHolder<*>, item: T) {
        helper.mBinding?.let {
            it.setVariable(BR.data,item)
            it.executePendingBindings()
        }
    }

}