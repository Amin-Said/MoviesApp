package com.amin.moviesapp.viewmodel.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amin.moviesapp.model.Result

class DetailsViewModel @ViewModelInject constructor() : ViewModel() {
    var result: MutableLiveData<Result> = MutableLiveData()
    var posterUrl: MutableLiveData<String> = MutableLiveData()

}