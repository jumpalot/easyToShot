package com.jumpalo.altavista.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jumpalo.altavista.Alumnos
import com.jumpalo.altavista.DBCon
import com.jumpalo.altavista.R
import com.jumpalo.altavista.databinding.SheetEditarBinding
import com.theartofdev.edmodo.cropper.CropImage.*
import kotlinx.android.synthetic.main.activity_descripcion.*
import org.apache.http.message.BasicNameValuePair
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Descripcion : AppCompatActivity() {
    private lateinit var photoURI : Uri
    private val codCaptura = 1
    private lateinit var alu : Alumnos
    private val db = DBCon()
    private var foto : Bitmap? = null
    private lateinit var sheet : BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descripcion)

        alu = Alumnos().also {
            it.fromString(
                intent.extras?.getString("alumno") ?: "0"
            )
        }
        foto = db.getFoto(alu.Carnet)

        viewBind()

        sheet = BottomSheetDialog(this).also {
            it.setContentView(
                ediBind(SheetEditarBinding.inflate(layoutInflater))
            )
        }
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
                        "com.jumpalo.altavista.provider", photoFile
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
                im_fail.visibility  = View.INVISIBLE
                im_ok.visibility = View.INVISIBLE
                Thread {
                    var cont = 0
                    while (cont<10) {
                        if(db.sendFoto(bitmap, alu.Carnet)) break
                        cont++
                    }
                    runOnUiThread{
                        progressBar.visibility = View.INVISIBLE
                        if(cont==5) im_fail.visibility  = View.VISIBLE
                        else im_ok.visibility = View.VISIBLE
                    }
                }.start()
                imageView.setImageURI(result.uri)
                contentResolver.delete(photoURI, null, null)
            } else if (resultCode == CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                println(result.error)
            }
        }
    }

    fun editar(v: View) = sheet.show()

    private fun viewBind(){
        tv_carnet.text = alu.Carnet
        tv_dni.text = alu.Dni
        tv_nombre.text = alu.Apellido+" "+alu.Nombre
        tv_turno.text = alu.Turno
        progressBar.visibility = View.VISIBLE
        imageView.setImageBitmap(foto)
        progressBar.visibility = View.INVISIBLE
    }
    private fun ediBind(editador: SheetEditarBinding) : View {
        editador.spTurno.adapter = ArrayAdapter.createFromResource(
    this, R.array.turnos, android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        editador.spTurno.setSelection(
            when(alu.Turno){
                "Mañana" -> 0
                "Tarde" -> 1
                "Vespertino" -> 2
                else -> 0
            }
        )
        editador.edDni.setText(alu.Dni)
        editador.edNom.setText(alu.Nombre)
        editador.edApe.setText(alu.Apellido)
        editador.edCurso.setText(alu.Curso)
        editador.edDivision.setText(alu.Division)

        editador.fabUpdate.setOnClickListener {
            db.updateAlumno( mutableListOf(
                BasicNameValuePair("dni", editador.edDni.text.toString()),
                BasicNameValuePair("carnet", alu.Carnet),
                BasicNameValuePair("nombre", editador.edNom.text.toString()),
                BasicNameValuePair("apellido", editador.edApe.text.toString()),
                BasicNameValuePair("curso", editador.edCurso.text.toString()),
                BasicNameValuePair("division", editador.edDivision.text.toString()),
                BasicNameValuePair("turno", editador.spTurno.selectedItemPosition.toString())
            ))
            alu = Alumnos(
                alu.Carnet,
                editador.edNom.text.toString(),
                editador.edApe.text.toString(),
                editador.edCurso.text.toString(),
                editador.edDivision.text.toString(),
                editador.spTurno.selectedItem.toString(),
                editador.edDni.text.toString()
            )
            viewBind()
            sheet.dismiss()
        }
        return editador.root
    }
}