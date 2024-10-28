package com.submissionandroid.dicodingevents
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favoriteEvent: FavoriteEvent)

    @Delete
    fun delete(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_events WHERE id = :eventId LIMIT 1")
    fun getFavoriteById(eventId: Int): LiveData<FavoriteEvent?>

    @Query("SELECT * FROM favorite_events")
    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>>
}
