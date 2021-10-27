package com.openclassrooms.realestatemanager.loaning_simulator

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityLoaningSimulatorBinding

class LoaningSimulatorActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = LoaningSimulatorActivityViewModel()

    // Layout variables

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityLoaningSimulatorBinding>(
            this, R.layout.activity_loaning_simulator
        )

        binding.viewModel = viewModel

        // Setup toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Init layout variables

//        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun newInstance(context: Context) : Intent {
            return Intent(context, LoaningSimulatorActivity::class.java)
        }
    }
}