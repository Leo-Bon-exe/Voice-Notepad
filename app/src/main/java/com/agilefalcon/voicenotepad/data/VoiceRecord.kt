package com.agilefalcon.voicenotepad.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class VoiceRecord(
    val id: Int,
    val time: Int,
    val name: String,
    val path: String,
    val timestamp: Long
) {
    val durationFormatted: String
        get() {
            val minutes = time / 60
            val seconds = time % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

    val formattedDate: String
        get() {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
            return sdf.format(date)
        }

    val displayName: String
        get() {
            return try {
                val clean = name.removePrefix("recording_").removeSuffix(".3gp")
                val inputFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val date = inputFormat.parse(clean)
                val outputFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("tr"))
                outputFormat.format(date!!)
            } catch (e: Exception) {
                name
            }
        }
}