package com.submissionandroid.dicodingevents
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {

    val favoriteEvents: LiveData<List<FavoriteEvent>> = repository.getFavoriteEvents()

    fun addToFavorites(event: FavoriteEvent) {
        viewModelScope.launch {
            repository.addFavorite(event)
        }
    }

    fun removeFromFavorites(event: FavoriteEvent) {
        viewModelScope.launch {
            repository.deleteFavorite(event)
        }
    }
}
