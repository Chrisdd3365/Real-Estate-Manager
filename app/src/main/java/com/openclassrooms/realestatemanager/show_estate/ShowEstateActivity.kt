package com.openclassrooms.realestatemanager.show_estate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityShowEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums

class ShowEstateActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = ShowEstateActivityViewModel()

    // Layout variables
    private var typeIcon : ImageView? = null
    private var leftButton : Button? = null
    private var rightButton : Button? = null

    private var type : Enums.ShowEstateType? = null
    private var estate : Estate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = intent.extras?.get(TAG_TYPE) as Enums.ShowEstateType
        estate = intent.extras?.get(TAG_ESTATE) as Estate

        if (estate == null) finish()

        val binding = DataBindingUtil.setContentView<ActivityShowEstateBinding>(
            this, R.layout.activity_show_estate
        )

        binding.viewModel = viewModel

        typeIcon = binding.typeIcon
        leftButton = binding.leftButton
        rightButton = binding.rightButton

        viewModel.setButtons(this, type!!)
        viewModel.setData(this, estate!!)

        setImageType()

        setButtonsBehaviors()
    }

    private fun setImageType() {
        when (estate!!.typeIndex) {
            0 -> { typeIcon?.setImageResource(R.drawable.ic_flat) }
            1 -> { typeIcon?.setImageResource(R.drawable.ic_townhouse) }
            2 -> { typeIcon?.setImageResource(R.drawable.ic_penthouse) }
            3 -> { typeIcon?.setImageResource(R.drawable.ic_house) }
            4 -> { typeIcon?.setImageResource(R.drawable.ic_duplex) }
        }
    }

    private fun setButtonsBehaviors() {
        leftButton?.setOnClickListener {
            when (type!!) {
                Enums.ShowEstateType.ASK_FOR_CONFIRMATION -> {
                    // TODO : Cancel
                }
                Enums.ShowEstateType.SHOW_ESTATE -> {
                    // TODO : Delete this estate
                }
            }
        }
        rightButton?.setOnClickListener {
            when (type!!) {
                Enums.ShowEstateType.ASK_FOR_CONFIRMATION -> {
                    // TODO : Save this estate
                }
                Enums.ShowEstateType.SHOW_ESTATE -> {
                    // TODO : Edit this estate
                }
            }
        }
    }

    companion object {

        private const val TAG = "ShowEstateActivity"

        private const val TAG_ESTATE = "estate"
        private const val TAG_TYPE = "type"

        fun newInstance(context : Context?, estate : Estate, type : Enums.ShowEstateType) : Intent {
            val intent = Intent(context, ShowEstateActivity::class.java)
            intent.putExtra(TAG_TYPE, type)
            intent.putExtra(TAG_ESTATE, estate)
            return intent
        }

    }
}