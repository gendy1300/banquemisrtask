package com.ahmedelgendy.banquemisrtask.history.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.databinding.HistoryItemLayoutBinding
import java.util.*


class LastThreeDayRecyclerAdapter(
    private val items: TreeMap<String, HashMap<String, String>>,
    val from: String?,
    val to: String?,
) :
    RecyclerView.Adapter<LastThreeDayRecyclerAdapter.ViewHolder?>() {


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
        val key = items.keys.toList()[position]
        val item = items[key]
        val context = binding.root.context

        binding.fromCurrency.text = from
        binding.toCurrency.text = to


        binding.date.text = if (position == 0)
            context.getString(R.string.today)
        else
            key

        binding.fromAmount.text = "1"
        binding.toAmount.text = item?.get(to)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(b: HistoryItemLayoutBinding) :
        RecyclerView.ViewHolder(b.root) {
        var binding: HistoryItemLayoutBinding

        init {
            binding = b
        }
    }
}