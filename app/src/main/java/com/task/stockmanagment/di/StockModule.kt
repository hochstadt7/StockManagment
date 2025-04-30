package com.task.stockmanagment.di

import com.task.stockmanagment.data.repository.StockRepository
import com.task.stockmanagment.data.network.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StockModule {

    private const val BASE_URL = "https://trautocomplete.azurewebsites.net/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideStockApi(retrofit: Retrofit): StockApi =
        retrofit.create(StockApi::class.java)

    @Provides
    @Singleton
    fun provideStockRepository(api: StockApi): StockRepository =
        StockRepository(api)
}