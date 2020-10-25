package com.jumpalo.altavista.activity

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumpalo.altavista.DBCon
import com.jumpalo.altavista.R
import com.jumpalo.altavista.adapters.rv_divisionesAdapter
import kotlinx.android.synthetic.main.activity_listar_divi.*
import kotlinx.android.synthetic.main.item_curso_desc.view.*

class ListarDivi : AppCompatActivity() {
    private lateinit var curso : String
    private lateinit var division : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_divi)
        curso = intent.extras?.getString("curso") ?: "0"
        division = intent.extras?.getString("divi") ?: "0"
        rv_alums.layoutManager = LinearLayoutManager(this)
    }
    override fun onResume() {
        super.onResume()
        val divi = DBCon().getDivi(curso, division)
        val total = divi.size
        val actual = divi.count { it.hasImg==1 }
        pb_alumnos.max = total
        pb_alumnos.progress = actual
        tv_porcen.text = "$actual / $total"
        divi.sortBy { it.hasImg }
        rv_alums.adapter = rv_divisionesAdapter(divi)
    }

    fun descripcion(v : View) = startActivity (
        Intent(this, Descripcion::class.java)
            .putExtra("carnet",v.tv_curso.tag.toString()),
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
    )
}