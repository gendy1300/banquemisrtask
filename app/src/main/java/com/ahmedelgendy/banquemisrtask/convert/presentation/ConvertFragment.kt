package com.ahmedelgendy.banquemisrtask.convert.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.ahmedelgendy.banquemisrtask.convert.presentation.bottomsheets.CurrenciesBottomSheet
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

    var convertJob: Job = Job()

    var isConvertFragmentCreated = false

    var bottomSheet: CurrenciesBottomSheet? = null

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
        swapCurrencyImplementation()






        binding.detailsBtn.setOnClickListener {
            val navigationAction =
                ConvertFragmentDirections.convertFragmentToHistoryFragment(
                    viewModel.fromCurrency.value,
                    viewModel.toCurrency.value
                )
            viewModel.isHistoryFragmentLoaded = false
            it.findNavController().navigate(navigationAction)
        }

        binding.fromDropDown.setOnClickListener {
            showBottomSheet()
        }

        binding.toDropDown.setOnClickListener {
            showBottomSheet(false)
        }


    }


    private fun swapCurrencyImplementation() {
        binding.swapBtn.setOnClickListener {

            /**
             * when swapping currencies the input stays as it is and the output get the converted value
             */


            viewModel.apply {
                val tempCurrency = fromCurrency.value
                fromCurrency.value = toCurrency.value
                toCurrency.value = tempCurrency

                val tempCurrencyName = formCurrencyTitle.value
                formCurrencyTitle.value = toCurrencyTitle.value
                toCurrencyTitle.value = tempCurrencyName
                binding.scrollView.requestFocus()
                fromAmount.value?.let { it1 ->
                    convert(
                        fromCurrency.value, toCurrency.value,
                        it1, 1
                    )
                }

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
                            viewModel.fromCurrency.value,
                            viewModel.toCurrency.value,
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
                            viewModel.toCurrency.value,
                            viewModel.fromCurrency.value,
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
        isConvertFragmentCreated = false
        _binding = null

    }


    private fun loadData() {
        if (!viewModel.isConvertFragmentLoaded) {
            viewModel.getCurrencies()
            startObserver()
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

                                    viewModel.apply {
                                        currencies = response.toSortedMap(compareBy { it })
                                        fromCurrency.value = currencies.keys.toList()[0]
                                        toCurrency.value = currencies.keys.toList()[0]
                                        formCurrencyTitle.value =
                                            "${currencies[fromCurrency.value]} (${fromCurrency.value})"

                                        toCurrencyTitle.value =
                                            "${currencies[toCurrency.value]} (${toCurrency.value})"
                                    }


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
                                    showLongToast(event.cause, requireContext())
                                }
                                Resource.Loading -> {
                                }
                            }
                        }
                    }

                } else {
                    if (valueField == 1)
                        viewModel.toAmount.value = viewModel.fromAmount.value
                    else
                        viewModel.fromAmount.value = viewModel.toAmount.value
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

    private fun showBottomSheet(isInput: Boolean = true) {
        bottomSheet = CurrenciesBottomSheet(viewModel.currencies) { key, name ->
            if (isInput) {
                viewModel.fromCurrency.value = key
                viewModel.formCurrencyTitle.value = "$name ($key)"
            } else {
                viewModel.toCurrency.value = key
                viewModel.toCurrencyTitle.value = "$name ($key)"
            }


            viewModel.apply {
                fromAmount.value?.let { it1 ->
                    convert(
                        fromCurrency.value,
                        toCurrency.value,
                        it1,
                        1
                    )
                }
            }
        }
        bottomSheet?.show(childFragmentManager, "CurrenciesBottomSheet")

    }
}
