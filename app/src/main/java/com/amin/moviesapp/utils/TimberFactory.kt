package com.amin.moviesapp.utils

import timber.log.Timber

object TimberFactory {
    fun setupOnDebug(){
        Timber.plant(Timber.DebugTree())
    }
}