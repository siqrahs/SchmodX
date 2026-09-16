package com.chmods.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chmods.loader.PluginManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    fontSizeMultiplier: Float,
    onFontSizeChange: (Float) -> Unit
) {
    val scrollState = rememberScrollState()
    // Trigger recomposition saat status sakelar plugin berubah
    var refreshTrigger by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Pengaturan Aplikasi",
            fontSize = (22 * fontSizeMultiplier).sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // ================= SECTION 1: TAMPILAN =================
        Text("Tampilan & Kustomisasi", fontSize = (14 * fontSizeMultiplier).sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // SAKELAR GELAP / TERANG
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mode Gelap (Dark Mode)", fontSize = (16 * fontSizeMultiplier).sp)
                    Switch(checked = isDarkMode, onCheckedChange = onThemeChange)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // UKURAN FONT SLIDER
                Text("Ukuran Font Aplikasi", fontSize = (16 * fontSizeMultiplier).sp)
                Slider(
                    value = fontSizeMultiplier,
                    onValueChange = onFontSizeChange,
                    valueRange = 0.8f..1.4f,
                    steps = 2
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kecil", fontSize = 12.sp)
                    Text("Normal", fontSize = 12.sp)
                    Text("Besar", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================= SECTION 2: MANAJEMEN PLUGIN =================
        Text("Daftar Kontrol Plugin", fontSize = (14 * fontSizeMultiplier).sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val plugins = PluginManager.availablePlugins.values
                if (plugins.isEmpty()) {
                    Text("Belum ada plugin terdeteksi.", fontSize = (14 * fontSizeMultiplier).sp, color = Color.Gray)
                } else {
                    // Loop semua plugin untuk dijadikan opsi sakelar hidup/mati
                    plugins.forEach { meta ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(meta.label, fontSize = (16 * fontSizeMultiplier).sp, fontWeight = FontWeight.Bold)
                                
                                // Tag Keamanan Anti-Fraud
                                if (meta.isVerified) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, "Verified", tint = Color(0xFF198754), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Terverifikasi Resmi (Aman)", fontSize = 11.sp, color = Color(0xFF198754))
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, "Unverified", tint = Color.Red, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ilegal/Modifikasi (DIBLOKIR)", fontSize = 11.sp, color = Color.Red)
                                    }
                                }
                            }
                            
                            // Hanya izinkan sakelar menyala jika lolos verifikasi kriptografi SHA-256
                            Switch(
                                checked = meta.isEnabled && meta.isVerified,
                                enabled = meta.isVerified,
                                onCheckedChange = { isChecked ->
                                    meta.isEnabled = isChecked
                                    PluginManager.refreshNavbar()
                                    refreshTrigger++ // Force refresh UI
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================= SECTION 3: TENTANG DEVELOPER & ANTI-FRAUD =================
        Text("Informasi Keamanan & Developer", fontSize = (14 * fontSizeMultiplier).sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)) // Background kuning peringatan
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🛡️ Perlindungan Anti-Scam & Pencurian Data", fontSize = (15 * fontSizeMultiplier).sp, fontWeight = FontWeight.Bold, color = Color(0xFF664D03))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Aplikasi ini menggunakan sistem enkripsi terisolasi. Developer resmi tidak pernah meminta kredensial login, sesi token WhatsApp, atau data kontak Anda.\n\n" +
                           "Jangan pernah memuat file plugin (.apk) yang didapatkan dari forum tidak resmi atau grup chat mencurigakan karena berpotensi disisipi malware keylogger pembobol data keuangan.",
                    fontSize = (12 * fontSizeMultiplier).sp,
                    color = Color(0xFF664D03)
                )
                
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF856404))
                
                Text("Developer Utama: @chmods_dev Team", fontSize = (14 * fontSizeMultiplier).sp, fontWeight = FontWeight.Bold, color = Color(0xFF212529))
                Text("Versi Aplikasi: v1.0.0-Beta (Production Build)", fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}
