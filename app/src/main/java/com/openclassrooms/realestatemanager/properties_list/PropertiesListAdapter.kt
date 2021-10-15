package com.openclassrooms.realestatemanager.properties_list

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PropertiesListItemBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Singleton

class PropertiesListAdapter(val clicked : (Estate) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var context : Context? = null

    private var items = ArrayList<Estate>()
    private var selectedItem : Int? = null

    fun setData(context: Context?, estates : ArrayList<Estate>) {
        this.context = context
        items.clear()
        items.addAll(estates)
        notifyDataSetChanged()
    }

    fun selectItem(index: Int) {
        selectedItem = index
        notifyItemRangeChanged(0, itemCount)
    }

    fun unselectItem() {
        selectedItem = null
        notifyItemRangeChanged(0, itemCount)
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
        (holder as PropertiesListItemViewHolder)
            .setData(context, items[position], (position == selectedItem))
    }

    override fun getItemCount(): Int = items.size

    inner class PropertiesListItemViewHolder(private val binding : PropertiesListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        private var bitmap : Bitmap? = null
        private val viewModel = PropertiesListItemViewModel()

        fun setData(context: Context?, estate: Estate, selected: Boolean) {

            if (context != null) {
                if (selected) {
                    binding.itemRoot.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.selectedCardView)
                    )
                } else {
                    binding.itemRoot.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.cardview_dark_background)
                    )
                }
            }

            binding.viewModel = viewModel

            viewModel.setLoading()

            binding.viewModel?.setData(context, estate)
            binding.itemRoot.setOnClickListener { clicked.invoke(estate) }
            setPlaceHolder(estate.typeIndex!!)

            if (bitmap == null && context != null && estate.id != null) {
                DatabaseManager(context).getImagesForEstate(
                    estate.id!!,
                    success = {
                        bitmap = it[0]
                        setImage(bitmap!!)
                    },
                    failure = {
                        setPlaceHolder(estate.typeIndex!!)
                    }
                )
            } else if (bitmap != null) {
                setImage(bitmap!!)
            } else {
                setPlaceHolder(estate.typeIndex!!)
            }
        }

        private fun setImage(bitmap : Bitmap) {
            binding.imageView.setImageBitmap(bitmap)
            binding.imageView.scaleType = ImageView.ScaleType.FIT_XY
            viewModel.setImage()
        }

        private fun setPlaceHolder(estateType : Int?) {
            when (estateType) {
                0 -> { binding.imageView.setImageResource(R.drawable.ic_flat) }
                1 -> { binding.imageView.setImageResource(R.drawable.ic_townhouse) }
                2 -> { binding.imageView.setImageResource(R.drawable.ic_penthouse) }
                3 -> { binding.imageView.setImageResource(R.drawable.ic_house) }
                4 -> { binding.imageView.setImageResource(R.drawable.ic_duplex) }
                null -> { binding.imageView.setImageResource(R.drawable.ic_flat) }
            }
            viewModel.setImage()
        }

    }

    inner class PropertiesListItemViewModel {

        val type = ObservableField("")
        val city = ObservableField("")
        val price = ObservableField("")

        val imageVisibility = ObservableInt(View.GONE)
        val loadingImageVisibility = ObservableInt(View.VISIBLE)

        fun setData(context: Context?, estate: Estate) {

            if (estate.typeIndex != null)
                type.set(context?.resources?.getStringArray(R.array.estate_types)
                    ?.get(estate.typeIndex!!))

            city.set(estate.address)
            price.set("${estate.getPrice()} ${Singleton.currencySymbol}")
        }

        fun setImage() {
            loadingImageVisibility.set(View.GONE)
            imageVisibility.set(View.VISIBLE)
        }

        fun setLoading() {
            imageVisibility.set(View.GONE)
            loadingImageVisibility.set(View.VISIBLE)
        }

    }

    companion object {
        @Suppress("unused")
        private const val TAG = "PropertiesListAdapter"
    }
}