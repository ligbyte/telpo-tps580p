package com.stkj.cashier.app.stat

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.BaseViewModel
import com.stkj.cashier.bean.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class ConsumeStatViewModel @Inject constructor(application: Application, model: BaseModel?) : BaseViewModel(application, model){

    val consumeStatResult by lazy { MutableLiveData<ConsumeStatBean>()}

    fun requestConsumeStat(params: Map<String, Any>, errorCallback: suspend (Throwable) -> Unit) {
        launch(false, block = {
            // TODO Http请求
            val result = apiService.getConsumeStat(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            consumeStatResult.value = result
        }, error = errorCallback)
    }

}