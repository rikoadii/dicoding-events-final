package com.submissionandroid.dicodingevents

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ViewModel untuk mengelola pengaturan tema aplikasi
class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {

    // Mendapatkan pengaturan tema saat ini (dark mode atau light mode)
    fun getThemeSetting(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    // Menyimpan pengaturan tema (dark mode atau light mode)
    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}
