package com.agilefalcon.voicenotepad.helper

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.provider.Settings

class PermissionHelper(
    private val activity: ComponentActivity,
    private val permission: String
) {

    private val audioPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {

            if (isPermissionPermanentlyDenied()) {

                showPermissionDeniedDialog()
            } else {
                Toast.makeText(activity, "İzin gerekli", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isPermissionPermanentlyDenied(): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED &&
                !activity.shouldShowRequestPermissionRationale(permission)
    }

    fun checkAndRequestPermission(onPermissionGranted: () -> Unit) {

        when {

            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {

                onPermissionGranted()
            }

            activity.shouldShowRequestPermissionRationale(permission) -> {

                Toast.makeText(activity, "İzin gerekli, lütfen onaylayın", Toast.LENGTH_LONG).show()
                audioPermissionLauncher.launch(permission)
            }
            else -> {
                audioPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("İzin gerekiyor")
            .setMessage("Mikrofon izni kalıcı olarak reddedildi. Lütfen ayarlardan manuel olarak izin verin.")
            .setPositiveButton("Ayarlar") { _, _ -> openAppSettings() }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }
}
