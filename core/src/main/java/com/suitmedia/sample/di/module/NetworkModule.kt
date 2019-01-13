package com.suitmedia.sample.di.module

import android.content.Context
import com.suitmedia.sample.core.BuildConfig
import com.suitmedia.sample.di.scope.SuitCoreApplicationScope
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

@Module(includes = [(ContextModule::class)])
class NetworkModule {

    @Provides
    @SuitCoreApplicationScope
    fun lI(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @SuitCoreApplicationScope
    fun cache(file: File): Cache = Cache(file, 10 * 1000 * 1000)

    @Provides
    @SuitCoreApplicationScope
    fun file(context: Context): File = File(context.cacheDir, "okhttp_cache")

    @Provides
    @SuitCoreApplicationScope
    fun okHttpClient(cache: Cache) = getOkHttpClient(cache)

    private fun getOkHttpClient(cache: Cache): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(lI())
                    .cache(cache).build()!!
        } else {
            OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .cache(cache).build()!!
        }
    }
}