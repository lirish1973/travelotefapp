package com.travelotef.app.ui.mytours

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.travelotef.app.R
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.ui.home.TourAdapter
import com.travelotef.app.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * My Tours Fragment - displays favorite/saved tours
 * Supports swipe-to-remove and navigation to tour detail
 */
@AndroidEntryPoint
class MyToursFragment : Fragment() {

    private val viewModel: MyToursViewModel by viewModels()
    private lateinit var tourAdapter: TourAdapter

    // Views
    private lateinit var rvMyTours: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvEmptySubtitle: TextView
    private lateinit var btnBrowse: Button
    private lateinit var layoutEmpty: View
    private lateinit var layoutError: View
    private lateinit var tvError: TextView
    private lateinit var btnRetry: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_tours, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        rvMyTours = view.findViewById(R.id.rvMyTours)
        progressBar = view.findViewById(R.id.progressBar)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        tvEmptySubtitle = view.findViewById(R.id.tvEmptySubtitle)
        btnBrowse = view.findViewById(R.id.btnBrowse)
        layoutError = view.findViewById(R.id.layoutError)
        tvError = view.findViewById(R.id.tvError)
        btnRetry = view.findViewById(R.id.btnRetry)

        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        tourAdapter = TourAdapter(
            onTourClick = { tour ->
                val bundle = bundleOf("tourId" to tour.id)
                findNavController().navigate(R.id.action_myTours_to_detail, bundle)
            },
            onFavoriteClick = { tour ->
                viewModel.removeFromFavorites(tour.id)
                Snackbar.make(
                    requireView(),
                    "${tour.name} הוסר מהמועדפים",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        )

        rvMyTours.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tourAdapter
        }

        // Swipe to remove
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val tour = tourAdapter.currentList[position]
                viewModel.removeFromFavorites(tour.id)

                Snackbar.make(
                    requireView(),
                    "${tour.name} הוסר מהמועדפים",
                    Snackbar.LENGTH_LONG
                ).setAction("ביטול") {
                    viewModel.removeFromFavorites(tour.id) // Toggle back
                }.show()
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvMyTours)
    }

    private fun setupListeners() {
        btnBrowse.setOnClickListener {
            // Navigate to home tab
            findNavController().navigate(R.id.homeFragment)
        }

        btnRetry.setOnClickListener {
            viewModel.loadFavoriteTours()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toursState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showTours(state.data)
                        is UiState.Error -> showError(state.message)
                        is UiState.Empty -> showEmpty()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        rvMyTours.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
    }

    private fun showTours(tours: List<Tour>) {
        progressBar.visibility = View.GONE
        rvMyTours.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.GONE
        tourAdapter.submitList(tours)
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        rvMyTours.visibility = View.GONE
        layoutEmpty.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        rvMyTours.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        tvError.text = message
    }
}
