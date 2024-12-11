package com.stkj.cashier.app.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.BaseViewModel
import com.stkj.cashier.bean.AppConfigBean
import com.stkj.cashier.bean.Bean
import com.stkj.cashier.bean.CurrentTimeInfoBean
import com.stkj.cashier.bean.IntervalCardTypeBean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class SplashViewModel @Inject constructor(application: Application, model: BaseModel?) : BaseViewModel(application, model){

    val liveData by lazy { MutableLiveData<Bean?>() }
    val intervalCardType by lazy { MutableLiveData<MutableList<IntervalCardTypeBean>>() }
    val currentTimeInfo by lazy { MutableLiveData<CurrentTimeInfoBean>()}
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
    /**
     * 获取
     */
    fun getIntervalCardType(params:Map<String,Any>){
        launch(false) {
            // TODO Http请求
            val result = apiService.getIntervalCardType(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if(isSuccess(result)){
                intervalCardType.value = result.data
            }
        }
    }
    fun currentTimeInfo(params:Map<String,Any>){
        launch (false){
            // TODO Http请求
            val result = apiService.currentTimeInfo(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if(isSuccess(result)){
                currentTimeInfo.value = result.data
            }
        }
    }
}