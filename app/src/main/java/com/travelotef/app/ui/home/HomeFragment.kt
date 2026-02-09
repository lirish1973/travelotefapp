package com.travelotef.app.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.travelotef.app.R
import com.travelotef.app.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Home Fragment - displays tours from TryIt.co.il with search, categories, and pull-to-refresh
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var tourAdapter: TourAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    // Views
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var etSearch: EditText
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvTours: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var tvError: TextView
    private lateinit var btnRetry: MaterialButton
    private lateinit var emptyLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        etSearch = view.findViewById(R.id.etSearch)
        rvCategories = view.findViewById(R.id.rvCategories)
        rvTours = view.findViewById(R.id.rvTours)
        progressBar = view.findViewById(R.id.progressBar)
        errorLayout = view.findViewById(R.id.errorLayout)
        tvError = view.findViewById(R.id.tvError)
        btnRetry = view.findViewById(R.id.btnRetry)
        emptyLayout = view.findViewById(R.id.emptyLayout)

        setupAdapters()
        setupSearch()
        setupSwipeRefresh()
        setupRetryButton()
        observeViewModel()
    }

    private fun setupAdapters() {
        // Tour adapter
        tourAdapter = TourAdapter(
            onTourClick = { tour ->
                // Navigate to tour detail
                val bundle = Bundle().apply {
                    putInt("tourId", tour.id)
                }
                findNavController().navigate(R.id.action_home_to_detail, bundle)
            },
            onFavoriteClick = { tour ->
                viewModel.toggleFavorite(tour)
            }
        )

        rvTours.apply {
            adapter = tourAdapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(false)
        }

        // Category adapter
        categoryAdapter = CategoryAdapter(
            onCategoryClick = { categoryId ->
                viewModel.selectCategory(categoryId)
            }
        )

        rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(false)
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchTours(s?.toString() ?: "")
            }
        })
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupRetryButton() {
        btnRetry.setOnClickListener {
            viewModel.loadTours()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Tours
                launch {
                    viewModel.toursState.collectLatest { state ->
                        when (state) {
                            is UiState.Loading -> showLoading()
                            is UiState.Success -> showTours(state.data)
                            is UiState.Error -> showError(state.message)
                            is UiState.Empty -> showEmpty()
                        }
                    }
                }

                // Categories
                launch {
                    viewModel.categoriesState.collectLatest { state ->
                        if (state is UiState.Success) {
                            categoryAdapter.submitList(state.data)
                        }
                    }
                }

                // Selected category
                launch {
                    viewModel.selectedCategory.collectLatest { categoryId ->
                        categoryAdapter.setSelectedCategory(categoryId)
                    }
                }

                // Refreshing
                launch {
                    viewModel.isRefreshing.collectLatest { isRefreshing ->
                        swipeRefresh.isRefreshing = isRefreshing
                    }
                }
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        rvTours.visibility = View.GONE
        errorLayout.visibility = View.GONE
        emptyLayout.visibility = View.GONE
    }

    private fun showTours(tours: List<com.travelotef.app.domain.model.Tour>) {
        progressBar.visibility = View.GONE
        rvTours.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        emptyLayout.visibility = View.GONE
        tourAdapter.submitList(tours)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        rvTours.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        emptyLayout.visibility = View.GONE
        tvError.text = message
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        rvTours.visibility = View.GONE
        errorLayout.visibility = View.GONE
        emptyLayout.visibility = View.VISIBLE
    }
}
