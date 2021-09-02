package com.openclassrooms.realestatemanager.estate_creation.optional_details

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentOptionalDetailsBinding
import com.openclassrooms.realestatemanager.utils.Enums


class OptionalDetailsFragment(private var question: String,
                              private var questionType : Enums.OptionalDetailType,
                              private var defaultValue: Any?,
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

        if (defaultValue != null) {
            when (questionType) {
                Enums.OptionalDetailType.CLOSED -> {
                    if (defaultValue as Boolean) {
                        setBackgroundShapeColor(plusTextView, android.R.color.holo_green_light)
                        setBackgroundShapeColor(minusTextView, android.R.color.transparent)
                    }
                    else if (!(defaultValue as Boolean)) {
                        setBackgroundShapeColor(minusTextView, android.R.color.holo_red_light)
                        setBackgroundShapeColor(plusTextView, android.R.color.transparent)
                    }
                }
                Enums.OptionalDetailType.COUNT -> {
                    countValue = defaultValue as Int
                }
            }
        }

        minusTextView?.setOnClickListener {
            when (questionType) {
                Enums.OptionalDetailType.CLOSED -> {
                    closedAnswer = false
                    setBackgroundShapeColor(minusTextView, android.R.color.holo_red_light)
                    setBackgroundShapeColor(plusTextView, android.R.color.transparent)
                }
                Enums.OptionalDetailType.COUNT -> if (countValue > 0) countValue--
            }
            updateDetailAnswer()
        }
        plusTextView?.setOnClickListener {
            when (questionType) {
                Enums.OptionalDetailType.CLOSED -> {
                    closedAnswer = true
                    setBackgroundShapeColor(plusTextView, android.R.color.holo_green_light)
                    setBackgroundShapeColor(minusTextView, android.R.color.transparent)
                }
                Enums.OptionalDetailType.COUNT -> countValue++
            }
            updateDetailAnswer()
        }

        viewModel.setQuestion(question)
        countEditText?.setText(countValue.toString())


        // By default, we need to invoke the callback in order to set the value : If the user
        //  clicked on "Cancel" button on ShowEstateActivity, and now clicks on "Next" button
        //  without changing any value, we need to store a variable, as the list of
        //  OptionalDetailsFragments is reset each time the user clicks on "Cancel".
        if (questionType == Enums.OptionalDetailType.COUNT) {
            callback.invoke(0)
            if (defaultValue != null)
                callback.invoke(defaultValue as Int)
            else
                callback.invoke(0)
        } else if (questionType == Enums.OptionalDetailType.CLOSED && defaultValue != null)
            callback.invoke(defaultValue as Boolean)

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

    fun setDefaultValue(defaultValue: Any?) {
        this.defaultValue = defaultValue
    }

    private fun setBackgroundShapeColor(textView : TextView?, colorId : Int) {
        when (val background: Drawable? = textView?.background) {
            is ShapeDrawable -> {
                background.paint.color =
                    ContextCompat.getColor(requireContext(), colorId)
            }
            is GradientDrawable -> {
                background.setColor(
                    ContextCompat.getColor(
                        requireContext(),
                        colorId
                    )
                )
            }
            is ColorDrawable -> {
                background.color =
                    ContextCompat.getColor(requireContext(), colorId)
            }
        }
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "OptionalDetailsFragment"

        fun newInstance(
            question : String,
            questionType : Enums.OptionalDetailType,
            defaultValue : Any?,
            callback: (Any) -> Unit
        ) : OptionalDetailsFragment {
            return OptionalDetailsFragment(question, questionType, defaultValue, callback)
        }
    }
}