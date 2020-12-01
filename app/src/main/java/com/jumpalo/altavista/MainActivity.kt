package com.jumpalo.altavista

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.jumpalo.altavista.activity.ListarDivi
import com.jumpalo.altavista.adapters.rv_cursosAdapter
import com.jumpalo.altavista.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_divisiones.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
        verificarPermisos()
    }

    override fun onResume() {
        super.onResume()
        val mlAlumnos = DBCon().getAlumnos()
        val ordenados = ordenar(mlAlumnos)
        rv_cursos.setAdapter(rv_cursosAdapter(this, ordenados))
    }

    fun listarDivi(v : View) = startActivity (
        Intent(this, ListarDivi::class.java)
            .putExtra("divi",v.tv_divi.text.substring(0,1))
            .putExtra("curso",v.tag.toString().substring(0,1)),
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
    )

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

}

typealias mlAlu = MutableList<Alumnos>
typealias mlCur = MutableList<mlAlu>
typealias mlDiv = MutableList<mlCur>