package com.openclassrooms.realestatemanager

import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.model.Estate

open class BaseActivity : AppCompatActivity() {

    protected var estate : Estate? = null

    /**
     *  This function deletes the current [Estate]. It will first delete the images of this [Estate]
     *  in the database (as the images database is using a foreign key on the [Estate] id), then
     *  delete the actual [Estate].
     */
    open fun deleteEstate(estateToDelete: Estate) {}

    open fun handleCompleteEstateCreationCancelled(estateToEdit: Estate) {}

    companion object {
        @Suppress("unused")
        private const val TAG = "BaseActivity"
    }
}