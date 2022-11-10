package com.ahmedelgendy.banquemisrtask.history.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.convert.presentation.ConvertViewModel
import com.ahmedelgendy.banquemisrtask.databinding.HistoricalDataLayoutBinding
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.showLoading
import com.ahmedelgendy.banquemisrtask.general.showLongToast
import com.ahmedelgendy.banquemisrtask.history.presentation.adapters.LastThreeDayRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: HistoricalDataLayoutBinding? = null

    private val viewModel: ConvertViewModel by activityViewModels()

    private val binding get() = _binding!!

    private val args: HistoryFragmentArgs by navArgs()

    private var fromCurrency: String? = null
    private var toCurrency: String? = null
    private var popularCurrencies: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HistoricalDataLayoutBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner





        loadData()


    }


    private fun loadData() {

        val startDate = getDate(-3)
        val endDate = getDate()

        if (startDate != null && endDate != null) {
            viewModel.apply {
                getPastRates(
                    fromCurrency = fromCurrency.value,
                    toCurrencies = toCurrency.value,
                    startDate = startDate,
                    endDate = endDate
                )
            }

            startObserver()
        }
    }

    private fun startObserver() {
        lifecycleScope.launch() {
            viewModel.pastRatesResponse.observe(viewLifecycleOwner) { it ->
                it.getContentIfNotHandled()?.let { event ->
                    showLoading(event is Resource.Loading)
                    when (event) {
                        is Resource.Success -> {
                            val status = event.value.success
                            val response = event.value.rates?.toSortedMap(compareByDescending { it })

                            if (status == true) {

                                if (response != null) {

                                    viewModel.apply {
                                        binding.historyRecycler.adapter = LastThreeDayRecyclerAdapter(
                                            response as TreeMap<String, HashMap<String, String>>,fromCurrency.value,toCurrency.value)
                                    }


                                } else {
                                    showLongToast("there is no countries", requireContext())
                                }


                            } else {
                                showLongToast("an error has occurred", requireContext())
                            }
                        }
                        is Resource.Failure -> {

                            Log.e(getString(R.string.apiTag), "${event.errorCode} ${event.cause}")
                        }
                        Resource.Loading -> {
                        }
                    }

                } ?: run {

                }
            }
        }
    }


    private fun getDate(days: Int = 0): String? {

        val today = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val newDate = calendar.time
//        val parser = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy")
//        val formatter = SimpleDateFormat("yyyy-mm-dd")


        try {
            return SimpleDateFormat("yyyy-MM-dd").format(newDate)
        } catch (e: ParseException) {
            Log.e("parsing", e.message.toString())
        }
        return null
    }

}
