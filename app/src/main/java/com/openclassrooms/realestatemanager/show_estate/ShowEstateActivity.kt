package com.openclassrooms.realestatemanager.show_estate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityShowEstateBinding
import com.openclassrooms.realestatemanager.model.Estate

class ShowEstateActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = ShowEstateActivityViewModel()

    private var estate : Estate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        estate = intent.extras?.get(TAG_ESTATE) as Estate

        if (estate == null) finish()

        val binding = DataBindingUtil.setContentView<ActivityShowEstateBinding>(
            this, R.layout.activity_show_estate
        )

        binding.viewModel = viewModel
    }

    companion object {

        private const val TAG = "ShowEstateActivity"

        private const val TAG_ESTATE = "estate"

        fun newInstance(context : Context?, estate : Estate) : Intent {
            val intent = Intent(context, ShowEstateActivity::class.java)
            intent.putExtra(TAG_ESTATE, estate)
            return intent
        }

    }
}