package com.cyberoutine.db

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.cyberoutine.recy_task.data_task
import com.cyberoutine.recy_task.task_list
import com.cyberoutine.space
import com.cyberoutine.update_recy
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher

class db_task(val context: Context): SQLiteOpenHelper(context, "db_task.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE db_task (id INTEGER PRIMARY KEY, task TEXT, habilidad TEXT, money INTEGER, secure TEXT, iv TEXT)")

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}



    fun add(pref: SharedPreferences, task: String, habilidad: String, money: Int, secure: Int, iv: String){
        val db = this.writableDatabase


        if (secure == 0){
            pref.edit().putInt("size_normal", pref.getInt("size_normal", 0) + 1).commit()
        }

        db.execSQL("INSERT INTO db_task (id, task, habilidad, money, secure, iv) VALUES (?, ?, ?, ?, ?, ?)", arrayOf(pref.getInt("size", 0), task, habilidad, money, secure.toString(), iv))

        db.close()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun update(id: Int, task:String){
        val db = this.writableDatabase

        if (space == 1){
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

            val c = Cipher.getInstance("AES/GCM/NoPadding")
            c.init(Cipher.ENCRYPT_MODE, ks.getKey("key", null))

            db.execSQL("UPDATE db_task SET task  = ?, iv = ? WHERE id = ?", arrayOf(Base64.getEncoder().withoutPadding().encodeToString(c.doFinal(task.toByteArray())),
                Base64.getEncoder().withoutPadding().encodeToString(c.iv), id))
        }else {

            db.execSQL("UPDATE db_task SET task = ? WHERE id = ?", arrayOf(task, id))
        }
        task_list[task_list.indexOfFirst {dato -> dato.position == id }].task = task
        update_recy = true

        db.close()

    }

    fun delete (id: Int, pref: SharedPreferences){
        val db = this.writableDatabase
        task_list.removeIf {dato -> dato.position == id  }

        db.execSQL("DELETE FROM db_task WHERE id = ?", arrayOf(id))

        if (pref.getInt("size", 0) != 0) {
            pref.edit().putInt("size", pref.getInt("size", 0) - 1).commit()
        }
        if (task_list.size != 0) {
            var size_t = task_list[0].position
            for (distancia in 0..task_list.size - 1) {
                db.execSQL("UPDATE db_task SET id = ? WHERE id = ?", arrayOf(size_t, task_list[distancia].position))
                task_list[distancia].position = size_t
                size_t++
            }
        }

        if (space == 0){
            pref.edit().putInt("size_normal", pref.getInt("size_normal", 0) - 1).commit()
        }

        db.close()
    }

    fun extraccion(secure: Int): Boolean{
        task_list.clear()
        val db = this.readableDatabase
        val query = db.rawQuery("SELECT * FROM db_task WHERE secure = ?", arrayOf(secure.toString()))

        fun add (){
            task_list.add(data_task(query.getInt(0), query.getString(1), query.getString(2), query.getInt(3), query.getString(5)))
        }

        if (query.moveToFirst()){
            add()

            while (query.moveToNext()){
                add()
            }
            db.close()
            return true
        }else {
            db.close()
            return false
        }

    }


}