package com.ahmedelgendy.banquemisrtask.convert.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SpinnerAdapter
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.databinding.ConvertFragmentLayoutBinding
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.showLoading
import com.ahmedelgendy.banquemisrtask.general.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class ConvertFragment : Fragment() {

    private var _binding: ConvertFragmentLayoutBinding? = null

    private val viewModel: ConvertViewModel by viewModels()

    private val binding get() = _binding!!

    var fromCurrency: String? = null
    var fromAmount: MutableLiveData<String> = MutableLiveData("1.00")
    var fromCurrencyPosition: Int = 0

    var toCurrency: String? = null
    var toAmount: MutableLiveData<String> = MutableLiveData("1.0")
    var toCurrencyPosition: Int = 0


    private lateinit var currencies: SortedMap<String, String>

    var currencyArrayAdapter: MutableLiveData<SpinnerAdapter>? = null

    var convertJob: Job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ConvertFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currencyVariables = this
        binding.lifecycleOwner = viewLifecycleOwner

        loadData()
        textObservers()
        textFocusChange()
        spinnersItemsSelectedListener()
        swapCurrencyImplementation()


        binding.detailsBtn.setOnClickListener {
            it.findNavController().navigate(R.id.convertFragmentToHistoryFragment)
        }


    }

    private fun swapCurrencyImplementation() {
        binding.swapBtn.setOnClickListener {

            /**
             * when swapping currencies the input stays as it is and the output get the converted value
             */

            val tempCurrency = fromCurrency
            fromCurrency = toCurrency
            toCurrency = tempCurrency

            binding.fromDropDown.setSelection(toCurrencyPosition)
            toAmount.value = ""
            binding.toDropDown.setSelection(fromCurrencyPosition)

        }
    }


    private fun spinnersItemsSelectedListener() {
        /**
         * when the selected currency change the input stays as it is
         * and the output changes its value to the converted value
         */

        binding.toDropDown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                val spinner = view as AppCompatTextView
                toCurrency = spinner.text.toString().substringAfter("(")
                    .split(",")[0]

                toCurrencyPosition = position


                fromAmount.value?.let { convert(fromCurrency, toCurrency, it, 1) }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.fromDropDown.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                val spinner = view as AppCompatTextView
                fromCurrency = spinner.text.toString().substringAfter("(")
                    .split(",")[0]
                fromCurrencyPosition = position


                fromAmount.value?.let { convert(fromCurrency, toCurrency, it, 1) }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }


    private fun textFocusChange() {

        /**
         * if a field left empty we reset it to the default value
         */

        binding.fromTextField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val editText = v as EditText
                if (editText.length() <= 0) {
                    fromAmount.value = "1.00"
                }
            }
        }

        binding.toTextField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val editText = v as EditText
                if (editText.length() <= 0) {
                    toAmount.value = "1.00"
                }
            }
        }
    }


    private fun textObservers() {
        binding.fromTextField.doAfterTextChanged {
            it?.let {
                if (binding.fromTextField.hasFocus()) {
                    fromAmount.value?.let { it1 ->
                        startConvertJob(
                            fromCurrency,
                            toCurrency,
                            it1,
                            1
                        )
                    }
                }
            }
        }


        binding.toTextField.doAfterTextChanged {
            it?.let {
                if (binding.toTextField.hasFocus()) {
                    toAmount.value?.let { it1 -> startConvertJob(toCurrency, fromCurrency, it1, 2) }
                }
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun loadData() {
        viewModel.getCurrencies()
        startObserver()
    }

    private fun startObserver() {
        lifecycleScope.launch() {
            viewModel.currenciesResponse.observe(viewLifecycleOwner) { it ->
                it.getContentIfNotHandled()?.let { event ->
                    showLoading(event is Resource.Loading)
                    when (event) {
                        is Resource.Success -> {
                            val status = event.value.success
                            val response = event.value.symbols

                            if (status == true) {

                                if (response != null) {

                                    /**
                                     * sorting the map will give us a better user experience
                                     * and will ensure that the hashmap keys dose not change
                                     * positions when retrieving it on the on selected method
                                     */
                                    currencies = response.toSortedMap(compareBy { it })

                                    val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        currencies.toList()
                                    )
                                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                                    currencyArrayAdapter?.value = ad


                                    binding.fromDropDown.adapter = ad
                                    binding.toDropDown.adapter = ad
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
                    /**
                     * this is in case if the api is already loaded so we don't over spam
                     * the server when we don't have to
                     */
                    val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        currencies.toList()
                    )
                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    currencyArrayAdapter?.value = ad


                    binding.fromDropDown.adapter = currencyArrayAdapter?.value
                    binding.toDropDown.adapter = currencyArrayAdapter?.value
                }
            }
        }
    }

    /**
     * the valueField indicates which of the 2 text inputs will change its value
     * to the converted value
     * 1 indicates that the input text will stay as it is
     * 2 indicates that the output text will stay as it is
     */
    fun convert(from: String?, to: String?, amount: String, valueField: Int) {

        /**
         * I thought about making conversion local and fetching only the rate of 1
         * but the price of currency changes very fast
         */

        if (amount.isNotEmpty()) {
            if (from != null && to != null) {

                /**
                 * just making sure we don't call the api needlessly when we don't have to
                 * we can just set the values to 1.00
                 */

                if (from != to) {
                    viewModel.clearResponses()
                    viewModel.convert(from, to, amount)

                    lifecycleScope.launch() {

                        viewModel.convertResponse.observe(viewLifecycleOwner) { event ->
                            showLoading(event is Resource.Loading)
                            when (event) {
                                is Resource.Success -> {
                                    val status = event.value.success
                                    val response = event.value

                                    if (status == true) {
                                        if (valueField == 1) {
                                            toAmount.value = response.result.toString()
                                        } else {
                                            fromAmount.value = response.result.toString()
                                        }
                                    }
                                }
                                is Resource.Failure -> {
                                }
                                Resource.Loading -> {
                                }
                            }
                        }
                    }

                } else {
                    toAmount.value = fromAmount.value
                    convertJob.cancel()
                }
            }
        }
    }

    private fun startConvertJob(from: String?, to: String?, amount: String, valueField: Int) {

        if (amount.isNotEmpty()) {
            convertJob.cancel()

            convertJob = lifecycleScope.launch {
                /**
                 * making sure that we not load every change but when the user is finished
                 * typing there amount correctly
                 * the value field indicates if the changed number is from or to fields
                 */
                delay(1000)
                if (isActive)
                    convert(from, to, amount, valueField)
            }


        } else {
            convertJob.cancel()
        }
    }
}
