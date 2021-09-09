package com.openclassrooms.realestatemanager.estate_creation.add_images

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
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
import com.openclassrooms.realestatemanager.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class PicturesListAdapter(private val dragStartListener: OnStartDragListener, var context: Context)
    : RecyclerView.Adapter<PicturesListAdapter.PictureViewHolder>(), ItemTouchHelperAdapter {

    private var items = ArrayList<String>()

    fun addItems(pictures : ArrayList<String>) {
        items.clear()
        items.addAll(pictures)
        notifyDataSetChanged()
    }

    // TODO : Make sure the image is unique
    fun addNewItem(newPicture : String) {
        items.add(newPicture)
        notifyItemInserted(items.indexOf(newPicture))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        return PictureViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.pictures_list_item,
                null,
                false
            ),
            dragStartListener,
            context
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
        TODO("Not yet implemented")
    }

    inner class PictureViewHolder(private val binding : PicturesListItemBinding,
                                  private val dragStartListener: OnStartDragListener? = null,
                                  private val context : Context
    )
        : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun setData(pictureUri: String) {

            binding.reorderButton.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    dragStartListener?.onStartDrag(this)
                }
                false
            }

            val bitmap = Utils.decodeUri(context, Uri.parse(pictureUri), 125)
            binding.pictureImageView.setImageBitmap(bitmap)
            binding.pictureImageView.scaleType = ImageView.ScaleType.FIT_CENTER

        }
    }

    inner class PictureViewModel

    companion object {

        @Suppress("unused")
        private const val TAG = "PicturesListAdapter"

    }
}