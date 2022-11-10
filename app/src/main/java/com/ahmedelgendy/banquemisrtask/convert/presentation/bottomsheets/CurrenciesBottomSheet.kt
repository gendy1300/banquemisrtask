package com.ahmedelgendy.banquemisrtask.convert.presentation.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.convert.presentation.adapters.CurrenciesRecycler
import com.ahmedelgendy.banquemisrtask.databinding.CurrenciesBottomsheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class CurrenciesBottomSheet(
    private val currencies: SortedMap<String, String>,
    val onCurrencySelected: (String, String) -> Unit
) :
    BottomSheetDialogFragment() {

    lateinit var binding: CurrenciesBottomsheetLayoutBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.currencies_bottomsheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CurrenciesBottomsheetLayoutBinding.bind(view)


        binding.currenciesRecycler.adapter =
            CurrenciesRecycler(currencies, currencies.keys.toList()) { key, name ->
                onCurrencySelected.invoke(key,name)
                dismiss()
            }


    }


}