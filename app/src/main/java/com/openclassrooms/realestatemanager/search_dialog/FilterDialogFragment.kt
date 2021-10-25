package com.openclassrooms.realestatemanager.search_dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentFilterDialogBinding
import com.openclassrooms.realestatemanager.utils.CustomDialogInterface

class FilterDialogFragment : DialogFragment() {

    var customDialogInterface = object : CustomDialogInterface {
        override fun cancelButtonClicked() {
        }

        override fun confirmSearchClicked() {
        }

    }

    val viewModel = FilterDialogFragmentViewModel()

    // Layout variables

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentFilterDialogBinding>(
            inflater, R.layout.fragment_filter_dialog, container, false
        )

        return binding.root
    }

    companion object {

        fun newInstance(context: Context) : FilterDialogFragment {
            return FilterDialogFragment()
        }
    }
}