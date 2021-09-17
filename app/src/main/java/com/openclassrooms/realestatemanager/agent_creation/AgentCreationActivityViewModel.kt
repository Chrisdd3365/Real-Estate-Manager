package com.openclassrooms.realestatemanager.agent_creation

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class AgentCreationActivityViewModel : ViewModel() {

    val buttonSaveEnabled = ObservableField(false)

    fun setSaveButtonAbility(enable : Boolean) {
        buttonSaveEnabled.set(enable)
    }
}