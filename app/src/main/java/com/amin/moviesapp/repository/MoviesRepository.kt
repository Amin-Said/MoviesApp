package com.amin.moviesapp.repository

import com.amin.moviesapp.api.ApiClient
import com.amin.moviesapp.di.IODispatcher
import com.amin.moviesapp.model.ApiResponse
import com.amin.moviesapp.utils.data.ApiResult
import com.amin.moviesapp.utils.data.DataSource
import com.amin.moviesapp.utils.extensions.getResult
import com.amin.moviesapp.utils.extensions.isSuccessAndNotNull
import com.amin.moviesapp.utils.extensions.letOnFalseOnSuspend
import com.amin.moviesapp.utils.extensions.letOnTrueOnSuspend
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import timber.log.Timber

class MoviesRepository @Inject constructor(
    private val apiClient: ApiClient,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : DataSource(), MoviesRepositoryImpl {

    override fun fetchMovieDataForSearch(
        key: String,
        lang: String,
        query: String
    ): Flow<ApiResult<ApiResponse>> = flow {
        emit(ApiResult.Loading)
        networkCall {
            apiClient.fetchMovieDataForSearch(key, lang, query)
        }.let {
            it.isSuccessAndNotNull().letOnTrueOnSuspend {
                Timber.d("fetchMovieSearch apiResult : ${(it.getResult() as ApiResponse)}")
                val response = (it.getResult() as ApiResponse)
                emit(ApiResult.Success(response))
            }.letOnFalseOnSuspend {
                emit(ApiResult.Error(Exception("Unexpected error.")))
            }
        }
    }.flowOn(ioDispatcher)

}


interface MoviesRepositoryImpl {
    fun fetchMovieDataForSearch(
        key: String,
        lang: String,
        query: String
    ): Flow<ApiResult<ApiResponse>>

}