package com.chmods.loader

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.chmods.interfaces.IPlugin
import com.chmods.security.PluginSecurity
import dalvik.system.DexClassLoader
import java.io.File

object PluginManager {
    val availablePlugins = mutableMapOf<String, PluginMetadata>()
    private val activePluginsCache = mutableMapOf<String, IPlugin>()
    val loadedNavbarItems = mutableStateListOf<PluginMetadata>()

    data class PluginMetadata(
        val id: String,
        val label: String,
        val apkPath: String,
        val mainClassName: String,
        var isEnabled: Boolean = true,     // Pengaturan aktif/nonaktif oleh user
        var isVerified: Boolean = false    // Status keamanan anti-fraud
    )

    fun registerPlugin(context: Context, id: String, label: String, apkPath: String, mainClassName: String) {
        val file = File(apkPath)
        if (!file.exists()) return

        // JALANKAN VALIDASI ANTI-PENIPUAN SEBELUM REGISTER
        val isValidDeveloper = PluginSecurity.verifyPluginIntegrity(context, apkPath)

        val meta = PluginMetadata(
            id = id, 
            label = label, 
            apkPath = apkPath, 
            mainClassName = mainClassName,
            isEnabled = true, 
            isVerified = isValidDeveloper
        )
        
        availablePlugins[id] = meta
        refreshNavbar()
    }

    // Perbarui susunan navbar bawah berdasarkan plugin yang di-enable & terverifikasi resmi
    fun refreshNavbar() {
        loadedNavbarItems.clear()
        availablePlugins.values.forEach { meta ->
            if (meta.isEnabled && meta.isVerified) {
                loadedNavbarItems.add(meta)
            }
        }
    }

    fun getOrLoadPlugin(context: Context, id: String): IPlugin? {
        val meta = availablePlugins[id] ?: return null
        
        // Proteksi ganda: Jangan load jika dimatikan user atau gagal verifikasi keamanan
        if (!meta.isEnabled || !meta.isVerified) return null
        if (activePluginsCache.containsKey(id)) return activePluginsCache[id]

        return try {
            val classLoader = DexClassLoader(meta.apkPath, context.codeCacheDir.absolutePath, null, context.classLoader)
            val loadedClass = classLoader.loadClass(meta.mainClassName)
            val pluginInstance = loadedClass.getDeclaredConstructor().newInstance() as IPlugin
            activePluginsCache[id] = pluginInstance
            pluginInstance
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
