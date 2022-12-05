package com.gabyperez.notes

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.databinding.FragmentPhotoBinding
import com.gabyperez.notes.model.Multimedia
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoFragment : Fragment() {
    private lateinit var binding: FragmentPhotoBinding
    private var photoURI: Uri = "".toUri()
    private lateinit var miContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        miContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(layoutInflater)

        binding.selectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 111)
        }

        binding.takePhoto.setOnClickListener {
            validarPermisos()
        }

        binding.savePhoto.setOnClickListener {
            val file = Multimedia (
                arguments?.getString("id")!!.toInt(),
                "photo",
                photoURI.toString(),
                binding.description.text.toString()
            )
            //Insert
            NoteDatabase.getDatabase(requireActivity().applicationContext).MultimediaDao().insert(file)

            binding.savePhoto.visibility = View.INVISIBLE
            binding.takePhoto.visibility = View.INVISIBLE
            binding.selectPhoto.visibility = View.INVISIBLE
            binding.description.isEnabled = false
        }

        return binding.root
    }

    private lateinit var currentPhotoPath: String
    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private val REQUEST_TAKE_PHOTO = 1
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            activity?.let { it ->
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoURI= FileProvider.getUriForFile(
                            miContext,
                            "com.gabyperez.notes.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            binding.imageView.setImageURI(photoURI)
        } else if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            miContext.grantUriPermission(
                miContext.packageName,
                photoURI,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            data?.data?.also { uri ->

                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                photoURI = uri
                miContext.contentResolver.takePersistableUriPermission(photoURI, takeFlags)
            }
            binding.imageView.setImageURI(photoURI)
        }
    }

    private fun validarPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                miContext,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        miContext,
                        "android.permission.CAMERA"
                    ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                dispatchTakePictureIntent()
            }
            shouldShowRequestPermissionRationale("android.permission.CAMERA") -> {
                val dialog = AlertDialog.Builder(miContext).apply {
                    setTitle("Acepta permisos, por favor :c")
                    setMessage("Acepta los permisos para poder guardar archivos multimedia en tus tareas y notas")
                        .setNegativeButton("Ok", DialogInterface.OnClickListener {
                                _, _ ->
                        })
                        .setPositiveButton("Solicitar permiso de nuevo",
                            DialogInterface.OnClickListener { _, _ ->
                                requestPermissions(
                                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"),
                                    REQUEST_TAKE_PHOTO)
                            })
                    create()
                }
                dialog.show()
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"),
                    REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    dispatchTakePictureIntent()
                } else {

                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}