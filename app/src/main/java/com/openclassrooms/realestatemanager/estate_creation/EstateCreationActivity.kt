package com.openclassrooms.realestatemanager.estate_creation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityEstateCreationBinding
import com.openclassrooms.realestatemanager.estate_creation.basic_details.BasicDetailsFragment
import com.openclassrooms.realestatemanager.estate_creation.optional_details.OptionalDetailsFragment
import com.openclassrooms.realestatemanager.model.Estate

/**
 *  This [AppCompatActivity] will handle numerous [androidx.fragment.app.Fragment] that will handle
 *  the [com.openclassrooms.realestatemanager.model.Estate] creation :
 *  - BasicDetailsFragment : For the type, description, address, price & surface
 *  - Skip-able fragments with a single question on each of them (room count, etc).
 */
class EstateCreationActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = EstateCreationActivityViewModel()

    // Fragments
    private val basicDetailsFragment = BasicDetailsFragment.newInstance()
    private val optionalDetailsFragment = OptionalDetailsFragment.newInstance()

    // Layout variables
    private var fragmentRoot : ConstraintLayout? = null

    private var estate : Estate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityEstateCreationBinding>(
            this, R.layout.activity_estate_creation
        )

        binding.viewModel = viewModel

        fragmentRoot = binding.fragmentRoot

        showFirstFragment()
    }

    private fun showFirstFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_root, basicDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun goToOptionalDetails() {
        Log.d(TAG, "Should go to optional details")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_root, optionalDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun setupEstate(typeIndex : Int, address : String, price : Float, surface : Float,
                    description : String) {
        estate = Estate().apply {
            this.typeIndex = typeIndex
            this.address = address
            this.price = price
            this.surface = surface
            this.description = description
        }
    }

    companion object {

        private const val TAG = "EstateCreationActivity"

        fun newInstance(context: Context) : Intent =
            Intent(context, EstateCreationActivity::class.java)

    }
}