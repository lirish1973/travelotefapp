package com.example.travelotefapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelotefapp.R
import com.example.travelotefapp.data.model.Category
import com.example.travelotefapp.ui.adapters.CategoryAdapter
import com.example.travelotefapp.ui.adapters.TourAdapter
import com.example.travelotefapp.ui.TourDetails.TourDetailFragment

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvTours: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var tourAdapter: TourAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle? 
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupCategories()
        setupTours()
        setupSearch()
        observeViewModel()
    }

    private fun initViews(view: View) {
        rvCategories = view.findViewById(R.id.rvCategories)
        rvTours = view.findViewById(R.id.rvTours)
        searchView = view.findViewById(R.id.searchView)
    }

    private fun setupCategories() {
        val categories = listOf(
            Category("1", "הכל", "🌍"),
            Category("2", "טבע", "🏞️"),
            Category("3", "היסטוריה", "🏛️"),
            Category("4", "אומנות", "🎨"),
            Category("5", "אוכל", "🍽️"),
            Category("6", "הרפתקאות", "⛰️")
        )

        categoryAdapter = CategoryAdapter(categories) { category ->
            if (category.name == "הכל") {
                viewModel.loadTours()
            } else {
                viewModel.filterByCategory(category.name)
            }
            Toast.makeText(context, "נבחרה קטגוריה: ${category.name}", Toast.LENGTH_SHORT).show()
        }

        rvCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupTours() {
        tourAdapter = TourAdapter(
            tours = emptyList(),
            onTourClick = { tour ->
                // Navigate to TourDetailFragment
                val fragment = TourDetailFragment.newInstance(tour)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onFavoriteClick = { tour ->
                viewModel.toggleFavorite(tour)
            }
        )

        rvTours.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tourAdapter
        }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchTours(newText ?: "")
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.tours.observe(viewLifecycleOwner) { tours ->
            tourAdapter.updateTours(tours)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}