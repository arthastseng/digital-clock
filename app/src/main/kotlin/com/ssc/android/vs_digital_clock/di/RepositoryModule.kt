package com.ssc.android.vs_digital_clock.di

import com.ssc.android.vs_digital_clock.data.datasource.SettingDataSource
import com.ssc.android.vs_digital_clock.data.datasource.impl.SettingDataSourceImpl
import com.ssc.android.vs_digital_clock.domain.repository.SettingRepository
import com.ssc.android.vs_digital_clock.domain.repository.impl.SettingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
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
}