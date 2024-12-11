package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class ReportDeviceStatusBean {

    @SerializedName("updateConfig")
    var updateConfig: String? = null

    @SerializedName("updateUserInfo")
    var updateUserInfo: String? = null

    @SerializedName("IsOpen")
    var isOpen: Int? = null

}