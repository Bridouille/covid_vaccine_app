package com.covid.vaccination.di

import com.covid.vaccination.BuildConfig
import com.covid.vaccination.network.OwidEndpoint
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://covid.ourworldindata.org/"

    @Provides @Singleton
    fun provideLoggingInterceptor() : Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton
    fun provideOkHttpClient(loggingInterceptor: Interceptor) : OkHttpClient {
        if (BuildConfig.DEBUG) {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
        return OkHttpClient.Builder()
            .build()
    }

    @Provides @Singleton
    fun providesRetrofit(moshi: Moshi, httpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides @Singleton
    fun providesOwidEndpoint(retrofit: Retrofit) = retrofit.create(OwidEndpoint::class.java)
}