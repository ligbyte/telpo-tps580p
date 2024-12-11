package com.stkj.cashier.app.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.BaseViewModel
import com.stkj.cashier.bean.Bean
import com.stkj.cashier.bean.CanteenSummaryBean
import com.stkj.cashier.bean.ConsumeRecordListBean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(application: Application, model: BaseModel?) : BaseViewModel(application, model){

    val liveData by lazy { MutableLiveData<Bean?>() }
    val consumeRecord by lazy { MutableLiveData<ConsumeRecordListBean>()}
    val canteenSummary by lazy { MutableLiveData<CanteenSummaryBean>()}
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
    fun consumeRecordList(params:Map<String,Any>){
        launch (false){
            // TODO Http请求
            val result = apiService.consumeRecordList(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if(isSuccess(result)){
                consumeRecord.value = result.data
            }
        }
    }
    fun canteenSummary(params:Map<String,Any>){
        launch (false){
            // TODO Http请求
            val result = apiService.canteenSummary(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if(isSuccess(result)){
                canteenSummary.value = result.data
            }
        }
    }
}