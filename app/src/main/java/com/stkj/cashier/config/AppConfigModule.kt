package com.stkj.cashier.config

import android.content.Context
import com.king.base.baseurlmanager.BaseUrlManager
import com.king.frame.mvvmframe.config.AppliesOptions
import com.king.frame.mvvmframe.config.FrameConfigModule
import com.king.frame.mvvmframe.di.module.ConfigModule
import com.stkj.cashier.App
import com.stkj.cashier.app.Constants

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class AppConfigModule : FrameConfigModule() {
    override fun applyOptions(context: Context, builder: ConfigModule.Builder) {
        if (Constants.isDomain) {
            builder.baseUrl(BaseUrlManager.getInstance().baseUrl)

        } else {
            builder.baseUrl(App.BASE_URL)
        }
    }

}