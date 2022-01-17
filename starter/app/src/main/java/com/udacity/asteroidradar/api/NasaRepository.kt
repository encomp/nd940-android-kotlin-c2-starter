package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONException
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
        return parseAsteroidsResponse(asteroidResponse)
    }

    private fun parseAsteroidsResponse(asteroidResponse: String): List<Asteroid> {
        val formattedDates = getNextSevenDaysFormattedDates()
        val asteroidDataJson = JSONObject(asteroidResponse)
        val nearEarthObjects = asteroidDataJson.getJSONObject("near_earth_objects")
        val asteroidList = mutableListOf<Asteroid>()
        for (date in formattedDates) {
            val asteroid = parseAsteroidsByDate(date, nearEarthObjects)
            asteroid?.let {
                asteroidList.add(it)
            }
        }
        return asteroidList
    }

    private fun parseAsteroidsByDate(date: String, nearEarthObjects: JSONObject): Asteroid? {
        try {
            val asteroidDataByDate = nearEarthObjects.getJSONArray(date)
            for (index in 0 until asteroidDataByDate.length()) {
                val asteroid = parseAsteroid(asteroidDataByDate.getJSONObject(index))
                Timber.i("Asteroid: %s", asteroid)
                return asteroid
            }
        } catch (exception: JSONException) {
            Timber.e(exception, "Unable to parse the given asteroid data.")
        }
        return null
    }

   private fun parseAsteroid(asteroidJson: JSONObject): Asteroid {
       val id = asteroidJson.getLong("id")
       val codeName = asteroidJson.getString("name")
       val closeApproachRootJson =
           asteroidJson.getJSONArray("close_approach_data")
       val closeApproachJsonObject =
           closeApproachRootJson.getJSONObject(0)
       val closeApproachDate =
           closeApproachJsonObject.getString("close_approach_date")
       val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
       val estimatedDiameter =
           asteroidJson.getJSONObject("estimated_diameter").getJSONObject("miles")
               .getDouble("estimated_diameter_max")
       val relativeVelocity =
           closeApproachJsonObject.getJSONObject("relative_velocity")
               .getDouble("miles_per_hour")
       val distanceFromEarth =
           closeApproachJsonObject.getJSONObject("miss_distance").getString("miles")
               .toDouble()
       val isPotentiallyHazardous =
           asteroidJson.getBoolean("is_potentially_hazardous_asteroid")
       return Asteroid(
           id,
           codeName,
           closeApproachDate,
           absoluteMagnitude,
           estimatedDiameter,
           relativeVelocity,
           distanceFromEarth,
           isPotentiallyHazardous
       )
   }
}
