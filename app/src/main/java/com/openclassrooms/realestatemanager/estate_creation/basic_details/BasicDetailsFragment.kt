package com.openclassrooms.realestatemanager.estate_creation.basic_details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentBasicDetailsBinding
import com.openclassrooms.realestatemanager.utils.TextValidator

class BasicDetailsFragment : Fragment() {

    // Helper classes
    private val viewModel = BasicDetailsFragmentViewModel()
    private var typeSpinnerAdapter : ArrayAdapter<String>? = null
/*    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkIfAllFieldsAreFilled()
        }

        override fun afterTextChanged(s: Editable?) {}
    }*/

    // Layout variables
    private var typeSpinner : Spinner? = null
    private var addressEditText : EditText? = null
    private var priceEditText : EditText? = null
    private var surfaceEditText : EditText? = null
    private var descriptionEditText : EditText? = null
    private var nextButton : Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

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
        nextButton = binding.nextButton

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

        // Setup text validators
        // TODO : Put this in common, set auto validate() function in TextValidator
        addressEditText?.addTextChangedListener(
            object : TextValidator(addressEditText) {
                override fun validate(editText: EditText?, text: String?) {
                    if (text.isNullOrBlank()) {
                        addressEditText?.error = getString(R.string.mandatory_field)
                    } else {
                        addressEditText?.error = null
                        checkIfAllFieldsAreFilled()
                    }
                }
            }
        )
        priceEditText?.addTextChangedListener(
            object : TextValidator(priceEditText) {
                override fun validate(editText: EditText?, text: String?) {
                    val regex = Regex("[0-9]+[.,]{0,1}[0-9]{0,2}")
                    if (text.isNullOrBlank()) {
                        priceEditText?.error = getString(R.string.mandatory_field)
                    } else if (!regex.matches(text)) {
                        priceEditText?.error = getString(R.string.incorrect_format)
                    } else {
                        priceEditText?.error = null
                        checkIfAllFieldsAreFilled()
                    }
                }
            }
        )
        surfaceEditText?.addTextChangedListener(
            object : TextValidator(surfaceEditText) {
                override fun validate(editText: EditText?, text: String?) {
                    if (text.isNullOrBlank()) {
                        surfaceEditText?.error = getString(R.string.mandatory_field)
                    } else {
                        surfaceEditText?.error = null
                        checkIfAllFieldsAreFilled()
                    }
                }
            }
        )

        // Setup nextButton
        nextButton?.setOnClickListener {
            Log.d(TAG, "Should go next")
        }

        return binding.root
    }

    private fun checkIfAllFieldsAreFilled() {
        viewModel.setButtonEnabled(
            !addressEditText?.text.isNullOrBlank() && addressEditText?.error == null
                    && !priceEditText?.text.isNullOrBlank() && priceEditText?.error == null
                    && !surfaceEditText?.text.isNullOrBlank() && surfaceEditText?.error == null
        )
    }

    companion object {

        private const val TAG = "BasicDetailsFragment"

        fun newInstance() : Fragment {
            return BasicDetailsFragment()
        }
    }
}