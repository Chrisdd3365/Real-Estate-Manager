package com.openclassrooms.realestatemanager.show_estate

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.flexbox.FlexboxLayout
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentShowEstateBinding
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity
import com.openclassrooms.realestatemanager.estate_creation.managing_details.AgentsListAdapter
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums
import java.util.*
import kotlin.collections.ArrayList

class ShowEstateFragment(private val picturesRetrievedCallback : (ArrayList<Bitmap>) -> Unit = {},
                         private val managingAgentsRetrievedCallback: (ArrayList<Agent>) -> Unit = {})
    : Fragment() {

    // Helper classes
    private val viewModel = ShowEstateFragmentViewModel()
    private val managingAgentsAdapter = AgentsListAdapter(null, false)

    // Layout variables
    private var typeIcon : ImageView? = null
    private var leftButton : Button? = null
    private var rightButton : Button? = null
    private var markAsSoldButton : Button? = null
    private var nearbyImagesFlexbox : FlexboxLayout? = null
    private var picturesViewPager : ViewPager2? = null
    private var managingAgentsRv : RecyclerView? = null

    private var estate : Estate? = null
    private var type : Enums.ShowEstateType? = null
    private var picturesList = ArrayList<Bitmap>()
    private var managingAgents = ArrayList<Agent>()
    private var orientation = Configuration.ORIENTATION_UNDEFINED

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment, using DataBinding
        val binding = DataBindingUtil.inflate<FragmentShowEstateBinding>(
            inflater, R.layout.fragment_show_estate, container, false
        )

        binding.viewModel = viewModel

        if (arguments != null) {
            estate = arguments?.getSerializable(TAG_ESTATE) as Estate
            if (requireArguments().containsKey(TAG_TYPE)) {
                val typeOrdinal = arguments?.getInt(TAG_TYPE)
                type = Enums.ShowEstateType.values()[typeOrdinal ?: 0]
            }
            if (requireArguments().containsKey(TAG_PICTURES)) {
                val picturesArgs = arguments?.getParcelableArrayList<Parcelable>(TAG_PICTURES)
                if (picturesArgs != null) {
                    for (pictureArgs in picturesArgs) {
                        picturesList.add(pictureArgs as Bitmap)
                    }
                }
            }
            if (requireArguments().containsKey(TAG_AGENTS)) {
                val agentsArgs = arguments?.getSerializable(TAG_AGENTS) as ArrayList<*>
                for (agentArgs in agentsArgs) {
                    managingAgents.add(agentArgs as Agent)
                }
            }
            if (requireArguments().containsKey(TAG_ORIENTATION))
                orientation = requireArguments().getInt(TAG_ORIENTATION)
        } else {
            type = Enums.ShowEstateType.SHOW_ESTATE
        }

        typeIcon = binding.typeIcon
        leftButton = binding.leftButton
        rightButton = binding.rightButton
        markAsSoldButton = binding.markAsSoldButton
        nearbyImagesFlexbox = binding.nearbyImagesFlexbox
        picturesViewPager = binding.picturesViewPager
        managingAgentsRv = binding.managingAgentsRv

        setupOrientation(orientation)

        viewModel.setButtonsText(context, type!!)
        viewModel.setData(requireContext(), estate!!)

        setTypeIcon()

        setButtonsBehaviors()

        setNearbyData()

        setPicturesCarousel()

        if (type == Enums.ShowEstateType.SHOW_ESTATE)
            getManagingAgents()
        else
            setManagingAgents()

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
                viewModel.hideMarkAsSoldButton()
                leftButton?.setOnClickListener {
                    // The user wants to edit the data he provided, so we send him back to the
                    //  first fragment of EstateCreationActivity.
                    (activity as? BaseActivity)?.handleCompleteEstateCreationCancelled(estate!!)
                }
                rightButton?.setOnClickListener {
                    // The user is satisfied with the current data of the estate, we save this
                    //  [Estate].
                    (activity as? EstateCreationActivity)?.handleCompleteEstateCreationConfirmed()
                }
            }
            Enums.ShowEstateType.SHOW_ESTATE -> {
                viewModel.showMarkAsSoldButton()
                leftButton?.setOnClickListener {
                    (activity as? BaseActivity)?.deleteEstate(estate!!)
                }
                rightButton?.setOnClickListener {
                    (activity as? BaseActivity)?.handleCompleteEstateCreationCancelled(estate!!)
                }
                markAsSoldButton?.setOnClickListener {
                    estate?.sold = (estate?.sold != true)
                    estate?.soldDate = Calendar.getInstance()
                    viewModel.setSellDate(requireContext(), estate!!)
                    (activity as? BaseActivity)?.changeEstateSoldState(estate!!)
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

    private fun setPicturesCarousel() {

        if (estate?.id == null) {
            viewModel.hideImagesLayout()
            return
        }

        var pictures = ArrayList<Bitmap>()

        if (type == Enums.ShowEstateType.SHOW_ESTATE && estate != null && estate!!.id != null) {
            DatabaseManager(requireContext()).getImagesForEstate(
                estate!!.id!!,
                success = {
                    pictures = it
                    picturesRetrievedCallback.invoke(it)
                },
                failure = {
                    viewModel.hideImagesLayout()
                }
            )
        } else if (type == Enums.ShowEstateType.ASK_FOR_CONFIRMATION && estate != null && picturesList.isNotEmpty()) {
            for (picture : Bitmap in picturesList) {
                pictures.add(picture)
            }
        }

        if (pictures.isNotEmpty()) {
            val picturesViewPagerAdapter = PicturesSliderViewPagerAdapter()
            picturesViewPager?.adapter = picturesViewPagerAdapter
            picturesViewPager?.post { picturesViewPagerAdapter.setItems(pictures) }
            viewModel.showImagesLayout()
        } else
            viewModel.hideImagesLayout()
    }

    private fun getManagingAgents() {
        if (estate?.id == null)
            return
        managingAgents.clear()
        DatabaseManager(requireContext()).getEstateManagers(
            estate!!.id!!,
            success = {
                managingAgents = it
                managingAgentsRetrievedCallback.invoke(it)
                setManagingAgents()
            },
            failure = {
                // TODO : Touch to retry ?
            }
        )
    }

    private fun setManagingAgents() {
        if (managingAgents.isEmpty()) {
            viewModel.hideManagingAgents()
            return
        }

        managingAgentsRv?.layoutManager = LinearLayoutManager(context)
        managingAgentsRv?.adapter = managingAgentsAdapter
        managingAgentsRv?.post { managingAgentsAdapter.setItems(managingAgents) }
        viewModel.showManagingAgents()
    }

    fun setupOrientation(orientation : Int) {
        this.orientation = orientation
        if (picturesViewPager != null) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                picturesViewPager?.layoutParams = ViewGroup.LayoutParams(
                    MATCH_PARENT, 250)
            } else {
                picturesViewPager?.layoutParams = ViewGroup.LayoutParams(
                    MATCH_PARENT, 500)
            }
        }
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "ShowEstateActivity"

        private const val TAG_ORIENTATION = "orientation"
        private const val TAG_ESTATE = "estate"
        private const val TAG_TYPE = "type"
        private const val TAG_PICTURES = "pictures"
        private const val TAG_AGENTS = "agents"

        fun newInstance(estate : Estate?, type : Enums.ShowEstateType,
                        orientation: Int = Configuration.ORIENTATION_UNDEFINED,
                        picturesList : ArrayList<Bitmap>,
                        managingAgents : ArrayList<Agent>,
                        picturesRetrievedCallback : (ArrayList<Bitmap>) -> Unit,
                        managingAgentsRetrievedCallback : (ArrayList<Agent>) -> Unit)
        : ShowEstateFragment {

            val fragment = ShowEstateFragment(
                picturesRetrievedCallback,
                managingAgentsRetrievedCallback
            )
            val bundle = Bundle()
            bundle.putSerializable(TAG_ESTATE, estate)
            bundle.putInt(TAG_TYPE, type.ordinal)
            bundle.putParcelableArrayList(TAG_PICTURES, picturesList)
            bundle.putSerializable(TAG_AGENTS, managingAgents)
            bundle.putInt(TAG_ORIENTATION, orientation)
            fragment.arguments = bundle

            return fragment
        }

    }
}