package com.openclassrooms.realestatemanager.show_estate

import android.app.Activity
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexboxLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentShowEstateBinding
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums

/**
 *  TODO : This [Activity] should only displays a [androidx.fragment.app.Fragment] with all this
 *      content, as we need to be able to re-use this fragment for landscape display, on the right
 *      of the element selected in the [PropertiesListFragment].
 */
class ShowEstateFragment(private var estate: Estate?, private var type : Enums.ShowEstateType)
    : Fragment() {

    // Helper classes
    private val viewModel = ShowEstateFragmentViewModel()

    // Layout variables
    private var typeIcon : ImageView? = null
    private var leftButton : Button? = null
    private var rightButton : Button? = null
    private var nearbyImagesFlexbox : FlexboxLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment, using DataBinding
        val binding = DataBindingUtil.inflate<FragmentShowEstateBinding>(
            inflater, R.layout.fragment_show_estate, container, false
        )

        binding.viewModel = viewModel

        typeIcon = binding.typeIcon
        leftButton = binding.leftButton
        rightButton = binding.rightButton
        nearbyImagesFlexbox = binding.nearbyImagesFlexbox

        viewModel.setButtonsText(context, type)
        viewModel.setData(context, estate!!)

        setTypeIcon()

        setButtonsBehaviors()

        setNearbyData()

        return binding.root
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
        when (type) {
            Enums.ShowEstateType.ASK_FOR_CONFIRMATION -> {
                leftButton?.setOnClickListener {
                    // The user wants to edit the data he provided, so we send him back to the
                    //  first fragment of EstateCreationActivity.
                    (activity as? EstateCreationActivity)?.handleCompleteEstateCreationCancelled()
                }
                rightButton?.setOnClickListener {
                    // The user is satisfied with the current data of the estate, we save this
                    //  [Estate].
                    (activity as? EstateCreationActivity)?.handleCompleteEstateCreationConfirmed()
                }
            }
            Enums.ShowEstateType.SHOW_ESTATE -> {
                // TODO()
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

        Log.d(TAG, "Nearby icons set")
        viewModel.showNearbyLayout()
        Log.d(TAG, "End of setNearbyData()")
    }

    /**
     *  Creates an [ImageView] with the drawable given in parameters, and adds it in the
     *  [FlexboxLayout].
     *  @param iconId [Int] - ID of the drawable resource.
     */
    private fun addNearbyIconInFlexbox(iconId : Int) {
        Log.d(TAG, "addNearbyIconInFlexbox with icon $iconId")
        val imageView = ImageView(context)
        imageView.setImageResource(iconId)
        imageView.id = 0
        imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
        nearbyImagesFlexbox?.addView(imageView)

        val imageViewLayoutParams = (imageView.layoutParams as FlexboxLayout.LayoutParams)
        imageViewLayoutParams.width = 100
        imageViewLayoutParams.height = 100
        imageViewLayoutParams.marginEnd = 25
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "ShowEstateActivity"

        fun newInstance(estate : Estate?, type : Enums.ShowEstateType) : ShowEstateFragment {
            return ShowEstateFragment(estate, type)
        }

    }
}