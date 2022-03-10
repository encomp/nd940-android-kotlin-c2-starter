package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaRepository
import com.udacity.asteroidradar.persistence.AsteroidDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val asteroidDao: AsteroidDao) : ViewModel() {

    private val _imageOfDayMutableLive = MutableLiveData<PictureOfDay>()
    val imageOfDayLiveData: LiveData<PictureOfDay>
        get() = _imageOfDayMutableLive

    private val _asteroidsMutableLive = MutableLiveData<List<Asteroid>>()
    val asteroidsLiveData: LiveData<List<Asteroid>>
        get() = _asteroidsMutableLive

    init {
        getAsteroidsByWeekFromNasa()
        getImageOfDayFromNasa()
    }

    private fun getImageOfDayFromNasa() {
        viewModelScope.launch {
            _imageOfDayMutableLive.value = NasaRepository().getImageOfDay()
        }
    }

    fun getAsteroidsByWeekFromNasa() {
        viewModelScope.launch {
            // Format the Date into the same format as what the API dates for queries
            val currentDate = Calendar.getInstance()
            val formatter = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.US)
            val formattedStartDate = formatter.format(currentDate.time)
            // Add 7 Days as we want to get asteroids in a week span
            currentDate.add(Calendar.DATE, 7)
            val formattedEndDate = formatter.format(currentDate.time)
            val asteroidList = getAsteroidsFromDatabaseByWeek(formattedStartDate, formattedEndDate)
            _asteroidsMutableLive.value = asteroidList
        }
    }

    private suspend fun getAsteroidsFromDatabaseByWeek(
        startDate: String,
        endDate: String
    ): List<Asteroid> {
        var asteroids = listOf<Asteroid>()
        withContext(Dispatchers.IO) {
            asteroids = asteroidDao.getAsteroidsByWeek(startDate, endDate)
        }
        return asteroids
    }
}