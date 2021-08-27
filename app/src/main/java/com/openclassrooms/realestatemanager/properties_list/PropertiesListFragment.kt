package com.openclassrooms.realestatemanager.properties_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentPropertiesListBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.show_estate.ShowEstateActivity

class PropertiesListFragment : Fragment() {

    // Helper classes
    private val viewModel = PropertiesListFragmentViewModel()
    private val propertiesListAdapter = PropertiesListAdapter {
        startActivity(ShowEstateActivity.newInstance(context, it))
    }

    // Layout variables
    private var propertiesListRv : RecyclerView? = null

    var estatesList = ArrayList<Estate>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment, using DataBinding
        val binding = DataBindingUtil.inflate<FragmentPropertiesListBinding>(
            inflater, R.layout.fragment_properties_list, container, true
        )

        binding.viewModel = viewModel

        if (estatesList.isEmpty()) viewModel.setNoProperties() else viewModel.setLoading()

        propertiesListRv = binding.propertiesListRv
        propertiesListRv?.layoutManager = LinearLayoutManager(context)
        propertiesListRv?.adapter = propertiesListAdapter

        propertiesListRv?.post {
            propertiesListAdapter.setData(estatesList)
            viewModel.setPropertiesList()
        }

        return binding.root
    }

    companion object {

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