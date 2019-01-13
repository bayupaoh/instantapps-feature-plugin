package com.suitmedia.sample.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.suitmedia.sample.core.BuildConfig
import com.suitmedia.sample.data.api.APIService
import com.suitmedia.sample.di.scope.SuitCoreApplicationScope
import com.suitmedia.sample.data.api.model.BaseUrlHolder
import com.suitmedia.sample.data.api.model.User
import com.suitmedia.sample.data.api.model.deserelializer.UserDeserializer
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [(NetworkModule::class)])
class APIServiceModule {

    @Provides
    @SuitCoreApplicationScope
    fun apiService(retrofit: Retrofit): APIService = retrofit.create(APIService::class.java)

    @Provides
    @SuitCoreApplicationScope
    fun rxJavaCallAdapter(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    @Provides
    @SuitCoreApplicationScope
    fun gSon(): Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .registerTypeAdapter(User::class.java, UserDeserializer())
            .create()

    @Provides
    @SuitCoreApplicationScope
    fun retrofit(okHttpClient: OkHttpClient, rxJava2CallAdapterFactory: RxJava2CallAdapterFactory, baseUrlHolder: BaseUrlHolder?): Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gSon()))
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .client(okHttpClient)
            .baseUrl(baseUrlHolder?.baseUrl.toString())
            .build()

    @SuitCoreApplicationScope
    @Provides
    fun provideBaseUrlHolder(): BaseUrlHolder? {
        return BaseUrlHolder(BuildConfig.BASE_URL)
    }
}