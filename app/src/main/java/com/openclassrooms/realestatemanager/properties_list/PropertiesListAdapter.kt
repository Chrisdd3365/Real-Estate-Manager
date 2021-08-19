package com.openclassrooms.realestatemanager.properties_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PropertiesListItemBinding
import com.openclassrooms.realestatemanager.model.Estate

class PropertiesListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    inner class PropertiesListItemViewHolder(val binding : PropertiesListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(estate: Estate) {
            binding.viewModel?.setData(estate)
        }

    }

    inner class PropertiesListItemViewModel : ViewModel() {

        fun setData(estate: Estate) {
        }

    }
}