package com.openclassrooms.realestatemanager.properties_list

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentPropertiesListBinding
import com.openclassrooms.realestatemanager.main.MainActivity
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.search_dialog.FilterDialogFragment
import com.openclassrooms.realestatemanager.show_estate.ShowEstateFragment
import com.openclassrooms.realestatemanager.utils.CustomDialogInterface
import com.openclassrooms.realestatemanager.utils.Enums
import com.openclassrooms.realestatemanager.utils.Utils
import kotlin.collections.ArrayList

class PropertiesListFragment : Fragment() {

    // Helper classes
    private val viewModel = PropertiesListFragmentViewModel()
    private val propertiesListAdapter = PropertiesListAdapter { estateClicked(it) }

    // Layout variables
    private var propertiesListRv : RecyclerView? = null
    private var filterButton : Button? = null

    var estatesList : ArrayList<Estate>? = null
    var filteredResults : ArrayList<Estate>? = null

    var orientation = Configuration.ORIENTATION_UNDEFINED
    private var showEstateFragment : ShowEstateFragment? = null
    private var selectedEstate : Estate? = null
    private var areResultsFiltered = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment, using DataBinding
        val binding = DataBindingUtil.inflate<FragmentPropertiesListBinding>(
            inflater, R.layout.fragment_properties_list, container, true
        )

        binding.viewModel = viewModel

        if (arguments != null && arguments?.containsKey(TAG_ESTATES) == true) {
            val estateArgs = requireArguments().getSerializable(TAG_ESTATES) as ArrayList<*>
            estatesList = ArrayList()
            for (estateArg in estateArgs) {
                estatesList?.add(estateArg as Estate)
            }
        }

        setupOrientation(resources.configuration.orientation)

        if (estatesList?.isEmpty() == true) viewModel.setNoProperties() else viewModel.setLoading()

        filterButton = binding.filterButton

        propertiesListRv = binding.propertiesListRv
        propertiesListRv?.layoutManager = LinearLayoutManager(context)
        propertiesListRv?.adapter = propertiesListAdapter

        propertiesListRv?.post {
            propertiesListAdapter.setData(context, estatesList ?: ArrayList())
            viewModel.setPropertiesList()
            viewModel.setDefaultFilterButton(requireContext())
        }

        filterButton?.setOnClickListener {
            if (areResultsFiltered) setDefaultList() else showFilterDialog()
        }

        return binding.root
    }

    private fun showFilterDialog() {
        val filterDialogFragment = FilterDialogFragment.newInstance()
        filterDialogFragment.customDialogInterface = object : CustomDialogInterface {
            override fun cancelButtonClicked(dialog : Dialog) {
                dialog.dismiss()
            }

            override fun confirmSearchClicked(
                priceRange: IntArray, surfaceRange: IntArray,
                roomsRange: IntArray, bathroomsRange: IntArray,
                bedroomsRange: IntArray, schoolValue: Boolean,
                playgroundValue: Boolean, shopValue: Boolean,
                busesValue: Boolean, subwayValue: Boolean,
                parkValue: Boolean, fromDate: Long, sold: Boolean
            ) {
                viewModel.setLoading()
                (activity as MainActivity).filterEstates(priceRange, surfaceRange, roomsRange,
                    bathroomsRange, bedroomsRange, schoolValue, playgroundValue, shopValue,
                    busesValue, subwayValue, parkValue, fromDate, sold)
            }
        }

        filterDialogFragment.show(childFragmentManager, TAG)
    }

    fun displaySearchResults(results: ArrayList<Estate>) {
        filteredResults = results
        if (results.isEmpty()) {
            viewModel.setNoProperties()
        } else {
            propertiesListAdapter.setData(requireContext(), filteredResults!!)
            viewModel.setPropertiesList()
        }
        viewModel.setResultsFiltered(requireContext())
        areResultsFiltered = true
    }

    private fun setDefaultList() {
        filteredResults?.clear()
        filteredResults = null
        if (estatesList.isNullOrEmpty()) {
            viewModel.setNoProperties()
        } else {
            propertiesListAdapter.setData(requireContext(), estatesList!!)
            viewModel.setPropertiesList()
        }
        viewModel.setDefaultFilterButton(requireContext())
        areResultsFiltered = false
    }

    private fun estateClicked(clicked : Estate) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && estatesList != null) {
            selectedEstate = clicked
            setupEstatePreview()
            propertiesListAdapter.selectItem(estatesList!!.indexOf(clicked))
        } else {
            (activity as? MainActivity)?.estateClicked(clicked)
        }
    }

    fun setEstateList(list : ArrayList<Estate>) {
        // TODO : If the list is empty, show "No items" messages
        this.estatesList = list
        propertiesListRv?.post {
            propertiesListAdapter.setData(context, estatesList ?: ArrayList())
            viewModel.setPropertiesList()
        }
    }

    fun addNewEstate(estate : Estate) {
        if (estatesList == null)
            estatesList = ArrayList()
        estatesList!!.add(estate)
        propertiesListRv?.post { propertiesListAdapter.addItem(0, estate) }
    }

    fun addTestingEstates(testingEstates : ArrayList<Estate>) {
        for (testingEstate in testingEstates) {
            Utils.checkEstatesTimeOnMarket(testingEstate.onMarketSince!!)
        }
        estatesList?.addAll(testingEstates)
        estatesList?.sortByDescending { it.onMarketSince?.timeInMillis }
        propertiesListRv?.post { propertiesListAdapter.setData(requireContext(), estatesList!!) }
    }

    fun editEstateAtPosition(position: Int, estate: Estate) {
        if (estatesList != null && position < estatesList!!.size)
            estatesList!![position] = estate
        propertiesListRv?.post { propertiesListAdapter.changeItem(position, estate) }
    }

    fun removeEstateAtPosition(position: Int) {
        propertiesListRv?.post { propertiesListAdapter.removeItem(position) }
        estatesList?.removeAt(position)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (estatesList != null && estatesList?.isNotEmpty() == true) {
                val newSelectedPosition = if (position == 0) position else position - 1
                selectedEstate = estatesList!![newSelectedPosition]
                propertiesListAdapter.selectItem(newSelectedPosition)
                setupEstatePreview()
            } else {
                // TODO
            }
        }
    }

    /**
     *  Updates the [propertiesListAdapter] items to change the currency displayed.
     */
    fun currencyChanged() {
        propertiesListAdapter.notifyItemRangeChanged(0, propertiesListAdapter.itemCount)
        if (showEstateFragment != null)
            setupEstatePreview()
    }

    fun unitChanged() {
        propertiesListAdapter.notifyItemRangeChanged(0, propertiesListAdapter.itemCount)
        if (showEstateFragment != null)
            setupEstatePreview()
    }

    fun setupOrientation(orientation : Int) {
        this.orientation = orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupEstatePreview()
            viewModel.setEstatePreview()
            if (showEstateFragment != null)
                showEstateFragment?.setupOrientation(orientation)
        } else {
            viewModel.removeEstatePreview()
            propertiesListAdapter.unselectItem()
        }
    }

    private fun setupEstatePreview() {
        if (selectedEstate == null && (estatesList == null || estatesList!!.isEmpty()))
            return
        if (selectedEstate == null && estatesList != null) {
            selectedEstate = estatesList!![0]
            propertiesListAdapter.selectItem(0)
        }

        showEstateFragment = ShowEstateFragment.newInstance(
            selectedEstate,
            Enums.ShowEstateType.SHOW_ESTATE,
            orientation,
            ArrayList(), ArrayList(),
            // TODO : Set a default function for this
            picturesRetrievedCallback = {},
            managingAgentsRetrievedCallback = {}
        )

        childFragmentManager.beginTransaction()
            .replace(R.id.estate_preview_layout, showEstateFragment!!)
            .commit()
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "PropertiesListFrag"

        private const val TAG_ESTATES = "estates"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PropertiesListFragment.
         */
        fun newInstance(estateList : ArrayList<Estate>) : PropertiesListFragment {
            val fragment = PropertiesListFragment()
            val bundle = Bundle()
            bundle.putSerializable(TAG_ESTATES, estateList)
            fragment.arguments = bundle
            return fragment
        }
    }
}