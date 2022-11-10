package com.ahmedelgendy.banquemisrtask.history.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.convert.presentation.ConvertViewModel
import com.ahmedelgendy.banquemisrtask.databinding.HistoricalDataLayoutBinding
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.showLoading
import com.ahmedelgendy.banquemisrtask.general.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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

//        binding.currencyVariables = this

        fromCurrency = args.fromCurrency
        toCurrency = args.toCurrency
        popularCurrencies = args.popularCurrencies



        loadData()


    }


    private fun loadData() {

        val startDate = getDate(-3)
        val endDate = getDate()

        if (startDate != null && endDate != null) {
            viewModel.getPastRates(
                fromCurrency = fromCurrency,
                toCurrencies = toCurrency,
                startDate = startDate,
                endDate = endDate
            )
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
                            val response = event.value.rates

                            if (status == true) {

                                if (response != null) {

                                    Log.d(getString(R.string.apiTag), response.toString())

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
