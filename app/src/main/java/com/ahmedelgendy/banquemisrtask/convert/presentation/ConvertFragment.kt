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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.ahmedelgendy.banquemisrtask.databinding.ConvertFragmentLayoutBinding
import com.ahmedelgendy.banquemisrtask.general.network.Resource
import com.ahmedelgendy.banquemisrtask.general.showLoading
import com.ahmedelgendy.banquemisrtask.general.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ConvertFragment : Fragment() {

    private var _binding: ConvertFragmentLayoutBinding? = null

    /**
     * using a shared view model to handel configuration changes and to
     * save the state when popping the backstack
     */

    private val viewModel: ConvertViewModel by activityViewModels()

    private val binding get() = _binding!!


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

        binding.currencyVariables = viewModel
        binding.lifecycleOwner = viewLifecycleOwner



        loadData()
        textObservers()
        textFocusChange()
        spinnersItemsSelectedListener()
        swapCurrencyImplementation()


        binding.detailsBtn?.setOnClickListener {
            val navigationAction =
                ConvertFragmentDirections.convertFragmentToHistoryFragment(
                    viewModel.fromCurrency,
                    viewModel.toCurrency
                )
            it.findNavController().navigate(navigationAction)
        }


    }

    private fun swapCurrencyImplementation() {
        binding.swapBtn.setOnClickListener {

            /**
             * when swapping currencies the input stays as it is and the output get the converted value
             */

            val tempCurrency = viewModel.fromCurrency
            viewModel.fromCurrency = viewModel.toCurrency
            viewModel.toCurrency = tempCurrency

            binding.fromDropDown.setSelection(viewModel.toCurrencyPosition)
            viewModel.toAmount.value = ""
            binding.toDropDown.setSelection(viewModel.fromCurrencyPosition)

        }
    }


    private fun spinnersItemsSelectedListener() {
        /**
         * when the selected currency change the input stays as it is
         * and the output changes its value to the converted value
         */

        binding.toDropDown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {

                viewModel.toCurrency =
                    binding.toDropDown.selectedItem.toString().substringAfter("(")
                        .split(",")[0]

                viewModel.toCurrencyPosition = position


                viewModel.fromAmount.value?.let {
                    convert(
                        viewModel.fromCurrency,
                        viewModel.toCurrency,
                        it,
                        1
                    )
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.fromDropDown.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
//                val spinner = view as AppCompatTextView
                viewModel.fromCurrency =
                    binding.fromDropDown.selectedItem.toString().substringAfter("(")
                        .split(",")[0]
                viewModel.fromCurrencyPosition = position


                viewModel.fromAmount.value?.let {
                    convert(
                        viewModel.fromCurrency,
                        viewModel.toCurrency,
                        it,
                        1
                    )
                }

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
                    viewModel.fromAmount.value = "1.00"
                }
            }
        }

        binding.toTextField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val editText = v as EditText
                if (editText.length() <= 0) {
                    viewModel.toAmount.value = "1.00"
                }
            }
        }
    }


    private fun textObservers() {
        binding.fromTextField.doAfterTextChanged {
            it?.let {
                if (binding.fromTextField.hasFocus()) {
                    viewModel.fromAmount.value?.let { it1 ->
                        startConvertJob(
                            viewModel.fromCurrency,
                            viewModel.toCurrency,
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
                    viewModel.toAmount.value?.let { it1 ->
                        startConvertJob(
                            viewModel.toCurrency,
                            viewModel.fromCurrency,
                            it1,
                            2
                        )
                    }
                }
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun loadData() {
        if (!viewModel.isConvertFragmentLoaded) {
            viewModel.getCurrencies()
            startObserver()
        } else {
            val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.currencies.toList()
            )
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            currencyArrayAdapter?.value = ad


            binding.fromDropDown.adapter = ad
            binding.toDropDown.adapter = ad
        }
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
                                    viewModel.isConvertFragmentLoaded = true

                                    /**
                                     * sorting the map will give us a better user experience
                                     * and will ensure that the hashmap keys dose not change
                                     * positions when retrieving it on the on selected method
                                     */

                                    viewModel.currencies = response.toSortedMap(compareBy { it })

                                    val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        viewModel.currencies.toList()
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
                        viewModel.currencies.toList()
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
                                            viewModel.toAmount.value = response.result.toString()
                                        } else {
                                            viewModel.fromAmount.value = response.result.toString()
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
                    viewModel.toAmount.value = viewModel.fromAmount.value
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
