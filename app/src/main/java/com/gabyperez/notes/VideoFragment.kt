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
import android.widget.MediaController
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.databinding.FragmentVideoBinding
import com.gabyperez.notes.model.Multimedia
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private var videoURI: Uri = "".toUri()
    private lateinit var miContext: Context
    private lateinit var mediaController: MediaController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        miContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(layoutInflater)

        binding.selectVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, 111)
        }

        binding.takeVideo.setOnClickListener {
            validarPermisos()
        }

        binding.saveVideo.setOnClickListener {
            val file = Multimedia (
                arguments?.getString("id")!!.toInt(),
                "video",
                videoURI.toString(),
                binding.description.text.toString()
            )
            //Insert
            NoteDatabase.getDatabase(requireActivity().applicationContext).MultimediaDao().insert(file)

            binding.saveVideo.visibility = View.INVISIBLE
            binding.takeVideo.visibility = View.INVISIBLE
            binding.selectVideo.visibility = View.INVISIBLE
            binding.description.isEnabled = false
        }

        mediaController = MediaController(miContext)
        mediaController.setAnchorView(binding.root)
        binding.videoView .setMediaController(mediaController)

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_TAKE_VIDEO && resultCode == AppCompatActivity.RESULT_OK){
            binding.videoView.setVideoURI(videoURI)
            binding.videoView.start()
            binding.videoView.setOnClickListener {
                mediaController.show()
            }
        } else if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            miContext.grantUriPermission(miContext.packageName,videoURI,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data?.data?.also { uri ->

                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                videoURI=uri
                miContext.contentResolver.takePersistableUriPermission(videoURI, takeFlags)
            }
            binding.videoView.setVideoURI(videoURI)
            binding.videoView.start()
            binding.videoView.setOnClickListener { binding.videoView.start() }

        }
    }

    private lateinit var currentVideoPath: String
    @Throws(IOException::class)
    fun createVideoFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(null)
            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            "VIDEO_${timeStamp}_", /* prefix */
            ".mp4", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentVideoPath = absolutePath
        }
    }

    private val REQUEST_TAKE_VIDEO: Int = 1001
    private fun tomarVideo() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            activity?.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createVideoFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        videoURI= FileProvider.getUriForFile(
                            miContext,
                            "com.gabyperez.notes.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_VIDEO)
                    }
                }
            }
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
                tomarVideo()
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
                                    REQUEST_TAKE_VIDEO)
                            })
                    create()
                }
                dialog.show()
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"),
                    REQUEST_TAKE_VIDEO)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_TAKE_VIDEO -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    tomarVideo()
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