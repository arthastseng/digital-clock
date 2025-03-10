package com.ssc.android.vs_digital_clock.di

import com.ssc.android.vs_digital_clock.data.datasource.DashboardDataSource
import com.ssc.android.vs_digital_clock.data.datasource.DatabaseDataSource
import com.ssc.android.vs_digital_clock.data.datasource.SettingDataSource
import com.ssc.android.vs_digital_clock.data.datasource.impl.DashboardDataSourceImpl
import com.ssc.android.vs_digital_clock.data.datasource.impl.DatabaseDataSourceImpl
import com.ssc.android.vs_digital_clock.data.datasource.impl.SettingDataSourceImpl
import com.ssc.android.vs_digital_clock.data.db.TimeZoneDao
import com.ssc.android.vs_digital_clock.domain.repository.DashboardRepository
import com.ssc.android.vs_digital_clock.domain.repository.DatabaseRepository
import com.ssc.android.vs_digital_clock.domain.repository.SettingRepository
import com.ssc.android.vs_digital_clock.domain.repository.impl.DashboardRepositoryImpl
import com.ssc.android.vs_digital_clock.domain.repository.impl.SettingRepositoryImpl
import com.ssc.android.vs_digital_clock.domain.repository.impl.DatabaseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideSettingDataSource(): SettingDataSource {
        return SettingDataSourceImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideSettingRepository(
        datasource: SettingDataSource
    ): SettingRepository {
        return SettingRepositoryImpl(dataSource = datasource)
    }

    @Provides
    @ViewModelScoped
    fun provideDatabaseRepository(
        dataSource: DatabaseDataSource
    ): DatabaseRepository {
        return DatabaseRepositoryImpl(dataSource = dataSource)
    }

    @Provides
    @ViewModelScoped
    fun provideDatabaseDataSource(
        timeZoneDao: TimeZoneDao
    ): DatabaseDataSource {
        return DatabaseDataSourceImpl(timeZoneDao = timeZoneDao)
    }

    @Provides
    @ViewModelScoped
    fun provideDashBoardDataSource(): DashboardDataSource {
        return DashboardDataSourceImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideDashBoardRepositoryRepository(
        datasource: DashboardDataSource
    ): DashboardRepository {
        return DashboardRepositoryImpl(dataSource = datasource)
    }
}