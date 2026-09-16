package com.chmods.interfaces

import androidx.compose.runtime.Composable

interface IPlugin {
    val pluginId: String       // Id unik, misal: "wa_web" atau "phone_contacts"
    val pluginLabel: String    // Nama yang muncul di Navbar bawah, misal: "WA Web"
    val pluginIconRes: Int     // Icon (opsional, bisa diganti pakai String/Data gambar)
    
    // Fungsi ini yang akan mengembalikan tampilan UI (Compose) dari dalam plugin
    @Composable
    fun Content()
}
