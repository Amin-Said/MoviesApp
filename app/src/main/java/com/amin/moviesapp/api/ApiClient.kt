package com.amin.moviesapp.api

import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchMovieDataForSearch(key: String, lang: String,query: String) =
        apiService.fetchMoviesDataForSearch(key, lang,query)
}