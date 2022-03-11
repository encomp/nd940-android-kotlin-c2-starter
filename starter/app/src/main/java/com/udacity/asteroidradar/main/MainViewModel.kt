package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaRepository
import com.udacity.asteroidradar.api.Outcome
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

    init {
        getImageOfDayFromNasa()
    }

    private fun getImageOfDayFromNasa() {
        viewModelScope.launch {
            _imageOfDayMutableLive.value = getImageOfDayFromNasaApi()
        }
    }

    private suspend fun getImageOfDayFromNasaApi(): PictureOfDay {
        var imageOfDay: PictureOfDay
        withContext(Dispatchers.IO) {
            imageOfDay = NasaRepository().getImageOfDay()
        }
        return imageOfDay
    }

    fun getAsteroidsFromDatabaseByWeek() = liveData<Outcome<List<Asteroid>>> {
        withContext(Dispatchers.IO) {
            emit(Outcome.loading())
            try {
                // Format the Date into the same format as what the API dates for queries
                val currentDate = Calendar.getInstance()
                val formatter = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.US)
                val formattedStartDate = formatter.format(currentDate.time)
                // Add 7 Days as we want to get asteroids in a week span
                currentDate.add(Calendar.DATE, 7)
                val formattedEndDate = formatter.format(currentDate.time)
                val outcome = Outcome.success(
                    asteroidDao.getAsteroidsByWeek(
                        formattedStartDate,
                        formattedEndDate
                    )
                )
                emit(outcome)
            } catch (ioException: Exception) {
                emit(
                    Outcome.error(
                        "Unable to retrieve the asteroids: $ioException.message",
                        ioException
                    )
                )
            }
        }
    }

    fun getAsteroidsFromDatabaseByDate() = liveData<Outcome<List<Asteroid>>> {
        withContext(Dispatchers.IO) {
            emit(Outcome.loading())
            try {
                val currentDateTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.US)
                val formattedDate = formatter.format(currentDateTime)
                val outcome = Outcome.success(asteroidDao.getAsteroidsByDate(formattedDate))
                emit(outcome)
            } catch (ioException: Exception) {
                emit(
                    Outcome.error(
                        "Unable to retrieve the asteroids: $ioException.message",
                        ioException
                    )
                )
            }
        }
    }

    fun getAllAsteroidsFromDatabase() = liveData<Outcome<List<Asteroid>>> {
        withContext(Dispatchers.IO) {
            emit(Outcome.loading())
            try {
                val outcome = Outcome.success(asteroidDao.getAllAsteroids())
                emit(outcome)
            } catch (ioException: Exception) {
                emit(
                    Outcome.error(
                        "Unable to retrieve the asteroids: $ioException.message",
                        ioException
                    )
                )
            }
        }
    }
}