package com.stkj.cashier.api

import com.stkj.cashier.app.stat.ConsumeStatBean
import com.stkj.cashier.bean.*
import retrofit2.http.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@JvmSuppressWildcards
interface ApiService {

    /**
     * 设备查询心跳接口
     */
    @GET("home/v2/index")
    suspend fun reportDeviceStatus(@QueryMap params: Map<String, Any>): Result<ReportDeviceStatusBean>

    /**
     * 设备录入时段接口
     */
    @GET("home/v2/index")
    suspend fun getIntervalCardType(@QueryMap params: Map<String, Any>): Result<MutableList<IntervalCardTypeBean>>

    /**
     * 设备录入脱机参数接口
     */
    @GET("home/v2/index")
    suspend fun offlineSet(@QueryMap params: Map<String, Any>): Result<OfflineSetBean>

    /**
     * 设备录入公司名称接口
     */
    @GET("home/v2/index")
    suspend fun companySetup(@QueryMap params: Map<String, Any>): Result<DeviceNameBean>

    /**
     * 设备录入人员信息接口
     */
    @GET("home/v2/index")
    suspend fun companyMember(@QueryMap params: Map<String, Any>): Result<CompanyMemberBean>

    /**
     * 获取消费统计
     */
    @GET("home/v2/index")
    suspend fun getConsumeStat(@QueryMap params: Map<String, Any>): ConsumeStatBean

    /**
     * 设备录入人员回调接口
     */
    @GET("home/v2/index")
    suspend fun downFaceFail(@QueryMap params: Map<String, Any>): Result<Any>

    /**
     * 设备录入人员回调接口
     */
    @GET("home/v2/index")
    suspend fun downFaceSyn(@QueryMap params: Map<String, Any>): Result<Any>

    /**
     * 设备消费接口
     */
    @GET("home/v2/index")
    suspend fun modifyBalance(@QueryMap params: Map<String, Any>): Result<ModifyBalanceBean>

    /**
     * 设备查询消费记录接口
     */
    @GET("home/v2/index")
    suspend fun consumeRecordList(@QueryMap params: Map<String, Any>): Result<ConsumeRecordListBean>

    /**
     * 设备查询退款消费记录接口
     */
    @GET("home/v2/index")
    suspend fun consumeRefundList(@QueryMap params: Map<String, Any>): Result<ConsumeRefundListBean>

    /**
     * 设备查询订餐信息接口
     */
    @GET("home/v2/index")
    suspend fun takeMealsList(@QueryMap params: Map<String, Any>): TakeMealsListResult

    /**
     * 设备订单取餐接口
     */
    @GET("home/v2/index")
    suspend fun takeMeals(@QueryMap params: Map<String, Any>): Result<TakeMealsBean>

    /**
     * 当前时段信息查询接口
     */
    @GET("home/v2/index")
    suspend fun currentTimeInfo(@QueryMap params: Map<String, Any>): Result<CurrentTimeInfoBean>

    /**
     * 设备 APP 升级接口
     */
    @GET("home/v2/index")
    suspend fun checkAppVersion(@QueryMap params: Map<String, Any>): Result<CheckAppVersionBean>

    /**
     * 设备 APP 升级回调接口
     */
    @GET("home/v2/index")
    suspend fun equUpgCallback(@QueryMap params: Map<String, Any>): Result<Any>

    /**
     * 设备订单取餐接口
     */
    @GET("home/v2/index")
    suspend fun canteenSummary(@QueryMap params: Map<String, Any>): Result<CanteenSummaryBean>

    /**
     * 设备金额退款接口
     */
    @GET("home/v2/index")
    suspend fun consumRefundMoney(@QueryMap params: Map<String, Any>): Result<TakeMealsBean>


    /**
     * 设备的网络状态
     */
    @GET("home/v2/index")
    suspend fun networkStatus(@QueryMap params: Map<String, Any>): Result<Any>

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(@FieldMap params: Map<String, Any>): Result<Login>

    /**
     * 注册
     */
    @POST("api/user/register")
    suspend fun register(@Body params: Any): Result<Any>

    /**
     * 重置密码
     */
    @POST("api/user/password/reset")
    suspend fun resetPassword(@Body params: Any): Result<Any>

    /**
     * 修改密码
     */
    @POST("api/user/password/update")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body params: Any
    ): Result<Any>

    /**
     * 获取验证码
     */
    @GET("api/sms/code")
    suspend fun getVerifyCode(@QueryMap params: Any): Result<Any>

    //--------------------------------

    @GET("api/getRequest")
    suspend fun getRequest(@Header("Authorization") token: String): Result<Bean>

    @FormUrlEncoded
    @POST("api/postRequest")
    suspend fun postRequest(
        @Header("Authorization") token: String,
        @Field("username") username: String
    ): Result<Any>

    @POST("api/postRequest")
    suspend fun postRequest(@Header("Authorization") token: String, @Body bean: Bean): Result<Any>

    @PUT("api/putRequest")
    suspend fun putRequest(@Header("Authorization") token: String, @Body bean: Bean): Result<Any>

    @PATCH("api/patchRequest")
    suspend fun patchRequest(@Header("Authorization") token: String, @Body bean: Bean): Result<Any>

    @DELETE("api/deleteRequest/{id}")
    suspend fun deleteRequest(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Result<Any>

    @GET("api/getListBean")
    suspend fun getListBean(@Header("Authorization") token: String): Result<List<Bean>>

}