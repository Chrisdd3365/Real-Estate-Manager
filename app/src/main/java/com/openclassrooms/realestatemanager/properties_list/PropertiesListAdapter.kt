package com.openclassrooms.realestatemanager.properties_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PropertiesListItemBinding
import com.openclassrooms.realestatemanager.model.Estate

class PropertiesListAdapter(val clicked : (Estate) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var context : Context? = null

    private var items = ArrayList<Estate>()

    fun setData(context: Context?, estates : ArrayList<Estate>) {
        this.context = context
        items.clear()
        items.addAll(estates)
        notifyDataSetChanged()
    }

    fun addItem(index: Int?, toAdd : Estate) {
        if (index != null) items.add(index, toAdd)
        else items.add(toAdd)
        notifyItemInserted(items.indexOf(toAdd))
    }

    fun changeItem(index: Int, toEdit : Estate) {
        items[index] = toEdit
        notifyItemChanged(index)
    }

    fun removeItem(toRemoveIndex : Int) {
        items.removeAt(toRemoveIndex)
        notifyItemRemoved(toRemoveIndex)
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
        (holder as PropertiesListItemViewHolder).setData(context, items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PropertiesListItemViewHolder(private val binding : PropertiesListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun setData(context: Context?, estate: Estate) {
            binding.viewModel = PropertiesListItemViewModel()
            binding.viewModel?.setData(context, estate)
            binding.itemRoot.setOnClickListener { clicked.invoke(estate) }
        }

    }

    inner class PropertiesListItemViewModel {

        val type = ObservableField("")
        val city = ObservableField("")
        val price = ObservableField("")

        fun setData(context: Context?, estate: Estate) {

            if (estate.typeIndex != null)
                type.set(context?.resources?.getStringArray(R.array.estate_types)
                    ?.get(estate.typeIndex!!))

            city.set(estate.address)
            price.set("${estate.price} $")
        }

    }

    companion object {
        @Suppress("unused")
        private const val TAG = "PropertiesListAdapter"
    }
}