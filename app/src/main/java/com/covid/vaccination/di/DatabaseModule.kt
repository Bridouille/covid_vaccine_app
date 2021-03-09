package com.covid.vaccination.di

import android.content.Context
import androidx.room.Room
import com.covid.vaccination.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "room_db"
    const val DB_VERSION = 1

    @Provides @Singleton
    fun providesAppDb(@ApplicationContext ctx: Context) : AppDatabase {
        return Room.databaseBuilder(ctx, AppDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides @Singleton
    fun provideVaccinationDataDao(appDatabase: AppDatabase) = appDatabase.vaccinationDataDao()
}