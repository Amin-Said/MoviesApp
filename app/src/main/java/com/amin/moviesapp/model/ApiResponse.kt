package com.amin.moviesapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApiResponse(
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
):Parcelable