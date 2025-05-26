package com.agilefalcon.voicenotepad.helper


import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class AudioShareHelper(private val context: Context) {

    fun shareAudio(filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/mp4"

            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

        val chooserIntent = Intent.createChooser(shareIntent, "Paylaş").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
    }
}
