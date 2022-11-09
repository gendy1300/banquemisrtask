package com.ahmedelgendy.banquemisrtask.di

import com.ahmedelgendy.banquemisrtask.convert.data.remote.ConvertApis
import com.ahmedelgendy.banquemisrtask.convert.data.repository.ConvertRepoImpl
import com.ahmedelgendy.banquemisrtask.convert.domain.repositories.ConvertRepo
import com.ahmedelgendy.banquemisrtask.general.network.RetrofitImplementation
import com.ahmedelgendy.banquemisrtask.history.data.remote.HistoryApis
import com.ahmedelgendy.banquemisrtask.history.data.repository.HistoryRepoImpl
import com.ahmedelgendy.banquemisrtask.history.domain.repositories.HistoryRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideConvertApis(retrofitImplementation: RetrofitImplementation): ConvertApis {
        return retrofitImplementation.buildApi(ConvertApis::class.java)
    }

    @Singleton
    @Provides
    fun provideConvertRepo(api: ConvertApis): ConvertRepo {
        return ConvertRepoImpl(api)
    }


    @Singleton
    @Provides
    fun provideHistoryApis(retrofitImplementation: RetrofitImplementation): HistoryApis {
        return retrofitImplementation.buildApi(HistoryApis::class.java)
    }

    @Singleton
    @Provides
    fun provideHistoryRepo(api: HistoryApis): HistoryRepo {
        return HistoryRepoImpl(api)
    }

}
