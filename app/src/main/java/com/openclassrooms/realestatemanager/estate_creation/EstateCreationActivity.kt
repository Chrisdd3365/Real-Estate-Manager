package com.openclassrooms.realestatemanager.estate_creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityEstateCreationBinding
import com.openclassrooms.realestatemanager.estate_creation.add_images.AddPicturesFragment
import com.openclassrooms.realestatemanager.estate_creation.basic_details.BasicDetailsFragment
import com.openclassrooms.realestatemanager.estate_creation.managing_details.ManagingFragment
import com.openclassrooms.realestatemanager.estate_creation.optional_details.OptionalDetailsFragment
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.show_estate.ShowEstateFragment
import com.openclassrooms.realestatemanager.utils.Enums

/**
 *  This [AppCompatActivity] will handle numerous [androidx.fragment.app.Fragment] that will handle
 *  the [com.openclassrooms.realestatemanager.model.Estate] creation :
 *  - BasicDetailsFragment : For the type, description, address, price & surface
 *  - Skip-able fragments with a single question on each of them (room count, etc).
 */
class EstateCreationActivity : BaseActivity() {

    // Helper classes
    private val viewModel = EstateCreationActivityViewModel()

    // Fragments
    private var basicDetailsFragment : Fragment? = null
    private val optionalDetailsFragmentList : ArrayList<OptionalDetailsFragment> = ArrayList()

    // Layout variables
    private var fragmentRoot : ConstraintLayout? = null
    private var previousButton : Button? = null
    private var nextButton : Button? = null

    private var optionalDetailsFragmentPosition = -1
    private var isEditing = false
    private var isNewEstate = false
    private var picturesList = ArrayList<Bitmap>()
    private var managingAgents = ArrayList<Agent>()
    private var estatePosition : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityEstateCreationBinding>(
            this, R.layout.activity_estate_creation
        )

        binding.viewModel = viewModel

        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        viewModel.setLoading()

        if (estate == null) {
            if (intent.hasExtra(TAG_ESTATE)) {
                estate = intent.extras!!.get(TAG_ESTATE) as Estate
                basicDetailsFragment = BasicDetailsFragment.newInstance(estate)
                isEditing = true
            } else
                estate = Estate()
        }

        if (intent.hasExtra(TAG_NEW_ESTATE))
            isNewEstate = intent.getBooleanExtra(TAG_NEW_ESTATE, true)

        if (intent.hasExtra(TAG_CURRENT_LOCATION))
            estatePosition = intent.extras?.get(TAG_CURRENT_LOCATION) as LatLng

        // Init layout variables
        fragmentRoot = binding.fragmentRoot
        previousButton = binding.previousButton
        nextButton = binding.nextButton

        setupButtons()

        if (isNewEstate) {
            showFirstFragment(null)
            setupOptionalDetailsFragmentList()
        } else {
            showFullEstate(Enums.ShowEstateType.SHOW_ESTATE)
        }
    }

    private fun setupButtons() {
        nextButton?.setOnClickListener {

            if (optionalDetailsFragmentPosition == -1 && basicDetailsFragment != null) {
                // If we are on the basicDetailsFragment, setup the Estate with the data from it.
                val basicDetailsFragmentResult =
                    (basicDetailsFragment as BasicDetailsFragment).getEstate()
                if (basicDetailsFragmentResult != null)
                    estate = basicDetailsFragmentResult
            }

            goToNextOptionalDetails()
        }
        previousButton?.setOnClickListener { goToPreviousOptionalDetails() }
    }

    private fun setupOptionalDetailsFragmentList() {
        optionalDetailsFragmentList.clear()
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.rooms_count_question),
                Enums.OptionalDetailType.COUNT,
                estate?.roomCount
            ) { result: Any ->
                try {
                    estate?.roomCount = result as Int
                    optionalDetailsFragmentList[0].setDefaultValue(estate?.roomCount)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.bathrooms_count_question),
                Enums.OptionalDetailType.COUNT,
                estate?.bathroomsCount
            ) { result : Any ->
                try {
                    estate?.bathroomsCount = result as Int
                    optionalDetailsFragmentList[1].setDefaultValue(estate?.bathroomsCount)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.bedrooms_count_question),
                Enums.OptionalDetailType.COUNT,
                estate?.bedroomsCount
            ) { result : Any ->
                try {
                    estate?.bedroomsCount = result as Int
                    optionalDetailsFragmentList[2].setDefaultValue(estate?.bedroomsCount)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.school_question),
                Enums.OptionalDetailType.CLOSED,
                estate?.school
            ) { result : Any ->
                try {
                    estate?.school = result as Boolean
                    optionalDetailsFragmentList[3].setDefaultValue(estate?.school)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.playground_question),
                Enums.OptionalDetailType.CLOSED,
                estate?.playground
            ) { result : Any ->
                try {
                    estate?.playground = result as Boolean
                    optionalDetailsFragmentList[4].setDefaultValue(estate?.playground)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.shop_question),
                Enums.OptionalDetailType.CLOSED,
                estate?.shop
            ) { result : Any ->
                try {
                    estate?.shop = result as Boolean
                    optionalDetailsFragmentList[5].setDefaultValue(estate?.shop)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.park_question),
                Enums.OptionalDetailType.CLOSED,
                estate?.park
            ) { result : Any ->
                try {
                    estate?.park = result as Boolean
                    optionalDetailsFragmentList[6].setDefaultValue(estate?.park)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.buses_question),
                Enums.OptionalDetailType.CLOSED,
                estate?.buses
            ) { result : Any ->
                try {
                    estate?.buses = result as Boolean
                    optionalDetailsFragmentList[7].setDefaultValue(estate?.buses)
                } catch (ignored : Exception) {}
            }
        )
        optionalDetailsFragmentList.add(
            OptionalDetailsFragment.newInstance(
                getString(R.string.subway_question),
                Enums.OptionalDetailType.CLOSED,
                estate?.subway
            ) { result : Any ->
                try {
                    estate?.subway = result as Boolean
                    optionalDetailsFragmentList[8].setDefaultValue(estate?.subway)
                } catch (ignored : Exception) {}
            }
        )
    }

    /**
     *  Shows the first fragment, with the type, address, price, surface, and description.
     *  The "Previous" button is hidden, as there is no fragment before this one.
     *  We use `.replace` instead of `.add`, as we can have another fragment if the user clicks on
     *  "Cancel" button in [ShowEstateFragment].
     */
    private fun showFirstFragment(estate : Estate?) {
        if (basicDetailsFragment == null)
            basicDetailsFragment = BasicDetailsFragment.newInstance(estate)
        viewModel.setNavigationButtonVisibility(View.GONE, View.VISIBLE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_root, basicDetailsFragment!!)
            .commit()
        viewModel.setFragments()
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
     *  [showFullEstate].
     */
    private fun goToNextOptionalDetails() {
        optionalDetailsFragmentPosition++
        if (optionalDetailsFragmentPosition >= 0)
            viewModel.setButtonPreviousEnabled(true)

        when {
            optionalDetailsFragmentPosition < optionalDetailsFragmentList.size ->
                goToOptionalDetails()
            optionalDetailsFragmentPosition == optionalDetailsFragmentList.size ->
                goToImagesFragment()
            optionalDetailsFragmentPosition == optionalDetailsFragmentList.size + 1 ->
                goToManagingFragment()
            else ->
                showFullEstate(Enums.ShowEstateType.ASK_FOR_CONFIRMATION)
        }
    }

    /**
     *  Decrement [optionalDetailsFragmentPosition]. If it is equals to -1, that means we need to
     *  go back to the [BasicDetailsFragment], and hide "Previous" button.
     *  In the other case, we go to the right [OptionalDetailsFragment].
     */
    private fun goToPreviousOptionalDetails() {
        optionalDetailsFragmentPosition--
        if (optionalDetailsFragmentPosition >= 0 && optionalDetailsFragmentPosition < optionalDetailsFragmentList.size)
            goToOptionalDetails()
        else if (optionalDetailsFragmentPosition == optionalDetailsFragmentList.size)
            goToImagesFragment()
        else if (optionalDetailsFragmentPosition == optionalDetailsFragmentList.size + 1)
            goToManagingFragment()
        else {
            showFirstFragment(estate)
            viewModel.setNavigationButtonVisibility(View.GONE, View.VISIBLE)
        }
    }

    private fun goToImagesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_root,
                AddPicturesFragment.newInstance(picturesList, estate?.id) {
                    picturesList = it
                }
            )
            .commit()
    }

    private fun goToManagingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_root,
                ManagingFragment.newInstance(managingAgents) {
                    managingAgents = it
                }
            )
            .commit()
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
     *  Displays the activity [ShowEstateFragment] to ask if the user is satisfied with the data he
     *  provided. We need to wait for a result, as this [EstateCreationActivity] will save the
     *  [Estate], or allow the user to edit it.
     */
    private fun showFullEstate(type : Enums.ShowEstateType) {
        viewModel.setNavigationButtonVisibility(View.GONE, View.GONE)
        viewModel.setFragments()
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_root,
                ShowEstateFragment.newInstance(estate, type, Configuration.ORIENTATION_UNDEFINED,
                    picturesList, managingAgents,
                    picturesRetrievedCallback = {
                        picturesList = it
                    },
                    managingAgentsRetrievedCallback = {
                        managingAgents = it
                    }
                )
            )
            .commit()
    }

    /**
     *  This function deletes the current [Estate]. It will first delete the images of this [Estate]
     *  in the database (as the images database is using a foreign key on the [Estate] id), then
     *  delete the actual [Estate].
     */
    override fun deleteEstate(estateToDelete: Estate) {
        if (estate != null && estate?.id != null) {
            viewModel.setLoading()
            val databaseManager = DatabaseManager(this)
            databaseManager.deleteImagesForEstate(
                estate!!.id!!,
                onSuccess = {
                    databaseManager.deleteEstate(
                        estate!!.id!!,
                        onSuccess = {
                            finishWithResult(-1, estate)
                        },
                        onFailure = {
                            showFullEstate(Enums.ShowEstateType.SHOW_ESTATE)
                            viewModel.setFragments()
                            Log.e(TAG, "An error occurred while deletion")
                            Toast.makeText(this, getString(R.string.dumb_error), Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                }
            )
        }
    }

    /**
     *  This function is used when the user clicked on "Cancel" button in [ShowEstateFragment]
     *  (or if he presses the back button).
     *  It will remove every [androidx.fragment.app.Fragment] in this [Activity], and display again
     *  [BasicDetailsFragment] to allow the user to edit the data he provided.
     *  We hide "Previous" button and set [optionalDetailsFragmentPosition] to -1.
     *  We also need to reset the [optionalDetailsFragmentList], in order to give it already
     *  provided data.
     *  Finally we call [showFirstFragment].
     */
    override fun handleCompleteEstateCreationCancelled(estateToEdit : Estate) {
        // Remove every fragment in the Activity
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        viewModel.setNavigationButtonVisibility(View.GONE, View.VISIBLE)

        setupOptionalDetailsFragmentList()
        optionalDetailsFragmentPosition = -1
        showFirstFragment(estateToEdit)
    }

    /**
     *  This function is used when the user clicked on "Confirm" button in [ShowEstateFragment].
     *  If the user is editing an existing [Estate] ([isEditing] is true), we call
     *  [saveEstateChanges].
     *  If this is a very new [Estate], we use [saveEstate] instead.
     */
    fun handleCompleteEstateCreationConfirmed() {
        viewModel.setLoading()

        if (isEditing)
            saveEstateChanges(estate)
        else
            saveEstate(estate)
    }

    private fun saveEstate(estateToSave: Estate?) {
        if (estateToSave == null)
            return
        estateToSave.latitude = estatePosition?.latitude
        estateToSave.longitude = estatePosition?.longitude
        DatabaseManager(this).saveEstate(
            estateToSave,
            onSuccess = { insertedId ->
                if (picturesList.isEmpty() && managingAgents.isEmpty())
                    finishWithResult(insertedId, estateToSave)
                else {
                    saveManagingAgents(
                        insertedId,
                        success = {
                            if (picturesList.isNotEmpty())
                                saveImages(insertedId, estateToSave)
                            else
                                finishWithResult(insertedId, estateToSave)
                        }
                    )
                }
            },
            onFailure = {
                Toast.makeText(this, R.string.dumb_error, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun saveEstateChanges(estateToSave: Estate?) {
        if (estateToSave == null)
            return
        DatabaseManager(this).updateEstate(
            estateToSave,
            onSuccess = {
                if (picturesList.isEmpty() && managingAgents.isEmpty())
                    finishWithResult(estateToSave.id!!, estateToSave)
                else {
                    saveManagingAgents(
                        estateToSave.id!!,
                        success = {
                            if (picturesList.isNotEmpty())
                                saveImages(estateToSave.id!!, estateToSave)
                            else
                                finishWithResult(estateToSave.id!!, estateToSave)
                        }
                    )
                }
            },
            onFailure = {
                Log.e(TAG, "An error occurred with the database.")
                Toast.makeText(this, R.string.dumb_error, Toast.LENGTH_LONG).show()
            }
        )
    }

    /**
     *  Saves the images of this [Estate] in the database with the [newEstateId].
     *  First, we delete all the images for this [Estate], to avoid duplicated images, then, we
     *  save every picture in the database.
     *  @param newEstateId ([Int]) - The newly added [Estate] id, that will be kept in the database
     *  along with images.
     *  @param estateToSave ([Estate]) - The saved [Estate]. We need it in order to give it to
     *  [finishWithResult] function.
     */
    private fun saveImages(newEstateId: Int, estateToSave: Estate) {
        val databaseManager = DatabaseManager(this)
        databaseManager.deleteImagesForEstate(
            newEstateId,
            onSuccess = {
                var savedPictures = 0
                for (picture : Bitmap in picturesList) {
                    databaseManager.saveEstateImage(
                        newEstateId,
                        picture,
                        onSuccess = { savedPictures++ },
                        onFailure = { }
                    )
                }
                if (savedPictures == picturesList.size)
                    finishWithResult(newEstateId, estateToSave)
                else {
                    Toast.makeText(this, R.string.dumb_error, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "ERROR")
                }
            }
        )
    }

    private fun saveManagingAgents(newEstateId: Int, success : () -> Unit) {
        val databaseManager = DatabaseManager(this)

        databaseManager.deleteEstateManagers(
            newEstateId,
            success = {
                var savedManagingAgents = 0
                for (agent : Agent in managingAgents) {
                    databaseManager.saveEstateManager(
                        newEstateId,
                        agent,
                        {
                            savedManagingAgents++
                        }, {
                            Log.e(TAG, "Failed to save managing agents for estate $newEstateId")
                            Toast.makeText(this, getString(R.string.dumb_error), Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                }
                if (savedManagingAgents == managingAgents.size)
                    success.invoke()
            }
        )
    }

    private fun finishWithResult(insertedId : Int, estateToSave: Estate?) {
        if (insertedId != -1)
            estateToSave!!.id = insertedId
        setResult(
            Activity.RESULT_OK,
            Intent().apply {
                if (estateToSave != null)
                    putExtra(TAG_ESTATE, estateToSave)
                putExtra(TAG_TO_DELETE, (insertedId == -1))
                putExtra(TAG_NEW_ESTATE, !isEditing)
            }
        )
        finish()
    }

    companion object {

        private const val TAG = "EstateCreationActivity"

        private const val TAG_ESTATE = "estate"
        private const val TAG_NEW_ESTATE = "is_new_estate"
        private const val TAG_TO_DELETE = "to_delete"
        private const val TAG_CURRENT_LOCATION = "current_location"

        fun newInstance(context: Context, estate: Estate?, newEstate: Boolean, position: LatLng?)
        : Intent {
            val intent = Intent(context, EstateCreationActivity::class.java)
            if (estate != null) intent.putExtra(TAG_ESTATE, estate)
            intent.putExtra(TAG_NEW_ESTATE, newEstate)
            if (position != null)
                intent.putExtra(TAG_CURRENT_LOCATION, position)
            return intent
        }

    }
}