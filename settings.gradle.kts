pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
    }
}

rootProject.name = "ChmodsEcosystem"

// Aktifkan modul yang sudah ada foldernya saja
include(":app-core")

// Kita matikan dulu pendaftaran modul plugin dengan tanda komentar (//) 
// sampai nanti kita benar-benar membuat foldernya agar tidak error.
// include(":shared-interface")
// include(":plugin-whatsapp")
// include(":plugin-phone")
