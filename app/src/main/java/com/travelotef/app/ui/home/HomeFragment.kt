package com.travelotef.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.travelotef.app.R
import com.travelotef.app.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * Home Fragment - displays available tours with sync capability
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize UI components
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // TODO: Setup RecyclerView for tours list
        // TODO: Setup search functionality
        // TODO: Setup SwipeRefreshLayout for pull to refresh
        // TODO: Setup filter buttons
        
        // For now, trigger initial sync
        viewModel.loadTours()
    }
    
    private fun observeViewModel() {
        // Observe tours data
        viewModel.tours.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator
                    // TODO: Show progress bar
                }
                is Resource.Success -> {
                    // Update UI with tours
                    val tours = resource.data
                    // TODO: Update RecyclerView adapter
                    Toast.makeText(context, "Loaded ${tours?.size ?: 0} tours", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    // Show error message
                    Toast.makeText(context, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}