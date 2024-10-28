package com.submissionandroid.dicodingevents

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.submissionandroid.dicodingevents.databinding.FragmentFavoriteBinding
import com.submissionandroid.dicodingevents.ui.detail.DetailActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val dao = FavoriteDatabase.getDatabase(requireContext()).favoriteDao()
        val repository = FavoriteRepository(dao)
        val factory = FavoriteViewModelFactory(repository)
        favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        favoriteAdapter = FavoriteEventsAdapter(emptyList()) { event ->
            // Handle click untuk menampilkan detail
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("eventId", event.id) // Kirim ID event ke DetailActivity
            startActivity(intent)
        }

        binding.recyclerViewFavoriteEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavoriteEvents.adapter = favoriteAdapter

        favoriteViewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            favoriteAdapter.updateEvents(events)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
