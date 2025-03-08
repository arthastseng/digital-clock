package com.ssc.android.vs_digital_clock.di

import android.content.Context
import androidx.room.Room
import com.ssc.android.vs_digital_clock.data.db.TimeZoneDao
import com.ssc.android.vs_digital_clock.data.db.TimeZoneDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): TimeZoneDatabase {
        return Room.databaseBuilder(
            context,
            TimeZoneDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideTimeZoneDao(database: TimeZoneDatabase): TimeZoneDao {
        return database.timeZoneDao()
    }
}