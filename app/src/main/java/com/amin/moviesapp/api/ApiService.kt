package com.amin.moviesapp.api

import com.amin.moviesapp.model.ApiResponse
import com.amin.moviesapp.utils.Config
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @GET(Config.BASE_RETURN)
    @Headers("Content-Type: application/json")
    suspend fun fetchMoviesDataForSearch(
        @Query(Config.KEY_PARAM) apiKey: String,
        @Query(Config.LANG_PARAM) hash: String,
        @Query(Config.SEARCH_PARAM) searchQuery: String
    ): Response<ApiResponse>
}