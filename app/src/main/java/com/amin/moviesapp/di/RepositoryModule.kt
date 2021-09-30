package com.amin.moviesapp.di

import com.amin.moviesapp.api.ApiClient
import com.amin.moviesapp.repository.MoviesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesMoviesRepository(
        apiClient: ApiClient,
        @IODispatcher dispatcher: CoroutineDispatcher
    ) = MoviesRepository(apiClient, dispatcher)


}