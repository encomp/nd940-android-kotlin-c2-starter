package com.udacity.asteroidradar

import com.udacity.asteroidradar.api.NasaApi
import org.junit.Assert.assertNotNull
import org.junit.Test

class NasaApiTest {

    @Test
    fun getImageOfTheDay() {
        var nasaApi = NasaApi.retrofitService.getImageOfTheDay()
        var pictureOfDay = nasaApi.execute().body()
        assertNotNull(pictureOfDay)
    }
}