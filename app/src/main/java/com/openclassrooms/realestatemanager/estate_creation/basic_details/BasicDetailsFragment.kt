package com.openclassrooms.realestatemanager.estate_creation.basic_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentBasicDetailsBinding
import com.openclassrooms.realestatemanager.utils.StaticData

class BasicDetailsFragment : Fragment() {

    // Helper classes
    private val viewModel = BasicDetailsFragmentViewModel()
    private var typeSpinnerAdapter : ArrayAdapter<String>? = null

    // Layout variables
    private var typeSpinner : Spinner? = null
    private var addressEditText : EditText? = null
    private var priceEditText : EditText? = null
    private var surfaceEditText : EditText? = null
    private var descriptionEditText : EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentBasicDetailsBinding>(
            LayoutInflater.from(context),
            R.layout.fragment_basic_details,
            container,
            false
        )

        binding.viewModel = viewModel

        // Init layout variables
        typeSpinner = binding.typeSpinner
        addressEditText = binding.addressEditText
        priceEditText = binding.priceEditText
        surfaceEditText = binding.surfaceEditText
        descriptionEditText = binding.descriptionEditText

        // Setup spinner
        typeSpinnerAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.estate_types)
        )
        typeSpinner?.adapter = typeSpinnerAdapter
        typeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                Log.d("OK", "OK")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return binding.root
    }

    companion object {
        fun newInstance() : Fragment {
            return BasicDetailsFragment()
        }
    }
}