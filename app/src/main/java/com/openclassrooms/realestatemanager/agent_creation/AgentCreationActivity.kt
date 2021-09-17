package com.openclassrooms.realestatemanager.agent_creation

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAgentCreationBinding
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.utils.TextValidator
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.Utils.getTmpFileUri

class AgentCreationActivity : AppCompatActivity() {

    // Helper classes
    private val viewModel = AgentCreationActivityViewModel()
    private var permissionRequestLauncher : ActivityResultLauncher<Array<String>>? = null
    private var galleryRequestLauncher : ActivityResultLauncher<Intent>? = null
    private var cameraRequestLauncher : ActivityResultLauncher<Uri>? = null

    // Layout variables
    private var avatarImageView : ImageView? = null
    private var firstNameEditText : EditText? = null
    private var lastNameEditText : EditText? = null
    private var emailEditText : EditText? = null
    private var phoneNumberEditText : EditText? = null
    private var cancelButton : Button? = null
    private var saveButton : Button? = null

    private var newUri : Uri? = null
    private var agentAvatar : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityAgentCreationBinding>(
            this, R.layout.activity_agent_creation
        )

        binding.viewModel = viewModel

        permissionRequestLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (!it.containsValue(false)) {
                avatarImageView?.performClick()
            }
        }

        // Gets a picture from the gallery
        galleryRequestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null && it.data!!.data != null) {
                    val uri = (it.data!!.data as Uri).toString()
                    if (uri.isNotEmpty()) {
                        val bitmap = Utils.getBitmapFromUri(this, uri)
                        agentAvatar = bitmap
                        setAvatar(bitmap)
                    }
                }
            }
        }

        // Gets a picture from the camera
        cameraRequestLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
        ) {
            if (it && newUri != null && newUri != Uri.EMPTY) {
                val bitmap = Utils.getBitmapFromUri(this, newUri.toString())
                newUri = null
                agentAvatar = bitmap
                setAvatar(bitmap)
            }
        }

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
            if (checkAndAskPermissions()) {
                // The permissions are accepted
                showImagePickerDialog()
            }
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

    private fun saveAgent() {
        DatabaseManager(this).saveAgent(
            Agent().apply {
                firstName = firstNameEditText?.text?.toString()
                lastName = lastNameEditText?.text?.toString()
                email = emailEditText?.text?.toString()
                phoneNumber = phoneNumberEditText?.text?.toString()
                avatar = agentAvatar
            },
            onSuccess = {
                finish()
            },
            onFailure = {
                Toast.makeText(this, getString(R.string.dumb_error), Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun setAvatar(bitmap : Bitmap?) {
        if (bitmap != null) {
            avatarImageView?.scaleType = ImageView.ScaleType.FIT_XY
            avatarImageView?.setImageBitmap(bitmap)
        } else {
            avatarImageView?.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun checkAndAskPermissions() : Boolean {
        val permissionNeeded = ArrayList<String>()

        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        )

        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED)
            permissionNeeded.add(Manifest.permission.CAMERA)

        if (permissionNeeded.isEmpty())
            return true

        permissionRequestLauncher?.launch(permissionNeeded.toTypedArray())
        return false
    }

    private fun showImagePickerDialog() {
        val optionsMenu = arrayOf<CharSequence>(
            getString(R.string.new_picture),
            getString(R.string.pick_from_gallery),
            getString(R.string.button_remove_picture),
            getString(R.string.button_cancel)
        )
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(R.string.add_picture)
        alertDialogBuilder.setItems(
            optionsMenu
        ) { dialog, which ->
            when {
                optionsMenu[which] == getString(R.string.new_picture) -> {
                    newUri = getTmpFileUri(this)
                    cameraRequestLauncher?.launch(newUri)
                }
                optionsMenu[which] == getString(R.string.pick_from_gallery) -> {
                    galleryRequestLauncher?.launch(
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    )
                }
                optionsMenu[which] == getString(R.string.button_remove_picture) -> {
                    agentAvatar = null
                    setAvatar(null)
                }
                else -> {
                    dialog.dismiss()
                }
            }
        }

        alertDialogBuilder.show()
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "AgentCreationActivity"

        private const val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

        fun newInstance(context: Context) : Intent {
            return Intent(context, AgentCreationActivity::class.java)
        }
    }
}