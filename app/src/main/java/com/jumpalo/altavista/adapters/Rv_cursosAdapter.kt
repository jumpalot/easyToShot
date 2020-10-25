package com.jumpalo.altavista.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.jumpalo.altavista.R
import com.jumpalo.altavista.mlAlu
import com.jumpalo.altavista.mlDiv
import kotlinx.android.synthetic.main.item_curso.view.*
import kotlinx.android.synthetic.main.item_divisiones.view.*

class rv_cursosAdapter(var context: Context, var body : mlDiv) : BaseExpandableListAdapter(){
    override fun getGroup(groupPosition: Int): String = body[groupPosition][0][0].Curso
    override fun getGroupCount(): Int = body.size
    override fun getChild(groupPosition: Int, childPosition: Int): mlAlu = body[groupPosition][childPosition]
    override fun getChildrenCount(groupPosition: Int): Int = body[groupPosition].size
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = getCombinedChildId(getGroupId(groupPosition),childPosition.toLong())
    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val hijo = body[groupPosition]
        val actualg = hijo.count { it[0].hasImg==1 }
        val totalg = hijo.size
        val v= convertView ?: LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false)
        with(v){
            tv_curso.text = getGroup(groupPosition)+"º"
            pb_curso.max = totalg
            pb_curso.progress = actualg
            pb_curso.secondaryProgress = totalg
            if (hijo[0][0].hasImg==1){
                tv_actual_cur.visibility = View.INVISIBLE
                tv_total_cur.visibility = View.INVISIBLE
                divisor_cur.visibility = View.INVISIBLE
                check_cur.visibility = View.VISIBLE
                tv_curso.setTextColor(Color.GRAY)
            }else{
                tv_actual_cur.text = actualg.toString()
                tv_total_cur.text = totalg.toString()
                tv_actual_cur.visibility = View.VISIBLE
                tv_total_cur.visibility = View.VISIBLE
                divisor_cur.visibility = View.VISIBLE
                check_cur.visibility = View.INVISIBLE
                tv_curso.setTextColor(Color.BLACK)
            }
        }
        return v
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val hijo = getChild(groupPosition,childPosition)
        val actualc=hijo.count { it.hasImg==1 }
        val totalc=hijo.size
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_divisiones, parent, false)
        with(v){
            tv_divi.text = hijo[0].Division+"ª"
            pb_divi.max = totalc
            pb_divi.progress = actualc
            pb_divi.secondaryProgress = totalc
            if (hijo[0].hasImg==1){
                tv_actual_div.visibility = View.INVISIBLE
                tv_total_div.visibility = View.INVISIBLE
                divisor_div.visibility = View.INVISIBLE
                check_div.visibility = View.VISIBLE
                tv_divi.setTextColor(Color.GRAY)
            }else{
                tv_actual_div.text = actualc.toString()
                tv_actual_div.visibility = View.VISIBLE
                tv_total_div.text = totalc.toString()
                tv_total_div.visibility = View.VISIBLE
                divisor_div.visibility = View.VISIBLE
                check_div.visibility = View.INVISIBLE
                tv_divi.setTextColor(Color.BLACK)
            }
            tag = getGroup(groupPosition)
        }
        return v
    }
}