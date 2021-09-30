package com.amin.moviesapp.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.viewModels
import com.amin.moviesapp.databinding.FragmentPosterBinding
import com.amin.moviesapp.utils.extensions.setImageWithPicasso
import com.amin.moviesapp.viewmodel.details.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PosterFragment : Fragment() {
    private var _binding: FragmentPosterBinding? = null
    private val binding get() = _binding!!

    @VisibleForTesting
    val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val posterUrl = PosterFragmentArgs.fromBundle(it).posterUrl
            viewModel.posterUrl.value = posterUrl
            observePosterUrlData()
        }


    }

    private fun observePosterUrlData() {
        viewModel.posterUrl.observe(viewLifecycleOwner) { url ->
            binding.posterFullIV.setImageWithPicasso(url)
        }
    }
}