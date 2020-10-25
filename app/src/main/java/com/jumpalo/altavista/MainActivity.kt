package com.jumpalo.altavista

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
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

    private fun ordenar(alums: mlAlu) : mlDiv {
        alums.sortBy { it.Curso }                                                       /*por cursos*/
        val aux = Array<mlAlu>(alums[alums.lastIndex].Curso.toInt()){ mutableListOf() }
        val cursos : mlCur = mutableListOf()
        for(al in alums) aux[al.Curso.toInt()-1].add(al)
        for(indi in aux.indices)
            if (aux[indi].isNotEmpty())
                cursos.add(aux[indi])
        var aux3 : Map<String,mlAlu>                                                    /*por divisiones*/
        val aux4 : mlDiv = mutableListOf()
        for(alum in cursos){
            aux3 = HashMap()
            for (al in alum) {
                if(aux3.contains(al.Division)) aux3[al.Division]?.add(al)
                else aux3[al.Division] = mutableListOf(al)
            }
            aux4.add(mutableListOf())
            for(division in aux3)
                aux4[aux4.lastIndex].add(division.component2())
        }
        for (curso in aux4){
            for (division in curso)
                division.sortBy { it.hasImg }
            curso.sortBy { it[0].hasImg }
        }
        aux4.sortBy { it[0][0].hasImg }
        return aux4
    }
}

typealias mlAlu = MutableList<Alumnos>
typealias mlCur = MutableList<mlAlu>
typealias mlDiv = MutableList<mlCur>