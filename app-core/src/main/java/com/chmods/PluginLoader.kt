package com.chmods.loader

import android.content.Context
import com.chmods.interfaces.IPlugin
import dalvik.system.DexClassLoader
import java.io.File

object PluginLoader {

    fun loadPluginFromApk(context: Context, apkPath: String, className: String): IPlugin? {
        val apkFile = File(apkPath)
        if (!apkFile.exists()) return null

        // Tempat aman untuk ekstrak file .dex secara internal (.odex)
        val optimizedDexDir = context.codeCacheDir

        // Load APK eksternal secara runtime
        val classLoader = DexClassLoader(
            apkFile.absolutePath,
            optimizedDexDir.absolutePath,
            null,
            context.classLoader
        )

        return try {
            // Memuat kelas target dari dalam APK plugin
            val loadedClass = classLoader.loadClass(className)
            // Mengubah instansiasi menjadi bentuk Interface yang kita sepakati
            loadedClass.getDeclaredConstructor().newInstance() as? IPlugin
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
