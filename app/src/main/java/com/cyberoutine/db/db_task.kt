package com.cyberoutine.db

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cyberoutine.recy_task.data_task
import com.cyberoutine.recy_task.task_list

class db_task(context: Context): SQLiteOpenHelper(context, "db_task.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE db_task (id INTEGER PRIMARY KEY, task TEXT, habilidad TEXT, money INTEGER, secure TEXT, iv TEXT)")

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}



    fun add(pref: SharedPreferences, task: String, habilidad: String, money: Int, secure: Int, iv: String){
        val db = this.writableDatabase

        db.execSQL("INSERT INTO db_task (id, task, habilidad, money, secure, iv) VALUES (?, ?, ?, ?, ?, ?)", arrayOf(pref.getInt("size", 0), task, habilidad, money, secure.toString(), iv))

        pref.edit().putInt("size", pref.getInt("size", 0) + 1).commit()
        db.close()
    }

    fun delete (id: Int, pref: SharedPreferences){
        val db = this.writableDatabase

        db.execSQL("DELETE FROM db_task WHERE id = ?", arrayOf(id))
        task_list.removeIf {dato -> dato.position == id  }

        for (size_t in 0..task_list.size - 1){
            db.execSQL("UPDATE db_task SET id = ? WHERE id = ?", arrayOf(size_t, task_list[size_t].position))
            task_list[size_t].position = size_t
        }

        pref.edit().putInt("size", pref.getInt("size", 0) - 1).commit()

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