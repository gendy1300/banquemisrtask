package com.ahmedelgendy.banquemisrtask.convert.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ahmedelgendy.banquemisrtask.databinding.ConvertFragmentLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConvertFragment : Fragment() {

    private var _binding: ConvertFragmentLayoutBinding? = null

    private val viewModel: ConvertViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ConvertFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getCurrencies()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}