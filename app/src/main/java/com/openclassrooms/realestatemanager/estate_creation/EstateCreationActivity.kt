package com.openclassrooms.realestatemanager.estate_creation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityEstateCreationBinding
import com.openclassrooms.realestatemanager.estate_creation.basic_details.BasicDetailsFragment
import com.openclassrooms.realestatemanager.estate_creation.optional_details.OptionalDetailsFragment
import com.openclassrooms.realestatemanager.model.Estate
import java.lang.Exception

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
    private val optionalDetailsFragmentList : ArrayList<OptionalDetailsFragment> = ArrayList()

    // Layout variables
    private var fragmentRoot : ConstraintLayout? = null

    private var estate = Estate()
    private var optionalDetailsFragmentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityEstateCreationBinding>(
            this, R.layout.activity_estate_creation
        )

        binding.viewModel = viewModel

        fragmentRoot = binding.fragmentRoot

        showFirstFragment()

        setupOptionalDetailsFragmentList()
    }

    private fun setupOptionalDetailsFragmentList() {
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.rooms_count_question)) { result: Any ->
                    try { estate.roomCount = result as Int } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.bathrooms_count_question)) { result : Any ->
                    try { estate.bathroomsCount = result as Int } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.bedrooms_count_question)) { result : Any ->
                    try { estate.bedroomsCount = result as Int } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.school_question)) { result : Any ->
                    try { estate.school = result as Boolean } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.playground_question)) { result : Any ->
                    try { estate.playground = result as Boolean } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.shop_question)) { result : Any ->
                    try { estate.shop = result as Boolean } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.park_question)) { result : Any ->
                    try { estate.park = result as Boolean } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.buses_question)) { result : Any ->
                    try { estate.buses = result as Boolean } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.subway_question)) { result : Any ->
                    try { estate.subway = result as Boolean } catch (ignored : Exception) {}
                    goToNextOptionalDetails()
                }
        )
    }

    private fun showFirstFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_root, basicDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun goToOptionalDetails() {
        viewModel.setSkipTextVisibility(View.VISIBLE)
        viewModel.setNavigationButtonVisibility(View.VISIBLE)
        Log.d(TAG, "Should go to optional details")
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_root,
                optionalDetailsFragmentList[optionalDetailsFragmentPosition]
            )
            .addToBackStack(null)
            .commit()
    }

    private fun goToNextOptionalDetails() {
        Log.d(TAG, "Current roomCount = ${estate.roomCount}")
        optionalDetailsFragmentPosition++
        if (optionalDetailsFragmentPosition < optionalDetailsFragmentList.size)
            goToOptionalDetails()
    }

    fun setupEstate(typeIndex : Int, address : String, price : Float, surface : Float,
                    description : String) {
        estate.apply {
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