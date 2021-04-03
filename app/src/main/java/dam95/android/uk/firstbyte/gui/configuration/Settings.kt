package dam95.android.uk.firstbyte.gui.configuration

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import kotlinx.coroutines.Dispatchers
import java.util.*

const val HOME_CASUAL_PCS_START: Int = 1
const val GAMING_PCS_START: Int = 5
const val WORKSTATION_PCS_START: Int = 9
private const val HOME_CASUAL = "HOME CASUAL"
private const val GAMING = "GAMING"
private const val WORKSTATION = "WORKSTATION"

//Assign the starting Recommended PC ID to this KEY, the last PC ID will always be n+3. n must never be 0 or less.
const val RECOMMENDED_BUILDS = "RECOMMENDED_BUILDS"
const val NIGHT_MODE = "NIGHT_MODE"
private const val RECOMMENDED_LIST = "RECOMMENDED_LIST"
private const val RESET_DATA = "RESET_DATA"

/**
 *
 */
class Settings : PreferenceFragmentCompat() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        nightModeListener()
        recommendedListListener()
        resetDataListener()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun nightModeListener() {
        findPreference<SwitchPreferenceCompat>(NIGHT_MODE)?.summaryProvider =
            Preference.SummaryProvider<SwitchPreferenceCompat> { night ->
                if (night.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                ""
            }
    }

    private fun recommendedListListener() {

        findPreference<ListPreference>(RECOMMENDED_LIST)?.summaryProvider =
            Preference.SummaryProvider<ListPreference> { chooseRecommendations ->
                chooseRecommendations.setOnPreferenceChangeListener { _, newValue ->
                    //Assign the new starting PC ID for each type of recommended build set.
                    when (newValue.toString().toUpperCase(Locale.ROOT)) {
                        HOME_CASUAL -> sharedPreferences.edit()
                            .putInt(RECOMMENDED_BUILDS, HOME_CASUAL_PCS_START).apply()
                        GAMING -> sharedPreferences.edit()
                            .putInt(RECOMMENDED_BUILDS, GAMING_PCS_START).apply()
                        WORKSTATION -> sharedPreferences.edit()
                            .putInt(RECOMMENDED_BUILDS, WORKSTATION_PCS_START).apply()
                    }
                    true
                }
                ""
            }
    }

    private fun resetDataListener() {
        findPreference<Preference>(RESET_DATA)?.summaryProvider =
            Preference.SummaryProvider<Preference> { reset ->
                reset.setOnPreferenceClickListener {
                    areYouSureAlert()
                    true
                }
                ""
            }
    }

    private fun areYouSureAlert() {

        val alertBuilder =
            AlertDialog.Builder(ContextThemeWrapper(activity, R.style.myAlertDialogTheme))

        //Setup a dialog box that allows the user to back out of their choice to resetting app data.
        alertBuilder.setTitle(activity?.resources?.getString(R.string.alertResetData))
            .setCancelable(false)
            .setPositiveButton(activity?.getString(R.string.yesButton)) { _, _ ->
                //Reset app data
                val resetDatabase =
                    FirstByteDBAccess.dbInstance(
                        requireActivity().applicationContext,
                        Dispatchers.Main
                    )
                resetDatabase?.resetDatabase()
            }
            .setNegativeButton(activity?.resources?.getString(R.string.noButton)) { dialog, _ ->
                // Don't reset app data
                dialog.dismiss()
            }
        //Show the alert box
        val alert = alertBuilder.create()
        alert.show()
    }
}