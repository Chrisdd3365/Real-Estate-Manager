package com.openclassrooms.realestatemanager.show_estate

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PictureSliderItemBinding

class PicturesSliderViewPagerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = ArrayList<Bitmap>()

    fun setItems(pictures : ArrayList<Bitmap>) {
        Log.d(TAG, "Setting ${pictures.size} items in the viewPager adapter")
        items.clear()
        items.addAll(pictures)
        notifyItemRangeChanged(0, itemCount)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = DataBindingUtil.inflate<PictureSliderItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.picture_slider_item,
            null,
            false
        )
        view.root.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        return PictureSliderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PictureSliderItemViewHolder)?.setData(items[position])
    }

    inner class PictureSliderItemViewHolder(private val binding : PictureSliderItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun setData(item : Bitmap) {
            binding.pictureImageView.setImageBitmap(item)
            binding.pictureImageView.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    inner class PictureSliderItemViewModel

    companion object {

        @Suppress("unused")
        private const val TAG = "PicturesSliderVPAdapter"
    }
}