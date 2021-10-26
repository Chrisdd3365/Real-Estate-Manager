package com.openclassrooms.realestatemanager.estate_creation.add_images

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PicturesListItemBinding
import com.openclassrooms.realestatemanager.utils.ItemTouchHelperAdapter
import com.openclassrooms.realestatemanager.utils.OnStartDragListener
import java.util.*
import kotlin.collections.ArrayList

/**
 * TODO : This should take Bitmaps items
 */
class PicturesListAdapter(private val dragStartListener: OnStartDragListener,
                          val removePicture : (Bitmap) -> Unit)
    : RecyclerView.Adapter<PicturesListAdapter.PictureViewHolder>(), ItemTouchHelperAdapter {

    private var items = ArrayList<Bitmap>()

    fun addItems(pictures : ArrayList<Bitmap>) {
        items.clear()
        items.addAll(pictures)
        notifyDataSetChanged()
    }

    fun getItems() : ArrayList<Bitmap> {
        return items
    }

    // TODO : Make sure the image is unique
    fun addNewItem(newPicture : Bitmap) {
        items.add(newPicture)
        notifyItemInserted(items.indexOf(newPicture))
    }

    fun removeItem(toRemove : Bitmap) {
        val index = items.indexOf(toRemove)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        return PictureViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.pictures_list_item,
                null,
                false
            ),
            dragStartListener
        )
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.setData(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        removePicture.invoke(items[position])
    }

    inner class PictureViewHolder(private val binding : PicturesListItemBinding,
                                  private val dragStartListener: OnStartDragListener? = null)
        : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun setData(picture : Bitmap) {

            binding.reorderButton.setColorFilter(Color.WHITE)

            binding.reorderButton.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    dragStartListener?.onStartDrag(this)
                }
                false
            }

            binding.deleteButton.setOnClickListener {
                removePicture.invoke(picture)
            }

            binding.pictureImageView.setImageBitmap(picture)
            binding.pictureImageView.scaleType = ImageView.ScaleType.FIT_CENTER

        }
    }

    inner class PictureViewModel

    companion object {

        @Suppress("unused")
        private const val TAG = "PicturesListAdapter"

    }
}