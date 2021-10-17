package com.udacity.asteroidradar

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.asteroidradar.persistence.AsteroidDatabase
import com.udacity.asteroidradar.persistence.AsteroidDatabaseDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AsteroidDatabaseTest {

    private lateinit var asteroidDb: AsteroidDatabase
    private lateinit var asteroidDao: AsteroidDatabaseDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        asteroidDb = Room.inMemoryDatabaseBuilder(context, AsteroidDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        asteroidDao = asteroidDb.asteroidDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        asteroidDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() {
        val asteroid = Asteroid(1L, "new", "2021-10-02", 1.0, 1.0, 1.0, 1.0, true)
        asteroidDao.insertAll(asteroid)
        val asteroidFromDb = asteroidDao.findById(1L)
        assertEquals(asteroidFromDb?.id, asteroid.id)
        assertEquals(asteroidFromDb?.codename, asteroid.codename)
        assertEquals(asteroidFromDb?.closeApproachDate, asteroid.closeApproachDate)
        assertEquals(asteroidFromDb?.absoluteMagnitude, asteroid.absoluteMagnitude)
        assertEquals(asteroidFromDb?.estimatedDiameter, asteroid.estimatedDiameter)
        assertEquals(asteroidFromDb?.relativeVelocity, asteroid.relativeVelocity)
        assertEquals(asteroidFromDb?.distanceFromEarth, asteroid.distanceFromEarth)
        assertEquals(asteroidFromDb?.isPotentiallyHazardous, asteroid.isPotentiallyHazardous)
    }
}