package dam95.android.uk.firstbyte.gui.configuration

import android.os.Bundle
import android.view.*
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dam95.android.uk.firstbyte.R


private const val REPORT = "REPORT"
private const val CONTACT = "CONTACT"
private const val LICENSES = "LICENSES"

class Help : PreferenceFragmentCompat() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        reportProblemListener()
        contactUseListener()
        displayLicensesListener()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.help_preferences, rootKey)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun reportProblemListener() {

        findPreference<Preference>(REPORT)?.summaryProvider =
            Preference.SummaryProvider<Preference> { report ->
                report.setOnPreferenceClickListener {
                    true
                }
                ""
            }
    }

    private fun contactUseListener() {
        findPreference<Preference>(CONTACT)?.summaryProvider =
            Preference.SummaryProvider<Preference>{ contact ->
                contact.setOnPreferenceClickListener {
                    true
                }
                ""
            }
    }

    private fun displayLicensesListener() {
        findPreference<Preference>(LICENSES)?.summaryProvider =
            Preference.SummaryProvider<Preference>{ licensesDisplay ->
                licensesDisplay.setOnPreferenceClickListener {
                    true
                }
                ""
            }
    }
}