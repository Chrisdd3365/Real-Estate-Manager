package com.openclassrooms.realestatemanager.estate_creation.basic_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentBasicDetailsBinding
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.TextValidator

class BasicDetailsFragment(private var estate: Estate?) : Fragment() {

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

        // Setup spinner
        typeSpinnerAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.estate_types)
        )
        typeSpinner?.adapter = typeSpinnerAdapter

        // Setup default variables if needed
        if (estate != null) {
            if (estate?.typeIndex != null) typeSpinner?.setSelection(estate?.typeIndex!!)
            else typeSpinner?.setSelection(0)
            addressEditText?.setText(estate?.address)
            priceEditText?.setText(estate?.getPrice())
            surfaceEditText?.setText(estate?.getSurface())
            descriptionEditText?.setText(estate?.description)

            checkIfAllFieldsAreFilled()
        }

        setupValidators()

        return binding.root
    }

    /**
     *  This function calls [EstateCreationActivity.setNextButtonAbility] to enable or disable the
     *  "Next" button, given if all the mandatory fields (address, price and surface) are filled.
     */
    private fun checkIfAllFieldsAreFilled() {
        (activity as EstateCreationActivity).setNextButtonAbility(
            !addressEditText?.text.isNullOrBlank() && addressEditText?.error == null
                    && !priceEditText?.text.isNullOrBlank() && priceEditText?.error == null
                    && !surfaceEditText?.text.isNullOrBlank() && surfaceEditText?.error == null
        )
    }

    /**
     *  This function is only called by [EstateCreationActivity], when the user presses "Next"
     *  button for the first time. It is used to save the data provided in the current [Fragment],
     *  in an [Estate] instance in [EstateCreationActivity].
     *  @return ([Estate]) An [Estate] instance, pre-filled with data filled in this
     *  [BasicDetailsFragment].
     */
    fun getEstate() : Estate? {
        if (estate == null)
            estate = Estate()
        estate?.apply {
            typeIndex = typeSpinner?.selectedItemPosition!!
            address = addressEditText?.text.toString()
            setPrice(priceEditText?.text.toString().toDouble())
            setSurface(surfaceEditText?.text.toString().toDouble())
            description = descriptionEditText?.text.toString()
        }
        return estate
    }

    /**
     *  Adds validators on [addressEditText], [surfaceEditText] and [priceEditText].
     */
    private fun setupValidators() {
        addBasicTextChangedValidator(addressEditText)
        addBasicTextChangedValidator(surfaceEditText)

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
    }

    /**
     *  Adds a basic [TextValidator] on the provided [EditText], that will check the input at each
     *  change.
     *  @param editText [EditText] - The [EditText] to validate.
     */
    private fun addBasicTextChangedValidator(editText : EditText?) {
        editText?.addTextChangedListener(
            object : TextValidator(editText) {
                override fun validate(editText: EditText?, text: String?) {
                    if (text.isNullOrBlank()) {
                        editText?.error = getString(R.string.mandatory_field)
                    } else {
                        editText?.error = null
                        checkIfAllFieldsAreFilled()
                    }
                }
            }
        )
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "BasicDetailsFragment"

        fun newInstance(estate: Estate?) : Fragment {
            return BasicDetailsFragment(estate)
        }
    }
}