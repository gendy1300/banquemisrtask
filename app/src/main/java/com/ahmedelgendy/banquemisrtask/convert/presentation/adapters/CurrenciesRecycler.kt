package com.ahmedelgendy.banquemisrtask.convert.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmedelgendy.banquemisrtask.databinding.CurrenciesRecyclerItemLayoutBinding
import java.util.*


class CurrenciesRecycler(
    var items: SortedMap<String, String> = TreeMap(),
    private val keys: List<String>,
    val onItemClicked: (String,String) -> Unit
) :
    RecyclerView.Adapter<CurrenciesRecycler.ViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CurrenciesRecyclerItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val key = keys[position]
        val item = items[key]
        val context = binding.root.context

        binding.textview.text = "$item ($key)"

        binding.root.setOnClickListener {
            if (item != null) {
                onItemClicked.invoke(key,item)
            }
        }

    }

    override fun getItemCount(): Int {
        return keys.size
    }

    inner class ViewHolder(b: CurrenciesRecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(b.root) {
        var binding: CurrenciesRecyclerItemLayoutBinding

        init {
            binding = b
        }
    }
}