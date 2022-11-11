package com.ahmedelgendy.banquemisrtask.history.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.databinding.HistoryItemLayoutBinding


class OtherCurrenciesRecyclerAdapter(
    private val items: HashMap<String, String>,
    val from: String?,
    val to: String?,
    private val keys: List<String>?
) :
    RecyclerView.Adapter<OtherCurrenciesRecyclerAdapter.ViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HistoryItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val key = keys?.get(position)
        val item = items[key]
        val context = binding.root.context


        binding.fromCurrency.text = from
        binding.toCurrency.text = key


        binding.date.text = context.getString(R.string.today)

        binding.fromAmount.text = "1"
        binding.toAmount.text = item

    }

    override fun getItemCount(): Int {
        return keys?.size ?: 0
    }

    inner class ViewHolder(b: HistoryItemLayoutBinding) :
        RecyclerView.ViewHolder(b.root) {
        var binding: HistoryItemLayoutBinding

        init {
            binding = b
        }
    }
}