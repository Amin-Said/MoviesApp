package com.amin.moviesapp.viewmodel.home

import android.accounts.NetworkErrorException
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.amin.moviesapp.model.ApiResponse
import com.amin.moviesapp.repository.MoviesRepository
import com.amin.moviesapp.utils.data.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel @ViewModelInject constructor(
    private val repository: MoviesRepository
): ViewModel(){

    var result: LiveData<ApiResult<ApiResponse>> = MutableLiveData()
    var searchResult: LiveData<ApiResult<ApiResponse>> = MutableLiveData()
    var isNetworkError = MutableLiveData(false)


    fun fetchMovieDataForSearch(key:String, lang:String, query:String){
        viewModelScope.launch {
            try{
                searchResult = repository.fetchMovieDataForSearch(key,lang,query)
                    .asLiveData(viewModelScope.coroutineContext+ Dispatchers.Default)
            }catch (e: NetworkErrorException){
                isNetworkError.value = true
                Timber.e(e)
            }
        }
    }


}