package com.amin.moviesapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amin.moviesapp.R
import com.amin.moviesapp.adapter.SearchedMoviesRecyclerAdapter
import com.amin.moviesapp.databinding.FragmentHomeBinding
import com.amin.moviesapp.model.Result
import com.amin.moviesapp.utils.Config
import com.amin.moviesapp.utils.data.ApiResult
import com.amin.moviesapp.utils.extensions.isOnline
import com.amin.moviesapp.utils.extensions.toast
import com.amin.moviesapp.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class HomeFragment : Fragment(), SearchedMoviesRecyclerAdapter.Interaction, CoroutineScope {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    @ExperimentalCoroutinesApi
    val query = MutableStateFlow("")
    private var lastQuery = ""

    private var mSearchAdapter: SearchedMoviesRecyclerAdapter? = null

    @VisibleForTesting
    val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        job = Job()

        handleSearchQuery()
    }

    private fun initSearchRecyclerView() {

        binding.moviesRV.apply {

            layoutManager = LinearLayoutManager(requireActivity())
            mSearchAdapter = SearchedMoviesRecyclerAdapter(this@HomeFragment)
            adapter = mSearchAdapter
        }
    }

    private fun cleanSearchRecyclerView() {
        binding.moviesRV.apply {
            layoutManager = null
            mSearchAdapter = null
            adapter = null
        }
    }

    @ExperimentalCoroutinesApi
    private fun handleSearchQuery() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // this make the default cursor which appears on focus on search EditText appears
                // only after writing words and not if there is no thing written

                if (newText.trim().isNotEmpty()) {
                    binding.welcomeMsg.visibility = View.GONE
                    binding.avi.smoothToShow()
                    binding.moviesRV.visibility = View.VISIBLE
                    query.value = newText
                    launch {
                        handleQueryChanges()
                    }

                } else {
                    binding.welcomeMsg.visibility = View.VISIBLE
                    binding.moviesRV.visibility = View.GONE
                    binding.avi.hide()
                    cleanSearchRecyclerView()


                }

                return false
            }
        })
    }

    suspend fun handleQueryChanges() {
        query.debounce(500)
            .filter { query ->
                return@filter query.isNotEmpty()
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .collect { saveQuery ->
                if (lastQuery != saveQuery) {
                    lastQuery = saveQuery
                    requestSearchData(saveQuery)
                    resetLastQueryAfterTime()
                }
            }
    }

    private suspend fun resetLastQueryAfterTime() {
        delay(500)
        lastQuery = ""
    }


    private fun requestSearchData(query: String) {
        try {
            if (requireActivity().isOnline()) {
                initSearchRecyclerView()
                observeSearchResultLiveData(query)
            } else {
                handleErrorNetworkState()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun observeSearchResultLiveData(query: String) {
        viewModel.fetchMovieDataForSearch(
            Config.PRIVATE_KEY_VALUE,
            "en-US",
            query
        )

        viewModel.searchResult.observe(viewLifecycleOwner) {
            when (it) {
                ApiResult.Loading -> {

                }
                is ApiResult.Error -> {
                    handleRequestError()
                }
                is ApiResult.Success -> {
                    binding.avi.hide()
                    mSearchAdapter?.submitList(it.data.results)


                }
            }

        }

        viewModel.isNetworkError.observe(viewLifecycleOwner) {
            if (it) {
                handleErrorNetworkState()
            }
        }
    }

    private fun handleErrorNetworkState() {
        requireActivity().toast(getString(R.string.network_error))
    }

    private fun handleRequestError() {
        requireActivity().toast(getString(R.string.request_error))
    }

    override fun onItemSelected(position: Int, item: Result) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(item)
        findNavController().navigate(action)
    }


}