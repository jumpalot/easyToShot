package com.jumpalo.altavista.activity

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumpalo.altavista.DBCon
import com.jumpalo.altavista.MainActivity
import com.jumpalo.altavista.R
import com.jumpalo.altavista.adapters.rv_divisionesAdapter
import com.jumpalo.altavista.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_listar_divi.*
import kotlinx.android.synthetic.main.item_curso_desc.view.*

class ListarDivi : AppCompatActivity() {
    private lateinit var curso : String
    private lateinit var division : String
    private lateinit var url : String
    private lateinit var db : DBCon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_divi)
        curso = intent.extras?.getString("curso") ?: "0"
        division = intent.extras?.getString("divi") ?: "0"
        url = intent.extras?.getString("url") ?: ""
        rv_alums.layoutManager = LinearLayoutManager(this)
        db = DBCon(url)
    }
    override fun onResume() {
        super.onResume()
        val divi = db.getDivi(curso, division)
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
            .putExtra("alumno",v.tv_curso.tag.toString())
            .putExtra("url", url),
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
    )
}