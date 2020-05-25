package com.example.legoland.creatingProject

import android.content.Context
import android.os.AsyncTask
import com.example.legoland.SettingsManager.prefix
import java.io.FileNotFoundException
import java.net.URL

class ValidatingProjectManager(private val context: Context, private val number: String) :
    AsyncTask<String, Int, String>() {

    var validated = 0

    override fun doInBackground(vararg params: String?): String {
        return try {
            val url = URL("http://$prefix$number.xml")
            val connection = url.openConnection()
            connection.connect()

            url.openStream().available()
            validated = 1
            "Available"
        } catch (e: Exception) {
            validated = -1
            "Not available"
        }
    }
}