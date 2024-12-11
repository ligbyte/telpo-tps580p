package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class CheckAppVersionBean {
    @SerializedName("version")
    var version: String? = null

    @SerializedName("content")
    var content: String? = null

    @SerializedName("url")
    var url: String? = null
    /*
    //系统升级是否强制(0 不强制 1 强制
     */
    @SerializedName("versionForce")
    var versionForce: String? = null
}