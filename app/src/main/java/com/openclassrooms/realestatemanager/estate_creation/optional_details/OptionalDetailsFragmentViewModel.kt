package com.openclassrooms.realestatemanager.estate_creation.optional_details

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Enums

class OptionalDetailsFragmentViewModel : ViewModel() {

    var editTextVisibility = ObservableInt(View.GONE)

    var question = ObservableField("")
    var minusText = ObservableField("")
    var plusText = ObservableField("")

    fun setQuestion(question : String) {
        this.question.set(question)
    }

    fun setQuestionType(context : Context?, questionType : Enums.OptionalDetailType) {
        when (questionType) {
            Enums.OptionalDetailType.CLOSED -> {
                minusText.set(context?.getString(R.string.no))
                plusText.set(context?.getString(R.string.yes))
                editTextVisibility.set(View.GONE)
            }
            Enums.OptionalDetailType.COUNT -> {
                minusText.set(context?.getString(R.string.minus))
                plusText.set(context?.getString(R.string.plus))
                editTextVisibility.set(View.VISIBLE)
            }
        }
    }
}