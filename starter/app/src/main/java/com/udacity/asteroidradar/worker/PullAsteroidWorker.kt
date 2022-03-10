package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaRepository
import com.udacity.asteroidradar.persistence.AsteroidDao
import com.udacity.asteroidradar.persistence.AsteroidDatabase
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/** Defines a worker that retrieves the information from the NASA API. */
class PullAsteroidWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.i("Inside asteroid worker.")
            val asteroidDao = AsteroidDatabase.getInstance(applicationContext).asteroidDao
            pullAsteroidsFromNasaApi(asteroidDao)
            Timber.i("Recent asteroids have been fetched.")
            Result.success()
        } catch (throwable: Throwable) {
            Timber.e("Unable to retrieve the asteroids from Nasa API: ${throwable.message}")
            Result.failure()
        }
    }

    private suspend fun pullAsteroidsFromNasaApi(asteroidDao: AsteroidDao) {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.US)
        val formattedStartDate = formatter.format(currentDate.time)
        currentDate.add(Calendar.DATE, 7)
        val formattedEndDate = formatter.format(currentDate.time)
        val asteroidList = NasaRepository().getAsteroids(
            formattedStartDate,
            formattedEndDate
        )
        asteroidDao.deleteAllAsteroidsBeforeToday(formattedStartDate)
        asteroidDao.insertAllAsteroids(asteroidList)
    }
}