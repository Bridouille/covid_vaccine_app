package com.covid.vaccination.di

import com.covid.vaccination.main.DictEffectHandlerImpl
import com.covid.vaccination.main.MainEffectHandler
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidModule {

    @Provides @Singleton
    fun providesMoshi() = Moshi.Builder().build()
}

@Module
@InstallIn(SingletonComponent::class)
interface AndroidModuleBindings {

    @Binds
    fun bindsMainEffectHandler(effectHandler: DictEffectHandlerImpl): MainEffectHandler
}