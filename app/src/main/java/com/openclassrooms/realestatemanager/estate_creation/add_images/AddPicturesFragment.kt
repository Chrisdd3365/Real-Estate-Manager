package com.openclassrooms.realestatemanager.estate_creation.add_images

import android.Manifest.permission.*
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddPicturesBinding
import com.openclassrooms.realestatemanager.utils.OnStartDragListener
import com.openclassrooms.realestatemanager.utils.ReorderHelperCallback
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.Utils.getTmpFileUri

// FIXME : Pass as arguments
class AddPicturesFragment(private var picturesList: ArrayList<Bitmap>?,
                          private val editingEstateId: Int?,
                          private val picturesListChanged : (ArrayList<Bitmap>) -> Unit)
    : Fragment(), OnStartDragListener {

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

    private var pictures = ArrayList<Bitmap>()
    private var newUri : Uri? = null
    private var isAskingForPermissions = false

    override fun onCreate(savedInstanceState: Bundle?) {

        permissionRequestLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (!it.containsValue(false)) {
                addPictureButton?.performClick()
            } else {
                Utils.showPermissionsDeniedDialog(
                    requireContext(),
                    getString(R.string.pictures_permissions_not_granted_dialog),
                    goToSettings = {
                        isAskingForPermissions = true
                    },
                    reAskPermissions = {
                        checkAndAskPermissions()
                    }
                )
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
                        val bitmap = Utils.getBitmapFromUri(requireContext(), uri)
                        if (picturesAdapter?.addNewItem(bitmap) == true)
                            notifyEstateCreationActivity()
                        else
                            showBitmapAlreadyInListError()
                    }
                }
            }
        }

        // Gets a picture from the camera
        cameraRequestLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
        ) {
            if (it && newUri != null && newUri != Uri.EMPTY) {
                val bitmap = Utils.getBitmapFromUri(requireContext(), newUri.toString())
                picturesAdapter?.addNewItem(bitmap)
                newUri = null
                notifyEstateCreationActivity()
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

        picturesAdapter = PicturesListAdapter(this) {
            picturesAdapter?.removeItem(it)
            notifyEstateCreationActivity()
        }

        picturesRv?.layoutManager = LinearLayoutManager(context)
        picturesRv?.adapter = picturesAdapter

        val callback : ItemTouchHelper.Callback = ReorderHelperCallback(picturesAdapter!!)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(picturesRv)

        if (picturesList != null && picturesList!!.isNotEmpty()) {
            pictures.addAll(picturesList!!)
            picturesRv?.post { picturesAdapter?.addItems(pictures) }
        } else if (editingEstateId != null) {
            DatabaseManager(requireContext()).getImagesForEstate(
                editingEstateId,
                {
                    picturesListChanged.invoke(it)
                },
                failure = {}
            )
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
            notifyEstateCreationActivity()
        }
    }

    private fun notifyEstateCreationActivity() {
        if (picturesAdapter != null)
            pictures = picturesAdapter!!.getItems()
        picturesListChanged.invoke(pictures)
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
                    newUri = getTmpFileUri(requireContext())
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

    private fun showBitmapAlreadyInListError() {
        Toast.makeText(
            requireContext(),
            getString(R.string.bitmap_already_added_error ),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        if (isAskingForPermissions) {
            isAskingForPermissions = false
            checkAndAskPermissions()
        }
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "AddImagesFragment"

        fun newInstance(picturesUri : ArrayList<Bitmap>?, editingEstateId : Int?,
                        callback : (ArrayList<Bitmap>) -> Unit)
        : AddPicturesFragment {
            return AddPicturesFragment(picturesUri, editingEstateId, callback)
        }
    }

}