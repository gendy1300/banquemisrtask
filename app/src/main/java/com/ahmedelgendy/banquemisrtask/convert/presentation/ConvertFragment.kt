package com.ahmedelgendy.banquemisrtask.convert.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ahmedelgendy.banquemisrtask.databinding.ConvertFragmentLayoutBinding
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.showLongToast
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


        viewModel.currenciesResponse.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    is Resource.Success -> {
                        val status = event.value.success
                        val response = event.value.symbols

                        if (status == true) {


                        }
                    }
                    is Resource.Failure -> {
                        showLongToast("${event.errorCode} ${event.cause}", requireContext())
                    }
                    Resource.Loading -> {
                        Log.d("TAG", "request loading ")
                    }
                }

            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}