package com.openclassrooms.realestatemanager.estate_creation.optional_details

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class OptionalDetailsFragmentViewModel : ViewModel() {

    var question = ObservableField("")

    fun setQuestion(question : String) {
        this.question.set(question)
    }
}