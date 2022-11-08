package com.ahmedelgendy.banquemisrtask.convert.presentation

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

    lateinit var fromCurrency: String

    lateinit var toCurrency: String

    private lateinit var currencies: SortedMap<String, String>

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

        loadData()
        textObservers()
        textFocusChange()
        spinnersItemsSelectedListener()


    }


    private fun spinnersItemsSelectedListener() {
        binding.toDropDown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                val spinner = view as AppCompatTextView
                toCurrency = spinner.text.toString().substringAfter("(")
                    .split(",")[0]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.fromDropDown.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                val spinner = view as AppCompatTextView
                fromCurrency = spinner.text.toString().substringAfter("(")
                    .split(",")[0]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }


    private fun textFocusChange() {
        binding.fromTextField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val editText = v as EditText
                if (editText.length() <= 0) {
                    v.setText("1.00")
                }
            }
        }

        binding.toTextField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val editText = v as EditText
                if (editText.length() <= 0) {
                    v.setText("1.00")
                }
            }
        }
    }


    private fun textObservers() {
        binding.fromTextField.doAfterTextChanged {
            it?.let {
                if (it.isNotEmpty()) {


                    convertJob.cancel()

                    convertJob = lifecycleScope.launch {
                        /**
                         * making sure that we not load every change but when the user is finished
                         * typing there amount correctly
                         * the value field indicates if the changed number is from or to fields
                         */
                        delay(1000)
                        if (isActive)
                            convert(it.toString(), 1)
                    }


                } else {
                    convertJob.cancel()
                }
            }
        }


        binding.toTextField.doAfterTextChanged {
            it?.let {
                if (it.isNotEmpty()) {
                    convertJob.cancel()
                    convertJob = lifecycleScope.launch {
                        /**
                         * making sure that we not load every change but when the user is finished
                         * typing there amount correctly
                         * the value field indicates if the changed number is from or to fields
                         */
                        delay(1000)
                        if (isActive)
                            convert(it.toString(), 2)
                    }


                } else {
                    convertJob.cancel()
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
                                        R.layout.simple_spinner_item,
                                        currencies.toList()
                                    )
                                    ad.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

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
                            Log.d("TAG", "request loading")
                        }
                    }

                } ?: run {
                    /**
                     * this is in case if the api is already loaded so we don't over spam
                     * the server when we don't have to
                     */
                    val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
                        requireContext(),
                        R.layout.simple_spinner_item,
                        currencies.toList()
                    )
                    ad.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

                    binding.fromDropDown.adapter = ad
                    binding.toDropDown.adapter = ad
                }
            }
        }
    }

    fun convert(amount: String, valueField: Int) {
        viewModel.convert(fromCurrency, toCurrency, amount)

        lifecycleScope.launch() {
            viewModel.convertResponse.observe(viewLifecycleOwner) { it ->
                it.getContentIfNotHandled()?.let { event ->
                    showLoading(event is Resource.Loading)
                    when (event) {
                        is Resource.Success -> {
                            val status = event.value.success
                            val response = event.value

                            if (status == true) {
                                if (valueField == 1) {
                                    binding.toTextField.setText(response.result.toString())
                                } else {
                                    binding.fromTextField.setText(response.result.toString())
                                }

                                convertJob.cancel()
                            }
                        }
                        is Resource.Failure -> {
                        }
                        Resource.Loading -> {
                        }
                    }
                }
            }
        }
    }
}
