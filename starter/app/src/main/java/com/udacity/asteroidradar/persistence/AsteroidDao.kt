package com.udacity.asteroidradar.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

/**
 * Defines the data access object of the Asteroid database.
 */
@Dao
interface AsteroidDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: Asteroid)

    @Update
    suspend fun update(asteroid: Asteroid)

    @Query("SELECT * from asteroid WHERE id = :key")
    suspend fun findById(key: Long): Asteroid?

    @Query("DELETE from asteroid")
    suspend fun deleteAll()

    @Query("SELECT * FROM asteroid where closeApproachDate >= date() ORDER BY closeApproachDate")
    fun getAllAsteroids(): LiveData<List<Asteroid>>
}