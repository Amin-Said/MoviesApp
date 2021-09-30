package com.amin.moviesapp.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amin.moviesapp.R
import com.amin.moviesapp.databinding.FragmentDetailsBinding
import com.amin.moviesapp.utils.Config
import com.amin.moviesapp.utils.extensions.setImageWithPicasso
import com.amin.moviesapp.viewmodel.details.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    @VisibleForTesting
    val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val movie = DetailsFragmentArgs.fromBundle(it).movie
            viewModel.result.value = movie
            observeMovieData()
        }




    }
    private fun observeMovieData(){
        viewModel.result.observe(viewLifecycleOwner){movie->
            val posterUrl = Config.POSTER_PATH+movie.poster_path
            binding.posterDetailsIV.setImageWithPicasso(posterUrl)
            binding.titleTV.text = movie.title
            binding.popularityTV.text = "#${movie.popularity}"
            binding.voteAverageTV.text = getString(R.string.vote_average)+ movie.vote_average
            binding.releaseDateTV.text = getString(R.string.release_date) + movie.release_date
            binding.overviewTV.text = movie.overview

            binding.viewPosterAction.setOnClickListener {
                val action = DetailsFragmentDirections.actionDetailsFragmentToPosterFragment(posterUrl)
                findNavController().navigate(action)
            }
        }

    }
}