package com.agilefalcon.voicenotepad.data


class RecordRepository(private val dbHelper: RecordDataBaseHelper) {

    fun getRecords(): List<VoiceRecord> = dbHelper.getAllRecords()

    fun changeRecordName(id: Int, newName: String) = dbHelper.changeRecordName(id, newName)

    fun addRecord(path: String,time: Int) = dbHelper.insertRecord(path, time)

    fun deleteRecord(id: Int) = dbHelper.deleteRecord(id)
}