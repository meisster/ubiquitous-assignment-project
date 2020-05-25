package com.example.legoland


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.legoland.SettingsManager.prefix
import com.example.legoland.SettingsManager.showArchived


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsButton, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefixPreference =
                findPreference<EditTextPreference>("prefix") as EditTextPreference
            prefixPreference.summary = prefix
            prefixPreference.text = prefix

            prefixPreference.setOnPreferenceChangeListener { _, newValue ->
                prefixPreference.text = newValue as String
                prefixPreference.summary = newValue
                savePrefix(newValue)
                true
            }

            val archivePreference =
                findPreference<CheckBoxPreference>("archived") as CheckBoxPreference

            archivePreference.setOnPreferenceChangeListener { _, newValue ->
                changeShowingArchived(newValue as Boolean)
                true
            }
        }

        private fun changeShowingArchived(newValue: Boolean) {
            showArchived = newValue
            SettingsManager.saveSettings(this.requireContext().filesDir)
        }

        private fun savePrefix(newValue: String) {
            prefix = newValue
            SettingsManager.saveSettings(this.requireContext().filesDir)
        }

    }
}




