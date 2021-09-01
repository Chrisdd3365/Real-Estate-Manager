package com.openclassrooms.realestatemanager.estate_creation

import android.app.Activity
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
    private var optionalDetailsFragmentPosition = -1
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
            if (result.resultCode == Activity.RESULT_CANCELED)
                handleCompleteEstateCreationCancelled(result.data)
            else if (result.resultCode == Activity.RESULT_OK)
                handleCompleteEstateCreationConfirmed(result.data)
        }

        // Init layout variables
        fragmentRoot = binding.fragmentRoot
        previousButton = binding.previousButton
        nextButton = binding.nextButton

        showFirstFragment()

        setupOptionalDetailsFragmentList()

        nextButton?.setOnClickListener {

            if (optionalDetailsFragmentPosition == -1)
                // If we are on the basicDetailsFragment, setup the Estate with the data from it.
                estate = (basicDetailsFragment as BasicDetailsFragment).getEstate()

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

    /**
     *  Shows the first fragment, with the type, address, price, surface, and description.
     *  The "Previous" button is hidden, as there is no fragment before this one.
     *  We use `.replace` instead of `.add`, as we can have another fragment if the user clicks on
     *  "Cancel" button in [ShowEstateActivity].
     */
    private fun showFirstFragment() {
        viewModel.setNavigationButtonVisibility(View.GONE, View.VISIBLE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_root, basicDetailsFragment)
            .commit()
    }

    /**
     *  Replace the current fragment with an [OptionalDetailsFragment] instance, given the index
     *  [optionalDetailsFragmentPosition] ; and enable "Previous" and "Next" buttons.
     */
    private fun goToOptionalDetails() {
        viewModel.setSkipTextVisibility(View.VISIBLE)
        viewModel.setNavigationButtonVisibility(View.VISIBLE, View.VISIBLE)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_root,
                optionalDetailsFragmentList[optionalDetailsFragmentPosition]
            )
            .commit()
    }

    /**
     *  Increment [optionalDetailsFragmentPosition]. If there is more [OptionalDetailsFragment] to
     *  display, we call [goToOptionalDetails]. In the other case, we show the [Estate] result with
     *  [completeEstateCreation].
     */
    private fun goToNextOptionalDetails() {
        optionalDetailsFragmentPosition++
        if (optionalDetailsFragmentPosition >= 0)
            viewModel.setButtonPreviousEnabled(true)
        if (optionalDetailsFragmentPosition < optionalDetailsFragmentList.size)
            goToOptionalDetails()
        else
            completeEstateCreation()
    }

    /**
     *  Decrement [optionalDetailsFragmentPosition]. If it is equals to -1, that means we need to
     *  go back to the [BasicDetailsFragment], and hide "Previous" button.
     *  In the other case, we go to the right [OptionalDetailsFragment].
     */
    private fun goToPreviousOptionalDetails() {
        Log.d(TAG, "goToPreviousOptionalDetail() ; $optionalDetailsFragmentPosition")
        optionalDetailsFragmentPosition--
        if (optionalDetailsFragmentPosition >= 0)
            goToOptionalDetails()
        else {
            showFirstFragment()
            viewModel.setNavigationButtonVisibility(View.GONE, View.VISIBLE)
        }
    }

    /**
     *  This function is used by [BasicDetailsFragment], and is used to enable or disable "Next"
     *  button, given if the fields are correctly filled.
     *  @param enable ([Boolean]) Decides if the "Next" button should be enabled or not.
     */
    fun setNextButtonAbility(enable : Boolean) {
        viewModel.setButtonNextEnabled(enable)
    }

    /**
     *  Displays the activity [ShowEstateActivity] to ask if the user is satisfied with the data he
     *  provided. We need to wait for a result, as this [EstateCreationActivity] will save the
     *  [Estate], or allow the user to edit it.
     */
    private fun completeEstateCreation() {
        resultLauncher?.launch(ShowEstateActivity.newInstance(
            this, estate, Enums.ShowEstateType.ASK_FOR_CONFIRMATION))
    }

    /**
     *  This function is used when the user clicked on "Cancel" button in [ShowEstateActivity]
     *  (or if he presses the back button).
     *  It will remove every [androidx.fragment.app.Fragment] in this [Activity], and display again
     *  [BasicDetailsFragment] to allow the user to edit the data he provided.
     *  We hide "Previous" button and set [optionalDetailsFragmentPosition] to -1, then call
     *  [showFirstFragment].
     */
    private fun handleCompleteEstateCreationCancelled(resultIntent : Intent?) {
        if (resultIntent != null && resultIntent.hasExtra(TAG_ESTATE))
            estate = resultIntent.extras!!.get(TAG_ESTATE) as Estate

        // Remove every fragment in the Activity
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        viewModel.setNavigationButtonVisibility(View.GONE, View.VISIBLE)

        optionalDetailsFragmentPosition = -1
        showFirstFragment()
    }

    /**
     *  This function is used when the user clicked on "Confirm" button in [ShowEstateActivity].
     *  It will save the [Estate] instance in the database.
     *  TODO
     */
    private fun handleCompleteEstateCreationConfirmed(resultIntent: Intent?) {
        if (resultIntent != null && resultIntent.hasExtra(TAG_ESTATE)) {
            // TODO : Save this estate in the database
            Log.d(TAG, "Should save estate")
        } else {
            // TODO : Display error
            Log.d(TAG, "Error")
        }
    }

    companion object {

        private const val TAG = "EstateCreationActivity"

        private const val TAG_ESTATE = "estate"

        fun newInstance(context: Context) : Intent =
            Intent(context, EstateCreationActivity::class.java)

    }
}