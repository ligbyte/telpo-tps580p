package com.stkj.cashier.config;

import android.content.Context;

import androidx.room.RoomDatabase;

import com.stkj.cashier.util.util.LogUtils;
import com.google.gson.GsonBuilder;
import com.king.base.baseurlmanager.BaseUrlManager;
import com.king.frame.mvvmframe.config.FrameConfigModule;
import com.king.frame.mvvmframe.di.module.ConfigModule;
import com.stkj.cashier.App;
import com.stkj.cashier.app.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/4/23
 */
public class AppConfigModules extends FrameConfigModule {
    @Override
    public void applyOptions(Context context, ConfigModule.Builder builder) {
        LogUtils.e("isDomain" + Constants.isDomain);
        if (Constants.isDomain) {
            builder.baseUrl(BaseUrlManager.getInstance().getBaseUrl())
                    .okHttpClientOptions(new OkHttpClientOptions() {
                        @Override
                        public void applyOptions(OkHttpClient.Builder builder) {
                            //TODO 配置OkHttpClient

                            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                            builder.addInterceptor(logging);
                        }
                    });

        } else {
            builder.baseUrl(App.instance.getBASE_URL())
                    .okHttpClientOptions(new OkHttpClientOptions() {
                        @Override
                        public void applyOptions(OkHttpClient.Builder builder) {
                            //TODO 配置OkHttpClient

                            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                            //builder.addInterceptor(logging);

                            builder.addInterceptor((new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                                @Override
                                public void log(String message) {
                                    //LogUtils.d2("HTTP  " + message);
                                    LogUtils.d3("HTTP  " + message);
                                }
                            }).setLevel(HttpLoggingInterceptor.Level.BODY)));
                        }
                    });
        }
      /*  builder.baseUrl(Constants.BASE_URL)//TODO 配置Retrofit中的baseUrl
                .retrofitOptions(new RetrofitOptions() {
                    @Override
                    public void applyOptions(Retrofit.Builder builder) {
                        //TODO 配置Retrofit
                        //如想使用RxJava
                        //builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    }
                })
                .okHttpClientOptions(new OkHttpClientOptions() {
                    @Override
                    public void applyOptions(OkHttpClient.Builder builder) {
                        //TODO 配置OkHttpClient

                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                        builder.addInterceptor(logging);
                    }
                })
                .gsonOptions(new GsonOptions() {
                    @Override
                    public void applyOptions(GsonBuilder builder) {
                        //TODO 配置Gson
                    }
                })
                .roomDatabaseOptions(new RoomDatabaseOptions<RoomDatabase>() {
                    @Override
                    public void applyOptions(RoomDatabase.Builder<RoomDatabase> builder) {
                        //TODO 配置RoomDatabase
                    }
                });*/
    }
}
