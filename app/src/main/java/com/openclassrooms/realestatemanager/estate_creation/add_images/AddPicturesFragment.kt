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
import android.webkit.MimeTypeMap
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

class AddPicturesFragment(private var picturesList: ArrayList<String>?,
                          private val editingEstateId: Int?,
                          private val picturesListChanged : (ArrayList<String>) -> Unit)
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

    private var pictures = ArrayList<String>()
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
                        val contentResolver = requireContext().contentResolver
                        val mimeType = MimeTypeMap.getSingleton()
                        val type = mimeType.getExtensionFromMimeType(contentResolver.getType(Uri.parse(uri)))
                        if (ALLOWED_MIMETYPES.contains(type)) {
                            pictures.add(uri)
                            setFlagsOnUri(it.data, uri)
                            val bitmap = Utils.getBitmapFromUri(requireContext(), uri)
                            if (picturesAdapter?.addNewItem(bitmap) == true)
                                notifyEstateCreationActivity()
                            else
                                showBitmapAlreadyInListError()
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.wrong_type_error),
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        // Gets a picture from the camera
        cameraRequestLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
        ) {
            if (it && newUri != null && newUri != Uri.EMPTY) {
                pictures.add(newUri.toString())
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

        picturesAdapter = PicturesListAdapter(this, {
            val index = picturesAdapter?.removeItem(it)
            if (index != null && index != -1)
                pictures.removeAt(index)
            notifyEstateCreationActivity()
        }, { from : Int, to : Int ->
            val itemToMove = pictures[from]
            pictures.removeAt(from)
            pictures.add(to, itemToMove)
            notifyEstateCreationActivity()
        })

        picturesRv?.layoutManager = LinearLayoutManager(context)
        picturesRv?.adapter = picturesAdapter

        val callback : ItemTouchHelper.Callback = ReorderHelperCallback(picturesAdapter!!)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(picturesRv)

        if (picturesList != null && picturesList!!.isNotEmpty()) {
            pictures.addAll(picturesList!!)
            pictures.forEach {
                val bitmap = Utils.getBitmapFromUri(requireContext(), it)
                picturesRv?.post { picturesAdapter?.addNewItem(bitmap) }
            }
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
                    val mimeTypes = ArrayList<String>()
                    mimeTypes.add("image/*")
                    val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT, EXTERNAL_CONTENT_URI)
                    galleryIntent.type = mimeTypes[0]
                    galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    galleryRequestLauncher?.launch(galleryIntent)
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

    /**
     *  On API >= 19, we only have temporal access to pictures in the gallery. To prevent a
     *  SecurityException to happen when retrieving this picture in another [Activity] or process,
     *  we need to change flags on the uri.
     *  @see <a href="https://stackoverflow.com/a/40403137/8286029">StackOverflow answer</a>
     *  @see <a href="https://stackoverflow.com/a/34665204/8286029">StackOverflow answer</a>
     *  @see <a href="https://developer.android.com/guide/topics/providers/document-provider#permissions">Google Android Documentation</a>
     */
    private fun setFlagsOnUri(intent : Intent?, uri : String) {
        val originalFlags = intent?.flags
        val takeFlags: Int = originalFlags?.and(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            ?: Intent.FLAG_GRANT_READ_URI_PERMISSION
        requireContext().contentResolver.takePersistableUriPermission(Uri.parse(uri), takeFlags)
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

        private val ALLOWED_MIMETYPES = listOf("jpeg", "jpg", "png")

        fun newInstance(picturesUri : ArrayList<String>?, editingEstateId : Int?,
                        callback : (ArrayList<String>) -> Unit)
        : AddPicturesFragment {
            return AddPicturesFragment(picturesUri, editingEstateId, callback)
        }
    }

}