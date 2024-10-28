package com.submissionandroid.dicodingevents

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


class SettingFragment : Fragment() {

    private lateinit var switchTheme: Switch
    private lateinit var switchReminder: Switch
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        switchTheme = view.findViewById(R.id.switch_theme)
        switchReminder = view.findViewById(R.id.switch_reminder)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SettingPreferences dan ViewModel
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = SettingViewModelFactory(pref)
        settingViewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]
        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()

        // Mengamati perubahan pengaturan tema
        settingViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme.isChecked = isDarkModeActive
        }

        // Mengatur listener ketika switch diubah
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Schedule the daily reminder
                WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                    "DailyEventReminder",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    dailyWorkRequest
                )
            } else {
                // Cancel the daily reminder
                WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyEventReminder")
            }
        }

    }
}
