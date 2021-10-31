package com.udacity.asteroidradar.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

/**
 * Defines the data access object of the Asteroid database.
 */
@Dao
interface AsteroidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: Asteroid)

    @Update
    fun update(asteroid: Asteroid)

    @Query("SELECT * from asteroid WHERE id = :key")
    fun findById(key: Long): Asteroid?

    @Query("DELETE from asteroid")
    fun deleteAll()

    @Query("SELECT * FROM asteroid where closeApproachDate >= date() ORDER BY closeApproachDate")
    fun getAllAsteroids(): LiveData<List<Asteroid>>
}