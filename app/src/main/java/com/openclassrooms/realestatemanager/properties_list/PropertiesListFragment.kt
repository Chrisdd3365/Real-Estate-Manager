package com.openclassrooms.realestatemanager.properties_list

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentPropertiesListBinding
import com.openclassrooms.realestatemanager.main.MainActivity
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.show_estate.ShowEstateFragment
import com.openclassrooms.realestatemanager.utils.Enums

class PropertiesListFragment : Fragment() {

    // Helper classes
    private val viewModel = PropertiesListFragmentViewModel()
    private val propertiesListAdapter = PropertiesListAdapter { estateClicked(it) }

    // Layout variables
    private var propertiesListRv : RecyclerView? = null

    var estatesList = ArrayList<Estate>()

    var orientation = Configuration.ORIENTATION_UNDEFINED
    private var showEstateFragment : ShowEstateFragment? = null
    private var selectedEstate : Estate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment, using DataBinding
        val binding = DataBindingUtil.inflate<FragmentPropertiesListBinding>(
            inflater, R.layout.fragment_properties_list, container, true
        )

        binding.viewModel = viewModel

        setupOrientation(resources.configuration.orientation)

        if (estatesList.isEmpty()) viewModel.setNoProperties() else viewModel.setLoading()

        propertiesListRv = binding.propertiesListRv
        propertiesListRv?.layoutManager = LinearLayoutManager(context)
        propertiesListRv?.adapter = propertiesListAdapter

        propertiesListRv?.post {
            propertiesListAdapter.setData(context, estatesList)
            viewModel.setPropertiesList()
        }

        return binding.root
    }

    private fun estateClicked(clicked : Estate) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            selectedEstate = clicked
            setupEstatePreview()
        } else {
            (activity as? MainActivity)?.estateClicked(clicked)
        }
    }

    fun setEstateList(list : ArrayList<Estate>) {
        this.estatesList = list
        propertiesListRv?.post {
            propertiesListAdapter.setData(context, estatesList)
            viewModel.setPropertiesList()
        }
    }

    fun addNewEstate(estate : Estate) {
        estatesList.add(estate)
        propertiesListRv?.post { propertiesListAdapter.addItem(0, estate) }
    }

    fun editEstateAtPosition(position: Int, estate: Estate) {
        estatesList[position] = estate
        propertiesListRv?.post { propertiesListAdapter.changeItem(position, estate) }
    }

    fun removeEstateAtPosition(position: Int) {
        propertiesListRv?.post { propertiesListAdapter.removeItem(position) }
        estatesList.removeAt(position)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (estatesList.isNotEmpty()) {
                val newSelectedPosition = if (position == 0) position else position - 1
                selectedEstate = estatesList[newSelectedPosition]
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
        } else {
            viewModel.removeEstatePreview()
        }
    }

    private fun setupEstatePreview() {
        if (selectedEstate == null && estatesList.isEmpty())
            return
        if (selectedEstate == null)
            selectedEstate = estatesList[0]

        showEstateFragment = ShowEstateFragment.newInstance(
            selectedEstate,
            Enums.ShowEstateType.SHOW_ESTATE,
            ArrayList(), ArrayList(),
            picturesRetrievedCallback = {

            },
            managingAgentsRetrievedCallback = {

            }
        )

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.estate_preview_layout,
                showEstateFragment!!
            )
            ?.commit()
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "PropertiesListFrag"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PropertiesListFragment.
         */
        fun newInstance(estateList : ArrayList<Estate>) : PropertiesListFragment {
            val fragment = PropertiesListFragment()
            fragment.estatesList = estateList

            return fragment
        }
    }
}