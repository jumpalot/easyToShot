package com.jumpalo.altavista

class Alumnos(
    var Carnet:String,
    var Nombre:String,
    var Apellido:String,
    var Curso:String,
    var Division:String,
    var Turno:String,
    var hasImg:Int
){
    override fun toString() = "$Carnet,$Nombre,$Apellido,$Curso,$Division,$Turno,$hasImg"
    constructor(str : String){
        val data = str.split(',')
        Carnet = data[0];
        Nombre = data[1]; Apellido = data[2]
        Curso = data[3]; Division = data[4]
        Turno = data[5]; hasImg = data[6].toInt()
    }
}