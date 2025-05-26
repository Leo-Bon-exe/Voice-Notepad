package com.agilefalcon.voicenotepad.data

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordDataBaseHelper(context: Context)
    : SQLiteOpenHelper(context, "records.db", null, 2) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                time INTEGER,
                name TEXT NOT NULL,
                path TEXT,
                timestamp INTEGER
            )
        """.trimIndent())
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS records")
        onCreate(db)
    }

    fun insertRecord(path: String,time: Int) {
        val db = writableDatabase
        val currentTime = System.currentTimeMillis()

        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(currentTime))
        val values = ContentValues().apply {
            put("name", formattedDate)
            put("time", time)
            put("path", path )
            put("timestamp", currentTime)
        }
        db.insert("records", null, values)
    }

    fun changeRecordName(id: Int, newName: String){
        val db = writableDatabase
        if (newName.isNotEmpty()){
            val values = ContentValues().apply {
                put("name", newName)
            }

            db.update(
                "records",
                values,
                "id = ?",
                arrayOf(id.toString())
            )
        }
    }

    fun deleteRecord(id: Int) {
        val db = writableDatabase
        db.delete("records", "id=?", arrayOf(id.toString()))
    }

    fun getAllRecords(): List<VoiceRecord> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM records", null)
        val records = mutableListOf<VoiceRecord>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val time = cursor.getInt(cursor.getColumnIndexOrThrow("time"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val path = cursor.getString(cursor.getColumnIndexOrThrow("path"))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
            records.add(VoiceRecord(id,time,  name, path, timestamp))
        }
        cursor.close()
        return records
    }
}