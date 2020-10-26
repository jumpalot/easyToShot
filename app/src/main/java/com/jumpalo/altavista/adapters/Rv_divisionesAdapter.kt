package com.jumpalo.altavista.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jumpalo.altavista.Alumnos
import com.jumpalo.altavista.databinding.ItemCursoDescBinding

class rv_divisionesAdapter(private val nombres: MutableList<Alumnos>) : RecyclerView.Adapter<rv_divisionesAdapter.ViewHolder>() {

    class ViewHolder(var item : ItemCursoDescBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(al: Alumnos) {
            item.tvCurso.text = al.Nombre+" "+al.Apellido
            item.tvCurso.tag = al.toString()
            if (al.hasImg==1){
                item.check.visibility = View.VISIBLE
                item.tvCurso.setTextColor(Color.GRAY)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemCursoDescBinding.inflate(LayoutInflater.from(parent.context), parent,false)
    )
    override fun onBindViewHolder(h: ViewHolder, position: Int) = h.bind(nombres[position])
    override fun getItemCount() = nombres.size
}