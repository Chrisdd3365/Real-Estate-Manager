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

class OptionalDetailsFragment(private var question: String,
                              private val callback : (Any) -> Unit) : Fragment() {

    // Helper classes
    private val viewModel = OptionalDetailsFragmentViewModel()

    // Layout variables
    private var countEditText : EditText? = null
    private var minusTextView : TextView? = null
    private var plusTextView : TextView? = null

    private var countValue = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = DataBindingUtil.inflate<FragmentOptionalDetailsBinding>(
            LayoutInflater.from(context),
            R.layout.fragment_optional_details,
            container,
            false
        )

        binding.viewModel = viewModel

        // Init layout variables
        countEditText = binding.countEditText
        minusTextView = binding.minusTextView
        plusTextView = binding.plusTextView

        minusTextView?.setOnClickListener {
            if (countValue > 0) {
                countValue--
                updateCountText()
            }
        }
        plusTextView?.setOnClickListener {
            countValue++
            updateCountText()
        }

        viewModel.setQuestion(question)
        countEditText?.setText(countValue.toString())

        return binding.root
    }

    private fun updateCountText() {
        countEditText?.setText(countValue.toString())
        callback.invoke(countValue)
    }

    companion object {
        fun newInstance(question : String, callback: (Any) -> Unit) : OptionalDetailsFragment {
            return OptionalDetailsFragment(question, callback)
        }
    }
}