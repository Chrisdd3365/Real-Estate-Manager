package com.openclassrooms.realestatemanager.estate_creation.optional_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentOptionalDetailsBinding

class OptionalDetailsFragment : Fragment() {

    private val viewModel = OptionalDetailsFragmentViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = DataBindingUtil.inflate<FragmentOptionalDetailsBinding>(
            LayoutInflater.from(context),
            R.layout.fragment_optional_details,
            container,
            false
        )

        binding.viewModel = viewModel

        return binding.root
    }

    companion object {
        fun newInstance() : OptionalDetailsFragment {
            return OptionalDetailsFragment()
        }
    }
}