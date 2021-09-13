package com.openclassrooms.realestatemanager.agent_creation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAgentCreationBinding

class AgentCreationActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = AgentCreationActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityAgentCreationBinding>(
            this, R.layout.activity_agent_creation
        )

        binding.viewModel = viewModel
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "AgentCreationActivity"

        fun newInstance(context: Context) : Intent {
            return Intent(context, AgentCreationActivity::class.java)
        }
    }
}