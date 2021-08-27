package com.openclassrooms.realestatemanager.properties_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PropertiesListItemBinding
import com.openclassrooms.realestatemanager.model.Estate

class PropertiesListAdapter(val clicked : (Estate) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = ArrayList<Estate>()

    fun setData(estates : ArrayList<Estate>) {
        items.clear()
        items.addAll(estates)
        notifyDataSetChanged()
    }

    fun addItem(toAdd : Estate) {
        items.add(toAdd)
        notifyItemInserted(items.indexOf(toAdd))
    }

    fun removeItem(toRemove : Estate) {
        val position = items.indexOf(toRemove)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PropertiesListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.properties_list_item,
                null,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PropertiesListItemViewHolder).setData(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PropertiesListItemViewHolder(private val binding : PropertiesListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun setData(estate: Estate) {
            binding.viewModel = PropertiesListItemViewModel()
            binding.viewModel?.setData(estate)
            binding.itemRoot.setOnClickListener { clicked.invoke(estate) }
        }

    }

    inner class PropertiesListItemViewModel {

        val type = ObservableField("")
        val city = ObservableField("")
        val price = ObservableField("")

        fun setData(estate: Estate) {
            type.set(estate.type)
            city.set(estate.address)
            price.set("${estate.price} $")
        }

    }
}