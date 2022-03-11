package com.udacity.asteroidradar

import android.app.Application
import androidx.work.*
import com.udacity.asteroidradar.worker.PullAsteroidWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

/** Defines the Asteroid application. */
class AsteroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("Init work manager")
        val fetchAsteroidWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<PullAsteroidWorker>(24, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresCharging(true)
                    .build()
                )
                .build()

        WorkManager
            .getInstance(applicationContext).enqueueUniquePeriodicWork("fetchAsteroids",
                ExistingPeriodicWorkPolicy.KEEP,
                fetchAsteroidWorkRequest)
    }
}
