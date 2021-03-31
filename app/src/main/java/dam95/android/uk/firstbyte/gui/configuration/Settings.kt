package dam95.android.uk.firstbyte.gui.configuration

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import kotlinx.coroutines.Dispatchers

const val RECOMMENDED_LIST = "RECOMMENDED_LIST"
const val NIGHT_MODE = "NIGHT_MODE"
private const val RESET_DATA = "RESET_DATA"

class Settings : PreferenceFragmentCompat() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

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

    private fun recommendedListListener(){
        findPreference<ListPreference>(RECOMMENDED_LIST)?.summaryProvider =
            Preference.SummaryProvider<ListPreference>{ chooseRecommendations ->

                ""
            }
    }

    private fun resetDataListener(){
        findPreference<Preference>(RESET_DATA)?.summaryProvider =
            Preference.SummaryProvider<Preference>{ reset ->
               reset.setOnPreferenceClickListener {
                   areYouSureAlert()
                   true
               }
                ""
            }
    }

    private fun areYouSureAlert(){

        val alertBuilder =
            AlertDialog.Builder(ContextThemeWrapper(activity, R.style.myAlertDialogTheme))

        //Setup a dialog box that allows the user to back out of their choice to resetting app data.
        alertBuilder.setTitle(activity?.resources?.getString(R.string.alertResetData))
            .setCancelable(false)
            .setPositiveButton(activity?.getString(R.string.yesButton)) { _, _ ->
                //Reset app data
                val resetDatabase =
                    FirstByteDBAccess.dbInstance(requireActivity().applicationContext, Dispatchers.Main)
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