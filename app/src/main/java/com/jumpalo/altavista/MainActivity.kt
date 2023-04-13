package com.jumpalo.altavista

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.jumpalo.altavista.activity.ListarDivi
import com.jumpalo.altavista.adapters.rv_cursosAdapter
import com.jumpalo.altavista.databinding.ActivityMainBinding
import com.jumpalo.altavista.databinding.DialogSolicitarUrlBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_solicitar_url.*
import kotlinx.android.synthetic.main.dialog_solicitar_url.view.*
import kotlinx.android.synthetic.main.item_divisiones.view.*
import java.io.File

class MainActivity : AppCompatActivity() {

    var url = ""
    private lateinit var urlFile : File
    private lateinit var db : DBCon
    private lateinit var alerta : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
        urlFile = File(filesDir,"/easyToShotURL.txt")
        verificarPermisos()
        verificarURL()
    }


    override fun onResume() {
        super.onResume()
        if (db.enLinea()) {
            val mlAlumnos = db.getAlumnos()
            val ordenados = ordenar(mlAlumnos)
            rv_cursos.setAdapter(rv_cursosAdapter(this, ordenados))
        } else {
            mostrarErrorDeConexion()
        }
    }

    private fun mostrarErrorDeConexion() {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage("Verifique su conexion")
            setPositiveButton("Aceptar", null)
            create().show()
        }
    }

    fun listarDivi(v : View) {
        val divi = v.tv_divi.text.toString()
        val curso = v.tag.toString()
        startActivity (
            Intent(this, ListarDivi::class.java)
                .putExtra("divi",divi.substring(0,divi.length-1))
                .putExtra("curso",curso)
                .putExtra("url", url),
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )
    }

    private fun ordenar(alums: mlAlu) : mlDiv{
        //inicializar
        val escuela : mlDiv = mutableListOf()
        escuela.add(mutableListOf())
        escuela[0].add(mutableListOf())
        var anteriorCur = alums[0].Curso
        var anteriorDiv = alums[0].Division
        var indCur = 0
        var indDiv = 0
        //armar estructura
        for (al in alums){
            if(al.Curso!=anteriorCur){
                anteriorCur = al.Curso
                indDiv=-1
                escuela.add(mutableListOf())
                indCur++
            }
            if (al.Division!=anteriorDiv){
                anteriorDiv = al.Division
                escuela[indCur].add(mutableListOf())
                indDiv++
            }
            escuela[indCur][indDiv].add(al)
        }
        //ordenar por quien tiene imagen
        for (curso in escuela) curso.sortBy { it[0].hasImg }
        escuela.sortBy { it[0][0].hasImg }

        return escuela
    }

    private fun verificarPermisos(){
        val requestMultiplePermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                permissions.entries.forEach {
                    if(!it.value)
                        Toast.makeText(this, "se requiere ${it.key}", Toast.LENGTH_SHORT).show()
                }
            }
        requestMultiplePermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }
    private fun verificarURL() {
        if (!urlFile.exists()){
            urlFile.createNewFile()
            solicitarURL()
        } else {
            url = urlFile.readText()
            db = DBCon(url)
            if (!db.enLinea()) solicitarURL()
        }
    }
    private fun solicitarURL() {
        val alertaBuilder = AlertDialog.Builder(this)
        alertaBuilder.setView(DialogSolicitarUrlBinding.inflate(layoutInflater).root)
        alerta = alertaBuilder.create()
        alerta.show()
    }
    fun botonGuardar(v : View){
        val nuevaUrl = alerta.ed_url.text.toString()
        urlFile.writeText(nuevaUrl)
        verificarURL()
        alerta.dismiss()
    }

}

typealias mlAlu = MutableList<Alumnos>
typealias mlCur = MutableList<mlAlu>
typealias mlDiv = MutableList<mlCur>