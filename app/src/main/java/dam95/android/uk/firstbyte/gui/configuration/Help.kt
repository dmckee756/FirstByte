package dam95.android.uk.firstbyte.gui.configuration

import android.os.Bundle
import android.view.*
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dam95.android.uk.firstbyte.R


private const val REPORT = "REPORT"
private const val CONTACT = "CONTACT"
private const val LICENSES = "LICENSES"

/**
 * @author David Mckee
 * @Version 0.1
 * Help screen designed to utilise the persistence XML layout.
 * Allows the user to contact me at davidmckee756@outlook.com
 * Allows the user to report a problem to me at dam95@aber.ac.uk (Will change after University ofcourse)
 * Allows the user to browse the list of used libraries and licenses in this app.
 * Displays the app's current build version.
 */
class Help : PreferenceFragmentCompat() {

    /**
     * When the help fragment is created, assign listeners to the preferences and allow the resource preference layout to be displayed.
     */
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

    /**
     * Creates the help preference XML resource layout, whilst this is not used for the correct reason,
     * it allows a very easy implementation of a simple screen design that I needed.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.help_preferences, rootKey)
    }

    /**
     * Makes the app bar clear of menu items.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    /**
     * Preference listener for
     */
    private fun reportProblemListener() {

        findPreference<Preference>(REPORT)?.summaryProvider =
            Preference.SummaryProvider<Preference> { report ->
                report.setOnPreferenceClickListener {
                    true
                }
                ""
            }
    }

    /**
     * Preference listener for
     */
    private fun contactUseListener() {
        findPreference<Preference>(CONTACT)?.summaryProvider =
            Preference.SummaryProvider<Preference>{ contact ->
                contact.setOnPreferenceClickListener {
                    true
                }
                ""
            }
    }

    /**
     * Preference listener for
     */
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