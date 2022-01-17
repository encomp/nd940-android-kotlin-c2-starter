package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONObject
import retrofit2.await
import timber.log.Timber

/** Defines a repository that fetches the information from the NASA Api as a List of Asteroids. */
class NasaRepository {
    private var nasaClient = NasaApi.retrofitService;

    suspend fun getImageOfDay(apiKey: String): PictureOfDay {
        val pictureOfDay = nasaClient.getImageOfTheDay(apiKey)
        return pictureOfDay.await()
    }

    suspend fun getAsteroids(
        startDate: String,
        endDate: String,
        apiKey: String
    ): List<Asteroid> {
        val asteroidResponse = nasaClient.getAsteroids(startDate, endDate, apiKey).await()
        Timber.i("AsteroidResponse: %s", asteroidResponse)
        return parseAsteroidsJsonResult(JSONObject(asteroidResponse))
    }
}
