package com.example.legoland

import java.io.File
import java.util.*

object SettingsManager {
    internal var showArchived : Boolean = false
    internal var prefix = "fcds.cs.put.poznan.pl/MyWeb/BL/"

    private const val settings = "settings.properties"


    fun readSettings(path : File) {
        val settingsDirectory = File(path, "settings")
        if (!settingsDirectory.exists()) {
            saveSettings(path)
        }
        var file = File(settingsDirectory, settings)
        if (!file.exists()) {
            saveSettings(path)
            file = File(settingsDirectory, settings)
        }
        val prop = Properties()
        prop.load(file.inputStream())
        prefix =prop.getProperty("prefix")
        showArchived = prop.getProperty("archived")!!.toBoolean()
    }

    fun saveSettings(path : File) {
        val settingsDirectory = File(path, "settings")
        settingsDirectory.mkdirs()
        val file = File(settingsDirectory, settings)
        val prop = Properties()
        prop["prefix"] = prefix
        prop["archived"] = showArchived.toString()
        prop.store(file.outputStream(), "")
    }

}