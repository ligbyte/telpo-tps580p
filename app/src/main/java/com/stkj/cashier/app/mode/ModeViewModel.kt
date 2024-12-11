package com.stkj.cashier.app.mode

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.king.base.util.SystemUtils
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.BaseViewModel
import com.stkj.cashier.bean.*
import com.stkj.cashier.config.MessageEventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class ModeViewModel @Inject constructor(application: Application, model: BaseModel?) :
    BaseViewModel(application, model) {


    val liveDataBanner by lazy { MutableLiveData<List<BannerBean>>() }

    val liveData by lazy { MutableLiveData<MutableList<Bean>>() }
    val modifyBalance by lazy { MutableLiveData<Result<ModifyBalanceBean>>() }
    val consumeRecord by lazy { MutableLiveData<ConsumeRecordListBean>() }
    val takeMealsList by lazy { MutableLiveData<TakeMealsListResult>() } //订餐信息列表
    val takeMeals by lazy { MutableLiveData<Result<TakeMealsBean>>() } //已出餐的订单信息
    val currentTimeInfo by lazy { MutableLiveData<Result<CurrentTimeInfoBean>>() }

    val consumeRefund by lazy { MutableLiveData<ConsumeRefundListBean>() }//查询退款订单列表

    val consumeRefundList by lazy { MutableLiveData<Result<ConsumeRefundListBean>>() }//查询退款订单列表

    val consumeRefundResult by lazy { MutableLiveData<Result<Any>>() }//处理退款

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

    fun modifyBalance(params: Map<String, Any>) {
        if (SystemUtils.isNetWorkActive(getApp())) {
            launch(false, block = {
                // TODO Http请求
                val result = apiService.modifyBalance(params)
                // TODO 只需处理成功的场景，失败的场景都已统一处理
                /* ToastUtils.showShort("失败")
                 if(!isSuccess(result)){
                     ToastUtils.showShort("断网"+Gson().toJson(result))
                 }*/

//            if(isSuccess(result)){
                modifyBalance.value = result
//            }
            }, error = {
                Timber.w(it)
                if (SystemUtils.isNetWorkActive(getApp())) {
                    when (it) {
                        is SocketTimeoutException -> ttsSpeak("网络连接超时")
                        is ConnectException -> ttsSpeak("网络连接失败")
                        else -> ttsSpeak("系统异常,请重试")
                    }
                } else {
                    ttsSpeak("网络已断开，请检查网络。")
                }
                //金额模式
                EventBus.getDefault().post(MessageEventBean(MessageEventType.AmountCancel))
            })
        } else {
            ttsSpeak(getString(R.string.result_network_unavailable_error))
            //金额模式
            EventBus.getDefault().post(MessageEventBean(MessageEventType.AmountCancel))
        }


    }

    /**
     * 设备查询消费记录接口
     */
    fun consumeRecordList(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.consumeRecordList(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            if (isSuccess(result)) {
                consumeRecord.value = result.data
            }
        }
    }

    /**
     * 设备查询消费记录接口
     */
    fun consumeRefundList(params: Map<String, Any>) {
        launch(false, block =  {
            // TODO Http请求
            val result = apiService.consumeRefundList(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            consumeRefundList.value = result
        }, error = {
            Timber.w(it)
            if (SystemUtils.isNetWorkActive(getApp())) {
                when (it) {
                    is SocketTimeoutException -> ttsSpeak("网络连接超时")
                    is ConnectException -> ttsSpeak("网络连接失败")
                    else -> ttsSpeak("系统异常,请重试")
                }
            } else {
                ttsSpeak("网络已断开，请检查网络。")
            }
            EventBus.getDefault()
                .post(MessageEventBean(MessageEventType.AmountRefund))
        })
    }

    /**
     * 订单退款
     */
    fun consumeRefundResult(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.downFaceSyn(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            LogUtils.e("订单退款"+Gson().toJson(result))
            consumeRefundResult.value = result
        }
    }

    /**
     * 设备查询订餐信息接口
     */
    fun takeMealsList(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.takeMealsList(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
//            if(isSuccess(result)){
            takeMealsList.value = result
//            }
        }
    }

    /**
     * 设备订单取餐接口
     */
    fun takeMeals(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.takeMeals(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            //  if(isSuccess(result)){
            takeMeals.value = result
            //  }
        }
    }

    /**
     * 2.设备订单取餐接口
     */
    fun takeMeal(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.takeMeals(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            LogUtils.e("默认出餐" + Gson().toJson(result))
            //  if(isSuccess(result)){
            //takeMeals.value = result
            //  }
        }
    }

    /**
     * 当前时段信息查询接口
     */
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

    /**
     * 订单支付中,状态查询
     */
    fun payStatus(params: Map<String, Any>) {
        launch(false) {
            // TODO Http请求
            val result = apiService.modifyBalance(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            LogUtils.e("支付中" + Gson().toJson(result))
            modifyBalance.value = result
        }
    }

    fun ttsSpeak(value: String) {
        App.TTS.setSpeechRate(1f)
        App.TTS.speak(
            value,
            TextToSpeech.QUEUE_FLUSH, null
        )
    }
}