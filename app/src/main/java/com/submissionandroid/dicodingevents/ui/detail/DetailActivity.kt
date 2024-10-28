package com.submissionandroid.dicodingevents.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.submissionandroid.dicodingevents.EventsViewModel
import com.submissionandroid.dicodingevents.EventsViewModelFactory
import com.submissionandroid.dicodingevents.repository.EventsRepository
import com.submissionandroid.dicodingevents.retrofit.ApiConfig
import com.submissionandroid.dicodingevents.R
import com.submissionandroid.dicodingevents.Event
import com.submissionandroid.dicodingevents.FavoriteDatabase
import com.submissionandroid.dicodingevents.FavoriteEvent
import com.submissionandroid.dicodingevents.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var viewModel: EventsViewModel
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var eventName: TextView
    private lateinit var eventDate: TextView
    private lateinit var eventDescription: TextView
    private lateinit var eventOwner: TextView
    private lateinit var eventCity: TextView
    private lateinit var eventQuota: TextView
    private lateinit var eventCategory: TextView
    private lateinit var eventSummary: TextView
    private lateinit var eventRegistrants: TextView
    private lateinit var eventImage: ImageView
    private lateinit var eventMediaCover: ImageView
    private lateinit var btnEventLink: Button
    private lateinit var eventQuotaLeft: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnFavorite: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Inisialisasi ViewModel dan ProgressBar
        val repository = EventsRepository(ApiConfig.getApiService())
        val viewModelFactory = EventsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[EventsViewModel::class.java]
        favoriteRepository = FavoriteRepository(FavoriteDatabase.getDatabase(this).favoriteDao())

        // Inisialisasi Views
        eventName = findViewById(R.id.tvEventNameDetail)
        eventDate = findViewById(R.id.tvEventDateDetail)
        eventDescription = findViewById(R.id.tvEventDescription)
        eventOwner = findViewById(R.id.tvEventOwner)
        eventCity = findViewById(R.id.tvEventCity)
        eventQuota = findViewById(R.id.tvEventQuota)
        eventCategory = findViewById(R.id.tvEventCategory)
        eventSummary = findViewById(R.id.tvEventSummary)
        eventRegistrants = findViewById(R.id.tvEventRegistrants)
        eventImage = findViewById(R.id.ivEventImageDetail)
        eventMediaCover = findViewById(R.id.ivEventMediaCover)
        btnEventLink = findViewById(R.id.btnEventLink)
        eventQuotaLeft = findViewById(R.id.tvEventQuotaLeft)
        progressBar = findViewById(R.id.progressBarLoading)
        btnFavorite = findViewById(R.id.btnFavorite)

        // Ambil eventId dari Intent
        val eventId = intent.getIntExtra("eventId", -1)
        if (eventId != -1) {
            showLoading(true)
            viewModel.fetchEventDetail(eventId)
        }

        // Observe data dari ViewModel
        viewModel.eventDetail.observe(this) { response ->
            showLoading(false)
            response?.let {
                showEventDetails(it.event)
                checkFavoriteStatus(it.event?.id ?: return@observe) // Periksa status favorit
            }
        }

        // Button untuk mengubah status favorite
        btnFavorite.setOnClickListener {
            val currentEvent = viewModel.eventDetail.value?.event
            currentEvent?.let { eventDetail -> toggleFavorite(eventDetail) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showEventDetails(event: Event?) {
        event?.let {
            eventName.text = it.name ?: "No Name Available"
            eventDate.text = "${it.beginTime} - ${it.endTime}"
            eventDescription.text = HtmlCompat.fromHtml(
                it.description ?: "No Description Available",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            eventOwner.text = "Owner: ${it.ownerName ?: "Unknown"}"
            eventCity.text = "City: ${it.cityName ?: "Unknown"}"
            eventQuota.text = "Quota: ${it.quota ?: "No Limit"}"
            eventCategory.text = "Category: ${it.category ?: "Not Specified"}"
            eventSummary.text = it.summary ?: "No Summary Available"
            eventRegistrants.text = "Registrants: ${it.registrants ?: 0}"
            eventQuotaLeft.text = "Quota Left: ${(it.quota ?: 0) - (it.registrants ?: 0)}"

            // Event link
            btnEventLink.setOnClickListener {
                val link = event.link
                if (!link.isNullOrBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }
            }

            // Load images with Glide
            Glide.with(this).load(it.imageLogo).into(eventImage)
            Glide.with(this).load(it.mediaCover).into(eventMediaCover)
        }
    }
    private fun checkFavoriteStatus(eventId: Int) {
        favoriteRepository.getFavoriteById(eventId).observe(this) { favoriteEvent ->
            btnFavorite.text = if (favoriteEvent != null) {
                getString(R.string.remove_from_favorites)
            } else {
                getString(R.string.add_to_favorites)
            }
        }
    }

    private fun toggleFavorite(event: Event) {
        val favoriteEvent = FavoriteEvent(
            id = event.id ?: 0,
            name = event.name ?: "",
            date = "${event.beginTime} - ${event.endTime}",
            description = event.description ?: "",
            imageLogo = event.imageLogo ?: ""
        )

        lifecycleScope.launch {
            // Ambil status favorite
            val favoriteEventLiveData = favoriteRepository.getFavoriteById(event.id ?: 0)

// Tempatkan observer di dalam coroutine dan batalkan setelah mendapatkan data
            favoriteEventLiveData.observe(this@DetailActivity) { favoriteEventInDb ->
                // Batalkan observer setelah mendapatkan data
                favoriteEventLiveData.removeObservers(this@DetailActivity)

                lifecycleScope.launch(Dispatchers.IO) {
                    if (favoriteEventInDb != null) {
                        // Hapus dari favorit
                        Log.d("FavoriteToggle", "Deleting favorite: $favoriteEventInDb")
                        favoriteRepository.deleteFavorite(favoriteEventInDb)
                        Log.d("FavoriteToggle", "Deleted favorite: $favoriteEventInDb")
                    } else {
                        // Tambah ke favorit
                        favoriteRepository.addFavorite(favoriteEvent)
                        Log.d("FavoriteToggle", "Added to favorites: $favoriteEvent")
                    }
                }
                // Perbarui status favorit di UI
                checkFavoriteStatus(event.id ?: 0)  // Panggil kembali untuk memperbarui UI
            }

        }
    }



    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
