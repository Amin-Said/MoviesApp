package com.amin.moviesapp.di

import com.amin.moviesapp.api.ApiClient
import com.amin.moviesapp.api.ApiService
import com.amin.moviesapp.utils.Config
import com.amin.moviesapp.utils.HttpRequestInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient():OkHttpClient
            = OkHttpClient.Builder()
        .addInterceptor(HttpRequestInterceptor())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit:Retrofit) = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiClient(apiService: ApiService) = ApiClient(apiService)
}