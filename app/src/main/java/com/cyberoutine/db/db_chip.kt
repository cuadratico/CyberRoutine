package com.cyberoutine.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class chip_data (val name: String, val description: String, val price: String, var dispo: Int, var position: Int)
class db_chip(context: Context): SQLiteOpenHelper(context,  "db_chip.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE db_chip (id INTEGER PRIMARY KEY, name TEXT, description TEXT, price TEXT, dispo INTEGER)")

        val list_chips = listOf(
            chip_data("NeuroNoti-F0", "A biochip that sends you a reminder notification every time you start a day, telling you how many tasks you have to do.", "2000", 1, 0),
            chip_data("Neur0-Clock", "A biochip that lets you earn two more points for each task.", "6000", 1, 1),
            chip_data("NeuroMoney-Infinite", "A biochip that lets you change the amount of money you have.", "50000", 1, 2),
            chip_data("NeuroMoney-Simple", "A biochip that gives you the maximum amount of money you can earn for each task (1000 eurodollars).", "12000", 1, 3),
            chip_data("NeuroSecure-S001", "A chip that gives you the ability to create a secure space for which you'll need biometric data and where you can save all the tasks you want to hide.", "15000", 1, 4)
        )

        for (chip in list_chips){
            db?.execSQL("INSERT INTO db_chip (id, name, description, price, dispo) VALUES (?, ?, ?, ?, ?)", arrayOf(chip.position, chip.name, chip.description, chip.price, chip.dispo))
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}


    fun update(id: Int, dispo: Int){
        val db = this.writableDatabase

        chip_list[id].dispo = dispo
        db.execSQL("UPDATE db_chip SET dispo = ? WHERE id = ?", arrayOf(dispo, id))

        db.close()
    }


    fun extraccion(){
        chip_list.clear()
        val db = this.readableDatabase
        val query = db.rawQuery("SELECT * FROM db_chip", null)

        fun add (){
            chip_list.add(chip_data(query.getString(1), query.getString(2), query.getString(3), query.getInt(4), query.getInt(0)))
        }

        if (query.moveToFirst()){
            add()
            while (query.moveToNext()){
                add()
            }
            db.close()
        }
    }


    companion object {
        val chip_list = mutableListOf<chip_data>()
    }

}