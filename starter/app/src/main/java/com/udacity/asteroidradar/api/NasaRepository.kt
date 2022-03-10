package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONObject
import retrofit2.await
import timber.log.Timber

/** Defines a repository that fetches the information from the NASA Api as a List of Asteroids. */
class NasaRepository {
    private var nasaClient = NasaApi.retrofitService;

    suspend fun getImageOfDay(): PictureOfDay {
        val pictureOfDay = nasaClient.getImageOfTheDay(BuildConfig.API_KEY)
        return pictureOfDay.await()
    }

    suspend fun getAsteroids(
        startDate: String,
        endDate: String
    ): List<Asteroid> {
        val asteroidResponse = nasaClient.getAsteroids(startDate, endDate, BuildConfig.API_KEY).await()
        Timber.i("AsteroidResponse: %s", asteroidResponse)
        return parseAsteroidsJsonResult(JSONObject(asteroidResponse))
    }
}
