package com.team2052.frckrawler.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.crash.FirebaseCrash
import com.team2052.frckrawler.FRCKrawler
import com.team2052.frckrawler.R
import com.team2052.frckrawler.di.DaggerFragmentComponent
import com.team2052.frckrawler.di.FragmentComponent
import com.team2052.frckrawler.di.subscribers.SubscriberModule
import com.team2052.frckrawler.fragments.dialog.ExportDialogFragment
import com.team2052.frckrawler.interfaces.HasComponent
import com.team2052.frckrawler.services.BackupDBIntentService

class SettingsActivity : AppCompatActivity(), HasComponent {
    private var mComponent: FragmentComponent? = null

    var clickedPreference = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }

    /*override fun pickedEvent(event: Event) {
        ExportDialogFragment.newInstance(event, clickedPreference).show(supportFragmentManager, "exportDialogFragment")
    }*/

    override fun getComponent(): FragmentComponent {
        if (mComponent == null) {
            val app = application as FRCKrawler
            mComponent = DaggerFragmentComponent
                    .builder()
                    .fRCKrawlerModule(app.module)
                    .subscriberModule(SubscriberModule(this))
                    .applicationComponent(app.component)
                    .build()
        }
        return mComponent!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}

const val DATABSE_BACKUP_PREFRENCE_KEY = "database_backup"
const val EXPORT_PREFERENCE_KEY = "compile_export_preference"
const val EXPORT_RAW_PREFERENCE_KEY = "compile_raw_export"

const val TEAM_WEBSITE = "http://www.team2052.com/"
const val TEAM_WEBSITE_APP = "http://www.team2052.com/frckrawler/"
const val APP_GITHUB = "https://github.com/frc2052/FRC-Krawler"

internal class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity !is SettingsActivity) {
            FirebaseCrash.report(IllegalStateException("ServerFragment must have a parent activity of SettingsActivity"))
            activity.finish()
            return
        }

        addPreferencesFromResource(R.xml.preferences)
        //val numEvents = RxDBManager.getInstance(activity).eventsTable.getAllEvents().size
        //findPreference(EXPORT_PREFERENCE_KEY).isEnabled = numEvents >= 1
        //findPreference(EXPORT_RAW_PREFERENCE_KEY).isEnabled = numEvents >= 1

        val teamWebsite = findPreference("team_website")
        teamWebsite.intent = Intent(Intent.ACTION_VIEW, Uri.parse(TEAM_WEBSITE))
        val githubLink = findPreference("github_link")
        githubLink.intent = Intent(Intent.ACTION_VIEW, Uri.parse(APP_GITHUB))
        val learnMoreLink = findPreference("learn_more_link")
        learnMoreLink.intent = Intent(Intent.ACTION_VIEW, Uri.parse(TEAM_WEBSITE_APP))

        findPreference(EXPORT_PREFERENCE_KEY).onPreferenceClickListener = this
        findPreference(EXPORT_RAW_PREFERENCE_KEY).onPreferenceClickListener = this
        findPreference(DATABSE_BACKUP_PREFRENCE_KEY).onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val key = preference.key
        when (key) {
            EXPORT_PREFERENCE_KEY -> {
                ExportDialogFragment.newInstance(ExportDialogFragment.EXPORT_TYPE_NORMAL).show((activity as SettingsActivity).supportFragmentManager, "exportDialog")
            }
            EXPORT_RAW_PREFERENCE_KEY -> {
                ExportDialogFragment.newInstance(ExportDialogFragment.EXPORT_TYPE_RAW).show((activity as SettingsActivity).supportFragmentManager, "exportDialog")
            }
            DATABSE_BACKUP_PREFRENCE_KEY -> {
                val intent = Intent(activity, BackupDBIntentService::class.java)
                activity.startService(intent)
            }
        }
        return false
    }
}
