package com.amin.moviesapp

import android.app.Application
import com.amin.moviesapp.utils.TimberFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        TimberFactory.setupOnDebug()

    }
}