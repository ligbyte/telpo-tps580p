package com.stkj.cashier.app.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.BaseViewModel
import com.stkj.cashier.bean.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class SettingViewModel @Inject constructor(application: Application, model: BaseModel?) : BaseViewModel(application, model){

    val liveData by lazy { MutableLiveData<MutableList<Bean>>()}
    val companyMember by lazy { MutableLiveData<CompanyMemberBean>()}
    val checkAppVersion by lazy { MutableLiveData<Result<CheckAppVersionBean>>()}
    val company by lazy { MutableLiveData<DeviceNameBean>()}
    fun getRequestData(curPage: Int,pageSize : Int){
        // TODO 模拟请求
        launch(false) {
            var start = (curPage - 1) * pageSize + 1
            var end = (curPage) * pageSize
            if(curPage > 1){
                end -= pageSize / 2
            }
            var data = ArrayList<Bean>()
            for(index in start..end){
                var bean = Bean()
                with(bean){
                    title = "列表模板标题示例$index"
                    content = "列表模板内容示例$index"
                    imageUrl = "http://jenly1314.gitee.io/medias/banner/${index % 7}.jpg"
                }
                data.add(bean)
            }
            delay(1000)
            liveData.value = data
        }
    }
    fun companyMember(params:Map<String,Any>){
        launch {
            // TODO Http请求
            val result = apiService.companyMember(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if(isSuccess(result)){
                companyMember.value = result.data
            }
        }
    }
    fun checkAppVersion(params:Map<String,Any>){
        launch {
            // TODO Http请求
            val result = apiService.checkAppVersion(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
//            if(isSuccess(result)){
//                companyMember.value = result.data
//            }
            checkAppVersion.value = result
        }
    }
    fun companySetup(params:Map<String,Any>){
        launch (false){
            // TODO Http请求
            val result = apiService.companySetup(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if(isSuccess(result)){
                company.value = result.data
            }
        }
    }
}