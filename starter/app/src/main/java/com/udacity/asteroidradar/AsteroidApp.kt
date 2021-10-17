package com.udacity.asteroidradar

import android.app.Application
import timber.log.Timber

/** Defines the Asteroid application. */
class AsteroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
