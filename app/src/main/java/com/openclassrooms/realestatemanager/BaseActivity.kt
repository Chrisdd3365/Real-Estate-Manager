package com.openclassrooms.realestatemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.model.Estate

open class BaseActivity : AppCompatActivity() {

    protected var estate : Estate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

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