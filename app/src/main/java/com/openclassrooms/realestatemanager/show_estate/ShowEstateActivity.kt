package com.openclassrooms.realestatemanager.show_estate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.flexbox.FlexboxLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityShowEstateBinding
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums

/**
 *  TODO : This [Activity] should only displays a [androidx.fragment.app.Fragment] with all this
 *      content, as we need to be able to re-use this fragment for landscape display, on the right
 *      of the element selected in the [PropertiesListFragment].
 */
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

        setTypeIcon()

        setButtonsBehaviors()

        setNearbyData()
    }

    private fun setTypeIcon() {
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
                    // The user wants to edit the data he provided, so we send him back to previous
                    //  Activity.
                    setResult(
                        Activity.RESULT_CANCELED,
                        Intent().apply { putExtra(TAG_ESTATE, estate) }
                    )
                    finish()
                }
                Enums.ShowEstateType.SHOW_ESTATE -> {
                    // TODO : Delete this estate
                }
            }
        }
        rightButton?.setOnClickListener {
            when (type!!) {
                Enums.ShowEstateType.ASK_FOR_CONFIRMATION -> {
                    // The user is satisfied by the current data displayed, so we send him back to
                    //  the previous Activity in order to save this [Estate].
                    setResult(
                        Activity.RESULT_OK,
                        Intent().apply { putExtra(TAG_ESTATE, estate) }
                    )
                    finish()
                }
                Enums.ShowEstateType.SHOW_ESTATE -> {
                    startActivity(EstateCreationActivity.newInstance(this, estate))
                    finish()
                }
            }
        }
    }

    /**
     *  Shows or hide "Nearby:" icons, given the [estate] data.
     */
    private fun setNearbyData() {
        if (estate!!.school == null && estate!!.playground == null && estate!!.shop == null
            && estate!!.buses == null && estate!!.subway == null && estate!!.park == null) {
            // The user did not provide data, hide the "Nearby:" layout
            viewModel.hideNearbyLayout()
            return
        }

        // TODO : Check null
        if (estate!!.school == false && estate!!.playground == false && estate!!.shop == false
            && estate!!.buses == false && estate!!.subway == false && estate!!.park == false) {
            // If there are nothing nearby, hide the "Nearby:" layout
            viewModel.hideNearbyLayout()
            return
        }

        // Setting "Nearby:" icons given [estate] data
        if (estate!!.school == true)
            addNearbyIconInFlexbox(R.drawable.ic_school)
        if (estate!!.playground == true)
            addNearbyIconInFlexbox(R.drawable.ic_playground)
        if (estate!!.shop == true)
            addNearbyIconInFlexbox(R.drawable.ic_shop)
        if (estate!!.buses == true)
            addNearbyIconInFlexbox(R.drawable.ic_bus_station)
        if (estate!!.subway == true)
            addNearbyIconInFlexbox(R.drawable.ic_subway_station)
        if (estate!!.park == true)
            addNearbyIconInFlexbox(R.drawable.ic_park)

        viewModel.showNearbyLayout()
    }

    /**
     *  Creates an [ImageView] with the drawable given in parameters, and adds it in the
     *  [FlexboxLayout].
     *  @param iconId [Int] - ID of the drawable resource.
     */
    private fun addNearbyIconInFlexbox(iconId : Int) {
        val imageView = ImageView(this)
        imageView.setImageResource(iconId)
        imageView.id = 0
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
        nearbyImagesFlexbox?.addView(imageView)

        val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
        imageViewLayoutParams.width = 100
        imageViewLayoutParams.height = 100
        imageViewLayoutParams.marginEnd = 25
    }

    companion object {

        @Suppress("unused")
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