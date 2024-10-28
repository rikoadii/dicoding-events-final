package com.submissionandroid.dicodingevents

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    // Fungsi untuk menambahkan favorite event (suspend function untuk operasi asinkron)
    suspend fun addFavorite(favoriteEvent: FavoriteEvent) {
        favoriteDao.insert(favoriteEvent)
    }

    // Fungsi untuk menghapus favorite event (suspend function untuk operasi asinkron)
    suspend fun deleteFavorite(favoriteEvent: FavoriteEvent) {
        favoriteDao.delete(favoriteEvent)
    }

    // Cek apakah event sudah favorit
    fun isFavorite(eventId: Int): LiveData<Boolean> {
        return favoriteDao.getFavoriteById(eventId).map { favoriteEvent ->
            favoriteEvent != null
        }
    }

    // Mengambil favorite event berdasarkan ID
    fun getFavoriteById(eventId: Int): LiveData<FavoriteEvent?> {
        return favoriteDao.getFavoriteById(eventId)
    }

    // Mengambil semua favorite events
    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteDao.getFavoriteEvents()
    }
}
