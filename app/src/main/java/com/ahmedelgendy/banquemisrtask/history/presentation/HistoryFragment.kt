package com.ahmedelgendy.banquemisrtask.history.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ahmedelgendy.banquemisrtask.databinding.HistoricalDataLayoutBinding
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.showLoading
import com.ahmedelgendy.banquemisrtask.general.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: HistoricalDataLayoutBinding? = null

    private val viewModel: HistoryViewModel by viewModels()

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HistoricalDataLayoutBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.currencyVariables = this
        binding.lifecycleOwner = viewLifecycleOwner

        loadData()


    }


    private fun loadData() {
        val today: Date = Calendar.getInstance().time
        Log.d("TAG", "loadData: $today ")

//        viewModel.getPastRates()
        startObserver()
    }

    private fun startObserver() {
        lifecycleScope.launch() {
            viewModel.pastRatesResponse.observe(viewLifecycleOwner) { it ->
                it.getContentIfNotHandled()?.let { event ->
                    showLoading(event is Resource.Loading)
                    when (event) {
                        is Resource.Success -> {
                            val status = event.value.success
                            val response = event.value.rates

                            if (status == true) {

                                if (response != null) {


                                } else {
                                    showLongToast("there is no countries", requireContext())
                                }


                            } else {
                                showLongToast("an error has occurred", requireContext())
                            }
                        }
                        is Resource.Failure -> {
                            showLongToast("${event.errorCode} ${event.cause}", requireContext())
                        }
                        Resource.Loading -> {
                        }
                    }

                } ?: run {

                }
            }
        }
    }


}
