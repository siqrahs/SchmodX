package com.chmods.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.security.MessageDigest

object PluginSecurity {
    
    // GANTI INI dengan SHA-256 asli dari Sertifikat/Keystore Developer lu!
    private const val OFFICIAL_DEVELOPER_SIGNATURE_SHA256 = 
        "A1:B2:C3:D4:E5:F6:A7:B8:C9:D0:E1:F2:F3:F4:F5:F6:A7:B8:C9:D0:E1:F2:F3:F4:F5:F6:A7:B8:C9:D0:E1:F2"

    /**
     * Memeriksa apakah file APK Plugin aman dan ditandatangani oleh developer resmi
     */
    fun verifyPluginIntegrity(context: Context, apkPath: String): Boolean {
        val file = File(apkPath)
        if (!file.exists()) return false

        try {
            val packageManager = context.packageManager
            
            // Mengambil signature dari file APK mentah yang belum di-install
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo?.signatures
            }

            if (signatures.isNullOrEmpty()) return false

            // Hitung SHA-256 dari signature APK plugin
            val rawCert = signatures[0].toByteArray()
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(rawCert)
            
            val apkSha256 = hashBytes.joinToString(":") { String.format("%02X", it) }

            // Validasi: Harus cocok dengan tanda tangan developer utama
            return apkSha256.equals(OFFICIAL_DEVELOPER_SIGNATURE_SHA256, ignoreCase = true)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
