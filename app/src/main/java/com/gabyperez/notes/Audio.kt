package com.gabyperez.notes

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.gabyperez.notes.databinding.FragmentAudioBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

private const val LOG_TAG = "AudioRecordTest"
class Audio : Fragment(){

    lateinit var requestPermissionLauncher:ActivityResultLauncher<String>
    private var mStartRecording: Boolean = true
    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    lateinit var miContexto: Context

    // Requesting permission to RECORD_AUDIO
    override fun onAttach(context: Context) {
        super.onAttach(context)
        miContexto = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAudioBinding.inflate(layoutInflater)
        iniPerm()
        binding.btnStart.setOnClickListener {
            grabar()
            onRecord(mStartRecording)
            mStartRecording = !mStartRecording
        }

        binding.btnStop.setOnClickListener {
            onPlay(mStartPlaying)
            mStartPlaying = !mStartPlaying
        }
       binding.btnGuardar.setOnClickListener{
           it.findNavController().navigate(R.id.action_audio_to_createTask)
       }
        return binding.root
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    var mStartPlaying = true


    private fun onRecord(start: Boolean) = if (start) {
        iniciarGraabacion()
    } else {
        stopRecording()
    }

    private fun iniPerm(){
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
    }

    private fun grabar() {
        revisarPermisos()
    }

    private fun revisarPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                miContexto,
                "android.permission.RECORD_AUDIO"
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") -> {
                MaterialAlertDialogBuilder( miContexto
                )
                    .setTitle("Title")
                    .setMessage("Debes dar perimso para grabar audios")
                    .setNegativeButton("Cancel") { dialog, which ->
                    }
                    .setPositiveButton("OK") { dialog, which ->

                        requestPermissions(
                            arrayOf("android.permission.RECORD_AUDIO",
                                "android.permission.WRITE_EXTERNAL_STORAGE"),
                            1001)
                    }
                    .show()
            }
            else -> {
                /*requestPermissionLauncher.launch(
                    "android.permission.RECORD_AUDIO")*/
                requestPermissions(
                    arrayOf("android.permission.RECORD_AUDIO",
                        "android.permission.WRITE_EXTERNAL_STORAGE"),
                    1001)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray

    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED
                            )) {
                    iniciarGraabacion()
                } else {

                }
                return
            }
            else -> {
            }
        }
    }

    private fun iniciarGraabacion() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            createAudioFile()
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    @Throws(IOException::class)
    fun createAudioFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        /*val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)*/
        val storageDir: File? = activity?.getExternalFilesDir(null)
        //val storageDir: File? = filesDir
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".mp3",
            storageDir
        ).apply {
            fileName = absolutePath
        }
    }
}