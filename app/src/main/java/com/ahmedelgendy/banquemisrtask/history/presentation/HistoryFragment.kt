package com.ahmedelgendy.banquemisrtask.history.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.convert.presentation.ConvertViewModel
import com.ahmedelgendy.banquemisrtask.databinding.HistoricalDataLayoutBinding
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.showLoading
import com.ahmedelgendy.banquemisrtask.general.showLongToast
import com.ahmedelgendy.banquemisrtask.history.presentation.adapters.LastThreeDayRecyclerAdapter
import com.ahmedelgendy.banquemisrtask.history.presentation.adapters.OtherCurrenciesRecyclerAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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


    lateinit var chart: LineChart

    // on below line we are creating
    // a variable for data set
    private lateinit var lineDataset2: LineDataSet

    // on below line we are creating array list for bar data
    private lateinit var entriesList: ArrayList<Entry>


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
        chart = binding.idBarChart

        loadData()


    }


    private fun loadData() {

        if (!viewModel.isHistoryFragmentLoaded) {
            val startDate = getDate(-3)
            val endDate = getDate()

            if (startDate != null && endDate != null) {
                viewModel.apply {
                    getPastRates(
                        fromCurrency = fromCurrency.value,
                        toCurrencies = this@HistoryFragment.getCurrencies(),
                        startDate = startDate,
                        endDate = endDate
                    )
                }


                startObserver()
            }
        }else {
                updateUi(viewModel.historicalData)
            }

    }


    private fun startObserver() {
        lifecycleScope.launch {
            viewModel.pastRatesResponse.observe(viewLifecycleOwner) { it ->
                it.getContentIfNotHandled()?.let { event ->
                    showLoading(event is Resource.Loading)
                    when (event) {
                        is Resource.Success -> {
                            val status = event.value.success
                            val response =
                                event.value.rates?.toSortedMap(compareByDescending { it })

                            if (status == true) {

                                if (response != null) {
                                    updateUi(response)
                                    viewModel.historicalData = response
                                    viewModel.isHistoryFragmentLoaded = true

                                } else {
                                    showLongToast("there is no countries", requireContext())
                                }


                            } else {
                                showLongToast("an error has occurred", requireContext())
                            }
                        }
                        is Resource.Failure -> {

                            showLongToast(event.cause, requireContext())
                        }
                        Resource.Loading -> {
                        }
                    }

                }
            }
        }
    }


    /**
     * a function that takes number of days and adds it to the current date
     * you can call it without a parameter it will get you todays date
     */
    private fun getDate(days: Int = 0): String? {

        val today = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val newDate = calendar.time

        try {
            return SimpleDateFormat("yyyy-MM-dd").format(newDate)
        } catch (e: ParseException) {
            Log.e("parsing", e.message.toString())
        }
        return null
    }


    /**
     * had to save the top currencies locally there was no api that will get the top 10 currencies
     * it will also reduce the number of times that we need to call an api
     * the app adds one more currency so if he choose one that already in the list
     * the app will still show 10 currencies
     */

    private fun getCurrencies(): String? {
        var getCurrencies = viewModel.toCurrency.value

        getCurrencies += ",USD"
        getCurrencies += ",EUR"
        getCurrencies += ",JPY"
        getCurrencies += ",GBP"
        getCurrencies += ",CNY"
        getCurrencies += ",AUD"
        getCurrencies += ",CAD"
        getCurrencies += ",CHF"
        getCurrencies += ",HKD"
        getCurrencies += ",SGD"
        getCurrencies += ",SEK"



        return getCurrencies
    }


    private fun setupBarChart(
        dates: List<String>,
        pricesForSetTwo: List<String>
    ) {


        lineDataset2 = LineDataSet(
            getChartData(pricesForSetTwo),
            "1 ${viewModel.toCurrency.value} value compered to 1 ${viewModel.fromCurrency.value}"
        )




        lineDataset2.color = ContextCompat.getColor(requireContext(), R.color.purple_700)
        lineDataset2.circleRadius = 10f
        lineDataset2.valueTextSize = 20F
        lineDataset2.mode = LineDataSet.Mode.LINEAR

        //We connect our data to the UI Screen
        var data = LineData(lineDataset2)
        binding.idBarChart.data = data
        binding.idBarChart.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.idBarChart.animateXY(1000, 1000, Easing.EaseInCubic)


        // on below line we are setting data to our chart
        chart.data = data

        // on below line we are setting description enabled.
        chart.description.isEnabled = false

        // on below line setting x axis
        val xAxis = chart.xAxis

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.
        xAxis.valueFormatter = IndexAxisValueFormatter(dates)

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true)

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // below line is to set granularity
        // to our x axis labels.
        xAxis.granularity = 1f

        // below line is to enable
        // granularity to our x axis.
        xAxis.isGranularityEnabled = true

        // below line is to make our
        // bar chart as draggable.
        chart.isDragEnabled = true

        // below line is to make visible
        // range for our bar chart.
        chart.setVisibleXRangeMaximum(3f)


        // below line is to set minimum
        // axis to our chart.
        chart.xAxis.axisMinimum = 0f

        // below line is to
        // animate our chart.
        chart.animate()


        // below line is to invalidate
        // our bar chart.
        chart.invalidate()
    }


    private fun getChartData(prices: List<String>): ArrayList<Entry> {
        entriesList = ArrayList<Entry>()
        // on below line we are adding
        // data to our bar entries list

        prices.forEachIndexed { index, s ->
            entriesList.add(Entry(index.toFloat(), s.toFloat()))
        }

        return entriesList
    }


    fun updateUi(response: SortedMap<String, HashMap<String, String>>) {


        val chartPriceArray = ArrayList<String>()
        for (i in response.keys.toList()) {
            response[i]?.get(viewModel.toCurrency.value)
                ?.let { it1 -> chartPriceArray.add(it1) }

        }

        setupBarChart(response.keys.toList(), chartPriceArray)


        viewModel.apply {
            binding.historyRecycler.adapter =
                LastThreeDayRecyclerAdapter(
                    response as TreeMap<String, HashMap<String, String>>,
                    fromCurrency.value,
                    toCurrency.value
                )
        }


        /**
         * just to get the currencies in the right order because hashmaps
         * order is not constant
         */
        val currenciesArray: ArrayList<String> =
            this@HistoryFragment.getCurrencies()
                ?.split(",") as ArrayList<String>

        /**
         * removing the first currency as it is our selected output currency
         */
        currenciesArray.removeAt(0)




        viewModel.apply {

            /**
             * removing the current from currency if it exists in the array
             */
            currenciesArray.remove(fromCurrency.value)

            binding.otherCurrenciesRecycler.adapter =
                response[getDate()]?.let { it1 ->
                    OtherCurrenciesRecyclerAdapter(
                        it1,
                        fromCurrency.value,
                        toCurrency.value,
                        currenciesArray.subList(
                            0,
                            10
                        ) //only to show 10 items if no item is removed
                    )
                }


        }


    }


}
