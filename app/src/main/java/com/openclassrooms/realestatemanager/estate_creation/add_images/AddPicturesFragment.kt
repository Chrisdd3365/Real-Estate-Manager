package com.openclassrooms.realestatemanager.estate_creation.add_images

import android.Manifest.permission.*
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddPicturesBinding
import com.openclassrooms.realestatemanager.utils.OnStartDragListener
import com.openclassrooms.realestatemanager.utils.ReorderHelperCallback
import java.io.File

class AddPicturesFragment(private var picturesUri: ArrayList<String>?) : Fragment(), OnStartDragListener {

    // Helper classes
    private val viewModel = AddPicturesFragmentViewModel()
    private var permissionRequestLauncher : ActivityResultLauncher<Array<String>>? = null
    private var galleryRequestLauncher : ActivityResultLauncher<Intent>? = null
    private var cameraRequestLauncher : ActivityResultLauncher<Uri>? = null
    private var picturesAdapter : PicturesListAdapter? = null
    private var itemTouchHelper : ItemTouchHelper? = null

    // Layout variables
    private var addPictureButton : Button? = null
    private var picturesRv : RecyclerView? = null

    private var pictures = ArrayList<String>()
    private var newUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        permissionRequestLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (!it.containsValue(false)) {
                addPictureButton?.performClick()
            }
        }

        galleryRequestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null && it.data!!.data != null) {
                    // From gallery
                    val newItem = (it.data!!.data as Uri).toString()
                    if (newItem.isNotBlank()) {
                        picturesAdapter?.addNewItem(newItem)
                        pictures.add(newItem)
                    }
                }

            }
        }

        cameraRequestLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
        ) {
            if (it && newUri != null && newUri != Uri.EMPTY) {
                picturesAdapter?.addNewItem(newUri.toString())
                pictures.add(newUri.toString())
                newUri = null
            }
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = DataBindingUtil.inflate<FragmentAddPicturesBinding>(
            inflater, R.layout.fragment_add_pictures, container, false
        )

        binding.viewModel = viewModel

        // Setup layout variables
        addPictureButton = binding.addPictureButton
        picturesRv = binding.picturesRv

        picturesAdapter = PicturesListAdapter(this, requireContext())

        picturesRv?.layoutManager = LinearLayoutManager(context)
        picturesRv?.adapter = picturesAdapter

        val callback : ItemTouchHelper.Callback = ReorderHelperCallback(picturesAdapter!!)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(picturesRv)

        if (picturesUri != null) {
            pictures.addAll(picturesUri!!)
            picturesRv?.post { picturesAdapter?.addItems(pictures) }
        }

        addPictureButton?.setOnClickListener {
            if (checkAndAskPermissions()) {
                // The permissions are accepted
                showImagePickerDialog()
            }
        }

        return binding.root
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        viewHolder?.let {
            itemTouchHelper?.startDrag(it)
        }
    }

    private fun checkAndAskPermissions() : Boolean {
        val permissionNeeded = ArrayList<String>()

        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(), WRITE_EXTERNAL_STORAGE
        )
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(), READ_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(), CAMERA
        )

        if (writeExternalStoragePermission != PERMISSION_GRANTED)
            permissionNeeded.add(WRITE_EXTERNAL_STORAGE)
        if (readExternalStoragePermission != PERMISSION_GRANTED)
            permissionNeeded.add(READ_EXTERNAL_STORAGE)
        if (cameraPermission != PERMISSION_GRANTED)
            permissionNeeded.add(CAMERA)

        if (permissionNeeded.isEmpty())
            return true

        permissionRequestLauncher?.launch(permissionNeeded.toTypedArray())
        return false
    }

    private fun showImagePickerDialog() {
        val optionsMenu = arrayOf<CharSequence>(
            getString(R.string.new_picture),
            getString(R.string.pick_from_gallery),
            getString(R.string.button_cancel)
        )
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle(R.string.add_picture)
        alertDialogBuilder.setItems(
            optionsMenu
        ) { dialog, which ->
            when {
                optionsMenu[which] == getString(R.string.new_picture) -> {
                    newUri = getTmpFileUri()
                    cameraRequestLauncher?.launch(newUri)
                }
                optionsMenu[which] == getString(R.string.pick_from_gallery) -> {
                    galleryRequestLauncher?.launch(
                        Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI)
                    )
                }
                else -> {
                    dialog.dismiss()
                }
            }
        }

        alertDialogBuilder.show()
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile(
            "tmp_image_file",
            ".png",
            requireContext().cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider", tmpFile
        )
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "AddImagesFragment"

        fun newInstance(picturesUri : ArrayList<String>?) : AddPicturesFragment {
            return AddPicturesFragment(picturesUri)
        }
    }

}