package com.stkj.cashier.app.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.BaseViewModel
import com.stkj.cashier.bean.Bean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class MenuViewModel @Inject constructor(application: Application, model: BaseModel?) : BaseViewModel(application, model){

    val liveData by lazy { MutableLiveData<Bean?>() }

    /**
     * 请求示例
     */
    fun requestData(){
        launch {
            // TODO Http请求
            val result = apiService.getRequest("")
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if(isSuccess(result)){
                liveData.value = result.data
            }
        }
    }

}