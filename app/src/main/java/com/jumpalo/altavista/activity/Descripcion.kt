package com.jumpalo.altavista.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.jumpalo.altavista.DBCon
import com.jumpalo.altavista.R
import com.theartofdev.edmodo.cropper.BuildConfig
import com.theartofdev.edmodo.cropper.CropImage.*
import kotlinx.android.synthetic.main.activity_descripcion.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.jumpalo.altavista.Alumnos

class Descripcion : AppCompatActivity() {
    private lateinit var photoURI : Uri
    private val codCaptura = 1
    private lateinit var alu : Alumnos
    private val db = DBCon()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descripcion)
        alu = Alumnos(intent.extras?.getString("carnet") ?: "0")
        tv_carnet.text = alu.Carnet
        tv_dni.text = alu.Dni
        tv_nombre.text = alu.Apellido+" "+alu.Nombre
        tv_turno.text = alu.Turno
        progressBar.visibility = View.VISIBLE
        imageView.setImageBitmap(db.getFoto(alu.Carnet))
        progressBar.visibility = View.INVISIBLE
    }

    fun capturar(v: View) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Crea el File
            val photoFile = try {createImageFile()} catch (ex: IOException) {null}
            if (photoFile != null) {
                photoURI =
                    FileProvider.getUriForFile(
                        Objects.requireNonNull(applicationContext),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile
                    )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, codCaptura)
            }
        }
    }

    private fun createImageFile() : File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == codCaptura && resultCode == RESULT_OK) {
            activity(photoURI)
                .setAspectRatio(4, 4)
                .setFixAspectRatio(true)
                .setMinCropResultSize(720, 720)
                .start(this)
        }
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                progressBar.visibility = View.VISIBLE
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, result.uri)
                db.sendFoto(bitmap, alu.Carnet)
                imageView.setImageURI(result.uri)
                progressBar.visibility = View.INVISIBLE
                contentResolver.delete(photoURI, null, null)
            } else if (resultCode == CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                println(result.error)
            }
        }
    }
}