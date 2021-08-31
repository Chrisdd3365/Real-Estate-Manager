package com.openclassrooms.realestatemanager.estate_creation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityEstateCreationBinding
import com.openclassrooms.realestatemanager.estate_creation.basic_details.BasicDetailsFragment
import com.openclassrooms.realestatemanager.estate_creation.optional_details.OptionalDetailsFragment
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.show_estate.ShowEstateActivity
import com.openclassrooms.realestatemanager.utils.Enums
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
    private var previousButton : Button? = null
    private var nextButton : Button? = null

    private var estate = Estate()
    private var optionalDetailsFragmentPosition = 0
    private var resultLauncher : ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityEstateCreationBinding>(
            this, R.layout.activity_estate_creation
        )

        binding.viewModel = viewModel

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d(TAG, "Result = $result")
        }

        // Init layout variables
        fragmentRoot = binding.fragmentRoot
        previousButton = binding.previousButton
        nextButton = binding.nextButton

        showFirstFragment()

        setupOptionalDetailsFragmentList()

        nextButton?.setOnClickListener {
            Log.d(TAG, "nextButton clicked !")
            goToNextOptionalDetails()
        }
        previousButton?.setOnClickListener { goToPreviousOptionalDetails() }
    }

    private fun setupOptionalDetailsFragmentList() {
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.rooms_count_question), Enums.OptionalDetailType.COUNT) { result: Any ->
                    try {
                        estate.roomCount = result as Int
                    } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.bathrooms_count_question), Enums.OptionalDetailType.COUNT) { result : Any ->
                    try {
                        estate.bathroomsCount = result as Int
                    } catch (ignored : Exception) {}
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.bedrooms_count_question), Enums.OptionalDetailType.COUNT) { result : Any ->
                    try {
                        estate.bedroomsCount = result as Int
                    } catch (ignored : Exception) {}
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.school_question), Enums.OptionalDetailType.CLOSED) { result : Any ->
                    try {
                        estate.school = result as Boolean
                    } catch (ignored : Exception) {}
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.playground_question), Enums.OptionalDetailType.CLOSED) { result : Any ->
                    try {
                        estate.playground = result as Boolean
                    } catch (ignored : Exception) {}
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.shop_question), Enums.OptionalDetailType.CLOSED) { result : Any ->
                    try {
                        estate.shop = result as Boolean
                    } catch (ignored : Exception) {}
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.park_question), Enums.OptionalDetailType.CLOSED) { result : Any ->
                    try {
                        estate.park = result as Boolean
                    } catch (ignored : Exception) {}
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.buses_question), Enums.OptionalDetailType.CLOSED) { result : Any ->
                    try {
                        estate.buses = result as Boolean
                    } catch (ignored : Exception) {}
                }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment
                .newInstance(getString(R.string.subway_question), Enums.OptionalDetailType.CLOSED) { result : Any ->
                    try {
                        estate.subway = result as Boolean
                    } catch (ignored : Exception) {}
                }
        )
    }

    private fun showFirstFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_root, basicDetailsFragment)
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
            .commit()
    }

    private fun goToNextOptionalDetails() {
        optionalDetailsFragmentPosition++
        if (optionalDetailsFragmentPosition > 0)
            viewModel.setButtonPreviousEnabled(true)
        if (optionalDetailsFragmentPosition < optionalDetailsFragmentList.size)
            goToOptionalDetails()
        else
            completeEstateCreation()
    }

    private fun goToPreviousOptionalDetails() {
        optionalDetailsFragmentPosition--
        if (optionalDetailsFragmentPosition >= 0)
            goToOptionalDetails()
        if (optionalDetailsFragmentPosition == 0)
            viewModel.setButtonPreviousEnabled(false)
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

    private fun completeEstateCreation() {
        // TODO : Here, we display the activity EstateDetailsActivity to ask if it is correct, then
        //  save the result in the database.
        resultLauncher?.launch(ShowEstateActivity.newInstance(
            this, estate, Enums.ShowEstateType.ASK_FOR_CONFIRMATION))
    }

    companion object {

        private const val TAG = "EstateCreationActivity"

        fun newInstance(context: Context) : Intent =
            Intent(context, EstateCreationActivity::class.java)

    }
}