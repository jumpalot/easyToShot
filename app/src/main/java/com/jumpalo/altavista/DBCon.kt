package com.jumpalo.altavista

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.ByteArrayOutputStream

class DBCon {
    private val httpclient: HttpClient = DefaultHttpClient()
    //private val url = "https://itching-requirement.000webhostapp.com"
    private val url = "http://mattprofe.com.ar:81/alumno/3635/TESIS"
    fun getAlumnos() : MutableList<Alumnos>{
        val httppost = HttpPost("$url/getAlumnos.php")
        httpclient.getConnectionManager().schemeRegistry.register(
            Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
        )
        val tachito = BasicResponseHandler()
        var respuesta = ""
        val hilo = Thread {
            try {
                respuesta = httpclient.execute(httppost, tachito)
            } catch (e: java.lang.Exception) {
                Log.e("Error", "Exception: " + e.message)
            }
        }
        hilo.start()
        while (hilo.isAlive){
            Log.d("conexion", "Recibiendo datos")}
        val tipo = object : TypeToken<Array<Alumnos?>?>() {}.type
        return Gson().fromJson<Array<Alumnos>>(respuesta, tipo).toMutableList()
    }

    fun getDivi(curso: String, divi : String) : MutableList<Alumnos>{
        val httppost = HttpPost("$url/getDivision.php")
        httpclient.getConnectionManager().schemeRegistry.register(
            Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
        )
        val tachito = BasicResponseHandler()
        var respuesta = ""
        val valores = mutableListOf(
            BasicNameValuePair("curso", curso),
            BasicNameValuePair("division", divi)
        )
        val hilo = Thread {
            try {
                httppost.entity = UrlEncodedFormEntity(valores)
                respuesta = httpclient.execute(httppost, tachito)
            } catch (e: java.lang.Exception) {
                Log.e("Error", "Exception: " + e.message)
            }
        }
        hilo.start()
        while (hilo.isAlive){
            Log.d("conexion", "Recibiendo datos")}
        val tipo = object : TypeToken<Array<Alumnos?>?>() {}.type
        return Gson().fromJson<Array<Alumnos>>(respuesta, tipo).toMutableList()
    }

    fun getFoto(carnet: String) : Bitmap?{
        val httppost = HttpPost("$url/getFoto.php")
        httpclient.getConnectionManager().schemeRegistry.register(
            Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
        )
        val tachito = BasicResponseHandler()
        var respuesta = ""
        val valores = mutableListOf(
            BasicNameValuePair("carnet", carnet)
        )
        val hilo = Thread {
            try {
                httppost.entity = UrlEncodedFormEntity(valores)
                respuesta = httpclient.execute(httppost, tachito)
            } catch (e: java.lang.Exception) {
                Log.e("Error", "Exception: " + e.message)
            }
        }
        hilo.start()
        while (hilo.isAlive)
            Log.d("conexion", "Recibiendo datos")
        val decodedString: ByteArray =
            Base64.decode(respuesta, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun sendFoto(img:Bitmap, carnet: String) : Boolean{
        val httppost = HttpPost("$url/setFoto.php")
        httpclient.getConnectionManager().schemeRegistry.register(
            Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
        )
        val byt = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.JPEG, 100, byt)
        val imagen = Base64.encodeToString(byt.toByteArray(), Base64.DEFAULT)
        //Log.i("imagen", imagen)
        //Log.i("carnet", carnet)
        val tachito = BasicResponseHandler()
        val valores = mutableListOf(
            BasicNameValuePair("carnet", carnet),
            BasicNameValuePair("foto", imagen)
        )
        return try{
            httppost.entity = UrlEncodedFormEntity(valores)
            val res = httpclient.execute(httppost, tachito)
            res.isBlank()
        } catch(ex : Exception) {
            Log.w("foto", ex.message ?: "error")
            false
        }
    }

    fun updateAlumno(alumno : MutableList<BasicNameValuePair>){
        val httppost = HttpPost("$url/updateAlumnos.php")
        httpclient.getConnectionManager().schemeRegistry.register(
            Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
        )
        val tachito = BasicResponseHandler()
        val hilo = Thread {
            try{
                httppost.entity = UrlEncodedFormEntity(alumno)
                val res = httpclient.execute(httppost, tachito)
                if (res.isNotBlank()) Log.w("update alumno", res)
            } catch(ex : Exception) {
                Log.w("update alumno", ex.message ?: "error")
            }
        }
        hilo.start()

        while (hilo.isAlive){
            Log.d("conexion", "Enviando datos")}
    }
}