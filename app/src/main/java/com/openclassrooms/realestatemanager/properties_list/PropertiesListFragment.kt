package com.openclassrooms.realestatemanager.properties_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentPropertiesListBinding

class PropertiesListFragment : Fragment() {

    // Helper classes
    private val viewModel = PropertiesListFragmentViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment, using DataBinding
        val binding = DataBindingUtil.inflate<FragmentPropertiesListBinding>(
            inflater, R.layout.fragment_properties_list, container, true
        )

        binding.viewModel = viewModel

        return binding.root
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PropertiesListFragment.
         */
        fun newInstance() : Fragment {
            return PropertiesListFragment()
        }
    }
}