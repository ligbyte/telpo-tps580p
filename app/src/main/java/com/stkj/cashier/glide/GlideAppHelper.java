package com.stkj.cashier.glide;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/7/19
 */

import android.app.Application;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * glide初始化
 */
public class GlideAppHelper {

    public static void init(Application application) {
        //替换默认的okhttpClient
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                requestBuilder.addHeader("Referer","http://console.shengtudx.cn/");
                return chain.proceed(requestBuilder.build());
            }
        });
        GlideApp.get(application).getRegistry().replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okhttpBuilder.build()));
    }

}
