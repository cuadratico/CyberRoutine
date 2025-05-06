package com.cyberoutine.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cyberoutine.habilidades
import com.cyberoutine.list_habilidad

data class points_spec(val image: Int, val name: String, var points: Int)
class db_user(context: Context): SQLiteOpenHelper(context, "db_user.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE db_user (id INTEGER PRIMARY KEY AUTOINCREMENT, hability TEXT, points INTEGER)")

        for (habili in list_habilidad){
            db?.execSQL("INSERT INTO db_user (hability, points) VALUES (?, ?)", arrayOf(habili, 0))
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun update(habil: String, point: Int){
        val db = this.writableDatabase

        db.execSQL("UPDATE db_user SET hability = ?, points = points + ? WHERE hability = ?", arrayOf(habil, point, habil))

        db.close()
    }


    fun extraccion (): Boolean{
        points_spec_list.clear()
        val db = this.readableDatabase
        val query = db.rawQuery("SELECT * FROM db_user", null)

        fun add (){
            points_spec_list.add(points_spec(habilidades[query.getString(1)]!!.imagen, query.getString(1), query.getInt(2)))
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



    companion object {
        val points_spec_list = mutableListOf<points_spec>()
    }

}