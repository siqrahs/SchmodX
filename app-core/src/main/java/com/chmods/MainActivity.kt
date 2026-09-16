package com.chmods

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.chmods.loader.PluginManager
import com.chmods.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // State global untuk kustomisasi aplikasi (Bisa dikembangkan dengan SharedPreferences / DataStore)
            var isDarkMode by remember { mutableStateOf(false) }
            var fontSizeMultiplier by remember { mutableStateOf(1.0f) }

            // Terapkan skema warna dinamis berdasarkan preferensi gelap/terang user
            val colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()

            MaterialTheme(colorScheme = colorScheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppShell(
                        isDarkMode = isDarkMode,
                        onThemeChange = { isDarkMode = it },
                        fontSizeMultiplier = fontSizeMultiplier,
                        onFontSizeChange = { fontSizeMultiplier = it }
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppShell(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    fontSizeMultiplier: Float,
    onFontSizeChange: (Float) -> Unit
) {
    var selectedTabId by remember { mutableStateOf("dashboard_utama") }
    val navigationItems = PluginManager.loadedNavbarItems // Membaca plugin yang lolos verifikasi secara dinamis

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Tab Tetap 1: Dashboard Utama
                NavigationBarItem(
                    selected = selectedTabId == "dashboard_utama",
                    onClick = { selectedTabId = "dashboard_utama" },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Utama", fontSize = (11 * fontSizeMultiplier).sp) }
                )

                // Render Otomatis Tab Baru untuk Plugin yang terverifikasi resmi & di-enable
                navigationItems.forEach { meta ->
                    NavigationBarItem(
                        selected = selectedTabId == meta.id,
                        onClick = { selectedTabId = meta.id },
                        icon = { Icon(Icons.Default.Share, null) },
                        label = { Text(meta.label, fontSize = (11 * fontSizeMultiplier).sp) }
                    )
                }

                // Tab Tetap 2: Pengaturan Aplikasi
                NavigationBarItem(
                    selected = selectedTabId == "pengaturan_app",
                    onClick = { selectedTabId = "pengaturan_app" },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Setelan", fontSize = (11 * fontSizeMultiplier).sp) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTabId) {
                "dashboard_utama" -> {
                    DashboardScreen(fontSizeMultiplier = fontSizeMultiplier)
                }
                "pengaturan_app" -> {
                    SettingsScreen(
                        isDarkMode = isDarkMode,
                        onThemeChange = onThemeChange,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onFontSizeChange = onFontSizeChange
                    )
                }
                else -> {
                    // Jika tab yang dipilih adalah plugin eksternal, load kelasnya secara dinamis
                    val context = LocalContext.current
                    val activePlugin = PluginManager.getOrLoadPlugin(context, selectedTabId)
                    
                    if (activePlugin != null) {
                        activePlugin.Content() // Memanggil UI Jetpack Compose milik plugin
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text("Modul tidak dapat diakses atau tidak aman.", fontSize = (14 * fontSizeMultiplier).sp)
                        }
                    }
                }
            }
        }
    }
}
