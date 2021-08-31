package com.openclassrooms.realestatemanager.show_estate

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.google.android.flexbox.FlexboxLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityShowEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums

class ShowEstateActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = ShowEstateActivityViewModel()

    // Layout variables
    private var typeIcon : ImageView? = null
    private var leftButton : Button? = null
    private var rightButton : Button? = null
    private var nearbyImagesFlexbox : FlexboxLayout? = null

    private var type : Enums.ShowEstateType? = null
    private var estate : Estate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = intent.extras?.get(TAG_TYPE) as Enums.ShowEstateType
        estate = intent.extras?.get(TAG_ESTATE) as Estate

        if (estate == null) finish()

        val binding = DataBindingUtil.setContentView<ActivityShowEstateBinding>(
            this, R.layout.activity_show_estate
        )

        binding.viewModel = viewModel

        typeIcon = binding.typeIcon
        leftButton = binding.leftButton
        rightButton = binding.rightButton
        nearbyImagesFlexbox = binding.nearbyImagesFlexbox

        viewModel.setButtonsText(this, type!!)
        viewModel.setData(this, estate!!)

        setImageType()

        setButtonsBehaviors()

        setNearbyData()
    }

    private fun setImageType() {
        when (estate!!.typeIndex) {
            0 -> { typeIcon?.setImageResource(R.drawable.ic_flat) }
            1 -> { typeIcon?.setImageResource(R.drawable.ic_townhouse) }
            2 -> { typeIcon?.setImageResource(R.drawable.ic_penthouse) }
            3 -> { typeIcon?.setImageResource(R.drawable.ic_house) }
            4 -> { typeIcon?.setImageResource(R.drawable.ic_duplex) }
        }
    }

    private fun setButtonsBehaviors() {
        leftButton?.setOnClickListener {
            when (type!!) {
                Enums.ShowEstateType.ASK_FOR_CONFIRMATION -> {
                    // TODO : Cancel
                }
                Enums.ShowEstateType.SHOW_ESTATE -> {
                    // TODO : Delete this estate
                }
            }
        }
        rightButton?.setOnClickListener {
            when (type!!) {
                Enums.ShowEstateType.ASK_FOR_CONFIRMATION -> {
                    // TODO : Save this estate
                }
                Enums.ShowEstateType.SHOW_ESTATE -> {
                    // TODO : Edit this estate
                }
            }
        }
    }

    private fun setNearbyData() {
        if (estate!!.school == null && estate!!.playground == null && estate!!.shop == null
            && estate!!.buses == null && estate!!.subway == null && estate!!.park == null) {
            // The user did not provide data, hide the "Nearby:" layout
            viewModel.hideNearbyLayout()
            return
        }
        if (!estate!!.school!! && !estate!!.playground!! && !estate!!.shop!! && !estate!!.buses!!
            && !estate!!.subway!! && !estate!!.park!!) {
            // If there are nothing nearby, hide the "Nearby:" layout
            viewModel.hideNearbyLayout()
            return
        }

        if (estate!!.school != null && estate!!.school == true) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.ic_school)
            imageView.id = 0
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
            nearbyImagesFlexbox?.addView(imageView)

            val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
            imageViewLayoutParams.width = 100
            imageViewLayoutParams.height = 100
            imageViewLayoutParams.marginEnd = 25
        }
        if (estate!!.playground != null && estate!!.playground == true) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.ic_playground)
            imageView.id = 0
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
            nearbyImagesFlexbox?.addView(imageView)

            val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
            imageViewLayoutParams.width = 100
            imageViewLayoutParams.height = 100
            imageViewLayoutParams.marginEnd = 25
        }
        if (estate!!.shop != null && estate!!.shop == true) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.ic_shop)
            imageView.id = 0
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
            nearbyImagesFlexbox?.addView(imageView)

            val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
            imageViewLayoutParams.width = 100
            imageViewLayoutParams.height = 100
            imageViewLayoutParams.marginEnd = 25
        }
        if (estate!!.buses != null && estate!!.buses == true) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.ic_bus_station)
            imageView.id = 0
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
            nearbyImagesFlexbox?.addView(imageView)

            val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
            imageViewLayoutParams.width = 100
            imageViewLayoutParams.height = 100
            imageViewLayoutParams.marginEnd = 25
        }
        if (estate!!.subway != null && estate!!.subway == true) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.ic_subway_station)
            imageView.id = 0
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
            nearbyImagesFlexbox?.addView(imageView)

            val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
            imageViewLayoutParams.width = 100
            imageViewLayoutParams.height = 100
            imageViewLayoutParams.marginEnd = 25
        }
        if (estate!!.park != null && estate!!.park == true) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.ic_park)
            imageView.id = 0
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
            nearbyImagesFlexbox?.addView(imageView)

            val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
            imageViewLayoutParams.width = 100
            imageViewLayoutParams.height = 100
        }
        viewModel.showNearbyLayout()
    }

    companion object {

        private const val TAG = "ShowEstateActivity"

        private const val TAG_ESTATE = "estate"
        private const val TAG_TYPE = "type"

        fun newInstance(context : Context?, estate : Estate, type : Enums.ShowEstateType) : Intent {
            val intent = Intent(context, ShowEstateActivity::class.java)
            intent.putExtra(TAG_TYPE, type)
            intent.putExtra(TAG_ESTATE, estate)
            return intent
        }

    }
}