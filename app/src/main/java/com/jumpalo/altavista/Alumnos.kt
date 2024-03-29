package com.jumpalo.altavista

class Alumnos(
    var Carnet:String="",
    var Nombre:String="",
    var Apellido:String="",
    var Curso:String="0",
    var Division:String="0",
    var Turno:String="0",
    var Dni:String="",
    var hasImg:Int=0
){

    override fun toString() = "$Carnet,$Nombre,$Apellido,$Curso,$Division,$Turno,$Dni,$hasImg"
    fun fromString(str : String){
        if(str!="0"){
            val data = str.split(',')
            Carnet = data[0]; Dni = data[6]
            Nombre = data[1]; Apellido = data[2]
            Curso = data[3]; Division = data[4]
            Turno = data[5]; hasImg = data[7].toInt()
        }
    }
}