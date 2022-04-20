package com.example.firebaseauth.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.firebaseauth.ui.utils.Constants.THEME
import com.example.firebaseauth.R
import com.example.firebaseauth.ui.utils.setupTheme
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    androidx.preference.Preference.SummaryProvider<androidx.preference.ListPreference> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        setupToolbar()

        androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        this.title = getString(R.string.option_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Automatic implement changes on preferences.
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        setupTheme(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)

            // Get preferences.
            bindSharedPrefSummary(findPreference(THEME)!!)
        }

        companion object {
            /**
             * A preference value change listener that updates the preference's summary
             * to reflect its new value.
             */
            private val sBindPreferenceSummaryToValueListener =
                androidx.preference.Preference.OnPreferenceChangeListener { preference, value ->

                    val stringValue = value.toString()

                    if (preference is androidx.preference.ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        val index = preference.findIndexOfValue(stringValue)

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                            when {
                                index >= 0 -> preference.entries[index]
                                else -> null
                            }
                        )
                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.summary = stringValue
                    }
                    true
                }

            private fun bindSharedPrefSummary(preference: androidx.preference.Preference) {
                // Set the listener to watch for value changes.
                preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

                // Trigger the listener immediately with the preference's
                // current value.
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    androidx.preference.PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
                )
            }
        }
    }

    override fun provideSummary(preference: androidx.preference.ListPreference?): CharSequence =
        if (preference?.key == THEME) preference.entry
        else "Unknown Preference"
}