package com.chmods.interfaces

import androidx.compose.runtime.Composable

interface IPlugin {
    val pluginId: String       
    val pluginLabel: String    
    val pluginIconRes: Int     
    
    @Composable
    fun Content()
}
