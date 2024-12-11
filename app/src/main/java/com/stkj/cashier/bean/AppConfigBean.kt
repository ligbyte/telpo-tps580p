package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class AppConfigBean(){
    @SerializedName("tts_status")
    private var ttsStatus: String? = null

    @SerializedName("ai_status")
    private var aiStatus: String? = null
    fun getTtsStatus(): String? {
        return ttsStatus
    }

    fun setTtsStatus(ttsStatus: String?) {
        this.ttsStatus = ttsStatus
    }

    fun getAiStatus(): String? {
        return aiStatus
    }

    fun setAiStatus(aiStatus: String?) {
        this.aiStatus = aiStatus
    }
}
