package com.mab.mojoapp.network

import com.google.gson.GsonBuilder
import com.mab.mojoapp.BuildConfig
import com.mab.mojoapp.network.api.ConfigApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private val _loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val _okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(NullBodyResponseInterceptor())
        .addInterceptor(_loggingInterceptor)
        .cache(null)
        .build()

    private val _retrofitBuilderMojo = Retrofit.Builder()
        .baseUrl(BuildConfig.URL_BASE)
        .client(_okHttpClient)
        .addConverterFactory(getGsonConverterFactory())
        .build()

    fun <S> createGenericMojoService(serviceClass: Class<S>?): S {
        return _retrofitBuilderMojo.create(serviceClass)
    }

    fun createConfig(): ConfigApiService {
        return createGenericMojoService(ConfigApiService::class.java)
    }

    private fun getGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().apply {
        }.create())
    }

}

/**
 * Used for cases where response is 200 but body is null. That specific case, it'll crash.
 */
class NullBodyResponseInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request()
        val response: okhttp3.Response = chain.proceed(request)
        val bodyString = response.body?.string() ?: ""
        return response.newBuilder()
            .body(
                ResponseBody.create(
                    response.body!!.contentType(),
                    if (bodyString.isEmpty()) "{}" else bodyString
                )
            )
            .build()
    }
}

