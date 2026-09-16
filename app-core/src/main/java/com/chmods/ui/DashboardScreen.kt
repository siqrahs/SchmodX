
package com.chmods

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chmods.loader.PluginManager
import moe.shizuku.api.Shizuku
import java.io.File

@Composable
fun DashboardScreen(fontSizeMultiplier: Float) {
    val context = LocalContext.current
    
    // State manajemen UI Shizuku
    var shizukuStatus by remember { mutableStateOf("Memeriksa layanan...") }
    var statusColor by remember { mutableStateOf(Color(0xFF0D6EFD)) }
    var isButtonVisible by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf("") }
    var buttonAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Fungsi memeriksa status Shizuku API (Dioptimalkan agar lebih clean)
    val checkShizuku = {
        try {
            // Memeriksa apakah aplikasi Shizuku terinstal di HP
            context.packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)

            if (Shizuku.pingBinder()) {
                shizukuStatus = "Aktif (Layanan Berjalan)"
                statusColor = Color(0xFF198754) // Hijau
                isButtonVisible = false
                
                // Minta izin Shizuku jika belum diberikan
                if (Shizuku.checkPermission(0) != PackageManager.PERMISSION_GRANTED) {
                    Shizuku.requestPermission(0)
                }
            } else {
                shizukuStatus = "Terinstal (Layanan Mati)"
                statusColor = Color(0xFFFD7E14) // Oranye
                buttonText = "Mulai Layanan Shizuku"
                isButtonVisible = true
                buttonAction = {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                    } else {
                        Toast.makeText(context, "Gagal membuka Shizuku", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            shizukuStatus = "Tidak Ditemukan"
            statusColor = Color(0xFFDC3545) // Merah
            buttonText = "Download Shizuku App"
            isButtonVisible = true
            buttonAction = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://rikka.app"))
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            shizukuStatus = "Error mengecek status"
            statusColor = Color.Gray
            isButtonVisible = false
        }
    }

    // Memicu pengecekan saat pertama kali halaman dibuka
    LaunchedEffect(Unit) {
        checkShizuku()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Plugin Manager & Peluncur",
            fontSize = (22 * fontSizeMultiplier).sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // CARD STATUS SHIZUKU
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Status Layanan Shizuku",
                    fontSize = (14 * fontSizeMultiplier).sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = shizukuStatus,
                    fontSize = (18 * fontSizeMultiplier).sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.padding(top = 8dp)
                )

                if (isButtonVisible) {
                    Button(
                        onClick = { buttonAction?.invoke() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = buttonText, fontSize = (14 * fontSizeMultiplier).sp)
                    }
                }
            }
        }

        // CARD TOMBOL SIMULASI DAFTAR PLUGIN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Core APK Loader",
                    fontSize = (16 * fontSizeMultiplier).sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Klik tombol di bawah untuk mensimulasikan pemindaian file APK plugin dari penyimpanan lokal.",
                    fontSize = (13 * fontSizeMultiplier).sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4dp)
                )
                
                Button(
                    onClick = {
                        val targetApk = File(context.getExternalFilesDir(null), "wa_web_plugin.apk")
                        PluginManager.registerPlugin(
                            context = context,
                            id = "wa_web",
                            label = "WA Web HD",
                            apkPath = targetApk.absolutePath,
                            mainClassName = "com.chmods.plugin.wa.WhatsAppWebPlugin"
                        )
                        Toast.makeText(context, "Sistem memindai 'wa_web_plugin.apk'...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(top = 12dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Pindai & Muat Modul", fontSize = (13 * fontSizeMultiplier).sp)
                }
            }
        }
    }
}
