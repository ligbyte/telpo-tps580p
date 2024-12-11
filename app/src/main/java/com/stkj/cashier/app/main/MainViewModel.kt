package com.stkj.cashier.app.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.stkj.cashier.util.util.LogUtils
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
class MainViewModel @Inject constructor(application: Application, model: BaseModel?) :
    BaseViewModel(application, model) {


    val liveDataBanner by lazy { MutableLiveData<List<BannerBean>>() }

    val liveData by lazy { MutableLiveData<MutableList<Bean>>() }
    val deviceStatus by lazy { MutableLiveData<Result<ReportDeviceStatusBean>>() }
    val offlineSet by lazy { MutableLiveData<OfflineSetBean>() }
    val company by lazy { MutableLiveData<Result<DeviceNameBean>>() }
    val companyMember by lazy { MutableLiveData<Result<CompanyMemberBean>>() }
    val companyMemberStatus by lazy { MutableLiveData<Result<Any>>() }
    val intervalCardType by lazy { MutableLiveData<MutableList<IntervalCardTypeBean>>() }
    val checkAppVersion by lazy { MutableLiveData<Result<CheckAppVersionBean>>() }
    val equUpgCallback by lazy { MutableLiveData<Result<Any>>() }
    val currentTimeInfo by lazy { MutableLiveData<Result<CurrentTimeInfoBean>>() }
    val netStatus by lazy { MutableLiveData<Result<Any>>() }
    fun getRequestBanner() {
        launch(false) {
            // TODO 模拟请求
            val data = arrayOf(
                "https://jenly1314.gitee.io/medias/banner/1.jpg",
                "https://jenly1314.gitee.io/medias/banner/2.jpg",
                "https://jenly1314.gitee.io/medias/banner/3.jpg",
                "https://jenly1314.gitee.io/medias/banner/4.jpg"
            )
            delay(1000)
            liveDataBanner.value = data.map { BannerBean(it) }
        }
    }

    fun getRequestData(curPage: Int, pageSize: Int) {
        // TODO 模拟请求
        launch(false) {
            var start = (curPage - 1) * pageSize + 1
            var end = (curPage) * pageSize
            if (curPage > 1) {
                end -= pageSize / 2
            }
            var data = ArrayList<Bean>()
            for (index in start..end) {
                var bean = Bean()
                with(bean) {
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

    fun reportDeviceStatus(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.reportDeviceStatus(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            deviceStatus.value = result
        }
    }

    /**
     * 设备的网络状态
     */
    fun networkStatus(params: Map<String, Any>, errorCallback: suspend (Throwable) -> Unit) {
        launch(false, block = {
            // TODO Http请求
            val result = apiService.networkStatus(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            netStatus.value = result
        }, error = errorCallback)
    }

    fun offlineSet(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.offlineSet(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if (isSuccess(result)) {
                offlineSet.value = result.data
            }
        }
    }

    fun companySetup(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.companySetup(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if (isSuccess(result)) {
                company.value = result
            }
        }
    }

    fun companyMember(params: Map<String, Any>) {
        launch(false, block =  {
            // TODO Http请求
            val result = apiService.companyMember(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理

//            if(isSuccess(result)){
           // companyMember.value = result
            callback?.onDataReceived(result)
            LogUtils.e("人脸录入回调" + result)
//            }
        }, error = {
            callback?.onError(it)
        })
    }

    fun downFaceFail(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.downFaceFail(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            /* if(isSuccess(result)){
                 companyMemberStatus.value = result
             }*/
            companyMemberStatus.value = result
        }
    }

    /**
     * 获取
     */
    fun getIntervalCardType(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.getIntervalCardType(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if (isSuccess(result)) {
                intervalCardType.value = result.data
            }
        }
    }

    fun checkAppVersion(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.checkAppVersion(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
//            if(isSuccess(result)){
//                companyMember.value = result.data
//            }
            checkAppVersion.value = result
        }
    }

    fun equUpgCallback(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.equUpgCallback(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
//            if(isSuccess(result)){
//                companyMember.value = result.data
//            }
            equUpgCallback.value = result
        }
    }

    fun currentTimeInfo(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.currentTimeInfo(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
//            if(isSuccess(result)){
            currentTimeInfo.value = result
//            }
        }
    }

    // 定义一个回调接口
    interface MyCompanyMemberCallback {
        fun onDataReceived(data: Result<CompanyMemberBean>)
        fun onError(error: Throwable)
    }

    // 声明接口变量
    private var callback: MyCompanyMemberCallback? = null

    // 设置回调接口
    fun setCallback(callback: MyCompanyMemberCallback) {
        this.callback = callback
    }
}