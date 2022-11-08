package com.ahmedelgendy.banquemisrtask.di

import com.ahmedelgendy.banquemisrtask.convert.data.remote.ConvertApis
import com.ahmedelgendy.banquemisrtask.convert.data.repository.ConvertRepoImpl
import com.ahmedelgendy.banquemisrtask.convert.domain.repositories.ConvertRepo
import com.ahmedelgendy.banquemisrtask.general.network.RetrofitImplementation
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

}
