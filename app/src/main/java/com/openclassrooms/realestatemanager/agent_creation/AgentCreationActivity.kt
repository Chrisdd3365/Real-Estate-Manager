package com.openclassrooms.realestatemanager.agent_creation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAgentCreationBinding
import com.openclassrooms.realestatemanager.utils.TextValidator

class AgentCreationActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = AgentCreationActivityViewModel()

    // Layout variables
    private var avatarImageView : ImageView? = null
    private var firstNameEditText : EditText? = null
    private var lastNameEditText : EditText? = null
    private var emailEditText : EditText? = null
    private var phoneNumberEditText : EditText? = null
    private var cancelButton : Button? = null
    private var saveButton : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityAgentCreationBinding>(
            this, R.layout.activity_agent_creation
        )

        binding.viewModel = viewModel

        // Init layout variables
        avatarImageView = binding.avatarImageView
        firstNameEditText = binding.firstNameEditText
        lastNameEditText = binding.lastNameEditText
        emailEditText = binding.emailEditText
        phoneNumberEditText = binding.phoneNumberEditText
        cancelButton = binding.cancelButton
        saveButton = binding.saveButton

        firstNameEditText?.addTextChangedListener(
            object : TextValidator(firstNameEditText) {
                override fun validate(editText: EditText?, text: String?) {
                    if (text.isNullOrBlank())
                        firstNameEditText?.error = getString(R.string.mandatory_field)
                    else {
                        firstNameEditText?.error = null
                        checkIfAllFieldsAreFilled()
                    }
                }
            }
        )

        lastNameEditText?.addTextChangedListener(
            object : TextValidator(lastNameEditText) {
                override fun validate(editText: EditText?, text: String?) {
                    if (text.isNullOrBlank())
                        lastNameEditText?.error = getString(R.string.mandatory_field)
                    else {
                        lastNameEditText?.error = null
                        checkIfAllFieldsAreFilled()
                    }
                }
            }
        )

        emailEditText?.addTextChangedListener(
            object : TextValidator(emailEditText) {
                override fun validate(editText: EditText?, text: String?) {
                    when {
                        text.isNullOrBlank()
                            -> emailEditText?.error = getString(R.string.mandatory_field)

                        !EMAIL_REGEX.toRegex().matches(emailEditText?.text.toString())
                            -> emailEditText?.error = getString(R.string.incorrect_format)

                        else -> {
                                emailEditText?.error = null
                                checkIfAllFieldsAreFilled()
                        }
                    }
                }
            }
        )

        phoneNumberEditText?.addTextChangedListener(
            object : TextValidator(phoneNumberEditText) {
                override fun validate(editText: EditText?, text: String?) {
                    when {
                        text.isNullOrBlank()
                            -> phoneNumberEditText?.error = getString(R.string.mandatory_field)

                        !PhoneNumberUtils.isGlobalPhoneNumber(phoneNumberEditText?.text.toString())
                            -> phoneNumberEditText?.error = getString(R.string.incorrect_format)

                        else -> {
                            phoneNumberEditText?.error = null
                            checkIfAllFieldsAreFilled()
                        }
                    }
                }
            }
        )

        avatarImageView?.setOnClickListener {
            // TODO : Get profile picture
            // TODO : Set tint back to null
        }

        saveButton?.setOnClickListener {
            saveAgent()
        }

        cancelButton?.setOnClickListener {
            finish()
        }
    }

    private fun checkIfAllFieldsAreFilled() {
        viewModel.setSaveButtonAbility(
            !firstNameEditText?.text.isNullOrBlank() && !lastNameEditText?.text.isNullOrBlank()
                    && !emailEditText?.text.isNullOrBlank() && emailEditText?.error == null
                    && !phoneNumberEditText?.text.isNullOrBlank()
                    && phoneNumberEditText?.error == null
        )
    }

    private fun saveAgent() {}

    companion object {

        @Suppress("unused")
        private const val TAG = "AgentCreationActivity"

        private const val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

        fun newInstance(context: Context) : Intent {
            return Intent(context, AgentCreationActivity::class.java)
        }
    }
}