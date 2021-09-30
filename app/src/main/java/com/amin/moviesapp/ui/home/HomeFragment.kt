package com.amin.moviesapp.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), SearchedMoviesRecyclerAdapter.Interaction {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    requestSearchData(newText)

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



    private fun requestSearchData(query: String) {
        try {
            if (requireActivity().isOnline()){
                initSearchRecyclerView()
                observeSearchResultLiveData(query)
            }else{
                handleErrorNetworkState()
            }

        }catch (e:Exception){
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

    fun handleErrorNetworkState(){
        requireActivity().toast(getString(R.string.network_error))
    }

    fun handleRequestError(){
        requireActivity().toast(getString(R.string.request_error))
    }

    override fun onItemSelected(position: Int, item: Result) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(item)
        findNavController().navigate(action)
    }


}