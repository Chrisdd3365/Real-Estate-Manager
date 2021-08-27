package com.openclassrooms.realestatemanager.estate_creation.optional_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentOptionalDetailsBinding
import com.openclassrooms.realestatemanager.utils.Enums

class OptionalDetailsFragment(private var question: String,
                              private var questionType : Enums.OptionalDetailType,
                              private val callback : (Any) -> Unit) : Fragment() {

    // Helper classes
    private val viewModel = OptionalDetailsFragmentViewModel()

    // Layout variables
    private var countEditText : EditText? = null
    private var minusTextView : TextView? = null
    private var plusTextView : TextView? = null

    private var countValue = 0
    private var closedAnswer = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = DataBindingUtil.inflate<FragmentOptionalDetailsBinding>(
            LayoutInflater.from(context),
            R.layout.fragment_optional_details,
            container,
            false
        )

        binding.viewModel = viewModel

        // Setup question type
        viewModel.setQuestionType(context, questionType)

        // Init layout variables
        countEditText = binding.countEditText
        minusTextView = binding.minusTextView
        plusTextView = binding.plusTextView

        minusTextView?.setOnClickListener {
            when (questionType) {
                Enums.OptionalDetailType.CLOSED -> closedAnswer = false
                Enums.OptionalDetailType.COUNT -> if (countValue > 0) countValue--
            }
            updateDetailAnswer()
        }
        plusTextView?.setOnClickListener {
            when (questionType) {
                Enums.OptionalDetailType.CLOSED -> closedAnswer = true
                Enums.OptionalDetailType.COUNT -> countValue++
            }
            updateDetailAnswer()
        }

        viewModel.setQuestion(question)
        countEditText?.setText(countValue.toString())

        return binding.root
    }

    private fun updateDetailAnswer() {
        when (questionType) {
            Enums.OptionalDetailType.CLOSED -> {
                callback.invoke(closedAnswer)
            }
            Enums.OptionalDetailType.COUNT -> {
                countEditText?.setText(countValue.toString())
                callback.invoke(countValue)
            }
        }
    }

    companion object {
        fun newInstance(
            question : String,
            questionType : Enums.OptionalDetailType,
            callback: (Any) -> Unit
        ) : OptionalDetailsFragment {
            return OptionalDetailsFragment(question, questionType, callback)
        }
    }
}